# Mirrors the production database into a local HML MongoDB, handling Queryable Encryption:
# dumps g12 + the encryption db (key vault), restores with --drop, and copies the documents
# carrying the server-managed __safeContent__ field (rejected by raw mongorestore inserts).
# The local app must run with the SAME MONGODB_MASTER_KEY as production.
# See docs/QUERYABLE_ENCRYPTION.md ("Backup and restore").
#
# Usage: .\scripts\mirror-hml.ps1 -ProdUri "mongodb+srv://user:pass@cluster.xpto.mongodb.net"

param(
    [Parameter(Mandatory = $true)] [string] $ProdUri,
    [string] $LocalUri = "mongodb://localhost:27017",
    [string] $DumpDir = "dump"
)

$ErrorActionPreference = "Stop"

# Safety check: every destructive operation (--drop, replaceOne) targets $LocalUri.
# Refuse to run unless it clearly points to a local server, so the production URI can
# never end up on the receiving end by mistake.
if ($LocalUri -notmatch "localhost|127\.0\.0\.1") {
    throw "Safety check failed: -LocalUri '$LocalUri' does not look local (expected localhost/127.0.0.1). Refusing to run --drop against it."
}
if ($ProdUri -match "localhost|127\.0\.0\.1") {
    throw "Safety check failed: -ProdUri '$ProdUri' looks local. Did you swap the parameters?"
}

# mongodump rejects a database in the URI when --db is also given; strip any "/<db>" path
# (e.g. .../g12 or .../g12?retryWrites=true) so the same URI works for both dumps.
$ProdUri = $ProdUri -replace '^(mongodb(?:\+srv)?://[^/]+)/[^?]*', '$1/'

Write-Host "==> Dumping g12 and encryption (key vault) from production..."
mongodump --uri $ProdUri --db g12 --out $DumpDir
if ($LASTEXITCODE -ne 0) { throw "mongodump g12 failed" }
mongodump --uri $ProdUri --db encryption --out $DumpDir
if ($LASTEXITCODE -ne 0) { throw "mongodump encryption failed" }

Write-Host "==> Restoring into $LocalUri (with --drop, excluding bet_plain_backup)..."
# __safeContent__ docs fail here by design; they are copied separately below
mongorestore --uri $LocalUri --drop --nsExclude "g12.bet_plain_backup" $DumpDir

Write-Host "==> Copying __safeContent__ documents (stripped) straight from production..."
$copyScript = @"
const prod = new Mongo("$ProdUri").getDB("g12");
let copied = 0;
prod.bet.find({__safeContent__: {`$exists: true}}).forEach(d => {
    delete d.__safeContent__;
    db.getSiblingDB("g12").bet.replaceOne({_id: d._id}, d, {upsert: true});
    copied++;
});
print("__safeContent__ docs copied: " + copied);
const prodCount = prod.bet.countDocuments();
const localCount = db.getSiblingDB("g12").bet.countDocuments();
print("bet count -> prod: " + prodCount + " | local: " + localCount);
if (prodCount !== localCount) {
    print("WARNING: counts differ - investigate before using the mirror");
}
"@
$tmp = New-TemporaryFile
Set-Content -Path $tmp -Value $copyScript
mongosh $LocalUri --file $tmp
Remove-Item $tmp

Write-Host "==> Done. Run the app against $LocalUri with the PRODUCTION MONGODB_MASTER_KEY."

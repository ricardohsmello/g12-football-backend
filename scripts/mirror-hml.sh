#!/usr/bin/env bash
set -euo pipefail

PROD_URI="${1:?Usage: ./mirror-hml.sh <prod-uri> [local-uri] [dump-dir]}"
LOCAL_URI="${2:-mongodb://localhost:27017}"
DUMP_DIR="${3:-dump}"

if [[ "$LOCAL_URI" != mongodb* ]]; then
    echo "Safety check failed: 2nd argument '$LOCAL_URI' is not a MongoDB URI." >&2
    exit 1
fi
if [[ "$LOCAL_URI" != *localhost* && "$LOCAL_URI" != *127.0.0.1* ]]; then
    echo "Safety check failed: local URI '$LOCAL_URI' does not look local." >&2
    exit 1
fi
if [[ "$PROD_URI" == *localhost* || "$PROD_URI" == *127.0.0.1* ]]; then
    echo "Safety check failed: prod URI looks local. Did you swap the parameters?" >&2
    exit 1
fi

PROD_URI="$(printf '%s' "$PROD_URI" | sed -E 's|^(mongodb(\+srv)?://[^/]+)/[^?]*|\1/|')"

echo "==> Dumping g12 and encryption (key vault) from production..."
mongodump --uri "$PROD_URI" --db g12 --out "$DUMP_DIR"
mongodump --uri "$PROD_URI" --db encryption --out "$DUMP_DIR"

echo "==> Restoring into $LOCAL_URI (with --drop, excluding bet_plain_backup)..."
mongorestore --uri "$LOCAL_URI" --drop --nsExclude "g12.bet_plain_backup" "$DUMP_DIR" || true

echo "==> Copying __safeContent__ documents (stripped) straight from production..."
TMP_JS="$(mktemp)"
trap 'rm -f "$TMP_JS"' EXIT
cat > "$TMP_JS" <<EOF
const prod = new Mongo("$PROD_URI").getDB("g12");
let copied = 0;
prod.bet.find({__safeContent__: {\$exists: true}}).forEach(d => {
    delete d.__safeContent__;
    db.getSiblingDB("g12").bet.replaceOne({_id: d._id}, d, {upsert: true});
    copied++;
});
print("__safeContent__ docs copied: " + copied);
const prodCount = prod.bet.countDocuments();
const localCount = db.getSiblingDB("g12").bet.countDocuments();
print("bet count -> prod: " + prodCount + " | local: " + localCount);
if (prodCount !== localCount) { print("WARNING: counts differ"); }
EOF
mongosh "$LOCAL_URI" --file "$TMP_JS"

echo "==> Done. Run the app against $LOCAL_URI with the PRODUCTION MONGODB_MASTER_KEY."
/*
db.bet.createIndex(
  { matchId: 1, username: 1 },
  { name: "matchIdAndUsername" }
)

db.bet.createIndex(
  { matchId: 1 },
  { name: "matchId_1" }
)
db.bet.getIndexes()


db.match.createIndex(
  { status: 1, matchDate: 1 },
  { name: "statusAndDate" }
)

db.match.createIndex(
  { round: 1 },
  { name: "round_1" }
)

db.scoreboard.createIndex(
  { round: 1, username: 1 },
  { name: "roundAndUsername" }
)

db.scoreboard.createIndex(
  { round: 1 },
  { name: "round_1" }
)

db.match.createIndex({ round: 1, status: 1 }, { name: "roundAndStatus" })

*/

// MongoDB Playground
// Use Ctrl+Space inside a snippet or a string literal to trigger completions.

// The current database to use.
use('g12');

const round = 14;
const roundTotal = 0;

const result = db.bet.aggregate([
    {
        $match: {
            round: round
        }
    },
    {
        $group: {
            _id: "$username",
            total: {
                $sum: "$pointsEarned"
            }
        }
    },
  
    {
        $project: {
          _id: 1,
          total: 1
        }
    }
]).toArray();



for (let u = 0; u < result.length; u++) {
  const username = result[u]._id;
  const totalToSubtract = result[u].total;

  const scoreboard = db.scoreboard.findOne({ username: username, round: roundTotal });

  if (scoreboard) {
    const newPoints = scoreboard.points - totalToSubtract;

    db.scoreboard.updateOne(
      { _id: scoreboard._id },
      { $set: { points: newPoints } }
    );

  }
}

db.scoreboard.deleteMany({ round: round });

 
 
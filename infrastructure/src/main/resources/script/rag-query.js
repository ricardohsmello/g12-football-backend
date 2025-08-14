/*
* [

  {
    $lookup: {
      from: "match",
      localField: "matchId",
      foreignField: "_id",
      as: "m"
    }
  },
  { $unwind: "$m" },

  {
    $lookup: {
      from: "scoreboard", // use o nome real da coleção (ex: "score" se for o caso)
      let: { u: "$username", r: "$round" },
      pipeline: [
        { $match: { $expr: { $and: [
          { $eq: ["$username", "$$u"] },
          { $eq: ["$round", "$$r"] }
        ] } } },
        { $project: { _id: 0, roundPoints: "$points" } }
      ],
      as: "roundScore"
    }
  },

  {
    $lookup: {
      from: "scoreboard", // idem
      let: { u: "$username", r: "$round" },
      pipeline: [
        { $match: { $expr: { $and: [
          { $eq: ["$username", "$$u"] },
          { $lte: ["$round", "$$r"] }
        ] } } },
        { $group: { _id: null, totalPoints: { $sum: "$points" } } },
        { $project: { _id: 0, totalPoints: 1 } }
      ],
      as: "totalScore"
    }
  },

  {
    $addFields: {
      roundPoints: { $ifNull: [ { $first: "$roundScore.roundPoints" }, 0 ] },
      totalPoints: { $ifNull: [ { $first: "$totalScore.totalPoints" }, 0 ] }
    }
  },

  {
    $project: {
      _id: 0,
      username: 1,
      "prediction.homeTeam": 1,
      "prediction.awayTeam": 1,
      round: 1,
      date: 1,
      pointsEarned: 1,
      homeTeam: "$m.homeTeam",
      awayTeam: "$m.awayTeam",
      actualHome: "$m.score.homeTeam",
      actualAway: "$m.score.awayTeam",
      roundPoints: 1,
      totalPoints: 1
    }
  },

]*/
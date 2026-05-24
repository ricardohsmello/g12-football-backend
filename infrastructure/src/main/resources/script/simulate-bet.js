const usernames =  [
    "antonio", "braulio", "bruno", "cleber", "daniel", "edmilson", "fabio",
    "gabriel", "giovanni", "guilherme", "heraldo", "joaozorzella", "lucas", "luciano", "matheus",
    "murilo", "rafaelcarvalho", "ricardocoutinho", "ricardomello", "weslley", "zitras"
];

function generateGoals() {
    const weights = [0, 1, 1, 2, 2, 3, 3, 4, 5]; // mais chance para 0-3
    const index = Math.floor(Math.random() * weights.length);
    return weights[index];
}

const matches = db.match.find({round:14}).toArray();


for (let u = 0; u < usernames.length; u++) {
    const username = usernames[u];

    for (let i = 0; i < matches.length; i++) {
   
        const homeGoals = generateGoals();
        const awayGoals = generateGoals();

        db.bet.insertOne({
            matchId: matches[i]._id,
            username: username,
            prediction: {
                homeTeam: homeGoals,
                awayTeam: awayGoals
            },
            round: 14,
            date: new Date()
        });
    }
}
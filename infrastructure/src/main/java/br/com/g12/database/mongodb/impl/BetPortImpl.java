package br.com.g12.database.mongodb.impl;

import br.com.g12.database.mongodb.BetRepository;
import br.com.g12.entity.BetDocument;
import br.com.g12.model.Bet;
import br.com.g12.port.BetPort;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BetPortImpl implements BetPort {

    private final BetRepository betRepository;

    BetPortImpl(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    @Override
    public Bet save(Bet bet) {
        BetDocument existing = betRepository.findByMatchIdAndUsername(new ObjectId(bet.getMatchId()), bet.getUsername());

        if (existing != null) {
            existing.setPrediction(bet.getPrediction());
            existing.setPointsEarned(bet.getPointsEarned());

            return betRepository.save(existing).toModel();
        }

        return betRepository.save(BetDocument.fromModel(bet)).toModel();
    }

    @Override
    public Bet findById(String id) {
        Optional<BetDocument> byId = betRepository.findById(id);
        return byId.map(BetDocument::toModel).orElse(null);
    }

    @Override
    public List<Bet> findByMatchId(String id) {
        List<BetDocument> byMatchId = betRepository.findByMatchId(new ObjectId(id));
        return byMatchId.stream().map(BetDocument::toModel).toList();
    }

}

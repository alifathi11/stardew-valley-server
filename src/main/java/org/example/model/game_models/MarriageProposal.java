package org.example.model.game_models;

import org.example.model.generic.Pair;


public class MarriageProposal extends Pair<Player, Player> {

    private final String id;
    private ProposalState state;

    public MarriageProposal(String id,
                            Player p1,
                            Player p2) {
        super(p1, p2);
        this.id = id;
        this.state = ProposalState.PENDING;
    }

    public ProposalState getState() {
        return state;
    }

    public void setState(ProposalState state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }


    public enum ProposalState {
        PENDING,
        ACCEPTED,
        REJECTED,
        ;
    }
}

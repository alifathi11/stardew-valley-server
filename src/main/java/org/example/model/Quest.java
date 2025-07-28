package org.example.model;

public class Quest {
    private String title;
    private NPC npc;
    private Player player;

    public Quest(String title,
                 NPC npc,
                 Player player) {
        this.title = title;
        this.npc = npc;
        this.player = player;
    }

    public String getTitle() {
        return title;
    }

    public Player getPlayer() {
        return player;
    }

    public NPC getNpc() {
        return npc;
    }
}

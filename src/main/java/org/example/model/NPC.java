package org.example.model;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class NPC {
    private String name;
    private Profession profession;
    private Gender gender;
    private List<Quest> quests;
    private Vector2 position;

    public NPC(String name,
               Profession profession,
               Gender gender,
               List<Quest> quests,
               Vector2 position) {

        this.name = name;
        this.profession = profession;
        this.gender = gender;
        this.quests = quests;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void setQuests(List<Quest> quests) {
        this.quests = quests;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}

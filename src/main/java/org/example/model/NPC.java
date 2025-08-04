package org.example.model;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class NPC {
    private final String id;
    private String name;
    private Profession profession;
    private Gender gender;
    private Vector2 position;

    private List<Quest> quests;

    public NPC(String id,
               String name,
               Profession profession,
               Gender gender,
               Vector2 position) {

        this.id = id;
        this.name = name;
        this.profession = profession;
        this.gender = gender;
        this.position = position;

        this.quests = new ArrayList<>();
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

    public void setQuests(List<Quest> quests) {
        this.quests = quests;
    }

    public String getId() {
        return id;
    }
}

package org.example.model;

import java.util.List;

public class NPC {
    private String name;
    private Profession profession;
    private Gender gender;
    private List<Quest> quests;

    public NPC(String name,
               Profession profession,
               Gender gender,
               List<Quest> quests) {
        this.name = name;
        this.profession = profession;
        this.gender = gender;
        this.quests = quests;
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
}

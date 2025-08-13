package org.example.model.game_models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.example.controller.NPC.NPCAnimationManager;
import org.example.controller.NPC.NPCController;
import org.example.model.consts.Gender;
import org.example.model.consts.ItemIDs;
import org.example.model.consts.Profession;

import java.util.ArrayList;
import java.util.List;

public class NPC {
    private final String id;
    private String name;
    private String characteristics;
    private Profession profession;
    private Gender gender;
    private Vector2 position;

    // graphics
    private Vector2 velocity;
    private float speed = 50f;
    private Rectangle collisionRect;
    private float stateTime = 0f;
    private boolean canTalkNow = false;

    // logic controller and managers
    private NPCController controller;
    private NPCAnimationManager animationManager;

    private List<Quest> quests;
    private int daysToLastQuest;

    private List<ItemIDs> favoriteItems;

    private boolean shopNPC = false;

    public NPC(String id,
               String name,
               Profession profession,
               Gender gender) {
        this.id = id;
        this.name = name;
        this.profession = profession;
        this.gender = gender;
        this.shopNPC = true;
    }

    public NPC(String id,
               String name,
               String characteristics,
               Profession profession,
               Gender gender,
               Vector2 position,
               List<ItemIDs> favoriteItems,
               int daysToLastQuest) {

        this.id = id;
        this.name = name;
        this.characteristics = characteristics;
        this.profession = profession;
        this.gender = gender;
        this.position = position;

        this.quests = new ArrayList<>();

        this.daysToLastQuest = daysToLastQuest;
        this.animationManager = new NPCAnimationManager();
        this.controller = new NPCController(this, animationManager);

        this.favoriteItems = favoriteItems;

        this.shopNPC = false;
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

    public void setQuests(List<Quest> quests) {
        this.quests = quests;
    }

    public String getId() {
        return id;
    }


    public void incrementStateTime(float delta) {
        this.stateTime += delta;
    }

    public void setPosition(Vector2 newPos) {
        this.position = newPos;
        updateCollisionRect();
    }

    public void updateCollisionRect() {
        collisionRect.setPosition(
                (position.x - collisionRect.getWidth() / 2f),
                (position.y - collisionRect.getHeight() / 2f)
        );
    }

    public Quest getQuest(int number) {
        try {
            return this.quests.get(number - 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Rectangle getCollisionRect() {
        return collisionRect;
    }

    public boolean canTalkNow() {
        return canTalkNow;
    }

    public void setCanTalkNow(boolean canTalkNow) {
        this.canTalkNow = canTalkNow;
    }

    public float getSpeed() {
        return speed;
    }

    public NPCController getController() {
        return controller;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public boolean hasFavorite(ItemIDs itemID) {
        return favoriteItems.contains(itemID);
    }

    public int getDaysToLastQuest() {
        return daysToLastQuest;
    }

    public boolean isShopNPC() {
        return shopNPC;
    }

    public List<ItemIDs> getFavoriteItems() {
        return favoriteItems;
    }
}

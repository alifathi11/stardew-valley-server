package org.example.model;

public class PlayerAbilities {
    private int farmingAbility;
    private int miningAbility;
    private int fishingAbility;
    private int natureAbility;

    public PlayerAbilities(int farmingAbility,
                           int miningAbility,
                           int fishingAbility,
                           int natureAbility) {
        this.farmingAbility = farmingAbility;
        this.miningAbility = miningAbility;
        this.fishingAbility = fishingAbility;
        this.natureAbility = natureAbility;
    }

    public int getFarmingAbility() {
        return farmingAbility;
    }

    public void setFarmingAbility(int farmingAbility) {
        this.farmingAbility = farmingAbility;
    }

    public int getFishingAbility() {
        return fishingAbility;
    }

    public void setFishingAbility(int fishingAbility) {
        this.fishingAbility = fishingAbility;
    }

    public int getMiningAbility() {
        return miningAbility;
    }

    public void setMiningAbility(int miningAbility) {
        this.miningAbility = miningAbility;
    }

    public int getNatureAbility() {
        return natureAbility;
    }

    public void setNatureAbility(int natureAbility) {
        this.natureAbility = natureAbility;
    }
}

package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class PlayerAbilities {
    private final Map<Ability, Integer> abilities;

    public PlayerAbilities() {
        this.abilities = new HashMap<>();
        abilities.put(Ability.FARMING, 0);
        abilities.put(Ability.MINING, 0);
        abilities.put(Ability.FISHING, 0);
        abilities.put(Ability.NATURE, 0);
    }

    public PlayerAbilities(int farmingAbility,
                           int miningAbility,
                           int fishingAbility,
                           int natureAbility) {

        this.abilities = new HashMap<>();
        abilities.put(Ability.FARMING, farmingAbility);
        abilities.put(Ability.MINING, miningAbility);
        abilities.put(Ability.FISHING, fishingAbility);
        abilities.put(Ability.NATURE, natureAbility);
    }

    public int getAbilityValue(Ability ability) {

        return abilities.get(ability);
    }

    public int getAbilityLevel(Ability ability) {
        int x = Math.max(0, abilities.get(ability) - 50);
        return x / 100;
    }

    public enum Ability {
        FARMING,
        MINING,
        FISHING,
        NATURE,
        ;
    }
}

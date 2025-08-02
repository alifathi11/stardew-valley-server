package org.example.ban;

import java.util.ArrayList;
import java.util.Arrays;

public class GlobalBanList {
    public static ArrayList<String> banList = new ArrayList<>();

    public static void add(String username) {
        banList.add(username);
    }

    public static void remove(String username) {
        banList.remove(username);
    }

    public static boolean has(String username) {
        return banList.contains(username);
    }
}

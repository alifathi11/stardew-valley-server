//package org.example.network;
//
//import org.example.global.GameManager;
//import org.example.model.*;
//import org.example.utils.MessageParser;
//
//import javax.sql.DataSource;
//import java.io.*;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class LeaderboardBroadcaster implements Runnable {
//    private final DataSource dataSource;
//    private final long intervalMillis;
//
//    public LeaderboardBroadcaster(DataSource dataSource,
//                                  long intervalMillis) {
//        this.dataSource = dataSource;
//        this.intervalMillis = intervalMillis;
//    }
//
//    @Override
//    public void run() {
//        while (true) {
//            try {
//
//                for (Game game : GameManager.getGames().values()) {
//
//                    List<Player> gamePlayers = game.getPlayers();
//                    List<Map<String, Object>> leaderboard = fetchLeaderboard(game);
//
//                    for (Player player : gamePlayers) {
//                        broadcastLeaderboardToPlayer(player, leaderboard);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            try {
//                Thread.sleep(intervalMillis);
//            } catch (InterruptedException ie) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }
//    }
//
//    private void broadcastLeaderboardToPlayer(Player player, List<Map<String, Object>> leaderboard) {
//        Socket socket = SocketManager.getSocket(player.getUser().getUsername());
//        if (socket == null || socket.isClosed()) return;
//
//        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
//            Map<String, Object> payload = new HashMap<>();
//            payload.put("leaderboard", leaderboard);
//            Message msg = new Message(Type.LEADERBOARD, payload);
//            out.write(MessageParser.toJson(msg));
//            out.newLine();
//            out.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private List<Map<String, Object>> fetchLeaderboard(Game game) {
//        List<Map<String, Object>> leaderboard = new ArrayList<>();
//
//        List<Player> players = game.getPlayers();
//
//        for (Player player : players) {
//            Map<String, Object> row = new HashMap<>();
//            row.put("name", player.getName());
//            row.put("coin", player.getWallet().getCoin());
//            row.put("finished_quests", player.getQuests().size());
//
//            Map<String, Integer> abilities = new HashMap<>();
//            for (PlayerAbilities pa : player.getPlayerAbilities()) {
//                abilities.put("farming_ability", pa.getFarmingAbility());
//                abilities.put("mining_ability", pa.getMiningAbility());
//                abilities.put("fishing_ability", pa.getFishingAbility());
//                abilities.put("nature_ability", pa.getNatureAbility());
//            }
//
//            row.put("abilities", abilities);
//            leaderboard.add(row);
//        }
//
//        return leaderboard;
//
//    }
//}

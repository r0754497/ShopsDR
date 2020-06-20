package simon.shopsdr;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerActions {
    public static ArrayList<PlayerObj> players = new ArrayList<>();

    public static void addPlayer(PlayerObj player) {
        players.add(player);
    }

    public static void removePlayer(PlayerObj player) {
        players.remove(player);
    }

    public static PlayerObj getPlayer(String name) {
        for (PlayerObj player : players) {
            if (player.playerName.equals(name)) {
                return player;
            }
        }
        return null;
    }

    public static void closeInventory(Player player) {
        Bukkit.getScheduler().runTask(ShopsDR.getPlugin(), player::closeInventory);
    }
}

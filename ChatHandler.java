package simon.shopsdr;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatHandler {


    public static ArrayList<Player> playerList = new ArrayList<>();
    public static Map<Player, String> messages = new HashMap<>();

    public static void addPlayer(Player player) {
        playerList.add(player);
    }

    public static void removePlayer(Player player) {
        playerList.remove(player);
    }

    public static ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public static void addMessage(Player player, String message) {
        messages.put(player, message);
    }

    public static String getMessage(Player player) {
        String message = messages.get(player);
        messages.remove(player);
        return message;
    }
}

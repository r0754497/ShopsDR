package simon.shopsdr;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatHandler {

    public static Map<Player, Block> shopNameList = new HashMap<>();
    public static ArrayList<Player> renameShopList = new ArrayList<>();
    public static Map<Player, ItemStack> priceItemList = new HashMap<>();
    public static ArrayList<Player> confirmList = new ArrayList<>();

    public static void addShopNamer(Player player, Block block) {
        shopNameList.put(player, block);
    }

    public static void removeShopNamer(Player player) {
        shopNameList.remove(player);
    }

    public static Map<Player, Block> getShopNameList() {
        return shopNameList;
    }

    public static boolean checkShopName(String message) {
        if (message.length() > 2 && message.length() < 13) {
            return message.matches("[a-zA-Z0-9]*");
        }
        return false;
    }

    public static ArrayList<Player> getRenameShopList() {
        return renameShopList;
    }

    public static void addShopRenamer(Player player) {
        renameShopList.add(player);
    }

    public static void removeShopRenamer(Player player) {
        renameShopList.remove(player);
    }

    public static Map<Player, ItemStack> getPriceItemList() {
        return priceItemList;
    }

    public static void addPlayerPricing(Player player, ItemStack item) {
        priceItemList.put(player, item);
    }

    public static void removePlayerPricing(Player player) {
        priceItemList.remove(player);
    }

    public static ArrayList<Player> getConfirmList() {
        return confirmList;
    }
    public static void addConfirmingPlayer(Player player) {
        confirmList.add(player);
    }

    public static void removeConfirmingPlayer(Player player) {
        confirmList.remove(player);
    }

}

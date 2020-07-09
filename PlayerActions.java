package simon.shopsdr;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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

    public static void closeInvForAll(Shop shop, boolean shutdown) {
        Inventory inventory = shop.inventory;
        for (HumanEntity viewer : new ArrayList<>(inventory.getViewers())) {
            if (shutdown) {
                closeInventory((Player) viewer);
            }
            else if (!shop.isShopOwner(viewer.getName())) {
                closeInventory((Player) viewer);
            }
        }
    }

    public static void levelUp(Player player) {
        PlayerObj playerObj = getPlayer(player.getName());
        if (playerObj.getLevel() > 4) {
            player.sendMessage("You're already the maximum level");
            return;
        }
        int price = (playerObj.getLevel() + 1) * 10;
        if (playerObj.getCurrency() > price) {
            playerObj.setCurrency(playerObj.getCurrency() - price);
            playerObj.setLevel(playerObj.getLevel() + 1);
            Shop shop = ShopActions.shops.get(player.getName());
            shop.updateInventory();
            player.sendMessage(ChatColor.GREEN + "Your shop is now level " + playerObj.getLevel());
        } else {
            player.sendMessage(ChatColor.RED + "You need " + price + "money to do this.");
        }
    }
}

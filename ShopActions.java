package simon.shopsdr;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ShopActions {
    public static Map<String, Shop> shops = new HashMap<>();

    public static void createShop(Block block, Player player, String shopName) {
        Bukkit.getScheduler().runTask(ShopsDR.getPlugin(), () -> {
            Block block1 = block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0));
            Block block2 = block.getWorld().getBlockAt(block.getLocation().add(1, 1, 0));
            if (block1.getType() == Material.AIR && block2.getType() == Material.AIR) {
                block1.setType(Material.CHEST);
                block2.setType(Material.CHEST);
                Chest chestBlockState1 = (Chest) block1.getBlockData();
                chestBlockState1.setType(Chest.Type.LEFT);
                block1.setBlockData(chestBlockState1, true);
                Chest chestBlockState2 = (Chest) block1.getBlockData();
                chestBlockState2.setType(Chest.Type.RIGHT);
                block2.setBlockData(chestBlockState2, true);
            } else {
                player.sendMessage("Placing shop cancelled.");
                return;
            }
            Shop shop = new Shop(player, block1, shopName);
            shops.put(player.getName(), shop);
            player.sendMessage("Shop placed!");
        });
    }

    public static boolean hasShop(Player player) {
        return shops.containsKey(player.getName());
    }

    public static Shop getShop(Block block) {
        for (Shop shop : shops.values()) {
            if (shop.block1.getX() == block.getX() && shop.block1.getY() == block.getY()
                    && shop.block1.getZ() == block.getZ()
                    || shop.block2.getX() == block.getX() && shop.block2.getY() == block.getY()
                    && shop.block2.getZ() == block.getZ()) {
                return shop;
            }
        }
        return null;
    }

    public static Shop getShop(Inventory inventory) {
        for (Shop shop : shops.values()) {
            if (inventory != null && inventory.equals(shop.inventory)) {
                return shop;
            }
        }
        return null;
    }

    public static void buttonPress(ItemStack item, Player player, Shop shop) {
        Material itemType = item.getType();
        if (itemType.equals(Material.BARRIER)) {
            PlayerActions.closeInventory(player);
            shop.removeShop();
        } else if (itemType.equals(Material.GRAY_DYE) || itemType.equals(Material.LIME_DYE)) {
            shop.changeShopState();
        } else if (itemType.equals(Material.NAME_TAG) && !shop.isOpen) {
            ChatHandler.addShopRenamer(player);
            player.sendMessage("Enter a new shop name!");
            PlayerActions.closeInventory(player);
        }
    }
}

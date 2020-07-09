package simon.shopsdr;


import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Shop {
    public String shopOwnerName;
    public String shopName;
    public Block block1;
    public Block block2;
    public Inventory inventory;
    public boolean isOpen;
    public Hologram hologram;
    public ArrayList<Player> viewers = new ArrayList<>();


    public Shop(Player player, Block location, String shopName) {
        shopOwnerName = player.getName();
        this.shopName = shopName;
        block1 = location;
        block2 = location.getWorld().getBlockAt(location.getLocation().add(1, 0, 0));
        inventory = createShopInv(shopOwnerName);
        isOpen = false;
        hologram = HologramsAPI.createHologram(ShopsDR.getPlugin(), block1.getLocation().add(1, 1.5, .5));
        hologram.insertTextLine(0, ChatColor.RED + shopName);
        hologram.insertTextLine(1, ChatColor.RED + getViewerAmount());
        hologram.getVisibilityManager().setVisibleByDefault(true);
    }

    private String getViewerAmount() {
        return Integer.toString(viewers.size());
    }

    public void addViewer(Player player) {
        if (!viewers.contains(player)) {
            viewers.add(player);
            updateHologram();
        }
    }

    public void updateHologram() {
        hologram.clearLines();
        if (isOpen) {
            hologram.insertTextLine(0, ChatColor.GREEN + shopName);
            hologram.insertTextLine(1, ChatColor.GREEN + getViewerAmount());
        } else {
            hologram.insertTextLine(0, ChatColor.RED + shopName);
            hologram.insertTextLine(1, ChatColor.RED + getViewerAmount());
        }
    }

    public String setName(String newShopName) {
        if (ChatHandler.checkShopName(newShopName)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(ShopsDR.getPlugin(), () -> {
                shopName = newShopName;
                ItemStack[] items = inventory.getContents();
                inventory = createShopInv(shopOwnerName);
                inventory.setContents(items);
                hologram.clearLines();
                hologram.insertTextLine(0, ChatColor.RED + shopName);
                hologram.insertTextLine(1, ChatColor.RED + getViewerAmount());
            });
            return "Shop name changed";
        }
        return "No shop name found";
    }


    public int getSize(String playerName) {
        int level = PlayerActions.getPlayer(playerName).getLevel();
        return level * 9 + 9;
    }

    private Inventory createShopInv(String playerName) {
        int size = getSize(playerName);
        Inventory inv = Bukkit.createInventory(null, size, shopName + " @" + shopOwnerName);
        fillBottom(inv);
        return inv;
    }

    public void removeShop() {
        block1.setType(Material.AIR);
        block2.setType(Material.AIR);
        ShopActions.shops.remove(shopOwnerName);
        hologram.delete();
        PlayerActions.closeInvForAll(this, true);
    }

    private void fillBottom(Inventory inv) {
        int size = getSize(shopOwnerName);
        inv.setItem(size - 1, getOpenButton());
        inv.setItem(size - 2, getDeleteButton());
        inv.setItem(size - 8, getRenameButton());
        inv.setItem(size - 9, getPlayerHead());
        for (int i = size - 3; i > size - 8; i--) {
            inv.setItem(i, getPane());
        }
    }

    private ItemStack getOpenButton() {
        ItemStack item = new ItemStack(Material.GRAY_DYE);
        return ItemHandler.nameItem(item, ChatColor.GREEN + "Open Shop", "This will open your shop");
    }

    private ItemStack getCloseButton() {
        ItemStack item = new ItemStack(Material.LIME_DYE);
        return ItemHandler.nameItem(item, ChatColor.RED + "Close Shop", "This will close your shop");
    }

    private ItemStack getDeleteButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        return ItemHandler.nameItem(item, ChatColor.RED + "Delete Shop", "This will delete your shop");
    }

    private ItemStack getPlayerHead() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) item.getItemMeta();
        //noinspection deprecation
        sm.setOwner(shopOwnerName);
        item.setItemMeta(sm);
        return ItemHandler.nameItem(item, shopOwnerName);
    }

    private ItemStack getRenameButton() {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        return ItemHandler.nameItem(item, ChatColor.GREEN + "Rename Shop", "Click to rename your shop");
    }

    private ItemStack getPane() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        return ItemHandler.nameItem(item, " ");
    }

    public void changeShopState() {
        isOpen = !isOpen;
        if (isOpen) {
            inventory.setItem(inventory.getSize() - 1, getCloseButton());
            hologram.clearLines();
            hologram.insertTextLine(0, ChatColor.GREEN + shopName);
            hologram.insertTextLine(1, ChatColor.GREEN + getViewerAmount());
        } else {
            PlayerActions.closeInvForAll(this, false);
            inventory.setItem(inventory.getSize() - 1, getOpenButton());
            hologram.clearLines();
            hologram.insertTextLine(0, ChatColor.RED + shopName);
            hologram.insertTextLine(1, ChatColor.RED + getViewerAmount());
        }

    }

    public boolean isShopOwner(String nameToCheck) {
        return nameToCheck.equals(shopOwnerName);
    }

    public void addShopItem(ItemStack item, String price, Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ShopsDR.getPlugin(), () -> {
            try {
                int priceInt = Integer.parseInt(price);
                if (priceInt > 0) {
                    ItemStack itemPriced = ItemHandler.addprice(item, price);
                    inventory.addItem(itemPriced);
                }
                else {
                    player.getInventory().addItem(item);
                    player.sendMessage("Add a valid price");
                }

            } catch (NumberFormatException nfe) {
                player.getInventory().addItem(item);
                player.sendMessage("Add a valid price");
            }
            player.openInventory(inventory);
        });
    }

    public void removeItem(int slot, Player player, int amount) {
        ItemStack item = inventory.getItem(slot);
        int totalAmount = item.getAmount();
        if (!isShopOwner(player.getName())) {
            PlayerObj buyer = PlayerActions.getPlayer(player.getName());
            PlayerObj seller = PlayerActions.getPlayer(shopOwnerName);
            int price = ItemHandler.getPrice(item) * amount;
            if (buyer.getCurrency() > price) {
                buyer.setCurrency(buyer.getCurrency() - price);
                seller.setCurrency(seller.getCurrency() + price);
                if (totalAmount > 1) {
                    ItemStack soldItem = item.clone();
                    soldItem.setAmount(amount);
                    item.setAmount(totalAmount - amount);
                    player.getInventory().addItem(ItemHandler.removePrice(soldItem));
                    inventory.setItem(slot, item);
                    return;
                }
            } else {
                player.sendMessage("You don't have enough cash.");
                return;
            }

        }
        inventory.clear(slot);
        player.getInventory().addItem(ItemHandler.removePrice(item));
    }

    public void updateInventory() {
        ItemStack[] items = Arrays.copyOfRange(inventory.getContents(), 0, inventory.getSize() - 9);
        inventory = createShopInv(shopOwnerName);
        inventory.setContents(items);
        fillBottom(inventory);
    }
}

package simon.shopsdr;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class ShopsDRListener implements Listener {
    Plugin plugin = ShopsDR.getPlugin();

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)) {
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (player.isSneaking()) {
                if (event.getClickedBlock().getType().equals(Material.CHEST)) {
                    Shop shop = ShopActions.getShop(event.getClickedBlock());
                    if (shop != null && !shop.isOpen && shop.isShopOwner(player.getName())) {
                        player.sendMessage("Do you want to level up your shop? (enter Y/N)");
                        ChatHandler.addConfirmingPlayer(player);
                        event.setCancelled(true);
                    }
                }
            } else {
                if (player.getInventory().getItemInMainHand().getType() == Material.BOOK
                        && !event.getClickedBlock().getType().equals(Material.CHEST)) {
                    if (ShopActions.hasShop(player)) {
                        player.sendMessage("You already have a shop. Close that one first!");
                    } else {
                        player.sendMessage("Enter a shop name: (Between 3 and 12 characters)");
                        ChatHandler.addShopNamer(player, event.getClickedBlock());
                    }
                }
                if (event.getClickedBlock().getType().equals(Material.CHEST)) {
                    Shop shop = ShopActions.getShop(event.getClickedBlock());
                    if (shop != null && shop.isOpen) {
                        event.setCancelled(true);
                        player.openInventory(shop.inventory);
                        shop.addViewer(player);
                    } else if (shop != null && shop.isShopOwner(player.getName())) {
                        event.setCancelled(true);
                        player.openInventory(shop.inventory);
                        shop.addViewer(player);
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)) {
            return;
        }
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (event.getClickedBlock().getType().equals(Material.CHEST)) {
                Shop shop = ShopActions.getShop(event.getClickedBlock());
                if (shop != null) {
                    if (shop.isShopOwner(event.getPlayer().getName())) {
                        shop.removeShop();
                    }
                    event.setCancelled(true);
                }

            }
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventoryView = player.getOpenInventory();
        Inventory clickedInventory = event.getClickedInventory();
        if (ShopActions.getShop(inventoryView.getTopInventory()) != null) {
            Shop shop = ShopActions.getShop(inventoryView.getTopInventory());
            if (Objects.equals(clickedInventory, inventoryView.getBottomInventory())) {
                if (event.isShiftClick()) {
                    event.setCancelled(true);
                }
            }
            if (Objects.equals(clickedInventory, shop.inventory)) {
                event.setCancelled(true);
                if (shop.isShopOwner(player.getName())) {
                    if (event.getCurrentItem() != null) {
                        ItemStack item = event.getCurrentItem();
                        if (event.getSlot() >= shop.inventory.getSize() - 9) {
                            ShopActions.buttonPress(item, player, shop);
                        } else if (event.getSlot() < shop.inventory.getSize() - 9 && !shop.isOpen) {
                            shop.removeItem(event.getSlot(), player, item.getAmount());
                        }
                    } else if (event.getCursor() != null && event.getCursor().getType() != Material.AIR && !shop.isOpen) {
                        ItemStack sellItem = event.getCursor();
                        if (sellItem != null) {
                            ChatHandler.addPlayerPricing(player, sellItem);
                            event.setCursor(null);
                            PlayerActions.closeInventory(player);
                            player.sendMessage("Enter item price:");
                        }
                    }
                } else {
                    if (event.getCurrentItem() != null) {
                        if (event.getSlot() < shop.inventory.getSize() - 9) {
                            ItemStack item = event.getCurrentItem();
                            if (item != null) {
                                if (event.getClick().equals(ClickType.LEFT)) {
                                    shop.removeItem(event.getSlot(), player, 1);
                                }
                                if (event.getClick().equals(ClickType.SHIFT_LEFT)) {
                                    shop.removeItem(event.getSlot(), player, item.getAmount());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (ChatHandler.getShopNameList().containsKey(player)) {
            if (ChatHandler.checkShopName(event.getMessage())) {
                Block block = ChatHandler.getShopNameList().get(player);
                ChatHandler.removeShopNamer(player);
                ShopActions.createShop(block, player, event.getMessage());
            } else {
                player.sendMessage("Shop name should be between 3 and 12 letters or numbers");
            }
            event.setCancelled(true);
        }
        if (ChatHandler.getRenameShopList().contains(player)) {
            ChatHandler.removeShopRenamer(player);
            if (ShopActions.hasShop(player)) {
                Shop shop = ShopActions.shops.get(player.getName());
                player.sendMessage(shop.setName(event.getMessage()));
            } else {
                player.sendMessage("You don't have a shop...");
            }
            event.setCancelled(true);
        }
        if (ChatHandler.getPriceItemList().containsKey(player)) {
            if (ShopActions.hasShop(player)) {
                ItemStack item = ChatHandler.getPriceItemList().get(player);
                ChatHandler.removePlayerPricing(player);
                Shop shop = ShopActions.shops.get(player.getName());
                shop.addShopItem(item, event.getMessage(), player);
            } else {
                player.sendMessage("You don't have a shop...");
            }
            event.setCancelled(true);
        }
        if (ChatHandler.getConfirmList().contains(player)) {
            ChatHandler.removeConfirmingPlayer(player);
            event.setCancelled(true);
            if (event.getMessage().toLowerCase().equalsIgnoreCase("y")
                    || event.getMessage().toLowerCase().equalsIgnoreCase("yes")) {
                PlayerActions.levelUp(player);
                event.setCancelled(true);
            } else {
                player.sendMessage(ChatColor.YELLOW + "Cancelled");
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        PlayerObj player = new PlayerObj(1, 1000, event.getPlayer());
        PlayerActions.addPlayer(player);
    }

    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event) {
        PlayerObj player = PlayerActions.getPlayer(event.getPlayer().getName());
        if (player != null) {
            PlayerActions.removePlayer(player);
        }
    }

}

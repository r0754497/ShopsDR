package simon.shopsdr;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ShopsDRListener implements Listener {
    Plugin plugin = ShopsDR.getPlugin();

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (player.getInventory().getItemInMainHand().getType() == Material.BOOK
                    && !event.getClickedBlock().getType().equals(Material.CHEST)) {
                ShopActions.createShop(event.getClickedBlock(), player);
            }
            if (event.getClickedBlock().getType().equals(Material.CHEST)) {
                Shop shop = ShopActions.getShop(event.getClickedBlock());
                if (shop != null && shop.isOpen) {
                    event.setCancelled(true);
                    player.openInventory(shop.inventory);
                    shop.addViewer(player);
                } else if (shop != null && shop.shopOwnerName.equals(player.getName())) {
                    event.setCancelled(true);
                    player.openInventory(shop.inventory);
                    shop.addViewer(player);
                } else {
                    event.setCancelled(true);
                }
            }
        }


    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
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
    public void onClickInventory(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        Shop shop = ShopActions.getShop(inventory);
        Player player = (Player) event.getWhoClicked();
        if (shop != null) {
            event.setCancelled(true);
            if (shop.isShopOwner(player.getName())) {
                if (event.getCurrentItem() != null) {
                    ItemStack item = event.getCurrentItem();
                    if (event.getSlot() >= shop.inventory.getSize() - 9) {
                        ShopActions.buttonPress(item, player, shop);
                    } else if (event.getSlot() < shop.inventory.getSize() - 9 && !shop.isOpen) {
                        shop.removeItem(event.getSlot(), player, false);
                    }
                } else if (event.getCursor() != null && !shop.isOpen) {
                    ItemStack sellItem = event.getCursor();
                    event.setCursor(null);
                    PlayerActions.closeInventory(player);
                    ChatHandler.addPlayer(player);
                    player.sendMessage("Enter item price:");
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        shop.addShopItem(sellItem, event.getSlot(), ChatHandler.getMessage(player), player);
                        player.openInventory(shop.inventory);
                    }, 100L);
                } //TODO: Move this crap to a proper method.
            } else {
                if (event.getSlot() < shop.inventory.getSize() - 9) {
                    shop.removeItem(event.getSlot(), player, true);
                }
            }
        }
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (ChatHandler.getPlayerList().contains(player)) {
            String message = event.getMessage();
            ChatHandler.removePlayer(player);
            ChatHandler.addMessage(player, message);
            event.setCancelled(true);
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

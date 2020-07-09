package simon.shopsdr;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopsDR extends JavaPlugin {
    public static ShopsDR plugin;

    public static ShopsDR getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        boolean useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        if (useHolographicDisplays) {
            getLogger().info("Holographic Displays enabled");
        }
        getLogger().info("Plugin loaded");
        getServer().getPluginManager().registerEvents(new ShopsDRListener(), this);
    }

    @Override
    public void onDisable() {
        for (Shop shop : ShopActions.shops.values()) {
            shop.removeShop();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerObj player = PlayerActions.getPlayer(sender.getName());
        if (label.equalsIgnoreCase("levelup")) {
            player.setLevel(player.getLevel() + 1);
            return true;
        }
        if (label.equalsIgnoreCase("leveldown")) {
            player.setLevel(player.getLevel() - 1);
            return true;
        }
        if (label.equalsIgnoreCase("checklevel")) {
            int level = player.getLevel();
            sender.sendMessage("Level: " + level);
        }
        if (label.equalsIgnoreCase("addcurrency")) {
            player.setCurrency(player.getCurrency() + 500);
            return true;
        }
        if (label.equalsIgnoreCase("removecurrency")) {
            player.setCurrency(player.getCurrency() - 500);
        }
        if (label.equalsIgnoreCase("checkcurrency")) {
            int currency = player.getCurrency();
            sender.sendMessage("Cash: " + currency);
        }
        return false;
    }
}

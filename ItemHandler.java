package simon.shopsdr;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class ItemHandler {

    public static ItemStack nameItem(ItemStack item, String itemName, String lore) {
        ItemMeta itemmeta = item.getItemMeta();
        itemmeta.setDisplayName(itemName);
        itemmeta.setLore(Collections.singletonList(ChatColor.GRAY + lore));
        item.setItemMeta(itemmeta);
        return item;
    }

    public static ItemStack nameItem(ItemStack item, String itemName) {
        ItemMeta itemmeta = item.getItemMeta();
        itemmeta.setDisplayName(itemName);
        item.setItemMeta(itemmeta);
        return item;
    }

    public static ItemStack addprice(ItemStack item, String price) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(Collections.singletonList(ChatColor.GRAY + price));
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack removePrice(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta.getLore() != null) {
            itemMeta.setLore(null);
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    public static int getPrice(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> itemPrice = itemMeta.getLore();
        return Integer.parseInt(itemPrice.get(0));
    }
}

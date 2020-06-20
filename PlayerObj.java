package simon.shopsdr;

import org.bukkit.entity.Player;

public class PlayerObj {
    public String playerName;
    private int level;
    private int currency;

    public PlayerObj(int level, int currency, Player player) {
        this.level = level;
        this.currency = currency;
        playerName = player.getName();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }
}

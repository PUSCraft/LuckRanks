package co.linuxman.luckranks.managers;

import co.linuxman.luckranks.LuckRanks;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import java.util.List;
import java.util.Set;

public class ConfigManager {
    private Set<String> ranksList;
    private String guiTitle;
    private int guiRows;
    private ConfigurationSection ranks;
    private List<String> premiumRanks;

    public ConfigManager(){
        Configuration config = LuckRanks.getLuckRanks().getConfig();
        ranksList = config.getConfigurationSection("ranks").getKeys(false);
        guiTitle = config.getString("gui_title");
        guiRows = config.getInt("gui_rows");
        ranks = config.getConfigurationSection("ranks");
        premiumRanks = config.getStringList("premium");
    }

    public ConfigurationSection getRanks(){
        return ranks;
    }

    public Set<String> getRanksList(){
        return ranksList;
    }

    public String getGuiTitle(){
        return ChatColor.translateAlternateColorCodes('&',guiTitle);
    }

    public int getGuiRows(){
        return guiRows;
    }

    public List<String> getPremiumRanks() {
        return premiumRanks;
    }
}

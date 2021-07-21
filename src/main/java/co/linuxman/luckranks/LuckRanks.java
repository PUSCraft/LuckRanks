package co.linuxman.luckranks;

import co.linuxman.luckranks.commands.Commands;
import co.linuxman.luckranks.commands.CommandsTabComplete;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public class LuckRanks extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Plugin plugin;
    private static Economy econ;
    private static LuckPerms luckPerms;
    
    @Override
    public void onEnable() {
        //Test Comment
        //Load Plugin
        plugin = this;

        //Setup Economy
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp != null){
            econ = rsp.getProvider();
        }

        //Register LuckPerms
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }

        //Register Commands
        getCommand("ranks").setExecutor(new Commands());
        getCommand("ranks").setTabCompleter(new CommandsTabComplete());

        //Save default config
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        saveDefaultConfig();
    }

    public static Plugin getLuckRanks(){
        return plugin;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static LuckPerms getLuckPerms(){
        return luckPerms;
    }
}

package co.linuxman.luckranks.commands;

import co.linuxman.luckranks.LuckRanks;
import co.linuxman.luckranks.gui.LuckRanksGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            if(strings.length == 0){
                if(commandSender.hasPermission("luckranks.use")){
                    Player player = (Player) commandSender;
                    new LuckRanksGUI(player.getUniqueId());
                }
            }else if(strings[0].equals("reload")){
                if(commandSender.hasPermission("luckranks.reload")){
                    LuckRanks.getLuckRanks().reloadConfig();
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aReloaded LuckRanks"));
                }
            }
        }else{
            if(command.getName().equals("ranks")){
                if(strings[0].equals("reload")){
                    LuckRanks.getLuckRanks().reloadConfig();
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aReloaded LuckRanks"));
                }
            }
        }
        return false;
    }
}

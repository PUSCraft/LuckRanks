package co.linuxman.luckranks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.List;

public class CommandsTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> args = new ArrayList<>();
        args.add("reload");

        if(commandSender.hasPermission("luckranks.reload")){
            return args;
        }else {
            return null;
        }
    }
}

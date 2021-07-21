package co.linuxman.luckranks.gui;

import co.linuxman.luckranks.LuckRanks;
import co.linuxman.luckranks.managers.ConfigManager;
import me.mattstudios.mfgui.gui.guis.Gui;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.MetaNode;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LuckRanksGUI {
    public LuckRanksGUI(UUID playerUUID){
        //Get player from UUID
        Player player = Bukkit.getPlayer(playerUUID);

        ConfigManager cm = new ConfigManager();
        Gui gui = new Gui(cm.getGuiRows(),cm.getGuiTitle());
        LuckPerms lp = LuckRanks.getLuckPerms();
        Economy eco = LuckRanks.getEconomy();

        cm.getRanksList().forEach((rank) -> {
            ConfigurationSection rankRoot = cm.getRanks().getConfigurationSection(rank);
            ConfigurationSection rankRewards = rankRoot.getConfigurationSection("rewards");
            ConfigurationSection rankRequirements = rankRoot.getConfigurationSection("requirements");

            //Create Rank Owned Item
            Material ownedRankMat = Material.EMERALD_BLOCK;
            ItemStack ownedRankItem = new ItemStack(ownedRankMat);
            ItemMeta ownedRankItemMeta = ownedRankItem.getItemMeta();
            List<String> ownedRankLore = new ArrayList<>();

            ownedRankItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',rankRoot.getString("name")));
            ownedRankLore.add(ChatColor.translateAlternateColorCodes('&', "&a&lYou already unlocked this rank!"));

            ownedRankItemMeta.setLore(ownedRankLore);
            ownedRankItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            ownedRankItem.setItemMeta(ownedRankItemMeta);

            //Create Rank Item
            Material itemMaterial = Material.getMaterial(rankRoot.getString("material"));
            ItemStack item = new ItemStack(itemMaterial);
            ItemMeta itemMeta = item.getItemMeta();
            List<String> itemLore = new ArrayList<>();

            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',rankRoot.getString("name")));
            rankRoot.getStringList("lore").forEach((loreline) -> {
                itemLore.add(ChatColor.translateAlternateColorCodes('&', loreline));
            });
            //Add requirements lore
            int requiredXP = rankRequirements.getInt("required_xp");
            int playerXP = player.getTotalExperience();
            itemLore.add("");
            itemLore.add(ChatColor.translateAlternateColorCodes('&', "&6&lRequirements:"));
            //XP
            if(playerXP < requiredXP){
                itemLore.add(ChatColor.translateAlternateColorCodes('&',String.format("&cXP: %s (%s)", requiredXP, playerXP)));
            }else{
                itemLore.add(ChatColor.translateAlternateColorCodes('&', String.format("&aXP: %s (%s)", requiredXP, playerXP)));
            }
            //Money
            if(!eco.hasAccount(player)){
                eco.createPlayerAccount(player);
            }
            double playerMoney = eco.getBalance(player);
            double requiredMoney = rankRequirements.getDouble("required_money");
            if(playerMoney < requiredMoney){
                itemLore.add(ChatColor.translateAlternateColorCodes('&',String.format("&cMoney: %s (%s)", requiredMoney, (int)playerMoney)));
            }else if(playerMoney >= requiredMoney){
                itemLore.add(ChatColor.translateAlternateColorCodes('&',String.format("&aMoney: %s (%s)", requiredMoney, (int)playerMoney)));
            }
            //Rank
            String requiredRank = rankRequirements.getString("required_rank");
            CachedMetaData rankMeta = lp.getPlayerAdapter(Player.class).getMetaData(player);

            if(rankRequirements.getString("required_rank") != null){
                if(Boolean.parseBoolean(rankMeta.getMetaValue(requiredRank))){
                    itemLore.add(ChatColor.translateAlternateColorCodes('&', String.format("&aRequired Rank: %s", requiredRank)));
                }else{
                    itemLore.add(ChatColor.translateAlternateColorCodes('&', String.format("&cRequired Rank: %s", requiredRank)));
                }
            }

            itemMeta.setLore(itemLore);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(itemMeta);

            //Check if player owns current rank
            boolean rankCheck = Boolean.parseBoolean(rankMeta.getMetaValue(rank));
            GuiItem guiItem = null;

            if(rankCheck){
                guiItem = new GuiItem(ownedRankItem, event -> {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3.0F, 1.0F);
                });
            }else{
                guiItem = new GuiItem(item, event -> {
                    //Check if requirements are met
                    boolean xpCheck = playerXP >= requiredXP;
                    boolean moneyCheck = playerMoney >= requiredMoney;
                    boolean requiredRankCheck = Boolean.parseBoolean(rankMeta.getMetaValue(requiredRank));

                    if(xpCheck && moneyCheck && requiredRankCheck){
                        //Check if player has a premium rank
                        boolean hasPremRank = false;

                        //Get all player groups
                        List<String> groups = new ArrayList<>();
                        player.getEffectivePermissions().forEach(perm -> {
                            if(perm.getPermission().startsWith("group")){
                                groups.add(perm.getPermission());
                            }
                        });
                        for(String premRank : cm.getPremiumRanks()){
                            if(groups.contains("group."+premRank)){
                                hasPremRank = true;
                                break;
                            }
                        }

                        //Give rewards
                        double rewardMoney = rankRewards.getDouble("money");
                        int rewardXP = rankRewards.getInt("xp");
                        List<String> rewardCommands = rankRewards.getStringList("commands");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&a&lYou have unlocked: %s", rankRoot.getString("name"))));
                        //Reward money
                        if(rewardMoney > 0.0){
                            eco.depositPlayer(player, rewardMoney);
                        }
                        //Reward XP
                        if(rewardXP > 0){
                            player.giveExp(rewardXP);
                        }
                        //Reward commands
                        if(rewardCommands.size() > 0){
                            rewardCommands.forEach(cmd -> {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()).replace("%playeruuid%", player.getUniqueId().toString()));
                            });
                        }

                        User user = lp.getPlayerAdapter(Player.class).getUser(player);
                        MetaNode node = MetaNode.builder(rank, Boolean.toString(true)).build();

                        //Reward permissions
                        rankRewards.getStringList("permissions").forEach(perm -> {
                            user.data().add(Node.builder(perm).build());
                        });

                        //Take requirements
                        eco.withdrawPlayer(player,requiredMoney);
                        player.setTotalExperience(player.getTotalExperience() - requiredXP);

                        //Add custom meta for rank
                        user.data().add(node);
                        lp.getUserManager().saveUser(user);
                        //If player doesn't have a premium rank, set parent to new rank
                        if(!hasPremRank){
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("lp user %s parent set %s", player.getUniqueId().toString(), rank));
                        }
                    }else{
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have the requirements for this rank!"));
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 3.0F, 1.0F);
                    gui.close((HumanEntity) player);
                });
            }

            gui.addItem(guiItem);
        });

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });

        gui.open(player);
    }
}

package QuarantineMC.CodeConduit.LootPlugin.commands;

import QuarantineMC.CodeConduit.LootPlugin.Main;
import QuarantineMC.CodeConduit.LootPlugin.Utils;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveLootFinder implements CommandExecutor {
    //Variables
    private Main plugin;

    //Constructor
    public GiveLootFinder(Main plugin) {
        this.plugin = plugin;

        plugin.getCommand("lootfinder").setExecutor(this);
    }

    //Command Executors
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player player = (Player) sender;
        if (player.hasPermission("lootFinder.use")) {
            if (!plugin.getDataConfig().getBoolean("players." + player.getUniqueId() + ".givenLootFinder") || plugin.getDataConfig().get("players." + player.getUniqueId() + ".givenLootFinder") == null || player.hasPermission("lootSelector.use")) {
                plugin.getDataConfig().getBoolean("players." + player.getUniqueId() + ".givenLootFinder", true);
                boolean clearSlot = false;
                for (int i = 0; i < 36; i++) {
                    if (player.getInventory().getItem(i) == null) {
                        clearSlot = true;
                    }
                }
                if (clearSlot) {
                    player.getInventory().addItem(Main.lootFinder);
                } else {
                    player.getWorld().dropItem(player.getLocation(), Main.lootFinder);
                }
                player.sendMessage(Utils.chat("&6You have received your Treasure Spade!"));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            } else {
                player.sendMessage(Utils.chat("&cYou have already received a Treasure Spade! (You can only ever receive one)"));
            }
        } else {
            //No perms
            player.sendMessage(Utils.chat("&cYou do not have permission to use that command!"));
        }
        plugin.saveData();
        return true;
    }
}

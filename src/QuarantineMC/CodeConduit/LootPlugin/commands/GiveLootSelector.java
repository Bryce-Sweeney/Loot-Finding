package QuarantineMC.CodeConduit.LootPlugin.commands;

import QuarantineMC.CodeConduit.LootPlugin.Main;
import QuarantineMC.CodeConduit.LootPlugin.Utils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveLootSelector implements CommandExecutor {
    //Variables
    private Main plugin;

    //Constructor
    public GiveLootSelector(Main plugin) {
        this.plugin = plugin;

        plugin.getCommand("lootselector").setExecutor(this);
    }

    //Command Executors
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player player = (Player) sender;
        if (player.hasPermission("lootSelector.use")) {
            //Execute Action
            player.getInventory().addItem(Main.lootSelect);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            player.sendMessage(Utils.chat("&6You have received the loot selector!"));
        } else {
            //No perms
            player.sendMessage(Utils.chat("&cYou do not have permission to use that command!"));
        }
        return true;
    }
}

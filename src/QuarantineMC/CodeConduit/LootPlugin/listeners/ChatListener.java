package QuarantineMC.CodeConduit.LootPlugin.listeners;

import QuarantineMC.CodeConduit.LootPlugin.Main;
import QuarantineMC.CodeConduit.LootPlugin.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatListener implements Listener {
    //Variables
    private Main plugin;

    //Constructor
    public ChatListener(Main plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    //Chat Listener
    public void onChat(PlayerChatEvent e) {
        Player player = e.getPlayer();
        String msg = e.getMessage();
        Block block = (Block) plugin.getDataConfig().get("players." + player.getUniqueId() + ".savedBlock");
        //Is the message a float
        boolean isFloat = true;
        try {
            Float.parseFloat(msg);
        } catch (NumberFormatException error) {
            isFloat = false;
        }


        if (plugin.getDataConfig().getBoolean("players." + player.getUniqueId() + ".listeningChat")) {
            if ("cancel".equals(msg.toLowerCase())) {
                //Delete data
                plugin.getDataConfig().set("players." + player.getUniqueId() + ".listeningChat", false);
                plugin.getDataConfig().set("lootBlocks." + plugin.getDataConfig().getInt("lootBlocks.nextID"), null);

                //Restore block
                block.getLocation().getBlock().setType(block.getType());
                block.getLocation().getBlock().setBlockData(block.getBlockData());

                //Notify player
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                player.sendMessage(Utils.chat("&6Loot creation canceled."));
            } else if ("default".equals(msg.toLowerCase())) {
                //Do data configuration
                plugin.getDataConfig().set("lootBlocks." + plugin.getDataConfig().getInt("players." + player.getUniqueId() + ".selectedLootBlock") + ".weight", 0f);
                plugin.getDataConfig().set("players." + player.getUniqueId() + ".listeningChat", false);

                //Restore block
                block.getLocation().getBlock().setType(block.getType());
                block.getLocation().getBlock().setBlockData(block.getBlockData());

                //Notify player
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                player.sendMessage(Utils.chat("&6Loot creation completed."));
            } else {
                if (isFloat) {
                    //Do data configuration
                    plugin.getDataConfig().set("lootBlocks." + plugin.getDataConfig().getInt("players." + player.getUniqueId() + ".selectedLootBlock") + ".weight", Float.parseFloat(msg));
                    plugin.getDataConfig().set("players." + player.getUniqueId() + ".listeningChat", false);

                    //Restore block
                    block.getLocation().getBlock().setType(block.getType());
                    block.getLocation().getBlock().setBlockData(block.getBlockData());

                    //Notify player
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage(Utils.chat("&6Loot creation completed."));
                } else {
                    player.sendMessage("&cPlease enter a valid option.");
                }
            }
        }
    }
}

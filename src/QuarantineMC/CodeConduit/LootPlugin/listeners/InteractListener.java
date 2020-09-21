package QuarantineMC.CodeConduit.LootPlugin.listeners;

import QuarantineMC.CodeConduit.LootPlugin.Main;
import QuarantineMC.CodeConduit.LootPlugin.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {
    //Variables
    private Main plugin;

    //Constructor
    public InteractListener(Main plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    //Interact Event Listener
    public void onInteract(PlayerInteractEvent e) {
        //Temporary Variables
        Player player = e.getPlayer();
        Action action = e.getAction();
        ItemStack hand = player.getItemInHand();
        Block block = e.getClickedBlock();
        //Nested if statements
        player.sendMessage("debug 0");
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            player.sendMessage("debug 1");
            if (Main.lootSelect.equals(hand) && player.hasPermission("lootFindSelector.use")) {
                player.sendMessage("debug 2");
                //Do configuration for players
                plugin.getDataConfig().set("players." + player.getUniqueId() + ".selectedLootBlock", plugin.getDataConfig().getInt("lootBlocks.nextID"));
                plugin.getDataConfig().set("players." + player.getUniqueId() + ".listeningChat", true);

                //Do configuration for lootBlocks
                plugin.getDataConfig().set("lootBlocks." + plugin.getDataConfig().getInt("lootBlocks.nextID") + ".location", block.getLocation());
                plugin.getDataConfig().set("lootBlocks.nextID", plugin.getDataConfig().getInt("lootBlock.nextID") + 1);

                //Do visual effects
                plugin.getDataConfig().set("players." + player.getUniqueId() + ".savedBlock", block);
                block.setType(Material.BARREL);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                player.sendMessage(Utils.chat("&6Please state the weight of the chest.&e (-1.0 is always common, 1.0 is always legendary)"));
                player.sendMessage(Utils.chat("&6If you want to use the default chances, say &edefault&6. If you want to cancel, do &ecancel&e."));

                plugin.saveData(); //Save
            }
        }
    }
}

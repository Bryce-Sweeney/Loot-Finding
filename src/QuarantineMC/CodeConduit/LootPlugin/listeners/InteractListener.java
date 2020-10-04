package QuarantineMC.CodeConduit.LootPlugin.listeners;

import QuarantineMC.CodeConduit.LootPlugin.Main;
import QuarantineMC.CodeConduit.LootPlugin.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InteractListener implements Listener {
    //Variables
    private final Main plugin;

    //Constructor
    public InteractListener(Main plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void placeBarrel(Player player, Block block) {
        //Do configuration for players
        plugin.getDataConfig().set("players." + player.getUniqueId() + ".savedBlock.location", block.getLocation());
        plugin.getDataConfig().set("players." + player.getUniqueId() + ".savedBlock.type", String.valueOf(block.getType()));
        plugin.getDataConfig().set("players." + player.getUniqueId() + ".selectedLootBlock", plugin.getDataConfig().getInt("lootBlocks.nextID"));
        plugin.getDataConfig().set("players." + player.getUniqueId() + ".listeningChat", true);

        //Do configuration for lootBlocks
        plugin.getDataConfig().set("lootBlocks." + plugin.getDataConfig().getInt("lootBlocks.nextID") + ".location", block.getLocation());
        plugin.getDataConfig().set("lootBlocks.nextID", plugin.getDataConfig().getInt("lootBlocks.nextID") + 1);

        //Do visual effects
        block.setType(Material.BARREL);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        player.sendMessage(Utils.chat("&6Please state the weight of the chest.&e (-1.0 is always common, 1.0 is always legendary)"));
        player.sendMessage(Utils.chat("&6If you want to use the default chances, say &edefault&6. If you want to cancel, do &ecancel&6."));

        plugin.saveData(); //Save
    }

    //Interact Event Listener
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        //Temporary Variables
        Player player = e.getPlayer();
        Action action = e.getAction();
        ItemStack hand = player.getInventory().getItemInMainHand();
        Block block = e.getClickedBlock();
        boolean lootExists = false;
        int activeLootBlock = -1;

        for (int i = 0; i < plugin.getDataConfig().getInt("lootBlocks.nextID"); i++) {
            assert block != null;
            if (block.getLocation().equals(plugin.getDataConfig().getLocation("lootBlocks." + i + "location"))) {
                activeLootBlock = i;
                break;
            }
        }


        //Nested if statements
        if (hand.equals(Main.lootSelect) && player.hasPermission("lootSelector.use") && action.equals(Action.LEFT_CLICK_BLOCK)) {
            if (plugin.getDataConfig().getBoolean("players." + player.getUniqueId() + ".listeningChat")) {
                player.sendMessage(Utils.chat("&cPlease answer the first question before moving to another loot block!"));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                e.setCancelled(true);
            } else {
                if (plugin.getDataConfig().getInt("lootBlocks.nextID") == 0) {
                    e.setCancelled(true);
                    assert block != null;
                    placeBarrel(player, block);
                } else {
                    for (int i = 0; i < plugin.getDataConfig().getInt("lootBlocks.nextID"); i++) {
                        assert block != null;
                        if (block.getLocation().equals(plugin.getDataConfig().get("lootBlocks." + i + ".location"))) {
                            player.sendMessage(Utils.chat("&cThis loot block already exists."));
                            player.sendMessage(Utils.chat("&cWould you like to delete it? (y/n)"));
                            plugin.getDataConfig().set("players." + player.getUniqueId() + ".selectedLootBlock", i);
                            plugin.getDataConfig().set("players." + player.getUniqueId() + ".canceling", true);
                            block.setType(block.getType());
                            lootExists = true;
                            break;
                        }
                    }
                    if (!lootExists) {
                        e.setCancelled(true);
                        placeBarrel(player, block);
                    }
                }
            }
        }
        if (hand.equals(Main.lootFinder) && player.hasPermission("lootFinder.use") && action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (!plugin.getDataConfig().getStringList("lootBlocks." + activeLootBlock + ".searchedPlayers").contains(player.getUniqueId())) {
                if (!plugin.getDataConfig().getBoolean("players." + player.getUniqueId() + ".searching")) {
                    player.sendMessage(Utils.chat("&6Would you like to search this block? (y/n)"));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    plugin.getDataConfig().set("players." + player.getUniqueId() + ".searching", true);
                    assert block != null;
                    plugin.getDataConfig().set("players." + player.getUniqueId() + ".searchedLocation", block.getLocation());
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage(Utils.chat("&cPlease confirm or deny whether you want to search the first block before moving on to another."));
                }
            } else {
                player.sendMessage(Utils.chat("&cYou have already searched this loot block!"));
                player.playSound(player.getLocation(), Sound.ENTITY_SILVERFISH_AMBIENT, 1.0f, 1.0f);
            }
        }
    }
}

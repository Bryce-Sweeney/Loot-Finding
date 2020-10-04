package QuarantineMC.CodeConduit.LootPlugin.listeners;

import QuarantineMC.CodeConduit.LootPlugin.Main;
import QuarantineMC.CodeConduit.LootPlugin.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatListener implements Listener {
    //Variables
    private Main plugin;

    //Constructor
    public ChatListener(Main plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    //Chat Listener
    @EventHandler
    public void onChat(PlayerChatEvent e) {
        Player player = e.getPlayer();
        String msg = e.getMessage();
        Location blockLocation = plugin.getDataConfig().getLocation("players." + player.getUniqueId() + ".savedBlock.location");
        Material blockType = Material.getMaterial(Objects.requireNonNull(plugin.getDataConfig().getString("players." + player.getUniqueId() + ".savedBlock.type")));
        List<String> searchedPlayers = new ArrayList<>();
        boolean successfulSearch = true;
        int activeLootBlock = -1;
        for (int i = 0; i < plugin.getDataConfig().getInt("lootBlocks.nextID"); i++) {
            if (Objects.equals(plugin.getDataConfig().getLocation("players." + player.getUniqueId() + ".searchedLocation"), plugin.getDataConfig().getLocation("lootBlocks." + i + "location"))) {
                activeLootBlock = i;
                break;
            }
        }
        if (activeLootBlock == -1) {
            successfulSearch = false;
        }
        searchedPlayers = plugin.getDataConfig().getStringList("lootBlocks." + activeLootBlock + ".searchedPlayers");

        //Is the message a float
        boolean isFloat = true;
        try {
            Float.parseFloat(msg);
        } catch (NumberFormatException error) {
            isFloat = false;
        }
        if (plugin.getDataConfig().getBoolean("players." + player.getUniqueId() + ".listeningChat")) {
            switch (msg.toLowerCase()) {
                case "cancel":
                    //Delete data
                    plugin.getDataConfig().set("players." + player.getUniqueId() + ".listeningChat", false);
                    plugin.getDataConfig().set("lootBlocks." + plugin.getDataConfig().get("players." + player.getUniqueId() + ".selectedLootBlock"), null);

                    //Restore block
                    assert blockLocation != null;
                    assert blockType != null;
                    blockLocation.getBlock().setType(blockType);

                    //Notify player
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage(Utils.chat("&6Loot creation canceled."));
                    break;
                case "default":
                    //Do data configuration
                    plugin.getDataConfig().set("lootBlocks." + plugin.getDataConfig().getInt("players." + player.getUniqueId() + ".selectedLootBlock") + ".weight", 0f);
                    plugin.getDataConfig().set("players." + player.getUniqueId() + ".listeningChat", false);

                    //Restore block
                    assert blockLocation != null;
                    assert blockType != null;
                    blockLocation.getBlock().setType(blockType);

                    //Notify player
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    player.sendMessage(Utils.chat("&6Loot creation completed."));
                    break;
                default:
                    if (isFloat) {
                        if (Float.parseFloat(msg) >= -1.0f && Float.parseFloat(msg) <= 1.0) {
                            //Do data configuration
                            plugin.getDataConfig().set("lootBlocks." + plugin.getDataConfig().getInt("players." + player.getUniqueId() + ".selectedLootBlock") + ".weight", Float.parseFloat(msg));
                            plugin.getDataConfig().set("players." + player.getUniqueId() + ".listeningChat", false);

                            //Restore block
                            assert blockLocation != null;
                            assert blockType != null;
                            blockLocation.getBlock().setType(blockType);

                            //Notify player
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                            player.sendMessage(Utils.chat("&6Loot creation completed."));
                        } else {
                            //Notify player
                            player.sendMessage(Utils.chat("&cPlease enter a valid option! (-1.0 to 1.0)"));
                        }
                    } else {
                        player.sendMessage(Utils.chat("&cPlease enter a valid option!"));
                    }
                    break;
            }
            plugin.saveData();
            e.setCancelled(true);
        }
        if (plugin.getDataConfig().getBoolean("players." + player.getUniqueId() + ".canceling")) {
            e.setCancelled(true);
            switch (msg.toLowerCase()) {
                case "y":
                    player.sendMessage(Utils.chat("&eDeleted Loot Block"));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    plugin.getDataConfig().set("players." + player.getUniqueId() + ".canceling", false);
                    plugin.getDataConfig().set("lootBlocks." + plugin.getDataConfig().getInt("players." + player.getUniqueId() + ".selectedLootBlock"), null);
                    break;
                case "n":
                    player.sendMessage(Utils.chat("&eLoot block has not been changed."));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    plugin.getDataConfig().set("players." + player.getUniqueId() + ".canceling", false);
                    break;
                default:
                    player.sendMessage(Utils.chat("&cPlease enter a valid option."));
                    break;
            }
            plugin.saveData();
        }
        if (plugin.getDataConfig().getBoolean("players." + player.getUniqueId() + ".searching")) {
            e.setCancelled(true);
            switch (msg.toLowerCase()) {
                case "y":
                    player.sendMessage(Utils.chat("&6Searching block..."));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    plugin.getDataConfig().set("players." + player.getUniqueId() + ".searching", false);

                    if (successfulSearch) {
                        searchedPlayers.add(String.valueOf(player.getUniqueId()));
                        plugin.getDataConfig().set("lootBlocks." + activeLootBlock + ".searchedPlayers", searchedPlayers);

                        //Selecting Loot
                        int randVal = (int) Math.floor(Math.random() * 100);
                        int threshHold = plugin.getConfig().getInt("lootTable.legendary.chance");
                        String selectedRarity;

                        //Legendary Test
                        if (randVal < threshHold) {
                            selectedRarity = "legendary";
                            player.sendMessage("Legendary");
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.5f, 1.0f);
                        } else {
                            threshHold += plugin.getConfig().getInt("lootTable.epic.chance");
                            //Epic Test
                            if (randVal < threshHold) {
                                selectedRarity = "epic";
                                player.sendMessage("Epic");
                                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.4f, 1.0f);
                            } else {
                                threshHold += plugin.getConfig().getInt("lootTable.rare.chance");
                                //Rare Test
                                if (randVal < threshHold) {
                                    selectedRarity = "rare";
                                    player.sendMessage("Rare");
                                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.3f, 1.0f);
                                } else {
                                    threshHold += plugin.getConfig().getInt("lootTable.uncommon.chance");
                                    //Uncommon Test
                                    if (randVal < threshHold) {
                                        selectedRarity = "uncommon";
                                        player.sendMessage("Uncommon");
                                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.2f, 1.0f);
                                    } else {
                                        selectedRarity = "common";
                                        player.sendMessage("Common");
                                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.1f, 1.0f);
                                    }
                                }
                            }
                        } //Rarity has been decided

                        //Determining the random ID
                        int maxVal = plugin.getConfig().getInt("lootTable." + selectedRarity + ".totalLoot") - 1;
                        int selectedID = (int) (Math.random() * (maxVal + 1));

                        ItemStack loot = new ItemStack(Material.valueOf(plugin.getConfig().getString("lootTable." + selectedRarity + "." + selectedID + ".item")));
                        for (int i = 0; i < plugin.getConfig().getInt("lootTable." + selectedRarity + "." + selectedID + ".amount"); i++) {
                            player.getInventory().addItem(loot);
                        }
                    } else {
                        player.sendMessage(Utils.chat("&cYou didn't find anything. ):"));
                        player.playSound(player.getLocation(), Sound.ENTITY_SILVERFISH_AMBIENT, 1.0f, 1.0f);
                    }
                    break;
                case "n":
                    player.sendMessage(Utils.chat("&6Search cancelled."));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    plugin.getDataConfig().set("players." + player.getUniqueId() + ".searching", false);
                    break;
                default:
                    player.sendMessage(Utils.chat("&cPlease enter a valid option."));
                    break;
            }
        }
    }
}
/*
new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 1.0f);
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    }
                                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.3f, 1.0f);
                                }
                            }.runTaskAsynchronously(plugin);
 */
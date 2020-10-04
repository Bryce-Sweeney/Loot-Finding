package QuarantineMC.CodeConduit.LootPlugin.listeners;

import QuarantineMC.CodeConduit.LootPlugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BreakListener implements Listener {
    //Variables
    private Main plugin;

    //Constructor
    public BreakListener(Main plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    //Event Handler
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.equals(Main.lootSelect) || hand.equals(Main.lootFinder)) {
            e.setCancelled(true);
        }
        if (hand.equals(Main.lootFinder)) {
            player.getInventory().getItemInMainHand().setDurability((short) (player.getInventory().getItemInMainHand().getDurability() + 1));
        }
    }
}

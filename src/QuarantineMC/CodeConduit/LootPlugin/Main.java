package QuarantineMC.CodeConduit.LootPlugin;

import QuarantineMC.CodeConduit.LootPlugin.commands.GiveLootFinder;
import QuarantineMC.CodeConduit.LootPlugin.commands.GiveLootSelector;
import QuarantineMC.CodeConduit.LootPlugin.listeners.BreakListener;
import QuarantineMC.CodeConduit.LootPlugin.listeners.ChatListener;
import QuarantineMC.CodeConduit.LootPlugin.listeners.InteractListener;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {
    //Variables
    private final File dataFile = new File(getDataFolder(), "data.yml");
    private final FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    private final FileConfiguration config = this.getConfig();

    public static ItemStack lootSelect = new ItemStack(Material.BLAZE_POWDER);
    public ItemMeta lootSelectMeta = lootSelect.getItemMeta();

    public static ItemStack lootFinder = new ItemStack(Material.WOODEN_SHOVEL);
    public ItemMeta lootFinderMeta = lootFinder.getItemMeta();

    //Executes when plugin is enabled
    public void onEnable() {
        //Create data.yml if not created yet
        if (!dataFile.exists()) {
            saveResource("data.yml", false);
            getDataConfig().set("lootBlocks.nextID", 0);
        }
        //Activate Classes
        new GiveLootSelector(this);
        new InteractListener(this);
        new ChatListener(this);
        new BreakListener(this);
        new GiveLootFinder(this);
        //Assign Itemstack Metas
        assignMetas();
        //Load config
        loadConfig();
    }

    //Executed when plugin is disabled
    public void onDisable() {
        saveData();
        saveConfig();
    }

    //Method for item stack meta's
    public void assignMetas() {
        //lootSelect
        lootSelectMeta.setDisplayName(Utils.chat("&6&lSelect Loot Block"));
        lootSelectMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        lootSelectMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        lootSelect.setItemMeta(lootSelectMeta);

        //lootFinder
        lootFinderMeta.setDisplayName(Utils.chat("&6&lTreasure Spade"));
        lootFinderMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        lootFinderMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        lootFinder.setItemMeta(lootFinderMeta);
    }

    //Getters for data
    public FileConfiguration getDataConfig() {
        return dataConfig;
    }
    public File getDataFile() {
        return dataFile;
    }

    //Save data
    public void saveData() {
        try {
            getDataConfig().save(getDataFile());
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    //Config Loader
    public void loadConfig() {
        getConfig().options().copyDefaults(true);
    }
}

package ca.vire.ChatMunger;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigLoader {

    // Returns a new Settings object
    public static Settings LoadConfig(JavaPlugin plugin) {
        
        Settings settings = new Settings();
        
        settings.Enable = plugin.getConfig().getBoolean("enable");
        if (settings.Enable)
            plugin.getLogger().info("The plugin is enabled!");
        else
            plugin.getLogger().info("The plugin is NOT enabled.");

        return settings;
    }
    
}

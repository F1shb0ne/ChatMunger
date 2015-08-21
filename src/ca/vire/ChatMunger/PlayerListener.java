package ca.vire.ChatMunger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener {
    
    JavaPlugin LocalPlugin;

    public PlayerListener(JavaPlugin plugin) {
        LocalPlugin = plugin;
    }
    
}

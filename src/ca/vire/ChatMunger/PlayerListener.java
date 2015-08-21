package ca.vire.ChatMunger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener {

    JavaPlugin LocalPlugin;

    public PlayerListener(JavaPlugin plugin) {
        LocalPlugin = plugin;
    }


    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event) {
        String PlayerName = event.getPlayer().getName();
        String PlayerIP = event.getPlayer().getAddress().toString();
        String Message = event.getMessage();
        LocalPlugin.getLogger().info("Player " + PlayerName + " said: " + Message);
    }

}
package ca.vire.ChatMunger.Commands;

import org.bukkit.plugin.java.JavaPlugin;

public class cmdSpeak {
    
    public static void Speak(JavaPlugin plugin, String msg) {
        plugin.getLogger().info("The message sent was: \"" + msg + "\"");
    }
}

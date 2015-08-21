package ca.vire.ChatMunger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {
    
    Settings LocalSettings;
    
    @Override
    public void onEnable() {
        getLogger().info("Starting up.");

        // Load configuration.
        LocalSettings = ConfigLoader.LoadConfig(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down.");
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("greeting")) {
            if (sender.getName().contentEquals("CONSOLE")) {
                // getLogger().info("Hi Console");
                sender.getServer().broadcastMessage("Hi Console!");
            } else
            {
                sender.getServer().broadcastMessage("Hi " + sender.getName());
            }
            
            return true;
        }
        
        if (cmd.getName().equalsIgnoreCase("speak"))
        {
            sender.sendMessage("Player sent /speak command");
            
            return true;
        }
        
        return false;
    }

}

package ca.vire.ChatMunger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {
    
    Settings LocalSettings;
    PlayerListener LocalPlayerListener;
    
    @Override
    public void onEnable() {
        getLogger().info("Starting up.");

        // Load configuration.
        LocalSettings = ConfigLoader.LoadConfig(this);

        LocalPlayerListener = new PlayerListener(this);

        // Register player listener handler
        this.getServer().getPluginManager().registerEvents(LocalPlayerListener, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down.");
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        if (cmd.getName().equalsIgnoreCase("speak"))
        {
            for (Player p: sender.getServer().getOnlinePlayers()) {
                if (p.getName().contentEquals(sender.getName())) {
                    p.sendMessage("You used the /speak command");
                } else {
                    p.sendMessage(sender.getName() + " used the /speak command");
                }
            }
            
            return true;
        }
        
        return false;
    }

}

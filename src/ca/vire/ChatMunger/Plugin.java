package ca.vire.ChatMunger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import java.util.HashMap;


public final class Plugin extends JavaPlugin {

    Settings LocalSettings;
    PlayerListener LocalPlayerListener;
    HashMap<String, Language> LanguageTree;
    PlayerManager PlayerMgr;

    @Override
    public void onEnable() {
        getLogger().info("Starting up.");

        // Load configuration.
        LocalSettings = ConfigLoader.LoadConfig(this);

        // Load languages
        LanguageTree = LanguageManager.LoadLanguageTree(this);

        // Instantiate PlayerManager object
        PlayerMgr = new PlayerManager(this);

        LocalPlayerListener = new PlayerListener(this);
        // Register player listener handler
        this.getServer().getPluginManager().registerEvents(LocalPlayerListener, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down.");

        PlayerMgr.SavePlayerData();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String message = "";

        if (cmd.getName().equalsIgnoreCase("speak")) {
            // assemble the message being sent as a single string
            for (String s: args)
                message += s + " ";

            // remove any surrounding whitespace
            message = message.trim();

            if (sender.getName().contentEquals("CONSOLE"))
                this.getLogger().info("Only players may use this!");
            else
                Commands.Speak(this, PlayerMgr, LanguageTree, sender, message);

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("lang")) {

            if (args.length == 1) {
                Commands.Lang(this, sender, args[0]);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: /lang <language>");
            }

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("teachlang")) {
            if (args.length == 2) {
                Commands.TeachLang(this, sender, args[0], args[1]);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: /teachlang <player> <language>");
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("acceptlang")) {
            if (args.length == 0) {
                Commands.AcceptLang(this, sender);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: /acceptlang");
            }

            return true;
        }

        return false;
    }

}

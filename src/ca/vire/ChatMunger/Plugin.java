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
        // Load configuration.
        LocalSettings = ConfigLoader.LoadConfig(this);

        // Load languages
        LanguageTree = LanguageManager.LoadLanguageTree(this);

        // Instantiate PlayerManager object
        PlayerMgr = new PlayerManager(this, LocalSettings);

        LocalPlayerListener = new PlayerListener(this);
        // Register player listener handler
        this.getServer().getPluginManager().registerEvents(LocalPlayerListener, this);
    }

    @Override
    public void onDisable() {
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

            // Bail if the message is empty
            if (message.length() == 0) {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: /speak <message>");
                return true;
            }

            if (sender.getName().contentEquals("CONSOLE"))
                this.getLogger().info("Only players may use this!");
            else
                Commands.Speak(this, LocalSettings, PlayerMgr, LanguageTree, sender, message);

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("lang")) {

            if (sender.getName().contentEquals("CONSOLE")) {
                this.getLogger().info("Only players may use this!");
                return true;
            }

            if (args.length == 1) {
                Commands.Lang(this, PlayerMgr, LanguageTree, sender, args[0]);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: /lang <language>");
            }

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("teachlang")) {
            if (sender.getName().contentEquals("CONSOLE")) {
                this.getLogger().info("Only players may use this!");
                this.getLogger().info("use /givelang instead");
                return true;
            }
            if (args.length == 2) {
                Commands.TeachLang(this, LocalSettings, PlayerMgr, LanguageTree, sender, args[0], args[1]);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: /teachlang <player> <language>");
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("acceptlang")) {
            if (sender.getName().contentEquals("CONSOLE")) {
                this.getLogger().info("Only players may use this!");
                return true;
            }

            if (args.length == 0) {
                Commands.AcceptLang(this, LocalSettings, PlayerMgr, LanguageTree, sender);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: /acceptlang");
            }

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("langmenu")) {
            if (args.length == 0) {
                Commands.LangMenu(this, LocalSettings, PlayerMgr, LanguageTree, sender);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: /langmenu");
            }

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("givelang")) {
            if (args.length == 2) {
                Commands.GiveLang(this, PlayerMgr, LanguageTree, sender, args[0], args[1]);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: /givelang <player> <language>");
            }

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("removelang")) {
            if (args.length == 2) {
                Commands.RemoveLang(this, PlayerMgr, LanguageTree, sender, args[0], args[1]);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: /removelang <player> <language>");
            }

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("langinfo")) {
            if (args.length == 1) {
                // Wants information about the language specified in args[0]
                Commands.LangInfo(this, PlayerMgr, LanguageTree, sender, args[0]);
                // Wants more general information.
            } else if (args.length == 0) {
                Commands.LangInfo(this, PlayerMgr, LanguageTree, sender, null);
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: /langinfo [language]");
            }

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("langreload")) {
            if (args.length == 0) {
                // Reload languages
                LanguageTree = LanguageManager.LoadLanguageTree(this);
                sender.sendMessage(ChatColor.GREEN + "Language files reloaded.");
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Usage: /langreload");
            }

            return true;
        }

        return false;
    }

}

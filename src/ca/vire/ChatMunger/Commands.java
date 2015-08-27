package ca.vire.ChatMunger;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;

public class Commands {

    public static void Speak(JavaPlugin plugin, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender, String message) {
        String player = sender.getName();
        String MungedMessage, out;

        String CurrentLanguage = pMgr.GetPlayerCurrentLanguage(player);

        if (CurrentLanguage.equals("Common"))
            if (pMgr.NumLanguagesKnown(player) == 0) {
                sender.sendMessage(ChatColor.DARK_RED + "You don't know any other languages.");
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You haven't selected a language yet.");
            }

        else {
            MungedMessage = Munger.ProcessMessage(tree.get(CurrentLanguage).Vocab, message);

            out = player + ": ";
            out += "" + ChatColor.WHITE + ChatColor.ITALIC + "[";
            out += "" + ChatColor.BLUE + ChatColor.ITALIC + CurrentLanguage;
            out += "" + ChatColor.WHITE + ChatColor.ITALIC + "] ";
            out += "" + ChatColor.AQUA + MungedMessage;

            sender.sendMessage(out);
        }

        /*
        determine which language the player is currently using

        have the text munged into a new string

        iterate through each player on the server

            if player has chat bypass permission || is Op?
                pass unmunged msg to player
            else if player knows language speaker is using
                pass unmunged message to that player
            else
                pass the munged message to that player

        pass unmunged message to console
        */

    }

    public static void Lang(JavaPlugin plugin, CommandSender sender, String language) {
        String player = sender.getName();

        plugin.getLogger().info(player + " is switching to language " + language);
    }

    public static void TeachLang(JavaPlugin plugin, CommandSender sender, String player, String language) {
        String player_teacher = sender.getName();

        plugin.getLogger().info(player_teacher + " is issuing a /teachlang request to " + player + " to learn " + language);
    }

    // Intended for administrators
    public static void GiveLang(JavaPlugin plugin, CommandSender sender, String player, String language) {
        String player_teacher = sender.getName();

        plugin.getLogger().info("Giving " + language + " to " + player);


    }

    public static void AcceptLang(JavaPlugin plugin, CommandSender sender) {
        String player_student = sender.getName();

        plugin.getLogger().info(player_student + " has responded to a /teachlang request");
    }
}

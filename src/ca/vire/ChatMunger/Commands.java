package ca.vire.ChatMunger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;

public class Commands {

    public static void Speak(JavaPlugin plugin, CommandSender sender, String message) {
        String player = sender.getName();

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

        plugin.getLogger().info(player + " has spoken \"" + message + "\"");
    }

    public static void Lang(JavaPlugin plugin, CommandSender sender, String language) {
        String player = sender.getName();

        plugin.getLogger().info(player + " is switching to language " + language);
    }

    public static void TeachLang(JavaPlugin plugin, CommandSender sender, String player, String language) {
        String player_teacher = sender.getName();

        plugin.getLogger().info(player_teacher + " is issuing a /teachlang request to " + player + " to learn " + language);
    }

    public static void AcceptLang(JavaPlugin plugin, CommandSender sender) {
        String player_student = sender.getName();

        plugin.getLogger().info(player_student + " has responded to a /teachlang request");
    }
}

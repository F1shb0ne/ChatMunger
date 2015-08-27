package ca.vire.ChatMunger;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands {

    public static void Speak(JavaPlugin plugin, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender, String message) {
        String player = sender.getName();
        String MungedMessage;
        String header_known, header_unknown, outMunged, outUnmunged;

        String CurrentLanguage = pMgr.GetPlayerCurrentLanguage(player);

        if (CurrentLanguage.equals("Common"))
            if (pMgr.NumLanguagesKnown(player) == 0) {
                sender.sendMessage(ChatColor.DARK_RED + "You don't know any other languages.");
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You haven't selected a language yet.");
            }

        else {
            MungedMessage = Munger.ProcessMessage(tree.get(CurrentLanguage).Vocab, message);

            header_known = player + ": ";
            header_known += "" + ChatColor.WHITE + ChatColor.ITALIC + "[";
            header_known += "" + ChatColor.BLUE + ChatColor.ITALIC + CurrentLanguage;
            header_known += "" + ChatColor.WHITE + ChatColor.ITALIC + "] ";

            header_unknown = player + ": ";
            header_unknown += "" + ChatColor.WHITE + ChatColor.ITALIC + "[";
            header_unknown += "" + ChatColor.DARK_RED + ChatColor.ITALIC + CurrentLanguage;
            header_unknown += "" + ChatColor.WHITE + ChatColor.ITALIC + "] ";

            outMunged = header_unknown + ChatColor.AQUA + MungedMessage;
            outUnmunged = header_known + ChatColor.AQUA + message;

            // Show the player what others who don't know the language will see
            sender.sendMessage(outMunged);

            // For each player
            for (Player p: plugin.getServer().getOnlinePlayers()) {
                // Ignore the speaking player
                if (p.getName().equals(player))
                    continue;

                // If this player knows the language,
                if (pMgr.PlayerKnowsLanguage(p.getName(), CurrentLanguage)) {
                    // They see the unmunged message
                    p.sendMessage(outUnmunged);
                } else {
                    // Otherwise they're clueless to what was said.
                    p.sendMessage(outMunged);
                }
            }
        }
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

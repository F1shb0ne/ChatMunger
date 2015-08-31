package ca.vire.ChatMunger;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands {

    // Does the language exist in the tree?
    private static String LanguageExists(HashMap<String, Language> tree, String language) {
        String result = null;

        for (String lang: tree.keySet()) {
            if (lang.equalsIgnoreCase(language.toLowerCase())) {
                result = lang;
                break;
            }
        }

        return result;
    }

    // Is the player currently online?
    private static String PlayerOnline(JavaPlugin plugin, String player) {
        String result = null;

        for (Player p: plugin.getServer().getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(player)) {
                result = p.getName();
                break;
            }
        }

        return result;
    }


    public static void Speak(JavaPlugin plugin, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender, String message) {
        String player = sender.getName();
        String MungedMessage;
        String header_known, header_unknown, outMunged, outUnmunged;
        boolean SkillPointGain = false;

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

                    // However, check to see if this language can be passively learned
                    if (tree.get(CurrentLanguage).Settings.PassiveLearning) {
                        SkillPointGain = pMgr.AddExposurePoint(p.getName(), CurrentLanguage, 1);

                        if (SkillPointGain)
                            p.sendMessage("" + ChatColor.WHITE + "You have gained a skill point in the " + ChatColor.BLUE + CurrentLanguage + ChatColor.WHITE + " language!");

                        if (pMgr.PlayerKnowsLanguage(p.getName(), CurrentLanguage))
                            p.sendMessage("" + ChatColor.WHITE + "You now know the " + ChatColor.BLUE + CurrentLanguage + ChatColor.WHITE + " language!");
                    }
                }
            }
        }
    }

    public static void Lang(JavaPlugin plugin, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender, String language) {
        boolean found = false;
        String player = sender.getName();
        String SelectedLanguage = "Invalid";

        // Find a language match
        for (String lang: tree.keySet()) {
            if (lang.equalsIgnoreCase(language.toLowerCase())) {
                found = true;
                SelectedLanguage = lang;
                break;
            }
        }

        if (!found) {
            sender.sendMessage("" + ChatColor.DARK_RED + "Unknown language.");
            return;
        }

        // Does the player know this language?
        if (pMgr.PlayerKnowsLanguage(player, SelectedLanguage)) {
            pMgr.SetPlayerCurrentLanguage(player, SelectedLanguage);
            sender.sendMessage("" + ChatColor.YELLOW + "Switched to " + SelectedLanguage);
        } else {
            sender.sendMessage("" + ChatColor.DARK_RED + "You are unable to speak " + SelectedLanguage);
        }
    }

    public static void TeachLang(JavaPlugin plugin, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender, String player, String language) {
        String TeachingPlayer = sender.getName();
        String TargetPlayer;
        String SelectedLanguage = "Invalid";
        String Request;

        // Find a language match
        if ((SelectedLanguage = LanguageExists(tree, language)) == null) {
            sender.sendMessage("" + ChatColor.DARK_RED + "Unknown language.");
            return;
        }

        // Is the teaching player actually able to teach the language?
        if (!pMgr.PlayerKnowsLanguage(TeachingPlayer, SelectedLanguage)) {
            sender.sendMessage("" + ChatColor.DARK_RED + "You cant teach a language you dont know!");
            return;
        }

        // Find a player match
        if ((TargetPlayer = PlayerOnline(plugin, player)) == null) {
            sender.sendMessage("" + ChatColor.DARK_RED + "Player not found.");
            return;
        }

        // Does the receiving player already know the language?
        if (pMgr.PlayerKnowsLanguage(TargetPlayer, SelectedLanguage)) {
            sender.sendMessage("" + ChatColor.DARK_RED + "That player already knows this language.");
            return;
        }

        // Looks good: notify target player of the teaching offer
        Request = "" + ChatColor.GREEN + TeachingPlayer + ChatColor.WHITE + " is offering you a ";
        Request += "" + ChatColor.DARK_BLUE + "skill point" + ChatColor.WHITE + " in " + ChatColor.BLUE + SelectedLanguage + ".";
        plugin.getServer().getPlayer(TargetPlayer).sendMessage(Request);
        Request = "" + ChatColor.WHITE + "To accept use " + ChatColor.YELLOW + "/acceptlang";
        plugin.getServer().getPlayer(TargetPlayer).sendMessage(Request);

        // Also inform the commanding player the request was sent.
        sender.sendMessage("" + ChatColor.YELLOW + "Request sent!");

        // Store who the offering player is in the target player's data object
        pMgr.SetOfferingPlayer(TargetPlayer, TeachingPlayer, SelectedLanguage);
    }

    // Intended for administrators
    public static void GiveLang(JavaPlugin plugin, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender, String player, String language) {
        boolean found = false;
        String SelectedLanguage = "Invalid";
        String TargetPlayer = "Invalid";
        Player pRef = null;

        // Find a language match
        for (String lang: tree.keySet()) {
            if (lang.equalsIgnoreCase(language.toLowerCase())) {
                found = true;
                SelectedLanguage = lang;
                break;
            }
        }

        if (!found) {
            sender.sendMessage("" + ChatColor.DARK_RED + "Unknown language.");
            return;
        }

        // re-use 'found' for player next
        found = false;

        // Check if the player is online
        for (Player p: plugin.getServer().getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(player)) {
                found = true;
                TargetPlayer = p.getName();
                pRef = p;
                break;
            }
        }

        if (!found) {
            // Player is not online, so lets hope the spelling/case usage was correct
            TargetPlayer = player;
        }

        if (sender.getName().contentEquals("CONSOLE"))
            plugin.getLogger().info("Giving " + TargetPlayer + " " + SelectedLanguage);
        else
            sender.sendMessage("" + ChatColor.YELLOW + "Giving " + TargetPlayer + " " + SelectedLanguage);

        pMgr.GivePlayerLanguage(TargetPlayer, SelectedLanguage);

        // Inform the player if they are online
        if (pRef != null)
            pRef.sendMessage("" + ChatColor.YELLOW + "You now understand the " + SelectedLanguage + " language!");
    }

    public static void LangInfo(JavaPlugin plugin, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender, String language) {
        boolean found = false;
        String SelectedLanguage = "Invalid";
        String player = sender.getName();
        String KnownLanguages = "";
        int SkillPoints = 0;
        int ReqSkillPoints = 0;
        String msg;

        // No language specified; general inquiry
        if (language == null) {
            // Tell the user about the languages known on the server
            for (String lang: tree.keySet()) {
                if (pMgr.PlayerKnowsLanguage(player, lang))
                    KnownLanguages += "" + ChatColor.BLUE + lang + " ";
                else
                    KnownLanguages += "" + ChatColor.DARK_RED + lang + " ";
            }
            KnownLanguages = KnownLanguages.trim();
            sender.sendMessage("Known languages: " + KnownLanguages);
        } else {
            // Inform the user about what they know about a specified language

            // Find a language match
            for (String lang: tree.keySet()) {
                if (lang.equalsIgnoreCase(language.toLowerCase())) {
                    found = true;
                    SelectedLanguage = lang;
                    break;
                }
            }

            if (!found) {
                sender.sendMessage("" + ChatColor.DARK_RED + "Unknown language.");
                return;
            }

            if (pMgr.PlayerKnowsLanguage(player, SelectedLanguage)) {
                sender.sendMessage("" + ChatColor.GREEN + "You are fluent in this language.");
            } else {
                SkillPoints = pMgr.GetLanguageSkillPoints(player, SelectedLanguage);
                if (SkillPoints == 0) {
                    sender.sendMessage("" + ChatColor.DARK_RED + "You do not understand this language.");
                } else {
                    ReqSkillPoints = pMgr.GetLanguageReqSkillPoints(player, SelectedLanguage);
                    msg = "" + ChatColor.YELLOW + "You have " + ChatColor.YELLOW + "" + new Integer(SkillPoints).toString();
                    msg += " of " + new Integer(ReqSkillPoints).toString() + " skill points needed for this language.";
                    sender.sendMessage(msg);
                }
            }

        }

    }

    public static void AcceptLang(JavaPlugin plugin, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender) {
        String ReceivingPlayer = sender.getName();
        String TeachingPlayer = null;
        String OfferedLanguage = null;

        long LastExchangeTeacher = 0;
        long LastExchangeReceiver = 0;
        long CurrentTime = (System.currentTimeMillis() / 1000l);
        long CooldownTime = 0;

        Player pReceiver;
        Player pTeacher;

        // First check to see if a /teachlang offer was made to the commanding player
        if ((TeachingPlayer = pMgr.GetOfferingPlayer(ReceivingPlayer)) == null) {
            sender.sendMessage("" + ChatColor.DARK_RED + "You have not been offered a skill point yet.");
            return;
        }

        // Make sure the teaching player is still online
        if (PlayerOnline(plugin, TeachingPlayer) == null) {
            sender.sendMessage("" + ChatColor.DARK_RED + "That player is not currently online.");
            return;
        }

        // Get the language that was offered
        OfferedLanguage = pMgr.GetOfferedLanguage(ReceivingPlayer);

        // Determine the last time both players made a skill point exchange
        LastExchangeTeacher = pMgr.GetLastExchangeTime(TeachingPlayer);
        LastExchangeReceiver = pMgr.GetLastExchangeTime(ReceivingPlayer);

        // Get the cooldown time for the language
        CooldownTime = tree.get(OfferedLanguage).Settings.ExchangeCoolDown;

        // Simplify some code...
        pReceiver = plugin.getServer().getPlayer(ReceivingPlayer);
        pTeacher = plugin.getServer().getPlayer(TeachingPlayer);

        // Make sure enough time has elapsed for both teacher and student
        if (LastExchangeReceiver + CooldownTime > CurrentTime) {
            pReceiver.sendMessage("" + ChatColor.DARK_RED + "It is too soon to receive skill point");
            pTeacher.sendMessage("" + ChatColor.DARK_RED + "It is too soon for that player to receive skill point");
            return;
        }
        if (LastExchangeTeacher + CooldownTime > CurrentTime) {
            pReceiver.sendMessage("" + ChatColor.DARK_RED + "It is too soon for that player to teach a skill point");
            pTeacher.sendMessage("" + ChatColor.DARK_RED + "It is too soon to teach another skill point");
            return;
        }

        // Grant the skill point
        pMgr.AddSkillPoint(ReceivingPlayer, OfferedLanguage, 1);

        // Update the timestamps
        pMgr.SetLastExchangeTime(TeachingPlayer, CurrentTime);
        pMgr.SetLastExchangeTime(ReceivingPlayer, CurrentTime);

        // Inform players of the successful exchange
        if (pMgr.PlayerKnowsLanguage(ReceivingPlayer, OfferedLanguage)) {
            // Only tell the receiving player he or she has fluency now; they might want to keep that information private
            pReceiver.sendMessage("" + ChatColor.WHITE + "You now know the " + ChatColor.BLUE + OfferedLanguage + ChatColor.WHITE + " language!");
        } else {
            pReceiver.sendMessage("" + ChatColor.WHITE + "You have received a skill point in " + ChatColor.BLUE + OfferedLanguage + ChatColor.WHITE + "!");
        }

        pTeacher.sendMessage("" + ChatColor.WHITE + "You have taught a skill point in " + ChatColor.BLUE + OfferedLanguage + ChatColor.WHITE + "!");

        // Finally, clear the otffering player
        pMgr.ClearOfferingPlayer(ReceivingPlayer);
    }
}

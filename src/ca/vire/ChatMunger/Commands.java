package ca.vire.ChatMunger;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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


    public static void Speak(JavaPlugin plugin, Settings LocalSettings, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender, String message) {
        String player = sender.getName();
        String MungedMessage;
        String header_known, header_unknown, outMunged, outUnmunged;
        String SpeakersWorld = sender.getServer().getPlayer(player).getLocation().getWorld().getName();
        String PlayersWorld;
        Location SpeakersLocation = sender.getServer().getPlayer(player).getLocation();
        boolean SkillPointGain = false;
        double distance;
        double SpeakingVolume;

        String CurrentLanguage = pMgr.GetPlayerCurrentLanguage(player);

        if (CurrentLanguage.equals("Common"))
            if (pMgr.NumLanguagesKnown(player) == 0) {
                sender.sendMessage(ChatColor.RED + "You don't know any other languages.");
            } else {
                sender.sendMessage(ChatColor.RED + "You haven't selected a language yet.");
            }

        else {
            MungedMessage = Munger.ProcessMessage(tree.get(CurrentLanguage).Vocab, message);

            header_known = player + ": ";
            header_known += "" + ChatColor.WHITE + ChatColor.ITALIC + "[";
            header_known += "" + ChatColor.BLUE + ChatColor.ITALIC + CurrentLanguage;
            header_known += "" + ChatColor.WHITE + ChatColor.ITALIC + "] ";

            header_unknown = player + ": ";
            header_unknown += "" + ChatColor.WHITE + ChatColor.ITALIC + "[";
            header_unknown += "" + ChatColor.RED + ChatColor.ITALIC + CurrentLanguage;
            header_unknown += "" + ChatColor.WHITE + ChatColor.ITALIC + "] ";

            outMunged = header_unknown + ChatColor.AQUA + MungedMessage;
            outUnmunged = header_known + ChatColor.AQUA + message;

            // Show the speaking player what the others who don't know the language will see
            sender.sendMessage(outMunged);

            // For each player
            for (Player p: plugin.getServer().getOnlinePlayers()) {
                // Ignore the speaking player
                if (p.getName().equals(player))
                    continue;

                // Ensure receiving player is in the same world as the speaking player
                if (!p.getLocation().getWorld().getName().equals(SpeakersWorld))
                    continue;

                // Distance check against current speaking volume
                distance = p.getLocation().distance(SpeakersLocation);
                SpeakingVolume = pMgr.GetSpeakingVolume(player);
                if (distance > SpeakingVolume)
                    continue;

                // If this player knows the language,
                if (pMgr.PlayerKnowsLanguage(p.getName(), CurrentLanguage)) {
                    // They see the unmunged message
                    p.sendMessage(outUnmunged);
                } else {
                    // Otherwise they're (typically) clueless to what was said.
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
            sender.sendMessage("" + ChatColor.RED + "Unknown language.");
            return;
        }

        // Does the player know this language?
        if (pMgr.PlayerKnowsLanguage(player, SelectedLanguage)) {
            pMgr.SetPlayerCurrentLanguage(player, SelectedLanguage);
            sender.sendMessage("" + ChatColor.YELLOW + "Switched to " + SelectedLanguage);
        } else {
            sender.sendMessage("" + ChatColor.RED + "You are unable to speak " + SelectedLanguage);
        }
    }

    public static void TeachLang(JavaPlugin plugin, Settings LocalSettings, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender, String player, String language) {
        String TeachingPlayer, TargetPlayer;
        String SelectedLanguage = "Invalid";
        String Request;
        String TeachersWorld, TargetsWorld;
        Player TeachingPlayerEntity, TargetPlayerEntity;
        Location TeachersLocation, TargetsLocation;
        long CurrentTime;
        long LastExchange, CooldownTime;
        double TeachingVolume, distance;

        // Find a language match
        if ((SelectedLanguage = LanguageExists(tree, language)) == null) {
            sender.sendMessage("" + ChatColor.RED + "Unknown language.");
            return;
        }

        // Is the teaching player actually able to teach the language?
        TeachingPlayer = sender.getName();
        if (!pMgr.PlayerKnowsLanguage(TeachingPlayer, SelectedLanguage)) {
            sender.sendMessage("" + ChatColor.RED + "You cant teach a language you dont know!");
            return;
        }

        // Get the cooldown time for the language
        CurrentTime = (System.currentTimeMillis() / 1000l);
        CooldownTime = tree.get(SelectedLanguage).Settings.ExchangeCoolDown;
        LastExchange = pMgr.GetLastExchangeTime(TeachingPlayer);
        if (LastExchange + CooldownTime > CurrentTime) {
            sender.sendMessage("" + ChatColor.RED + "It is too soon to teach another skill point");
            return;
        }

        // Find a player match
        if ((TargetPlayer = PlayerOnline(plugin, player)) == null) {
            sender.sendMessage("" + ChatColor.RED + "Player not found.");
            return;
        }

        // Ensure receiving player is in the same world as the speaking player
        TeachersWorld = sender.getServer().getPlayer(TeachingPlayer).getLocation().getWorld().getName();
        TargetsWorld = sender.getServer().getPlayer(TargetPlayer).getLocation().getWorld().getName();
        if (!TargetsWorld.equals(TeachersWorld)) {
            sender.sendMessage("" + ChatColor.RED + "That player is in a different world.");
            return;
        }

        // Teaching should take place within close proximity.
        TeachersLocation = sender.getServer().getPlayer(TargetPlayer).getLocation();
        TargetsLocation = sender.getServer().getPlayer(TeachingPlayer).getLocation();
        TeachingVolume = (double)LocalSettings.DistanceWhisper;
        distance = TeachersLocation.distance(TargetsLocation);
        if (distance > TeachingVolume) {
            sender.sendMessage("" + ChatColor.RED + "You are too far away from that player to teach.");
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
            sender.sendMessage("" + ChatColor.RED + "Unknown language.");
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

    // Intended for administrators
    public static void RemoveLang(JavaPlugin plugin, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender, String player, String language) {
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
            sender.sendMessage("" + ChatColor.RED + "Unknown language.");
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

        pMgr.RemovePlayerLanguage(TargetPlayer, SelectedLanguage);

        if (sender.getName().contentEquals("CONSOLE"))
            plugin.getLogger().info("Removing " + SelectedLanguage + " from " + TargetPlayer);
        else
            sender.sendMessage("" + ChatColor.YELLOW + "Removing " + SelectedLanguage + " from " + TargetPlayer);

        // Inform the player if they are online
        if (pRef != null)
            pRef.sendMessage("" + ChatColor.YELLOW + "Your understanding of " + SelectedLanguage + " has vanished.");
    }

    public static void LangInfo(JavaPlugin plugin, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender, String language) {
        boolean found = false;
        String SelectedLanguage = "Invalid";
        String player = sender.getName();
        String ServerLanguages = "";
        int SkillPoints = 0;
        int ReqSkillPoints = 0;
        String msg;

        // No language specified; general inquiry
        if (language == null) {
            // Tell the user about the languages known on the server
            for (String lang: tree.keySet()) {
                if (pMgr.PlayerKnowsLanguage(player, lang))
                    ServerLanguages += "" + ChatColor.BLUE + lang + " ";
                else
                    ServerLanguages += "" + ChatColor.RED + lang + " ";
            }
            ServerLanguages = ServerLanguages.trim();
            sender.sendMessage("Server languages: " + ServerLanguages);
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
                sender.sendMessage("" + ChatColor.RED + "Unknown language.");
                return;
            }

            if (pMgr.PlayerKnowsLanguage(player, SelectedLanguage)) {
                sender.sendMessage("" + ChatColor.GREEN + "You are fluent in this language.");
            } else {
                SkillPoints = pMgr.GetLanguageSkillPoints(player, SelectedLanguage);
                if (SkillPoints == 0) {
                    sender.sendMessage("" + ChatColor.RED + "You do not understand this language.");
                } else {
                    ReqSkillPoints = pMgr.GetLanguageReqSkillPoints(player, SelectedLanguage);
                    msg = "" + ChatColor.YELLOW + "You have " + ChatColor.YELLOW + "" + new Integer(SkillPoints).toString();
                    msg += " of " + new Integer(ReqSkillPoints).toString() + " skill points needed for this language.";
                    sender.sendMessage(msg);
                }
            }

        }

    }

    public static void AcceptLang(JavaPlugin plugin, Settings LocalSettings, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender) {
        String TeachingPlayer, TargetPlayer;
        String SelectedLanguage;
        String TeachersWorld, TargetsWorld;
        Player TeachingPlayerEntity, TargetPlayerEntity;
        Location TeachersLocation, TargetsLocation;
        long CurrentTime;
        long LastExchange, CooldownTime;
        double TeachingVolume, distance;

        // Check to see if a /teachlang offer was made to the commanding player
        TargetPlayer = sender.getName();
        TeachingPlayer = pMgr.GetOfferingPlayer(TargetPlayer);
        if (TeachingPlayer == null) {
            sender.sendMessage("" + ChatColor.RED + "You have not been offered a skill point.");
            return;
        }

        // Next get the language that was offered
        SelectedLanguage = pMgr.GetOfferedLanguage(TargetPlayer);

        // Check if the target player already knows this language
        if (pMgr.PlayerKnowsLanguage(TargetPlayer, SelectedLanguage)) {
            sender.sendMessage("" + ChatColor.RED + "You already know this language!");
            pMgr.ClearOfferingPlayer(TargetPlayer);
            return;
        }

        // Get the cool-down time for the language
        CurrentTime = (System.currentTimeMillis() / 1000l);
        CooldownTime = tree.get(SelectedLanguage).Settings.ExchangeCoolDown;
        LastExchange = pMgr.GetLastExchangeTime(TeachingPlayer);
        if (LastExchange + CooldownTime > CurrentTime) {
            sender.sendMessage("" + ChatColor.RED + "It is too soon to receive another skill point");
            return;
        }

        // Make sure the teaching player is still online
        if (PlayerOnline(plugin, TeachingPlayer) == null) {
            sender.sendMessage("" + ChatColor.RED + "That player does not appear to be online anymore.");
            return;
        }

        // Make sure they're both in the same world
        TeachersWorld = sender.getServer().getPlayer(TeachingPlayer).getLocation().getWorld().getName();
        TargetsWorld = sender.getServer().getPlayer(TargetPlayer).getLocation().getWorld().getName();
        if (!TargetsWorld.equals(TeachersWorld)) {
            sender.sendMessage("" + ChatColor.RED + "That player is in a different world now.");
            return;
        }

        // Teaching should take place within close proximity.
        TeachersLocation = sender.getServer().getPlayer(TeachingPlayer).getLocation();
        TargetsLocation = sender.getServer().getPlayer(TargetPlayer).getLocation();
        TeachingVolume = (double)LocalSettings.DistanceWhisper;
        distance = TeachersLocation.distance(TargetsLocation);
        if (distance > TeachingVolume) {
            sender.sendMessage("" + ChatColor.RED + "You are too far away from the teaching player.");
            return;
        }

        // Grant the skill point
        pMgr.AddSkillPoint(TargetPlayer, SelectedLanguage, 1);

        // Update the time stamps
        pMgr.SetLastExchangeTime(TeachingPlayer, CurrentTime);
        pMgr.SetLastExchangeTime(TargetPlayer, CurrentTime);

        // Inform players of the successful exchange
        TeachingPlayerEntity = sender.getServer().getPlayer(TeachingPlayer).getPlayer();
        TargetPlayerEntity = sender.getServer().getPlayer(TargetPlayer).getPlayer();
        if (pMgr.PlayerKnowsLanguage(TargetPlayer, SelectedLanguage)) {
            // Only tell the target player he or she has fluency now; they might wish to keep that information private
            TargetPlayerEntity.sendMessage("" + ChatColor.WHITE + "You now know the " + ChatColor.BLUE + SelectedLanguage + ChatColor.WHITE + " language!");
        } else {
            TargetPlayerEntity.sendMessage("" + ChatColor.WHITE + "You have received a skill point in " + ChatColor.BLUE + SelectedLanguage + ChatColor.WHITE + "!");
        }

        TeachingPlayerEntity.sendMessage("" + ChatColor.WHITE + "You have taught a skill point in " + ChatColor.BLUE + SelectedLanguage + ChatColor.WHITE + "!");

        // Finally, clear the offering player
        pMgr.ClearOfferingPlayer(TargetPlayer);
    }

    public static void LangMenu(JavaPlugin plugin, Settings LocalSettings, PlayerManager pMgr, HashMap<String, Language> tree, CommandSender sender) {

    }
}

package ca.vire.ChatMunger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

//This object keeps track of the player data (MungerPlayer objects) for each player
public class PlayerManager {

    // Mapping between player on server and his/her own plugin data
    private HashMap<String, MungerPlayer> PlayerMap;

    // Back reference to plugin
    JavaPlugin plugin;

    // Plugin settings
    Settings LocalSettings;

    public PlayerManager(Plugin pluginref, Settings settings) {
        this.PlayerMap = new HashMap<String, MungerPlayer>();
        this.plugin = pluginref;
        this.LocalSettings = settings;
    }

    // Do we have existing data on the player in memory?
    public boolean PlayerExists(String player) {
        return PlayerMap.containsKey(player);
    }

    // Initializes language properties for player in PlayerMap object
    // Returns false if language properties already exist
    public boolean InitLanguage(String player, String language) {
        int CurrentSkillPoints, RequiredSkillPoints, CurrentExposures, RequiredExposures;
        boolean PassiveLearning;
        String LangConfigUrl = plugin.getDataFolder().getAbsolutePath() + "/" + language + "/config.yml";
        FileConfiguration LanguageData;

        // Load / create player data
        if (!PlayerExists(player))
            LoadPlayerData(player);

        if (PlayerMap.get(player).LangKnowledge.containsKey(language))
            // Bail if the Player exists and has existing properties about that language
            return false;

        // Dig up the language settings from the plugin data folder
        LanguageData = new YamlConfiguration().loadConfiguration(new File(LangConfigUrl));
        RequiredSkillPoints = LanguageData.getInt("RequiredSkillPoints");
        RequiredExposures = LanguageData.getInt("PassiveExposuresRequired");
        PassiveLearning = LanguageData.getBoolean("PassiveLearning");

        // Start with a clean slate
        CurrentSkillPoints = 0;
        CurrentExposures = 0;

        PlayerMap.get(player).LangKnowledge.put(language, new LanguageProperties(CurrentSkillPoints, RequiredSkillPoints, CurrentExposures, RequiredExposures, PassiveLearning));

        return true;
    }

    // If the player only partially knows the language, they'll now be fluent.
    public void GivePlayerLanguage(String player, String language) {
        int CurrentSkillPoints, RequiredSkillPoints, CurrentExposures, RequiredExposures;
        boolean PassiveLearning;

        // Load / create player data
        if (!PlayerExists(player))
            LoadPlayerData(player);

        // Make sure the player has a language properties object
        InitLanguage(player, language);

        // Max out the skill points needed
        RequiredSkillPoints = GetLanguageReqSkillPoints(player, language);
        CurrentSkillPoints = RequiredSkillPoints;
        CurrentExposures = 0;
        RequiredExposures = GetLanguageReqExposures(player, language);
        PassiveLearning = GetPassiveLanguageLearning(player, language);

        // Grant the player the language
        PlayerMap.get(player).LangKnowledge.put(language, new LanguageProperties(CurrentSkillPoints, RequiredSkillPoints, CurrentExposures, RequiredExposures, PassiveLearning));
    }

    // Returns true if language removed, false if language never existed.
    public boolean RemovePlayerLanguage(String player, String language) {
        // Load / create player data
        if (!PlayerExists(player))
            LoadPlayerData(player);

        // Check if player has any skill points in the language
        if (GetLanguageSkillPoints(player, language) == 0)
            return false; // player is effectively ignorant of language; bail

        // Is either fluent or has some knowledge: set skill points to zero
        PlayerMap.get(player).LangKnowledge.get(language).CurrentSkillPoints = 0;

        return true;
    }

    // Loads player language information from plugin data folder
    // We're here because player data doesn't exist in memory, but might on disk
    private boolean LoadPlayerData(String player) {
        boolean result = false;
        String PlayerDataUrl = plugin.getDataFolder().getAbsolutePath() + "/Players/" + player + ".yml";
        String DataURL = plugin.getDataFolder().getAbsolutePath();
        File PlayerFile = new File(PlayerDataUrl);
        FileConfiguration PlayerData, LanguageData;
        Set<String> LangList;
        int LangCount, LastExchange, CurrentSkillPoints, RequiredSkillPoints, CurrentExposures, RequiredExposures;
        boolean PassiveLearning;

        plugin.getLogger().info("Loading data for player " + player + " from disk...");

        if (!PlayerMap.containsKey(player)) {
            // Create new player data object in memory
            PlayerMap.put(player, new MungerPlayer());
            PlayerMap.get(player).Volume = LocalSettings.DistanceSpeak; // Set default speaking volume
        }

        if (PlayerFile.exists()) {
            // Player data exists on disk; load it in
            PlayerData = new YamlConfiguration().loadConfiguration(new File(PlayerDataUrl));

            // Load in data
            PlayerMap.get(player).CurrentLanguage = PlayerData.getString("CurrentLanguage");
            PlayerMap.get(player).LastExchange = PlayerData.getInt("LastExchange");

            // Check if the player knows any languages at all
            if (PlayerData.isConfigurationSection("Languages")) {
                // Languages player knows
                LangList = PlayerData.getConfigurationSection("Languages").getKeys(false);
                LangCount = LangList.size();

                plugin.getLogger().info(player + " knows " + new Integer(LangCount).toString() + " languages.");

                for (String lang: LangList) {
                    CurrentSkillPoints = PlayerData.getInt("Languages." + lang + ".CurrentSkillPoints");
                    RequiredSkillPoints = PlayerData.getInt("Languages." + lang + ".RequiredSkillPoints");
                    CurrentExposures = PlayerData.getInt("Languages." + lang + ".CurrentExposures");
                    RequiredExposures = PlayerData.getInt("Languages." + lang + ".RequiredExposures");
                    PassiveLearning = PlayerData.getBoolean("Languages." + lang + ".PassiveLearning");

                    plugin.getLogger().info(player + " has " + new Integer(CurrentExposures).toString() + " of " + new Integer(RequiredExposures).toString() + " exposure points in " + lang);
                    plugin.getLogger().info(player + " has " + new Integer(CurrentSkillPoints).toString() + " of " + new Integer(RequiredSkillPoints).toString() + " points in " + lang);

                    PlayerMap.get(player).LangKnowledge.put(lang, new LanguageProperties(CurrentSkillPoints, RequiredSkillPoints, CurrentExposures, RequiredExposures, PassiveLearning));
                }
            }

            // Indicate existing data was found to the caller
            result = true;
        } else {
            // Set some safe defaults as needed
        }

        return result;
    }

    // Update existing / create new player data files
    public void SavePlayerData() {
        String url;
        File PlayerFile;
        FileConfiguration yml;

        // For each player in memory
        for (String player: PlayerMap.keySet()) {
            // Initialize yml object
            url = plugin.getDataFolder().getAbsolutePath() + "/Players/" + player + ".yml";
            yml = new YamlConfiguration();

            // Assign data elements
            yml.set("CurrentLanguage", PlayerMap.get(player).CurrentLanguage);
            yml.set("LastExchange", PlayerMap.get(player).LastExchange);

            // Record data for each language the player knows
            for (String lang: PlayerMap.get(player).LangKnowledge.keySet()) {
                yml.set("Languages." + lang + ".CurrentSkillPoints", PlayerMap.get(player).LangKnowledge.get(lang).CurrentSkillPoints);
                yml.set("Languages." + lang + ".RequiredSkillPoints", PlayerMap.get(player).LangKnowledge.get(lang).RequiredSkillPoints);
                yml.set("Languages." + lang + ".CurrentExposures", PlayerMap.get(player).LangKnowledge.get(lang).CurrentExposures);
                yml.set("Languages." + lang + ".RequiredExposures", PlayerMap.get(player).LangKnowledge.get(lang).RequiredExposures);
                yml.set("Languages." + lang + ".PassiveLearning", PlayerMap.get(player).LangKnowledge.get(lang).PassiveLearning);
            }

            // Write the file
            try {
                PlayerFile = new File(url);
                yml.save(PlayerFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String GetPlayerCurrentLanguage(String player) {
        String language = "Invalid"; // Should never get this

        if (PlayerExists(player)) {
            // See if there is existing data on the player
            language = PlayerMap.get(player).CurrentLanguage;
        } else {
            LoadPlayerData(player);
            language = PlayerMap.get(player).CurrentLanguage;
        }

        return language;
    }

    // Assumes both player and language exist
    public void SetPlayerCurrentLanguage(String player, String language) {
        PlayerMap.get(player).CurrentLanguage = language;
    }

    public double GetSpeakingVolume(String player)
    {
        return PlayerMap.get(player).Volume;
    }
    public boolean PlayerKnowsLanguage(String player, String language) {

        // Load / create player data if not already in memory
        if (!PlayerExists(player))
            LoadPlayerData(player);

        return PlayerMap.get(player).IsFluentIn(language);
    }

    public int NumLanguagesKnown(String player) {
        int count = 0;

        if (PlayerExists(player)) {
            count = PlayerMap.get(player).GetFluentLanguageCount();
        }

        return count;
    }

    public int GetLanguageSkillPoints(String player, String language) {
        int SkillPoints = 0;

        if (PlayerMap.get(player).LangKnowledge.containsKey(language))
            SkillPoints = PlayerMap.get(player).LangKnowledge.get(language).CurrentSkillPoints;

        return SkillPoints;
    }

    public int GetLanguageReqSkillPoints(String player, String language) {
        int SkillPoints = 0;

        if (PlayerMap.get(player).LangKnowledge.containsKey(language))
            SkillPoints = PlayerMap.get(player).LangKnowledge.get(language).RequiredSkillPoints;

        return SkillPoints;
    }

    public int GetLanguageExposures(String player, String language) {
        int exposures = 0;

        if (PlayerMap.get(player).LangKnowledge.containsKey(language))
            exposures = PlayerMap.get(player).LangKnowledge.get(language).CurrentExposures;

        return exposures;
    }

    public int GetLanguageReqExposures(String player, String language) {
        int exposures = 0;

        if (PlayerMap.get(player).LangKnowledge.containsKey(language))
            exposures = PlayerMap.get(player).LangKnowledge.get(language).RequiredExposures;

        return exposures;
    }

    public boolean GetPassiveLanguageLearning(String player, String language) {
        boolean result = false;

        if (PlayerMap.get(player).LangKnowledge.containsKey(language))
            result = PlayerMap.get(player).LangKnowledge.get(language).PassiveLearning;

        return result;
    }

    public boolean SetOfferingPlayer(String ReceivingPlayer, String TeachingPlayer, String Language) {
        boolean result = false;

        if (PlayerMap.containsKey(ReceivingPlayer)) {
            PlayerMap.get(ReceivingPlayer).OfferingPlayer = TeachingPlayer;
            PlayerMap.get(ReceivingPlayer).OfferedLanguage = Language;
            result = true;
        }

        return result;
    }

    public void ClearOfferingPlayer(String ReceivingPlayer) {
        if (PlayerMap.containsKey(ReceivingPlayer)) {
            PlayerMap.get(ReceivingPlayer).OfferingPlayer = null;
        }
    }

    public String GetOfferingPlayer(String ReceivingPlayer) {
        String result = null;

        if (PlayerMap.containsKey(ReceivingPlayer)) {
            result = PlayerMap.get(ReceivingPlayer).OfferingPlayer;
        }

        return result;
    }

    public String GetOfferedLanguage(String ReceivingPlayer) {
        String result = null;

        if (PlayerMap.containsKey(ReceivingPlayer)) {
            result = PlayerMap.get(ReceivingPlayer).OfferedLanguage;
        }
        return result;
    }


    // Returns the time stamp when the player last used /teachlang or /acceptlang
    public long GetLastExchangeTime(String player) {
        long result = 0;

        if (PlayerMap.containsKey(player)) {
            result = PlayerMap.get(player).LastExchange;
        }

        return result;
    }

    // sets the LastExchange
    public void SetLastExchangeTime(String player, long timestamp) {
        int result = 0;

        if (PlayerMap.containsKey(player)) {
            PlayerMap.get(player).LastExchange = timestamp;
        }
    }

    public boolean AddSkillPoint(String player, String language, int point) {
        boolean result = false;
        String LangConfigUrl = plugin.getDataFolder().getAbsolutePath() + "/" + language + "/config.yml";
        FileConfiguration LanguageData = new YamlConfiguration().loadConfiguration(new File(LangConfigUrl));

        // Make sure language properties exist
        InitLanguage(player, language);
        PlayerMap.get(player).LangKnowledge.get(language).AddPoint(point);

        return result;
    }

    public boolean AddExposurePoint(String player, String language, int point) {
        boolean result = false;

        // Make sure language properties exist
        InitLanguage(player, language);
        result = PlayerMap.get(player).LangKnowledge.get(language).AddExposure(point);

        return result;
    }
}

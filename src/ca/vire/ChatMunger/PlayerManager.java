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

    public PlayerManager(JavaPlugin plugin) {
        PlayerMap = new HashMap<String, MungerPlayer>();
        this.plugin = plugin;
    }

    // Do we have existing data on the player in memory?
    public boolean PlayerExists(String player) {
        return PlayerMap.containsKey(player);
    }

    // Assumes the language exists
    // If the player only partially knows the language, they'll now be fluent.
    public void GivePlayerLanguage(String player, String language) {
        int CurrentSkillPoints, RequiredSkillPoints, CurrentExposures, RequiredExposures;
        boolean PassiveLearning;
        String LangConfigUrl = plugin.getDataFolder().getAbsolutePath() + "/" + language + "/config.yml";
        FileConfiguration LanguageData;

        // Load / create player data
        if (!PlayerExists(player))
            LoadPlayerData(player);

        // Dig up the language settings from the plugin data folder
        LanguageData = new YamlConfiguration().loadConfiguration(new File(LangConfigUrl));
        RequiredSkillPoints = LanguageData.getInt("RequiredSkillPoints");
        RequiredExposures = LanguageData.getInt("RequiredExposures");
        PassiveLearning = LanguageData.getBoolean("PassiveLearning");
        CurrentSkillPoints = RequiredSkillPoints;
        CurrentExposures = 0;

        // Grant the player the language
        PlayerMap.get(player).LangKnowledge.put(language, new LanguageProperties(CurrentSkillPoints, RequiredSkillPoints, CurrentExposures, RequiredExposures, PassiveLearning));
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

        // Create new player data object in memory
        PlayerMap.put(player, new MungerPlayer());

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

    // Assumes both player and language exist
    public boolean AddSkillPoint(String player, String language, int point) {
        boolean result = false;

        PlayerMap.get(player).LangKnowledge.get(language).AddPoint(point);

        return result;
    }
}

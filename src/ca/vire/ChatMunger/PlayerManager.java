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
    private boolean PlayerExists(String player) {
        return PlayerMap.containsKey(player);
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
        int LangCount, LastExchange, CurrentSkillPoints, RequiredSkillPoints;

        // Create new player data object in memory
        PlayerMap.put(player, new MungerPlayer());

        if (PlayerFile.exists()) {
            // Player data exists on disk; load it in
            PlayerData = new YamlConfiguration().loadConfiguration(new File(PlayerDataUrl));

            // Load in data
            PlayerMap.get(player).CurrentLanguage = PlayerData.getString("CurrentLanguage");
            PlayerMap.get(player).LastExchange = PlayerData.getInt("LastExchange");

            // Languages player knows
            LangList = PlayerData.getConfigurationSection("Languages").getKeys(false);
            LangCount = LangList.size();

            plugin.getLogger().info(player + " knows " + new Integer(LangCount).toString() + " languages.");

            for (String lang: LangList) {
                CurrentSkillPoints = PlayerData.getInt("Languages." + lang + ".CurrentSkillPoints");
                RequiredSkillPoints = PlayerData.getInt("Languages." + lang + ".RequiredSkillPoints");

                plugin.getLogger().info(player + " has " + new Integer(CurrentSkillPoints).toString() + " of " + new Integer(RequiredSkillPoints).toString() + " points in " + lang);

                PlayerMap.get(player).LangKnowledge.put(lang, new LanguageProperties(CurrentSkillPoints, RequiredSkillPoints));
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
        int a;

        return PlayerMap.get(player).LangKnowledge.get(language).CurrentSkillPoints;
    }

}

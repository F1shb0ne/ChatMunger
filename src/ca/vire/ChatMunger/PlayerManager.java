package ca.vire.ChatMunger;

import java.util.HashMap;
import org.bukkit.plugin.java.JavaPlugin;

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
    private boolean LoadPlayerData(String player) {
        boolean result = false;

        return result;
    }

    public boolean PlayerKnowsLanguage(String player, String language) {
        boolean result = false;        


        return result;
    }

}

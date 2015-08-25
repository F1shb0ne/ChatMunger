package ca.vire.ChatMunger;

import java.io.File;
import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;

public class LanguageManager {

	// Returns an array list containing the names of each language used by the plugin.
	public static ArrayList<String> GetLanguageList(JavaPlugin plugin) {
        ArrayList<String> list = new ArrayList<String>();
        File file;
        String path = plugin.getDataFolder().getAbsolutePath();
        String url;

        // Get a list of all the sub-folders within the plugin data folder. 
        for (String entry: plugin.getDataFolder().list()) {
            url = path + "/" + entry;
        	file = new File(url);
            
            if (file.isDirectory()) {
                // Check if the required files config.yml, words.txt and dictionary.txt exist.
            	if ((new File(url + "/config.yml")).exists() && (new File(url + "/words.txt")).exists() && (new File(url + "/dictionary.txt")).exists()) {
                    // This constitutes as a language the plugin can use.
            		list.add(url);
            	}
            }
        }

        return list;
	}
	
}

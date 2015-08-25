package ca.vire.ChatMunger;

import java.io.File;
import java.util.HashMap;
import org.bukkit.plugin.java.JavaPlugin;

public class LanguageManager {

	// Returns a hash map of all the available languages (vocabularies)
	public static HashMap<String, Vocabulary> LoadLanguageTree(JavaPlugin plugin) {
        HashMap<String, Vocabulary>tree = new HashMap<String, Vocabulary>();
        File file;
        String path = plugin.getDataFolder().getAbsolutePath();
        String url;
        String langlist = "";

        // Get a list of all the sub-folders within the plugin data folder. 
        for (String entry: plugin.getDataFolder().list()) {
            url = path + "/" + entry;
        	file = new File(url);
            
            if (file.isDirectory() && !entry.equals("ExampleLang")) {
                // Check if the required files config.yml, words.txt and dictionary.txt exist.
            	if ((new File(url + "/config.yml")).exists() && (new File(url + "/words.txt")).exists() && (new File(url + "/dictionary.txt")).exists()) {
                    // This constitutes as a language the plugin can use.
                    tree.put(entry, new Vocabulary(url));
                    langlist += entry + " ";
            	}
            }
        }

        plugin.getLogger().info("Languages: " + langlist);

        return tree;
	}

}

package ca.vire.ChatMunger;

import java.io.File;
import java.util.HashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LanguageManager {

    // Returns a hash map of all the available languages
    public static HashMap<String, Language> LoadLanguageTree(JavaPlugin plugin) {
        HashMap<String, Language>tree = new HashMap<String, Language>();
        File file;
        String path = plugin.getDataFolder().getAbsolutePath();
        String url;
        String langlist = "";
        String info;
        FileConfiguration yml;

        Vocabulary vocab;
        LanguageSettings settings;

        // Get a list of all the sub-folders within the plugin data folder.
        for (String entry: plugin.getDataFolder().list()) {
            url = path + "/" + entry;
            file = new File(url);

            if (file.isDirectory() && !entry.equals("ExampleLang")) {
                // Check if the required files config.yml, words.txt and dictionary.txt exist.
                if ((new File(url + "/config.yml")).exists() && (new File(url + "/words.txt")).exists() && (new File(url + "/dictionary.txt")).exists()) {
                    // This constitutes as a language the plugin can use.

                    // Load the vocabulary
                    vocab = new Vocabulary(url);

                    // Then get the language settings
                    yml = YamlConfiguration.loadConfiguration(new File(url + "/config.yml"));
                    settings = new LanguageSettings(yml.getInt("RequiredSkillPoints"), (long)yml.getInt("ExchangeCooldown"));

                    // Insert the language into the tree
                    tree.put(entry, new Language(vocab, settings));
                    langlist += entry + " ";

                    // Give some statistics
                    info = entry + ": " + new Integer(vocab.GetDictionarySize()) + " words, ";
                    info += "" + new Integer(vocab.GetWordmapSize()) + " word mappings.";
                    plugin.getLogger().info(info);
                }
            }
        }

        return tree;
    }

    public static boolean LanguageExists(HashMap<String, Language> tree, String language) {
        boolean result = false;

        if (tree.containsKey(language))
            result = true;

        return result;
    }

    // Assumes the language exists
    public static int GetRequiredSkillPoints(HashMap<String, Language> tree, String language) {
        return tree.get(language).Settings.RequiredSkillPoints;
    }

}

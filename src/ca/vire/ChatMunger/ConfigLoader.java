package ca.vire.ChatMunger;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigLoader {

    // Returns a new Settings object
    public static Settings LoadConfig(JavaPlugin plugin) {

        Settings settings = new Settings();
        String DataFolder = plugin.getDataFolder().toString();

        // First determine if plugin data folder exists
        if (DataFolderExists(plugin)) {

            settings.Enable = plugin.getConfig().getBoolean("enable");
            if (!settings.Enable)
                plugin.getLogger().info("The plugin is NOT enabled.");

        } else {
            CreateSkeletonDataFolder(plugin);
        }

        return settings;
    }

    public static boolean DataFolderExists(JavaPlugin plugin) {
        if (plugin.getDataFolder().exists()) {
            return true;
        }
        return false;
    }

    public static boolean CreateSkeletonDataFolder(JavaPlugin plugin) {
        String PluginRootPath = plugin.getDataFolder().getAbsolutePath();
        File PluginRootFolder = new File(PluginRootPath);
        File PlayersFolder = new File(PluginRootPath + "/Players");
        File ExampleLangFolder = new File(PluginRootPath + "/ExampleLang");
        File ExampleLangWords = new File(PluginRootPath + "/ExampleLang/words.txt");
        File ExampleLangDictionary = new File(PluginRootPath + "/ExampleLang/dictionary.txt");
        File ExampleLangConfig = new File(PluginRootPath + "/ExampleLang/config.yml");
        BufferedWriter writer;
        String ExampleWordsContents = "";
        String ExampleDictionaryContents = "";
        String ExampleConfigContents = "";

        ExampleConfigContents += "# Example config\n";
        ExampleConfigContents += "# See Documentation.txt for more details at: https://github.com/F1shb0ne/ChatMunger\n";
        ExampleConfigContents += "\n";
        ExampleConfigContents += "Enabled: true\n";
        ExampleConfigContents += "\n";
        ExampleConfigContents += "# Number of skill points required to learn this language\n";
        ExampleConfigContents += "SkillPointsRequired: 8\n";
        ExampleConfigContents += "\n";
        ExampleConfigContents += "# Minimum amount of time (in seconds) that must elapse before another skill may be taught/learned.\n";
        ExampleConfigContents += "SkillPointCooldown: 86400\n";

        ExampleWordsContents += "# Example words file.\n";
        ExampleWordsContents += "# See Documentation.txt for more details at: https://github.com/F1shb0ne/ChatMunger\n";
        ExampleWordsContents += "\n";
        ExampleWordsContents += "a\n";
        ExampleWordsContents += "i\n";
        ExampleWordsContents += "ba\n";
        ExampleWordsContents += "mi\n";
        ExampleWordsContents += "ko\n";
        ExampleWordsContents += "bop\n";
        ExampleWordsContents += "blah\n";
        ExampleWordsContents += "blarg\n";

        ExampleDictionaryContents += "# Example dictionary file.\n";
        ExampleDictionaryContents += "# See Documentation.txt for more details at: https://github.com/F1shb0ne/ChatMunger\n";
        ExampleDictionaryContents += "\n";
        ExampleDictionaryContents += "hello bloop\n";
        ExampleDictionaryContents += "goodbye bleep\n";

        // Create the ChatMunger data folder
        plugin.getLogger().info("Creating data folder skeleton...");
        PluginRootFolder.mkdir();

        // Create the Players folder
        PlayersFolder.mkdir();

        // Copy over the default config
        plugin.saveDefaultConfig();

        // Create the dummy (example) language folder
        ExampleLangFolder.mkdir();

        // Create the example files
        try {
            writer = new BufferedWriter(new FileWriter(ExampleLangConfig));
            writer.write(ExampleConfigContents);
            writer.close();

            writer = new BufferedWriter(new FileWriter(ExampleLangWords));
            writer.write(ExampleWordsContents);
            writer.close();

            writer = new BufferedWriter(new FileWriter(ExampleLangDictionary));
            writer.write(ExampleDictionaryContents);
            writer.close();

        } catch (IOException e) {
            plugin.getLogger().info("IO error creating files at " + PluginRootPath);
            return false;
        }

        return true;
    }

    public static ArrayList<String> GetDirectoryList(JavaPlugin plugin) {
        ArrayList<String> list = new ArrayList<String>();

        for (String entry: plugin.getDataFolder().list()) {
            list.add(entry);
            plugin.getLogger().info("Found " + entry);

        }

        return list;
    }

}

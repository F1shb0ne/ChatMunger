package ca.vire.ChatMunger;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class WordlistManager {

    public static Map<String, String> ReadWordmap(String WordmapFile) {
        Map<String, String> wordmap = new HashMap<String, String>();
        String input;
        String[] tokens;
        int length;

        BufferedReader Reader = null;

        try {
            Reader = new BufferedReader(new FileReader(WordmapFile));

            while (true) {
                // Read in the next word (skipping empty lines)
                input = Reader.readLine();

                // read input until EOF (get null) is reached
                if (input == null) {
                    break;
                } else {
                    length = input.length();
                    // A word map can't be fewer than 3 characters
                    if (length >= 3) {
                        tokens = input.split(" ");
                        if (tokens[0] != null && tokens[1] != null) {
                            // Insert word association into word map.
                            wordmap.put(tokens[0], tokens[1]);
                        }
                    }
                }
            }

            Reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return wordmap;
    }

    public static boolean WriteWordmap(Map<String, String> wordmap, String WordmapFile) {

        BufferedWriter Writer = null;

        try {
            Writer = new BufferedWriter(new FileWriter(WordmapFile));

            for (String key: wordmap.keySet()) {
                Writer.write(key + " " + wordmap.get(key) + "\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            Writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean WriteWordlist(Map<Integer, ArrayList<String>> dict, String WordlistFile) {
        int i;

        BufferedWriter Writer = null;

        try {
            Writer = new BufferedWriter(new FileWriter(WordlistFile));

            if (Writer != null) {
                for (int key: dict.keySet()) {
                    for (i = 0; i < dict.get(key).size(); ++i) {
                        Writer.write(dict.get(key).get(i) + "\n");
                    }
                }
            }

            Writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Map<Integer, ArrayList<String>> ReadWordlist(String WordlistFile) {
        Map<Integer, ArrayList<String>> dict = new HashMap<Integer, ArrayList<String>>();
        BufferedReader Reader = null;
        String input, word;
        int length;

        try {
            Reader = new BufferedReader(new FileReader(WordlistFile));

            while (true) {
                // Read in the next word (skipping empty lines)
                input = Reader.readLine();

                // read input until EOF (get null) is reached
                if (input == null) {
                    break;
                } else {
                    length = input.length();
                    if (length > 0) {
                        // Found word
                        word = input;

                        // Insert into dictionary, organized by word length.
                        if (!dict.containsKey(length))
                            dict.put(length, new ArrayList<String>());

                        (dict.get(length)).add(word);
                    }
                }
            }

            Reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return dict;
    }

}

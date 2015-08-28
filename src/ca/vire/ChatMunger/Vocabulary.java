package ca.vire.ChatMunger;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;


public class Vocabulary {

    private Map<Integer, ArrayList<String>> Dictionary;
    private Map<String, String> Wordmap;
    private String DictionaryFile, WordmapFile;

    public Vocabulary(String language_url) {
        DictionaryFile = language_url + "/words.txt";
        WordmapFile = language_url + "/dictionary.txt";
        Dictionary = WordlistManager.ReadWordlist(DictionaryFile);
        Wordmap = WordlistManager.ReadWordmap(WordmapFile);
    }

    public boolean SaveToDisk() {
        // Return false should one or both operations fail
        return WordlistManager.WriteWordlist(Dictionary, DictionaryFile) & WordlistManager.WriteWordmap(Wordmap, WordmapFile);
    }

    public boolean AddWord(String word) {
        boolean result = false;
        int length;

        length = word.length();

        if (WordExists(word))
            return false;

        // Insert into dictionary, organized by word length.
        if (!Dictionary.containsKey(length))
            Dictionary.put(length, new ArrayList<String>());

        (Dictionary.get(length)).add(word.toLowerCase());
        result = true;

        return result;
    }

    public boolean DelWord(String word) {
        ArrayList<String> removal = new ArrayList<String>();
        String word_lc = word.toLowerCase();


        removal.add(word_lc);
        if (!WordExists(word_lc))
            return false;
        else {
            Dictionary.get(word.length()).removeAll(removal);
        }

        return true;
    }

    public boolean AddWordmap(String OriginalWord, String NewWord) {
        if (Wordmap.containsKey(OriginalWord.toLowerCase()))
            return false;
        else
            Wordmap.put(OriginalWord.toLowerCase(), NewWord.toLowerCase());

        return true;
    }

    public boolean DelWordmap(String OriginalWord) {
        String OriginalWord_lc = OriginalWord.toLowerCase();

        if (Wordmap.containsKey(OriginalWord_lc))
            Wordmap.remove(OriginalWord_lc);
        else
            return false;

        return true;
    }

    public boolean MappedWordExists(String word) {
        if (Wordmap.containsKey(word.toLowerCase()))
            return true;

        return false;
    }

    public boolean WordExists(String word) {
        String word_lc = word.toLowerCase();
        int length = word_lc.length();

        if (Dictionary.containsKey(length)) {
            for (String element: Dictionary.get(length)) {
                if (element.equals(word_lc)) {
                    return true;
                }
            }
        } else {
            return false;
        }

        return false;
    }

    public int GetDictionarySize() {
        int result = 0;

        // Iterate and sum lengths of all word size categories.
        for (int key: Dictionary.keySet()) {
            result += Dictionary.get(key).size();
        }

        return result;
    }

    public int GetWordmapSize() {
        return Wordmap.size();
    }

    public String GetRandomWord(int length) {

        String result = null;

        // First determine if a list of the requested word length exists.
        if (Dictionary.containsKey(length)) {
            // Randomize that list and return a word.
            Collections.shuffle(Dictionary.get(length));
            result = Dictionary.get(length).get(0);
        } else {
            // If not, continue searching the next size down
            if (length > 1) {
                return GetRandomWord(length - 1);
            } else {
                // Shouldn't get here; the dictionary should at least have one entry.
                return null;
            }
        }

        return result;
    }

    public String GetMappedWord(String word) {
        String result = null;

        if (Wordmap.containsKey(word.toLowerCase()))
            result = Wordmap.get(word.toLowerCase());

        return result;
    }

}

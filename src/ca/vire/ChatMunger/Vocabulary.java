package ca.vire.ChatMunger;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import ca.vire.ChatMunger.WordlistManager;


public class Vocabulary {

	private Map<Integer, ArrayList<String>> Dictionary;
	private Map<String, String> Wordmap;
	private String Language, DictionaryFile, WordmapFile;

	public Vocabulary(String langauge) {
		Language = langauge;
		DictionaryFile = Language + "-dict.txt";
		WordmapFile = Language + "-wordmap.txt";
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

		(Dictionary.get(length)).add(word);
		result = true;

		return result;
	}

	public boolean DelWord(String word) {
		ArrayList<String> removal = new ArrayList<String>();

		removal.add(word);
		if (!WordExists(word))
			return false;
		else {
			Dictionary.get(word.length()).removeAll(removal);
		}

		return true;
	}

	public boolean AddWordmap(String OriginalWord, String NewWord) {
		if (Wordmap.containsKey(OriginalWord))
			return false;
		else
			Wordmap.put(OriginalWord, NewWord);

		return true;
	}

	public boolean DelWordmap(String OriginalWord) {
		if (Wordmap.containsKey(OriginalWord))
			Wordmap.remove(OriginalWord);
		else
			return false;

		return true;
	}

	public boolean WordExists(String word) {
		int length = word.length();

		if (Dictionary.containsKey(length)) {
			for (String element: Dictionary.get(word.length())) {
				if (element.equals(word)) {
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

		if (Wordmap.containsKey(word))
			result = Wordmap.get(word);

		return result;
	}

}

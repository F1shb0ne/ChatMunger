package ca.vire.ChatMunger;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import ca.vire.ChatMunger.WordlistManager;
import java.io.IOException;


public class Vocabulary {

	private Map<Integer, ArrayList<String>> Dictionary;
	private String Language, DictionaryFile, AssociationFile;
	
	public Vocabulary(String langauge) {
		Language = langauge;
		String DictionaryFile = Language + "-dict.txt";
		String AssociationFile = Language + "-assoc.txt";
		Dictionary = WordlistManager.ReadWordlist(DictionaryFile);
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
	
}

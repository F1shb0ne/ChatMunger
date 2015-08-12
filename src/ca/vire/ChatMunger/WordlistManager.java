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
			System.out.println("Wordlist file not found.");
			return false;
		} catch (IOException e) {
			System.out.println("Problems writing to wordlist:");
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
			System.out.println("Could not open " + WordlistFile + " for reading.");
			return null;
		} catch (IOException e) {
			System.out.println("There was a problem reading from " + WordlistFile);
			e.printStackTrace();
			return null;
		}
		
		return dict;
	}
	
}

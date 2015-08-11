package ca.vire.ChatMunger;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;


public class WordlistManager {
	
	private String WordlistFile = null;
	private BufferedReader Reader = null;
	private int WordCount = 0;

	public WordlistManager(String Filename) throws IOException {
		WordlistFile = Filename;
		
		try {
			Reader = new BufferedReader(new FileReader(WordlistFile));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new IOException();
		}
	}

	public String GetNextWord() {
		
		String input = null, word = null;
		
		try {
			while (true) {
				// Read in the next word (skipping empty lines)
				input = Reader.readLine();
				
				// read input until EOF (get null) is reached
				if (input == null) {
					break;
				} else {
					if (input.length() > 0) {
						// Found word
						++WordCount;
						word = input;
						break;
					}
				}
			}
		} catch (IOException e) {
			// This should cover any strange read errors that might happen.			
			e.printStackTrace();
		}
		
		// Return the word, or null if at EOF.
		return word;
	}
	
	public int GetWordCount() {
		return WordCount;
	}
	
}

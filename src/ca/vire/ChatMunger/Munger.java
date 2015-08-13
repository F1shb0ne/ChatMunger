package ca.vire.ChatMunger;

import java.util.ArrayList;
import ca.vire.ChatMunger.Vocabulary;

public class Munger {

	public static String ProcessMessage(Vocabulary v, String msg) {
		int index = 0;
		String result = "";
		String word = "";
		String msg_lc = msg.toLowerCase();
		boolean mask[] = GetCaseMask(msg);

		// Go through the string and piece together words from sequential letters
		for (char c: msg_lc.toCharArray()) {
			++index;
			if ((int)c >= 97 && (int)c <= 122) {
				// We have a letter; build the word.
				word += c;

				if (index == msg.length()) {
					// Reached the end of the string, ending on a word
					result += ExchangeWord(v, word);
				}
				continue;
			}

			// We either have a word, or no word
			if (word.length() > 0) {
				result += ExchangeWord(v, word);
				word = "";
			} else {
				// We're outside a word, print whatever was scanned.
			}

			result += c;

		}

		return result;
	}

	private static String ExchangeWord(Vocabulary v, String word) {
		String result = "";

		if (v.MappedWordExists(word)) {
			result = result + v.GetMappedWord(word);
		} else {
			result = result + v.GetRandomWord(word.length());
		}

		return result;
	}

	public static boolean[] GetCaseMask(String msg) {
		boolean mask[] = new boolean[msg.length()];
		int index = 0;

		// Build a boolean bit array for where characters in a string are capitalized
		for (char c: msg.toCharArray()) {
			if ((int)c >= 65 && (int)c <= 91)
				mask[index] = true;
			else
				mask[index] = false;
			++index;
		}
		return mask;
	}

}

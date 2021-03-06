package ca.vire.ChatMunger;

import ca.vire.ChatMunger.Vocabulary;

public class Munger {

    public static String ProcessMessage(Vocabulary v, String msg) {
        int index = 0;
        String result = "";
        String word = "";

        // Go through the string and piece together words from sequential letters
        // Include escape char: `
        for (char c: msg.toCharArray()) {
            ++index;
            if (((int)c >= 97 && (int)c <= 122) || ((int)c >= 65 && (int)c <= 91) || (int)c == 96) {
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
        boolean capitalize = isCapitalized(word);
        boolean AllCaps = false;

        if (isEscaped(word))
            return getEscapedWord(word);

        if (word.length() > 1)
            AllCaps = isAllCaps(word);

        if (v.MappedWordExists(word)) {
            result = result + v.GetMappedWord(word);
        } else {
            result = result + v.GetRandomWord(word.length());
        }

        if (AllCaps)
            result = result.toUpperCase();
        if (capitalize)
            result = Capitalize(result);

        return result;
    }


    private static boolean isEscaped(String word) {
        if (word.charAt(0) == 96)
            return true;
        return false;
    }

    private static String getEscapedWord(String word) {
        return word.substring(1);
    }

    private static boolean isCapitalized(String word) {
        if (word.charAt(0) >= 65 && word.charAt(0) <= 91)
            return true;
        return false;
    }

    private static String Capitalize(String word) {
        String result = "";
        int index = 0;

        for (char c: word.toCharArray()) {
            if (index == 0) {
                result += c;
                result = result.toUpperCase();
            } else {
                result += c;
            }
            ++index;
        }
        return result;
    }

    private static boolean isAllCaps(String word) {
        for (char c: word.toCharArray()) {
            if (c >= 97 && c <= 122)
                return false;
        }
        return true;
    }

}

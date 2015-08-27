package ca.vire.ChatMunger;

import java.util.HashMap;

/*
 * This object represents everything the plugin needs to know about
 * the player as an individual.
 */
public class MungerPlayer {
    // Mapping between a language name and skill point data for that player
    public HashMap<String, LanguageProperties> LangKnowledge;
    public String CurrentLanguage;
    public int LastExchange; // Time in seconds since using /teachlang or /acceptlang commands

    public MungerPlayer() {
        LangKnowledge = new HashMap<String, LanguageProperties>();
        CurrentLanguage = "Common";
    }
    
    // Return number of languages the player has had any teaching in
    public int GetLanguageCount() {
        return LangKnowledge.size();
    }
    
    // Return number of languages the player is completely fluent in
    public int GetFluentLanguageCount() {
        int num = 0;
        
        for (String lang: LangKnowledge.keySet()) {
            if (LangKnowledge.get(lang).GetSkillPercentage() == 100)
                ++num;
        }
        return num;
    }
    
    public boolean IsFluentIn(String language) {
        boolean result = false;

        if (LangKnowledge.containsKey(language))
            if (LangKnowledge.get(language).IsFluent())
                result = true;

        return result;
    }

    public void SetLanguage(String language, LanguageProperties ml) {
        LangKnowledge.put(language, ml);
    }
}

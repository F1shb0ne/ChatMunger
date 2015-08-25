package ca.vire.ChatMunger;

import java.util.HashMap;

/*
 * This object represents everything the plugin needs to know about
 * the player as an individual.
 */
public class MungerPlayer {
    // Mapping between a language name and skill point data for that player
    private HashMap<String, MungedLanguage> LangKnowledge;

    public MungerPlayer(String name) {
        LangKnowledge = new HashMap<String, MungedLanguage>();
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
}

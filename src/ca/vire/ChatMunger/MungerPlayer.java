package ca.vire.ChatMunger;

import java.util.HashMap;
import java.util.Map;

/*
 * This object represents everything the plugin needs to know about
 * the player as an individual.
 */
public class MungerPlayer {
    private String PlayerName;
    private Map<String, MungedLanguage> LangKnowledge;

    public MungerPlayer(String name) {
        PlayerName = name;
        LangKnowledge = new HashMap<String, MungedLanguage>();
    }
    
    public String GetPlayer() {
        return PlayerName;
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
    
}

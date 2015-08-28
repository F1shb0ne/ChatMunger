package ca.vire.ChatMunger;

// Settings that belong to a language globally 
public class LanguageSettings {

    public int RequiredSkillPoints;
    public long ExchangeCoolDown;
    
    LanguageSettings(int requiredpoints, long cooldown) {
        RequiredSkillPoints = requiredpoints;
        ExchangeCoolDown = cooldown;
    }
}

package ca.vire.ChatMunger;

// Settings that belong to a language globally 
public class LanguageSettings {

    public int RequiredSkillPoints;
    public int ExchangeCoolDown;
    
    LanguageSettings(int requiredpoints, int cooldown) {
        RequiredSkillPoints = requiredpoints;
        ExchangeCoolDown = cooldown;
    }
}

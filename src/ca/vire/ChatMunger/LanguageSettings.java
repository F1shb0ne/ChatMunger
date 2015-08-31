package ca.vire.ChatMunger;

// Settings that belong to a language globally 
public class LanguageSettings {

    public int RequiredSkillPoints;
    public long ExchangeCoolDown;
    public int RequiredExposures;
    public boolean PassiveLearning;
    
    LanguageSettings(int requiredpoints, long cooldown, int requiredexposures, boolean passivelearning) {
        RequiredSkillPoints = requiredpoints;
        ExchangeCoolDown = cooldown;
        RequiredExposures = requiredexposures;
        PassiveLearning = passivelearning;
    }
}

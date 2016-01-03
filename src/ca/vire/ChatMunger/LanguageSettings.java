package ca.vire.ChatMunger;

// Settings that belong to a language globally 
public class LanguageSettings {

    public int RequiredSkillPoints;
    public long ExchangeCoolDown;
    public int RequiredExposures;
    public boolean PassiveLearning;
    public boolean StarterLanguage;

    LanguageSettings(int requiredpoints, long cooldown, int requiredexposures, boolean passivelearning, boolean starterlanguage) {
        RequiredSkillPoints = requiredpoints;
        ExchangeCoolDown = cooldown;
        RequiredExposures = requiredexposures;
        PassiveLearning = passivelearning;
        StarterLanguage = starterlanguage;
    }
}

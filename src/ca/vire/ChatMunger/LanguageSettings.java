package ca.vire.ChatMunger;

// Settings that belong to a language globally 
public class LanguageSettings {

    public int SkillPointsRequired;
    public long SkillPointCooldown;
    public int PassiveExposuresRequired;
    public boolean PassiveLearning;
    public int StarterLanguagePoints;

    LanguageSettings(int skillpoints, long cooldown, int requiredexposures, boolean passivelearning, int starterlanguagepoints) {
        SkillPointsRequired = skillpoints;
        SkillPointCooldown = cooldown;
        PassiveExposuresRequired = requiredexposures;
        PassiveLearning = passivelearning;
        StarterLanguagePoints = starterlanguagepoints;
    }
}

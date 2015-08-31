package ca.vire.ChatMunger;

/*
 * This object represents the attributes of a specific language that belongs to a player
 */
public class LanguageProperties {

    public int CurrentSkillPoints;  // Number of skill points the player has gained so far
    public int RequiredSkillPoints; // Number of skill points required to have learned the language
    public int CurrentExposures;    // Number of times a player has heard someone /speak in this language
    public int RequiredExposures;   // Number of exposures required to advance one skill point
    public boolean PassiveLearning; // Can the language be learned in a passive manner

    public LanguageProperties(int currentSkillPoints, int requiredSkillPoints, int currentExposures, int requiredExposures, boolean passiveLearning) {
        CurrentSkillPoints = currentSkillPoints;
        RequiredSkillPoints = requiredSkillPoints;
        CurrentExposures = currentExposures;
        RequiredExposures = requiredExposures;
        PassiveLearning = passiveLearning;
    }

    // Add or remove a skill point
    public void AddPoint(int point) {
        CurrentSkillPoints += point;

        if (CurrentSkillPoints > RequiredSkillPoints)
            CurrentSkillPoints = RequiredSkillPoints;

        if (CurrentSkillPoints < 0)
            CurrentSkillPoints = 0;
    }

    // Return true if exposure resulted in skill point gain
    public boolean AddExposure(int exposure) {
        boolean result = false;

        if (PassiveLearning) {
            CurrentExposures += exposure;

            if (CurrentExposures > RequiredExposures) {
                AddPoint(1);
                result = true;
                CurrentExposures = 0;
            }
            if (CurrentExposures < 0)
                CurrentExposures = 0;
        }

        return result;
    }

    // Return language skill as number between 0 - 100
    public int GetSkillPercentage() {
        return (int)(((double)CurrentSkillPoints / (double)RequiredSkillPoints) * 100.0);
    }

    public boolean IsFluent() {
        if (GetSkillPercentage() == 100)
            return true;
        else
            return false;
    }
}

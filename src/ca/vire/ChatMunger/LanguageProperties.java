package ca.vire.ChatMunger;

/*
 * This object represents the attributes of a specific language that belongs to a player
 */
public class LanguageProperties {

    public int CurrentSkillPoints; // Number of skill points the player has gained so far
    public int RequiredSkillPoints; // Number of skill points required to have learned the language

    public LanguageProperties(int currentSkillPoints, int requiredSkillPoints) {
        CurrentSkillPoints = currentSkillPoints;
        RequiredSkillPoints = currentSkillPoints;
    }
    
    // Add or remove a skill point
    public void AddPoint(int point) {
        CurrentSkillPoints += point;
        
        if (CurrentSkillPoints > RequiredSkillPoints)
            CurrentSkillPoints = RequiredSkillPoints;
        
        if (CurrentSkillPoints < 0)
            CurrentSkillPoints = 0;
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

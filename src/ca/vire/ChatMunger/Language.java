package ca.vire.ChatMunger;

// A language object is a union of vocabulary and it's properties
public class Language {

    public Vocabulary Vocab;
    public LanguageSettings Settings;
    
    public Language(Vocabulary v, LanguageSettings s) {
        Vocab = v;
        Settings = s;        
    }    
}

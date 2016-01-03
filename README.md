# ChatMunger :: The made-up language chat plugin

== Intro ==
The aim of this plugin is to add both novelty and a hint of strategy to Minecraft chat.

Think back to the Starwars or Lord of the Rings movies and some of the dialog between the characters.  On occasion you'll see two discussing something in a language other than English (with subtitles below) or with one speaking English, the other something different, and yet they both understand each other.  This plugin attempts to emulate this sort of atmosphere: all without the need for learning and memorizing constructed languages.

The uses for this plugin may not appeal to all, but others (such as RP'ers) may find it to their liking as an added element for gameplay on their server.  A group of friends could share similar skins and have their own race of elves, androids, vampires, etc.  Having their own language to go with it might be the icing on the cake!

The plugin is still somewhat early in its development with more features being planned.  If you're curious, check it out on my Github page here: https://github.com/F1shb0ne/ChatMunger

== Usage ==

Once you run the plugin for the first time, it will create a skeleton folder for where all your languages will go, and ExampleLang to use as a template.  Be sure to make a copy/rename ExampleLang to something different, as that name will never be used.

There are two text files that serve as a dictionary for each 'language' your server will have. The first is a list of your made-up words (one per line), and the second is a word association file: a word you want to substitute followed by a word you make up that will always be used.

The way the chat munging works is that the made-up words you give it are categorized by word length.  As the plugin processes a player's /speak command, it replaces each word with a randomly chosen word of the same length in the dictionary.  Priority is first given to the association file; it will try to match the original word with one in that list, otherwise it will randomly choose from the plain word list.  If there is a word of a length that cannot be found in the dictionary, it will find a smaller word to substitute instead.  So for example a 12-letter word in the original message might be replaced with a 10-letter word in the dictionary.

=== IMPORTANT! ===

The 'look and feel' of the language will only be as good as what you give it.  A small wordlist or one filled with unimaginative vocabulary will quickly show to your players, so be creative!  There's loads of inspiration to be found on the net in case you're stumped on what to use: there's plenty of individuals out there who have miticulously created dictionaries for languages like orcish, elvish etc, on the net.

There will be bugs and I will fix them as I find them and when I can, so if anyone does find a problem send me a PM.  As mentioned earlier, the source is publically available, so if you encounter a problem see if it is the result of a bug in my code.  Bug reports are always welcome ("It doesn't work" doesn't help; please be concise) and bug fixes are even better!  I will be sure to give credit in the release notes.

== Features ==

* Language learning is not instant, and should take a long time.
* The learning process is divided up into skill points, and each language will have a required number of skill points for it to be learned.
* Skill points in a language are gained when a player uses /acceptlang in response to someone who knows the language offering to teach it with /teachlang
* Both /teachlang and /acceptlang should have a lengthy cooldown period; 24 hours or so is recommended.  This controls language spread more carefully which in turn preserves its novelty and hopefully gives players something to plan for tomorrow and come back.
* An 'easy' language should require 2-4 skill points, where more difficult ones require 8 or more.
* Passive language learning: Each language has a flag which determines if it can be learned simply by exposure.  If enabled, you specify how many messages (usually something in the hundreds) a player needs to 'hear' before they gain a skill point for the language being spoken.

To view a list of all known languages on the server, the player types /langinfo.  Any languages the player knows will be in blue, as opposed to red indicating they don't.  To get more information about a specific language, the player can type /langinfo [language].

To start using a language, the player types /lang <language>, and then may use /speak <message>.  An administrator will need to get the ball rolling by either using using /givelang to instantly give one or more players some languages, or doing it on themselves and using the slower /teachlang method as a player would.

==  Commands  ==

=== Player ===
* /speak <message>  Let's the player 'speak' the language, those who know it see what you type, otherwise they see the made up language.
* /lang <language>  Switch to another language
* /langinfo [language]  Gets information about a language, or general language information on the server.
* /teachlang <player> <language>  Offers a skill point to another player in the specified language
* /acceptlang  Accepts the offer

=== Administrators ===
* /givelang <player> <language>  Instantly gives a player the ability to understand and /speak a language.
* /removelang <player> <language>  Removes a language from a player.
* /langreload  Reloads the language files in the plugin's data folder.

==  Permissions  ==
* ChatMunger.speak
* ChatMunger.lang
* ChatMunger.langinfo
* ChatMunger.teachlang
* ChatMunger.acceptlang
* ChatMunger.givelang
* ChatMunger.removelang
* ChatMunger.langreload

== Planned Features ==

* Starter Language Points: Players will have a /langmenu command to spend these language points on languages marked with a special flag to start off with.
* Three configurable speaking volumes: whisper, normal and shout. (Currently hard-coded to use normal speaking volume)
* User Language creation, possible donor feature & EULA Compliant!
* With the right requirements, players might wish to create their own languages.  Imposing appropriate cooldowns with commands like /createlang, /addword, /addwordmap could potentially lead to some interesting player content creation.  Careful planning will be required.

* Optional "Hardcore" mode: Players are required to choose a 'starter' language from a list, or are randomly assigned one when they first join. /speak is not used, however any usual methods of communication (like using 't') or commands like /msg, /tell, etc are transparently passed through the plugin with players consequently being forced to always use a munged language.


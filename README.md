# ChatMunger :: The made-up language chat plugin

The aim of this plugin is to add both novelty and a hint of strategy to Minecraft chat.

Think back to the Starwars or Lord of the Rings movies and some of the dialog between the characters.  On occasion you'll see two discussing something in a language other than English (with subtitles below) or with one speaking English, the other something different, and yet they both understand each other.

This plugin attempts to emulate this sort of atmosphere: all without the need for learning and memorizing constructed languages.

The implementation is relatively simple: There are two text files that serve as a dictionary for each 'language' your server will have.  The first is a list of your made-up words (one per line), and the second is a word association file: a word you want to substitute followed by a word you make up that will always be used.

The way the dictionary works is that the made-up words you give it are categorized by word length.  As the plugin processes a chat message, it replaces each word with a randomly chosen word of the same length in the dictionary.  Priority is first given to the association file; it will try to match the original word with one in that list, otherwise it will randomly choose from the plain word list.  If there is a word of a length that cannot be found in the dictionary, it will find a smaller word to substitute instead.  So for example a 12-letter word in the original message might be replaced with a 10-letter word in the dictionary.

The exact commands / command names have yet to be defined, but general usage will be as follows:

* By default the plugin won't affect what players say.

* There will be a /speak command where what you type will be munged by the plugin, and the translated message will be 'spoken' instead to everyone in chat.  The message will be stylized or marked in a manner so that players will know it was spoken in that language.  To change the language they /speak with, players can use the /lang [language] command.

* If a player knows that language, they'll see what the speaker originally typed, but anyone else who doesn't know it, will get the munged text instead.

* Once a player knows a language, they have the ability to teach it to someone else, using something like /teachlang [player] [language]. The person receiving the offer will be notified, and may accept by using /acceptlang

* Teaching and being taught a language however takes time to learn.  The /teachlang and /acceptlang will have a cooldown period, which is recommended to be 24 hours of real time.  The language will also require a specific number of sessions for it to be taught.  Both the cooldown time and number of sessions for each language must be specified in the config file.  This allows for some languages to be 'harder' than others.

* There will be a special permission node and set of commands that will allow someone (typically the owner or an admin) to maintain the languages in-game.  This means they can add or remove new words and word mappings without editing the files directly on the server.

Future planning: langauge creation

* It might be interesting and fun for players to define their own language, teach it to others, and develop it over time.  The actual command to create a language should have a significant cooldown period; a limit of 1 per week or longer so as not to over saturate the server.   A word invention command will be needed, and it too should have a cooldown delay system that increases with the language size.  The imposed delay is critical to force players to think carefully about what they put in their language.

* There should be a proportionate relationship between how difficult it is to teach (as in, how many teaching sessions it requires to learn) vs how big its vocabulary is.  This allows for a group of friends to all get on board quickly with its early development.

* It is suggested that all players should have the ability to be taught, speak and teach existing languages, but perhaps reserve language creation and new word development as carefully controlled or donor feature.



The uses for this plugin may not appeal to all, but others (such as RP'ers) may find it to their liking as an added element for gameplay on their server.  A group of friends could share similar skins and have their own race of elves, androids, vampires, etc.  Having their own language to go with it might be the icing on the cake.

Players that are part of a clan or faction (that may not have a party/faction chat plugin available to them) might reserve a language just for themselves.  Perhaps a group of players with similiar skins (elves, androids, vampires etc).

All that is required of the server owner is to either use a made-up language on the net somewhere (elvish, orcish, klingon, huttese, etc, etc) or make up their own.


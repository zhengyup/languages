package com.huskie.languages.config

import com.huskie.languages.domain.scenario.LearningLanguage
import com.huskie.languages.domain.scenario.ScenarioTopic
import org.springframework.stereotype.Component

@Component
class GermanScenarioSeedDataProvider : ScenarioSeedDataProvider {
    override val language: LearningLanguage = LearningLanguage.GERMAN

    override fun scenarios(): List<SeedScenario> =
        listOf(
            SeedScenario(
                title = "Checking into a Hotel",
                description = "A simple hotel check-in conversation.",
                language = language,
                topic = ScenarioTopic.TRAVEL,
                lines = listOf(
                    SeedLine(
                        speakerName = "Rezeptionist",
                        targetText = "Guten Tag, wie kann ich Ihnen helfen?",
                        pronunciationGuide = "goo-ten tahk, vee kahn ikh ee-nen hel-fen",
                        englishTranslation = "Good day, how can I help you?",
                        vocabularyItems = listOf(
                            seedVocabulary("Guten Tag", "goo-ten tahk", "good day", "A polite greeting at a hotel desk.", 0, 9),
                            seedVocabulary("wie kann ich", "vee kahn ikh", "how can I", "A common phrase for offering help.", 11, 23),
                            seedVocabulary("Ihnen helfen", "ee-nen hel-fen", "help you", "A polite formal way to offer assistance.", 24, 36)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Hallo, ich habe eine Reservierung.",
                        pronunciationGuide = "hah-loh, ikh hah-buh eye-nuh re-zer-vee-roong",
                        englishTranslation = "Hello, I have a reservation.",
                        vocabularyItems = listOf(
                            seedVocabulary("Hallo", "hah-loh", "hello", "A simple greeting used by the guest.", 0, 5),
                            seedVocabulary("ich habe", "ikh hah-buh", "I have", "Used to state possession or a booking.", 7, 15),
                            seedVocabulary("eine Reservierung", "eye-nuh re-zer-vee-roong", "a reservation", "The booking the guest already made.", 16, 33)
                        )
                    ),
                    SeedLine(
                        speakerName = "Rezeptionist",
                        targetText = "Wie ist Ihr Name?",
                        pronunciationGuide = "vee ist eer nah-muh",
                        englishTranslation = "What is your name?",
                        vocabularyItems = listOf(
                            seedVocabulary("Wie ist", "vee ist", "what is", "Used to ask for identifying information.", 0, 7),
                            seedVocabulary("Ihr Name", "eer nah-muh", "your name", "A formal way to ask the guest's name.", 8, 16)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Mein Name ist Tan.",
                        pronunciationGuide = "mine nah-muh ist tahn",
                        englishTranslation = "My name is Tan.",
                        vocabularyItems = listOf(
                            seedVocabulary("Mein Name", "mine nah-muh", "my name", "Introduces the speaker's name.", 0, 9),
                            seedVocabulary("ist Tan", "ist tahn", "is Tan", "Gives the actual family name.", 10, 17)
                        )
                    ),
                    SeedLine(
                        speakerName = "Rezeptionist",
                        targetText = "Haben Sie Ihren Pass?",
                        pronunciationGuide = "hah-ben zee ee-ren pahss",
                        englishTranslation = "Do you have your passport?",
                        vocabularyItems = listOf(
                            seedVocabulary("Haben Sie", "hah-ben zee", "do you have", "A formal question about having something with you.", 0, 9),
                            seedVocabulary("Ihren Pass", "ee-ren pahss", "your passport", "The receptionist asks for identification.", 10, 20)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Ja, hier ist mein Pass.",
                        pronunciationGuide = "yah, heer ist mine pahss",
                        englishTranslation = "Yes, here is my passport.",
                        vocabularyItems = listOf(
                            seedVocabulary("Ja", "yah", "yes", "A simple positive answer.", 0, 2),
                            seedVocabulary("hier ist", "heer ist", "here is", "Used when handing something over.", 4, 12),
                            seedVocabulary("mein Pass", "mine pahss", "my passport", "The document the guest gives to the receptionist.", 13, 22)
                        )
                    ),
                    SeedLine(
                        speakerName = "Rezeptionist",
                        targetText = "Ihr Zimmer ist im zweiten Stock.",
                        pronunciationGuide = "eer tsim-mer ist im tsvy-ten shtok",
                        englishTranslation = "Your room is on the second floor.",
                        vocabularyItems = listOf(
                            seedVocabulary("Ihr Zimmer", "eer tsim-mer", "your room", "Refers to the guest's assigned room.", 0, 10),
                            seedVocabulary("ist im", "ist im", "is on the", "Links the room to its location.", 11, 17),
                            seedVocabulary("zweiten Stock", "tsvy-ten shtok", "second floor", "Tells the guest where the room is.", 18, 31)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Danke. Wo ist der Aufzug?",
                        pronunciationGuide = "dahn-kuh. voh ist dare ouf-tsoog",
                        englishTranslation = "Thank you. Where is the elevator?",
                        vocabularyItems = listOf(
                            seedVocabulary("Danke", "dahn-kuh", "thank you", "A polite response after getting help.", 0, 5),
                            seedVocabulary("Wo ist", "voh ist", "where is", "Used to ask for a location.", 7, 13),
                            seedVocabulary("der Aufzug", "dare ouf-tsoog", "the elevator", "The guest asks how to reach the room.", 14, 24)
                        )
                    )
                )
            ),
            SeedScenario(
                title = "Ordering Food at a Restaurant",
                description = "Ordering food and drinks.",
                language = language,
                topic = ScenarioTopic.RESTAURANT,
                lines = listOf(
                    SeedLine(
                        speakerName = "Kellner",
                        targetText = "Guten Abend, möchten Sie etwas essen?",
                        pronunciationGuide = "goo-ten ah-bent, merkh-ten zee et-vas ess-en",
                        englishTranslation = "Good evening, would you like something to eat?",
                        vocabularyItems = listOf(
                            seedVocabulary("Guten Abend", "goo-ten ah-bent", "good evening", "A polite greeting used at dinner time.", 0, 11),
                            seedVocabulary("möchten Sie", "merkh-ten zee", "would you like", "A formal way to offer something.", 13, 24),
                            seedVocabulary("etwas essen", "et-vas ess-en", "something to eat", "Asks whether the guest wants food.", 25, 36)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Ja, ich möchte eine Suppe.",
                        pronunciationGuide = "yah, ikh merkh-tuh eye-nuh zoo-puh",
                        englishTranslation = "Yes, I would like a soup.",
                        vocabularyItems = listOf(
                            seedVocabulary("Ja", "yah", "yes", "A direct positive answer.", 0, 2),
                            seedVocabulary("ich möchte", "ikh merkh-tuh", "I would like", "A polite phrase for ordering.", 4, 14),
                            seedVocabulary("eine Suppe", "eye-nuh zoo-puh", "a soup", "The food item the guest chooses.", 15, 25)
                        )
                    ),
                    SeedLine(
                        speakerName = "Kellner",
                        targetText = "Möchten Sie etwas trinken?",
                        pronunciationGuide = "merkh-ten zee et-vas trin-ken",
                        englishTranslation = "Would you like something to drink?",
                        vocabularyItems = listOf(
                            seedVocabulary("Möchten Sie", "merkh-ten zee", "would you like", "A formal offer from the server.", 0, 11),
                            seedVocabulary("etwas trinken", "et-vas trin-ken", "something to drink", "Asks whether the guest wants a beverage.", 12, 25)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Ich nehme ein Wasser.",
                        pronunciationGuide = "ikh nay-muh ine vah-ser",
                        englishTranslation = "I'll take a water.",
                        vocabularyItems = listOf(
                            seedVocabulary("Ich nehme", "ikh nay-muh", "I'll take", "A natural way to choose an item.", 0, 9),
                            seedVocabulary("ein Wasser", "ine vah-ser", "a water", "The drink the guest orders.", 10, 20)
                        )
                    ),
                    SeedLine(
                        speakerName = "Kellner",
                        targetText = "Möchten Sie Brot?",
                        pronunciationGuide = "merkh-ten zee broht",
                        englishTranslation = "Would you like bread?",
                        vocabularyItems = listOf(
                            seedVocabulary("Möchten Sie", "merkh-ten zee", "would you like", "The server offers an extra item.", 0, 11),
                            seedVocabulary("Brot", "broht", "bread", "The optional side item being offered.", 12, 16)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Ja, bitte.",
                        pronunciationGuide = "yah, bit-tuh",
                        englishTranslation = "Yes, please.",
                        vocabularyItems = listOf(
                            seedVocabulary("Ja", "yah", "yes", "A positive answer to the offer.", 0, 2),
                            seedVocabulary("bitte", "bit-tuh", "please", "Keeps the reply polite.", 4, 9)
                        )
                    ),
                    SeedLine(
                        speakerName = "Kellner",
                        targetText = "Kommt sofort.",
                        pronunciationGuide = "komt zo-fort",
                        englishTranslation = "Coming right away.",
                        vocabularyItems = listOf(
                            seedVocabulary("Kommt", "komt", "coming", "The server confirms the order will arrive.", 0, 5),
                            seedVocabulary("sofort", "zo-fort", "right away", "Shows the order will come soon.", 6, 12)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Danke.",
                        pronunciationGuide = "dahn-kuh",
                        englishTranslation = "Thank you.",
                        vocabularyItems = listOf(
                            seedVocabulary("Danke", "dahn-kuh", "thank you", "A polite response after ordering.", 0, 5)
                        )
                    )
                )
            ),
            SeedScenario(
                title = "Asking for Directions",
                description = "Asking how to get somewhere.",
                language = language,
                topic = ScenarioTopic.DIRECTIONS,
                lines = listOf(
                    SeedLine(
                        speakerName = "Tourist",
                        targetText = "Entschuldigung, wo ist der Bahnhof?",
                        pronunciationGuide = "ent-shool-di-goong, voh ist dare bahn-hohf",
                        englishTranslation = "Excuse me, where is the train station?",
                        vocabularyItems = listOf(
                            seedVocabulary("Entschuldigung", "ent-shool-di-goong", "excuse me", "A polite way to begin a question with a stranger.", 0, 14),
                            seedVocabulary("wo ist", "voh ist", "where is", "Used to ask for a place.", 16, 22),
                            seedVocabulary("der Bahnhof", "dare bahn-hohf", "the train station", "The destination the tourist is asking about.", 23, 34)
                        )
                    ),
                    SeedLine(
                        speakerName = "Local",
                        targetText = "Der Bahnhof ist nicht weit.",
                        pronunciationGuide = "dare bahn-hohf ist nikht vite",
                        englishTranslation = "The train station is not far.",
                        vocabularyItems = listOf(
                            seedVocabulary("Der Bahnhof", "dare bahn-hohf", "the train station", "Refers to the destination in question.", 0, 11),
                            seedVocabulary("ist nicht", "ist nikht", "is not", "Builds the negative statement.", 12, 21),
                            seedVocabulary("weit", "vite", "far", "Describes distance.", 22, 26)
                        )
                    ),
                    SeedLine(
                        speakerName = "Tourist",
                        targetText = "Wie komme ich dorthin?",
                        pronunciationGuide = "vee kom-muh ikh dort-hin",
                        englishTranslation = "How do I get there?",
                        vocabularyItems = listOf(
                            seedVocabulary("Wie komme ich", "vee kom-muh ikh", "how do I get", "A common phrase for asking for directions.", 0, 13),
                            seedVocabulary("dorthin", "dort-hin", "there", "Refers to the place being asked about.", 14, 21)
                        )
                    ),
                    SeedLine(
                        speakerName = "Local",
                        targetText = "Gehen Sie geradeaus.",
                        pronunciationGuide = "gay-en zee guh-rah-duhs",
                        englishTranslation = "Go straight ahead.",
                        vocabularyItems = listOf(
                            seedVocabulary("Gehen Sie", "gay-en zee", "go", "A formal instruction for movement.", 0, 9),
                            seedVocabulary("geradeaus", "guh-rah-duhs", "straight ahead", "Tells the tourist to continue forward.", 10, 19)
                        )
                    ),
                    SeedLine(
                        speakerName = "Local",
                        targetText = "Dann links und dann rechts.",
                        pronunciationGuide = "dahn links oont dahn rekhts",
                        englishTranslation = "Then left and then right.",
                        vocabularyItems = listOf(
                            seedVocabulary("Dann links", "dahn links", "then left", "Gives the next step in the route.", 0, 10),
                            seedVocabulary("und dann", "oont dahn", "and then", "Connects the direction steps.", 11, 19),
                            seedVocabulary("rechts", "rekhts", "right", "The final turn in the directions.", 20, 26)
                        )
                    ),
                    SeedLine(
                        speakerName = "Tourist",
                        targetText = "Ist es weit?",
                        pronunciationGuide = "ist ess vite",
                        englishTranslation = "Is it far?",
                        vocabularyItems = listOf(
                            seedVocabulary("Ist es", "ist ess", "is it", "A short question about distance.", 0, 6),
                            seedVocabulary("weit", "vite", "far", "Asks whether the place is far away.", 7, 11)
                        )
                    ),
                    SeedLine(
                        speakerName = "Local",
                        targetText = "Nein, nur fünf Minuten.",
                        pronunciationGuide = "nine, noor funf mee-noo-ten",
                        englishTranslation = "No, only five minutes.",
                        vocabularyItems = listOf(
                            seedVocabulary("Nein", "nine", "no", "A negative answer.", 0, 4),
                            seedVocabulary("nur fünf Minuten", "noor funf mee-noo-ten", "only five minutes", "Shows the place is close by.", 6, 22)
                        )
                    ),
                    SeedLine(
                        speakerName = "Tourist",
                        targetText = "Danke schön.",
                        pronunciationGuide = "dahn-kuh shern",
                        englishTranslation = "Thank you very much.",
                        vocabularyItems = listOf(
                            seedVocabulary("Danke schön", "dahn-kuh shern", "thank you very much", "A warm way to thank someone for help.", 0, 11)
                        )
                    )
                )
            ),
            SeedScenario(
                title = "Buying a Train Ticket",
                description = "Buying a ticket at a station.",
                language = language,
                topic = ScenarioTopic.TRAVEL,
                lines = listOf(
                    SeedLine(
                        speakerName = "Kunde",
                        targetText = "Hallo, ich möchte ein Ticket kaufen.",
                        pronunciationGuide = "hah-loh, ikh merkh-tuh ine tik-et kou-fen",
                        englishTranslation = "Hello, I would like to buy a ticket.",
                        vocabularyItems = listOf(
                            seedVocabulary("Hallo", "hah-loh", "hello", "A simple greeting at the ticket desk.", 0, 5),
                            seedVocabulary("ich möchte", "ikh merkh-tuh", "I would like", "A polite phrase used for requests.", 7, 17),
                            seedVocabulary("ein Ticket kaufen", "ine tik-et kou-fen", "buy a ticket", "States what the customer wants to do.", 18, 35)
                        )
                    ),
                    SeedLine(
                        speakerName = "Mitarbeiter",
                        targetText = "Wohin möchten Sie fahren?",
                        pronunciationGuide = "voh-hin merkh-ten zee fah-ren",
                        englishTranslation = "Where would you like to travel?",
                        vocabularyItems = listOf(
                            seedVocabulary("Wohin", "voh-hin", "to where", "Asks for the travel destination.", 0, 5),
                            seedVocabulary("möchten Sie", "merkh-ten zee", "would you like", "A formal way to ask about preference.", 6, 17),
                            seedVocabulary("fahren", "fah-ren", "travel / go", "Used for going by train.", 18, 24)
                        )
                    ),
                    SeedLine(
                        speakerName = "Kunde",
                        targetText = "Ich möchte nach Berlin fahren.",
                        pronunciationGuide = "ikh merkh-tuh nahkh ber-leen fah-ren",
                        englishTranslation = "I would like to travel to Berlin.",
                        vocabularyItems = listOf(
                            seedVocabulary("Ich möchte", "ikh merkh-tuh", "I would like", "Introduces the request politely.", 0, 10),
                            seedVocabulary("nach Berlin", "nahkh ber-leen", "to Berlin", "Names the destination city.", 11, 22),
                            seedVocabulary("fahren", "fah-ren", "travel", "Used for going somewhere by train.", 23, 29)
                        )
                    ),
                    SeedLine(
                        speakerName = "Mitarbeiter",
                        targetText = "Einfach oder hin und zurück?",
                        pronunciationGuide = "ine-fakh oh-der hin oont tsoo-ruek",
                        englishTranslation = "One way or round trip?",
                        vocabularyItems = listOf(
                            seedVocabulary("Einfach", "ine-fakh", "one way", "Refers to a single journey ticket.", 0, 7),
                            seedVocabulary("oder", "oh-der", "or", "Presents the second option.", 8, 12),
                            seedVocabulary("hin und zurück", "hin oont tsoo-ruek", "round trip", "Means there and back again.", 13, 27)
                        )
                    ),
                    SeedLine(
                        speakerName = "Kunde",
                        targetText = "Einfach, bitte.",
                        pronunciationGuide = "ine-fakh, bit-tuh",
                        englishTranslation = "One way, please.",
                        vocabularyItems = listOf(
                            seedVocabulary("Einfach", "ine-fakh", "one way", "The customer chooses a one-way ticket.", 0, 7),
                            seedVocabulary("bitte", "bit-tuh", "please", "Keeps the answer polite.", 9, 14)
                        )
                    ),
                    SeedLine(
                        speakerName = "Mitarbeiter",
                        targetText = "Das kostet 20 Euro.",
                        pronunciationGuide = "dahs kos-tet tsvan-tsikh oy-roh",
                        englishTranslation = "That costs 20 euros.",
                        vocabularyItems = listOf(
                            seedVocabulary("Das kostet", "dahs kos-tet", "that costs", "Introduces the total price.", 0, 10),
                            seedVocabulary("20 Euro", "tsvan-tsikh oy-roh", "20 euros", "States the ticket price.", 11, 18)
                        )
                    ),
                    SeedLine(
                        speakerName = "Kunde",
                        targetText = "Hier bitte.",
                        pronunciationGuide = "heer bit-tuh",
                        englishTranslation = "Here you go.",
                        vocabularyItems = listOf(
                            seedVocabulary("Hier", "heer", "here", "Used when handing over money.", 0, 4),
                            seedVocabulary("bitte", "bit-tuh", "please / here you go", "A polite phrase while paying.", 5, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "Mitarbeiter",
                        targetText = "Danke.",
                        pronunciationGuide = "dahn-kuh",
                        englishTranslation = "Thank you.",
                        vocabularyItems = listOf(
                            seedVocabulary("Danke", "dahn-kuh", "thank you", "A polite response after receiving payment.", 0, 5)
                        )
                    )
                )
            ),
            SeedScenario(
                title = "At the Airport",
                description = "Basic airport interaction.",
                language = language,
                topic = ScenarioTopic.TRAVEL,
                lines = listOf(
                    SeedLine(
                        speakerName = "Mitarbeiter",
                        targetText = "Guten Tag, haben Sie ein Ticket?",
                        pronunciationGuide = "goo-ten tahk, hah-ben zee ine tik-et",
                        englishTranslation = "Good day, do you have a ticket?",
                        vocabularyItems = listOf(
                            seedVocabulary("Guten Tag", "goo-ten tahk", "good day", "A polite airport greeting.", 0, 9),
                            seedVocabulary("haben Sie", "hah-ben zee", "do you have", "A formal question about documents.", 11, 20),
                            seedVocabulary("ein Ticket", "ine tik-et", "a ticket", "The item the staff member wants to see.", 21, 31)
                        )
                    ),
                    SeedLine(
                        speakerName = "Reisender",
                        targetText = "Ja, ich habe ein Ticket.",
                        pronunciationGuide = "yah, ikh hah-buh ine tik-et",
                        englishTranslation = "Yes, I have a ticket.",
                        vocabularyItems = listOf(
                            seedVocabulary("Ja", "yah", "yes", "A simple confirmation.", 0, 2),
                            seedVocabulary("ich habe", "ikh hah-buh", "I have", "Confirms possession of the ticket.", 4, 12),
                            seedVocabulary("ein Ticket", "ine tik-et", "a ticket", "The travel document being shown.", 13, 23)
                        )
                    ),
                    SeedLine(
                        speakerName = "Mitarbeiter",
                        targetText = "Wohin fliegen Sie?",
                        pronunciationGuide = "voh-hin flee-gen zee",
                        englishTranslation = "Where are you flying to?",
                        vocabularyItems = listOf(
                            seedVocabulary("Wohin", "voh-hin", "to where", "Asks for the destination.", 0, 5),
                            seedVocabulary("fliegen Sie", "flee-gen zee", "are you flying", "A formal way to ask about the flight.", 6, 17)
                        )
                    ),
                    SeedLine(
                        speakerName = "Reisender",
                        targetText = "Ich fliege nach München.",
                        pronunciationGuide = "ikh flee-guh nahkh muen-khen",
                        englishTranslation = "I'm flying to Munich.",
                        vocabularyItems = listOf(
                            seedVocabulary("Ich fliege", "ikh flee-guh", "I am flying", "States the planned flight.", 0, 10),
                            seedVocabulary("nach München", "nahkh muen-khen", "to Munich", "Names the destination city.", 11, 23)
                        )
                    ),
                    SeedLine(
                        speakerName = "Mitarbeiter",
                        targetText = "Haben Sie Gepäck?",
                        pronunciationGuide = "hah-ben zee guh-pek",
                        englishTranslation = "Do you have luggage?",
                        vocabularyItems = listOf(
                            seedVocabulary("Haben Sie", "hah-ben zee", "do you have", "The staff member asks about bags.", 0, 9),
                            seedVocabulary("Gepäck", "guh-pek", "luggage", "Refers to checked or carried bags.", 10, 16)
                        )
                    ),
                    SeedLine(
                        speakerName = "Reisender",
                        targetText = "Ja, ein Koffer.",
                        pronunciationGuide = "yah, ine kof-fer",
                        englishTranslation = "Yes, one suitcase.",
                        vocabularyItems = listOf(
                            seedVocabulary("Ja", "yah", "yes", "A direct answer.", 0, 2),
                            seedVocabulary("ein Koffer", "ine kof-fer", "one suitcase", "Specifies the piece of luggage.", 4, 14)
                        )
                    ),
                    SeedLine(
                        speakerName = "Mitarbeiter",
                        targetText = "Alles klar.",
                        pronunciationGuide = "al-les klahr",
                        englishTranslation = "All right.",
                        vocabularyItems = listOf(
                            seedVocabulary("Alles klar", "al-les klahr", "all right", "Shows the check-in information is understood.", 0, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "Reisender",
                        targetText = "Danke.",
                        pronunciationGuide = "dahn-kuh",
                        englishTranslation = "Thank you.",
                        vocabularyItems = listOf(
                            seedVocabulary("Danke", "dahn-kuh", "thank you", "A polite response to the staff member.", 0, 5)
                        )
                    )
                )
            ),
            SeedScenario(
                title = "Checking into Airbnb",
                description = "Meeting a host.",
                language = language,
                topic = ScenarioTopic.TRAVEL,
                lines = listOf(
                    SeedLine(
                        speakerName = "Gastgeber",
                        targetText = "Hallo, willkommen!",
                        pronunciationGuide = "hah-loh, vil-kom-men",
                        englishTranslation = "Hello, welcome!",
                        vocabularyItems = listOf(
                            seedVocabulary("Hallo", "hah-loh", "hello", "A friendly greeting from the host.", 0, 5),
                            seedVocabulary("willkommen", "vil-kom-men", "welcome", "Greets the guest on arrival.", 7, 17)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Hallo, danke.",
                        pronunciationGuide = "hah-loh, dahn-kuh",
                        englishTranslation = "Hello, thank you.",
                        vocabularyItems = listOf(
                            seedVocabulary("Hallo", "hah-loh", "hello", "The guest greets the host back.", 0, 5),
                            seedVocabulary("danke", "dahn-kuh", "thank you", "A polite reply to the welcome.", 7, 12)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gastgeber",
                        targetText = "Wie war die Reise?",
                        pronunciationGuide = "vee var dee rye-zuh",
                        englishTranslation = "How was the trip?",
                        vocabularyItems = listOf(
                            seedVocabulary("Wie war", "vee var", "how was", "Used to ask about the journey.", 0, 7),
                            seedVocabulary("die Reise", "dee rye-zuh", "the trip", "Refers to the guest's travel here.", 8, 17)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Gut, danke.",
                        pronunciationGuide = "goot, dahn-kuh",
                        englishTranslation = "Good, thank you.",
                        vocabularyItems = listOf(
                            seedVocabulary("Gut", "goot", "good", "A short positive answer.", 0, 3),
                            seedVocabulary("danke", "dahn-kuh", "thank you", "Adds politeness to the reply.", 5, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gastgeber",
                        targetText = "Das ist die Wohnung.",
                        pronunciationGuide = "dahs ist dee voh-noong",
                        englishTranslation = "This is the apartment.",
                        vocabularyItems = listOf(
                            seedVocabulary("Das ist", "dahs ist", "this is", "Introduces the place.", 0, 7),
                            seedVocabulary("die Wohnung", "dee voh-noong", "the apartment", "Shows the guest the rental unit.", 8, 19)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Sehr schön.",
                        pronunciationGuide = "zair shern",
                        englishTranslation = "Very nice.",
                        vocabularyItems = listOf(
                            seedVocabulary("Sehr schön", "zair shern", "very nice", "A positive reaction to the apartment.", 0, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gastgeber",
                        targetText = "Haben Sie Fragen?",
                        pronunciationGuide = "hah-ben zee frah-gen",
                        englishTranslation = "Do you have questions?",
                        vocabularyItems = listOf(
                            seedVocabulary("Haben Sie", "hah-ben zee", "do you have", "A formal question from the host.", 0, 9),
                            seedVocabulary("Fragen", "frah-gen", "questions", "Checks whether the guest needs more information.", 10, 16)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Nein, alles gut.",
                        pronunciationGuide = "nine, al-les goot",
                        englishTranslation = "No, everything is fine.",
                        vocabularyItems = listOf(
                            seedVocabulary("Nein", "nine", "no", "The guest says they have no questions.", 0, 4),
                            seedVocabulary("alles gut", "al-les goot", "everything is fine", "Shows no help is needed right now.", 6, 15)
                        )
                    )
                )
            ),
            SeedScenario(
                title = "Shopping at a Store",
                description = "Buying something.",
                language = language,
                topic = ScenarioTopic.SHOPPING,
                lines = listOf(
                    SeedLine(
                        speakerName = "Verkäufer",
                        targetText = "Guten Tag.",
                        pronunciationGuide = "goo-ten tahk",
                        englishTranslation = "Good day.",
                        vocabularyItems = listOf(
                            seedVocabulary("Guten Tag", "goo-ten tahk", "good day", "A standard greeting in a store.", 0, 9)
                        )
                    ),
                    SeedLine(
                        speakerName = "Kunde",
                        targetText = "Hallo, ich möchte das kaufen.",
                        pronunciationGuide = "hah-loh, ikh merkh-tuh dahs kou-fen",
                        englishTranslation = "Hello, I would like to buy this.",
                        vocabularyItems = listOf(
                            seedVocabulary("Hallo", "hah-loh", "hello", "A simple customer greeting.", 0, 5),
                            seedVocabulary("ich möchte", "ikh merkh-tuh", "I would like", "A polite phrase for asking to buy something.", 7, 17),
                            seedVocabulary("das kaufen", "dahs kou-fen", "buy this", "Refers to the item the customer wants.", 18, 28)
                        )
                    ),
                    SeedLine(
                        speakerName = "Verkäufer",
                        targetText = "Möchten Sie noch etwas?",
                        pronunciationGuide = "merkh-ten zee nokh et-vas",
                        englishTranslation = "Would you like anything else?",
                        vocabularyItems = listOf(
                            seedVocabulary("Möchten Sie", "merkh-ten zee", "would you like", "A formal offer from the clerk.", 0, 11),
                            seedVocabulary("noch etwas", "nokh et-vas", "anything else", "Asks whether the customer wants more items.", 12, 22)
                        )
                    ),
                    SeedLine(
                        speakerName = "Kunde",
                        targetText = "Nein, danke.",
                        pronunciationGuide = "nine, dahn-kuh",
                        englishTranslation = "No, thank you.",
                        vocabularyItems = listOf(
                            seedVocabulary("Nein", "nine", "no", "A clear negative answer.", 0, 4),
                            seedVocabulary("danke", "dahn-kuh", "thank you", "Keeps the refusal polite.", 6, 11)
                        )
                    ),
                    SeedLine(
                        speakerName = "Verkäufer",
                        targetText = "Das kostet 10 Euro.",
                        pronunciationGuide = "dahs kos-tet tsehn oy-roh",
                        englishTranslation = "That costs 10 euros.",
                        vocabularyItems = listOf(
                            seedVocabulary("Das kostet", "dahs kos-tet", "that costs", "Introduces the price.", 0, 10),
                            seedVocabulary("10 Euro", "tsehn oy-roh", "10 euros", "States the total price.", 11, 18)
                        )
                    ),
                    SeedLine(
                        speakerName = "Kunde",
                        targetText = "Hier bitte.",
                        pronunciationGuide = "heer bit-tuh",
                        englishTranslation = "Here you go.",
                        vocabularyItems = listOf(
                            seedVocabulary("Hier", "heer", "here", "Used while handing over money.", 0, 4),
                            seedVocabulary("bitte", "bit-tuh", "please / here you go", "A polite phrase during payment.", 5, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "Verkäufer",
                        targetText = "Danke schön.",
                        pronunciationGuide = "dahn-kuh shern",
                        englishTranslation = "Thank you very much.",
                        vocabularyItems = listOf(
                            seedVocabulary("Danke schön", "dahn-kuh shern", "thank you very much", "A warm thanks after payment.", 0, 11)
                        )
                    ),
                    SeedLine(
                        speakerName = "Kunde",
                        targetText = "Danke.",
                        pronunciationGuide = "dahn-kuh",
                        englishTranslation = "Thank you.",
                        vocabularyItems = listOf(
                            seedVocabulary("Danke", "dahn-kuh", "thank you", "A polite final response before leaving.", 0, 5)
                        )
                    )
                )
            ),
            SeedScenario(
                title = "Asking for WiFi",
                description = "Asking for internet access.",
                language = language,
                topic = ScenarioTopic.TRAVEL,
                lines = listOf(
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Entschuldigung, haben Sie WLAN?",
                        pronunciationGuide = "ent-shool-di-goong, hah-ben zee vay-lahn",
                        englishTranslation = "Excuse me, do you have WiFi?",
                        vocabularyItems = listOf(
                            seedVocabulary("Entschuldigung", "ent-shool-di-goong", "excuse me", "A polite opener before asking for help.", 0, 14),
                            seedVocabulary("haben Sie", "hah-ben zee", "do you have", "A formal question for availability.", 16, 25),
                            seedVocabulary("WLAN", "vay-lahn", "WiFi", "The internet service the guest wants to use.", 26, 30)
                        )
                    ),
                    SeedLine(
                        speakerName = "Mitarbeiter",
                        targetText = "Ja, wir haben WLAN.",
                        pronunciationGuide = "yah, veer hah-ben vay-lahn",
                        englishTranslation = "Yes, we have WiFi.",
                        vocabularyItems = listOf(
                            seedVocabulary("Ja", "yah", "yes", "A positive answer.", 0, 2),
                            seedVocabulary("wir haben", "veer hah-ben", "we have", "Confirms the service is available.", 4, 13),
                            seedVocabulary("WLAN", "vay-lahn", "WiFi", "The internet service being offered.", 14, 18)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Wie ist das Passwort?",
                        pronunciationGuide = "vee ist dahs pahss-vohrt",
                        englishTranslation = "What is the password?",
                        vocabularyItems = listOf(
                            seedVocabulary("Wie ist", "vee ist", "what is", "Used to ask for a specific piece of information.", 0, 7),
                            seedVocabulary("das Passwort", "dahs pahss-vohrt", "the password", "The WiFi password the guest needs.", 8, 20)
                        )
                    ),
                    SeedLine(
                        speakerName = "Mitarbeiter",
                        targetText = "Das Passwort ist hier.",
                        pronunciationGuide = "dahs pahss-vohrt ist heer",
                        englishTranslation = "The password is here.",
                        vocabularyItems = listOf(
                            seedVocabulary("Das Passwort", "dahs pahss-vohrt", "the password", "Refers to the needed login information.", 0, 12),
                            seedVocabulary("ist hier", "ist heer", "is here", "Shows where the guest can find it.", 13, 21)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Danke.",
                        pronunciationGuide = "dahn-kuh",
                        englishTranslation = "Thank you.",
                        vocabularyItems = listOf(
                            seedVocabulary("Danke", "dahn-kuh", "thank you", "A polite response after receiving help.", 0, 5)
                        )
                    ),
                    SeedLine(
                        speakerName = "Mitarbeiter",
                        targetText = "Bitte.",
                        pronunciationGuide = "bit-tuh",
                        englishTranslation = "You're welcome.",
                        vocabularyItems = listOf(
                            seedVocabulary("Bitte", "bit-tuh", "you're welcome", "A polite reply after being thanked.", 0, 5)
                        )
                    )
                )
            ),
            SeedScenario(
                title = "Taking a Taxi",
                description = "Getting to a destination.",
                language = language,
                topic = ScenarioTopic.TRAVEL,
                lines = listOf(
                    SeedLine(
                        speakerName = "Fahrer",
                        targetText = "Hallo, wohin?",
                        pronunciationGuide = "hah-loh, voh-hin",
                        englishTranslation = "Hello, where to?",
                        vocabularyItems = listOf(
                            seedVocabulary("Hallo", "hah-loh", "hello", "A casual greeting from the driver.", 0, 5),
                            seedVocabulary("wohin", "voh-hin", "where to", "Asks for the destination.", 7, 12)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Zum Hotel, bitte.",
                        pronunciationGuide = "tsoom hoh-tel, bit-tuh",
                        englishTranslation = "To the hotel, please.",
                        vocabularyItems = listOf(
                            seedVocabulary("Zum Hotel", "tsoom hoh-tel", "to the hotel", "Gives the destination.", 0, 9),
                            seedVocabulary("bitte", "bit-tuh", "please", "Makes the request polite.", 11, 16)
                        )
                    ),
                    SeedLine(
                        speakerName = "Fahrer",
                        targetText = "Welches Hotel?",
                        pronunciationGuide = "vel-khes hoh-tel",
                        englishTranslation = "Which hotel?",
                        vocabularyItems = listOf(
                            seedVocabulary("Welches", "vel-khes", "which", "Asks for the specific place.", 0, 7),
                            seedVocabulary("Hotel", "hoh-tel", "hotel", "The place the driver needs clarified.", 8, 13)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Das Hotel Berlin.",
                        pronunciationGuide = "dahs hoh-tel ber-leen",
                        englishTranslation = "The Hotel Berlin.",
                        vocabularyItems = listOf(
                            seedVocabulary("Das Hotel", "dahs hoh-tel", "the hotel", "Introduces the hotel name.", 0, 9),
                            seedVocabulary("Berlin", "ber-leen", "Berlin", "The identifying name of the hotel.", 10, 16)
                        )
                    ),
                    SeedLine(
                        speakerName = "Fahrer",
                        targetText = "Alles klar.",
                        pronunciationGuide = "al-les klahr",
                        englishTranslation = "All right.",
                        vocabularyItems = listOf(
                            seedVocabulary("Alles klar", "al-les klahr", "all right", "Shows the driver understands the destination.", 0, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Wie lange dauert es?",
                        pronunciationGuide = "vee lahng-uh dow-ert ess",
                        englishTranslation = "How long will it take?",
                        vocabularyItems = listOf(
                            seedVocabulary("Wie lange", "vee lahng-uh", "how long", "Asks about the duration.", 0, 9),
                            seedVocabulary("dauert es", "dow-ert ess", "does it take", "Used for asking travel time.", 10, 19)
                        )
                    ),
                    SeedLine(
                        speakerName = "Fahrer",
                        targetText = "Zehn Minuten.",
                        pronunciationGuide = "tsayn mee-noo-ten",
                        englishTranslation = "Ten minutes.",
                        vocabularyItems = listOf(
                            seedVocabulary("Zehn Minuten", "tsayn mee-noo-ten", "ten minutes", "The estimated travel time.", 0, 12)
                        )
                    ),
                    SeedLine(
                        speakerName = "Gast",
                        targetText = "Danke.",
                        pronunciationGuide = "dahn-kuh",
                        englishTranslation = "Thank you.",
                        vocabularyItems = listOf(
                            seedVocabulary("Danke", "dahn-kuh", "thank you", "A polite response to the information.", 0, 5)
                        )
                    )
                )
            ),
            SeedScenario(
                title = "Emergency / Pharmacy",
                description = "Asking for help.",
                language = language,
                topic = ScenarioTopic.TRAVEL,
                lines = listOf(
                    SeedLine(
                        speakerName = "Tourist",
                        targetText = "Entschuldigung, ich brauche Hilfe.",
                        pronunciationGuide = "ent-shool-di-goong, ikh brow-khuh hil-fuh",
                        englishTranslation = "Excuse me, I need help.",
                        vocabularyItems = listOf(
                            seedVocabulary("Entschuldigung", "ent-shool-di-goong", "excuse me", "A polite way to get someone's attention in an emergency.", 0, 14),
                            seedVocabulary("ich brauche", "ikh brow-khuh", "I need", "Used to say that help is necessary.", 16, 27),
                            seedVocabulary("Hilfe", "hil-fuh", "help", "States the problem directly.", 28, 33)
                        )
                    ),
                    SeedLine(
                        speakerName = "Person",
                        targetText = "Was ist passiert?",
                        pronunciationGuide = "vahs ist pah-seert",
                        englishTranslation = "What happened?",
                        vocabularyItems = listOf(
                            seedVocabulary("Was ist", "vahs ist", "what is", "Begins the question about the problem.", 0, 7),
                            seedVocabulary("passiert", "pah-seert", "happened", "Asks what went wrong.", 8, 16)
                        )
                    ),
                    SeedLine(
                        speakerName = "Tourist",
                        targetText = "Ich bin krank.",
                        pronunciationGuide = "ikh bin krahnk",
                        englishTranslation = "I am sick.",
                        vocabularyItems = listOf(
                            seedVocabulary("Ich bin", "ikh bin", "I am", "Introduces the speaker's condition.", 0, 7),
                            seedVocabulary("krank", "krahnk", "sick", "Describes the health problem.", 8, 13)
                        )
                    ),
                    SeedLine(
                        speakerName = "Person",
                        targetText = "Gehen Sie zur Apotheke.",
                        pronunciationGuide = "gay-en zee tsoor ah-poh-tay-kuh",
                        englishTranslation = "Go to the pharmacy.",
                        vocabularyItems = listOf(
                            seedVocabulary("Gehen Sie", "gay-en zee", "go", "A formal instruction.", 0, 9),
                            seedVocabulary("zur Apotheke", "tsoor ah-poh-tay-kuh", "to the pharmacy", "Tells the tourist where to get help.", 10, 22)
                        )
                    ),
                    SeedLine(
                        speakerName = "Tourist",
                        targetText = "Wo ist die Apotheke?",
                        pronunciationGuide = "voh ist dee ah-poh-tay-kuh",
                        englishTranslation = "Where is the pharmacy?",
                        vocabularyItems = listOf(
                            seedVocabulary("Wo ist", "voh ist", "where is", "Used to ask for the location.", 0, 6),
                            seedVocabulary("die Apotheke", "dee ah-poh-tay-kuh", "the pharmacy", "The place the tourist needs to find.", 7, 19)
                        )
                    ),
                    SeedLine(
                        speakerName = "Person",
                        targetText = "Dort links.",
                        pronunciationGuide = "dort links",
                        englishTranslation = "Over there on the left.",
                        vocabularyItems = listOf(
                            seedVocabulary("Dort", "dort", "over there", "Points out the general direction.", 0, 4),
                            seedVocabulary("links", "links", "left", "Gives the turning direction.", 5, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "Tourist",
                        targetText = "Danke.",
                        pronunciationGuide = "dahn-kuh",
                        englishTranslation = "Thank you.",
                        vocabularyItems = listOf(
                            seedVocabulary("Danke", "dahn-kuh", "thank you", "A polite reply after getting help.", 0, 5)
                        )
                    ),
                    SeedLine(
                        speakerName = "Person",
                        targetText = "Gute Besserung.",
                        pronunciationGuide = "goo-tuh bes-se-roong",
                        englishTranslation = "Get well soon.",
                        vocabularyItems = listOf(
                            seedVocabulary("Gute Besserung", "goo-tuh bes-se-roong", "get well soon", "A kind expression used when someone is ill.", 0, 14)
                        )
                    )
                )
            )
        )
}

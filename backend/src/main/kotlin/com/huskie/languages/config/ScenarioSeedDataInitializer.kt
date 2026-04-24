package com.huskie.languages.config

import com.huskie.languages.domain.scenario.DifficultyLevel
import com.huskie.languages.domain.scenario.Scenario
import com.huskie.languages.domain.scenario.ScenarioLine
import com.huskie.languages.domain.scenario.ScenarioTopic
import com.huskie.languages.domain.scenario.VocabularyItem
import com.huskie.languages.repository.scenario.ScenarioLineRepository
import com.huskie.languages.repository.scenario.ScenarioRepository
import com.huskie.languages.repository.scenario.VocabularyItemRepository
import com.huskie.languages.service.scenario.ScenarioLineVocabularyCoverageValidator
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.text.Normalizer
import java.time.Instant

@Component
@Profile("!test")
class ScenarioSeedDataInitializer(
    private val scenarioRepository: ScenarioRepository,
    private val scenarioLineRepository: ScenarioLineRepository,
    private val vocabularyItemRepository: VocabularyItemRepository,
    private val scenarioLineVocabularyCoverageValidator: ScenarioLineVocabularyCoverageValidator
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        if (scenarioRepository.count() > 0) {
            return
        }

        seedScenarios().forEach { seedScenario(it) }
    }

    private fun seedScenario(seedScenario: SeedScenario) {
        val createdAt = Instant.now()
        val scenario = scenarioRepository.save(
            Scenario(
                title = seedScenario.title,
                description = seedScenario.description,
                topic = seedScenario.topic,
                difficultyLevel = DifficultyLevel.INTERMEDIATE,
                createdAt = createdAt
            )
        )

        seedScenario.lines.forEachIndexed { index, seedLine ->
            val scenarioLine = scenarioLineRepository.save(
                ScenarioLine(
                    scenario = scenario,
                    lineOrder = index + 1,
                    speakerName = seedLine.speakerName,
                    targetText = seedLine.targetText,
                    pronunciationGuide = normalizePronunciationGuide(seedLine.pronunciationGuide),
                    englishTranslation = seedLine.englishTranslation,
                    createdAt = createdAt
                )
            )

            val vocabularyItems = seedLine.vocabularyItems.map { seedVocabularyItem ->
                VocabularyItem(
                    scenarioLine = scenarioLine,
                    expression = seedVocabularyItem.expression,
                    pronunciationGuide = normalizePronunciationGuide(seedVocabularyItem.pronunciationGuide),
                    gloss = seedVocabularyItem.gloss,
                    explanation = seedVocabularyItem.explanation,
                    startCharIndex = seedVocabularyItem.startCharIndex,
                    endCharIndex = seedVocabularyItem.endCharIndex,
                    createdAt = createdAt
                )
            }

            scenarioLineVocabularyCoverageValidator.validate(scenarioLine, vocabularyItems)
            vocabularyItemRepository.saveAll(vocabularyItems)
        }
    }

    private fun seedScenarios(): List<SeedScenario> =
        listOf(
            SeedScenario(
                title = "Ordering Food at a Restaurant",
                description = "A realistic restaurant conversation about getting seated and ordering a meal.",
                topic = ScenarioTopic.RESTAURANT,
                lines = listOf(
                    SeedLine(
                        speakerName = "服务员",
                        targetText = "欢迎光临，请问几位？",
                        pronunciationGuide = "huān yíng guāng lín, qǐng wèn jǐ wèi?",
                        englishTranslation = "Welcome, how many people?",
                        vocabularyItems = listOf(
                            seedVocabulary("欢迎光临", "huān yíng guāng lín", "welcome", "A standard greeting used when customers enter a restaurant.", 0, 4),
                            seedVocabulary("请问", "qǐng wèn", "may I ask", "A polite phrase used before asking a question.", 5, 7),
                            seedVocabulary("几位", "jǐ wèi", "how many guests", "A polite way to ask the number of people in the party.", 7, 9)
                        )
                    ),
                    SeedLine(
                        speakerName = "顾客",
                        targetText = "两位，有靠窗的位子吗？",
                        pronunciationGuide = "liǎng wèi, yǒu kào chuāng de wèi zi ma?",
                        englishTranslation = "Two people. Do you have a window seat?",
                        vocabularyItems = listOf(
                            seedVocabulary("两位", "liǎng wèi", "two people", "Used when giving the number of diners.", 0, 2),
                            seedVocabulary("有", "yǒu", "to have", "Used to ask whether something is available.", 3, 4),
                            seedVocabulary("靠窗的位子", "kào chuāng de wèi zi", "a window seat", "Refers to a seat next to the window.", 4, 9),
                            seedVocabulary("吗", "ma", "question particle", "Turns the sentence into a yes-or-no question.", 9, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "服务员",
                        targetText = "有，请跟我来。",
                        pronunciationGuide = "yǒu, qǐng gēn wǒ lái.",
                        englishTranslation = "Yes, please follow me.",
                        vocabularyItems = listOf(
                            seedVocabulary("有", "yǒu", "yes / available", "Confirms that the requested seat is available.", 0, 1),
                            seedVocabulary("请", "qǐng", "please", "Used politely before giving an instruction.", 2, 3),
                            seedVocabulary("跟我来", "gēn wǒ lái", "follow me", "Invites the guest to walk with the server.", 3, 6)
                        )
                    ),
                    SeedLine(
                        speakerName = "顾客",
                        targetText = "我想点一份牛肉面，再来一杯热茶。",
                        pronunciationGuide = "wǒ xiǎng diǎn yí fèn niú ròu miàn, zài lái yì bēi rè chá.",
                        englishTranslation = "I'd like to order one beef noodle soup and a cup of hot tea.",
                        vocabularyItems = listOf(
                            seedVocabulary("我想", "wǒ xiǎng", "I would like", "A softer way to express what you want.", 0, 2),
                            seedVocabulary("点", "diǎn", "to order", "Used when ordering food or drinks.", 2, 3),
                            seedVocabulary("一份", "yí fèn", "one portion", "Measure phrase for a serving of food.", 3, 5),
                            seedVocabulary("牛肉面", "niú ròu miàn", "beef noodles", "A common noodle dish.", 5, 8),
                            seedVocabulary("再来", "zài lái", "and also have", "Adds another item to the order.", 9, 11),
                            seedVocabulary("一杯", "yì bēi", "one cup", "Measure phrase for drinks.", 11, 13),
                            seedVocabulary("热茶", "rè chá", "hot tea", "Tea served warm.", 13, 15)
                        )
                    ),
                    SeedLine(
                        speakerName = "服务员",
                        targetText = "好的，要不要再加一个小菜？",
                        pronunciationGuide = "hǎo de, yào bú yào zài jiā yí ge xiǎo cài?",
                        englishTranslation = "Okay. Would you like to add a side dish?",
                        vocabularyItems = listOf(
                            seedVocabulary("好的", "hǎo de", "okay", "Confirms the order politely.", 0, 2),
                            seedVocabulary("要不要", "yào bú yào", "would you like", "Common pattern used to offer something.", 3, 6),
                            seedVocabulary("再加", "zài jiā", "add another", "Suggests adding one more item.", 6, 8),
                            seedVocabulary("一个", "yí ge", "one", "A general measure phrase.", 8, 10),
                            seedVocabulary("小菜", "xiǎo cài", "side dish", "A small extra dish served with the meal.", 10, 12)
                        )
                    ),
                    SeedLine(
                        speakerName = "顾客",
                        targetText = "好啊，那就来一盘拍黄瓜。",
                        pronunciationGuide = "hǎo a, nà jiù lái yì pán pāi huáng guā.",
                        englishTranslation = "Sure, then let's have one plate of smashed cucumber salad.",
                        vocabularyItems = listOf(
                            seedVocabulary("好啊", "hǎo a", "sure", "A relaxed way to agree.", 0, 2),
                            seedVocabulary("那就", "nà jiù", "in that case", "Used to move naturally to a decision.", 3, 5),
                            seedVocabulary("来", "lái", "to have / bring", "Used when placing an order.", 5, 6),
                            seedVocabulary("一盘", "yì pán", "one plate", "Measure phrase for a plate of food.", 6, 8),
                            seedVocabulary("拍黄瓜", "pāi huáng guā", "smashed cucumber salad", "A common cold side dish.", 8, 11)
                        )
                    ),
                    SeedLine(
                        speakerName = "服务员",
                        targetText = "没问题，面和茶马上给您送来。",
                        pronunciationGuide = "méi wèn tí, miàn hé chá mǎ shàng gěi nín sòng lái.",
                        englishTranslation = "No problem. We'll bring your noodles and tea right away.",
                        vocabularyItems = listOf(
                            seedVocabulary("没问题", "méi wèn tí", "no problem", "A reassuring response to the request.", 0, 3),
                            seedVocabulary("面", "miàn", "noodles", "Refers to the noodle dish in the order.", 4, 5),
                            seedVocabulary("和", "hé", "and", "Connects the two ordered items.", 5, 6),
                            seedVocabulary("茶", "chá", "tea", "The drink in the order.", 6, 7),
                            seedVocabulary("马上", "mǎ shàng", "right away", "Indicates something will happen soon.", 7, 9),
                            seedVocabulary("给您", "gěi nín", "for you", "Polite way to refer to the customer.", 9, 11),
                            seedVocabulary("送来", "sòng lái", "bring over", "To deliver something to the table.", 11, 13)
                        )
                    ),
                    SeedLine(
                        speakerName = "顾客",
                        targetText = "谢谢。",
                        pronunciationGuide = "xiè xie.",
                        englishTranslation = "Thank you.",
                        vocabularyItems = listOf(
                            seedVocabulary("谢谢", "xiè xie", "thank you", "A standard expression of thanks.", 0, 2)
                        )
                    )
                )
            ),
            SeedScenario(
                title = "Chatting with the Taxi Driver about Your Trip to Shanghai",
                description = "A casual taxi conversation about visiting Shanghai for work and sightseeing.",
                topic = ScenarioTopic.TRAVEL,
                lines = listOf(
                    SeedLine(
                        speakerName = "司机",
                        targetText = "你是第一次来上海吗？",
                        pronunciationGuide = "nǐ shì dì yī cì lái shàng hǎi ma?",
                        englishTranslation = "Is this your first time coming to Shanghai?",
                        vocabularyItems = listOf(
                            seedVocabulary("你", "nǐ", "you", "Refers to the passenger.", 0, 1),
                            seedVocabulary("是", "shì", "to be", "Links the subject with the description.", 1, 2),
                            seedVocabulary("第一次", "dì yī cì", "the first time", "Indicates first experience.", 2, 5),
                            seedVocabulary("来", "lái", "to come", "Used for arriving in a place.", 5, 6),
                            seedVocabulary("上海", "shàng hǎi", "Shanghai", "The city being discussed.", 6, 8),
                            seedVocabulary("吗", "ma", "question particle", "Turns the sentence into a yes-or-no question.", 8, 9)
                        )
                    ),
                    SeedLine(
                        speakerName = "乘客",
                        targetText = "不是，我去年也来过，不过这次是来出差的。",
                        pronunciationGuide = "bú shì, wǒ qù nián yě lái guo, bú guò zhè cì shì lái chū chāi de.",
                        englishTranslation = "No, I came last year too, but this time I'm here on a business trip.",
                        vocabularyItems = listOf(
                            seedVocabulary("不是", "bú shì", "no / not", "Used to deny the driver's assumption.", 0, 2),
                            seedVocabulary("我", "wǒ", "I", "Refers to the speaker.", 3, 4),
                            seedVocabulary("去年", "qù nián", "last year", "Indicates when the previous visit happened.", 4, 6),
                            seedVocabulary("也", "yě", "also", "Adds that the visit happened too.", 6, 7),
                            seedVocabulary("来过", "lái guo", "have come before", "Shows prior experience visiting.", 7, 9),
                            seedVocabulary("不过", "bú guò", "however", "Introduces a contrast.", 10, 12),
                            seedVocabulary("这次", "zhè cì", "this time", "Refers to the current visit.", 12, 14),
                            seedVocabulary("是", "shì", "to be", "Links the trip with its purpose.", 14, 15),
                            seedVocabulary("来", "lái", "to come", "Used with the purpose of the trip.", 15, 16),
                            seedVocabulary("出差", "chū chāi", "to travel on business", "Means to be on a business trip.", 16, 18),
                            seedVocabulary("的", "de", "nominal particle", "Marks the explanation of the current trip.", 18, 19)
                        )
                    ),
                    SeedLine(
                        speakerName = "司机",
                        targetText = "上海变化很快，这几年新开的店特别多。",
                        pronunciationGuide = "shàng hǎi biàn huà hěn kuài, zhè jǐ nián xīn kāi de diàn tè bié duō.",
                        englishTranslation = "Shanghai changes quickly. In the past few years, many newly opened shops have appeared.",
                        vocabularyItems = listOf(
                            seedVocabulary("上海", "shàng hǎi", "Shanghai", "The city being described.", 0, 2),
                            seedVocabulary("变化", "biàn huà", "changes", "Refers to developments in the city.", 2, 4),
                            seedVocabulary("很快", "hěn kuài", "very quickly", "Describes the speed of change.", 4, 6),
                            seedVocabulary("这几年", "zhè jǐ nián", "these past few years", "A recent time period.", 7, 10),
                            seedVocabulary("新开", "xīn kāi", "newly opened", "Describes businesses that opened recently.", 10, 12),
                            seedVocabulary("的", "de", "modifier particle", "Links the description to the noun that follows.", 12, 13),
                            seedVocabulary("店", "diàn", "shops", "Refers to stores or businesses.", 13, 14),
                            seedVocabulary("特别", "tè bié", "especially", "Adds emphasis.", 14, 16),
                            seedVocabulary("多", "duō", "many", "Describes large quantity.", 16, 17)
                        )
                    ),
                    SeedLine(
                        speakerName = "乘客",
                        targetText = "我已经发现了，路上高楼比以前多了很多。",
                        pronunciationGuide = "wǒ yǐ jīng fā xiàn le, lù shàng gāo lóu bǐ yǐ qián duō le hěn duō.",
                        englishTranslation = "I've already noticed that there are many more tall buildings than before.",
                        vocabularyItems = listOf(
                            seedVocabulary("我", "wǒ", "I", "Refers to the speaker.", 0, 1),
                            seedVocabulary("已经", "yǐ jīng", "already", "Shows the noticing happened by now.", 1, 3),
                            seedVocabulary("发现", "fā xiàn", "notice", "To become aware of something.", 3, 5),
                            seedVocabulary("了", "le", "aspect particle", "Marks a realized change or observation.", 5, 6),
                            seedVocabulary("路上", "lù shàng", "on the road", "Refers to what the passenger sees outside.", 7, 9),
                            seedVocabulary("高楼", "gāo lóu", "tall buildings", "High-rise buildings.", 9, 11),
                            seedVocabulary("比", "bǐ", "compared with", "Introduces a comparison.", 11, 12),
                            seedVocabulary("以前", "yǐ qián", "before", "Refers to the past.", 12, 14),
                            seedVocabulary("多了", "duō le", "more than before", "Indicates an increase.", 14, 16),
                            seedVocabulary("很多", "hěn duō", "a lot", "Adds emphasis to the increase.", 16, 18)
                        )
                    ),
                    SeedLine(
                        speakerName = "司机",
                        targetText = "这几天打算去哪里逛逛？",
                        pronunciationGuide = "zhè jǐ tiān dǎ suàn qù nǎ lǐ guàng guàng?",
                        englishTranslation = "Where are you planning to go look around these next few days?",
                        vocabularyItems = listOf(
                            seedVocabulary("这几天", "zhè jǐ tiān", "these next few days", "Refers to the time during the current stay.", 0, 3),
                            seedVocabulary("打算", "dǎ suàn", "plan to", "Asks about the passenger's intentions.", 3, 5),
                            seedVocabulary("去", "qù", "to go", "Movement toward a place.", 5, 6),
                            seedVocabulary("哪里", "nǎ lǐ", "where", "Asks for a location.", 6, 8),
                            seedVocabulary("逛逛", "guàng guàng", "walk around / browse", "A casual expression for sightseeing or strolling.", 8, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "乘客",
                        targetText = "我想先去外滩，再去城隍庙吃小吃。",
                        pronunciationGuide = "wǒ xiǎng xiān qù wài tān, zài qù chéng huáng miào chī xiǎo chī.",
                        englishTranslation = "I want to go to the Bund first, then head to City God Temple for snacks.",
                        vocabularyItems = listOf(
                            seedVocabulary("我想", "wǒ xiǎng", "I want to", "Introduces the travel plan politely.", 0, 2),
                            seedVocabulary("先", "xiān", "first", "Shows sequence.", 2, 3),
                            seedVocabulary("去", "qù", "to go", "Movement to a destination.", 3, 4),
                            seedVocabulary("外滩", "wài tān", "the Bund", "A famous Shanghai waterfront area.", 4, 6),
                            seedVocabulary("再", "zài", "then", "Introduces the next step.", 7, 8),
                            seedVocabulary("去", "qù", "to go", "Movement to another destination.", 8, 9),
                            seedVocabulary("城隍庙", "chéng huáng miào", "City God Temple", "A well-known tourist area in Shanghai.", 9, 12),
                            seedVocabulary("吃", "chī", "to eat", "Used when talking about food plans.", 12, 13),
                            seedVocabulary("小吃", "xiǎo chī", "snacks", "Local street food or small dishes.", 13, 15)
                        )
                    ),
                    SeedLine(
                        speakerName = "司机",
                        targetText = "那不错，晚上浦东的夜景也值得看看。",
                        pronunciationGuide = "nà bú cuò, wǎn shang pǔ dōng de yè jǐng yě zhí de kàn kan.",
                        englishTranslation = "That sounds good. Pudong's night view is also worth seeing in the evening.",
                        vocabularyItems = listOf(
                            seedVocabulary("那", "nà", "that", "Refers to the passenger's plan.", 0, 1),
                            seedVocabulary("不错", "bú cuò", "not bad", "A natural way to say something sounds good.", 1, 3),
                            seedVocabulary("晚上", "wǎn shang", "evening", "Suggests a time to visit.", 4, 6),
                            seedVocabulary("浦东", "pǔ dōng", "Pudong", "The district famous for its skyline.", 6, 8),
                            seedVocabulary("的", "de", "modifier particle", "Links Pudong with the night view.", 8, 9),
                            seedVocabulary("夜景", "yè jǐng", "night view", "Refers to the city lights and skyline.", 9, 11),
                            seedVocabulary("也", "yě", "also", "Adds another recommendation.", 11, 12),
                            seedVocabulary("值得", "zhí de", "worth", "Indicates something is worthwhile.", 12, 14),
                            seedVocabulary("看看", "kàn kan", "take a look", "A casual way to suggest seeing something.", 14, 16)
                        )
                    ),
                    SeedLine(
                        speakerName = "乘客",
                        targetText = "听起来很好，我希望这次能多待两天。",
                        pronunciationGuide = "tīng qǐ lái hěn hǎo, wǒ xī wàng zhè cì néng duō dāi liǎng tiān.",
                        englishTranslation = "That sounds great. I hope I can stay two more days this time.",
                        vocabularyItems = listOf(
                            seedVocabulary("听起来", "tīng qǐ lái", "sounds", "Used to react to a suggestion or description.", 0, 3),
                            seedVocabulary("很好", "hěn hǎo", "very good", "Expresses approval.", 3, 5),
                            seedVocabulary("我", "wǒ", "I", "Refers to the speaker.", 6, 7),
                            seedVocabulary("希望", "xī wàng", "hope", "Expresses a wish.", 7, 9),
                            seedVocabulary("这次", "zhè cì", "this time", "Refers to the current trip.", 9, 11),
                            seedVocabulary("能", "néng", "can / be able to", "Indicates possibility.", 11, 12),
                            seedVocabulary("多待", "duō dāi", "stay a bit longer", "To remain for extra time.", 12, 14),
                            seedVocabulary("两天", "liǎng tiān", "two days", "The extra amount of time desired.", 14, 16)
                        )
                    )
                )
            ),
            SeedScenario(
                title = "Conversing with a Colleague about Weekend Plans",
                description = "A light workplace conversation about movies, food, and making weekend plans.",
                topic = ScenarioTopic.WORK,
                lines = listOf(
                    SeedLine(
                        speakerName = "同事甲",
                        targetText = "这个周末你有什么安排？",
                        pronunciationGuide = "zhè ge zhōu mò nǐ yǒu shén me ān pái?",
                        englishTranslation = "What plans do you have for this weekend?",
                        vocabularyItems = listOf(
                            seedVocabulary("这个周末", "zhè ge zhōu mò", "this weekend", "Refers to the coming weekend.", 0, 4),
                            seedVocabulary("你", "nǐ", "you", "Addresses the colleague.", 4, 5),
                            seedVocabulary("有什么", "yǒu shén me", "what do you have", "Asks what plans exist.", 5, 8),
                            seedVocabulary("安排", "ān pái", "plans / arrangements", "Refers to scheduled activities.", 8, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事乙",
                        targetText = "我打算先睡个懒觉，然后下午去看电影。",
                        pronunciationGuide = "wǒ dǎ suàn xiān shuì ge lǎn jiào, rán hòu xià wǔ qù kàn diàn yǐng.",
                        englishTranslation = "I'm planning to sleep in first, then watch a movie in the afternoon.",
                        vocabularyItems = listOf(
                            seedVocabulary("我", "wǒ", "I", "Refers to the speaker.", 0, 1),
                            seedVocabulary("打算", "dǎ suàn", "plan to", "Introduces the weekend plan.", 1, 3),
                            seedVocabulary("先", "xiān", "first", "Shows what comes first.", 3, 4),
                            seedVocabulary("睡个懒觉", "shuì ge lǎn jiào", "sleep in", "Means to sleep longer than usual.", 4, 8),
                            seedVocabulary("然后", "rán hòu", "then", "Connects the next action.", 9, 11),
                            seedVocabulary("下午", "xià wǔ", "afternoon", "Specifies when the next plan happens.", 11, 13),
                            seedVocabulary("去", "qù", "to go", "Used before the activity destination.", 13, 14),
                            seedVocabulary("看电影", "kàn diàn yǐng", "watch a movie", "A common leisure activity.", 14, 17)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事甲",
                        targetText = "听起来不错，你要看哪一部？",
                        pronunciationGuide = "tīng qǐ lái bú cuò, nǐ yào kàn nǎ yí bù?",
                        englishTranslation = "That sounds good. Which movie do you want to see?",
                        vocabularyItems = listOf(
                            seedVocabulary("听起来", "tīng qǐ lái", "sounds", "Used to react to a plan or idea.", 0, 3),
                            seedVocabulary("不错", "bú cuò", "not bad", "A natural positive reaction.", 3, 5),
                            seedVocabulary("你", "nǐ", "you", "Addresses the colleague.", 6, 7),
                            seedVocabulary("要", "yào", "want to", "Expresses intent.", 7, 8),
                            seedVocabulary("看", "kàn", "watch", "Used for films and shows.", 8, 9),
                            seedVocabulary("哪一部", "nǎ yí bù", "which one", "Asks which specific movie.", 9, 12)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事乙",
                        targetText = "我还没决定，可能会看那部新上映的喜剧片。",
                        pronunciationGuide = "wǒ hái méi jué dìng, kě néng huì kàn nà bù xīn shàng yìng de xǐ jù piàn.",
                        englishTranslation = "I haven't decided yet. I might watch that newly released comedy.",
                        vocabularyItems = listOf(
                            seedVocabulary("我", "wǒ", "I", "Refers to the speaker.", 0, 1),
                            seedVocabulary("还没", "hái méi", "not yet", "Shows the decision has not been made so far.", 1, 3),
                            seedVocabulary("决定", "jué dìng", "decide", "To make a choice.", 3, 5),
                            seedVocabulary("可能", "kě néng", "maybe / possibly", "Expresses uncertainty.", 6, 8),
                            seedVocabulary("会", "huì", "will / might", "Marks a likely future action.", 8, 9),
                            seedVocabulary("看", "kàn", "watch", "Used for viewing a film.", 9, 10),
                            seedVocabulary("那部", "nà bù", "that one", "Refers to a specific movie.", 10, 12),
                            seedVocabulary("新上映", "xīn shàng yìng", "newly released", "Describes a film that just came out.", 12, 15),
                            seedVocabulary("的", "de", "modifier particle", "Links the description to the movie type.", 15, 16),
                            seedVocabulary("喜剧片", "xǐ jù piàn", "comedy film", "The kind of movie being considered.", 16, 19)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事甲",
                        targetText = "如果你愿意，我也可以一起去。",
                        pronunciationGuide = "rú guǒ nǐ yuàn yì, wǒ yě kě yǐ yì qǐ qù.",
                        englishTranslation = "If you'd like, I can go with you too.",
                        vocabularyItems = listOf(
                            seedVocabulary("如果", "rú guǒ", "if", "Introduces a condition.", 0, 2),
                            seedVocabulary("你", "nǐ", "you", "Addresses the colleague.", 2, 3),
                            seedVocabulary("愿意", "yuàn yì", "be willing", "Checks whether the other person is open to the idea.", 3, 5),
                            seedVocabulary("我", "wǒ", "I", "Refers to the speaker.", 6, 7),
                            seedVocabulary("也", "yě", "also", "Adds the speaker to the plan.", 7, 8),
                            seedVocabulary("可以", "kě yǐ", "can", "Shows possibility or willingness.", 8, 10),
                            seedVocabulary("一起", "yì qǐ", "together", "Means doing something with another person.", 10, 12),
                            seedVocabulary("去", "qù", "go", "Used for joining the outing.", 12, 13)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事乙",
                        targetText = "好啊，看完电影我们还可以去吃火锅。",
                        pronunciationGuide = "hǎo a, kàn wán diàn yǐng wǒ men hái kě yǐ qù chī huǒ guō.",
                        englishTranslation = "Sure. After the movie, we can also go eat hot pot.",
                        vocabularyItems = listOf(
                            seedVocabulary("好啊", "hǎo a", "sure", "A relaxed way to agree.", 0, 2),
                            seedVocabulary("看完", "kàn wán", "after finishing watching", "Indicates an action completed first.", 3, 5),
                            seedVocabulary("电影", "diàn yǐng", "movie", "Refers to the film they plan to watch.", 5, 7),
                            seedVocabulary("我们", "wǒ men", "we", "Refers to both colleagues together.", 7, 9),
                            seedVocabulary("还", "hái", "also / still", "Adds another possible plan.", 9, 10),
                            seedVocabulary("可以", "kě yǐ", "can", "Shows another possible activity.", 10, 12),
                            seedVocabulary("去", "qù", "go", "Movement to the next activity.", 12, 13),
                            seedVocabulary("吃", "chī", "eat", "Used with food plans.", 13, 14),
                            seedVocabulary("火锅", "huǒ guō", "hot pot", "A popular shared meal.", 14, 16)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事甲",
                        targetText = "没问题，我正好想吃点辣的。",
                        pronunciationGuide = "méi wèn tí, wǒ zhèng hǎo xiǎng chī diǎn là de.",
                        englishTranslation = "No problem. I happen to be craving something spicy.",
                        vocabularyItems = listOf(
                            seedVocabulary("没问题", "méi wèn tí", "no problem", "A natural way to agree to the plan.", 0, 3),
                            seedVocabulary("我", "wǒ", "I", "Refers to the speaker.", 4, 5),
                            seedVocabulary("正好", "zhèng hǎo", "just happen to", "Shows convenient timing.", 5, 7),
                            seedVocabulary("想", "xiǎng", "want", "Expresses desire.", 7, 8),
                            seedVocabulary("吃点", "chī diǎn", "eat some", "A casual way to talk about eating a bit of something.", 8, 10),
                            seedVocabulary("辣的", "là de", "something spicy", "Refers to spicy food.", 10, 12)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事乙",
                        targetText = "那我们周六下午联系，到时候再定时间。",
                        pronunciationGuide = "nà wǒ men zhōu liù xià wǔ lián xì, dào shí hou zài dìng shí jiān.",
                        englishTranslation = "Then let's get in touch Saturday afternoon and decide the time then.",
                        vocabularyItems = listOf(
                            seedVocabulary("那", "nà", "then", "Moves the conversation to the next step.", 0, 1),
                            seedVocabulary("我们", "wǒ men", "we", "Refers to both colleagues.", 1, 3),
                            seedVocabulary("周六", "zhōu liù", "Saturday", "Specifies the day.", 3, 5),
                            seedVocabulary("下午", "xià wǔ", "afternoon", "Specifies the time of day.", 5, 7),
                            seedVocabulary("联系", "lián xì", "get in touch", "Means to contact each other.", 7, 9),
                            seedVocabulary("到时候", "dào shí hou", "at that time", "Refers to when they reconnect later.", 10, 13),
                            seedVocabulary("再", "zài", "then / again", "Indicates the next step will happen later.", 13, 14),
                            seedVocabulary("定时间", "dìng shí jiān", "set the time", "Means to finalize the exact time.", 14, 17)
                        )
                    )
                )
            )
        )

    private fun seedVocabulary(
        expression: String,
        pronunciationGuide: String,
        gloss: String,
        explanation: String,
        startCharIndex: Int,
        endCharIndex: Int
    ): SeedVocabularyItem =
        SeedVocabularyItem(
            expression = expression,
            pronunciationGuide = normalizePronunciationGuide(pronunciationGuide),
            gloss = gloss,
            explanation = explanation,
            startCharIndex = startCharIndex,
            endCharIndex = endCharIndex
        )

    private fun normalizePronunciationGuide(value: String): String =
        Normalizer.normalize(value, Normalizer.Form.NFC)

    private data class SeedScenario(
        val title: String,
        val description: String,
        val topic: ScenarioTopic,
        val lines: List<SeedLine>
    )

    private data class SeedLine(
        val speakerName: String,
        val targetText: String,
        val pronunciationGuide: String,
        val englishTranslation: String,
        val vocabularyItems: List<SeedVocabularyItem>
    )

    private data class SeedVocabularyItem(
        val expression: String,
        val pronunciationGuide: String,
        val gloss: String,
        val explanation: String,
        val startCharIndex: Int,
        val endCharIndex: Int
    )
}

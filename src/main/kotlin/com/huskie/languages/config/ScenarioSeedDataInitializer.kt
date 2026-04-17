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
                    hanziText = seedLine.hanziText,
                    pinyinText = seedLine.pinyinText,
                    englishTranslation = seedLine.englishTranslation,
                    createdAt = createdAt
                )
            )

            val vocabularyItems = seedLine.vocabularyItems.map { seedVocabularyItem ->
                VocabularyItem(
                    scenarioLine = scenarioLine,
                    expression = seedVocabularyItem.expression,
                    pinyin = seedVocabularyItem.pinyin,
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
                        hanziText = "欢迎光临，请问几位？",
                        pinyinText = "Huan ying guang lin, qing wen ji wei?",
                        englishTranslation = "Welcome, how many people?",
                        vocabularyItems = listOf(
                            seedVocabulary("欢迎光临", "huan ying guang lin", "welcome", "A standard greeting used when customers enter a restaurant.", 0, 4),
                            seedVocabulary("请问", "qing wen", "may I ask", "A polite phrase used before asking a question.", 5, 7),
                            seedVocabulary("几位", "ji wei", "how many guests", "A polite way to ask the number of people in the party.", 7, 9)
                        )
                    ),
                    SeedLine(
                        speakerName = "顾客",
                        hanziText = "两位，有靠窗的位子吗？",
                        pinyinText = "Liang wei, you kao chuang de wei zi ma?",
                        englishTranslation = "Two people. Do you have a window seat?",
                        vocabularyItems = listOf(
                            seedVocabulary("两位", "liang wei", "two people", "Used when giving the number of diners.", 0, 2),
                            seedVocabulary("有", "you", "to have", "Used to ask whether something is available.", 3, 4),
                            seedVocabulary("靠窗的位子", "kao chuang de wei zi", "a window seat", "Refers to a seat next to the window.", 4, 9),
                            seedVocabulary("吗", "ma", "question particle", "Turns the sentence into a yes-or-no question.", 9, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "服务员",
                        hanziText = "有，请跟我来。",
                        pinyinText = "You, qing gen wo lai.",
                        englishTranslation = "Yes, please follow me.",
                        vocabularyItems = listOf(
                            seedVocabulary("有", "you", "yes / available", "Confirms that the requested seat is available.", 0, 1),
                            seedVocabulary("请", "qing", "please", "Used politely before giving an instruction.", 2, 3),
                            seedVocabulary("跟我来", "gen wo lai", "follow me", "Invites the guest to walk with the server.", 3, 6)
                        )
                    ),
                    SeedLine(
                        speakerName = "顾客",
                        hanziText = "我想点一份牛肉面，再来一杯热茶。",
                        pinyinText = "Wo xiang dian yi fen niu rou mian, zai lai yi bei re cha.",
                        englishTranslation = "I'd like to order one beef noodle soup and a cup of hot tea.",
                        vocabularyItems = listOf(
                            seedVocabulary("我想", "wo xiang", "I would like", "A softer way to express what you want.", 0, 2),
                            seedVocabulary("点", "dian", "to order", "Used when ordering food or drinks.", 2, 3),
                            seedVocabulary("一份", "yi fen", "one portion", "Measure phrase for a serving of food.", 3, 5),
                            seedVocabulary("牛肉面", "niu rou mian", "beef noodles", "A common noodle dish.", 5, 8),
                            seedVocabulary("再来", "zai lai", "and also have", "Adds another item to the order.", 9, 11),
                            seedVocabulary("一杯", "yi bei", "one cup", "Measure phrase for drinks.", 11, 13),
                            seedVocabulary("热茶", "re cha", "hot tea", "Tea served warm.", 13, 15)
                        )
                    ),
                    SeedLine(
                        speakerName = "服务员",
                        hanziText = "好的，要不要再加一个小菜？",
                        pinyinText = "Hao de, yao bu yao zai jia yi ge xiao cai?",
                        englishTranslation = "Okay. Would you like to add a side dish?",
                        vocabularyItems = listOf(
                            seedVocabulary("好的", "hao de", "okay", "Confirms the order politely.", 0, 2),
                            seedVocabulary("要不要", "yao bu yao", "would you like", "Common pattern used to offer something.", 3, 6),
                            seedVocabulary("再加", "zai jia", "add another", "Suggests adding one more item.", 6, 8),
                            seedVocabulary("一个", "yi ge", "one", "A general measure phrase.", 8, 10),
                            seedVocabulary("小菜", "xiao cai", "side dish", "A small extra dish served with the meal.", 10, 12)
                        )
                    ),
                    SeedLine(
                        speakerName = "顾客",
                        hanziText = "好啊，那就来一盘拍黄瓜。",
                        pinyinText = "Hao a, na jiu lai yi pan pai huang gua.",
                        englishTranslation = "Sure, then let's have one plate of smashed cucumber salad.",
                        vocabularyItems = listOf(
                            seedVocabulary("好啊", "hao a", "sure", "A relaxed way to agree.", 0, 2),
                            seedVocabulary("那就", "na jiu", "in that case", "Used to move naturally to a decision.", 3, 5),
                            seedVocabulary("来", "lai", "to have / bring", "Used when placing an order.", 5, 6),
                            seedVocabulary("一盘", "yi pan", "one plate", "Measure phrase for a plate of food.", 6, 8),
                            seedVocabulary("拍黄瓜", "pai huang gua", "smashed cucumber salad", "A common cold side dish.", 8, 11)
                        )
                    ),
                    SeedLine(
                        speakerName = "服务员",
                        hanziText = "没问题，面和茶马上给您送来。",
                        pinyinText = "Mei wen ti, mian he cha ma shang gei nin song lai.",
                        englishTranslation = "No problem. We'll bring your noodles and tea right away.",
                        vocabularyItems = listOf(
                            seedVocabulary("没问题", "mei wen ti", "no problem", "A reassuring response to the request.", 0, 3),
                            seedVocabulary("面", "mian", "noodles", "Refers to the noodle dish in the order.", 4, 5),
                            seedVocabulary("和", "he", "and", "Connects the two ordered items.", 5, 6),
                            seedVocabulary("茶", "cha", "tea", "The drink in the order.", 6, 7),
                            seedVocabulary("马上", "ma shang", "right away", "Indicates something will happen soon.", 7, 9),
                            seedVocabulary("给您", "gei nin", "for you", "Polite way to refer to the customer.", 9, 11),
                            seedVocabulary("送来", "song lai", "bring over", "To deliver something to the table.", 11, 13)
                        )
                    ),
                    SeedLine(
                        speakerName = "顾客",
                        hanziText = "谢谢。",
                        pinyinText = "Xie xie.",
                        englishTranslation = "Thank you.",
                        vocabularyItems = listOf(
                            seedVocabulary("谢谢", "xie xie", "thank you", "A standard expression of thanks.", 0, 2)
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
                        hanziText = "你是第一次来上海吗？",
                        pinyinText = "Ni shi di yi ci lai Shanghai ma?",
                        englishTranslation = "Is this your first time coming to Shanghai?",
                        vocabularyItems = listOf(
                            seedVocabulary("你", "ni", "you", "Refers to the passenger.", 0, 1),
                            seedVocabulary("是", "shi", "to be", "Links the subject with the description.", 1, 2),
                            seedVocabulary("第一次", "di yi ci", "the first time", "Indicates first experience.", 2, 5),
                            seedVocabulary("来", "lai", "to come", "Used for arriving in a place.", 5, 6),
                            seedVocabulary("上海", "Shanghai", "Shanghai", "The city being discussed.", 6, 8),
                            seedVocabulary("吗", "ma", "question particle", "Turns the sentence into a yes-or-no question.", 8, 9)
                        )
                    ),
                    SeedLine(
                        speakerName = "乘客",
                        hanziText = "不是，我去年也来过，不过这次是来出差的。",
                        pinyinText = "Bu shi, wo qu nian ye lai guo, bu guo zhe ci shi lai chu chai de.",
                        englishTranslation = "No, I came last year too, but this time I'm here on a business trip.",
                        vocabularyItems = listOf(
                            seedVocabulary("不是", "bu shi", "no / not", "Used to deny the driver's assumption.", 0, 2),
                            seedVocabulary("我", "wo", "I", "Refers to the speaker.", 3, 4),
                            seedVocabulary("去年", "qu nian", "last year", "Indicates when the previous visit happened.", 4, 6),
                            seedVocabulary("也", "ye", "also", "Adds that the visit happened too.", 6, 7),
                            seedVocabulary("来过", "lai guo", "have come before", "Shows prior experience visiting.", 7, 9),
                            seedVocabulary("不过", "bu guo", "however", "Introduces a contrast.", 10, 12),
                            seedVocabulary("这次", "zhe ci", "this time", "Refers to the current visit.", 12, 14),
                            seedVocabulary("是", "shi", "to be", "Links the trip with its purpose.", 14, 15),
                            seedVocabulary("来", "lai", "to come", "Used with the purpose of the trip.", 15, 16),
                            seedVocabulary("出差", "chu chai", "to travel on business", "Means to be on a business trip.", 16, 18),
                            seedVocabulary("的", "de", "nominal particle", "Marks the explanation of the current trip.", 18, 19)
                        )
                    ),
                    SeedLine(
                        speakerName = "司机",
                        hanziText = "上海变化很快，这几年新开的店特别多。",
                        pinyinText = "Shanghai bian hua hen kuai, zhe ji nian xin kai de dian te bie duo.",
                        englishTranslation = "Shanghai changes quickly. In the past few years, many newly opened shops have appeared.",
                        vocabularyItems = listOf(
                            seedVocabulary("上海", "Shanghai", "Shanghai", "The city being described.", 0, 2),
                            seedVocabulary("变化", "bian hua", "changes", "Refers to developments in the city.", 2, 4),
                            seedVocabulary("很快", "hen kuai", "very quickly", "Describes the speed of change.", 4, 6),
                            seedVocabulary("这几年", "zhe ji nian", "these past few years", "A recent time period.", 7, 10),
                            seedVocabulary("新开", "xin kai", "newly opened", "Describes businesses that opened recently.", 10, 12),
                            seedVocabulary("的", "de", "modifier particle", "Links the description to the noun that follows.", 12, 13),
                            seedVocabulary("店", "dian", "shops", "Refers to stores or businesses.", 13, 14),
                            seedVocabulary("特别", "te bie", "especially", "Adds emphasis.", 14, 16),
                            seedVocabulary("多", "duo", "many", "Describes large quantity.", 16, 17)
                        )
                    ),
                    SeedLine(
                        speakerName = "乘客",
                        hanziText = "我已经发现了，路上高楼比以前多了很多。",
                        pinyinText = "Wo yi jing fa xian le, lu shang gao lou bi yi qian duo le hen duo.",
                        englishTranslation = "I've already noticed that there are many more tall buildings than before.",
                        vocabularyItems = listOf(
                            seedVocabulary("我", "wo", "I", "Refers to the speaker.", 0, 1),
                            seedVocabulary("已经", "yi jing", "already", "Shows the noticing happened by now.", 1, 3),
                            seedVocabulary("发现", "fa xian", "notice", "To become aware of something.", 3, 5),
                            seedVocabulary("了", "le", "aspect particle", "Marks a realized change or observation.", 5, 6),
                            seedVocabulary("路上", "lu shang", "on the road", "Refers to what the passenger sees outside.", 7, 9),
                            seedVocabulary("高楼", "gao lou", "tall buildings", "High-rise buildings.", 9, 11),
                            seedVocabulary("比", "bi", "compared with", "Introduces a comparison.", 11, 12),
                            seedVocabulary("以前", "yi qian", "before", "Refers to the past.", 12, 14),
                            seedVocabulary("多了", "duo le", "more than before", "Indicates an increase.", 14, 16),
                            seedVocabulary("很多", "hen duo", "a lot", "Adds emphasis to the increase.", 16, 18)
                        )
                    ),
                    SeedLine(
                        speakerName = "司机",
                        hanziText = "这几天打算去哪里逛逛？",
                        pinyinText = "Zhe ji tian da suan qu na li guang guang?",
                        englishTranslation = "Where are you planning to go look around these next few days?",
                        vocabularyItems = listOf(
                            seedVocabulary("这几天", "zhe ji tian", "these next few days", "Refers to the time during the current stay.", 0, 3),
                            seedVocabulary("打算", "da suan", "plan to", "Asks about the passenger's intentions.", 3, 5),
                            seedVocabulary("去", "qu", "to go", "Movement toward a place.", 5, 6),
                            seedVocabulary("哪里", "na li", "where", "Asks for a location.", 6, 8),
                            seedVocabulary("逛逛", "guang guang", "walk around / browse", "A casual expression for sightseeing or strolling.", 8, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "乘客",
                        hanziText = "我想先去外滩，再去城隍庙吃小吃。",
                        pinyinText = "Wo xiang xian qu Waitan, zai qu Chenghuangmiao chi xiao chi.",
                        englishTranslation = "I want to go to the Bund first, then head to City God Temple for snacks.",
                        vocabularyItems = listOf(
                            seedVocabulary("我想", "wo xiang", "I want to", "Introduces the travel plan politely.", 0, 2),
                            seedVocabulary("先", "xian", "first", "Shows sequence.", 2, 3),
                            seedVocabulary("去", "qu", "to go", "Movement to a destination.", 3, 4),
                            seedVocabulary("外滩", "Waitan", "the Bund", "A famous Shanghai waterfront area.", 4, 6),
                            seedVocabulary("再", "zai", "then", "Introduces the next step.", 7, 8),
                            seedVocabulary("去", "qu", "to go", "Movement to another destination.", 8, 9),
                            seedVocabulary("城隍庙", "Chenghuangmiao", "City God Temple", "A well-known tourist area in Shanghai.", 9, 12),
                            seedVocabulary("吃", "chi", "to eat", "Used when talking about food plans.", 12, 13),
                            seedVocabulary("小吃", "xiao chi", "snacks", "Local street food or small dishes.", 13, 15)
                        )
                    ),
                    SeedLine(
                        speakerName = "司机",
                        hanziText = "那不错，晚上浦东的夜景也值得看看。",
                        pinyinText = "Na bu cuo, wan shang Pudong de ye jing ye zhi de kan kan.",
                        englishTranslation = "That sounds good. Pudong's night view is also worth seeing in the evening.",
                        vocabularyItems = listOf(
                            seedVocabulary("那", "na", "that", "Refers to the passenger's plan.", 0, 1),
                            seedVocabulary("不错", "bu cuo", "not bad", "A natural way to say something sounds good.", 1, 3),
                            seedVocabulary("晚上", "wan shang", "evening", "Suggests a time to visit.", 4, 6),
                            seedVocabulary("浦东", "Pudong", "Pudong", "The district famous for its skyline.", 6, 8),
                            seedVocabulary("的", "de", "modifier particle", "Links Pudong with the night view.", 8, 9),
                            seedVocabulary("夜景", "ye jing", "night view", "Refers to the city lights and skyline.", 9, 11),
                            seedVocabulary("也", "ye", "also", "Adds another recommendation.", 11, 12),
                            seedVocabulary("值得", "zhi de", "worth", "Indicates something is worthwhile.", 12, 14),
                            seedVocabulary("看看", "kan kan", "take a look", "A casual way to suggest seeing something.", 14, 16)
                        )
                    ),
                    SeedLine(
                        speakerName = "乘客",
                        hanziText = "听起来很好，我希望这次能多待两天。",
                        pinyinText = "Ting qi lai hen hao, wo xi wang zhe ci neng duo dai liang tian.",
                        englishTranslation = "That sounds great. I hope I can stay two more days this time.",
                        vocabularyItems = listOf(
                            seedVocabulary("听起来", "ting qi lai", "sounds", "Used to react to a suggestion or description.", 0, 3),
                            seedVocabulary("很好", "hen hao", "very good", "Expresses approval.", 3, 5),
                            seedVocabulary("我", "wo", "I", "Refers to the speaker.", 6, 7),
                            seedVocabulary("希望", "xi wang", "hope", "Expresses a wish.", 7, 9),
                            seedVocabulary("这次", "zhe ci", "this time", "Refers to the current trip.", 9, 11),
                            seedVocabulary("能", "neng", "can / be able to", "Indicates possibility.", 11, 12),
                            seedVocabulary("多待", "duo dai", "stay a bit longer", "To remain for extra time.", 12, 14),
                            seedVocabulary("两天", "liang tian", "two days", "The extra amount of time desired.", 14, 16)
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
                        hanziText = "这个周末你有什么安排？",
                        pinyinText = "Zhe ge zhou mo ni you shen me an pai?",
                        englishTranslation = "What plans do you have for this weekend?",
                        vocabularyItems = listOf(
                            seedVocabulary("这个周末", "zhe ge zhou mo", "this weekend", "Refers to the coming weekend.", 0, 4),
                            seedVocabulary("你", "ni", "you", "Addresses the colleague.", 4, 5),
                            seedVocabulary("有什么", "you shen me", "what do you have", "Asks what plans exist.", 5, 8),
                            seedVocabulary("安排", "an pai", "plans / arrangements", "Refers to scheduled activities.", 8, 10)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事乙",
                        hanziText = "我打算先睡个懒觉，然后下午去看电影。",
                        pinyinText = "Wo da suan xian shui ge lan jiao, ran hou xia wu qu kan dian ying.",
                        englishTranslation = "I'm planning to sleep in first, then watch a movie in the afternoon.",
                        vocabularyItems = listOf(
                            seedVocabulary("我", "wo", "I", "Refers to the speaker.", 0, 1),
                            seedVocabulary("打算", "da suan", "plan to", "Introduces the weekend plan.", 1, 3),
                            seedVocabulary("先", "xian", "first", "Shows what comes first.", 3, 4),
                            seedVocabulary("睡个懒觉", "shui ge lan jiao", "sleep in", "Means to sleep longer than usual.", 4, 8),
                            seedVocabulary("然后", "ran hou", "then", "Connects the next action.", 9, 11),
                            seedVocabulary("下午", "xia wu", "afternoon", "Specifies when the next plan happens.", 11, 13),
                            seedVocabulary("去", "qu", "to go", "Used before the activity destination.", 13, 14),
                            seedVocabulary("看电影", "kan dian ying", "watch a movie", "A common leisure activity.", 14, 17)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事甲",
                        hanziText = "听起来不错，你要看哪一部？",
                        pinyinText = "Ting qi lai bu cuo, ni yao kan na yi bu?",
                        englishTranslation = "That sounds good. Which movie do you want to see?",
                        vocabularyItems = listOf(
                            seedVocabulary("听起来", "ting qi lai", "sounds", "Used to react to a plan or idea.", 0, 3),
                            seedVocabulary("不错", "bu cuo", "not bad", "A natural positive reaction.", 3, 5),
                            seedVocabulary("你", "ni", "you", "Addresses the colleague.", 6, 7),
                            seedVocabulary("要", "yao", "want to", "Expresses intent.", 7, 8),
                            seedVocabulary("看", "kan", "watch", "Used for films and shows.", 8, 9),
                            seedVocabulary("哪一部", "na yi bu", "which one", "Asks which specific movie.", 9, 12)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事乙",
                        hanziText = "我还没决定，可能会看那部新上映的喜剧片。",
                        pinyinText = "Wo hai mei jue ding, ke neng hui kan na bu xin shang ying de xi ju pian.",
                        englishTranslation = "I haven't decided yet. I might watch that newly released comedy.",
                        vocabularyItems = listOf(
                            seedVocabulary("我", "wo", "I", "Refers to the speaker.", 0, 1),
                            seedVocabulary("还没", "hai mei", "not yet", "Shows the decision has not been made so far.", 1, 3),
                            seedVocabulary("决定", "jue ding", "decide", "To make a choice.", 3, 5),
                            seedVocabulary("可能", "ke neng", "maybe / possibly", "Expresses uncertainty.", 6, 8),
                            seedVocabulary("会", "hui", "will / might", "Marks a likely future action.", 8, 9),
                            seedVocabulary("看", "kan", "watch", "Used for viewing a film.", 9, 10),
                            seedVocabulary("那部", "na bu", "that one", "Refers to a specific movie.", 10, 12),
                            seedVocabulary("新上映", "xin shang ying", "newly released", "Describes a film that just came out.", 12, 15),
                            seedVocabulary("的", "de", "modifier particle", "Links the description to the movie type.", 15, 16),
                            seedVocabulary("喜剧片", "xi ju pian", "comedy film", "The kind of movie being considered.", 16, 19)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事甲",
                        hanziText = "如果你愿意，我也可以一起去。",
                        pinyinText = "Ru guo ni yuan yi, wo ye ke yi yi qi qu.",
                        englishTranslation = "If you'd like, I can go with you too.",
                        vocabularyItems = listOf(
                            seedVocabulary("如果", "ru guo", "if", "Introduces a condition.", 0, 2),
                            seedVocabulary("你", "ni", "you", "Addresses the colleague.", 2, 3),
                            seedVocabulary("愿意", "yuan yi", "be willing", "Checks whether the other person is open to the idea.", 3, 5),
                            seedVocabulary("我", "wo", "I", "Refers to the speaker.", 6, 7),
                            seedVocabulary("也", "ye", "also", "Adds the speaker to the plan.", 7, 8),
                            seedVocabulary("可以", "ke yi", "can", "Shows possibility or willingness.", 8, 10),
                            seedVocabulary("一起", "yi qi", "together", "Means doing something with another person.", 10, 12),
                            seedVocabulary("去", "qu", "go", "Used for joining the outing.", 12, 13)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事乙",
                        hanziText = "好啊，看完电影我们还可以去吃火锅。",
                        pinyinText = "Hao a, kan wan dian ying wo men hai ke yi qu chi huo guo.",
                        englishTranslation = "Sure. After the movie, we can also go eat hot pot.",
                        vocabularyItems = listOf(
                            seedVocabulary("好啊", "hao a", "sure", "A relaxed way to agree.", 0, 2),
                            seedVocabulary("看完", "kan wan", "after finishing watching", "Indicates an action completed first.", 3, 5),
                            seedVocabulary("电影", "dian ying", "movie", "Refers to the film they plan to watch.", 5, 7),
                            seedVocabulary("我们", "wo men", "we", "Refers to both colleagues together.", 7, 9),
                            seedVocabulary("还", "hai", "also / still", "Adds another possible plan.", 9, 10),
                            seedVocabulary("可以", "ke yi", "can", "Shows another possible activity.", 10, 12),
                            seedVocabulary("去", "qu", "go", "Movement to the next activity.", 12, 13),
                            seedVocabulary("吃", "chi", "eat", "Used with food plans.", 13, 14),
                            seedVocabulary("火锅", "huo guo", "hot pot", "A popular shared meal.", 14, 16)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事甲",
                        hanziText = "没问题，我正好想吃点辣的。",
                        pinyinText = "Mei wen ti, wo zheng hao xiang chi dian la de.",
                        englishTranslation = "No problem. I happen to be craving something spicy.",
                        vocabularyItems = listOf(
                            seedVocabulary("没问题", "mei wen ti", "no problem", "A natural way to agree to the plan.", 0, 3),
                            seedVocabulary("我", "wo", "I", "Refers to the speaker.", 4, 5),
                            seedVocabulary("正好", "zheng hao", "just happen to", "Shows convenient timing.", 5, 7),
                            seedVocabulary("想", "xiang", "want", "Expresses desire.", 7, 8),
                            seedVocabulary("吃点", "chi dian", "eat some", "A casual way to talk about eating a bit of something.", 8, 10),
                            seedVocabulary("辣的", "la de", "something spicy", "Refers to spicy food.", 10, 12)
                        )
                    ),
                    SeedLine(
                        speakerName = "同事乙",
                        hanziText = "那我们周六下午联系，到时候再定时间。",
                        pinyinText = "Na wo men Zhou liu xia wu lian xi, dao shi hou zai ding shi jian.",
                        englishTranslation = "Then let's get in touch Saturday afternoon and decide the time then.",
                        vocabularyItems = listOf(
                            seedVocabulary("那", "na", "then", "Moves the conversation to the next step.", 0, 1),
                            seedVocabulary("我们", "wo men", "we", "Refers to both colleagues.", 1, 3),
                            seedVocabulary("周六", "Zhou liu", "Saturday", "Specifies the day.", 3, 5),
                            seedVocabulary("下午", "xia wu", "afternoon", "Specifies the time of day.", 5, 7),
                            seedVocabulary("联系", "lian xi", "get in touch", "Means to contact each other.", 7, 9),
                            seedVocabulary("到时候", "dao shi hou", "at that time", "Refers to when they reconnect later.", 10, 13),
                            seedVocabulary("再", "zai", "then / again", "Indicates the next step will happen later.", 13, 14),
                            seedVocabulary("定时间", "ding shi jian", "set the time", "Means to finalize the exact time.", 14, 17)
                        )
                    )
                )
            )
        )

    private fun seedVocabulary(
        expression: String,
        pinyin: String,
        gloss: String,
        explanation: String,
        startCharIndex: Int,
        endCharIndex: Int
    ): SeedVocabularyItem =
        SeedVocabularyItem(
            expression = expression,
            pinyin = pinyin,
            gloss = gloss,
            explanation = explanation,
            startCharIndex = startCharIndex,
            endCharIndex = endCharIndex
        )

    private data class SeedScenario(
        val title: String,
        val description: String,
        val topic: ScenarioTopic,
        val lines: List<SeedLine>
    )

    private data class SeedLine(
        val speakerName: String,
        val hanziText: String,
        val pinyinText: String,
        val englishTranslation: String,
        val vocabularyItems: List<SeedVocabularyItem>
    )

    private data class SeedVocabularyItem(
        val expression: String,
        val pinyin: String,
        val gloss: String,
        val explanation: String,
        val startCharIndex: Int,
        val endCharIndex: Int
    )
}

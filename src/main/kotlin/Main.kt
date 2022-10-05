import com.mrx.XLog
import com.mrx.bot.Bot

object Main {

    private val logger = XLog.getLogger(this::class)

    @JvmStatic
    fun main(args: Array<String>) {
        Bot("Test").chat()
//        BotTrainer.trainCorpusFile2(
//            "E:\\Jet Brains\\IntelliJ IDEA\\YAMLChatBot\\src\\main\\resources\\xiaohuangji.tsv"
//        )
    }
}
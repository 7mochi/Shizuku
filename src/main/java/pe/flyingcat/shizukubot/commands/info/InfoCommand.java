package pe.flyingcat.shizukubot.commands.info;

import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pe.flyingcat.shizukubot.commands.Command;
import pe.flyingcat.shizukubot.util.ApplicationInfo;

/**
 *
 * @author FlyingCat
 */
public class InfoCommand extends Command {

    private final static String MSG = "```asciidoc\n"
            + "= " + ApplicationInfo.APP + " =\n"
            + "* " + multiLang.getMessage("APP_INFO_COMM_MSG_1") + "\n"
            + "- " + multiLang.getMessage("APP_INFO_COMM_MSG_2") + ": FlyingCat\n"
            + "- ID: 123933139449020416\n"
            + "\n"
            + "* " + multiLang.getMessage("APP_INFO_COMM_MSG_3") + "\n"
            + "- " + multiLang.getMessage("APP_INFO_COMM_MSG_4") + ": Java 12.0.1\n"
            + "- " + multiLang.getMessage("APP_INFO_COMM_MSG_5") + ": JDA - v" + JDAInfo.VERSION + "\n"
            + "```";

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        MessageBuilder builder = new MessageBuilder();
        builder.append(MSG);
        sendMessage(e, builder.build());
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("sh!info");
    }

    @Override
    public String getDescription() {
        return multiLang.getMessage("APP_INFO_COMM_DESC");
    }

    @Override
    public String getName() {
        return multiLang.getMessage("APP_INFO_COMM_NAME");
    }

    @Override
    public List<String> getUsageInstructions() {
        return Arrays.asList(multiLang.getMessage("APP_INFO_COMM_USAGE"));
    }
}

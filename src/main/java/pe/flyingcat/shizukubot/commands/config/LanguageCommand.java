package pe.flyingcat.shizukubot.commands.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pe.flyingcat.shizukubot.beans.Settings;
import pe.flyingcat.shizukubot.commands.Command;
import pe.flyingcat.shizukubot.settings.SettingsManager;
import pe.flyingcat.shizukubot.util.RegExp;

/**
 *
 * @author FlyingCat
 */
public class LanguageCommand extends Command {

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        if (!e.isFromType(ChannelType.PRIVATE) && args.length < 2) {
            e.getTextChannel().sendMessage(new MessageBuilder()
                    .append(e.getAuthor())
                    .append(multiLang.getMessage("APP_HELP_COMM"))
                    .build()).queue();
        }
        sendPrivate(e, e.getAuthor().openPrivateChannel().complete(), args);
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("sh!language", "sh!lang");
    }

    @Override
    public String getDescription() {
        return multiLang.getMessage("APP_LANG_COMM_DESC");
    }

    @Override
    public String getName() {
        return multiLang.getMessage("APP_LANG_COMM_NAME");
    }

    @Override
    public List<String> getUsageInstructions() {
        return Collections.singletonList(multiLang.getMessage("APP_LANG_COMM_USAGE"));
    }

    private void sendPrivate(MessageReceivedEvent e, PrivateChannel channel, String[] args) {
        if (args.length < 2) {
            StringBuilder s = new StringBuilder();
            multiLang.getMessagesAvailables().forEach((l) -> {
                s.append("**").append(l).append("** - ").append(multiLang.toLongName(l));
                s.append("\n");
            });
            channel.sendMessage(new MessageBuilder()
                    .append(multiLang.getMessage("APP_LANG_COMM_MSGPR") + "\n")
                    .append(s.toString())
                    .build()).queue();
        } else {
            String argsStr = Arrays.toString(args);
            argsStr = argsStr.substring(1, argsStr.length() - 1).replace(",", "");
            Matcher matcher = Pattern.compile(RegExp.TEXT_BETWEEN_QUOTES).matcher(argsStr);
            // Check if the language is written between quotes
            if (!matcher.find()) {
                MessageBuilder builder = new MessageBuilder();
                builder.append(e.getAuthor());
                builder.append(multiLang.getMessage("APP_LANG_COMM_QUOTES"));
                sendMessage(e, builder.build());
            } else {
                Member author = e.getMessage().getMember();
                if (author.hasPermission(Permission.ADMINISTRATOR)) {
                    Settings settingsBean = SettingsManager.getInstance().getSettings();
                    if (multiLang.isRegistered(matcher.group(1))) {
                        settingsBean.setLanguage(matcher.group(1));
                        multiLang.setLang(new Locale(matcher.group(1)));
                        SettingsManager.getInstance().updateSettings(settings);
                        MessageBuilder builder = new MessageBuilder();
                        builder.append(e.getAuthor());
                        builder.append(multiLang.getMessage("APP_LANG_COMM_SUCCESS_1"))
                                .append(multiLang.toLongName(matcher.group(1)))
                                .append("**");
                        sendMessage(e, builder.build());
                    } else {
                        MessageBuilder builder = new MessageBuilder();
                        builder.append(e.getAuthor());
                        builder.append(multiLang.getMessage("APP_LANG_COMM_ERROR"));
                        sendMessage(e, builder.build());
                    }
                } else {
                    MessageBuilder builder = new MessageBuilder();
                    builder.append(e.getAuthor());
                    builder.append(multiLang.getMessage("APP_LANG_COMM_CANT"));
                    sendMessage(e, builder.build());
                }
            }
        }
    }
}

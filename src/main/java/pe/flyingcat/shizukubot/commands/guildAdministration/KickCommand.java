package pe.flyingcat.shizukubot.commands.guildAdministration;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import pe.flyingcat.shizukubot.commands.Command;
import pe.flyingcat.shizukubot.util.RegExp;

/**
 *
 * @author FlyingCat
 */
public class KickCommand extends Command {

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        // 3 parameters, command with prefix, user and reason
        // Ex: sh!kick @FlyingCat#8108 "n word"
        if (!e.isFromType(ChannelType.PRIVATE)) {
            String argsStr = Arrays.toString(args);
            argsStr = argsStr.substring(1, argsStr.length() - 1).replace(",", "");
            Matcher matcher = Pattern.compile(RegExp.TEXT_BETWEEN_QUOTES).matcher(argsStr);
            // Check if the reason is written between quotes
            if (!matcher.find()) {
                MessageBuilder builder = new MessageBuilder();
                builder.append(e.getAuthor());
                builder.append(multiLang.getMessage("APP_KICK_COMM_QUOTES"));
                sendMessage(e, builder.build());
            } else {
                Member author = e.getMessage().getMember();
                if (author.hasPermission(Permission.KICK_MEMBERS)) {
                    List<Member> members = e.getMessage().getMentionedMembers();
                    // Author have permission but didn't tag user
                    if (members.isEmpty()) {
                        MessageBuilder builder = new MessageBuilder();
                        builder.append(e.getAuthor());
                        builder.append(multiLang.getMessage("APP_KICK_COMM_EMPTY"));
                        sendMessage(e, builder.build());
                    } else {
                        // Author tried to kick himself
                        if (e.getAuthor() == members.get(0)) {
                            MessageBuilder builder = new MessageBuilder();
                            builder.append(e.getAuthor());
                            builder.append(multiLang.getMessage("APP_KICK_COMM_AUTOKICK"));
                            sendMessage(e, builder.build());
                        } else {
                            Guild guild = e.getGuild();
                            try {
                                guild.kick(members.get(0)).queue();
                                MessageBuilder builder = new MessageBuilder();
                                builder.append(e.getAuthor());
                                builder.append(multiLang.getMessage("APP_KICK_COMM_SUCCESS_1"));
                                builder.append(members.get(0).getUser().getName());
                                builder.append(multiLang.getMessage("APP_KICK_COMM_SUCCESS_2"));
                                sendMessage(e, builder.build());
                            } catch (HierarchyException ex) {
                                MessageBuilder builder = new MessageBuilder();
                                builder.append(e.getAuthor());
                                builder.append(multiLang.getMessage("APP_KICK_COMM_HIGHER_ROL"));
                                sendMessage(e, builder.build());
                            }
                        }
                    }
                } else {
                    List<Member> members = e.getMessage().getMentionedMembers();
                    // Author don't have permission and didn't tag user
                    if (members.isEmpty()) {
                        MessageBuilder builder = new MessageBuilder();
                        builder.append(e.getAuthor());
                        builder.append(multiLang.getMessage("APP_KICK_COMM_EMPTY"));
                        sendMessage(e, builder.build());
                    } else { // Author don't have permission
                        MessageBuilder builder = new MessageBuilder();
                        builder.append(e.getAuthor());
                        builder.append(multiLang.getMessage("APP_KICK_COMM_CANT"));
                        sendMessage(e, builder.build());
                    }
                }
            }
        }
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("sh!kick");
    }

    @Override
    public String getDescription() {
        return multiLang.getMessage("APP_KICK_COMM_DESC");
    }

    @Override
    public String getName() {
        return multiLang.getMessage("APP_KICK_COMM_NAME");
    }

    @Override
    public List<String> getUsageInstructions() {
        return Arrays.asList(multiLang.getMessage("APP_KICK_COMM_USAGE"));
    }
}

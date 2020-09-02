package pe.flyingcat.shizukubot.commands.guildAdministration;

import java.util.ArrayList;
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
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import pe.flyingcat.shizukubot.commands.Command;
import pe.flyingcat.shizukubot.util.RegExp;

/**
 *
 * @author FlyingCat
 */
public class BanCommand extends Command {

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        /* 4 parameters:
         * - Command with prefix
         * - User
         * - Delete messages from last x days ago (min = 0, max = 7)
         * - Reason
         * 
         * Ex's:
         * sh!ban @FlyingCat#8108 "0" "n word"
         * sh!ban @FlyingCat#8108 "1" "n word"
         * sh!ban @FlyingCat#8108 "7" "n word"
         */
        if (!e.isFromType(ChannelType.PRIVATE)) {
            int deleteMessagesDay = -1;
            String reason = "";
            String argsStr = Arrays.toString(args);
            argsStr = argsStr.substring(1, argsStr.length() - 1).replace(",", "");
            Matcher matcher = Pattern.compile(RegExp.TEXT_BETWEEN_QUOTES).matcher(argsStr);
            // Check if the deleteMessagesDay and reason is written between quotes
            if (!matcher.find()) {
                MessageBuilder builder = new MessageBuilder();
                builder.append(e.getAuthor());
                builder.append(multiLang.getMessage("APP_BAN_COMM_QUOTES"));
                sendMessage(e, builder.build());
            } else {
                Member author = e.getMessage().getMember();
                List<Member> members = e.getMessage().getMentionedMembers();
                // Author didn't tag user
                if (members.isEmpty()) {
                    MessageBuilder builder = new MessageBuilder();
                    builder.append(e.getAuthor());
                    builder.append(multiLang.getMessage("APP_BAN_COMM_EMPTY"));
                    sendMessage(e, builder.build());
                } else {
                    if (author.hasPermission(Permission.BAN_MEMBERS)) {
                        matcher = Pattern.compile(RegExp.TEXT_BETWEEN_QUOTES).matcher(argsStr);
                        List<String> allMatches = new ArrayList<>();
                        while (matcher.find()) {
                            allMatches.add(matcher.group(1));
                        }
                        // Get only 2 first parameters with quotes
                        try {
                            deleteMessagesDay = Integer.parseInt(allMatches.get(0));
                            reason = allMatches.get(1);
                            // Author tried to ban himself
                            if (e.getAuthor() == members.get(0)) {
                                MessageBuilder builder = new MessageBuilder();
                                builder.append(e.getAuthor());
                                builder.append(multiLang.getMessage("APP_BAN_COMM_AUTOBAN"));
                                sendMessage(e, builder.build());
                            } else {
                                Guild guild = e.getGuild();
                                try {
                                    guild.ban(members.get(0), deleteMessagesDay, reason)
                                            .queue();
                                    MessageBuilder builder = new MessageBuilder();
                                    builder.append(e.getAuthor());
                                    builder.append(multiLang.getMessage("APP_BAN_COMM_SUCCESS_1"));
                                    builder.append(members.get(0).getUser().getName());
                                    builder.append(multiLang.getMessage("APP_BAN_COMM_SUCCESS_2"));
                                    sendMessage(e, builder.build());
                                } catch (Exception ex) {
                                    MessageBuilder builder = new MessageBuilder();
                                    builder.append(e.getAuthor());
                                    if (ex instanceof HierarchyException) {
                                        builder.append(multiLang.getMessage("APP_BAN_COMM_HIGHER_ROL"));
                                    } else if (ex instanceof InsufficientPermissionException) {
                                        builder.append(multiLang.getMessage("APP_BAN_COMM_INSUFF_PERMS"));
                                    }
                                    sendMessage(e, builder.build());
                                }
                            }
                        } catch (NumberFormatException | IndexOutOfBoundsException numEx) {
                            MessageBuilder builder = new MessageBuilder();
                            builder.append(e.getAuthor());
                            builder.append(multiLang.getMessage("APP_BAN_COMM_DMD_ERR"));
                            sendMessage(e, builder.build());
                        }
                    } else {
                        // Author doesn't have permission 
                        MessageBuilder builder = new MessageBuilder();
                        builder.append(e.getAuthor());
                        builder.append(multiLang.getMessage("APP_BAN_COMM_CANT"));
                        sendMessage(e, builder.build());
                    }
                }
            }
        }
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("sh!ban");
    }

    @Override
    public String getDescription() {
        return multiLang.getMessage("APP_BAN_COMM_DESC");
    }

    @Override
    public String getName() {
        return multiLang.getMessage("APP_BAN_COMM_NAME");
    }

    @Override
    public List<String> getUsageInstructions() {
        return Arrays.asList(multiLang.getMessage("APP_BAN_COMM_USAGE"));
    }
}

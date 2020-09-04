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
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import pe.flyingcat.shizukubot.commands.Command;
import pe.flyingcat.shizukubot.util.RegExp;

/**
 *
 * @author FlyingCat
 */
public class BulkUnmuteCommand extends Command {

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        // 2 parameters, command with prefix and voice channel name
        // Ex's:
        // - sh!bulkunmute "General #1"
        // - sh!bunm "Gaming"
        if (!e.isFromType(ChannelType.PRIVATE)) {
            String argsStr = Arrays.toString(args);
            argsStr = argsStr.substring(1, argsStr.length() - 1).replace(",", "");
            Matcher matcher = Pattern.compile(RegExp.TEXT_BETWEEN_QUOTES).matcher(argsStr);
            // Check if channel's name is written between quotes
            if (!matcher.find()) {
                MessageBuilder builder = new MessageBuilder();
                builder.append(e.getAuthor());
                builder.append(multiLang.getMessage("APP_BULKUNMUTE_COMM_QUOTES"));
                sendMessage(e, builder.build());
            } else {
                Member author = e.getMessage().getMember();
                if (author.hasPermission(Permission.VOICE_MUTE_OTHERS)) {
                    Guild guild = e.getGuild();
                    List<VoiceChannel> voiceChannels = guild.getVoiceChannels();
                    VoiceChannel voice = voiceChannels.stream()
                            .filter(c -> c.getName().equals(matcher.group(1)))
                            .findFirst()
                            .orElse(null);
                    if (voice == null) {
                        MessageBuilder builder = new MessageBuilder();
                        builder.append(e.getAuthor());
                        builder.append(multiLang.getMessage("APP_BULKUNMUTE_COMM_NO_EXIST_1"));
                        builder.append(matcher.group(1));
                        builder.append(multiLang.getMessage("APP_BULKUNMUTE_COMM_NO_EXIST_2"));
                        sendMessage(e, builder.build());
                    } else {
                        List<Member> membersInVoice = voice.getMembers();
                        for (Member member : membersInVoice) {
                            try {
                                guild.mute(member, false).queue();
                            } catch (Exception ex) {
                                MessageBuilder builder = new MessageBuilder();
                                builder.append(e.getAuthor());
                                if (ex instanceof InsufficientPermissionException) {
                                    builder.append(multiLang.getMessage("APP_BULKUNMUTE_COMM_INSUFF_PERMS"));
                                }
                                sendMessage(e, builder.build());
                            }
                        }
                        MessageBuilder builder = new MessageBuilder();
                        builder.append(e.getAuthor());
                        builder.append(multiLang.getMessage("APP_BULKUNMUTE_COMM_SUCCESS_1"));
                        builder.append(matcher.group(1));
                        builder.append(multiLang.getMessage("APP_BULKUNMUTE_COMM_SUCCESS_2"));
                        sendMessage(e, builder.build());
                    }
                } else {
                    // Author doesn't have permission
                    MessageBuilder builder = new MessageBuilder();
                    builder.append(e.getAuthor());
                    builder.append(multiLang.getMessage("APP_BULKUNMUTE_COMM_CANT"));
                    sendMessage(e, builder.build());
                }
            }
        }
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("sh!bulkunmute", "sh!bunm");
    }

    @Override
    public String getDescription() {
        return multiLang.getMessage("APP_BULKUNMUTE_COMM_DESC");
    }

    @Override
    public String getName() {
        return multiLang.getMessage("APP_BULKUNMUTE_COMM_NAME");
    }

    @Override
    public List<String> getUsageInstructions() {
        return Arrays.asList(multiLang.getMessage("APP_BULKUNMUTE_COMM_USAGE"));
    }

}

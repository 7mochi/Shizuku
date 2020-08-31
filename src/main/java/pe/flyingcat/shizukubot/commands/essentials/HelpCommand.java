package pe.flyingcat.shizukubot.commands.essentials;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import pe.flyingcat.shizukubot.commands.Command;

/**
 *
 * @author FlyingCat
 */
public class HelpCommand extends Command {

    private static final String NO_NAME = multiLang.getMessage("APP_COMM_NO_NAME");
    private static final String NO_DESCRIPTION = multiLang.getMessage("APP_COMM_NO_DESC");
    private static final String NO_USAGE = multiLang.getMessage("APP_COMM_NO_USAGE");

    private final TreeMap<String, Command> commands;

    public HelpCommand() {
        commands = new TreeMap<>();
    }

    public Command registerCommand(Command command) {
        commands.put(command.getAliases().get(0), command);
        return command;
    }

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        if (!e.isFromType(ChannelType.PRIVATE)) {
            e.getTextChannel().sendMessage(new MessageBuilder()
                    .append(e.getAuthor())
                    .append(multiLang.getMessage("APP_HELP_COMM"))
                    .build()).queue();
        }
        sendPrivate(e.getAuthor().openPrivateChannel().complete(), args);
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("sh!help", "sh!commands");
    }

    @Override
    public String getDescription() {
        return multiLang.getMessage("APP_HELP_COMM_DESC");
    }

    @Override
    public String getName() {
        return multiLang.getMessage("APP_HELP_COMM_NAME");
    }

    @Override
    public List<String> getUsageInstructions() {
        return Collections.singletonList(multiLang.getMessage("APP_HELP_COMM_USAGE"));
    }

    private void sendPrivate(PrivateChannel channel, String[] args) {
        if (args.length < 2) {
            StringBuilder s = new StringBuilder();
            commands.values().forEach((c) -> {
                String description = c.getDescription();
                description = (description == null || description.isEmpty()) ? NO_DESCRIPTION : description;

                s.append("**").append(c.getAliases().get(0)).append("** - ");
                s.append(description).append("\n");
            });

            channel.sendMessage(new MessageBuilder()
                    .append(multiLang.getMessage("APP_HELP_COMM_MSG_1") + "\n")
                    .append(s.toString())
                    .build()).queue();
        } else {
            String command = args[1].charAt(0) == '.' ? args[1] : "sh!" + args[1];
            for (Command c : commands.values()) {
                if (c.getAliases().contains(command)) {
                    String name = c.getName();
                    String description = c.getDescription();
                    List<String> usageInstructions = c.getUsageInstructions();
                    name = (name == null || name.isEmpty()) ? NO_NAME : name;
                    description = (description == null || description.isEmpty()) ? NO_DESCRIPTION : description;
                    usageInstructions = (usageInstructions == null || usageInstructions.isEmpty()) ? Collections.singletonList(NO_USAGE) : usageInstructions;

                    channel.sendMessage(new MessageBuilder()
                            .append(multiLang.getMessage("APP_HELP_COMM_MSG_2") + name + "\n")
                            .append(multiLang.getMessage("APP_HELP_COMM_MSG_3") + description + "\n")
                            .append(multiLang.getMessage("APP_HELP_COMM_MSG_4") + StringUtils.join(c.getAliases(), ", ") + "\n")
                            .append(multiLang.getMessage("APP_HELP_COMM_MSG_5"))
                            .append(usageInstructions.get(0))
                            .build()).queue();
                    for (int i = 1; i < usageInstructions.size(); i++) {
                        channel.sendMessage(new MessageBuilder()
                                .append("__" + name + " Usage Cont. (" + (i + 1) + ")__\n")
                                .append(usageInstructions.get(i))
                                .build()).queue();
                    }
                    return;
                }
            }
            channel.sendMessage(new MessageBuilder()
                    .append(multiLang.getMessage("APP_HELP_COMM_MSG_6") + args[1] + multiLang.getMessage("APP_HELP_COMM_MSG_7"))
                    .build()).queue();
        }
    }
}

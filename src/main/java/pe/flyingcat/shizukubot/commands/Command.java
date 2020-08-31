package pe.flyingcat.shizukubot.commands;

import java.util.List;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pe.flyingcat.shizukubot.beans.Settings;
import pe.flyingcat.shizukubot.settings.SettingsManager;
import pe.flyingcat.shizukubot.util.Multilanguage;

/**
 *
 * @author FlyingCat
 */
public abstract class Command extends ListenerAdapter {

    protected static final Settings settings = SettingsManager.getInstance().getSettings();
    protected static final Multilanguage multiLang = new Multilanguage(settings.getLanguage());

    public abstract void onCommand(MessageReceivedEvent e, String[] args);

    public abstract List<String> getAliases();

    public abstract String getDescription();

    public abstract String getName();

    public abstract List<String> getUsageInstructions();

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot() && !respondToBots()) {
            return;
        }
        if (containsCommand(e.getMessage())) {
            onCommand(e, commandArgs(e.getMessage()));
        }
    }

    protected boolean containsCommand(Message message) {
        return getAliases().contains(commandArgs(message)[0]);
    }

    protected String[] commandArgs(Message message) {
        return commandArgs(message.getContentDisplay());
    }

    protected String[] commandArgs(String string) {
        return string.split(" ");
    }

    protected Message sendMessage(MessageReceivedEvent e, Message message) {
        if (e.isFromType(ChannelType.PRIVATE)) {
            return e.getPrivateChannel().sendMessage(message).complete();
        } else {
            return e.getTextChannel().sendMessage(message).complete();
        }
    }

    protected Message sendMessage(MessageReceivedEvent e, String message) {
        return sendMessage(e, new MessageBuilder().append(message).build());
    }

    protected boolean respondToBots() {
        return false;
    }
}

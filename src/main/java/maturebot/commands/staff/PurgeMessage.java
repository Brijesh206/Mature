package maturebot.commands.staff;

import maturebot.MatureBot;
import maturebot.commands.Category;
import maturebot.commands.Command;
import maturebot.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

/**
 * Command that deletes number of messages from the current channel.
 *
 * @author Brijesh
 */

public class PurgeMessage extends Command {
    public PurgeMessage(MatureBot bot) {
        super(bot);
        this.name = "purge";
        this.description = "Delete messages from the channel.";
        this.category = Category.STAFF;
        this.args.add(new OptionData(OptionType.INTEGER,"number", "Number of message you want to delete.",true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        try {
            OptionMapping number = event.getOption("number");
            int values = number.getAsInt();

            if(values < 1 || values > 100){
                // Sending Error
                event.replyEmbeds(EmbedUtils.createError("Only 1 to 100 message can be deleted!")).setEphemeral(true).queue();
            }else if(values > 1 && values < 100){
                // Delete number of messages in current channel

                List<Message> messages = event.getChannel().getHistory().retrievePast(values).complete();
                event.getGuild().getTextChannelById(event.getChannel().getIdLong()).deleteMessages(messages).queue();

                event.replyEmbeds(EmbedUtils.createSuccess("Messages deleted!")).setEphemeral(true).queue();
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
}

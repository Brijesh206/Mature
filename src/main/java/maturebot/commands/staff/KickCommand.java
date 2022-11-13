package maturebot.commands.staff;

import maturebot.MatureBot;
import maturebot.commands.Category;
import maturebot.commands.Command;
import maturebot.commands.data.GuildData;
import maturebot.handler.ModerationHandler;
import maturebot.util.embeds.EmbedColor;
import maturebot.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * Command that kicks a user from the guild.
 *
 * @author Brijesh
 */

public class KickCommand extends Command {

    public KickCommand(MatureBot bot) {
        super(bot);
        this.name = "kick";
        this.description = "Kicks a user from your server.";
        this.category = Category.STAFF;
        this.args.add(new OptionData(OptionType.USER, "user", "The user to kick", true));
        this.args.add(new OptionData(OptionType.STRING, "reason", "Reason for the kick"));
        this.permission = Permission.KICK_MEMBERS;
        this.botPermission = Permission.KICK_MEMBERS;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        User user = event.getOption("user").getAsUser();
        Member target = event.getOption("user").getAsMember();
        if(target == null){
            event.replyEmbeds(EmbedUtils.createError("That user is not in this server")).setEphemeral(true).queue();
        }
        else if(target.getIdLong() == event.getJDA().getSelfUser().getIdLong()){
            event.replyEmbeds(EmbedUtils.createError("Do you really want to kick myself?")).setEphemeral(true).queue();
        }

        // Check target role position
        ModerationHandler moderationHandler = GuildData.get(event.getGuild()).moderationHandler;
        if (!moderationHandler.canTargetMember(target)) {
            event.replyEmbeds(EmbedUtils.createError("This member cannot be kicked. I need my role moved higher than theirs.")).setEphemeral(true).queue();
            return;
        }

        // Kick user from guild
        OptionMapping reasonOption = event.getOption("reason");
        String reason = reasonOption != null ? reasonOption.getAsString() : "Unspecified";
        user.openPrivateChannel().queue(privateChannel -> {
            // Private message user with reason for kick
            MessageEmbed msg = moderationHandler.createCaseMessage(event.getUser().getIdLong(), "Kick", reason, EmbedColor.WARNING.color);
            privateChannel.sendMessageEmbeds(msg).queue(
                    message -> target.kick(reason).queue(),
                    failure -> target.kick(reason).queue()
            );
        }, fail -> target.kick(reason).queue());

        // send confirmation message
        event.replyEmbeds(new EmbedBuilder()
                .setAuthor(user.getAsTag() + "has been kicked", null, user.getEffectiveAvatarUrl())
                .setDescription("**Reason**:" + reason)
                .setColor(EmbedColor.DEFAULT.color)
                .build()
        ).queue();
    }
}

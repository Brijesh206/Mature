package maturebot.handler;

import maturebot.MatureBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

import java.util.Date;

public class ModerationHandler {

    private final Guild guild;
    private final MatureBot bot;

    public ModerationHandler(MatureBot bot, Guild guild) {
        this.bot = bot;
        this.guild = guild;
    }

    /**
     * Creates a private message embed for one of the moderation commands with reason.
     *
     * @param moderatorID the moderator who ran the command.
     * @param action the command action (Kick, Ban, Warn, etc).
     * @param reason the reason for the action.
     * @param color the color to display on the embed.
     * @return embed custom to this case.
     */
    public MessageEmbed createCaseMessage(long moderatorID, String action, String reason, int color) {
        return caseMessageHelper(moderatorID, action, reason, null, color);
    }

    /**
     * Creates a private message embed for one of the moderation commands with reason and duration.
     *
     * @param moderatorID the moderator who ran the command.
     * @param action the command action (Kick, Ban, Warn, etc).
     * @param reason the reason for the action.
     * @param duration the duration of the action (days, weeks, months, etc).
     * @param color the color to display on the embed.
     * @return embed custom to this case.
     */
    public MessageEmbed createCaseMessage(long moderatorID, String action, String reason, String duration, int color) {
        return caseMessageHelper(moderatorID, action, reason, duration, color);
    }

    /**
     * Creates a private message embed for one of the moderation commands.
     *
     * @param moderatorID the moderator who ran the command.
     * @param action the command action (Kick, Ban, Warn, etc).
     * @param color the color to display on the embed.
     * @return embed custom to this case.
     */
    public MessageEmbed createCaseMessage(long moderatorID, String action, int color) {
        return caseMessageHelper(moderatorID, action, null, null, color);
    }
    /**
     * Creates the actual embed for createCaseMessage(), ignoring null values.
     */
    private MessageEmbed caseMessageHelper(long moderatorID, String action, String reason, String duration, int color) {
        String text = "**Server:** " + guild.getName();
        text += "\n**Actioned by:** <@!" + moderatorID + ">";
        text += "\n**Action:** " + action;
        if (duration != null) text += "\n**Duration:** " + duration;
        if (reason != null) text += "\n**Reason:** " + reason;
        return new EmbedBuilder().setColor(color).setDescription(text).setTimestamp(new Date().toInstant()).build();
    }

    /**
     * Checks if bot can run staff command against this member.
     *
     * @param target the member targeted by this command.
     * @return true if bot can target this member, otherwise false.
     */

    public boolean canTargetMember(Member target){

        // check the target is not the owner of the guild
        if(target == null) return true;
        if(target.isOwner()) return false;

        //check if bot has higher role than user
        int botPos =guild.getBotRole().getPosition();
        for (Role role : target.getRoles()){
            if (role.getPosition() >= botPos){
                return false;
            }
        }
        return true;
    }

}

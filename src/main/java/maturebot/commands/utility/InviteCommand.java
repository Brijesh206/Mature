package maturebot.commands.utility;

import maturebot.MatureBot;
import maturebot.commands.Category;
import maturebot.commands.Command;
import maturebot.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class InviteCommand extends Command {
    public InviteCommand(MatureBot bot) {
        super(bot);
        this.name = "invite";
        this.description = "Invite bot in your server";
        this.category = Category.UTILITY;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        Button b1 = Button.link("https://discord.com/api/oauth2/authorize?client_id=1037681681668247602&permissions=8&scope=bot", "Invite bot");
        Button b2 = Button.link("https://discord.gg/PMzhsb65", "Server Invite");
        event.replyEmbeds(EmbedUtils.createDefault(":robot: Click the button below to invite Mature bot to your servers!"))
                .addActionRow(b1, b2).queue();
    }
}

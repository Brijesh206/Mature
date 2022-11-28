package maturebot.commands;

import maturebot.MatureBot;
import maturebot.commands.data.GuildData;
import maturebot.commands.staff.KickCommand;
import maturebot.commands.staff.PurgeMessage;
import maturebot.commands.utility.InviteCommand;
import maturebot.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers, listens, and executes commands.
 *
 * @author Brijesh
 */

public class CommandRegistry extends ListenerAdapter {
    /** List of commands in the exact order registered */
    public static final List<Command> commands = new ArrayList<>();

    /** Map of command names to command objects */
    public static final Map<String, Command> commandsMap = new HashMap<>();

    /**
     * Adds commands to a global list and registers them as event listener.
     *
     * @param bot An instance of MatureBot.
     */
    public CommandRegistry(MatureBot bot) {
        mapCommand(
                // Commands to be registered
                new InviteCommand(bot),
                new KickCommand(bot),
                new PurgeMessage(bot)

        );
    }

    /**
     * Adds a command to the static list and map.
     *
     * @param cmds a spread list of command objects.
     */
    private void mapCommand(Command ...cmds) {
        for (Command cmd : cmds) {
            commandsMap.put(cmd.name, cmd);
            commands.add(cmd);
        }
    }

    /**
     * Creates a list of CommandData for all commands.
     *
     * @return a list of CommandData to be used for registration.
     */
    public static List<CommandData> unpackCommandData() {
        // Register slash commands
        List<CommandData> commandData = new ArrayList<>();
        for (Command command : commands) {
            SlashCommandData slashCommand = Commands.slash(command.name, command.description).addOptions(command.args);
            if (command.permission != null) {
                slashCommand.setDefaultPermissions(DefaultMemberPermissions.enabledFor(command.permission));
            }
            if (!command.subCommands.isEmpty()) {
                slashCommand.addSubcommands(command.subCommands);
            }
            commandData.add(slashCommand);
        }
        return commandData;
    }

    /**
     * Runs whenever a slash command is run in Discord.
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Get command by name
        Command cmd = commandsMap.get(event.getName());
        if (cmd != null) {
            // Check for required bot permissions
            Role botRole = event.getGuild().getBotRole();
            if (cmd.botPermission != null) {
                if (!botRole.hasPermission(cmd.botPermission) && !botRole.hasPermission(Permission.ADMINISTRATOR)) {
                    String text = "I need the `" + cmd.botPermission.getName() + "` permission to execute that command.";
                    event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
                    return;
                }
            }
            // Run command
            cmd.execute(event);
        }
    }

    /**
     * Registers slash commands as guild commands.
     * NOTE: May change to global commands on release.
     *
     * @param event executes when a guild is ready.
     */
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        // Get GuildData from database
        GuildData.get(event.getGuild());
        // Register slash commands
        event.getGuild().updateCommands().addCommands(unpackCommandData()).queue(succ -> {}, fail -> {});
    }
}


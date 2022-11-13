package maturebot.commands.data;

import maturebot.MatureBot;
import maturebot.handler.ModerationHandler;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GuildData {

    /** The GuildSettings instance for each guild. */
    private static final Map<Long, GuildData> guilds = new HashMap<>();

    /** Static utility variables. */
    private static MatureBot bot;
    private static boolean initialized;

    /** local memory cache. */
    public ModerationHandler moderationHandler;

    /**
     * Represents the local memory cache of guild data.
     *
     * @param guild the guild these settings represent.
     */
    private GuildData(Guild guild) {
        // Setup caches

        moderationHandler = new ModerationHandler(bot, guild);
    }

    /**
     * Retrieves the GuildSettings instance for a given guild.
     * If it doesn't exist, it will create one.
     *
     * @param guild the discord guild.
     * @return GuildSettings for specified guild.
     */
    public static GuildData get(@NotNull Guild guild) {
        if (!guilds.containsKey(guild.getIdLong())) {
            return create(guild);
        }
        return guilds.get(guild.getIdLong());
    }

    /**
     * This should ask for the webserver to get or create a settings object.
     * For testing purposes, this will instantiate GuildData.
     *
     * @param guild The guild to fetch from webserver for.
     * @return the guild data object for the newly created guild.
     */
    private static GuildData create(@NotNull Guild guild) {
        GuildData data = new GuildData(guild);
        guilds.put(guild.getIdLong(), data);
        return data;
    }
}

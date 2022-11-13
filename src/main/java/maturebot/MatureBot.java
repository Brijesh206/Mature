package maturebot;

import io.github.cdimascio.dotenv.Dotenv;
import maturebot.commands.CommandRegistry;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class MatureBot  {
    private final Dotenv config;
    private final ShardManager shardManager;

    public MatureBot() throws LoginException {

        config = Dotenv.configure().load();
        String token = config.get("TOKEN");
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS
                );
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.enableCache(CacheFlag.ONLINE_STATUS);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.watching("Mature's Development!"));
        shardManager = builder.build();

        // Register Listeners
        shardManager.addEventListener(new CommandRegistry(this));
    }

    public Dotenv getConfig() {
        return config;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public static void main(String[] args) throws LoginException {

        try {
            MatureBot bot = new MatureBot();
        } catch (Exception e) {
            System.out.println("Error : Provided bot token is invalid");
        }

    }
}
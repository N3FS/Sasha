package uk.co.n3fs.sasha;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import javax.inject.Inject;
import org.slf4j.Logger;
import uk.co.n3fs.sasha.database.DatabaseManager;
import uk.co.n3fs.sasha.ticket.TicketManagerImpl;

@Plugin(id = "sasha", name = "Sasha", version = "@VERSION@",
        description = "Ticket plugin for Velocity", authors = {"md678685"})
public class SashaPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final Path pluginDir;
    private final DatabaseManager dbManager;
    private UserCache cache;
    private TicketManagerImpl ticketManager;

    @Inject
    public SashaPlugin(ProxyServer server, Logger logger, @DataDirectory Path pluginDir) {
        this.server = server;
        this.logger = logger;
        this.pluginDir = pluginDir;
        this.dbManager = new DatabaseManager(this);
    }

    @Subscribe
    public void onProxyInit(final ProxyInitializeEvent event) {
        try {
            dbManager.setupDatabase();
            cache = new UserCache(server, dbManager);
            ticketManager = new TicketManagerImpl(dbManager);
        } catch (Exception e) {
            logger.error("Failed to set up database!", e);
            dbManager.close();
        }
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getPluginDir() {
        return pluginDir;
    }

    public static String getResource(final String path) throws IOException {
        return CharStreams.toString(new InputStreamReader(SashaPlugin.class.getResourceAsStream(path), Charsets.UTF_8)).replace("\r\n", "\n");
    }

}

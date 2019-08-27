package uk.co.n3fs.sasha;

import co.aikar.idb.Database;
import co.aikar.idb.DbRow;
import com.google.common.cache.*;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import uk.co.n3fs.sasha.database.DatabaseManager;

public class UserCache {

    private final ProxyServer server;
    private final DatabaseManager dbManager;
    private final LoadingCache<UUID, String> cache;

    public UserCache(ProxyServer server, DatabaseManager dbManager) {
        this.server = server;
        this.dbManager = dbManager;
        this.cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .removalListener(new UserCacheRemovalListener())
            .build(new UserCacheLoader());
    }

    public void forceAddUsername(UUID uuid) {
        cache.getUnchecked(uuid);
        cache.invalidate(uuid);
    }

    public String getUsername(UUID uuid) {
        try {
            return cache.get(uuid);
        } catch (ExecutionException e) {
            return "unknown";
        }
    }

    public UUID matchUser(String partialName) {
        partialName = partialName.replace("_", "\\_").replace("%", "\\%");
        String param = "%" + partialName + "%";

        try {
            Database db = dbManager.getDatabase();
            String id = db.getFirstColumn(dbManager.replace("SELECT id FROM {prefix}users WHERE username LIKE ?"), param);
            return UUID.fromString(id);
        } catch (SQLException | IllegalArgumentException ignored) {}

        return null;
    }

    class UserCacheRemovalListener implements RemovalListener<UUID, String> {
        @Override
        public void onRemoval(RemovalNotification<UUID, String> notification) {
            if (!dbManager.isAvailable()) return;

            String uuid = notification.getKey().toString();
            String username = notification.getValue();

            try {
                Database db = dbManager.getDatabase();
                DbRow row = db.getFirstRow(dbManager.replace("SELECT * FROM {prefix}users WHERE id = ?"), uuid);
                if (row == null || row.isEmpty()) {
                    db.executeInsert(dbManager.replace("INSERT INTO {prefix}users ( id , username ) VALUES ( ? , ? )"), uuid, username);
                } else {
                    db.executeUpdate(dbManager.replace("UPDATE {prefix}users SET name = ? WHERE uuid = ?"), username, uuid);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    class UserCacheLoader extends CacheLoader<UUID, String> {
        @Override
        public String load(UUID uuid) throws Exception {
            String username = null;
            Optional<Player> playerOpt = server.getPlayer(uuid);
            if (playerOpt.isPresent()) {
                username = playerOpt.get().getUsername();
            }

            if (username == null && dbManager.isAvailable()) {
                username = dbManager.getDatabase()
                    .getFirstColumn(dbManager.replace("SELECT username FROM {prefix}users WHERE id = ?"), uuid.toString());
            }

            return username != null ? username : "unknown";
        }
    }
}

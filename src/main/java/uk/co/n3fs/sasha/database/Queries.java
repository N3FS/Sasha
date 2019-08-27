package uk.co.n3fs.sasha.database;

public class Queries {

    /**
     * reporter_id, reported_at, updated_at, open, location_x, location_y, location_z, location_world, assignee_id
     */
    public static final String TICKET_CREATE = "INSERT INTO {prefix}tickets ( reporter_id, reported_at, updated_at, open, location_x, location_y, location_z, location_world, assignee_id ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";
    /**
     * id
     */
    public static final String TICKET_GET_BY_ID = "SELECT * FROM {prefix}tickets WHERE id = ?";
    /**
     * updated_at, id
     */
    public static final String TICKET_UPDATE_UPDATED_AT = "UPDATE {prefix}ticket_comments SET updated_at = ? WHERE id = ?";
    /**
     * open, id
     */
    public static final String TICKET_UPDATE_OPEN_STATE = "UPDATE {prefix}ticket_comments SET open = ? WHERE id = ?";

    /**
     * ticket_id, author_id, conversation_id, written_at, message, new_open_state
     */
    public static final String COMMENT_CREATE = "INSERT INTO {prefix}ticket_comments ( ticket_id, author_id, conversation_id, written_at, message, new_open_state ) VALUES ( ?, ?, ?, ?, ?, ? )";
    /**
     * ticket_id
     */
    public static final String COMMENT_LIST_CONVERSATION_IDS = "SELECT conversation_id FROM {prefix}ticket_comments WHERE ticket_id = ?";

    /**
     * ticket_id, user_id, last_seen
     */
    public static final String SUBSCRIPTION_CREATE = "INSERT INTO {prefix}ticket_subscriptions ( ticket_id, user_id, last_seen ) VALUES ( ?, ?, ? )";
}

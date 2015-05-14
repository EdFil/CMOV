package pt.ulisboa.tecnico.cmov.airdesk.database;

import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class AirDeskContract {

    /* Inner class that defines the table contents of the Workspace table */
    public static final class WorkspaceEntry implements BaseColumns {
        // Table Name
        public static final String TABLE_NAME = "workspace";
        // Name of the workspace
        public static final String COLUMN_WORKSPACE_NAME = "name";
        // Owner of the workspace
        public static final String COLUMN_OWNER_KEY = "owner_key";
        // Amount of quota available
        public static final String COLUMN_WORKSPACE_QUOTA = "quota";
        // Is the workspace private?
        public static final String COLUMN_WORKSPACE_IS_PRIVATE = "is_private";
    }

    /* Inner class that defines the table contents of the Tag table */
    public static final class SubscriptionEntry {
        // Table Name
        public static final String TABLE_NAME = "subscription";
        // Name of the tag
        public static final String COLUMN_USER_KEY = "user_key";
        // Name of the tag
        public static final String COLUMN_NAME = "name";
        // Name of the tag
        public static final String COLUMN_TAGS = "tags";
    }

    /* Inner class that defines the table contents of the location table */
    public static final class FilesEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "file";
        // Foreign Key into the workspace table
        public static final String COLUMN_WORKSPACE_KEY = "workspace_key";
        // Local path of the file
        public static final String COLUMN_FILE_NAME = "name";
        // Local path of the file
        public static final String COLUMN_FILE_VERSION = "version";
    }

    /* Inner class that defines the table contents of the Tag table */
    public static final class TagsEntry {
        // Table Name
        public static final String TABLE_NAME = "tag";
        // Name of the tag
        public static final String COLUMN_WORKSPACE_KEY = "workspace_id";
        // Name of the tag
        public static final String COLUMN_TAG_NAME = "tag_name";
    }

    /* Inner class that defines the table contents of the User table */
    public static final class UsersEntry implements BaseColumns {
        // Table Name
        public static final String TABLE_NAME = "user";
        // Unique email of the user
        public static final String COLUMN_USER_EMAIL = "user_email";
        // User nick name
        public static final String COLUMN_USER_NICK = "user_nick";
    }

    /* Inner class that defines the table contents of the User table */
    public static final class AccessListEntry {
        // Table Name
        public static final String TABLE_NAME = "user_workspace";
        // Unique email of the user
        public static final String COLUMN_WORKSPACE_KEY = "workspace_key";
        // User nick name
        public static final String COLUMN_USER_EMAIL = "user_email";
    }
}


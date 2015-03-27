package pt.ulisboa.tecnico.cmov.airdesk.database;

import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class AirDeskContract {

    /* Inner class that defines the table contents of the location table */
    public static final class FileEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "file";
        // Foreign Key into the workspace table
        public static final String COLUMN_WORKSPACE_KEY = "workspace_id";
        // Name of the file
        public static final String COLUMN_FILE_NAME = "file_name";
    }

    /* Inner class that defines the table contents of the Workspace table */
    public static final class WorkspaceEntry implements BaseColumns {
        // Table Name
        public static final String TABLE_NAME = "workspace";
        // Name of the workspace
        public static final String COLUMN_WORKSPACE_NAME = "workspace_name";
        // Owner of the workspace
        public static final String COLUMN_WORKSPACE_OWNER = "workspace_owner";
        // Amount of quota available
        public static final String COLUMN_WORKSPACE_QUOTA = "workspace_quota";
        // Is the workspace private?
        public static final String COLUMN_WORKSPACE_IS_PRIVATE = "workspace_is_private";

    }

    /* Inner class that defines the table contents of the Tag table */
    public static final class TagsEntry {
        // Table Name
        public static final String TABLE_NAME = "tag";
        // Name of the foreign workspace
        public static final String COLUMN_WORKSPACE_KEY = "workspace_id";
        // Name of the workspace
        public static final String COLUMN_TAG_NAME = "tag_name";
    }

    /* Inner class that defines the table contents of the User table */
    public static final class UsersEntry {
        // Table Name
        public static final String TABLE_NAME = "user";
        // Name of the foreign workspace
        public static final String COLUMN_WORKSPACE_KEY = "workspace_id";
        // Name of the workspace
        public static final String COLUMN_USER_EMAIL = "user_email";
    }
}

package pt.ulisboa.tecnico.cmov.cmov_proj.database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

//
//public final class AirDeskContract {
//
//    public AirDeskContract() { /* Empty */ }
//
//    public static abstract class WorkspaceEntry implements BaseColumns {
//        public static final String TABLE_NAME = "workspace";
//        public static final String COLUMN_NAME_NAME = "name";
//        public static final String COLUMN_NAME_QUOTA = "quota";
//    }
//
//    public static abstract class FileEntry implements BaseColumns {
//        public static final String TABLE_NAME = "file";
//        public static final String COLUMN_NAME_NAME = "name";
//    }
//}

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
        // Workspace tags to be searched if the workspace is public
        public static final String COLUMN_WORKSPACE_TAGS = "workspace_tags";
        // File names inside the workspace
        public static final String COLUMN_WORKSPACE_FILES = "workspace_files";
        // Users that are allowed if the workspace is private
        public static final String COLUMN_WORKSPACE_ALLOWED_USERS = "workspace_allowed_users";
    }
}

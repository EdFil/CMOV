package pt.ulisboa.tecnico.cmov.cmov_proj.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import pt.ulisboa.tecnico.cmov.cmov_proj.database.AirDeskContract.WorkspaceEntry;

/**
 * Created by edgar on 23-03-2015.
 */
public class AirDeskDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "airdesk.db";
    public static final int DATABASE_VERSION = 2;

    public AirDeskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Query to create the Workspace table
        final String SQL_CREATE_WORKSPACE_TABLE = "CREATE TABLE " + WorkspaceEntry.TABLE_NAME + " (" +
                WorkspaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WorkspaceEntry.COLUMN_WORKSPACE_NAME + " TEXT UNIQUE NOT NULL, " +
                WorkspaceEntry.COLUMN_WORKSPACE_OWNER + " TEXT NOT NULL, " +
                WorkspaceEntry.COLUMN_WORKSPACE_QUOTA + " INTEGER NOT NULL, " +
                WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE + " INTEGER NOT NULL, " +
                WorkspaceEntry.COLUMN_WORKSPACE_TAGS + " TEXT, " +
                WorkspaceEntry.COLUMN_WORKSPACE_FILES + " TEXT, " +
                WorkspaceEntry.COLUMN_WORKSPACE_ALLOWED_USERS + " TEXT " +
                " );";

        db.execSQL(SQL_CREATE_WORKSPACE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WorkspaceEntry.TABLE_NAME);
        onCreate(db);
    }



}

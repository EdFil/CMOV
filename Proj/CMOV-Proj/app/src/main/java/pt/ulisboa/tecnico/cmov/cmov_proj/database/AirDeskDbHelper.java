package pt.ulisboa.tecnico.cmov.cmov_proj.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import pt.ulisboa.tecnico.cmov.cmov_proj.database.AirDeskContract.WorkspaceEntry;
import pt.ulisboa.tecnico.cmov.cmov_proj.database.AirDeskContract.TagsEntry;
import pt.ulisboa.tecnico.cmov.cmov_proj.database.AirDeskContract.UsersEntry;

/**
 * Created by edgar on 23-03-2015.
 */
public class AirDeskDbHelper extends SQLiteOpenHelper {

    public static final String TAG = AirDeskDbHelper.class.getSimpleName();
    private static AirDeskDbHelper mInstance;

    public static final String DATABASE_NAME = "airdesk.db";
    public static final int DATABASE_VERSION = 2;

    public static synchronized AirDeskDbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AirDeskDbHelper(context);
        }
        return mInstance;
    }

    private AirDeskDbHelper(Context context) {
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
                WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_TAG_TABLE = "CREATE TABLE " + TagsEntry.TABLE_NAME + " (" +
                TagsEntry.COLUMN_WORKSPACE_KEY + " INTEGER NOT NULL, " +
                TagsEntry.COLUMN_TAG_NAME + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + TagsEntry.COLUMN_WORKSPACE_KEY  + ") REFERENCES " + WorkspaceEntry.TABLE_NAME + "( " + WorkspaceEntry._ID + " )," +
                "PRIMARY KEY (" + TagsEntry.COLUMN_WORKSPACE_KEY  + ", " + TagsEntry.COLUMN_TAG_NAME + ") " +
                " );";

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UsersEntry.TABLE_NAME + " (" +
                UsersEntry.COLUMN_WORKSPACE_KEY + " INTEGER NOT NULL, " +
                UsersEntry.COLUMN_USER_EMAIL + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + UsersEntry.COLUMN_WORKSPACE_KEY  + ") REFERENCES " + WorkspaceEntry.TABLE_NAME + "( " + WorkspaceEntry._ID + " )," +
                "PRIMARY KEY (" + UsersEntry.COLUMN_WORKSPACE_KEY  + ", " +UsersEntry.COLUMN_USER_EMAIL + ") " +
                " );";

//        final String SQL_CREATE_FILE_TABLE = "CREATE TABLE " + FileEntry.TABLE_NAME + " (" +
//                FileEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                WorkspaceEntry.COLUMN_WORKSPACE_NAME + " TEXT UNIQUE NOT NULL, " +
//                WorkspaceEntry.COLUMN_WORKSPACE_OWNER + " TEXT NOT NULL, " +
//                WorkspaceEntry.COLUMN_WORKSPACE_QUOTA + " INTEGER NOT NULL, " +
//                WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE + " INTEGER NOT NULL, " +
//                " );";

        db.execSQL(SQL_CREATE_WORKSPACE_TABLE);
            db.execSQL(SQL_CREATE_TAG_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WorkspaceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TagsEntry.TABLE_NAME);
        onCreate(db);
    }

    public void insertWorkspace(String name, String owner, int quota, boolean isPrivate){
        SQLiteDatabase db = mInstance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_NAME, name);
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_OWNER, owner);
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_QUOTA, quota);
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE, isPrivate ? 1 : 0);

        db.insert(WorkspaceEntry.TABLE_NAME, null, values);

        db.close();
    }

    public void deleteWorkspace(int workspaceId){
        SQLiteDatabase db = mInstance.getWritableDatabase();

        // Delete all tag association
        db.delete(TagsEntry.TABLE_NAME, TagsEntry.COLUMN_WORKSPACE_KEY + "='" + workspaceId + "'", null);

        // Delete all user association
        db.delete(UsersEntry.TABLE_NAME, UsersEntry.COLUMN_WORKSPACE_KEY + "='" + workspaceId + "'", null);

        // Delete Workspace
        String whereClause = WorkspaceEntry._ID + "=?";
        String[] whereArgs = new String[]{ String.valueOf(workspaceId) };

        db.delete(WorkspaceEntry.TABLE_NAME, whereClause, whereArgs);

        db.close();
    }

    public void addTagsToWorkspace(int workspaceId, String[] files) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        // INSERT INTO 'tablename' ('column1', 'column2') VALUES
        String query = "INSERT INTO " + TagsEntry.TABLE_NAME + "('" + TagsEntry.COLUMN_WORKSPACE_KEY + "', '" + TagsEntry.COLUMN_TAG_NAME + "') VALUES ";
        for(int i = 0; i < files.length; i++){
            // Values ('data1', 'data2'), or ('data1', 'data2');
            query += "('" + workspaceId + "', '" + files[i] + "')" + ((i < files.length - 1) ? "," : ";");
        }

        db.execSQL(query);
    }

    public void removeTagsFromWorkspace(int workspaceId, String[] files) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String query = "DELETE FROM " + TagsEntry.TABLE_NAME + " WHERE " + TagsEntry.COLUMN_WORKSPACE_KEY + "='" + workspaceId + "' AND ";

        for(int i = 0; i < files.length; i++) {
            db.execSQL(query + TagsEntry.COLUMN_TAG_NAME + "='" + files[i] + "';");
        }
    }

    public void addUserToWorkspace(int workspaceId, String[] users) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        // INSERT INTO 'tablename' ('column1', 'column2') VALUES
        String query = "INSERT INTO " + UsersEntry.TABLE_NAME + "('" + UsersEntry.COLUMN_WORKSPACE_KEY + "', '" + UsersEntry.COLUMN_USER_EMAIL + "') VALUES ";
        for(int i = 0; i < users.length; i++){
            // Values ('data1', 'data2'), or ('data1', 'data2');
            query += "('" + workspaceId + "', '" + users[i] + "')" + ((i < users.length - 1) ? "," : ";");
        }

        db.execSQL(query);
    }

    public void removeUserFromWorkspace(int workspaceId, String[] files) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String query = "DELETE FROM " + UsersEntry.TABLE_NAME + " WHERE " + UsersEntry.COLUMN_WORKSPACE_KEY + "='" + workspaceId + "' AND ";

        for(int i = 0; i < files.length; i++) {
            db.execSQL(query + UsersEntry.COLUMN_USER_EMAIL + "='" + files[i] + "';");
        }
    }


}

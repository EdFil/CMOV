package pt.ulisboa.tecnico.cmov.airdesk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.FileEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.TagsEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.UsersEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.WorkspaceEntry;

/**
 * Created by edgar on 23-03-2015.
 */
public class AirDeskDbHelper extends SQLiteOpenHelper {

    public static final String TAG = AirDeskDbHelper.class.getSimpleName();
    private static AirDeskDbHelper mInstance;

    public static final String DATABASE_NAME = "airdesk.db";
    public static final int DATABASE_VERSION = 19;
    private File[] filesFromWorkspace;
    private HashMap<Long, ArrayList<File>> allFilesInMap;

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
                UsersEntry.COLUMN_USER_NICK + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + UsersEntry.COLUMN_WORKSPACE_KEY  + ") REFERENCES " + WorkspaceEntry.TABLE_NAME + "( " + WorkspaceEntry._ID + " )," +
                "PRIMARY KEY (" + UsersEntry.COLUMN_WORKSPACE_KEY  + ", " +UsersEntry.COLUMN_USER_EMAIL + ") " +
                " );";

        final String SQL_CREATE_FILE_TABLE = "CREATE TABLE " + FileEntry.TABLE_NAME + " (" +
                FileEntry.COLUMN_WORKSPACE_KEY + " INTEGER NOT NULL," +
                FileEntry.COLUMN_FILE_NAME + " TEXT NOT NULL," +
                FileEntry.COLUMN_FILE_SIZE + " INTEGER NOT NULL," +
                FileEntry.COLUMN_FILE_LAST_EDIT + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + FileEntry.COLUMN_WORKSPACE_KEY  + ") REFERENCES " + WorkspaceEntry.TABLE_NAME + "( " + WorkspaceEntry._ID + " )," +
                "PRIMARY KEY (" + FileEntry.COLUMN_WORKSPACE_KEY  + ", " + FileEntry.COLUMN_FILE_NAME + ") " +
                " );";

        db.execSQL(SQL_CREATE_WORKSPACE_TABLE);
        db.execSQL(SQL_CREATE_TAG_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_FILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WorkspaceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TagsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FileEntry.TABLE_NAME);
        onCreate(db);
    }

    public void insertWorkspace(Workspace workspace){
        SQLiteDatabase db = mInstance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_NAME, workspace.getName());
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_OWNER, workspace.getOwner().getEmail());
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_QUOTA, workspace.getQuota());
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE, workspace.isPrivate() ? 1 : 0);

        long rowId = db.insert(WorkspaceEntry.TABLE_NAME, null, values);

        Cursor cursor = db.query(
                WorkspaceEntry.TABLE_NAME,
                new String[] { WorkspaceEntry._ID },
                WorkspaceEntry.COLUMN_WORKSPACE_NAME + "=?",
                new String[] { workspace.getName() },
                null,
                null,
                null
        );

        cursor.moveToFirst();

        workspace.setDatabaseId(cursor.getLong(cursor.getColumnIndex(WorkspaceEntry._ID)));

        cursor.close();
        db.close();
    }

    public void removeFileFromWorkspace(Workspace workspace, File file){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String whereClause = String.format("%s=? AND %s=?;", FileEntry.COLUMN_WORKSPACE_KEY, FileEntry.COLUMN_FILE_NAME);
        String[] whereArgs = new String[] { workspace.getName(), file.getName() };
        db.delete(FileEntry.TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public void removeTagFromWorkspace(Workspace workspace, Tag tag){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String whereClause = String.format("%s=? AND %s=?;", TagsEntry.COLUMN_WORKSPACE_KEY, TagsEntry.COLUMN_TAG_NAME);
        String[] whereArgs = new String[] { workspace.getName(), tag.getText() };
        db.delete(TagsEntry.TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public void removeUserFromWorkspace(Workspace workspace, User user){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String whereClause = String.format("%s=? AND %s=?;", UsersEntry.COLUMN_WORKSPACE_KEY, UsersEntry.COLUMN_USER_EMAIL);
        String[] whereArgs = new String[] { workspace.getName(), user.getEmail() };
        db.delete(UsersEntry.TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public void deleteWorkspace(Workspace workspace){
        SQLiteDatabase db = mInstance.getWritableDatabase();

        // Delete all tag association
        db.delete(TagsEntry.TABLE_NAME, TagsEntry.COLUMN_WORKSPACE_KEY + "='" + workspace.getDatabaseId() + "'", null);

        // Delete all user association
        db.delete(UsersEntry.TABLE_NAME, UsersEntry.COLUMN_WORKSPACE_KEY + "='" + workspace.getDatabaseId() + "'", null);

        // Delete all file associations
        db.delete(FileEntry.TABLE_NAME, FileEntry.COLUMN_WORKSPACE_KEY + "='" + workspace.getDatabaseId() + "'", null);

        // Delete Workspace
        String whereClause = WorkspaceEntry._ID + "=?";
        String[] whereArgs = new String[]{ String.valueOf(workspace.getDatabaseId()) };

        db.delete(WorkspaceEntry.TABLE_NAME, whereClause, whereArgs);

        db.close();
    }

    public ArrayList<Workspace> getAllLocalWorkspaceInfo() {
        HashMap<Long, ArrayList<Tag>> tagList = getAllTagsInMap();
        HashMap<Long, ArrayList<User>> userList = getAllUsersInMap();
        HashMap<Long, ArrayList<File>> fileList = getAllFilesInMap();

        SQLiteDatabase db = mInstance.getReadableDatabase();

        Cursor workspaceCursor = db.query(WorkspaceEntry.TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Workspace> workspaces = new ArrayList<Workspace>();

        while (workspaceCursor.moveToNext()) {
            long workspaceId = workspaceCursor.getLong(workspaceCursor.getColumnIndex(WorkspaceEntry._ID));
            workspaces.add(new Workspace(
                    workspaceCursor.getString(workspaceCursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_NAME)),
                    new User(workspaceCursor.getString(workspaceCursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_OWNER)), ""),
                    workspaceCursor.getLong(workspaceCursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_QUOTA)),
                    workspaceCursor.getInt(workspaceCursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE)) == 1 ? true : false,
                    tagList.containsKey(workspaceId) ? tagList.get(workspaceId) : new ArrayList<Tag>(),
                    userList.containsKey(workspaceId) ? userList.get(workspaceId) : new ArrayList<User>(),
                    fileList.containsKey(workspaceId) ? fileList.get(workspaceId) : new ArrayList<File>(),
                    WorkspaceManager.getInstance()
            ));
            workspaces.get(workspaces.size() - 1).setDatabaseId(workspaceId);
        }

        return workspaces;
    }

    public void addTagsToWorkspace(Workspace workspace, Collection<Tag> tags){
        if(tags == null)
            return;
        SQLiteDatabase db = mInstance.getWritableDatabase();
        // INSERT INTO 'tablename' ('column1', 'column2') VALUES
        String query = "INSERT INTO " + TagsEntry.TABLE_NAME + "('" + TagsEntry.COLUMN_WORKSPACE_KEY + "', '" + TagsEntry.COLUMN_TAG_NAME + "') VALUES ";

        Iterator<Tag> iterator = tags.iterator();
        if(!iterator.hasNext())
            return;

        while(iterator.hasNext()){
            Tag tag = iterator.next();
            // Values ('data1', 'data2'), or ('data1', 'data2');
            query += "('" + workspace.getDatabaseId() + "', '" + tag.getText() + "')" + (iterator.hasNext() ? "," : ";");
        }

        db.execSQL(query);
        db.close();
    }

    public String[] getWorkspaceTags(Workspace workspace){
        String[] tags;
        SQLiteDatabase db = mInstance.getWritableDatabase();

        Cursor cursor = db.query(
                TagsEntry.TABLE_NAME,
                new String[]{TagsEntry.COLUMN_TAG_NAME},
                TagsEntry.COLUMN_WORKSPACE_KEY + "=?",
                new String[]{String.valueOf(workspace.getDatabaseId())},
                null,
                null,
                null
        );

        tags = new String[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;
        do{
            tags[i++] = cursor.getString(cursor.getColumnIndex(TagsEntry.COLUMN_TAG_NAME));
        }while(cursor.moveToNext());

        cursor.close();
        db.close();
        return tags;
    }

    public void removeTagsFromWorkspace(Workspace workspace, Collection<Tag> tags) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String query = "DELETE FROM " + TagsEntry.TABLE_NAME + " WHERE " + TagsEntry.COLUMN_WORKSPACE_KEY + "='" + workspace.getDatabaseId() + "' AND ";

        Iterator<Tag> iterator = tags.iterator();

        while(iterator.hasNext()){
            db.execSQL(query + TagsEntry.COLUMN_TAG_NAME + "='" + iterator.next().getText() + "';");
        }
        db.close();
    }

    public void addUsersToWorkspace(Workspace workspace, Collection<User> users) {
        if(users == null)
            return;
        SQLiteDatabase db = mInstance.getWritableDatabase();
        // INSERT INTO 'tablename' ('column1', 'column2') VALUES
        String query = "INSERT INTO " + UsersEntry.TABLE_NAME + "('" + UsersEntry.COLUMN_WORKSPACE_KEY + "', '" + UsersEntry.COLUMN_USER_EMAIL + "', " + UsersEntry.COLUMN_USER_NICK + ") VALUES ";

        Iterator<User> iterator = users.iterator();
        if(!iterator.hasNext())
            return;

        while(iterator.hasNext()){
            User user = iterator.next();
            // Values ('data1', 'data2'), or ('data1', 'data2');
            query += "('" + workspace.getDatabaseId() + "', '" + user.getEmail() + "', '" + user.getNick() + "')" + (iterator.hasNext() ? "," : ";");
        }

        db.execSQL(query);
        db.close();
    }

    public void removeUsersFromWorkspace(Workspace workspace, Collection<User> users) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String query = "DELETE FROM " + UsersEntry.TABLE_NAME + " WHERE " + UsersEntry.COLUMN_WORKSPACE_KEY + "='" + workspace.getDatabaseId() + "' AND ";

        Iterator<User> iterator = users.iterator();

        while(iterator.hasNext()){
            db.execSQL(query + UsersEntry.COLUMN_USER_EMAIL + "='" + iterator.next().getEmail() + "';");
        }

        db.close();
    }

    public static HashMap<Long, ArrayList<Tag>> getAllTagsInMap(){
        SQLiteDatabase db = mInstance.getReadableDatabase();
        Cursor cursor = db.query(TagsEntry.TABLE_NAME, null, null, null, null, null, null);
        int workspaceIdIndex = cursor.getColumnIndex(TagsEntry.COLUMN_WORKSPACE_KEY);
        int tagNameIndex = cursor.getColumnIndex(TagsEntry.COLUMN_TAG_NAME);
        HashMap<Long, ArrayList<Tag>> workspaceTags = new HashMap<Long, ArrayList<Tag>>();

        while(cursor.moveToNext()){
            long workspaceId = cursor.getLong(workspaceIdIndex);
            String tagName = cursor.getString(tagNameIndex);
            if(workspaceTags.containsKey(workspaceId))
                workspaceTags.get(workspaceId).add(new Tag(tagName));
            else {
                workspaceTags.put(workspaceId,  new ArrayList<Tag>());
                workspaceTags.get(workspaceId).add(new Tag(tagName));
            }
        }

        cursor.close();
        db.close();

        return workspaceTags;
    }

    public void addFilesToWorkspace(Workspace workspace, Collection<File> files) {
        if(files == null)
            return;
        SQLiteDatabase db = mInstance.getWritableDatabase();
        // INSERT INTO 'tablename' ('column1', 'column2') VALUES
        String query = "INSERT INTO " + FileEntry.TABLE_NAME + "('" + FileEntry.COLUMN_WORKSPACE_KEY + "', '" + FileEntry.COLUMN_FILE_NAME + "', '" + FileEntry.COLUMN_FILE_SIZE + "', " + FileEntry.COLUMN_FILE_LAST_EDIT + ") VALUES ";

        Iterator<File> iterator = files.iterator();
        if(!iterator.hasNext())
            return;

        while(iterator.hasNext()){
            File file = iterator.next();
            // Values ('data1', 'data2'), or ('data1', 'data2');
            query += "('" + workspace.getDatabaseId() + "', '" + file.getName() + "', '" + file.length() + "', '" + file.lastModified() + "')" + (iterator.hasNext() ? "," : ";");
        }

        db.execSQL(query);
        db.close();
    }

    public void removeFilesToWorkspace(Workspace workspace, Collection<File> files) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String query = "DELETE FROM " + UsersEntry.TABLE_NAME + " WHERE " + UsersEntry.COLUMN_WORKSPACE_KEY + "='" + workspace.getDatabaseId() + "' AND ";

        Iterator<File> iterator = files.iterator();

        while(iterator.hasNext()){
            db.execSQL(query + UsersEntry.COLUMN_USER_EMAIL + "='" + iterator.next().getName() + "';");
        }

        db.close();
    }

    public static HashMap<Long, ArrayList<User>> getAllUsersInMap(){
        SQLiteDatabase db = mInstance.getReadableDatabase();
        // Query to get users
        Cursor cursor = db.query(UsersEntry.TABLE_NAME, null, null, null, null, null, null);
        // Get all indices stored
        int workspaceIdIndex = cursor.getColumnIndex(UsersEntry.COLUMN_WORKSPACE_KEY);
        int userEmailIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_EMAIL);
        int userNickIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_NICK);
        // Create map
        HashMap<Long, ArrayList<User>> workspaceUsers = new HashMap<Long, ArrayList<User>>();

        while(cursor.moveToNext()){
            long workspaceId = cursor.getLong(workspaceIdIndex);
            String userEmail = cursor.getString(userEmailIndex);
            String userNick = cursor.getString(userNickIndex);
            if(workspaceUsers.containsKey(workspaceId))
                workspaceUsers.get(workspaceId).add(new User(userEmail, userNick));
            else {
                workspaceUsers.put(workspaceId, new ArrayList<User>());
                workspaceUsers.get(workspaceId).add(new User(userEmail, userNick));
            }
        }

        cursor.close();
        db.close();

        return workspaceUsers;
    }

    public HashMap<Long,ArrayList<File>> getAllFilesInMap() {
        SQLiteDatabase db = mInstance.getReadableDatabase();
        Cursor cursor = db.query(FileEntry.TABLE_NAME, null, null, null, null, null, null);
        int workspaceIdIndex = cursor.getColumnIndex(FileEntry.COLUMN_WORKSPACE_KEY);
        int fileNameIndex = cursor.getColumnIndex(FileEntry.COLUMN_FILE_NAME);
        HashMap<Long, ArrayList<File>> workspaceFiles = new HashMap<Long, ArrayList<File>>();

        while(cursor.moveToNext()){
            long workspaceId = cursor.getLong(workspaceIdIndex);
            String fileName = cursor.getString(fileNameIndex);
            if(workspaceFiles.containsKey(workspaceId))
                workspaceFiles.get(workspaceId).add(new File(fileName));
            else {
                workspaceFiles.put(workspaceId,  new ArrayList<File>());
                workspaceFiles.get(workspaceId).add(new File(fileName));
            }
        }

        cursor.close();
        db.close();

        return workspaceFiles;
    }
}

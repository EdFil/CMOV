package pt.ulisboa.tecnico.cmov.airdesk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.LocalFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileAlreadyExistsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.subscription.Subscription;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceException;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.AccessListEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.FilesEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.SubscriptionEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.TagsEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.UsersEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.WorkspaceEntry;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;

public class AirDeskDbHelper extends SQLiteOpenHelper {

    public static final String TAG = AirDeskDbHelper.class.getSimpleName();
    private static AirDeskDbHelper mInstance;

    public static final String DATABASE_NAME = "airdesk.db";
    public static final int DATABASE_VERSION = 30;
    private int subscriptions;

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
        final String SQL_CREATE_WORKSPACE_TABLE = "CREATE TABLE " + AirDeskContract.WorkspaceEntry.TABLE_NAME + " (" +
                WorkspaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WorkspaceEntry.COLUMN_WORKSPACE_NAME + " TEXT NOT NULL, " +
                WorkspaceEntry.COLUMN_OWNER_KEY + " INTEGER NOT NULL, " +
                WorkspaceEntry.COLUMN_WORKSPACE_QUOTA + " INTEGER NOT NULL, " +
                WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + WorkspaceEntry.COLUMN_OWNER_KEY  + ") REFERENCES " + UsersEntry.TABLE_NAME + "( " + UsersEntry._ID + " ) " +
                " );";

        final String SQL_CREATE_TAG_TABLE = "CREATE TABLE " + TagsEntry.TABLE_NAME + " (" +
                TagsEntry.COLUMN_TAG_NAME + " TEXT NOT NULL, " +
                TagsEntry.COLUMN_WORKSPACE_KEY + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + TagsEntry.COLUMN_WORKSPACE_KEY  + ") REFERENCES " + WorkspaceEntry.TABLE_NAME + "( " + AirDeskContract.WorkspaceEntry._ID + " ), " +
                "PRIMARY KEY (" + TagsEntry.COLUMN_TAG_NAME + ", " + TagsEntry.COLUMN_WORKSPACE_KEY + ") " +
                " );";

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UsersEntry.TABLE_NAME + " (" +
                UsersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UsersEntry.COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                UsersEntry.COLUMN_USER_NICK + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_FILE_TABLE = "CREATE TABLE " + FilesEntry.TABLE_NAME + " (" +
                FilesEntry.COLUMN_FILE_NAME + " TEXT NOT NULL, " +
                FilesEntry.COLUMN_WORKSPACE_KEY + " INTEGER NOT NULL, " +
                FilesEntry.COLUMN_FILE_VERSION + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + FilesEntry.COLUMN_WORKSPACE_KEY  + ") REFERENCES " + WorkspaceEntry.TABLE_NAME + "( " + AirDeskContract.WorkspaceEntry._ID + " ), " +
                "PRIMARY KEY (" + FilesEntry.COLUMN_FILE_NAME + ", " + FilesEntry.COLUMN_WORKSPACE_KEY + ") " +
                " );";

        final String SQL_SUBSCRIPTIONS_TABLE = "CREATE TABLE " + SubscriptionEntry.TABLE_NAME + " (" +
                SubscriptionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SubscriptionEntry.COLUMN_USER_KEY+ " TEXT NOT NULL, " +
                SubscriptionEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                SubscriptionEntry.COLUMN_TAGS + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + SubscriptionEntry.COLUMN_USER_KEY  + ") REFERENCES " + UsersEntry.TABLE_NAME + "( " + UsersEntry._ID + " ) " +
                " );";


        final String SQL_CREATE_USER_WORKSPACE_TABLE = "CREATE TABLE " + AccessListEntry.TABLE_NAME + " (" +
                AccessListEntry.COLUMN_WORKSPACE_KEY + " INTEGER NOT NULL," +
                AccessListEntry.COLUMN_USER_EMAIL + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + AccessListEntry.COLUMN_WORKSPACE_KEY  + ") REFERENCES " + AirDeskContract.WorkspaceEntry.TABLE_NAME + "( " + AirDeskContract.WorkspaceEntry._ID + " )," +
                "FOREIGN KEY (" + AccessListEntry.COLUMN_USER_EMAIL + ") REFERENCES " + UsersEntry.TABLE_NAME + "( " + UsersEntry._ID + " )," +
                "PRIMARY KEY (" + AccessListEntry.COLUMN_WORKSPACE_KEY + ", " + AccessListEntry.COLUMN_USER_EMAIL + ") " +
                " );";


        db.execSQL(SQL_CREATE_WORKSPACE_TABLE);
        db.execSQL(SQL_CREATE_TAG_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_FILE_TABLE);
        db.execSQL(SQL_SUBSCRIPTIONS_TABLE);
        db.execSQL(SQL_CREATE_USER_WORKSPACE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AirDeskContract.WorkspaceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TagsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AirDeskContract.FilesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AccessListEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SubscriptionEntry.TABLE_NAME);
        onCreate(db);
    }

    public long insertWorkspace(String workspaceName, long ownerId, long quotaValue, boolean isPrivate, boolean isLocal, long userId){
        SQLiteDatabase db = mInstance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_NAME, workspaceName);
        values.put(AirDeskContract.WorkspaceEntry.COLUMN_OWNER_KEY, ownerId);
        values.put(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_QUOTA, quotaValue);
        values.put(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE, isPrivate ? 1 : 0);

        long rowId = db.insert(AirDeskContract.WorkspaceEntry.TABLE_NAME, null, values);
        db.close();

        if(rowId == -1)
            throw new WorkspaceException("Could not add Workspace");

        return rowId;
    }

    public void deleteWorkspace(long workspaceId){
        SQLiteDatabase db = mInstance.getWritableDatabase();

        db.delete(TagsEntry.TABLE_NAME, TagsEntry.COLUMN_WORKSPACE_KEY + "='" + workspaceId + "'", null);
        db.delete(AccessListEntry.TABLE_NAME, AccessListEntry.COLUMN_WORKSPACE_KEY + "='" + workspaceId + "'", null);
        db.delete(FilesEntry.TABLE_NAME, FilesEntry.COLUMN_WORKSPACE_KEY + "='" + workspaceId + "'", null);
        db.delete(AirDeskContract.WorkspaceEntry.TABLE_NAME, AirDeskContract.WorkspaceEntry._ID + "=" + workspaceId, null);

        db.close();
    }


    public long insertUser(String email, String nick) {
        SQLiteDatabase db = mInstance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UsersEntry.COLUMN_USER_EMAIL, email);
        values.put(UsersEntry.COLUMN_USER_NICK, nick);

        long rowId = db.insert(UsersEntry.TABLE_NAME, null, values);

        db.close();

        return rowId;
    }


    public void deleteUser(long userId) {
        SQLiteDatabase db = mInstance.getWritableDatabase();

        db.delete(UsersEntry.TABLE_NAME, UsersEntry._ID + "=" + userId, null);

        db.close();
    }

    public void insertFileToWorkspace(Workspace workspace, String fileName, int version) {
        SQLiteDatabase db = mInstance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AirDeskContract.FilesEntry.COLUMN_WORKSPACE_KEY, workspace.getDatabaseId());
        values.put(FilesEntry.COLUMN_FILE_NAME, fileName);
        values.put(FilesEntry.COLUMN_FILE_VERSION, version);

        long id = db.insert(AirDeskContract.FilesEntry.TABLE_NAME, null, values);

        db.close();
        if(id == -1)
            throw new FileAlreadyExistsException(fileName);
    }

    public void removeFileFromWorkspace(Workspace workspace, String fileName) {
        SQLiteDatabase db = mInstance.getWritableDatabase();

        String whereClause = FilesEntry.COLUMN_FILE_NAME + "=? AND " + FilesEntry.COLUMN_WORKSPACE_KEY + "=?";
        String[] whereArgs = new String[] { fileName, String.valueOf(workspace.getDatabaseId()) };
        db.delete(AirDeskContract.FilesEntry.TABLE_NAME, whereClause, whereArgs);

        db.close();
    }

    public void insertTagToWorkspace(Workspace workspace, String tag) {
        SQLiteDatabase db = mInstance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TagsEntry.COLUMN_WORKSPACE_KEY, workspace.getDatabaseId());
        values.put(TagsEntry.COLUMN_TAG_NAME, tag);

        db.insert(TagsEntry.TABLE_NAME, null, values);

        db.close();
    }

    public void removeTagFromWorkspace(Workspace workspace, String tag){
        SQLiteDatabase db = mInstance.getWritableDatabase();

        String whereClause = String.format("%s=? AND %s=?;", TagsEntry.COLUMN_WORKSPACE_KEY, TagsEntry.COLUMN_TAG_NAME);
        String[] whereArgs = new String[] { String.valueOf(workspace.getDatabaseId()), tag };
        db.delete(TagsEntry.TABLE_NAME, whereClause, whereArgs);

        db.close();
    }

    public void insertUserToWorkspace(Workspace workspace, String userEmail) {
        SQLiteDatabase db = mInstance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AccessListEntry.COLUMN_WORKSPACE_KEY, workspace.getDatabaseId());
        values.put(AccessListEntry.COLUMN_USER_EMAIL, userEmail);
        db.insert(AccessListEntry.TABLE_NAME, null, values);

        db.close();
    }

    public void removeUserFromWorkspace(Workspace workspace, String userEmail){
        SQLiteDatabase db = mInstance.getWritableDatabase();

        String whereClause = String.format("%s=? AND %s=?;", AccessListEntry.COLUMN_WORKSPACE_KEY, AccessListEntry.COLUMN_USER_EMAIL);
        String[] whereArgs = new String[] { String.valueOf(workspace.getDatabaseId()), userEmail };
        db.delete(AccessListEntry.TABLE_NAME, whereClause, whereArgs);

        db.close();
    }

    public Collection<String> getWorkspaceTags(Workspace workspace){
        SQLiteDatabase db = mInstance.getReadableDatabase();
        ArrayList<String> tags = new ArrayList<>();

        final String MY_QUERY =
                " SELECT * FROM " + TagsEntry.TABLE_NAME +
                " WHERE " + TagsEntry.TABLE_NAME + "." + TagsEntry.COLUMN_WORKSPACE_KEY + " = " + workspace.getDatabaseId();

        Cursor cursor = db.rawQuery(MY_QUERY, null);

        int columnTagName = cursor.getColumnIndex(TagsEntry.COLUMN_TAG_NAME);

        while(cursor.moveToNext()) {
            tags.add(cursor.getString(columnTagName));
        }

        return tags;
    }

    public Collection<User> getAllUsers() {
        SQLiteDatabase db = mInstance.getReadableDatabase();
        ArrayList<User> users = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + UsersEntry.TABLE_NAME, null);

        int columnIdIndex = cursor.getColumnIndex(UsersEntry._ID);
        int columnEmailIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_EMAIL);
        int columnNickIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_NICK);

        while(cursor.moveToNext()){
            users.add(new User(cursor.getLong(columnIdIndex), cursor.getString(columnEmailIndex), cursor.getString(columnNickIndex)));
        }

        cursor.close();
        db.close();

        return users;
    }

    public Collection<String> getWorkspaceAccessList(Workspace workspace){
        SQLiteDatabase db = mInstance.getReadableDatabase();
        ArrayList<String> users = new ArrayList<>();
        final String MY_QUERY =
                " SELECT * FROM " + AccessListEntry.TABLE_NAME +
                " WHERE " + AccessListEntry.TABLE_NAME + "." + AccessListEntry.COLUMN_WORKSPACE_KEY + " = " + workspace.getDatabaseId();

        Cursor cursor = db.rawQuery(MY_QUERY, null);

        int columnEmail = cursor.getColumnIndex(AccessListEntry.COLUMN_USER_EMAIL);

        while(cursor.moveToNext())
            users.add(cursor.getString(columnEmail));

        return users;
    }

    public Collection<LocalFile> getWorkspaceFiles(Workspace workspace) {
        SQLiteDatabase db = mInstance.getReadableDatabase();
        ArrayList<LocalFile> files = new ArrayList<>();

        Cursor cursor = db.query(
                FilesEntry.TABLE_NAME,
                null,
                FilesEntry.COLUMN_WORKSPACE_KEY + "=?",
                new String[]{ String.valueOf(workspace.getDatabaseId()) },
                null,
                null,
                null
        );

        int columnNameIndex = cursor.getColumnIndex(FilesEntry.COLUMN_FILE_NAME);
        int columnVersionIndex = cursor.getColumnIndex(FilesEntry.COLUMN_FILE_VERSION);

        while(cursor.moveToNext()) {
                files.add(new LocalFile(
                        workspace,
                        cursor.getString(columnNameIndex),
                        cursor.getInt(columnVersionIndex)
                ));
        }

        return files;
    }

    public List<LocalWorkspace> getWorkspacesInfo(User owner) {
        SQLiteDatabase db = mInstance.getReadableDatabase();

        Cursor workspaceCursor = db.rawQuery(
                "SELECT * FROM " + AirDeskContract.WorkspaceEntry.TABLE_NAME +
                        " WHERE " + WorkspaceEntry.COLUMN_OWNER_KEY + " = " + owner.getDatabaseId(), null);

        int columnIdIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry._ID);
        int columnNameIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_NAME);
        int columnQuotaIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_QUOTA);
        int columnIsPrivateIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE);

        List<LocalWorkspace> workspaces = new ArrayList<>();

        while (workspaceCursor.moveToNext()) {
            long workspaceId = workspaceCursor.getLong(columnIdIndex);
            LocalWorkspace workspace = new LocalWorkspace(
                    workspaceId,
                    workspaceCursor.getString(columnNameIndex),
                    owner,
                    workspaceCursor.getLong(columnQuotaIndex),
                    workspaceCursor.getInt(columnIsPrivateIndex) == 1 ? true : false);
            workspace.setTags(getWorkspaceTags(workspace));
            workspace.setAccessList(getWorkspaceAccessList(workspace));
            workspace.setFiles(getWorkspaceFiles(workspace));
            workspaces.add(workspace);
        }

        return workspaces;
    }


    public HashMap<Long, ArrayList<String>> getAllTagsInMap(){
        SQLiteDatabase db = mInstance.getReadableDatabase();
        HashMap<Long, ArrayList<String>> workspaceTags = new HashMap<>();
        Cursor cursor = db.query(
                TagsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        int workspaceIdIndex = cursor.getColumnIndex(TagsEntry.COLUMN_WORKSPACE_KEY);
        int tagNameIndex = cursor.getColumnIndex(TagsEntry.COLUMN_TAG_NAME);

        while(cursor.moveToNext()){
            long workspaceId = cursor.getLong(workspaceIdIndex);
            String tagName = cursor.getString(tagNameIndex);
            if(workspaceTags.containsKey(workspaceId))
                workspaceTags.get(workspaceId).add(tagName);
            else {
                workspaceTags.put(workspaceId,  new ArrayList<String>());
                workspaceTags.get(workspaceId).add(tagName);
            }
        }

        cursor.close();
        db.close();

        return workspaceTags;
    }


    public void updateWorkspace(long workspaceId, String workspaceName, Long quotaValue, Boolean isPrivate) {
        SQLiteDatabase db = mInstance.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(workspaceName != null)
            values.put(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_NAME, workspaceName);
        if(quotaValue != null)
            values.put(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_QUOTA, quotaValue.longValue());
        if(isPrivate != null)
            values.put(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE, isPrivate.booleanValue());

        db.update(AirDeskContract.WorkspaceEntry.TABLE_NAME, values, AirDeskContract.WorkspaceEntry._ID + "=" + workspaceId, null);

        db.close();
    }

    public void insertSubscriptionToUser(User user, String name, String[] tags)  {
        SQLiteDatabase db = mInstance.getWritableDatabase();

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < tags.length; i++) {
            builder.append(tags[i]);
            if(i < tags.length - 1)
                builder.append("|");
        }

        ContentValues values = new ContentValues();
        values.put(SubscriptionEntry.COLUMN_USER_KEY, user.getDatabaseId());
        values.put(SubscriptionEntry.COLUMN_NAME, name);
        values.put(SubscriptionEntry.COLUMN_TAGS, builder.toString());

        long rowId = db.insert(SubscriptionEntry.TABLE_NAME, null, values);
        db.close();

        if(rowId == -1)
            throw new RuntimeException("Subscription already exists.");
    }

    public void removeSubscriptionFromUser(User user, Subscription subscription){
        SQLiteDatabase db = mInstance.getWritableDatabase();

        String whereClause = String.format("%s=? AND %s=?;", SubscriptionEntry.COLUMN_USER_KEY, SubscriptionEntry.COLUMN_NAME);
        String[] whereArgs = new String[] { String.valueOf(user.getDatabaseId()), subscription.getName() };
        db.delete(SubscriptionEntry.TABLE_NAME, whereClause, whereArgs);

        db.close();
    }

    public List<Subscription> getSubscriptions(User user) {
        SQLiteDatabase db = mInstance.getReadableDatabase();
        ArrayList<Subscription> subscriptions = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + SubscriptionEntry.TABLE_NAME, null);

        int columnNameIndex = cursor.getColumnIndex(SubscriptionEntry.COLUMN_NAME);
        int columnTagsIndex = cursor.getColumnIndex(SubscriptionEntry.COLUMN_TAGS);

        while(cursor.moveToNext()){
            subscriptions.add(new Subscription(cursor.getString(columnNameIndex), cursor.getString(columnTagsIndex).split("|")));
        }

        cursor.close();
        db.close();

        return subscriptions;
    }
}

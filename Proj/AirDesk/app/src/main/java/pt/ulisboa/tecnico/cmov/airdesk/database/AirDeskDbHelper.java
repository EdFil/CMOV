package pt.ulisboa.tecnico.cmov.airdesk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileAlreadyExistsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceException;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.FilesEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.TagsEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.UsersEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.UsersWorkspacesEntry;

public class AirDeskDbHelper extends SQLiteOpenHelper {

    public static final String TAG = AirDeskDbHelper.class.getSimpleName();
    private static AirDeskDbHelper mInstance;

    public static final String DATABASE_NAME = "airdesk.db";
    public static final int DATABASE_VERSION = 29;

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
                AirDeskContract.WorkspaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_NAME + " TEXT NOT NULL, " +
                AirDeskContract.WorkspaceEntry.COLUMN_OWNER_KEY + " INTEGER NOT NULL, " +
                AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_QUOTA + " INTEGER NOT NULL, " +
                AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE + " INTEGER NOT NULL, " +
                AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_IS_LOCAL + " INTEGER NOT NULL, " +
                AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_USER + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + AirDeskContract.WorkspaceEntry.COLUMN_OWNER_KEY  + ") REFERENCES " + UsersEntry.TABLE_NAME + "( " + UsersEntry._ID + " ), " +
                "FOREIGN KEY (" + AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_USER + ") REFERENCES " + UsersEntry.TABLE_NAME + "( " + UsersEntry._ID + " ) " +
                " );";

        final String SQL_CREATE_TAG_TABLE = "CREATE TABLE " + TagsEntry.TABLE_NAME + " (" +
                TagsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TagsEntry.COLUMN_TAG_NAME + " TEXT NOT NULL, " +
                TagsEntry.COLUMN_WORKSPACE_KEY + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + TagsEntry.COLUMN_WORKSPACE_KEY  + ") REFERENCES " + AirDeskContract.WorkspaceEntry.TABLE_NAME + "( " + AirDeskContract.WorkspaceEntry._ID + " ), " +
                "UNIQUE (" + TagsEntry.COLUMN_TAG_NAME + ", " + TagsEntry.COLUMN_WORKSPACE_KEY + ") " +
                " );";

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UsersEntry.TABLE_NAME + " (" +
                UsersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UsersEntry.COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                UsersEntry.COLUMN_USER_NICK + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_FILE_TABLE = "CREATE TABLE " + AirDeskContract.FilesEntry.TABLE_NAME + " (" +
                AirDeskContract.FilesEntry.COLUMN_FILE_PATH + " TEXT UNIQUE PRIMARY KEY NOT NULL, " +
                AirDeskContract.FilesEntry.COLUMN_WORKSPACE_KEY + " INTEGER NOT NULL, " +
                AirDeskContract.FilesEntry.COLUMN_FILE_LAST_EDIT + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + AirDeskContract.FilesEntry.COLUMN_WORKSPACE_KEY  + ") REFERENCES " + AirDeskContract.WorkspaceEntry.TABLE_NAME + "( " + AirDeskContract.WorkspaceEntry._ID + " ) " +
                " );";

        final String SQL_CREATE_USER_WORKSPACE_TABLE = "CREATE TABLE " + UsersWorkspacesEntry.TABLE_NAME + " (" +
                UsersWorkspacesEntry.COLUMN_WORKSPACE_KEY + " INTEGER NOT NULL," +
                UsersWorkspacesEntry.COLUMN_USER_KEY + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + UsersWorkspacesEntry.COLUMN_WORKSPACE_KEY  + ") REFERENCES " + AirDeskContract.WorkspaceEntry.TABLE_NAME + "( " + AirDeskContract.WorkspaceEntry._ID + " )," +
                "FOREIGN KEY (" + UsersWorkspacesEntry.COLUMN_USER_KEY + ") REFERENCES " + UsersEntry.TABLE_NAME + "( " + UsersEntry._ID + " )," +
                "PRIMARY KEY (" + UsersWorkspacesEntry.COLUMN_WORKSPACE_KEY + ", " + UsersWorkspacesEntry.COLUMN_USER_KEY + ") " +
                " );";


        db.execSQL(SQL_CREATE_WORKSPACE_TABLE);
        db.execSQL(SQL_CREATE_TAG_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_FILE_TABLE);
        db.execSQL(SQL_CREATE_USER_WORKSPACE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AirDeskContract.WorkspaceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TagsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AirDeskContract.FilesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UsersWorkspacesEntry.TABLE_NAME);
        onCreate(db);
    }

    public long insertWorkspace(String workspaceName, long ownerId, long quotaValue, boolean isPrivate, boolean isLocal, long userId){
        SQLiteDatabase db = mInstance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_NAME, workspaceName);
        values.put(AirDeskContract.WorkspaceEntry.COLUMN_OWNER_KEY, ownerId);
        values.put(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_QUOTA, quotaValue);
        values.put(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE, isPrivate ? 1 : 0);
        values.put(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_IS_LOCAL, isLocal ? 1 : 0);
        values.put(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_USER, userId);

        long rowId = db.insert(AirDeskContract.WorkspaceEntry.TABLE_NAME, null, values);
        db.close();

        if(rowId == -1)
            throw new WorkspaceException("Could not add Workspace");

        return rowId;
    }

    public void deleteWorkspace(long workspaceId){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        db.delete(TagsEntry.TABLE_NAME, TagsEntry.COLUMN_WORKSPACE_KEY + "='" + workspaceId + "'", null);
        db.delete(UsersWorkspacesEntry.TABLE_NAME, UsersWorkspacesEntry.COLUMN_WORKSPACE_KEY + "='" + workspaceId + "'", null);
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

    public void addFileToWorkspace(long workspaceId, String filePath, String lastEdited) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AirDeskContract.FilesEntry.COLUMN_WORKSPACE_KEY, workspaceId);
        values.put(FilesEntry.COLUMN_FILE_PATH, filePath);
        values.put(FilesEntry.COLUMN_FILE_LAST_EDIT, lastEdited);

        long id = db.insert(AirDeskContract.FilesEntry.TABLE_NAME, null, values);
        db.close();
        if(id == -1)
            throw new FileAlreadyExistsException(filePath);
    }

    public void removeFileFromWorkspace(long workspaceId, String filePath) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String whereClause = FilesEntry.COLUMN_FILE_PATH + "=? AND " + FilesEntry.COLUMN_WORKSPACE_KEY + "=?";
        String[] whereArgs = new String[] { filePath, String.valueOf(workspaceId) };
        db.delete(AirDeskContract.FilesEntry.TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public void addTagToWorkspace(long workspaceId, String tag) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TagsEntry.COLUMN_WORKSPACE_KEY, workspaceId);
        values.put(TagsEntry.COLUMN_TAG_NAME, tag);

        db.insert(TagsEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void removeTagFromWorkspace(long workspaceId, String tag){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String whereClause = String.format("%s=? AND %s=?;", TagsEntry.COLUMN_WORKSPACE_KEY, TagsEntry.COLUMN_TAG_NAME);
        String[] whereArgs = new String[] { String.valueOf(workspaceId), tag };
        db.delete(TagsEntry.TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public void addUserToWorkspace(long workspaceId, long userId) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UsersWorkspacesEntry.COLUMN_WORKSPACE_KEY, workspaceId);
        values.put(UsersWorkspacesEntry.COLUMN_USER_KEY, userId);

        db.insert(UsersWorkspacesEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void removeUserFromWorkspace(long workspaceId, long userId){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String whereClause = String.format("%s=? AND %s=?;", UsersWorkspacesEntry.COLUMN_WORKSPACE_KEY, UsersWorkspacesEntry.COLUMN_USER_KEY);
        String[] whereArgs = new String[] { String.valueOf(workspaceId), String.valueOf(userId) };
        db.delete(UsersWorkspacesEntry.TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public Collection<Tag> getWorkspaceTags(long workspaceId){
        SQLiteDatabase db = mInstance.getReadableDatabase();
        ArrayList<Tag> tags = new ArrayList<>();

        Cursor cursor = db.query(
                TagsEntry.TABLE_NAME,
                null,
                TagsEntry.COLUMN_WORKSPACE_KEY+ "=?",
                new String[]{ String.valueOf(workspaceId) },
                null,
                null,
                null
        );

        int columnTagIndex = cursor.getColumnIndex(TagsEntry.COLUMN_TAG_NAME);

        while(cursor.moveToNext()) {
            tags.add(new Tag(cursor.getString(columnTagIndex)));
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

    public Collection<User> getWorkspaceUsers(long workspaceId){
        SQLiteDatabase db = mInstance.getReadableDatabase();
        ArrayList<User> users = new ArrayList<>();
        final String MY_QUERY = "SELECT * FROM " + UsersWorkspacesEntry.TABLE_NAME + " INNER JOIN " + UsersEntry.TABLE_NAME +
                " ON " + UsersWorkspacesEntry.TABLE_NAME + "." + UsersWorkspacesEntry.COLUMN_USER_KEY + " = " + UsersEntry.TABLE_NAME + "." + TagsEntry._ID +
                " WHERE " + UsersWorkspacesEntry.TABLE_NAME + "." + UsersWorkspacesEntry.COLUMN_WORKSPACE_KEY + " = " + workspaceId;

        Cursor cursor = db.rawQuery(MY_QUERY, null);
        int columnEmailIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_EMAIL);
        int columnNickIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_NICK);

        while(cursor.moveToNext()) {
            users.add(UserManager.getInstance().createUser(cursor.getString(columnEmailIndex), cursor.getString(columnNickIndex)));
        }

        return users;
    }

    public Collection<File> getWorkspaceFiles(long workspaceId) {
        SQLiteDatabase db = mInstance.getReadableDatabase();
        ArrayList<File> files = new ArrayList<>();

        Cursor cursor = db.query(
                FilesEntry.TABLE_NAME,
                null,
                FilesEntry.COLUMN_WORKSPACE_KEY + "=?",
                new String[]{ String.valueOf(workspaceId) },
                null,
                null,
                null
        );
        int columnPathIndex = cursor.getColumnIndex(FilesEntry.COLUMN_FILE_PATH);
        int columnEditIndex = cursor.getColumnIndex(FilesEntry.COLUMN_FILE_LAST_EDIT);

        while(cursor.moveToNext()) {
            try {
                File file = new File(cursor.getString(columnPathIndex));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = dateFormat.parse(cursor.getString(columnEditIndex));
                file.setLastModified(date.getTime());
                files.add(file);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return files;
    }

    public List<Workspace> getWorkspacesInfo(long userId) {
        HashMap<Long, ArrayList<Tag>> tagList = getAllTagsInMap();
        HashMap<Long, ArrayList<User>> userList = getAllUsersInMap();
        HashMap<Long, ArrayList<File>> fileList = getAllFilesInMap();

        SQLiteDatabase db = mInstance.getReadableDatabase();

        Cursor workspaceCursor = db.rawQuery("SELECT * FROM " + AirDeskContract.WorkspaceEntry.TABLE_NAME + " WHERE " + AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_USER + " = " + userId, null);

        int columnIdIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry._ID);
        int columnNameIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_NAME);
        int columnOwnerIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_OWNER_KEY);
        int columnQuotaIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_QUOTA);
        int columnIsPrivateIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE);
        int columnUserIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_USER);

        List<Workspace> workspaces = new ArrayList<>();

        while (workspaceCursor.moveToNext()) {
            User owner = UserManager.getInstance().getUserById(workspaceCursor.getLong(columnOwnerIndex));
            User user = UserManager.getInstance().getUserById(workspaceCursor.getLong(columnUserIndex));

            long workspaceId = workspaceCursor.getLong(columnIdIndex);

            if(owner == user) {
                workspaces.add(new LocalWorkspace(
                        workspaceId,
                        workspaceCursor.getString(columnNameIndex),
                        owner,
                        workspaceCursor.getLong(columnQuotaIndex),
                        workspaceCursor.getInt(columnIsPrivateIndex) == 1 ? true : false,
                        tagList.containsKey(workspaceId) ? tagList.get(workspaceId) : new ArrayList<Tag>(),
                        userList.containsKey(workspaceId) ? userList.get(workspaceId) : new ArrayList<User>(),
                        fileList.containsKey(workspaceId) ? fileList.get(workspaceId) : new ArrayList<File>(),
                        WorkspaceManager.getInstance()));
            } else {
                workspaces.add(new ForeignWorkspace(
                        workspaceId,
                        workspaceCursor.getString(columnNameIndex),
                        owner,
                        workspaceCursor.getLong(columnQuotaIndex),
                        workspaceCursor.getInt(columnIsPrivateIndex) == 1 ? true : false,
                        tagList.containsKey(workspaceId) ? tagList.get(workspaceId) : new ArrayList<Tag>(),
                        userList.containsKey(workspaceId) ? userList.get(workspaceId) : new ArrayList<User>(),
                        fileList.containsKey(workspaceId) ? fileList.get(workspaceId) : new ArrayList<File>(),
                        WorkspaceManager.getInstance()));
            }

        }

        return workspaces;
    }

    public List<ForeignWorkspace> getForeignWorkspacesByUserId(long userId) {
        HashMap<Long, ArrayList<Tag>> tagList = getAllTagsInMap();
        HashMap<Long, ArrayList<User>> userList = getAllUsersInMap();
        HashMap<Long, ArrayList<File>> fileList = getAllFilesInMap();

        SQLiteDatabase db = mInstance.getReadableDatabase();

        Cursor workspaceCursor = db.rawQuery("SELECT * FROM " + AirDeskContract.WorkspaceEntry.TABLE_NAME + " WHERE " + AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_USER + " = " + userId, null);

        int columnIdIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry._ID);
        int columnNameIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_NAME);
        int columnOwnerIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_OWNER_KEY);
        int columnQuotaIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_QUOTA);
        int columnIsPrivateIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE);
        int columnUserIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_USER);

        List<ForeignWorkspace> workspaces = new ArrayList<>();

        while (workspaceCursor.moveToNext()) {
            User owner = UserManager.getInstance().getUserById(workspaceCursor.getLong(columnOwnerIndex));
            User user = UserManager.getInstance().getUserById(workspaceCursor.getLong(columnUserIndex));

            long workspaceId = workspaceCursor.getLong(columnIdIndex);

                workspaces.add(new ForeignWorkspace(
                        workspaceId,
                        workspaceCursor.getString(columnNameIndex),
                        owner,
                        workspaceCursor.getLong(columnQuotaIndex),
                        workspaceCursor.getInt(columnIsPrivateIndex) == 1 ? true : false,
                        tagList.containsKey(workspaceId) ? tagList.get(workspaceId) : new ArrayList<Tag>(),
                        userList.containsKey(workspaceId) ? userList.get(workspaceId) : new ArrayList<User>(),
                        fileList.containsKey(workspaceId) ? fileList.get(workspaceId) : new ArrayList<File>(),
                        WorkspaceManager.getInstance()));

        }
        return workspaces;
    }

    public HashMap<Long, ArrayList<Tag>> getAllTagsInMap(){
        SQLiteDatabase db = mInstance.getReadableDatabase();
        HashMap<Long, ArrayList<Tag>> workspaceTags = new HashMap<Long, ArrayList<Tag>>();
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

    public HashMap<Long, ArrayList<User>> getAllUsersInMap(){
        SQLiteDatabase db = mInstance.getReadableDatabase();
        HashMap<Long, ArrayList<User>> workspaceUsers = new HashMap<>();
        final String MY_QUERY = "SELECT * FROM " + UsersWorkspacesEntry.TABLE_NAME + " INNER JOIN " + UsersEntry.TABLE_NAME +
                " ON " + UsersWorkspacesEntry.TABLE_NAME + "." + UsersWorkspacesEntry.COLUMN_USER_KEY + " = " + UsersEntry.TABLE_NAME + "." + TagsEntry._ID;

        Cursor cursor = db.rawQuery(MY_QUERY, null);

        int workspaceIdIndex = cursor.getColumnIndex(UsersWorkspacesEntry.COLUMN_WORKSPACE_KEY);
        int userEmailIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_EMAIL);
        int userNickIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_NICK);

        while(cursor.moveToNext()){
            long workspaceId = cursor.getLong(workspaceIdIndex);
            String userEmail = cursor.getString(userEmailIndex);
            String userNick = cursor.getString(userNickIndex);
            if(workspaceUsers.containsKey(workspaceId))
                workspaceUsers.get(workspaceId).add(UserManager.getInstance().createUser(userEmail, userNick));
            else {
                workspaceUsers.put(workspaceId, new ArrayList<User>());
                workspaceUsers.get(workspaceId).add(UserManager.getInstance().createUser(userEmail, userNick));
            }
        }

        cursor.close();
        db.close();

        return workspaceUsers;
    }

    public HashMap<Long,ArrayList<File>> getAllFilesInMap() {
        SQLiteDatabase db = mInstance.getReadableDatabase();
        HashMap<Long, ArrayList<File>> workspaceFiles = new HashMap<>();

        Cursor cursor = db.query(FilesEntry.TABLE_NAME, null, null, null, null, null, null);

        int workspaceIdIndex = cursor.getColumnIndex(FilesEntry.COLUMN_WORKSPACE_KEY);
        int fileNameIndex = cursor.getColumnIndex(FilesEntry.COLUMN_FILE_PATH);

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

    public User getUser(long userId) {
        SQLiteDatabase db = mInstance.getReadableDatabase();
        String whereClause = UsersEntry._ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(userId) };
        User user = null;

        Cursor cursor = db.query(
                UsersEntry.TABLE_NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        int columnEmailIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_EMAIL);
        int columnNickIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_NICK);

        if(cursor.moveToFirst())
            user = UserManager.getInstance().createUser(cursor.getString(columnEmailIndex), cursor.getString(columnNickIndex));

        cursor.close();
        db.close();

        return user;
    }

    public File getFile(long fileId) {
        SQLiteDatabase db = mInstance.getReadableDatabase();
        String whereClause = FilesEntry._ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(fileId) };
        File file = null;

        Cursor cursor = db.query(
                FilesEntry.TABLE_NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        int columnPathIndex = cursor.getColumnIndex(FilesEntry.COLUMN_FILE_PATH);
        int columnEditIndex = cursor.getColumnIndex(FilesEntry.COLUMN_FILE_LAST_EDIT);

        if(cursor.moveToFirst()) {
            try {
                file = new File(cursor.getString(columnPathIndex));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                date = dateFormat.parse(cursor.getString(columnEditIndex));
                file.setLastModified(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                file = null;
            }
        }

        cursor.close();
        db.close();

        return file;
    }

    public Workspace getWorkspace(long workspaceId) {
        SQLiteDatabase db = mInstance.getReadableDatabase();
        String whereClause = AirDeskContract.WorkspaceEntry._ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(workspaceId) };
        Workspace workspace = null;

        Cursor cursor = db.query(
                AirDeskContract.WorkspaceEntry.TABLE_NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        int columnDatabaseIndex = cursor.getColumnIndex(AirDeskContract.WorkspaceEntry._ID);
        int columnNameIndex = cursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_NAME);
        int columnQuotaIndex = cursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_QUOTA);
        int columnPrivacyIndex = cursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE);
        int columnOwnerIndex = cursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_OWNER_KEY);

        if(cursor.moveToFirst()){
            User owner = UserManager.getInstance().getUserById(cursor.getInt(columnOwnerIndex));
            Collection<Tag> tags = getWorkspaceTags(workspaceId);
            Collection<User> users = getWorkspaceUsers(workspaceId);
            Collection<File> files = getWorkspaceFiles(workspaceId);
            long id = cursor.getLong(columnDatabaseIndex);
            String name = cursor.getString(columnNameIndex);
            long quota = cursor.getLong(columnQuotaIndex);
            boolean isPrivate = cursor.getInt(columnPrivacyIndex) == 1 ? true : false;
            workspace = new LocalWorkspace(id, name, owner, quota, isPrivate, tags, users, files, WorkspaceManager.getInstance());
        }


        cursor.close();
        db.close();

        return workspace;
    }

    public boolean isWorkspaceNameAvailable(String workspaceName, String ownerEmail) {
        SQLiteDatabase db = mInstance.getReadableDatabase();
        final String MY_QUERY = "SELECT * FROM " + AirDeskContract.WorkspaceEntry.TABLE_NAME + " INNER JOIN " + UsersEntry.TABLE_NAME +
                " ON " + AirDeskContract.WorkspaceEntry.TABLE_NAME + "." + AirDeskContract.WorkspaceEntry.COLUMN_OWNER_KEY + " = " + UsersEntry.TABLE_NAME + "." + TagsEntry._ID +
                " WHERE " + AirDeskContract.WorkspaceEntry.TABLE_NAME + "." + AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_NAME + " = '" + workspaceName + "' AND " +
                UsersEntry.TABLE_NAME + "." + UsersEntry.COLUMN_USER_EMAIL + " = '" + ownerEmail + "'";

        Cursor cursor = db.rawQuery(MY_QUERY, null);

        return cursor.moveToFirst();
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

    public List<ForeignWorkspace> getForeignWorkspacesWithTags(long ownerDbId, String[] tags) {

        SQLiteDatabase db = mInstance.getReadableDatabase();
        String MY_QUERY =
                " SELECT DISTINCT *" +
                " FROM " + AirDeskContract.WorkspaceEntry.TABLE_NAME + " INNER JOIN " + TagsEntry.TABLE_NAME +
                " ON " + AirDeskContract.WorkspaceEntry.TABLE_NAME + "." + AirDeskContract.WorkspaceEntry._ID + " = " + TagsEntry.TABLE_NAME + "." + TagsEntry.COLUMN_WORKSPACE_KEY +
                " WHERE " + AirDeskContract.WorkspaceEntry.TABLE_NAME + "." + AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_USER + " != " + ownerDbId + " AND ( ";

        for(String tag : tags)
            MY_QUERY += TagsEntry.COLUMN_TAG_NAME + " = '" + tag + "' OR ";

        MY_QUERY = MY_QUERY.substring(0, MY_QUERY.length()-3);
        MY_QUERY += ");";

        Cursor workspaceCursor = db.rawQuery(MY_QUERY, null);

        List<ForeignWorkspace> foreignWorkspaceList = new ArrayList<>();


        int columnIdIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry._ID);
        int columnNameIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_NAME);
        int columnOwnerIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_OWNER_KEY);
        int columnQuotaIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_QUOTA);
        int columnIsPrivateIndex = workspaceCursor.getColumnIndex(AirDeskContract.WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE);

        while (workspaceCursor.moveToNext())
            foreignWorkspaceList.add(new ForeignWorkspace(
                    workspaceCursor.getLong(columnIdIndex),
                    workspaceCursor.getString(columnNameIndex),
                    UserManager.getInstance().getUserById(workspaceCursor.getLong(columnOwnerIndex)),
                    workspaceCursor.getLong(columnQuotaIndex),
                    workspaceCursor.getInt(columnIsPrivateIndex) == 1 ? true : false,
                    new ArrayList<Tag>(),
                    new ArrayList<User>(),
                    new ArrayList<File>(),
                    WorkspaceManager.getInstance()));

        db.close();
        workspaceCursor.close();

        ForeignWorkspace foreignWorkspace;
        for(int i = foreignWorkspaceList.size() - 1; i >= 0; i--) {
            foreignWorkspace = foreignWorkspaceList.get(i);
            if (foreignWorkspace.countWorkspaceWithName(foreignWorkspaceList, foreignWorkspace.getName()) > 1)
                foreignWorkspaceList.remove(i);
        }


        return foreignWorkspaceList;

    }
}

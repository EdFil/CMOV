package pt.ulisboa.tecnico.cmov.airdesk;

import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.UsersEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.TagsEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.WorkspaceEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskDbHelper;
import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;

;


public class DatabaseTest extends AndroidTestCase {

    public static final String TAG = DatabaseTest.class.getSimpleName();

    @Override
    public void setUp(){
        setContext(new RenamingDelegatingContext(getContext(), "test_"));
        mContext.deleteDatabase(AirDeskDbHelper.DATABASE_NAME);
        WorkspaceManager.initWorkspaceManager(mContext);
    }

    @Override
    protected void tearDown() throws Exception {
        mContext.deleteDatabase(AirDeskDbHelper.DATABASE_NAME);
    }

    public void testCustom(){
        User owner = new User("Edgar@email.com", "Edgar");
        Workspace workspace = WorkspaceManager.getInstance().addNewWorkspace("Workspace", owner, 100, true, new ArrayList<Tag>());
        WorkspaceManager.getInstance().addFileToWorkspace("File 1", workspace);
        WorkspaceManager.getInstance().addFileToWorkspace("File 2", workspace);
        WorkspaceManager.getInstance().addTagToWorkspace("Tag 1", workspace);
        WorkspaceManager.getInstance().addTagToWorkspace("Tag 2", workspace);
        WorkspaceManager.getInstance().addTagToWorkspace("Tag 3", workspace);
        WorkspaceManager.getInstance().addUserToWorkspace("user1@email.com", "User 1", workspace);
        WorkspaceManager.getInstance().addUserToWorkspace("user2@email.com", "User 2", workspace);
        WorkspaceManager.getInstance().addUserToWorkspace("user3@email.com", "User 3", workspace);
        WorkspaceManager.getInstance().addUserToWorkspace("user4@email.com", "User 4", workspace);

        Workspace workspace2 = WorkspaceManager.getInstance().addNewWorkspace("Workspace 2", owner, 1, true, new ArrayList<Tag>());
        WorkspaceManager.getInstance().addFileToWorkspace("File 1", workspace2);
        WorkspaceManager.getInstance().addFileToWorkspace("File 2", workspace2);
        WorkspaceManager.getInstance().addTagToWorkspace("Tag 1", workspace2);
        WorkspaceManager.getInstance().addTagToWorkspace("Tag 2", workspace2);
        WorkspaceManager.getInstance().addTagToWorkspace("Tag 3", workspace2);
        WorkspaceManager.getInstance().addUserToWorkspace("user1@email.com", "User 1", workspace2);
        WorkspaceManager.getInstance().addUserToWorkspace("user2@email.com", "User 2", workspace2);
        WorkspaceManager.getInstance().addUserToWorkspace("user3@email.com", "User 3", workspace2);
        WorkspaceManager.getInstance().addUserToWorkspace("user4@email.com", "User 4", workspace2);
        WorkspaceManager.getInstance().deleteWorkspace(workspace2);
    }

    public void testCreateDb() {
        mContext.deleteDatabase(AirDeskDbHelper.DATABASE_NAME);
        SQLiteDatabase db = AirDeskDbHelper.getInstance(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    private HashMap<Long, ArrayList<Tag>> getAllTagsInMap(SQLiteDatabase db){
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

        return workspaceTags;
    }

    private HashMap<Long, ArrayList<User>> getAllUsersInMap(SQLiteDatabase db){
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

        return workspaceUsers;
    }

//    public void testInsertReadDb() throws JSONException {
//
//        // Test data we're going to insert into the DB to see if it works.
//        final String testWorkspaceName = "Workspace Name";
//        final int testWorkspaceQuota = 10;
//        final String testWorkspaceOwner = "Owner Name";
//        final int testWorkspaceIsPrivate = 0;
//
//        // If there's an error in those massive SQL table creation Strings,
//        // errors will be thrown here when you try to get a writable database.
//        SQLiteDatabase db = AirDeskDbHelper.getInstance(mContext).getWritableDatabase();
//
//        // Create a new map of values, where column names are the keys
//        ContentValues values = new ContentValues();
//        values.put(WorkspaceEntry.COLUMN_WORKSPACE_NAME, testWorkspaceName);
//        values.put(WorkspaceEntry.COLUMN_WORKSPACE_OWNER, testWorkspaceOwner);
//        values.put(WorkspaceEntry.COLUMN_WORKSPACE_QUOTA, testWorkspaceQuota);
//        values.put(WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE, testWorkspaceIsPrivate);
//
//        long locationRowId;
//        locationRowId = db.insert(WorkspaceEntry.TABLE_NAME, null, values);
//
//        // Verify we got a row back.
//        assertTrue(locationRowId != -1);
//        Log.d(TAG, "New row id: " + locationRowId);
//
//        // A cursor is your primary interface to the query results.
//        Cursor cursor = db.query(
//                WorkspaceEntry.TABLE_NAME,  // Table to Query
//                null, // Columns to show
//                WorkspaceEntry.COLUMN_WORKSPACE_NAME + "=?", // Columns for the "where" clause
//                new String[] { testWorkspaceName }, // Values for the "where" clause
//                null, // columns to group by
//                null, // columns to filter by row groups
//                null  // sort order
//        );
//
//        // If possible, move to the first row of the query results.
//        if (cursor.moveToFirst()) {
//            // Get the value in each column by finding the appropriate column index.
//            int nameIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_NAME);
//            String name = cursor.getString(nameIndex);
//
//            int ownerIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_OWNER);
//            String owner = cursor.getString(ownerIndex);
//
//            int quotaIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_QUOTA);
//            int quota = cursor.getInt(quotaIndex);
//
//            int isPrivateIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE);
//            int isPrivate = cursor.getInt(isPrivateIndex);
//
//            assertEquals(testWorkspaceName, name);
//            assertEquals(testWorkspaceOwner, owner);
//            assertEquals(testWorkspaceQuota, quota);
//            assertEquals(testWorkspaceIsPrivate, isPrivate);
//
//        } else {
//            // That's weird, it works on MY machine...
//            fail("No values returned :(");
//        }
//        cursor.close();
//        db.close();
//    }

}


package pt.ulisboa.tecnico.cmov.cmov_proj;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import pt.ulisboa.tecnico.cmov.cmov_proj.database.AirDeskDbHelper;


/**
 * Created by edgar on 23-03-2015.
 */
public class DatabaseTest extends AndroidTestCase {

    public static final String TAG = DatabaseTest.class.getSimpleName();

    @Override
    public void setUp(){
        setContext(new RenamingDelegatingContext(getContext(), "test_"));
        mContext.deleteDatabase(AirDeskDbHelper.DATABASE_NAME);
    }

    @Override
    protected void tearDown() throws Exception {
        /*mContext.deleteDatabase(AirDeskDbHelper.DATABASE_NAME);*/
    }

    public void testCreateDb() {
        SQLiteDatabase db = AirDeskDbHelper.getInstance(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertWorkspace(){
        final String testWorkspaceName = "Cenas";
        final int testWorkspaceQuota = 1;
        final String testWorkspaceOwner = "E tal";
        final boolean testWorkspaceIsPrivate = true;

        AirDeskDbHelper.getInstance(mContext).insertWorkspace(
                testWorkspaceName,
                testWorkspaceOwner,
                testWorkspaceQuota,
                testWorkspaceIsPrivate);

        AirDeskDbHelper.getInstance(mContext).addTagsToWorkspace(1, new String[] {"Tag 1", "Tag 2", "Tag 3", "Tag 4"});
        AirDeskDbHelper.getInstance(mContext).removeTagsFromWorkspace(1, new String[]{"Tag 1", "Tag 3"});
        AirDeskDbHelper.getInstance(mContext).addUserToWorkspace(1, new String[]{"edgar@mail.com", "diogo@email.com"});
        AirDeskDbHelper.getInstance(mContext).removeUserFromWorkspace(1, new String[]{"diogo@email.com"});
        AirDeskDbHelper.getInstance(mContext).deleteWorkspace(1);
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
//                null, // Columns for the "where" clause
//                null, // Values for the "where" clause
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
//            int tagsIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_TAGS);
//            String tags = cursor.getString(tagsIndex);
//
//            int filesIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_FILES);
//            String files = cursor.getString(filesIndex);
//
//            int allowedUsersIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_ALLOWED_USERS);
//            String allowedUsers = cursor.getString(allowedUsersIndex);
//
//            assertEquals(testWorkspaceName, name);
//            assertEquals(testWorkspaceOwner, owner);
//            assertEquals(testWorkspaceQuota, quota);
//            assertEquals(testWorkspaceIsPrivate, isPrivate);
//            assertEquals(testWorkspaceTags, tags);
//            assertEquals(testWorkspaceFiles, files);
//            assertEquals(testWorkspaceAllowedUsers, allowedUsers);
//
//            cursor.close();
//        } else {
//            // That's weird, it works on MY machine...
//            fail("No values returned :(");
//        }
//        db.close();
//    }



}

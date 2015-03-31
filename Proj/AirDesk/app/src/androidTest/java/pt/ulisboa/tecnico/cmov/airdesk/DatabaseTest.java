package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import org.json.JSONException;

import java.io.File;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskContract.WorkspaceEntry;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskDbHelper;
import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;


public class DatabaseTest extends AndroidTestCase {

    public static final String TAG = DatabaseTest.class.getSimpleName();

    @Override
    public void setUp(){
        setContext(new RenamingDelegatingContext(getContext(), "test_"));
    }

    @Override
    protected void tearDown() throws Exception {
        mContext.deleteDatabase(AirDeskDbHelper.DATABASE_NAME);
    }

    public void testCreateDb() {
        mContext.deleteDatabase(AirDeskDbHelper.DATABASE_NAME);
        SQLiteDatabase db = AirDeskDbHelper.getInstance(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testCustom(){
        FileManager.deleteFolder(mContext, "Test_Workspace");
        Tag[] tags = new Tag[] { new Tag("Fun"), new Tag("Help") };
        User[] users = new User[] { new User("edgar@email.com", "EdFil") };
        File[] files = new File[] {  };
        WorkspaceManager.initWorkspaceManager(mContext);
        WorkspaceManager.getInstance().addNewWorkspace("Test_Workspace", "Edgar", 120102, false, tags, users, files);
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

package pt.ulisboa.tecnico.cmov.cmov_proj;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.cmov.cmov_proj.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.cmov_proj.database.AirDeskContract.WorkspaceEntry;
import pt.ulisboa.tecnico.cmov.cmov_proj.database.AirDeskDbHelper;


/**
 * Created by edgar on 23-03-2015.
 */
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
        SQLiteDatabase db = new AirDeskDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() throws JSONException {

        // Test data we're going to insert into the DB to see if it works.
        final String testWorkspaceName = "Workspace Name";
        final int testWorkspaceQuota = 10;
        final String testWorkspaceOwner = "Owner Name";
        final int testWorkspaceIsPrivate = 0;
        final String testWorkspaceTags = new JSONArray(Arrays.asList((new String[]{"Tag 1", "Tag 2", "Tag 3"}))).toString();
        final String testWorkspaceFiles = new JSONArray(Arrays.asList((new String[]{"File 1", "File 2"}))).toString();
        final String testWorkspaceAllowedUsers = new JSONArray(Arrays.asList((new String[]{"User 1, User 2, User 3, User 4"}))).toString();

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        AirDeskDbHelper dbHelper = new AirDeskDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_NAME, testWorkspaceName);
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_OWNER, testWorkspaceOwner);
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_QUOTA, testWorkspaceQuota);
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE, testWorkspaceIsPrivate);
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_TAGS, testWorkspaceTags);
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_FILES, testWorkspaceFiles);
        values.put(WorkspaceEntry.COLUMN_WORKSPACE_ALLOWED_USERS, testWorkspaceAllowedUsers);

        long locationRowId;
        locationRowId = db.insert(WorkspaceEntry.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(TAG, "New row id: " + locationRowId);

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                WorkspaceEntry.TABLE_NAME,  // Table to Query
                null, // Columns to show
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int nameIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_NAME);
            String name = cursor.getString(nameIndex);

            int ownerIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_OWNER);
            String owner = cursor.getString(ownerIndex);

            int quotaIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_QUOTA);
            int quota = cursor.getInt(quotaIndex);

            int isPrivateIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_IS_PRIVATE);
            int isPrivate = cursor.getInt(isPrivateIndex);

            int tagsIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_TAGS);
            String tags = cursor.getString(tagsIndex);

            int filesIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_FILES);
            String files = cursor.getString(filesIndex);

            int allowedUsersIndex = cursor.getColumnIndex(WorkspaceEntry.COLUMN_WORKSPACE_ALLOWED_USERS);
            String allowedUsers = cursor.getString(allowedUsersIndex);

            assertEquals(testWorkspaceName, name);
            assertEquals(testWorkspaceOwner, owner);
            assertEquals(testWorkspaceQuota, quota);
            assertEquals(testWorkspaceIsPrivate, isPrivate);
            assertEquals(testWorkspaceTags, tags);
            assertEquals(testWorkspaceFiles, files);
            assertEquals(testWorkspaceAllowedUsers, allowedUsers);

        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }
        dbHelper.close();
    }

}

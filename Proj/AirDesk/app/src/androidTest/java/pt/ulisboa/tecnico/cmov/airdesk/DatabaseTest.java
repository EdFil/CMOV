package pt.ulisboa.tecnico.cmov.airdesk;

import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

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
        WorkspaceManager.initWorkspaceManager(mContext);
    }

    @Override
    protected void tearDown() throws Exception {
        mContext.deleteDatabase(AirDeskDbHelper.DATABASE_NAME);
    }

    public void testCustom(){
        AirDeskDbHelper dbHelper = AirDeskDbHelper.getInstance(mContext);
        long edgarID = dbHelper.insertUser("edgar@email.com", "Edgar");
        long stevensID = dbHelper.insertUser("stevens@email.com", "Stevens");
        long brunoID = dbHelper.insertUser("bruno@email.com", "Bruno");
        long workspaceID = dbHelper.insertWorkspace("Workspace", edgarID, 1024, true);
        long workspace2ID = dbHelper.insertWorkspace("Workspace2", stevensID, 1024, false);
        dbHelper.addTagToWorkspace(workspace2ID, "Tag 1");
        dbHelper.addTagToWorkspace(workspace2ID, "Tag 2");
        dbHelper.addTagToWorkspace(workspace2ID, "Tag 3");
        dbHelper.removeTagFromWorkspace(workspace2ID, "Tag 2");
        dbHelper.addFileToWorkspace(workspaceID, "File 1", getDate());
        dbHelper.addFileToWorkspace(workspaceID, "File 2", getDate());
        dbHelper.addFileToWorkspace(workspaceID, "File 3", getDate());
        dbHelper.removeFileFromWorkspace(workspaceID, "File 2");
        dbHelper.addUserToWorkspace(workspaceID, stevensID);
        dbHelper.addUserToWorkspace(workspaceID, brunoID);
        dbHelper.addUserToWorkspace(workspaceID, edgarID);
        dbHelper.removeUserFromWorkspace(workspaceID, brunoID);
        ArrayList<Tag> tags = new ArrayList<>(dbHelper.getWorkspaceTags(workspace2ID));
        ArrayList<User> users = new ArrayList<>(dbHelper.getWorkspaceUsers(workspaceID));
        ArrayList<File> files = new ArrayList<>(dbHelper.getWorkspaceFiles(workspaceID));
        dbHelper.getAllTagsInMap();
        dbHelper.getAllFilesInMap();
        dbHelper.getAllUsersInMap();
    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(System.currentTimeMillis());
    }

}


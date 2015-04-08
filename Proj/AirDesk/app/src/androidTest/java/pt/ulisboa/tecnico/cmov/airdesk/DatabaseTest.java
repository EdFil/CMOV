package pt.ulisboa.tecnico.cmov.airdesk;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskDbHelper;
import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;

;


public class DatabaseTest extends AndroidTestCase {

    public static final String TAG = DatabaseTest.class.getSimpleName();

    @Override
    public void setUp(){
        setContext(new RenamingDelegatingContext(getContext(), "test_"));
        WorkspaceManager.initWorkspaceManager(mContext);
        UserManager.initUserManager(mContext);
    }

    @Override
    protected void tearDown() throws Exception {
        mContext.deleteDatabase(AirDeskDbHelper.DATABASE_NAME);
    }

    public void testCustom(){
        AirDeskDbHelper dbHelper = AirDeskDbHelper.getInstance(mContext);

//        User owner = UserManager.getInstance().createUser("edgar@email.com", "Edgar");
//        User bruno = UserManager.getInstance().createUser("bruno@email.com", "Bruno");
//        User stevens = UserManager.getInstance().createUser("stevens@email.com", "Stevens");
//        UserManager.getInstance().setOwner(owner);
//
//        Workspace workspace = WorkspaceManager.getInstance().addNewWorkspace("Workspace", owner, 1024, true, new ArrayList<Tag>());
//        File file = WorkspaceManager.getInstance().addFileToWorkspace("New File", workspace);
//        Tag tag = WorkspaceManager.getInstance().addTagToWorkspace("New Tag", workspace);
//        User user = WorkspaceManager.getInstance().addUserToWorkspace(bruno, workspace);
//        WorkspaceManager.getInstance().removeFileFromWorkspace(file, workspace);
//        WorkspaceManager.getInstance().removeTagFromWorkspace(tag, workspace);
//        WorkspaceManager.getInstance().removeUserFromWorkspace(user, workspace);
//        WorkspaceManager.getInstance().updateWorkspace(workspace, "Updated Name", 120120120l, false);
//        WorkspaceManager.getInstance().deleteAllWorkspaces();

//        long edgarID = dbHelper.insertUser("edgar@email.com", "Edgar");
//        long stevensID = dbHelper.insertUser("stevens@email.com", "Stevens");
//        long brunoID = dbHelper.insertUser("bruno@email.com", "Bruno");
//        long workspaceID = dbHelper.insertWorkspace("Workspace", edgarID, 1024, true);
//        long workspace2ID = dbHelper.insertWorkspace("Workspace2", stevensID, 1024, false);
//        dbHelper.addTagToWorkspace(workspace2ID, "Tag 1");
//        dbHelper.addTagToWorkspace(workspace2ID, "Tag 2");
//        dbHelper.addTagToWorkspace(workspace2ID, "Tag 3");
//        dbHelper.removeTagFromWorkspace(workspace2ID, "Tag 2");
//        dbHelper.addFileToWorkspace(workspaceID, "File 1", getDate());
//        dbHelper.addFileToWorkspace(workspaceID, "File 2", getDate());
//        dbHelper.addFileToWorkspace(workspaceID, "File 3", getDate());
//        dbHelper.removeFileFromWorkspace(workspaceID, "File 2");
//        dbHelper.addUserToWorkspace(workspaceID, stevensID);
//        dbHelper.addUserToWorkspace(workspaceID, brunoID);
//        dbHelper.addUserToWorkspace(workspaceID, edgarID);
//        dbHelper.removeUserFromWorkspace(workspaceID, brunoID);
//        ArrayList<Tag> tags = new ArrayList<>(dbHelper.getWorkspaceTags(workspace2ID));
//        ArrayList<User> users = new ArrayList<>(dbHelper.getWorkspaceUsers(workspaceID));
//        ArrayList<File> files = new ArrayList<>(dbHelper.getWorkspaceFiles(workspaceID));
//        dbHelper.getAllTagsInMap();
//        dbHelper.getAllFilesInMap();
//        dbHelper.getAllUsersInMap();
    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(System.currentTimeMillis());
    }

}


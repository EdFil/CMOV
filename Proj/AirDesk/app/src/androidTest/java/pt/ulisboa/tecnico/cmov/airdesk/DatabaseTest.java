package pt.ulisboa.tecnico.cmov.airdesk;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskDbHelper;

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

//    public void testCustom(){
//        AirDeskDbHelper dbHelper = AirDeskDbHelper.getInstance(mContext);
//
//        User edgar = UserManager.getInstance().createUser("edgar@email.com", "Edgar");
//        User bruno = UserManager.getInstance().createUser("bruno@email.com", "Bruno");
//        User stevens = UserManager.getInstance().createUser("stevens@email.com", "Stevens");
//        UserManager.getInstance().setOwner(edgar);
//
//        Workspace mWorkspace = WorkspaceManager.getInstance().createLocalWorkspace("Workspace", edgar, 1024, true, new ArrayList<Tag>());
//        File file = WorkspaceManager.getInstance().insertFileToWorkspace("New File", mWorkspace);
//        Tag tag = WorkspaceManager.getInstance().insertTagToWorkspace("New Tag", mWorkspace);
//        User user = WorkspaceManager.getInstance().insertUserToWorkspace(bruno, mWorkspace);
//        WorkspaceManager.getInstance().removeFileFromWorkspace(file, mWorkspace);
//        WorkspaceManager.getInstance().removeTagFromWorkspace(tag.getText(), mWorkspace);
//        WorkspaceManager.getInstance().removeUserFromWorkspace(user, mWorkspace);
//        WorkspaceManager.getInstance().updateWorkspace(mWorkspace, "Updated Name", 120120120l, false);
//
//        UserManager.getInstance().deleteUser(edgar);
//
////        long edgarID = dbHelper.insertUser("edgar@email.com", "Edgar");
////        long stevensID = dbHelper.insertUser("stevens@email.com", "Stevens");
////        long brunoID = dbHelper.insertUser("bruno@email.com", "Bruno");
////        long workspaceID = dbHelper.insertWorkspace("Workspace", edgarID, 1024, true);
////        long workspace2ID = dbHelper.insertWorkspace("Workspace2", stevensID, 1024, false);
////        dbHelper.insertTagToWorkspace(workspace2ID, "Tag 1");
////        dbHelper.insertTagToWorkspace(workspace2ID, "Tag 2");
////        dbHelper.insertTagToWorkspace(workspace2ID, "Tag 3");
////        dbHelper.removeTagFromWorkspace(workspace2ID, "Tag 2");
////        dbHelper.insertFileToWorkspace(workspaceID, "File 1", getDate());
////        dbHelper.insertFileToWorkspace(workspaceID, "File 2", getDate());
////        dbHelper.insertFileToWorkspace(workspaceID, "File 3", getDate());
////        dbHelper.removeFileFromWorkspace(workspaceID, "File 2");
////        dbHelper.insertUserToWorkspace(workspaceID, stevensID);
////        dbHelper.insertUserToWorkspace(workspaceID, brunoID);
////        dbHelper.insertUserToWorkspace(workspaceID, edgarID);
////        dbHelper.removeUserFromWorkspace(workspaceID, brunoID);
////        ArrayList<Tag> tags = new ArrayList<>(dbHelper.getWorkspaceTags(workspace2ID));
////        ArrayList<User> users = new ArrayList<>(dbHelper.getWorkspaceAccessList(workspaceID));
////        ArrayList<File> files = new ArrayList<>(dbHelper.getWorkspaceFiles(workspaceID));
////        dbHelper.getAllTagsInMap();
////        dbHelper.getAllFilesInMap();
////        dbHelper.getAllUsersInMap();
//    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(System.currentTimeMillis());
    }

}


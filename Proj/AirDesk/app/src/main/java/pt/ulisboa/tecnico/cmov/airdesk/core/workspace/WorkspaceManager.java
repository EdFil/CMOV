package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileAlreadyExistsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceAlreadyExistsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceNameIsEmptyException;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskDbHelper;
import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;

public class WorkspaceManager {

    // ------------------------
    //     Singleton Stuff
    // ------------------------

    public static final String TAG = WorkspaceManager.class.getSimpleName();

    private static WorkspaceManager mInstance;
    private List<Workspace> mWorkspaces;

    public static synchronized WorkspaceManager getInstance() {
        return mInstance;
    }

    public static synchronized void initWorkspaceManager(Context context) {
        mInstance = new WorkspaceManager(context);
    }

    // ------------------------
    //      Manager Stuff
    // ------------------------

    private Context mContext = null;
    SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected WorkspaceManager(Context context){
        mContext = context;
    }

    public Workspace addNewWorkspace(String name, User owner, long quota, boolean isPrivate, Collection<Tag> tags) {
        if(name.isEmpty())
            throw new WorkspaceNameIsEmptyException();
        if(AirDeskDbHelper.getInstance(getContext()).isWorkspaceNameAvailable(name, owner.getEmail()))
            throw new WorkspaceAlreadyExistsException();

        long workspaceId = AirDeskDbHelper.getInstance(getContext()).insertWorkspace(name, owner.getDatabaseId(), quota, isPrivate, owner.getDatabaseId());
        FileManager.createFolder(getContext(), name);
        for(Tag tag : tags) {
            AirDeskDbHelper.getInstance(getContext()).addTagToWorkspace(workspaceId, tag.getText());
        }
        AirDeskDbHelper.getInstance(getContext()).addUserToWorkspace(workspaceId, owner.getDatabaseId());

        ArrayList<File> files = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        users.add(owner);

        LocalWorkspace newWorkspace = new LocalWorkspace(workspaceId, name, owner, quota, isPrivate, tags, users, files, this);

        return newWorkspace;
    }

    public void deleteWorkspace(int workspaceIndex){
        Workspace workspace = mWorkspaces.get(workspaceIndex);
        AirDeskDbHelper.getInstance(getContext()).deleteWorkspace(workspace.getDatabaseId());
        FileManager.deleteFolder(getContext(), workspace.getName());
        mWorkspaces.remove(workspaceIndex);
    }

    public List<File> getFilesFromWorkspace(Workspace workspace) {
        return workspace.getFiles();
    }

    public File addFileToWorkspace(String fileName, Workspace workspace) {
        try {
            File file = FileManager.createFile(getContext(), workspace.getName(), fileName);
            AirDeskDbHelper.getInstance(getContext()).addFileToWorkspace(workspace.getDatabaseId(), file.getPath(), mDateFormat.format(file.lastModified()));
            workspace.addFile(file);
            return file;
        } catch (SQLiteConstraintException e) {
            throw new FileAlreadyExistsException(fileName);
        }
    }

    public void removeFileFromWorkspace(File file, Workspace workspace) {
        FileManager.deleteFile(getContext(), workspace.getName(), file.getName());
        AirDeskDbHelper.getInstance(getContext()).removeFileFromWorkspace(workspace.getDatabaseId(), file.getPath());
        workspace.removeFile(file);
    }

    public User addUserToWorkspace(User user, Workspace workspace) {
        // Create folder in internal storage
        AirDeskDbHelper.getInstance(getContext()).addUserToWorkspace(workspace.getDatabaseId(), user.getDatabaseId());
        workspace.addUser(user);
        return user;
    }

    public void removeUserFromWorkspace(User user, Workspace workspace) {
        AirDeskDbHelper.getInstance(getContext()).removeUserFromWorkspace(workspace.getDatabaseId(), user.getDatabaseId());
        workspace.removeUser(user);
    }

    public Tag addTagToWorkspace(String tagName, Workspace workspace) {
        // Create folder in internal storage
        Tag tag = new Tag(tagName);
        workspace.addTag(tag);
        AirDeskDbHelper.getInstance(getContext()).addTagToWorkspace(workspace.getDatabaseId(), tagName);
        return tag;
    }

    public void removeTagFromWorkspace(Tag tag, Workspace workspace) {
        workspace.removeTag(tag);
        AirDeskDbHelper.getInstance(getContext()).removeTagFromWorkspace(workspace.getDatabaseId(), tag.getText());
    }

    public Context getContext() {
        return mContext;
    }

    public long getSpaceAvailableInternalStorage() {
        return FileManager.getAvailableSpace(mContext);
    }


    // TODO : não devia estar no fragmento dos workspaces?
//    public void reloadWorkspaces() {
//
//        for(Workspace workspace : db.getAllLocalWorkspaceInfo()) {
//            mWorkspaceListAdapter.add(workspace);
//        }
////        mWorkspaceListAdapter.sort(new Comparator<Workspace>() {
////            @Override
////            public int compare(Workspace lhs, Workspace rhs) {
////                return lhs.getName().compareToIgnoreCase(rhs.getName());
////            }
////        });
//    }

    public List<Workspace> getWorkspacesFromDB() {
        AirDeskDbHelper db = AirDeskDbHelper.getInstance(getContext());
        mWorkspaces = db.getAllLocalWorkspaceInfo(UserManager.getInstance().getOwner().getDatabaseId());
        return mWorkspaces;
    }

    public List<Workspace> getWorkspaces() {
        return mWorkspaces;
    }

    public void deleteAllWorkspaces() {
        int i = mWorkspaces.size() - 1;
        while (i >= 0)
            deleteWorkspace(i--);
    }

    public Workspace getWorkspaceAtIndex(int workspaceIndex) {
        return mWorkspaces.get(workspaceIndex);
    }

    public void setWorkspaceAtIndex(int workspaceIndex, Workspace editedWorkspace){
        Workspace workspace = mWorkspaces.get(workspaceIndex);
        workspace = editedWorkspace;
    }
}

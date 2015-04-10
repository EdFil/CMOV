package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.EditText;

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
    private List<LocalWorkspace> mLocalWorkspaces;
    private List<ForeignWorkspace> mForeignWorkspaces;

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
        mLocalWorkspaces = new ArrayList<>();
        mForeignWorkspaces = new ArrayList<>();
    }

    public void refreshWorkspaceLists(){
        List<Workspace> workspaces = getWorkspacesFromDB();
        mLocalWorkspaces.clear();
        mForeignWorkspaces.clear();
        for(Workspace workspace : workspaces) {
            if(workspace instanceof LocalWorkspace)
                mLocalWorkspaces.add((LocalWorkspace) workspace);
            else
                mForeignWorkspaces.add((ForeignWorkspace) workspace);
        }
    }

    public Workspace addNewWorkspace(String name, User owner, long quota, boolean isPrivate, Collection<Tag> tags) {
        if(name.isEmpty())
            throw new WorkspaceNameIsEmptyException();
        if(AirDeskDbHelper.getInstance(getContext()).isWorkspaceNameAvailable(name, owner.getEmail()))
            throw new WorkspaceAlreadyExistsException();

        ArrayList<File> files = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        users.add(owner);

        LocalWorkspace newWorkspace = new LocalWorkspace(name, owner, quota, isPrivate, tags, users, files, this);

        long workspaceId = AirDeskDbHelper.getInstance(getContext()).insertWorkspace(name, owner.getDatabaseId(), quota, isPrivate, owner.getDatabaseId());
        FileManager.createFolder(getContext(), name);
        for(Tag tag : tags) {
            AirDeskDbHelper.getInstance(getContext()).addTagToWorkspace(workspaceId, tag.getText());
        }
        AirDeskDbHelper.getInstance(getContext()).addUserToWorkspace(workspaceId, owner.getDatabaseId());
        newWorkspace.setDatabaseId(workspaceId);

        mLocalWorkspaces.add(newWorkspace);

        return newWorkspace;
    }

    public void deleteWorkspace(int workspaceIndex){
        Workspace workspace = mLocalWorkspaces.get(workspaceIndex);
        AirDeskDbHelper.getInstance(getContext()).deleteWorkspace(workspace.getDatabaseId());
        FileManager.deleteFolder(getContext(), workspace.getName());
        mLocalWorkspaces.remove(workspaceIndex);
    }

    public void deleteWorkspace(Workspace workspace){
        mLocalWorkspaces.remove(workspace);
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
        } catch (Exception e) {
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

    public void removeTagFromWorkspace(String tag, Workspace workspace) {
        workspace.removeTagFromString(tag);
        AirDeskDbHelper.getInstance(getContext()).removeTagFromWorkspace(workspace.getDatabaseId(), tag);
    }

    public void updateWorkspace(Workspace workspace, String workspaceName, Long quotaValue, Boolean isPrivate){
            String oldName = workspace.getName();
            if (workspaceName != null) {
                workspace.setName(workspaceName);
            }
            if (quotaValue != null)
                workspace.setQuota(quotaValue.longValue());
            if (isPrivate != null)
                workspace.setIsPrivate(isPrivate.booleanValue());
            FileManager.renameFolder(getContext(), oldName, workspaceName);
            AirDeskDbHelper.getInstance(getContext()).updateWorkspace(workspace.getDatabaseId(), workspaceName, quotaValue, isPrivate);
    }

    public Context getContext() {
        return mContext;
    }

    public long getSpaceAvailableInternalStorage() {
        return FileManager.getAvailableSpace(mContext);
    }

    public List<Workspace> getWorkspacesFromDB() {
        AirDeskDbHelper db = AirDeskDbHelper.getInstance(getContext());
        return db.getWorkspacesInfo(UserManager.getInstance().getOwner().getDatabaseId());
    }


    public void deleteAllUserWorkspaces() {
        int i = mLocalWorkspaces.size() - 1;
        while (i >= 0) {
            deleteWorkspace(i--);
        }
    }

    public Workspace getWorkspaceAtIndex(int workspaceIndex) {
        return mLocalWorkspaces.get(workspaceIndex);
    }

    public void setWorkspaceAtIndex(int workspaceIndex, Workspace editedWorkspace){
        Workspace workspace = mLocalWorkspaces.get(workspaceIndex);
        workspace = editedWorkspace;
    }

    public List<LocalWorkspace> getLocalWorkspaces() {
        return mLocalWorkspaces;
    }
    public List<ForeignWorkspace> getForeignWorkspaces() {
        return mForeignWorkspaces;
    }
}

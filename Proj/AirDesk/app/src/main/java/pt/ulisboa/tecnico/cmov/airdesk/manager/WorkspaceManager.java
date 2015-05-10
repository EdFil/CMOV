package pt.ulisboa.tecnico.cmov.airdesk.manager;

import android.content.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileAlreadyExistsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
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
            if(workspace instanceof LocalWorkspace) {
                mLocalWorkspaces.add((LocalWorkspace) workspace);
            }
            else
                mForeignWorkspaces.add((ForeignWorkspace) workspace);
        }
    }

    public Workspace addLocalWorkspace(String name, User owner, long quota, boolean isPrivate, Collection<String> tags) {
        // Create workspace
        LocalWorkspace newWorkspace = new LocalWorkspace(name, owner, quota, isPrivate, tags);
        newWorkspace.addUser(owner);
        // Insert workspace in DB
        long workspaceId = AirDeskDbHelper.getInstance(getContext()).insertWorkspace(name, owner.getDatabaseId(), quota, isPrivate, true, owner.getDatabaseId());
        newWorkspace.setDatabaseId(workspaceId);
        for(String tag : tags) {
            AirDeskDbHelper.getInstance(getContext()).addTagToWorkspace(workspaceId, tag);
        }
        AirDeskDbHelper.getInstance(getContext()).addUserToWorkspace(workspaceId, owner.getDatabaseId());
        // Create folder for workspace
        FileManager.createFolder(getContext(), newWorkspace.getWorkspaceFolderName());
        // Add workspace to Workspace Manager
        mLocalWorkspaces.add(newWorkspace);

        return newWorkspace;
    }

    public Workspace addForeignWorkspace(User userReceiving, String name, User owner, long quota, boolean isPrivate) {
        User savedOwner = UserManager.getInstance().getOwner();
        UserManager.getInstance().setOwner(userReceiving);
        // Create workspace
        ForeignWorkspace newWorkspace = new ForeignWorkspace(name, owner, quota, isPrivate);
        // Insert workspace in DB
        long workspaceId = AirDeskDbHelper.getInstance(getContext()).insertWorkspace(name, owner.getDatabaseId(), quota, isPrivate, false, userReceiving.getDatabaseId());
        newWorkspace.setDatabaseId(workspaceId);
        // Create workspace Folder
        FileManager.createFolder(getContext(), newWorkspace.getWorkspaceFolderName());
        // Add Workspace to Workspace Manager
        mForeignWorkspaces.add(newWorkspace);

        UserManager.getInstance().setOwner(savedOwner);
        return newWorkspace;
    }

    public void deleteWorkspace(boolean isLocalWS, int workspaceIndex){
        if(isLocalWS) {
            Workspace workspace = mLocalWorkspaces.get(workspaceIndex);
            AirDeskDbHelper.getInstance(getContext()).deleteWorkspace(workspace.getDatabaseId());
            FileManager.deleteFolder(getContext(), workspace.getWorkspaceFolderName());
            mLocalWorkspaces.remove(workspaceIndex);
        }
        else{
            Workspace workspace = mForeignWorkspaces.get(workspaceIndex);
            AirDeskDbHelper.getInstance(getContext()).deleteWorkspace(workspace.getDatabaseId());
            FileManager.deleteFolder(getContext(), workspace.getWorkspaceFolderName());
            mForeignWorkspaces.remove(workspaceIndex);
        }
    }

    public void deleteAllUserWorkspaces(boolean isLocalWS) {
        if(isLocalWS) {
            int i = mLocalWorkspaces.size() - 1;
            while (i >= 0) {
                deleteWorkspace(isLocalWS, i--);
            }
        }
        else {
            int i = mForeignWorkspaces.size() - 1;
            while (i >= 0) {
                deleteWorkspace(isLocalWS, i--);
            }
        }
    }

    public void deleteWorkspace(Workspace workspace){
        mLocalWorkspaces.remove(workspace);
    }

    public List<File> getFilesFromWorkspace(Workspace workspace) {
        return workspace.getFiles();
    }

    public File addFileToWorkspace(String fileName, Workspace workspace) {
        try {
            File file = FileManager.createFile(getContext(), workspace.getWorkspaceFolderName(), fileName);
            AirDeskDbHelper.getInstance(getContext()).addFileToWorkspace(workspace.getDatabaseId(), file.getPath(), mDateFormat.format(file.lastModified()));
            workspace.addFile(file);
            return file;
        } catch (Exception e) {
            throw new FileAlreadyExistsException(fileName);
        }
    }

    public void removeFileFromWorkspace(File file, Workspace workspace) {
        FileManager.deleteFile(getContext(), workspace.getWorkspaceFolderName(), file.getName());
        AirDeskDbHelper.getInstance(getContext()).removeFileFromWorkspace(workspace.getDatabaseId(), file.getPath());
        workspace.removeFile(file);
    }

    public void removeAllFilesFromWorkspace(Workspace workspace) {
        List<File> allFiles = workspace.getFiles();
        int i = allFiles.size() - 1;
        while (i >= 0) {
            removeFileFromWorkspace(allFiles.get(i--), workspace);
        }
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

    public String addTagToWorkspace(String tag, Workspace workspace) {
        // Create folder in internal storage
        workspace.addTag(tag);
        AirDeskDbHelper.getInstance(getContext()).addTagToWorkspace(workspace.getDatabaseId(), tag);
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
                FileManager.renameFolder(getContext(), oldName, workspaceName);
            }
            if (quotaValue != null)
                workspace.setQuota(quotaValue.longValue());
            if (isPrivate != null)
                workspace.setIsPrivate(isPrivate.booleanValue());
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

    public List<ForeignWorkspace> getWorkspacesFromDB(long foreignId) {

        AirDeskDbHelper db = AirDeskDbHelper.getInstance(getContext());
        return db.getForeignWorkspacesByUserId(foreignId);
    }


    public Workspace getWorkspaceWithId(long databaseId) {
        for(Workspace workspace : mLocalWorkspaces)
            if(workspace.getDatabaseId() == databaseId)
                return workspace;
        for(Workspace workspace : mForeignWorkspaces)
            if(workspace.getDatabaseId() == databaseId)
                return workspace;
        return null;
    }

    public Workspace getWorkspaceAtIndex(boolean isLocalWS, int workspaceIndex) {
        if(isLocalWS)
            return mLocalWorkspaces.get(workspaceIndex);
        else
            return mForeignWorkspaces.get(workspaceIndex);
    }

    public List<Workspace> getWorkspacesWithTags(String... tags) {
        List<Workspace> workspaceList = new ArrayList<>();
        for(Workspace workspace : mLocalWorkspaces)
            if(workspace.hasAllTags(Arrays.asList(tags)))
                workspaceList.add(workspace);
        return workspaceList;
    }

    public List<ForeignWorkspace> getForeignWorkspacesWithTags(Collection<Tag> tags) {
        long ownerDbId = UserManager.getInstance().getOwner().getDatabaseId();
        return AirDeskDbHelper.getInstance(getContext()).getForeignWorkspacesWithTags(ownerDbId, tags);
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

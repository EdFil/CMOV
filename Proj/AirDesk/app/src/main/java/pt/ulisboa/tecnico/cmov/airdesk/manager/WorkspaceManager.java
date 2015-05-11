package pt.ulisboa.tecnico.cmov.airdesk.manager;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.LocalFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.MyFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.RemoteFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileAlreadyExistsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskDbHelper;

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

    public Workspace createLocalWorkspace(String name, long quota, boolean isPrivate, Collection<String> tags) {
        // Create workspace
        User owner = UserManager.getInstance().getOwner();
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
        FileManager.getInstance().createLocalFolder(newWorkspace.getWorkspaceFolderName());
        // Add workspace to Workspace Manager
        mLocalWorkspaces.add(newWorkspace);

        return newWorkspace;
    }

    public ForeignWorkspace mountForeignWorkspace(JSONObject workspaceJSON) throws JSONException {
        String workspaceName = workspaceJSON.getString(ForeignWorkspace.NAME_KEY);
        User workspaceOwner = UserManager.getInstance().createUser(workspaceJSON.getJSONObject(ForeignWorkspace.OWNER_KEY));
        long maxQuota = workspaceJSON.getLong(ForeignWorkspace.MAX_QUOTA_KEY);
        boolean isPrivate = workspaceJSON.getBoolean(ForeignWorkspace.IS_PRIVATE_KEY);
        List<String> tags = new ArrayList<>();
        JSONArray tagArray = workspaceJSON.getJSONArray(ForeignWorkspace.TAGS_KEY);
        for(int i = 0; i < tagArray.length(); i++)
            tags.add(tagArray.getString(i));
        List<User> users = new ArrayList<>();
        JSONArray userArray = workspaceJSON.getJSONArray(ForeignWorkspace.USERS_KEY);
        for(int i = 0; i < userArray.length(); i++)
            users.add(UserManager.getInstance().createUser(userArray.getJSONObject(i)));
        List<RemoteFile> files = new ArrayList<>();
        JSONArray fileArray = workspaceJSON.getJSONArray(ForeignWorkspace.FILES_KEY);

        ForeignWorkspace newWorkspace = new ForeignWorkspace(
                workspaceName,
                workspaceOwner,
                maxQuota,
                isPrivate,
                tags,
                users
        );
        // Insert workspace in DB
        long workspaceId = AirDeskDbHelper.getInstance(getContext()).insertWorkspace(workspaceName, workspaceOwner.getDatabaseId(), maxQuota, isPrivate, false, UserManager.getInstance().getOwner().getDatabaseId());
        newWorkspace.setDatabaseId(workspaceId);
        // Create workspace Folder
        FileManager.getInstance().createLocalFolder(newWorkspace.getWorkspaceFolderName());

        for(int i = 0; i < fileArray.length(); i++)
            files.add(new RemoteFile(FileManager.getInstance().createTempFile(newWorkspace.getWorkspaceFolderName(), fileArray.getString(i))));

        // Add Workspace to Workspace Manager
        mForeignWorkspaces.add(newWorkspace);

        return newWorkspace;
    }

    public void deleteLocalWorkspace(int workspaceIndex) {
        Workspace workspace = mLocalWorkspaces.get(workspaceIndex);
        AirDeskDbHelper.getInstance(getContext()).deleteWorkspace(workspace.getDatabaseId());
        FileManager.getInstance().deleteLocalFolder(workspace.getWorkspaceFolderName());
        mLocalWorkspaces.remove(workspaceIndex);
    }

    public void unmountForeignWorkspace(int workspaceIndex) {
        Workspace workspace = mForeignWorkspaces.get(workspaceIndex);
        AirDeskDbHelper.getInstance(getContext()).deleteWorkspace(workspace.getDatabaseId());
        FileManager.getInstance().deleteTempFolder(workspace.getWorkspaceFolderName());
        mForeignWorkspaces.remove(workspaceIndex);
    }

    public void deletaAllLocalWorkspaces() {
        for(int i = mLocalWorkspaces.size() - 1; i > 0; i--)
            deleteLocalWorkspace(i--);
    }

    public void unmountAllForeignWorkspaces() {
        for(int i = mForeignWorkspaces.size() - 1; i > 0; i--)
            unmountForeignWorkspace(i--);
    }

    public List<MyFile> getFilesFromWorkspace(Workspace workspace) {
        return workspace.getFiles();
    }

    public LocalFile addFileToWorkspace(String fileName, Workspace workspace) {
        LocalFile localFile = null;
        try {
            File file = FileManager.getInstance().createLocalFile(workspace.getWorkspaceFolderName(), fileName);
            localFile = new LocalFile(file);
            AirDeskDbHelper.getInstance(getContext()).addFileToWorkspace(workspace.getDatabaseId(), file.getPath(), mDateFormat.format(file.lastModified()));
            workspace.addFile(localFile);
        } catch (Exception e) {
            throw new FileAlreadyExistsException(fileName);
        } finally {
            return localFile;
        }
    }

    public void removeFileFromWorkspace(LocalFile file, Workspace workspace) {
        FileManager.getInstance().deleteLocalFile(workspace.getWorkspaceFolderName(), file.getFile().getName());
        AirDeskDbHelper.getInstance(getContext()).removeFileFromWorkspace(workspace.getDatabaseId(), file.getFile().getPath());
        workspace.removeFile(file);
    }

    public void removeAllFilesFromWorkspace(Workspace workspace) {
        for(int i = workspace.getFiles().size(); i > 0; i--)
            removeFileFromWorkspace((LocalFile)workspace.getFiles().get(i), workspace);
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

//    public void updateWorkspace(Workspace workspace, String workspaceName, Long quotaValue, Boolean isPrivate){
//            String oldName = workspace.getName();
//            if (workspaceName != null) {
//                workspace.setName(workspaceName);
//                FileManager.getInstance().
//                FileManager.renameFolder(getContext(), oldName, workspaceName);
//            }
//            if (quotaValue != null)
//                workspace.setQuota(quotaValue.longValue());
//            if (isPrivate != null)
//                workspace.setIsPrivate(isPrivate.booleanValue());
//            AirDeskDbHelper.getInstance(getContext()).updateWorkspace(workspace.getDatabaseId(), workspaceName, quotaValue, isPrivate);
//    }

    public Context getContext() {
        return mContext;
    }

    public long getSpaceAvailableInternalStorage() {
        return FileManager.getInstance().getAvailableSpace();
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

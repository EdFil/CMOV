package pt.ulisboa.tecnico.cmov.airdesk.manager;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.LocalFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.RemoteFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskDbHelper;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

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

    public List<LocalWorkspace> getLocalWorkspaces() {
        return mLocalWorkspaces;
    }
    public List<ForeignWorkspace> getForeignWorkspaces() {
        return mForeignWorkspaces;
    }

    public void loadLocalWorkspaces(){
        List<LocalWorkspace> workspaces = getWorkspacesFromDB();
        mLocalWorkspaces.clear();

        for(LocalWorkspace workspace : workspaces)
            mLocalWorkspaces.add(workspace);
    }

    public Workspace createLocalWorkspace(String name, long quota, boolean isPrivate, Collection<String> tags) {
        // Create workspace
        User owner = UserManager.getInstance().getOwner();
        LocalWorkspace newWorkspace = new LocalWorkspace(name, owner, quota, isPrivate, tags);
        newWorkspace.addAccessToUser(owner.getEmail());
        // Insert workspace in DB
        long workspaceId = AirDeskDbHelper.getInstance(getContext()).insertWorkspace(name, owner.getDatabaseId(), quota, isPrivate, true, owner.getDatabaseId());
        newWorkspace.setDatabaseId(workspaceId);
        for(String tag : tags) {
            AirDeskDbHelper.getInstance(getContext()).insertTagToWorkspace(newWorkspace, tag);
        }
        AirDeskDbHelper.getInstance(getContext()).insertUserToWorkspace(newWorkspace, owner.getEmail());
        // Create folder for workspace
        FileManager.getInstance().createLocalFolder(newWorkspace.getWorkspaceFolderName());
        // Add workspace to Workspace Manager
        mLocalWorkspaces.add(newWorkspace);

        return newWorkspace;
    }

    public void mountForeignWorkspacesFromJSON(String output) {
        try {
            // Get the response as a JSONObject
            JSONObject response = new JSONObject(output);

            // Check if has error
            if (response.has(Constants.ERROR_KEY))
                throw new RuntimeException(response.getString(Constants.ERROR_KEY));

            // Get the list of workspaces returned and add them to our list
            JSONArray workspaceArray = response.getJSONArray(Constants.WORKSPACE_LIST_KEY);
            for(int i = 0; i < workspaceArray.length(); i++) {
                mountForeignWorkspace(workspaceArray.getJSONObject(i));
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public ForeignWorkspace mountForeignWorkspace(JSONObject workspaceJSON) throws JSONException {
        // Get main info
        String workspaceName = workspaceJSON.getString(ForeignWorkspace.NAME_KEY);
        User workspaceOwner = UserManager.getInstance().createUser(workspaceJSON.getJSONObject(ForeignWorkspace.OWNER_KEY));
        long maxQuota = workspaceJSON.getLong(ForeignWorkspace.MAX_QUOTA_KEY);
        boolean isPrivate = workspaceJSON.getBoolean(ForeignWorkspace.IS_PRIVATE_KEY);

        // Create workspace
        ForeignWorkspace newWorkspace = new ForeignWorkspace(
                workspaceName,
                workspaceOwner,
                maxQuota,
                isPrivate
        );

        // Build tag list
        JSONArray tagArray = workspaceJSON.getJSONArray(ForeignWorkspace.TAGS_KEY);
        for(int i = 0; i < tagArray.length(); i++)
            newWorkspace.addTag(tagArray.getString(i));

        // Build access list
        JSONArray accessListArray = workspaceJSON.getJSONArray(ForeignWorkspace.ACCESS_LIST_KEY);
        for(int i = 0; i < accessListArray.length(); i++)
            newWorkspace.addAccessToUser(accessListArray.getString(i));

        JSONArray fileArray = workspaceJSON.getJSONArray(ForeignWorkspace.FILES_KEY);
        for(int i = 0; i < fileArray.length(); i++)
            newWorkspace.addFile(new RemoteFile(newWorkspace, fileArray.getJSONObject(i)));

        // Create workspace Folder
        FileManager.getInstance().createTempFolder(newWorkspace.getWorkspaceFolderName());

        // Add Workspace to Workspace Manager
        if(!containsWorkspace(mForeignWorkspaces, newWorkspace))
            mForeignWorkspaces.add(newWorkspace);

        return newWorkspace;
    }

    public boolean containsWorkspace(Collection<ForeignWorkspace> workspaceCollection, Workspace workspaceToAdd) {
        String wsToAddName = workspaceToAdd.getName();
        String wsToAddOwnerEmail = workspaceToAdd.getOwner().getEmail();
        for(ForeignWorkspace workspace : workspaceCollection)
            if(workspace.getOwner().getEmail().equals(wsToAddOwnerEmail) && workspace.getName().equals(wsToAddName))
                return true;
        return false;
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

    public void unmountForeignWorkspace(ForeignWorkspace workspace) {
        AirDeskDbHelper.getInstance(getContext()).deleteWorkspace(workspace.getDatabaseId());
        FileManager.getInstance().deleteTempFolder(workspace.getWorkspaceFolderName());
        mForeignWorkspaces.remove(workspace);
    }

    public void deletaAllLocalWorkspaces() {
        for(int i = mLocalWorkspaces.size() - 1; i >= 0; i--)
            deleteLocalWorkspace(i);
    }

    public void unmountAllForeignWorkspaces() {
        for(int i = mForeignWorkspaces.size() - 1; i >= 0; i--)
            unmountForeignWorkspace(i);
    }

    public LocalFile addFileToWorkspace(String fileName, LocalWorkspace workspace) {
            AirDeskDbHelper.getInstance(getContext()).insertFileToWorkspace(workspace, fileName, 0);
            LocalFile localFile = new LocalFile(workspace, fileName, 0);
            workspace.addFile(localFile);
            return localFile;
    }

    public void removeFileFromWorkspace(LocalFile file, LocalWorkspace workspace) {
        FileManager.getInstance().deleteLocalFile(workspace.getWorkspaceFolderName(), file.getName());
        AirDeskDbHelper.getInstance(getContext()).removeFileFromWorkspace(workspace, file.getName());
        workspace.removeFile(file);
    }

    public void removeAllFilesFromWorkspace(LocalWorkspace workspace) {
        for(int i = workspace.getFiles().size(); i >= 0; i--)
            removeFileFromWorkspace((LocalFile)workspace.getFiles().get(i), workspace);
    }

    public void addAccessToUser(String userEmail, Workspace workspace) {
        // Create folder in internal storage
        AirDeskDbHelper.getInstance(getContext()).insertUserToWorkspace(workspace, userEmail);
        workspace.addAccessToUser(userEmail);
    }

    public void removeAccessToUser(String userEmail, Workspace workspace) {
        AirDeskDbHelper.getInstance(getContext()).removeUserFromWorkspace(workspace, userEmail);
        workspace.removeAccessToUser(userEmail);
    }

    public String addTagToWorkspace(String tag, Workspace workspace) {
        // Create folder in internal storage
        workspace.addTag(tag);
        AirDeskDbHelper.getInstance(getContext()).insertTagToWorkspace(workspace, tag);
        return tag;
    }

    public void removeTagFromWorkspace(String tag, Workspace workspace) {
        workspace.removeTag(tag);
        AirDeskDbHelper.getInstance(getContext()).removeTagFromWorkspace(workspace, tag);
    }


    public Context getContext() {
        return mContext;
    }

    public long getSpaceAvailableInternalStorage() {
        return FileManager.getInstance().getAvailableSpace();
    }

    public List<LocalWorkspace> getWorkspacesFromDB() {
        AirDeskDbHelper db = AirDeskDbHelper.getInstance(getContext());
        return db.getWorkspacesInfo(UserManager.getInstance().getOwner());
    }

    public LocalWorkspace getLocalWorkspaceWithId(long databaseId) {
        for(LocalWorkspace workspace : mLocalWorkspaces)
            if(workspace.getDatabaseId() == databaseId)
                return workspace;
        return null;
    }

    public LocalWorkspace getLocalWorkspaceWithName(String workspaceName) {
        for(LocalWorkspace workspace : mLocalWorkspaces)
            if(workspace.getName().equals(workspaceName))
                return workspace;
        return null;
    }

    public ForeignWorkspace getForeignWorkspaceWithId(long databaseId) {
        for(ForeignWorkspace workspace : mForeignWorkspaces)
            if(workspace.getDatabaseId() == databaseId)
                return workspace;
        return null;
    }


    public List<LocalWorkspace> getLocalWorkspacesToMount(String email, String... tags) {
        List<LocalWorkspace> workspaceList = new ArrayList<>();
        for(LocalWorkspace workspace : mLocalWorkspaces)
            if (workspace.containsAtLeastOneTag(Arrays.asList(tags)) || workspace.getAccessList().contains(email))
                workspaceList.add(workspace);

        return workspaceList;
    }

    public List<ForeignWorkspace> getForeignWorkspacesWithTags(String... tags) {
        List<ForeignWorkspace> workspaceList = new ArrayList<>();
        for(ForeignWorkspace workspace : mForeignWorkspaces)
            if(workspace.containsAtLeastOneTag(Arrays.asList(tags)))
                workspaceList.add(workspace);
        return workspaceList;
    }

    // used without network
    public List<JSONObject> getJsonWorkspacesWithTags(String[] tags) {
        List<JSONObject> jsonWorkspaceList = new ArrayList<>();
        for(LocalWorkspace workspace : mLocalWorkspaces)
            if(workspace.containsAtLeastOneTag(Arrays.asList(tags)))
                jsonWorkspaceList.add(workspace.toJSON());
        return jsonWorkspaceList;
    }

    public void unmountForeignWorkspacesWithTags(String[] tags) {
        List<ForeignWorkspace> foreignWorkspaces = getForeignWorkspacesWithTags(tags);
        for(ForeignWorkspace foreignWorkspace : foreignWorkspaces)
            unmountForeignWorkspace(foreignWorkspace);
    }
}
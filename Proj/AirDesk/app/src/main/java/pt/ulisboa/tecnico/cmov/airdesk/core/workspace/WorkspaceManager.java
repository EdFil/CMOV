package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.ListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.FileListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
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
    private WorkspaceListAdapter mWorkspaceListAdapter;

    protected WorkspaceManager(Context context){
        mContext = context;
        mWorkspaceListAdapter = new WorkspaceListAdapter(getContext(), new ArrayList<Workspace>());
    }

    public Workspace addNewWorkspace(String name, User owner, long quota, boolean isPrivate, Collection<Tag> tags) {
        if(name.isEmpty())
            throw new WorkspaceNameIsEmptyException();
        if(AirDeskDbHelper.getInstance(getContext()).isWorkspaceNameAvailable(name, owner.getEmail()))
            throw new WorkspaceAlreadyExistsException();

        long workspaceId = AirDeskDbHelper.getInstance(getContext()).insertWorkspace(name, 0, quota, isPrivate);
        FileManager.createFolder(getContext(), name);
        for(Tag tag : tags) {
            AirDeskDbHelper.getInstance(getContext()).addTagToWorkspace(workspaceId, tag.getText());
        }
        AirDeskDbHelper.getInstance(getContext()).addUserToWorkspace(workspaceId, owner.getDatabaseId());

        ArrayList<File> files = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        users.add(owner);

        LocalWorkspace newWorkspace = new LocalWorkspace(workspaceId, name, owner, quota, isPrivate, tags, users, files, this);

        mWorkspaceListAdapter.add(newWorkspace);
        return newWorkspace;
    }

    public void deleteWorkspace(Workspace workspace){
        AirDeskDbHelper.getInstance(getContext()).deleteWorkspace(workspace.getDatabaseId());
        FileManager.deleteFolder(getContext(), workspace.getName());
        mWorkspaceListAdapter.remove(workspace);
    }

    public List<File> getFilesFromWorkspace(Workspace workspace) {
        return workspace.getFiles();
    }

    public void addFileToWorkspace(String fileName, Workspace workspace) {
        // Create folder in internal storage
        try {
            File file = FileManager.createFile(getContext(), workspace.getName(), fileName);
            AirDeskDbHelper.getInstance(getContext()).addFileToWorkspace(workspace.getDatabaseId(), file.getPath());
            workspace.addFile(file);
            return;
        } catch (SQLiteConstraintException e) {
            throw new FileAlreadyExistsException(fileName);
        }
    }

    public void removeFileFromWorkspace(File file, Workspace workspace) {
        workspace.removeFile(file);
        FileManager.deleteFile(getContext(), workspace.getName(), file.getName());
        AirDeskDbHelper.getInstance(getContext()).removeFileFromWorkspace(workspace.getDatabaseId(), file.getName());
    }

    public void addUserToWorkspace(String userEmail, String userNick, Workspace workspace) {
        // Create folder in internal storage
        User user = UserManager.getInstance().createUser(userEmail, userNick);
        workspace.addUser(user);
//        AirDeskDbHelper.getInstance(getContext()).addUsersToWorkspace(workspace, Arrays.asList(new User[] { user }));
    }

    public void removeUserFromWorkspace(User user, Workspace workspace) {
        workspace.removeUser(user);
        //AirDeskDbHelper.getInstance(getContext()).removeUserFromWorkspace(workspace.getDatabaseId(), user.get);
    }

    public void addTagToWorkspace(String tagName, Workspace workspace) {
        // Create folder in internal storage
        Tag tag = new Tag(tagName);
        workspace.addTag(tag);
//        AirDeskDbHelper.getInstance(getContext()).addTagsToWorkspace(workspace, Arrays.asList(new Tag[] { tag }));
    }

    public void removeTagFromWorkspace(Tag tag, Workspace workspace) {
        workspace.removeTag(tag);
        //TODO: AirDeskDbHelper.getInstance(getContext()).removeTagFromWorkspace(workspace, tag);
    }

    public void loadExistingWorkspace(String name, User owner, long quota, boolean isPrivate, Collection<Tag> tags, Collection<User> users, Collection<File> files) {
        mWorkspaceListAdapter.add(new Workspace(name, owner, quota, isPrivate, tags, users, files, this));
    }

    public Context getContext() {
        return mContext;
    }

    public long getSpaceAvailableInternalStorage() {
        return FileManager.getAvailableSpace(mContext);
    }

    public ListAdapter getWorkspaceListAdapter() {
        return mWorkspaceListAdapter;
    }


    // TODO : não devia estar no fragmento dos workspaces?
    public void reloadWorkspaces() {
        AirDeskDbHelper db = AirDeskDbHelper.getInstance(getContext());
        mWorkspaceListAdapter.clear();
        for(Workspace workspace : db.getAllLocalWorkspaceInfo()) {
            mWorkspaceListAdapter.add(workspace);
        }
        mWorkspaceListAdapter.sort(new Comparator<Workspace>() {
            @Override
            public int compare(Workspace lhs, Workspace rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
    }



    public void deleteAllWorkspaces() {
        int i = mWorkspaceListAdapter.getCount() - 1;
        while(i >= 0)
            deleteWorkspace(mWorkspaceListAdapter.getItem(i--));
    }

    public Workspace getWorkspaceAtIndex(int workspaceIndex) {
        return mWorkspaceListAdapter.getItem(workspaceIndex);
    }
}

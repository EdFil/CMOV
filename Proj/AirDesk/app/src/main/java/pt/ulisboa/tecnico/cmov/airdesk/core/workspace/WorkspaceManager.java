package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import android.content.Context;
import android.widget.ListAdapter;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileAlreadyExistsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceAlreadyExistsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceNameIsEmptyException;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskDbHelper;
import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;

/**
 * Created by edgar on 31-03-2015.
 */
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

    public void addNewWorkspace(String name, User owner, long quota, boolean isPrivate, Collection<Tag> tags) {
        if(name.isEmpty())
            throw new WorkspaceNameIsEmptyException();
        if(!FileManager.isWorkspaceNameAvailable(getContext(), name))
            throw new WorkspaceAlreadyExistsException();
        ArrayList<File> files = new ArrayList<File>();
        ArrayList<User> users = new ArrayList<User>();
        users.add(owner);
        Workspace newWorkspace = new Workspace(name, owner, quota, isPrivate, tags, users, files, this);
        FileManager.createFolder(getContext(), name);
        AirDeskDbHelper.getInstance(getContext()).insertWorkspace(newWorkspace);
        AirDeskDbHelper.getInstance(getContext()).addTagsToWorkspace(newWorkspace, newWorkspace.getTags());
        AirDeskDbHelper.getInstance(getContext()).addUsersToWorkspace(newWorkspace, newWorkspace.getUsers());
        // TODO: Add files to workspace
        mWorkspaceListAdapter.add(newWorkspace);
    }

    public void deleteWorkspace(Workspace workspace){
        AirDeskDbHelper.getInstance(getContext()).deleteWorkspace(workspace);
        FileManager.deleteFolder(getContext(), workspace.getName());
        mWorkspaceListAdapter.remove(workspace);
    }

    public void addFileToWorkspace(String fileName, Workspace workspace) {
        for(int i = 0; i < mWorkspaceListAdapter.getCount(); i++)
            if(mWorkspaceListAdapter.getItem(i).getName().equals(fileName))
                throw new FileAlreadyExistsException(fileName);
        // Create folder in internal storage
        File file = FileManager.createFile(getContext(), workspace.getName(), fileName);
        workspace.addFile(file);
        AirDeskDbHelper.getInstance(getContext()).addFilesToWorkspace(workspace, Arrays.asList(new File[] { file }));
    }

    public void removeFileFromWorkspace(File file, Workspace workspace) {
        workspace.removeFile(file);
        FileManager.deleteFile(getContext(), workspace.getName(), file.getName());
        AirDeskDbHelper.getInstance(getContext()).removeFileFromWorkspace(workspace, file);
    }

    public void addUserToWorkspace(String userEmail, String userNick, Workspace workspace) {
        // Create folder in internal storage
        User user = new User(userEmail, userNick);
        workspace.addUser(new User(userEmail, userNick));
        AirDeskDbHelper.getInstance(getContext()).addUsersToWorkspace(workspace, Arrays.asList(new User[] { user }));
    }

    public void removeUserFromWorkspace(User user, Workspace workspace) {
        workspace.removeUser(user);
        AirDeskDbHelper.getInstance(getContext()).removeUserFromWorkspace(workspace, user);
    }

    public void addTagToWorkspace(String tagName, Workspace workspace) {
        // Create folder in internal storage
        Tag tag = new Tag(tagName);
        workspace.addTag(tag);
        AirDeskDbHelper.getInstance(getContext()).addTagsToWorkspace(workspace, Arrays.asList(new Tag[] { tag }));
    }

    public void removeTagFromWorkspace(Tag tag, Workspace workspace) {
        workspace.removeTag(tag);
        AirDeskDbHelper.getInstance(getContext()).removeTagFromWorkspace(workspace, tag);
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

    public ListAdapter getWorkspaceAdapter() {
        return mWorkspaceListAdapter;
    }

    public void reloadWorkspaces() {
        mWorkspaceListAdapter.clear();
        for(Workspace workspace : AirDeskDbHelper.getInstance(getContext()).getAllLocalWorkspaceInfo())
            mWorkspaceListAdapter.add(workspace);
        mWorkspaceListAdapter.sort(new Comparator<Workspace>() {
            @Override
            public int compare(Workspace lhs, Workspace rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
        // TODO: REMOVE
        if(mWorkspaceListAdapter.getCount() > 0)
            addFileToWorkspace("TestFile", mWorkspaceListAdapter.getItem(0));
    }



    public void deleteAllWorkspaces() {
        int i = mWorkspaceListAdapter.getCount() - 1;
        while(i >= 0)
            deleteWorkspace(mWorkspaceListAdapter.getItem(i--));
    }
}

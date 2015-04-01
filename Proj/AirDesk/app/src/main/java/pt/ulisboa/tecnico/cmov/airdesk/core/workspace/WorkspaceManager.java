package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import android.content.Context;
import android.widget.ListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
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
    }

    public void deleteAllWorkspaces() {
        int i = mWorkspaceListAdapter.getCount() - 1;
        while(i >= 0)
            deleteWorkspace(mWorkspaceListAdapter.getItem(i--));
    }
}

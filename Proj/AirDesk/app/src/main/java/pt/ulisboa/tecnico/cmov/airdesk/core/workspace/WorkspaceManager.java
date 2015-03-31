package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceAlreadyExistsException;
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

    public void addNewWorkspace(String name, String owner, long quota, boolean isPrivate, Tag[] tags, User[] users, File[] files) {
        if(!FileManager.isWorkspaceNameAvailable(getContext(), name))
            throw new WorkspaceAlreadyExistsException();
        FileManager.createFolder(getContext(), name);
        Workspace newWorkspace = new Workspace(name, new User(owner, ""), quota, isPrivate, tags, users, files, this);
        AirDeskDbHelper.getInstance(getContext()).insertWorkspace(newWorkspace);
        AirDeskDbHelper.getInstance(getContext()).addTagsToWorkspace(newWorkspace, newWorkspace.getTags());
        AirDeskDbHelper.getInstance(getContext()).addUsersToWorkspace(newWorkspace, newWorkspace.getUsers());
        // TODO: Add files to workspace
        mWorkspaceListAdapter.add(newWorkspace);
    }

    public void loadExistingWorkspace(String name, String owner, long quota, boolean isPrivate, Tag[] tags, User[] users, File[] files) {
        mWorkspaceListAdapter.add(new Workspace(name, new User(owner, ""), quota, isPrivate, tags, users, files, this));
    }

    public Context getContext() {
        return mContext;
    }

    public long getSpaceAvailableInternalStorage() {
        return FileManager.getAvailableSpace(mContext);
    }
}

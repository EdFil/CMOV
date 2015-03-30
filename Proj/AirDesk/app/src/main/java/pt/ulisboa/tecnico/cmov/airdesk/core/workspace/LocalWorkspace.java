package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import android.content.Context;

import java.util.HashSet;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceAlreadyExistsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceNameIsEmptyException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceQuotaInvalidException;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskDbHelper;
import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;

/**
 * Created by edgar on 20-03-2015.
 */
public class LocalWorkspace extends Workspace {

    public LocalWorkspace(Context context, String name, int quota, boolean isPrivate) {
        this(context, name, quota, isPrivate, null);
    }

    public LocalWorkspace(Context context, String name, int quota, boolean isPrivate, String[] tags) {
        super(context, name, quota, isPrivate, tags);
        FileManager.createFolder(context, name);
//        long workspaceId = AirDeskDbHelper.getInstance(context).insertWorkspace(name, getOwner(context), quota, isPrivate);
//        AirDeskDbHelper.getInstance(context).addTagsToWorkspace(workspaceId, tags);
//        AirDeskDbHelper.getInstance(context).addUsersToWorkspace(workspaceId, new String[] { getOwner(context) });

    }

    public LocalWorkspace(String workspaceName) {
        super(workspaceName);
    }

    private String getOwner(Context context){
        return context.getSharedPreferences("UserPref", context.MODE_PRIVATE).getString("user_email", null);
    }
}

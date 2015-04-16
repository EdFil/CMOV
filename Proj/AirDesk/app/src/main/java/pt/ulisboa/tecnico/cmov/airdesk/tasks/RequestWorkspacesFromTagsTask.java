package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;
import android.test.RenamingDelegatingContext;

import java.util.Collection;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;

/**
 * Created by edgar on 16-04-2015.
 */
public class RequestWorkspacesFromTagsTask extends AsyncTask<Collection<Tag>, Void, Collection<ForeignWorkspace>> {

    private WorkspaceListAdapter mWorkspaceListAdapter;

    public RequestWorkspacesFromTagsTask(WorkspaceListAdapter adapter){
        mWorkspaceListAdapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mWorkspaceListAdapter.clear();
    }

    @Override
    protected Collection<ForeignWorkspace> doInBackground(Collection<Tag>... tagsList) {
        //TODO: WIFI DIRECT REQUEST FOR WORKSPACES (ASYNC TASK)
        List<ForeignWorkspace> workspacesFound = WorkspaceManager.getInstance().getForeignWorkspacesWithTags(tagsList[0]);

        return workspacesFound;
    }

    @Override
    protected void onPostExecute(Collection<ForeignWorkspace> workspaces) {
        mWorkspaceListAdapter.addAll(workspaces);
    }
}

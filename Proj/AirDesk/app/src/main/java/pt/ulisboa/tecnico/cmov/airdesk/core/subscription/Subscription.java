package pt.ulisboa.tecnico.cmov.airdesk.core.subscription;

import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;

/**
 * Created by Diogo on 12-May-15.
 */
public class Subscription {

    private String mName;
    String[] mTags;
    List<ForeignWorkspace> mForeignWorkspaceList;
    List<LocalWorkspace> mLocalWorkspace;

    public Subscription(String name) {
        mName = name;
    }

    public Subscription(String[] tags) {
        mTags = tags;
    }

    public Subscription(String[] tags, List<LocalWorkspace> workspaceList) {
        mTags = tags;
        mLocalWorkspace = workspaceList;
    }

    public Subscription(String name, String[] tags, List<LocalWorkspace> workspaceList) {
        mName = name;
        mTags = tags;
        mLocalWorkspace = workspaceList;
    }


    public String getName() {
        return mName;
    }
}


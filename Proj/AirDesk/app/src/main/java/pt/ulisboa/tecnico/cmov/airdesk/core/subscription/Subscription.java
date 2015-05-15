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

    public Subscription(String name, String[] tags) {
        mName = name;
        mTags = tags;
    }

    public String[] getTags() {
        return mTags;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setTags(String[] tags) {
        mTags = tags;
    }
}


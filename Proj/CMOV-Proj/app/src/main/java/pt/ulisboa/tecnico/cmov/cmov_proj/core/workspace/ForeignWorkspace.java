package pt.ulisboa.tecnico.cmov.cmov_proj.core.workspace;

import java.util.HashSet;

/**
 * Created by edgar on 20-03-2015.
 */
public class ForeignWorkspace extends Workspace {

    public ForeignWorkspace(String name, int quota, boolean isPrivate, HashSet<String> tags) {
        super(name, quota);
    }
}

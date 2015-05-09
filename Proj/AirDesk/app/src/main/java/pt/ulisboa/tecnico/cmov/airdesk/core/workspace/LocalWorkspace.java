package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;

/**
 * Created by edgar on 20-03-2015.
 */
public class LocalWorkspace extends Workspace {

    public LocalWorkspace(String name, User owner, long quota, boolean isPrivate, Collection<String> tags) {
        this(-1, name, owner, quota, isPrivate, tags, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    public LocalWorkspace(String name, User owner, long quota, boolean isPrivate, Collection<String> tags, Collection<User> users, Collection<File> files) {
        this(-1, name, owner, quota, isPrivate, tags, users, files);
    }

    public LocalWorkspace(long workspaceId, String name, User owner, long quota, boolean isPrivate, Collection<String> tags, Collection<User> users, Collection<File> files) {
        super(workspaceId, name, owner, quota, isPrivate, tags, users, files);
    }

}

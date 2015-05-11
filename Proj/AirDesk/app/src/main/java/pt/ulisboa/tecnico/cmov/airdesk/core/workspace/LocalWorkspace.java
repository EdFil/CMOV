package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.LocalFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.MyFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;

/**
 * Created by edgar on 20-03-2015.
 */
public class LocalWorkspace extends Workspace {

    private List<LocalFile> mFiles;

    public LocalWorkspace(String name, User owner, long quota, boolean isPrivate, Collection<String> tags) {
        this(-1, name, owner, quota, isPrivate, tags, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    public LocalWorkspace(String name, User owner, long quota, boolean isPrivate, Collection<String> tags, Collection<User> users, Collection<MyFile> files) {
        this(-1, name, owner, quota, isPrivate, tags, users, files);
    }

    public LocalWorkspace(long workspaceId, String name, User owner, long quota, boolean isPrivate, Collection<String> tags, Collection<User> users, Collection<MyFile> files) {
        super(workspaceId, name, owner, quota, isPrivate, tags, users, files);
    }

}

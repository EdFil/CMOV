package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import android.content.Context;

import java.io.File;
import java.util.Collection;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskDbHelper;
import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;

/**
 * Created by edgar on 20-03-2015.
 */
public class LocalWorkspace extends Workspace {

    public LocalWorkspace(Context context, String name, String owner, int quota, boolean isPrivate, Collection<Tag> tags, Collection<User> users, Collection<File> files, WorkspaceManager workspaceManager) {
        super(name, new User(owner, ""), quota, isPrivate, tags, users, files, workspaceManager);
    }

}

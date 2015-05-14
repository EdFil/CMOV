package pt.ulisboa.tecnico.cmov.airdesk.core.file;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.FileManager;

/**
 * Created by Diogo on 10-May-15.
 */
public class RemoteFile extends MyFile {

    public RemoteFile(ForeignWorkspace workspace, JSONObject jsonObject) throws JSONException {
        super(
                workspace,
                jsonObject.getString(MyFile.NAME_KEY),
                jsonObject.getInt(MyFile.VERSION_KEY
                ));
    }

    @Override
    public File getFile() {
        // TODO: REMOTE UPDATE
        return FileManager.getInstance().createTempFile(getWorkspace().getWorkspaceFolderName(), getName());
    }
}

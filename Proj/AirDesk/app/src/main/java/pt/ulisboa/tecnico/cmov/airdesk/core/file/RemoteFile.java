package pt.ulisboa.tecnico.cmov.airdesk.core.file;


import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;

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
}

package pt.ulisboa.tecnico.cmov.airdesk.service;

import org.json.JSONArray;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class InviteUserService implements AirDeskService {

    @Override
    public JSONObject execute(JSONArray arguments) {
        JSONObject object = new JSONObject();
        try {
            JSONObject foreignWorkspaceInfo = new JSONObject(arguments.getString(0));
            WorkspaceManager.getInstance().mountForeignWorkspace(foreignWorkspaceInfo);
            object.put(Constants.RESULT_KEY, "OK");
        } catch (Exception e) {
            object.put(Constants.ERROR_KEY, e.getMessage());
        } finally {
            return object;
        }
    }

}

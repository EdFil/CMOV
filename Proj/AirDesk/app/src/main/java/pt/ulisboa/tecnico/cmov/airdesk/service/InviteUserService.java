package pt.ulisboa.tecnico.cmov.airdesk.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class InviteUserService implements AirDeskService {

    @Override
    public JSONObject execute(JSONArray arguments) {
        JSONObject object = new JSONObject();
        try {
            ForeignWorkspace workspace = new ForeignWorkspace(arguments.getJSONObject(0));
            workspace.addUser(UserManager.getInstance().getOwner());
            // TODO: Add workspace to foreign
            object.put(Constants.RESULT_KEY, "OK");
        } catch (JSONException e) {
            object.put(Constants.ERROR_KEY, e.getMessage());
        } finally {
            return object;
        }
    }

}

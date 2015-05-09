package pt.ulisboa.tecnico.cmov.airdesk.service;

import org.json.JSONArray;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;

/**
 * Created by edgar on 06-05-2015.
 */
public class GetUserService implements AirDeskService {

    @Override
    public JSONObject execute(JSONArray arguments) {
        return UserManager.getInstance().getOwner().toJson();
    }

}

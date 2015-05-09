package pt.ulisboa.tecnico.cmov.airdesk.service;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by edgar on 06-05-2015.
 */
public interface AirDeskService {

    public JSONObject execute(JSONArray arguments);

}

package pt.ulisboa.tecnico.cmov.airdesk.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by edgar on 08-05-2015.
 */
public class Util {

    public static JSONObject makeJSONErrorMessage(String errorMessage) {
        try {
            return new JSONObject().put(Constants.ERROR_KEY, errorMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] parseArguments(JSONArray arguments) {
        String[] tags = new String[arguments.length()];

        for (int i = 0; i < arguments.length(); i++)
            try {
                tags[i] = arguments.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return tags;
    }

}

package pt.ulisboa.tecnico.cmov.airdesk.service;

import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.UserManager;

/**
 * Created by edgar on 06-05-2015.
 */
public class GetUserService implements AirDeskService {

    @Override
    public String execute() {
        User user = UserManager.getInstance().getOwner();
     //   return user.toJson().toString();
        return user.getEmail();
    }

}

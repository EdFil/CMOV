package pt.ulisboa.tecnico.cmov.airdesk.core.user;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskDbHelper;

/**
 * Created by edgar on 08-04-2015.
 */
public class UserManager {

    // ------------------------
    //     Singleton Stuff
    // ------------------------

    public static final String TAG = UserManager.class.getSimpleName();
    private static UserManager mInstance;

    public static synchronized UserManager getInstance() {
        return mInstance;
    }

    public static synchronized void initUserManager(Context context) {
        mInstance = new UserManager(context);
    }

    // ------------------------
    //      Manager Stuff
    // ------------------------

    private Context mContext = null;
    private List<User> mUserList;
    private User mOwner;

    protected UserManager(Context context){
        mContext = context;
        mUserList = new ArrayList<>(AirDeskDbHelper.getInstance(context).getAllUsers());
    }

    public Context getContext() { return mContext; }

    public User createUser(String userEmail, String userNick){
        // If already exists
        for(User user : mUserList)
            if(user.getEmail().equals(userEmail))
                return user;
        // If not exists
        long userId = AirDeskDbHelper.getInstance(getContext()).insertUser(userEmail, userNick);
        User user = new User(userId, userEmail, userNick);
        mUserList.add(user);
        return user;
    }

    public User getUserById(long databaseId) {
        for(User user : mUserList)
            if(user.getDatabaseId() == databaseId)
                return user;
        return null;
    }

    public User getUserByEmail(String storedEmail) {
        for(User user : mUserList)
            if(user.getEmail().equals(storedEmail))
                return user;
        return null;
    }

    public User getOwner() { return mOwner; }
    public void setOwner(User owner) { mOwner = owner; }


    public List<User> getUsers() { return mUserList; }

}

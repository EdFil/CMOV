package pt.ulisboa.tecnico.cmov.airdesk.manager;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.subscription.Subscription;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
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
    private List<Subscription> mSubscriptionList;
    private User mOwner;

    protected UserManager(Context context){
        mContext = context;
        mUserList = new ArrayList<>(AirDeskDbHelper.getInstance(context).getAllUsers());
        // TODO : IR BUSCAR AS SUBSCRICOES A BD
        mSubscriptionList = new ArrayList<>();
    }

    public Context getContext() { return mContext; }

    public User createUser(JSONObject jsonObject) throws JSONException {
        String email = jsonObject.getString(User.EMAIL_KEY);
        String nick = jsonObject.getString(User.NICK_KEY);
        return createUser(email, nick);
    }

    public User createUser(String userEmail, String userNick){
        // If already exists
        for(User user : mUserList)
            if(user.getEmail().equals(userEmail))
                return user;

        if(mOwner != null && mOwner.getEmail().equals(userEmail)) {
            return null;
        }

        // If not exists
        long userId = AirDeskDbHelper.getInstance(getContext()).insertUser(userEmail, userNick);
        User user = new User(userId, userEmail, userNick);
        mUserList.add(user);
        return user;
    }

    public List<Subscription> getSubscriptionList() {
        return mSubscriptionList;
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
    public void setOwner(User owner) {
        mOwner = owner;
        FileManager.getInstance().setWorkspacesFolderName(mOwner.getEmail().replace("@", "_").replace(".", "_") + "_workspace");
    }


    public List<User> getUsers() { return mUserList; }

    public void deleteUser(User user) {
        WorkspaceManager.getInstance().deletaAllLocalWorkspaces();
        WorkspaceManager.getInstance().unmountAllForeignWorkspaces();
        AirDeskDbHelper.getInstance(getContext()).deleteUser(user.getDatabaseId());
        FileManager.getInstance().deleteRootFolder();
        mOwner = null;
        mUserList.remove(user);
    }


    public void clearUserList() {
        mUserList.clear();
    }

    public void deleteSubscription(int position) {
        Subscription subscription = mSubscriptionList.get(position);

        mSubscriptionList.remove(position);
    }
}
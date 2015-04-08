package pt.ulisboa.tecnico.cmov.airdesk.core.user;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import java.util.Comparator;
import java.util.HashSet;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;

public class User implements Parcelable {

    private long mDatabaseId;
    private String mEmail;
    private String mNick;

    public User(long databaseId, String email, String nick){
        mDatabaseId = databaseId;
        mEmail = email;
        mNick = nick;
    }

    public String getEmail() { return mEmail; }
    public String getNick() { return mNick; }
    public long getDatabaseId() { return mDatabaseId; }

    @Override
    public boolean equals(Object o) {
        if(o instanceof User){
            User user = (User) o;
            return user.getEmail().equals(getEmail());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mEmail.hashCode();
    }

    @Override
    public String toString(){
        return "Email = " + getEmail() + ", Nick = " + getNick();
    }



    // ---------------------
    // Stuff for Parcelable
    // ---------------------


    private User(Parcel in){
        mEmail = in.readString();
        mNick = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEmail);
        dest.writeString(mNick);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

}

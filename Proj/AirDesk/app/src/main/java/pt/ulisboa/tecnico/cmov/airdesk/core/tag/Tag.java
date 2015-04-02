package pt.ulisboa.tecnico.cmov.airdesk.core.tag;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;

/**
 * Created by edgar on 31-03-2015.
 */
public class Tag implements Parcelable {

    private Workspace mWorkspace;
    private String mText;

    public Tag(String text) {
        this(text, null);
    }
    public Tag(String text, Workspace workspace) {
        mText = text;
        mWorkspace = workspace;
    }

    // Getters
    public Workspace getWorkspace() { return mWorkspace; }
    public String getText() { return mText; }

    // Setters
    public void setText(String text) { mText = text; }
    public void setWorkspace(Workspace workspace) {
        mWorkspace = workspace;
    }




    // ---------------------
    // Stuff for Parcelable
    // ---------------------


    private Tag(Parcel in){
        mText = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mText);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
}

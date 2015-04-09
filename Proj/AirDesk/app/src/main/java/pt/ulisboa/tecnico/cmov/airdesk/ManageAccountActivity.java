package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.UserListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.WorkspacesFragment;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class ManageAccountActivity extends ActionBarActivity {

    private static final String TAG = ManageAccountActivity.class.getSimpleName();

    private User mSelectedUser;
    private TextView mEmailView;
    private TextView mNickView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        mEmailView = (TextView) findViewById(R.id.email);
        mNickView = (TextView) findViewById(R.id.nick);

        mSelectedUser = UserManager.getInstance().getOwner();

        mEmailView.setText(mSelectedUser.getEmail());
        mNickView.setText(mSelectedUser.getNick());
    }

    public void onDeleteUser(View view) {
        Button deleteUserButton = (Button)view;
        new AlertDialog.Builder(this)
                .setTitle(deleteUserButton.getText())
                .setMessage("Are you sure you want to delete you user profile?\nYour workspaces and files will also be deleted.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UserManager.getInstance().deleteUser(mSelectedUser);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Toast.makeText(getBaseContext(), "User deleted", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.UserListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class LoginActivity extends ActionBarActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private AutoCompleteTextView mEmailView;
    private EditText mNickView;
    private CheckBox mRememberMe;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toast.makeText(this, "LOGIN", Toast.LENGTH_SHORT).show();

        boolean logout = getIntent().getBooleanExtra(Constants.LOG_OUT_MESSAGE, false);
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREF_FILE, MODE_PRIVATE);

        if (logout) {
            mSharedPreferences.edit().remove(Constants.EMAIL_KEY).commit();
        } else {
            String storedEmail = mSharedPreferences.getString(Constants.EMAIL_KEY, null);
            if (storedEmail != null) {
                User user = UserManager.getInstance().getUserByEmail(storedEmail);
                if (user == null) {
                    Log.i(TAG, "Bad user E-mail");
                    mSharedPreferences.edit().remove(Constants.EMAIL_KEY).commit();
                } else {
                    UserManager.getInstance().setOwner(user);
                    Intent intent = new Intent(getApplicationContext(), AirDeskActivity.class);
                    startActivity(intent);
                }
            }
        }

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mNickView = (EditText) findViewById(R.id.nick);
        mRememberMe = (CheckBox) findViewById(R.id.rememberMe);

        UserListAdapter adapter = new UserListAdapter(getApplicationContext(), UserManager.getInstance().getUsers());
        mEmailView.setAdapter(adapter);

        mEmailView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) parent.getItemAtPosition(position);
                mEmailView.setText(user.getEmail());
                mNickView.setText(user.getNick());
            }
        });
    }

    public void onLoginCLicked(View view) {
        // Verify that input is correct
        if (mNickView.length() < 4) {
            Toast.makeText(getApplicationContext(), "Nick is too short, 4 chars minimum.", Toast.LENGTH_SHORT).show();
            return;
        } else if (!mEmailView.getText().toString().contains("@")) {
            Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = UserManager.getInstance().createUser(mEmailView.getText().toString(), mNickView.getText().toString());
        UserManager.getInstance().setOwner(user);

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        if (mRememberMe.isChecked()) {
            editor.putString(Constants.EMAIL_KEY, user.getEmail());
            editor.commit();
        } else {
            editor.remove(Constants.EMAIL_KEY);
            editor.commit();
        }

        Intent intent = new Intent(getApplicationContext(), AirDeskActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Application")
                .setMessage("Are you sure you want to leave?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LoginActivity.super.onBackPressed();
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

    public void onUserEmailClick(View view) {
        ((AutoCompleteTextView) view).showDropDown();
    }

}

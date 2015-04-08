package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

        boolean logout = getIntent().getBooleanExtra(Constants.LOG_OUT_MESSAGE, false);
        UserManager.getInstance().initUserManager(getApplicationContext());
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREF_FILE, MODE_PRIVATE);

        if(logout) {
            mSharedPreferences.edit().remove(Constants.EMAIL_KEY).commit();
        }else {
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
                User user = (User)parent.getItemAtPosition(position);
                mEmailView.setText(user.getEmail());
                mNickView.setText(user.getNick());
            }
        });
    }

    public void onLoginCLicked(View view){
        // Verify that input is correct
        if(mNickView.length() < 4 || !mEmailView.getText().toString().contains("@")) {
            Toast.makeText(getApplicationContext(), "Invalid info", Toast.LENGTH_SHORT);
            return;
        }

        User user = UserManager.getInstance().createUser(mEmailView.getText().toString(), mNickView.getText().toString());
        UserManager.getInstance().setOwner(user);

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        if(mRememberMe.isChecked()){
            editor.putString(Constants.EMAIL_KEY, user.getEmail());
            editor.commit();
        } else {
            editor.remove(Constants.EMAIL_KEY);
            editor.commit();
        }

        Intent intent = new Intent(getApplicationContext(), AirDeskActivity.class);
        startActivity(intent);
    }

    public void onUserEmailClick(View view) {
        ((AutoCompleteTextView)view).showDropDown();
    }
}

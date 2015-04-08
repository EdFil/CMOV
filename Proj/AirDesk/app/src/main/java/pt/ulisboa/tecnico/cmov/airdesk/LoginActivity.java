package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends ActionBarActivity {
    public static final int LOGIN_REQUEST = 1;
    public static final String SHARED_PREF_LOGIN_FILE = "Login";
    public static final String EMAIL_KEY = "user_";
    public static final String NICK_KEY = "nick_";

    private AutoCompleteTextView mEmailView;
    private EditText mNickView;

    private HashMap<String, String> mLoginCache;

    String email;
    String nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mNickView = (EditText) findViewById(R.id.nick);
        mLoginCache = new HashMap<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line);

        SharedPreferences pref = getSharedPreferences(SHARED_PREF_LOGIN_FILE, MODE_PRIVATE);
        int i = 0;
        while(pref.contains(EMAIL_KEY + i)){
            mLoginCache.put(pref.getString(EMAIL_KEY + i, null), pref.getString(NICK_KEY + i, null));
        }
        adapter.addAll(mLoginCache.keySet());

        mEmailView.setAdapter(adapter);
    }

    public void onLoginCLicked(View view){
        // Verify that input is correct
        if(mNickView.length() < 4 || !mEmailView.getText().toString().contains("@")) {
            Toast.makeText(getApplicationContext(), "Invalid info", Toast.LENGTH_SHORT);
            return;
        }

        loadUserDB(email, nick);

        // Put user on Cache.
        SharedPreferences pref = getSharedPreferences(SHARED_PREF_LOGIN_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(EMAIL_KEY, nick);
        editor.putString(NICK_KEY, email);
        editor.commit();

        // Return OK to allow the login into the AirDeskActivity.
        setResult(RESULT_OK);
        finish();
    }

    private void loadUserDB(String email, String nick) {

        //TODO : loadUserDB
        // Load User if it is on database
        if(!isUserOnDatabase(email, nick))
            createUserOnDB(email, nick);

        // load te user account shit
    }

    private void createUserOnDB(String email, String nick) {
        //TODO : createUserOnDB
    }

    // Verify if User is on DB
    private boolean isUserOnDatabase(String email, String nick) {
        //TODO : isUserOnDatabase
        return true;
    }
}

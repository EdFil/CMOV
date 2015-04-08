package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends ActionBarActivity {

    String email;
    String nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginCLicked(View view){

        nick = ((EditText)findViewById(R.id.nick)).getText().toString();
        email = ((EditText)findViewById(R.id.email)).getText().toString();

        // Verify that input is correct
        if(nick.length() < 4 || !email.contains("@")) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        loadUserDB(email, nick);

        // Put user on Cache.
        SharedPreferences pref = getSharedPreferences("UserPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_nick", nick);
        editor.putString("user_email", email);
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

package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.R;

public class LoginActivity extends ActionBarActivity {

    public static final int LOGIN_REQUEST = 1;

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
        // Load User if it is on database
        if(!isUserOnDatabase(email, nick))
            createUserOnDB(email, nick);

        // load te user account shit
    }

    private void createUserOnDB(String email, String nick) {
    }

    // Verify if User is on DB
    private boolean isUserOnDatabase(String email, String nick) {
        return true;
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

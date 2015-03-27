package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import pt.ulisboa.tecnico.cmov.airdesk.R;

public class LoginActivity extends ActionBarActivity {

    public static final int LOGIN_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //setResult(RESULT_CANCELED);
    }

    public void onLoginCLicked(View view){
        setResult(RESULT_OK);
        SharedPreferences pref = getSharedPreferences("UserPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("user_email", ((EditText)findViewById(R.id.email)).getText().toString());
        editor.putString("user_nick", ((EditText)findViewById(R.id.nick)).getText().toString());
        editor.commit();

        finish();
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

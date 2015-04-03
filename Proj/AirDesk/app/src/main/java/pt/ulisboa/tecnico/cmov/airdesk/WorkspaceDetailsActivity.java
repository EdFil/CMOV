package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class WorkspaceDetailsActivity extends ActionBarActivity {

    private static final int NUM_COLUMNS_PER_ROW = 4;

    public static final int LOGIN_REQUEST = 1;

    TextView mOwnerInformation;
    TextView mQuotaInformation;
    TextView mPrivacyInformation;

    TableLayout mTagsTableLayout;
    TableLayout mUsersTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace_details);
        setTitle("[Workspace name here]");

        mOwnerInformation = (TextView) findViewById(R.id.ownerInformation);
        mQuotaInformation = (TextView) findViewById(R.id.quotaIndormation);
        mPrivacyInformation = (TextView) findViewById(R.id.privateInformation);

        mTagsTableLayout = (TableLayout) findViewById(R.id.tagsTable);
        mTagsTableLayout.setStretchAllColumns(true);
        mUsersTableLayout = (TableLayout) findViewById(R.id.usersTable);
        mUsersTableLayout.setStretchAllColumns(true);

        addTagToTable("Tag 1");addTagToTable("Tag 2");addTagToTable("Tag 3");addTagToTable("Tag 4");
        addTagToTable("Tag 5");addTagToTable("Tag 6");addTagToTable("Tag 7");

        addUserToTable("User 1");addUserToTable("User 2");addUserToTable("User 3");addUserToTable("User 4");
        addUserToTable("User 5");addUserToTable("User 6");addUserToTable("User 7");addUserToTable("User 8");
    }

    private void addTagToTable(String tag) {
        TextView tagText = new TextView(this);
        tagText.setText(tag);
        tagText.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_Medium);
        TableRow rowToInsertTag;
        int numRows = mTagsTableLayout.getChildCount();
        if(numRows == 0 || ((TableRow)mTagsTableLayout.getChildAt(numRows - 1)).getChildCount() >= NUM_COLUMNS_PER_ROW) {
            rowToInsertTag = new TableRow(this);
            mTagsTableLayout.addView(rowToInsertTag);
        } else {
            rowToInsertTag = (TableRow)mTagsTableLayout.getChildAt(numRows - 1);
        }
        rowToInsertTag.addView(tagText, ActionBar.LayoutParams.WRAP_CONTENT);
    }

    private void addUserToTable(String tag) {
        TextView userText = new TextView(this);
        userText.setText(tag);
        userText.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_Medium);
        TableRow rowToInsertTag;
        int numRows = mUsersTableLayout.getChildCount();
        if(numRows == 0 || ((TableRow)mUsersTableLayout.getChildAt(numRows - 1)).getChildCount() >= NUM_COLUMNS_PER_ROW) {
            rowToInsertTag = new TableRow(this);
            mUsersTableLayout.addView(rowToInsertTag);
        } else {
            rowToInsertTag = (TableRow)mUsersTableLayout.getChildAt(numRows - 1);
        }
        rowToInsertTag.addView(userText, ActionBar.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar wil
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

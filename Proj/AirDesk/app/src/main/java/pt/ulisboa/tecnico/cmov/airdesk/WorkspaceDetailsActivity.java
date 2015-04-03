package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.WorkspaceFragment;

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

        int workspaceIndex = getIntent().getIntExtra(WorkspaceFragment.WORKSPACE_INDEX_TAG, -1);

        Workspace workspace = WorkspaceManager.getInstance().getWorkspaceAtIndex(workspaceIndex);

        mOwnerInformation = (TextView) findViewById(R.id.ownerInformation);
        mQuotaInformation = (TextView) findViewById(R.id.quotaIndormation);
        mPrivacyInformation = (TextView) findViewById(R.id.privateInformation);

        mTagsTableLayout = (TableLayout) findViewById(R.id.tagsTable);
        mTagsTableLayout.setStretchAllColumns(true);
        mUsersTableLayout = (TableLayout) findViewById(R.id.usersTable);
        mUsersTableLayout.setStretchAllColumns(true);

        setTitle(workspace.getName());

        mOwnerInformation.setText(workspace.getOwner().getEmail());

        long bytesUsed = 0;
        for(File file : workspace.getFiles())
            bytesUsed += file.length();

        mQuotaInformation.setText("Used " + bytesUsed + " out of " + workspace.getQuota());
        mPrivacyInformation.setText(workspace.isPrivate() ? "True" : "False");

        for(Tag tag : workspace.getTags())
            addTagToTable(tag.getText());

        for(User user : workspace.getUsers())
            addUserToTable(user.getEmail());
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

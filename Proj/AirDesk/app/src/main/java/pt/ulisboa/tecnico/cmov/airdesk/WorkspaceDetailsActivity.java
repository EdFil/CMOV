package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.io.File;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;

public class WorkspaceDetailsActivity extends ActionBarActivity {

    private static final int NUM_COLUMNS_PER_ROW = 4;

    public static final String WORKSPACE_INDEX_TAG = "worspace_index";
    public static final String EDIT_MODE = "view_mode";

    boolean mEditMode;

    TextView mNameInformationText;
    TextView mOwnerInformationText;
    TextView mQuotaInformationText;
    TextView mPrivacyInformationText;

    EditText mNameInformationEdit;
    EditText mOwnerInformationEdit;
    EditText mQuotaInformationEdit;
    Switch mPrivacyInformationSwitch;

    ViewSwitcher mNameViewSwitcher;
    ViewSwitcher mOwnerViewSwitcher;
    ViewSwitcher mQuotaViewSwitcher;
    ViewSwitcher mPrivacyViewSwitcher;

    TableLayout mTagsTableLayout;
    TableLayout mUsersTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace_details);

        mEditMode = getIntent().getBooleanExtra(EDIT_MODE, false);
        Workspace workspace = getIntent().getParcelableExtra("workspaceSelected");

        mNameInformationText = (TextView) findViewById(R.id.nameInformationText);
        mOwnerInformationText = (TextView) findViewById(R.id.ownerInformationText);
        mQuotaInformationText = (TextView) findViewById(R.id.quotaInformationText);
        mPrivacyInformationText = (TextView) findViewById(R.id.privateInformationText);

        mNameInformationEdit = (EditText) findViewById(R.id.nameInformationEdit);
        mOwnerInformationEdit = (EditText) findViewById(R.id.ownerInformationEdit);
        mQuotaInformationEdit = (EditText) findViewById(R.id.quotaInformationEdit);
        mPrivacyInformationSwitch = (Switch) findViewById(R.id.privateInformationSwitch);

        mNameViewSwitcher = (ViewSwitcher) findViewById(R.id.nameViewSwitcher);
        mOwnerViewSwitcher = (ViewSwitcher) findViewById(R.id.ownerViewSwitcher);
        mQuotaViewSwitcher = (ViewSwitcher) findViewById(R.id.quotaViewSwitcher);
        mPrivacyViewSwitcher = (ViewSwitcher) findViewById(R.id.privacyViewSwitcher);

        mTagsTableLayout = (TableLayout) findViewById(R.id.tagsTable);
        mTagsTableLayout.setStretchAllColumns(true);
        mUsersTableLayout = (TableLayout) findViewById(R.id.usersTable);
        mUsersTableLayout.setStretchAllColumns(true);

        mOwnerInformationText.setText(workspace.getOwner().getEmail());

        long bytesUsed = 0;
        for(File file : workspace.getFiles())
            bytesUsed += file.length();

        mQuotaInformationText.setText("Used " + bytesUsed + " out of " + workspace.getQuota());
        mPrivacyInformationText.setText(workspace.isPrivate() ? "True" : "False");

        for(Tag tag : workspace.getTags())
            addTagToTable(tag.getText());

        for(User user : workspace.getUsers())
            addUserToTable(user.getEmail());
    }

    @Override
    protected void onStart() {
        super.onStart();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_workspace_details, menu);
        updateView(menu.findItem(R.id.edit));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar wil
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit) {
            mEditMode = !mEditMode;
            updateView(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateView(MenuItem item){
        if((mNameViewSwitcher.getChildAt(0) instanceof TextView) && mEditMode) {
            mNameViewSwitcher.showNext();
            mOwnerViewSwitcher.showNext();
            mQuotaViewSwitcher.showNext();
            mPrivacyViewSwitcher.showNext();
        }
        if(mEditMode){
            item.setIcon(android.R.drawable.ic_menu_edit);
        } else {
            item.setIcon(android.R.drawable.ic_menu_info_details);
        }
    }
}

package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.MyFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;



public class FileActivity extends ActionBarActivity {

    public static final String TAG = FileActivity.class.getSimpleName();

    private TextView mTextToView;
    private ImageView mEditImageView;

    private EditText mTextToEdit;
    private ImageView mSaveImageView;

    private MyFile mFile;
    private Workspace mWorkspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        mTextToView = (TextView) findViewById(R.id.text_view);
        mEditImageView = (ImageView) findViewById(R.id.editTextImage);

        mTextToEdit = (EditText) findViewById(R.id.edit_text);
        mSaveImageView = (ImageView) findViewById(R.id.saveTextImage);

        Intent intent = getIntent();

        String fileName = intent.getStringExtra(Constants.FILE_NAME_KEY);
        long workspaceId = intent.getLongExtra(Constants.WORKSPACE_ID_KEY, -1);

        mWorkspace = WorkspaceManager.getInstance().getLocalWorkspaceWithId(workspaceId);
        if(mWorkspace == null)
            mWorkspace = WorkspaceManager.getInstance().getForeignWorkspaceWithId(workspaceId);
        if(mWorkspace == null)
            Log.e(TAG, "Could not open file");

        mFile = mWorkspace.getFileByName(fileName);

        // Action bar back button e name
        getSupportActionBar().setTitle(mFile.getName());

        mFile.read(mTextToView);
    }

    public void onClickEdit(View view) {
        try {
            mFile.lock();

            mFile.read(mTextToView);
            mTextToEdit.setText(mTextToView.getText().toString());

            mTextToView.setVisibility(View.GONE);
            mEditImageView.setVisibility(View.GONE);

            mTextToEdit.setVisibility(View.VISIBLE);
            mSaveImageView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickSave(View view) {
        try {
            mFile.write(mTextToEdit);

            mFile.unlock();

            mFile.read(mTextToView);

            mTextToView.setVisibility(View.VISIBLE);
            mEditImageView.setVisibility(View.VISIBLE);

            mTextToEdit.setVisibility(View.GONE);
            mSaveImageView.setVisibility(View.GONE);
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mTextToEdit.getVisibility() == View.VISIBLE)
            mFile.unlock();
    }
}

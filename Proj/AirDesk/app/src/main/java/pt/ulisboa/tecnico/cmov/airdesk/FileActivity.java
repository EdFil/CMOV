package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.LocalFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileExceedsAvailableSpaceException;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileExceedsMaxQuotaException;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.ReadFileTask;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.WriteFileTask;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;



public class FileActivity extends ActionBarActivity {

    public static final String TAG = FileActivity.class.getSimpleName();

    private TextView mTextToView;
    private ImageView mEditImageView;

    private EditText mTextToEdit;
    private ImageView mSaveImageView;

    private LocalFile mFile;
    private LocalWorkspace mWorkspace;

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
        mFile = mWorkspace.getFileByName(fileName);

        // Action bar back button e name
        getSupportActionBar().setTitle(mFile.getName());

        new ReadFileTask(mTextToView).execute(mFile.getFile());
    }

    public void onClickEdit(View view) {
        try {
            mFile.open();

            new ReadFileTask(mTextToEdit).execute(mFile.getFile());

            mTextToView.setVisibility(View.GONE);
            mEditImageView.setVisibility(View.GONE);

            mTextToEdit.setVisibility(View.VISIBLE);
            mSaveImageView.setVisibility(View.VISIBLE);
        } catch (FileException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickSave(View view) {
        try {
            long bytesToUse = mTextToEdit.length() - mTextToView.length() + (mWorkspace).getUsedQuota();
            long usableSpace = WorkspaceManager.getInstance().getSpaceAvailableInternalStorage();

            if(bytesToUse > usableSpace) {
                throw new FileExceedsAvailableSpaceException(bytesToUse, usableSpace);
            }
            if(bytesToUse > mWorkspace.getMaxQuota()) {
                throw new FileExceedsMaxQuotaException(bytesToUse, mWorkspace.getMaxQuota());
            }

            new WriteFileTask(mTextToEdit, mWorkspace).execute(mFile.getFile());

            mFile.close();

            new ReadFileTask(mTextToView).execute(mFile.getFile());

            mTextToView.setVisibility(View.VISIBLE);
            mEditImageView.setVisibility(View.VISIBLE);

            mTextToEdit.setVisibility(View.GONE);
            mSaveImageView.setVisibility(View.GONE);
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

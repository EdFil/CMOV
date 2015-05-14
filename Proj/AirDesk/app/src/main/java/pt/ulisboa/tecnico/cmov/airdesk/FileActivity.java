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
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.ReadFileTask;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.WriteFileTask;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;



public class FileActivity extends ActionBarActivity {

    public static final String LOG_TAG = FileActivity.class.getSimpleName();


    private TextView textToView;
    private ImageView edit;

    private EditText textToEdit;
    private ImageView save;

    private LocalFile mFile;
    private LocalWorkspace mWorkspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        textToView = (TextView) findViewById(R.id.textFile);
        edit = (ImageView) findViewById(R.id.editTextFile);

        textToEdit = (EditText) findViewById(R.id.editedTextFile);
        save = (ImageView) findViewById(R.id.saveTextFile);

        Intent intent = getIntent();

        String fileName = intent.getStringExtra(Constants.FILE_NAME_KEY);
        long workspaceId = intent.getLongExtra(Constants.WORKSPACE_ID_KEY, -1);

        mWorkspace = WorkspaceManager.getInstance().getLocalWorkspaceWithId(workspaceId);
        mFile = mWorkspace.getFileByName(fileName);

        // Action bar back button e name
        getSupportActionBar().setTitle(mFile.getName());

        Toast.makeText(getApplicationContext(), "Filename : " + mFile.getName(), Toast.LENGTH_SHORT).show();

        new ReadFileTask(textToView).execute(mFile.getFile());

    }


    public void onClickEdit(View view) {

        new ReadFileTask(textToEdit).execute(mFile.getFile());

        textToView.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);

        textToEdit.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
    }

    public void onClickSave(View view) {

        try {

            long bytesToUse = textToEdit.length() - textToView.length() + ((LocalWorkspace) mWorkspace).getUsedQuota();
            long usableSpace = WorkspaceManager.getInstance().getSpaceAvailableInternalStorage();

            if(bytesToUse > usableSpace) {
                throw new FileExceedsAvailableSpaceException(bytesToUse, usableSpace);
            }
            if(bytesToUse > mWorkspace.getMaxQuota()) {
                throw new FileExceedsMaxQuotaException(bytesToUse, mWorkspace.getMaxQuota());
            }

            new WriteFileTask(textToEdit, mWorkspace).execute(mFile.getFile());
            new ReadFileTask(textToView).execute(mFile.getFile());

            textToView.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);

            textToEdit.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

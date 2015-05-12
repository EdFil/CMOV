package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileExceedsAvailableSpaceException;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileExceedsMaxQuotaException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
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

    private File file;
    private Workspace workspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        textToView = (TextView) findViewById(R.id.textFile);
        edit = (ImageView) findViewById(R.id.editTextFile);

        textToEdit = (EditText) findViewById(R.id.editedTextFile);
        save = (ImageView) findViewById(R.id.saveTextFile);

        Intent intent = getIntent();

        file = (File) intent.getSerializableExtra(Constants.FILE_NAME_KEY);
        workspace = WorkspaceManager.getInstance().getLocalWorkspaceWithId(intent.getLongExtra(Constants.WORKSPACE_ID_KEY, -1));

        // Action bar back button e name
        getSupportActionBar().setTitle(file.getName());

        Toast.makeText(getApplicationContext(), "Filename : " + file.getName(), Toast.LENGTH_SHORT).show();

        new ReadFileTask(textToView).execute(file);

    }


    public void onClickEdit(View view) {

        new ReadFileTask(textToEdit).execute(file);

        textToView.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);

        textToEdit.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
    }

    public void onClickSave(View view) {

        try {

            long bytesToUse = textToEdit.length() - textToView.length() + ((LocalWorkspace)workspace).getUsedQuota();
            long usableSpace = WorkspaceManager.getInstance().getSpaceAvailableInternalStorage();

            if(bytesToUse > usableSpace) {
                throw new FileExceedsAvailableSpaceException(bytesToUse, usableSpace);
            }
            if(bytesToUse > workspace.getMaxQuota()) {
                throw new FileExceedsMaxQuotaException(bytesToUse, workspace.getMaxQuota());
            }

            new WriteFileTask(textToEdit, workspace).execute(file);
            new ReadFileTask(textToView).execute(file);

            textToView.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);

            textToEdit.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

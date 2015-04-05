package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.FileActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.WorkspaceDetailsActivity;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.FileListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;

public class FilesFragment extends Fragment {

    public static final String TAG = FilesFragment.class.getSimpleName();

    Workspace mWorkspace;
    List<File> mFiles;
    FileListAdapter mFileListAdapter;

    public FilesFragment() {}

    public FilesFragment newInstance(Workspace workspace) {
        setWorkspace(workspace);
        return new FilesFragment();
    }

    private void setWorkspace(Workspace workspace) {
        mWorkspace = workspace;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fileFragmentView = inflater.inflate(R.layout.fragment_files, container, false);


        WorkspaceManager manager = WorkspaceManager.getInstance();

        // Get the WORKSPACE selected in order to retrieve the respective files
        Bundle bundle = getArguments();
        mWorkspace = bundle.getParcelable("Workspace");

        // Request the MANAGER for the FILES (and its ADAPTER) of the WORKSPACE
        mFiles = manager.getFilesFromWorkspace(mWorkspace);
        mFileListAdapter = new FileListAdapter(container.getContext(), mFiles);

        ListView listView = (ListView) fileFragmentView.findViewById(R.id.filesList);
        listView.setAdapter(mFileListAdapter);

//        manager.addFileToWorkspace("wtv", mWorkspace);
//        mFileListAdapter.notifyDataSetChanged();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO : get the filme name to send it to the opener
                openFileActivity("fileText.txt");
            }
        });

        registerForContextMenu(listView);


        Button newButton = (Button) fileFragmentView.findViewById(R.id.newButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                NewFileFragment newFileFragment = new NewFileFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("Workspace", mWorkspace);
                newFileFragment.setArguments(bundle);
                newFileFragment.show(getActivity().getFragmentManager(), "New File");
            }
        });

        return fileFragmentView;
    }

    private void openFileActivity(String fileName) {
        Intent intent = new Intent(getActivity(), FileActivity.class);
        intent.putExtra("textFile", fileName);
        startActivity(intent);
    }

    // This will be invoked when an item in the listView is long pressed
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_my_workspaces, menu);
    }

    // This will be invoked when a menu item is selected
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Intent intent;
        switch(item.getItemId()){
            case R.id.menu_my_edit:
                intent = new Intent(getActivity(), WorkspaceDetailsActivity.class);
                intent.putExtra(WorkspaceDetailsActivity.EDIT_MODE, true);
                intent.putExtra(WorkspaceDetailsActivity.WORKSPACE_INDEX_TAG, info.position);
                getActivity().startActivity(intent);
                break;
            case R.id.menu_my_details:
                intent = new Intent(getActivity(), WorkspaceDetailsActivity.class);
                intent.putExtra(WorkspaceDetailsActivity.EDIT_MODE, false);
                intent.putExtra(WorkspaceDetailsActivity.WORKSPACE_INDEX_TAG, info.position);
                getActivity().startActivity(intent);
                break;
            case R.id.menu_my_delete:
                Workspace selectedWorkspace = (Workspace)((ListView)info.targetView.getParent()).getAdapter().getItem(info.position);
                WorkspaceManager.getInstance().deleteWorkspace(selectedWorkspace);
                break;
            case R.id.menu_my_invite:
                Toast.makeText(getActivity(), "TODO Invite", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AirDeskActivity) activity).onSectionAttached(1);
    }
}
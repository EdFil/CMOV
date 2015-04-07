package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
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
import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;

public class FilesFragment extends Fragment {

    public static final String TAG = FilesFragment.class.getSimpleName();

    FileListAdapter mFileListAdapter;

    Workspace mWorkspace;
    List<File> mFiles;

    public FilesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fileFragmentView = inflater.inflate(R.layout.fragment_files, container, false);

        // Get manager
        WorkspaceManager manager = WorkspaceManager.getInstance();

        // Get the WORKSPACE of the files
        Bundle bundle = getArguments();
        mWorkspace = bundle.getParcelable("Workspace");

        // Define action bar title as the Workspace Name
        ((AirDeskActivity) getActivity()).getSupportActionBar().setTitle(mWorkspace.getName());

        // Request the MANAGER for the FILES (and its ADAPTER) of the WORKSPACE
        mFiles = manager.getFilesFromWorkspace(mWorkspace);
        mFileListAdapter = new FileListAdapter(container.getContext(), mFiles);
        manager.setFileListAdapter(mFileListAdapter);


        ListView listView = (ListView) fileFragmentView.findViewById(R.id.filesList);
        listView.setAdapter(mFileListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Send file to the File activity
                File file = (File) parent.getItemAtPosition(position);
                file = FileManager.fileNameToFile(getActivity(), mWorkspace.getName(), file.getName());
                Intent intent = new Intent(getActivity(), FileActivity.class);
                intent.putExtra("textFile", file);
                startActivity(intent);
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

    @Override
    public void onStart() {
        super.onStart();
        mFileListAdapter.notifyDataSetChanged();
    }

//    private void openFileActivity(String fileName) {
//        FileManager.fileNameToFile(getActivity(), mWorkspace.getName(), file.getName())
//        Intent intent = new Intent(getActivity(), FileActivity.class);
//        intent.putExtra("textFile", fileName);
//        startActivity(intent);
//    }

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
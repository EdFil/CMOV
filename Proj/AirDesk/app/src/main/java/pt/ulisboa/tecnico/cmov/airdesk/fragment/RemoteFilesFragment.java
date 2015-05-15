package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.FileActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.FileListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.RemoteFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class RemoteFilesFragment extends Fragment {

    public static final String TAG = RemoteFilesFragment.class.getSimpleName();

    ForeignWorkspace mWorkspace;

    List<RemoteFile> mFiles;
    ListView mFileListView;
    FileListAdapter mFileListAdapter;

    public RemoteFilesFragment() {setHasOptionsMenu(true);}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        mWorkspace = WorkspaceManager.getInstance().getForeignWorkspaceWithId(bundle.getLong(Constants.WORKSPACE_ID_KEY));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = container.getContext();
        View fileFragmentView = inflater.inflate(R.layout.fragment_files, container, false);

        // Request the MANAGER for the FILES (and its ADAPTER) of the WORKSPACE
        mFiles = mWorkspace.getFiles();
        mFileListAdapter = new FileListAdapter(context, mFiles);

        mFileListView = (ListView) fileFragmentView.findViewById(R.id.filesList);
        mFileListView.setAdapter(mFileListAdapter);
        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Send file to the File activity
                RemoteFile file = ((RemoteFile) parent.getItemAtPosition(position));

                Intent intent = new Intent(getActivity(), FileActivity.class);
                intent.putExtra(Constants.FILE_NAME_KEY, file.getFile().getName());
                intent.putExtra(Constants.WORKSPACE_ID_KEY, mWorkspace.getDatabaseId());
                startActivity(intent);
            }


        });

        // Register the list of Files for the ContextMenu
        registerForContextMenu(mFileListView);

        Button newFileButton = (Button) fileFragmentView.findViewById(R.id.newFileButton);
        newFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                // Create fragment
                NewFileFragment newFileFragment = new NewFileFragment();

                // set the Workspace to send to fragment
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.WORKSPACE_ID_KEY, mWorkspace.getDatabaseId());
                newFileFragment.setArguments(bundle);

                // Show dialog fragment
                newFileFragment.show(getActivity().getFragmentManager(), NewFileFragment.class.getSimpleName());
            }
        });
        return fileFragmentView;
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        // Indicate that this fragment would like to influence the set of actions in the action bar.
//        setHasOptionsMenu(true);
//    }

    @Override
    public void onResume() {
        super.onResume();

        // Define action bar title as the Workspace Name
        ((AirDeskActivity) getActivity()).getSupportActionBar().setTitle(mWorkspace.getName());
        updateFileList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_file, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_files:
                updateFileList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // This will be invoked when an item in the listView is long pressed
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_context_my_files, menu);
    }

    // This will be invoked when a menu item is selected
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        return true;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AirDeskActivity) activity).onSectionAttached(1);
    }

    public void updateFileList() {
        mFileListAdapter.notifyDataSetChanged();
    }


    public static Fragment newInstance() {
        return new RemoteFilesFragment();
    }
}
package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import java.io.File;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.FileActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.FileListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;

public class FilesFragment extends Fragment {

    public static final String TAG = FilesFragment.class.getSimpleName();

    Workspace mWorkspace;

    List<File> mFiles;
    FileListAdapter mFileListAdapter;

    public FilesFragment() {setHasOptionsMenu(true);}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Context context = container.getContext();
        View fileFragmentView = inflater.inflate(R.layout.fragment_files, container, false);

        // Get manager
        WorkspaceManager manager = WorkspaceManager.getInstance();

        // Get the WORKSPACE of the files
        Bundle bundle = getArguments();
        mWorkspace = bundle.getParcelable("Workspace");

        // Request the MANAGER for the FILES (and its ADAPTER) of the WORKSPACE
        mFiles = manager.getFilesFromWorkspace(mWorkspace);
        mFileListAdapter = new FileListAdapter(context, mFiles);

        ListView listView = (ListView) fileFragmentView.findViewById(R.id.filesList);
        listView.setAdapter(mFileListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Send file to the File activity
                String fileName = ((File) parent.getItemAtPosition(position)).getName();
                File file = FileManager.fileNameToFile(getActivity(), mWorkspace.getName(), fileName);

                Intent intent = new Intent(getActivity(), FileActivity.class);
                intent.putExtra("textFile", file);
                intent.putExtra("workspaceIndex", mWorkspace);
                startActivity(intent);
            }


        });

        // Register the list of Files for the ContextMenu
        registerForContextMenu(listView);

        Button newFileButton = (Button) fileFragmentView.findViewById(R.id.newFileButton);
        newFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                // Create fragment
                NewFileFragment newFileFragment = new NewFileFragment();

                // set the Workspace to send to fragment
                Bundle bundle = new Bundle();
                bundle.putParcelable("Workspace", mWorkspace);
                newFileFragment.setArguments(bundle);

                // Show dialog fragment
                newFileFragment.show(getActivity().getFragmentManager(), "New File");
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
            case R.id.delete_all_files:
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete All")
                        .setMessage("Are you sure you want to delete all your files from this workspace?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                WorkspaceManager.getInstance().removeAllFilesFromWorkspace(mWorkspace);
                                mFileListAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "DELETED", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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

        switch(item.getItemId()){
            case R.id.menu_file_delete:
                File file = mFileListAdapter.getItem(info.position);
                WorkspaceManager.getInstance().removeFileFromWorkspace(file, mWorkspace);
                mFileListAdapter.notifyDataSetChanged();
                break;
        }
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
        return new FilesFragment();
    }
}
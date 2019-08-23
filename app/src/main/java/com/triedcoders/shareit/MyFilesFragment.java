package com.triedcoders.shareit;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFilesFragment extends BaseFragment {
    MenuItem prevSortAction;
    RecyclerView fileView;
    DatabaseHelper db;
    SearchView searchView;
    ArrayList<FileItem> items;
    String[] mimeTypes = {
            "image/*", "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.ms-excel", "text/plain", "text/csv"
    };
    private static final int FILE_REQUEST_CODE = 7;

    public MyFilesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_my_files, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        MenuItem menuItem = menu.findItem(R.id.app_bar_file_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(fileAdapter != null){
                    Log.d("adapter", "successful");
                    fileAdapter.getFilter().filter(s);
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(getActivity());
        fileView = view.findViewById(R.id.fileView);
        floatingAction = getActivity().findViewById(R.id.floatingAction);
        floatingAction.setOnClickListener(view_ -> {
            Log.d("click", "click");
            pick_path();
        });
        populate();
    }

    void pick_path(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortPopular:
                setPrevSortAction(item);
                fileAdapter.sort(FileItem.ratingComparator);
                return true;
            case R.id.sortExtension:
                setPrevSortAction(item);
                fileAdapter.sort(FileItem.extensionComparator);
                return true;
            case R.id.sortFileName:
                setPrevSortAction(item);
                fileAdapter.sort(FileItem.nameComparator);
                return true;
            case R.id.sortFileSize:
                setPrevSortAction(item);
                fileAdapter.sort(FileItem.fileSizeComparator);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case FILE_REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    if(data != null){
                        Uri uri;
                        if(data.getClipData() != null) {
                            // If data.getClipData is null then handle as single file to avoid errors
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                uri = data.getClipData().getItemAt(i).getUri();
                                FileItem file = new FileItem(uri, getActivity());
                                db.insertFileItem(file);
                                fileAdapter.addFileItem(file);
                            }
                            Toast.makeText(getActivity(), data.getClipData().getItemCount() + " files added", Toast.LENGTH_LONG).show();
                        }else{
                            uri = data.getData();
                            FileItem file = new FileItem(uri, getActivity());
                            db.insertFileItem(file);
                            fileAdapter.addFileItem(file);
                            Toast.makeText(getActivity(), "New file added", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        }
    }

    public void setPrevSortAction(MenuItem item){
        if(prevSortAction != null)
            prevSortAction.setChecked(false);
        prevSortAction = item;
        prevSortAction.setChecked(true);
    }

    void populate(){
        items = db.getAllFileItems();

        fileAdapter = new FileItemAdapter(items);
        fileView.setAdapter(fileAdapter);
        fileView.setLayoutManager(new LinearLayoutManager(getContext()));
        fileView.setItemAnimator(new DefaultItemAnimator());
        fileAdapter.notifyDataSetChanged();
        fileView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), fileView, new RecyclerTouchListener.ClickListener(){
            @Override
            public void onClick(View v, int position){
                FileItem item = items.get(position);
                Intent detailScreen = new Intent(getActivity(), FileDetailActivity.class);
                detailScreen.putExtra("FileItem", item.id);
                startActivity(detailScreen);
            }

            @Override
            public void onLongClick(View view, int position){

            }
        }));
    }

    public int getFloatingActionButtonColor() {
        return R.color.colorAccent;
    }
    public int getFloatingActionButtonIcon() {
        return R.drawable.add_files;
    }
}

package com.triedcoders.shareit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;

public class FileDetailActivity extends AppCompatActivity {
    DatabaseHelper db;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_detail);

        toolbar = findViewById(R.id.detailToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.detailCollapsingBar);

        db = new DatabaseHelper(this);
        Intent intent = getIntent();
        FileItem fileItem = db.getFileItem(intent.getLongExtra("FileItem",0));
        collapsingToolbarLayout.setTitle(fileItem.getFileName());
        collapsingToolbarLayout.setContentScrim(ContextCompat.getDrawable(getApplicationContext(), fileItem.getRepresentaion().getColor()));

        //Set the passed values
        TextView fileName = findViewById(R.id.detailFileName);
        fileName.setText(fileItem.getFileName());
        TextView extension = findViewById(R.id.detailExtension);
        extension.setText(fileItem.extension());
        RatingBar postRating = findViewById(R.id.detailRating);
        postRating.setRating(fileItem.getRating());
        TextView postedBy = findViewById(R.id.detailPostedBy);
        postedBy.setText(fileItem.getPostedBy());
        TextView postDate = findViewById(R.id.detailPostDate);
        postDate.setText(fileItem.posted());
        TextView fileSize = findViewById(R.id.detailFileSize);
        fileSize.setText(fileItem.getFormattedFileSize(getApplicationContext()));
        ImageView fileTypeImage = findViewById(R.id.detailFileTypeImage);
        fileTypeImage.setImageResource(fileItem.getRepresentaion().getIcon());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.file_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.fileDetailShare:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

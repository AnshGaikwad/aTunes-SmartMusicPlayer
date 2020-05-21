package com.example.musicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.Model.Upload;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Main3Activity extends AppCompatActivity {
    AppCompatEditText editTextTitle;
    TextView textViewImage;
    ProgressBar progressBar;
    Uri audioUri;
    StorageReference mStorageRef;
    StorageTask mUploadTask;
    DatabaseReference referenceSongs;
    Button uploadButton, upSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        editTextTitle = (AppCompatEditText)findViewById(R.id.songTitle);
        textViewImage = (TextView)findViewById(R.id.txtViewSongFileSelected);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        referenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("songs");
        uploadButton = (Button) findViewById(R.id.uploadButton);
        upSong = (Button) findViewById(R.id.upSong);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewImage.getText().toString().equals("No File Selected")){
                    Toast.makeText(getApplicationContext(),"Please Select An Image",Toast.LENGTH_LONG).show();
                }
                else {
                    if (mUploadTask!=null && mUploadTask.isInProgress()){
                        Toast.makeText(getApplicationContext(),"Song Upload Is Already In Progress",Toast.LENGTH_LONG).show();
                    }
                    else {
                        uploadFile();
                    }
                }
            }
        });
        upSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("audio/*");
                startActivityForResult(i,101);
            }
        });

    }
/*
    public void openAudioFile(View v)
    {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        startActivityForResult(i,101);

    }
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data.getData()!=null){
            audioUri = data.getData();
            String fileName = getFileName(audioUri);
            textViewImage.setText(fileName);
        }
    }
    private String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            try
            {
                if(cursor!=null && cursor.moveToFirst()) {
                    result= cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }


            }finally {
                cursor.close();

            }

        }
        if (result==null){
            result=uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut!=-1){
                result=result.substring(cut + 1);

            }
        }
        return result;
    }
    /*
    public void uploadAudioToFirebase(View v){
        if (textViewImage.getText().toString().equals("No File Selected")){
            Toast.makeText(getApplicationContext(),"Please Select An Image",Toast.LENGTH_LONG).show();
        }
        else {
            if (mUploadTask!=null && mUploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(),"Song Upload Is Already In Progress",Toast.LENGTH_LONG).show();
            }
            else {
                uploadFile();
            }
        }
    }
*/
    private void uploadFile() {
        if (audioUri!=null){
            String durationTxt;
            Toast.makeText(getApplicationContext(),"Uploading Please Wait...",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "."+getFileExtension(audioUri)) ;
            int durationInMillis = findSongDuration(audioUri);
            if (durationInMillis  == 0){
                durationTxt = "NA";

            }
            durationTxt = getDurationFromMilli(durationInMillis);
            final String finalDurationTxt = durationTxt;
            mUploadTask = storageReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                        @Override
                        public void onSuccess(Uri uri){
                            Upload uploadSong = new Upload(editTextTitle.getText().toString(),
                                    finalDurationTxt,uri.toString());
                                String uploadId = referenceSongs.push().getKey();
                            referenceSongs.child(uploadId).setValue(uploadSong);
                        }
                    });


                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>(){
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot){
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(),"No File Selected To Upload",Toast.LENGTH_LONG).show();
        }
    }

    private String getDurationFromMilli(int durationInMillis) {
        Date date = new Date(durationInMillis);
        SimpleDateFormat simple = new SimpleDateFormat("m:ss", Locale.getDefault());
        String myTime = simple.format(date);
        return myTime;
    }

    private int findSongDuration(Uri audioUri) {
        int timeInMillisec = 0;
        try{
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this,audioUri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeInMillisec = Integer.parseInt(time);
            retriever.release();
            return timeInMillisec;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private String getFileExtension(Uri audioUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(audioUri));

    }
}

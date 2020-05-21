
package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.musicplayer.CreateNotification.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";

    private ImageView pausePlayBtn, nextBtn, previousBtn;
    private TextView songNameTxt;
    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnabledBtn;
    private String mode = "ON";

    private MediaPlayer myMediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mSongName;

    private SeekBar positionBar, volumeBar;
    private TextView elapsedTimeLabel, remainingTimeLabel;
    private int totalTime;

    private ImageView sound1, sound2;


    private GestureDetectorCompat gestureDetectorCompat = null;




    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        DetectSwipeListener gestureListener = new DetectSwipeListener();
        gestureListener.setActivity(this);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);


        pausePlayBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);
        imageView = findViewById(R.id.logo);

        lowerRelativeLayout = findViewById(R.id.lower);
        voiceEnabledBtn = findViewById(R.id.voice_enabled_btn);
        songNameTxt = findViewById(R.id.songName);

        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);

        sound1 = findViewById(R.id.sound1);
        sound2 = findViewById(R.id.sound2);



        positionBar = findViewById(R.id.positionBar);


        volumeBar = findViewById(R.id.seek);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volumeNumber = progress/100f;
                myMediaPlayer.setVolume(volumeNumber, volumeNumber);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        parentRelativeLayout = findViewById(R.id.parentRelativeLayout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        validateRecieveValuesAndStartPlaying();

        thread();

        imageView.setBackgroundResource(R.drawable.logo);

        speechRecognizer.setRecognitionListener((new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsChanged) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(matchesFound != null){

                    if(mode.equals("ON")){
                        keeper = matchesFound.get(0);

                        if(keeper.equals("pause") || keeper.equals("pause the song") || keeper.equals("stop") || keeper.equals("stop the song")){
                            pausePlayBtn.setImageResource(R.drawable.play);
                            myMediaPlayer.pause();
                            Toast.makeText(MainActivity.this, "Command: " + keeper, Toast.LENGTH_LONG).show();
                        }
                        else if(keeper.equals("play the song") || keeper.equals("start the song") || keeper.equals("resume the song") || keeper.equals("play") || keeper.equals("resume") || keeper.equals("start")){
                            pausePlayBtn.setImageResource(R.drawable.pause);
                            myMediaPlayer.start();
                            Toast.makeText(MainActivity.this, "Command: " + keeper, Toast.LENGTH_LONG).show();
                        }else if(keeper.equals("play next song") || keeper.equals("next song") || keeper.equals("start next song") || keeper.equals("next") ){
                            playNextSong();
                            Toast.makeText(MainActivity.this, "Command: " + keeper, Toast.LENGTH_LONG).show();
                        }else if(keeper.equals("play previous song") || keeper.equals("start previous song") || keeper.equals("previous song") || keeper.equals("last song") || keeper.equals("previous") || keeper.equals("last")){
                            playPreviousSong();
                            Toast.makeText(MainActivity.this, "Command: " + keeper, Toast.LENGTH_LONG).show();
                        }else if(keeper.equals("back")){
                            myMediaPlayer.pause();
                            Toast.makeText(MainActivity.this, "Command: " +keeper, Toast.LENGTH_SHORT).show();
                            finish();
                        }else if(keeper.equals("mute") || keeper.equals("volume off") || keeper.equals("volume of")){
                            mute();
                            Toast.makeText(MainActivity.this, "Command: " +keeper, Toast.LENGTH_SHORT).show();
                        }else if(keeper.equals("volume") || keeper.equals("volume on")){
                            volume();
                            Toast.makeText(MainActivity.this, "Command: " +keeper, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Invalid Command: " +keeper, Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        }));

        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (mode.equals("ON")) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: //action down means long press
                            speechRecognizer.startListening(speechRecognizerIntent);
                            keeper = "";
                            break;

                        case MotionEvent.ACTION_UP:
                            speechRecognizer.stopListening();
                            break;
                    }
                }

                return false;
            }
        });


        voiceEnabledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals("ON")){
                    mode = "OFF";
                    voiceEnabledBtn.setText("laZyMode: OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                    volumeBar.setVisibility(View.VISIBLE);
                    sound1.setVisibility(View.VISIBLE);
                    sound2.setVisibility(View.VISIBLE);

                }else{
                    mode = "ON";
                    voiceEnabledBtn.setText("laZyMode: ONN");
                    lowerRelativeLayout.setVisibility(View.GONE);
                    volumeBar.setVisibility(View.GONE);
                    sound1.setVisibility(View.GONE);
                    sound2.setVisibility(View.GONE);

                }
            }
        });


        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseSong();
            }
        });


        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myMediaPlayer.getCurrentPosition()>0){
                    playPreviousSong();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myMediaPlayer.getCurrentPosition()>0){
                    playNextSong();
                }
            }
        });







    }

    // In this method we will be recieving the values we got in main2activity audio song, song name position, etc
    private void validateRecieveValuesAndStartPlaying(){
        if(myMediaPlayer != null){
            myMediaPlayer.reset();
            myMediaPlayer.pause();
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("song");
        mSongName = mySongs.get(position).getName();
        String songName = intent.getStringExtra("name");

        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position = bundle.getInt("position", 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());



        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);
        totalTime = myMediaPlayer.getDuration();

        myMediaPlayer.start();

        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextSong();
            }
        });


    }




    public void playPauseSong(){
        imageView.setBackgroundResource(R.drawable.four);

        if(myMediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();
        }else{
            pausePlayBtn.setImageResource(R.drawable.pause);
            myMediaPlayer.start();

            imageView.setBackgroundResource(R.drawable.five);
        }
    }

    private void thread(){


        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    myMediaPlayer.seekTo(progress);
                    positionBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        new Thread(new Runnable() {
            @Override
            public void run() {
                while(myMediaPlayer != null){
                    try{
                        if(myMediaPlayer.isPlaying()) {
                            Message message = new Message();
                            message.what = myMediaPlayer.getCurrentPosition();
                            handler.sendMessage(message);
                            Thread.sleep(1000);
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void playNextSong(){
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position+1)%mySongs.size());

        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);




        mSongName = mySongs.get(position).getName();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.three);

        if(myMediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.pause);
        }else{
            pausePlayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);
        }

        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextSong();
            }
        });


    }

    public void volume(){
        myMediaPlayer.setVolume(1.0f,1.0f);
    }

    public void mute(){
        myMediaPlayer.setVolume(0, 0);

    }

    public void playPreviousSong(){
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position-1)<0 ? (mySongs.size()-1) : (position-1));

        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);


        mSongName = mySongs.get(position).getName();


        songNameTxt.setText(mSongName);
        myMediaPlayer.start();


        imageView.setBackgroundResource(R.drawable.two);

        if(myMediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.pause);
        }else{
            pausePlayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);
        }

    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message message) {

            int currentPosition = message.what;
            positionBar.setProgress(currentPosition);

            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime-currentPosition);

            remainingTimeLabel.setText("- "+remainingTime);

        }
    };

    public String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetectorCompat.onTouchEvent(event);
        return true;
        //return super.onTouchEvent(event);
    }

}


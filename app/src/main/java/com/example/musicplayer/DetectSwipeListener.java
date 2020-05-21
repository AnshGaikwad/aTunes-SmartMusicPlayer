package com.example.musicplayer;

import android.media.MediaPlayer;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class DetectSwipeListener extends GestureDetector.SimpleOnGestureListener {

    public static int MIN_SWIPE_DISTANCE_X = 100;
    public static int MIN_SWIPE_DISTANCE_Y = 100;

    public static int MAX_SWIPE_DISTANCE_X = 1000;
    public static int MAX_SWIPE_DISTANCE_Y = 1000;

    private MainActivity activity = null;

    private MediaPlayer myMediaPlayer;

    public MainActivity getActivity() {
        return activity;
    }

    public void setActivity(MainActivity activity){
        this.activity = activity;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


        float deltaX = e1.getX() - e2.getX();
        float deltaY = e1.getY() - e2.getY();

        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        if(deltaXAbs >= MIN_SWIPE_DISTANCE_X && deltaX <= MAX_SWIPE_DISTANCE_X){
            if(deltaX > 0){
                this.activity.playPreviousSong();

            }else{
                this.activity.playNextSong();

            }
        }



        if(deltaYAbs >= MIN_SWIPE_DISTANCE_Y && deltaX <= MAX_SWIPE_DISTANCE_Y){
            if(deltaY > 0){
                this.activity.volume();

            }else{
                this.activity.mute();
            }
        }






        return true;
        //return super.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        this.activity.playPauseSong();
        return true;
        //return super.onDoubleTap(e);
    }

    /*
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {


        this.activity.

        return true
        //return super.onSingleTapConfirmed(e);
    }
    */



}

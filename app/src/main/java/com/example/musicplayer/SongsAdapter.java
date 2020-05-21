package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Model.Upload;

import java.io.IOException;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsAdapterViewHolder> {
    Context context;
    List<Upload> arrayListSongs;
    public SongsAdapter(Context context,List<Upload> arrayListSongs){
        this.context= context;
        this.arrayListSongs = arrayListSongs;
    }
    @NonNull
    @Override
    public SongsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_item,viewGroup,false);
        return new SongsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsAdapterViewHolder holder, int i) {
        Upload uploadSong = arrayListSongs.get(i);
        holder.titleTxt.setText(uploadSong.getSongTitle());
        holder.durationTxt.setText(uploadSong.getSongDuration());
    }

    @Override
    public int getItemCount() {
        return arrayListSongs.size();
    }

    public class SongsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView titleTxt,durationTxt;

        public SongsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt =(TextView)itemView.findViewById(R.id.song_title);
            durationTxt = (TextView)itemView.findViewById(R.id.song_duration);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            try {
                ((ShowSongsActivity)context).playSong(arrayListSongs,getAdapterPosition());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}

package com.example.musicapp07;

import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerSongAdapter extends RecyclerView.Adapter<RecyclerSongAdapter.ViewHolder>{

    ArrayList<Song> sData;

    public RecyclerSongAdapter(ArrayList<Song> sData) {
        this.sData = sData;
    }

    @NonNull
    @Override
    public RecyclerSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerSongAdapter.ViewHolder holder, int position) {
        Song s = sData.get(position);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date date = format.parse(s.updated_time);
            String required_format  = (String) DateFormat.format("MM-dd-yyyy", date);
            s.updated_time = required_format;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_trackname.setText(s.getTrack_name());
        holder.tv_artistname.setText(s.getArtist_name());
        holder.tv_albumname.setText(s.getAlbum_name());
        holder.tv_date.setText(s.getUpdated_time());
        holder.song = s;


    }

    @Override
    public int getItemCount() {
        return sData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_artistname;
        TextView tv_albumname;
        TextView tv_trackname;
        TextView tv_date;
        Song song;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_trackname = itemView.findViewById(R.id.tv_trackname);
            tv_artistname = itemView.findViewById(R.id.tv_artistname);
            tv_albumname = itemView.findViewById(R.id.tv_albumname);
            tv_date = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = song.getTrack_share_url();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    v.getContext().startActivity(i);
                }
            });
        }
    }
}

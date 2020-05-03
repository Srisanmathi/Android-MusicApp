package com.example.musicapp07;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {
    public SongAdapter(@NonNull Context context, int resource, @NonNull List<Song> objects) {
        super(context, resource, objects);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Song s = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_song,parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_artistname = convertView.findViewById(R.id.tv_artistname);
            viewHolder.tv_albumname = convertView.findViewById(R.id.tv_albumname);
            viewHolder.tv_trackname = convertView.findViewById(R.id.tv_trackname);
            viewHolder.tv_date = convertView.findViewById(R.id.tv_date);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date date = format.parse(s.updated_time);
            String required_format  = (String) DateFormat.format("MM-dd-yyyy", date);
            s.updated_time = required_format;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.tv_artistname.setText("Date : "+ s.updated_time);
        viewHolder.tv_albumname.setText("Album : " +  s.album_name);
        viewHolder.tv_trackname.setText("Track : " + s.track_name);
        viewHolder.tv_date.setText("Artist : " + s.artist_name);

        return convertView;
    }

    private static class ViewHolder{
        TextView tv_artistname;
        TextView tv_albumname;
        TextView tv_trackname;
        TextView tv_date;
    }
}

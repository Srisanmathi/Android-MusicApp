package com.example.musicapp07;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText et_search;
    private SeekBar sb_limit;
    private TextView tv_limit;
    private Button bt_search;
    private RadioGroup rg_sortby;
    //private ListView lv_results;
    private ProgressBar pb;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    int progressChangedValue=0;
    String chosenSort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("MusixMatch Track Search");
        //recyclerView = findViewById(R.id.my_recycler_view);
        et_search = findViewById(R.id.et_search);
        sb_limit = findViewById(R.id.sb_limit);
        tv_limit = findViewById(R.id.tv_limit);
        bt_search = findViewById(R.id.bt_search);
        rg_sortby = findViewById(R.id.rg_sortby);
        recyclerView = findViewById(R.id.my_recycler_view);
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.INVISIBLE);

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        sb_limit.setMax(90);

        sb_limit.setProgress(0);

        sb_limit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = (progress/5)*5;
                progressChangedValue = progressChangedValue+10;
                tv_limit.setText("Limit : " + progressChangedValue);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //Just for the display
        rg_sortby.check(R.id.rb_track);

        chosenSort = "s_track_rating";

        rg_sortby.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.rb_track:
                    {
                        chosenSort = "s_track_rating";
                        new GetDataAsync().execute( "http://api.musixmatch.com/ws/1.1/track.search?" + "q=" + et_search.getText().toString() +
                                "&page_size=" + progressChangedValue + "&s_track_rating=desc" + "&apikey=dce1692392e70a6a2883acdaab411e6f");
                        break;
                    }
                    case R.id.rb_artist:
                    {
                        chosenSort = "s_artist_rating";
                        new GetDataAsync().execute( "http://api.musixmatch.com/ws/1.1/track.search?" + "q=" + et_search.getText().toString() +
                                "&page_size=" + progressChangedValue + "&s_artist_rating=desc" + "&apikey=dce1692392e70a6a2883acdaab411e6f");
                        break;
                    }
                }
            }
        });

        bt_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(isConnected()){

                    if(chosenSort.equals("s_track_rating")){

                        new GetDataAsync().execute( "http://api.musixmatch.com/ws/1.1/track.search?" + "q=" + et_search.getText().toString() +
                                "&page_size=" + progressChangedValue + "&s_track_rating=desc" + "&apikey=dce1692392e70a6a2883acdaab411e6f");
                    }
                    else if(chosenSort.equals("s_artist_rating"))
                    {

                        new GetDataAsync().execute( "http://api.musixmatch.com/ws/1.1/track.search?" + "q=" + et_search.getText().toString() +
                                "&page_size=" + progressChangedValue + "&s_artist_rating=desc" + "&apikey=dce1692392e70a6a2883acdaab411e6f");
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }


     class GetDataAsync extends AsyncTask<String, Void, ArrayList<Song>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Song> doInBackground(String... params) {
            HttpURLConnection connection = null;
            ArrayList<Song> result = new ArrayList<>();
            try {

                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);

                    JSONObject message = root.getJSONObject("message");
                    JSONObject body = message.getJSONObject("body");
                    JSONArray tracklist = body.getJSONArray("track_list");
                    for (int i=0;i<tracklist.length();i++) {
                        JSONObject trackJson = tracklist.getJSONObject(i);
                        JSONObject track = trackJson.getJSONObject("track");
                        Song s = new Song();
                        s.track_name = track.getString("track_name");
                        s.album_name = track.getString("album_name");
                        s.artist_name = track.getString("artist_name");
                        s.updated_time = track.getString("updated_time");
                        s.track_share_url = track.getString("track_share_url");

                        result.add(s);

                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }


            return result;
        }


        @Override
        protected void onPostExecute(final ArrayList<Song> result) {

            pb.setVisibility(View.INVISIBLE);
            if(result.size()==0){
                Toast.makeText(MainActivity.this, "No results found", Toast.LENGTH_SHORT).show();
            }
            //SongAdapter adapter = new SongAdapter(getApplicationContext(),R.layout.item_song,result);
            //lv_results.setAdapter(adapter);

            mAdapter = new RecyclerSongAdapter(result);
            recyclerView.setAdapter(mAdapter);


//            lv_results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Song chosen =  result.get(position);
//                    String url = chosen.getTrack_share_url();
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    startActivity(i);
//
//                }
//            });
        }
    }
}

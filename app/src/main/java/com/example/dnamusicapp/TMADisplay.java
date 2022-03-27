package com.example.dnamusicapp;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;

import java.io.IOException;
import java.util.ArrayList;

public class TMADisplay extends AppCompatActivity {

    ArrayList<ArrayList<String>> TMAVals = new ArrayList<>();
    ArrayList<ArrayList<String>> PlaylistSongID = new ArrayList<>();
    ArrayList<ArrayList<Integer>> TMALengthCheck = new ArrayList<>();

    String Type = "";
    String ObjectType = "";

    //ExpandableList
    ExpandableListView TMAGrid;

    //Header
    TextView TMAAreaName;
    TextView TMAObjectCount;

    //Object
    LinearLayout TMAObjectDisplay;
    TextView TMAInstances;
    Button Expand;

    //Song Bars
    LinearLayout SongBar;

    ImageView SongImage;

    TextView SongName;
    TextView SongArtist;
    TextView SongLength;

    //Song Bar Extras
    TextView SongPR;
    TextView SongUR;

    int TMAType;

    //Time ints
    int TotalSec;
    int Modulus;
    int Sec;
    int Min;
    int Hour;

    //CustomAdapter
    CustomAdapter0 ExpandableAdapter = new CustomAdapter0();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_m_a_display);
        TMAGrid = findViewById(R.id.TMAGrid);
        GetStuff();
        SortStuff();
        TMAGrid.setAdapter(ExpandableAdapter);
    }

    public void GetStuff() {
        Intent Stuff = getIntent();
        Type = Stuff.getStringExtra("AreaName");

            TMAVals.add(Stuff.getStringArrayListExtra("AllTags"));
            TMAVals.add(Stuff.getStringArrayListExtra("AllMoods"));
            TMAVals.add(Stuff.getStringArrayListExtra("AllArtists"));

            //Do Nothing

        int Num = Stuff.getIntExtra("SongSize", 16);

        for (int i = 0; i < Num; i++) {
            PlaylistSongID.add(Stuff.getStringArrayListExtra("Song" + i));
        }

        ObjectType = Stuff.getStringExtra("ObjectType");

    }

    public void SortStuff() {

        switch (Type) {
            case "Tags":
                TMAType = 0;
                for (int i = 0; i < TMAVals.get(TMAType).size(); i++) {
                    TMALengthCheck.add(new ArrayList<Integer>());
                    for (int e = 0; e < PlaylistSongID.get(9).size(); e++) {
                        if (PlaylistSongID.get(9).get(e).contains(TMAVals.get(0).get(i))) {
                            TMALengthCheck.get(i).add(e);
                        }
                    }
                }

                break;
            case "Moods":
                TMAType = 1;
                for (int i = 0; i < TMAVals.get(TMAType).size(); i++) {
                    TMALengthCheck.add(new ArrayList<Integer>());
                    for (int e = 0; e < PlaylistSongID.get(7).size(); e++) {
                        if (PlaylistSongID.get(7).get(e).contains(TMAVals.get(1).get(i)))
                            TMALengthCheck.get(i).add(e);
                        {
                        }
                    }
                }
                break;
            case "PR Artists":
                //
                //Mismatch and stuff NOV 27/28 2020
                //
                TMAType = 2;
                for (int i = 0; i < TMAVals.get(TMAType).size(); i++) {
                    TMALengthCheck.add(new ArrayList<Integer>());
                    for (int e = 0; e < PlaylistSongID.get(1).size(); e++) {
                        if (PlaylistSongID.get(1).get(e).contains(TMAVals.get(2).get(i)))
                            TMALengthCheck.get(i).add(e);
                        {
                        }
                    }
                }
                break;
        }
    }

    public void TMAHeaderID(View view) {
        //TextViews
        TMAAreaName = view.findViewById(R.id.TMAAreaName);
        TMAObjectCount = view.findViewById(R.id.TMAObjectCount);
    }

    public void TMAObjectID(View view) {
        //LinearLayout
        TMAObjectDisplay = view.findViewById(R.id.ObjectDisplay);
        //TextViews
        TMAInstances = view.findViewById(R.id.Instances);
        //Button
        Expand = view.findViewById(R.id.ExpandButton);
    }

    public void TMAHeaderSetInfo() {
        TMAAreaName.setText(Type);
        TMAObjectCount.setText(TMAVals.get(TMAType).size() + " " + Type);
    }

    public void TMAObjectSetInfo(int i) {
        View Object = getLayoutInflater().inflate(R.layout.horizontal_view_object, null);
        TextView TMAObject = Object.findViewById(R.id.TMAObject);
        TMAObject.setText(TMAVals.get(TMAType).get(i));
        TMAObjectDisplay.addView(Object);
        if (TMALengthCheck.get(i).size() == 1) {
            TMAInstances.setText(TMALengthCheck.get(i).size() + " Instance");
        } else {
            TMAInstances.setText(TMALengthCheck.get(i).size() + " Instances");
        }

    }

    //
    //
    //PlaylistView Things
    //
    //

    public void LengthCalc(int Total) {
        int Temp = Total;
        Hour = Total / 3600;
        Total = Total % 3600;
        Min = Total / 60;
        Total = Total % 60;
        Sec = Total;
        //Return Orginal value In case later
        Total = Temp;
    }

    public void GetExtraInfoBarsSongs(View view) {
        GetRegularBarsSongs(view);
        SongPR = view.findViewById(R.id.SongPlaylistRank);
        SongUR = view.findViewById(R.id.SongUniversalRank);
    }

    public void GetRegularBarsSongs(View view) {
        SongBar = view.findViewById(R.id.SongBar);
        SongImage = view.findViewById(R.id.SongImage);
        SongName = view.findViewById(R.id.SongName);
        SongArtist = view.findViewById(R.id.SongArtist);
        SongLength = view.findViewById(R.id.SongLength);
    }

    public void SetRegularInfoSongs(int i) {
        //SongImage.setImageResource(Integer.parseInt(PlaylistSongID.get(4).get(i)));
        Glide.with(this).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholder).into(SongImage);

        if (PlaylistSongID.get(0).get(i).length() >= 18) {
            String tooLong = "";
            for (int e = 0; e < 18; e++) {
                tooLong += PlaylistSongID.get(0).get(i).charAt(e);
            }
            tooLong += "...";
            SongName.setText(tooLong);
        } else {
            SongName.setText(PlaylistSongID.get(0).get(i));
        }

        if (PlaylistSongID.get(1).get(i).length() >= 18) {
            String tooLong = "";

            for (int e = 0; e < 18; e++) {
                tooLong += PlaylistSongID.get(1).get(i).charAt(e);
            }
            tooLong += "...";
            SongArtist.setText(tooLong);
        } else {
            SongArtist.setText(PlaylistSongID.get(1).get(i));
        }

        LengthCalc(Integer.parseInt(PlaylistSongID.get(11).get(i)));
        if (Hour >= 1) {
            if (Min < 10) {
                SongLength.setText(Hour + ":" + "0" + Min + ":" + Sec);
                if (Sec < 10) {
                    SongLength.setText(Hour + ":" + "0" + Min + ":" + "0" + Sec);
                }

            } else {
                SongLength.setText(Hour + ":" + Min + ":" + Sec);
                if (Sec < 10) {
                    SongLength.setText(Hour + ":" + Min + ":" + "0" + Sec);
                }
            }

        } else if (Min >= 1) {
            if (Min < 10) {
                SongLength.setText("0" + Min + ":" + Sec);
                if (Sec < 10) {
                    SongLength.setText("0" + Min + ":" + "0" + Sec);
                }
            } else {
                SongLength.setText(Min + ":" + Sec);
            }
            if (Sec < 10) {
                SongLength.setText(Min + ":" + "0" + Sec);
            } else {
                SongLength.setText(Min + ":" + Sec);
            }
        } else {
            SongLength.setText(Sec + "");
        }
    }

    public void SetExtraInfoSongs(int i) {
        SetRegularInfoSongs(i);
        SongPR.setText(PlaylistSongID.get(5).get(i));
        SongUR.setText(PlaylistSongID.get(6).get(i));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void PlayMusic(int e, int i) throws IOException {
        //Uri MusicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String MusicUri = PlaylistSongID.get(3).get(TMALengthCheck.get(e).get(i));
        MediaPlayer MusicPlayer = new MediaPlayer();
        MusicPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build());
        MusicPlayer.setDataSource(MusicUri);
        MusicPlayer.prepare();
        MusicPlayer.start();
    }

    public void PlayQueue(int GP, int CP) {

        Intent intent = new Intent(getApplicationContext(), MusicPlayer.class);
        //Ok so something we are sending is too big, limit to send is 1 mb. Alright so I need to chop up all of the loops into smaller bits to fix this. Or worse case scenario just copy the grab from file void.

        //
        //Alright so yeah we are going to have to just recall the void for getting all the info out of the playlist file
        //

        for (int i = 0; i < TMALengthCheck.size(); i++) {
            intent.putExtra("SongChecker" + i, TMALengthCheck.get(i));
        }

        //For selected queue specify what was selected (AlbumID#, ArtistID#, SongID#, or Playlist)
       // intent.putExtra("SelectQueue", SelectedQueue);
        intent.putExtra("QueueMethod", "Tap");
        intent.putExtra("GroupPos", GP);
        intent.putExtra("ChildPos", CP);

        intent.putExtra("SongCheckerLength", TMALengthCheck.size());
        intent.putExtra("ObjectMethod", "Artist");
        intent.putExtra("SortMethod", "Alphabetical");


        //This stops the music and resets for a new queue
        stopService(new Intent(this, AudioPlayer.class));

        startActivity(intent);

    }




    private class CustomAdapter0 extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return TMALengthCheck.size() + 1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return TMALengthCheck.get(groupPosition - 1).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {

            View TMA = getLayoutInflater().inflate(R.layout.tma_header, null);

            if (groupPosition == 0) {
                //Header
                TMA = getLayoutInflater().inflate(R.layout.tma_header, null);
                TMAHeaderID(TMA);
                TMAHeaderSetInfo();
            } else {
                TMA = getLayoutInflater().inflate(R.layout.tma_object, null);
                TMAObjectID(TMA);
                TMAObjectSetInfo(groupPosition - 1);
                Expand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isExpanded) {
                            Expand.setText("Expand");
                            TMAGrid.collapseGroup(groupPosition);

                        } else {
                            Expand.setText("Collapse");
                            TMAGrid.expandGroup(groupPosition);
                        }
                    }
                });
            }
            return TMA;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View SongView = getLayoutInflater().inflate(R.layout.song_bar_object_small_extrainfo, parent, false);
            ObjectType = "Small EI";
            if (ObjectType.equals("Small EI")) {
                SongView = getLayoutInflater().inflate(R.layout.song_bar_object_small_extrainfo, parent, false);
                GetExtraInfoBarsSongs(SongView);
            } else if (ObjectType.equals("Small")) {
                SongView = getLayoutInflater().inflate(R.layout.song_bar_object_small, parent, false);
                GetRegularBarsSongs(SongView);
            } else if (ObjectType.equals("Big EI")) {
                SongView = getLayoutInflater().inflate(R.layout.song_bar_object_big_extrainfo, parent, false);
                GetExtraInfoBarsSongs(SongView);
            } else if (ObjectType.equals("Big")) {
                SongView = getLayoutInflater().inflate(R.layout.song_bar_object_big, parent, false);
                GetRegularBarsSongs(SongView);
            }

            switch (ObjectType) {
                case "Small EI":
                    SetExtraInfoSongs(TMALengthCheck.get(groupPosition - 1).get(childPosition));
                    SongBar.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {
                            //
                            //Play song and shit
                            //
                            PlayQueue(groupPosition - 1, childPosition);

                        }
                    });
                    break;
                case "Small":
                    SetRegularInfoSongs(TMALengthCheck.get(groupPosition - 1).get(childPosition));
                    SongBar.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {
                            //
                            //Play song and shit
                            //
                            PlayQueue(groupPosition - 1, childPosition);
                        }
                    });
                    break;
                case "Big EI":
                    SetExtraInfoSongs(TMALengthCheck.get(groupPosition - 1).get(childPosition));
                    SongBar.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {
                            //
                            //Play song and shit
                            //
                            PlayQueue(groupPosition - 1, childPosition);
                        }
                    });
                    break;
                case "Big":
                    SetRegularInfoSongs(TMALengthCheck.get(groupPosition - 1).get(childPosition));
                    SongBar.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {
                            //
                            //Play song and shit
                            //
                            PlayQueue(groupPosition - 1, childPosition);
                        }
                    });
                    break;
            }

            return SongView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
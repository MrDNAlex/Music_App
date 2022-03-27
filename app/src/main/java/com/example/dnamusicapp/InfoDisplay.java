package com.example.dnamusicapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class InfoDisplay extends AppCompatActivity {

    boolean Playlist = false;
    boolean SelectMode = false;


    ArrayList<ArrayList<Integer>> OwnedObjects = new ArrayList<>(); //Skins, Songs, Album

    int FilePos;

    //int LoopLength;

    //Playlist Position Info
    // int CP;
    // int GP;

    //Time ints
    //int TotalSec;
    // int Modulus;
    int Sec;
    int Min;
    int Hour;

    //Loop Numbers
    int SongNum;
    int ArtistNum;
    int AlbumNum;

    // int SongNumMaster;
    // int ArtistNumMaster;
    // int AlbumNumMaster;

    int SkinSongNum;
    int SkinArtistNum;
    int SkinAlbumNum;

    int VariableNumSong = 19;
    //PSID:  Song Name, Artist Name, Album Name, Song Path, Album Art, Playlist Rank, Universal Rank, Playlist Music Mood, Universal Music Mood, Playlist Music Tags, Universal Music Tags, Song Length,Playlist Times Played, Universal Times played, Last Time Played (date), AlbumID
    int VariableNumArtist = 16;
    //AID: Artist Name, Artist Playlist Rank,Artist Universal Rank, Artist Playlist Mood, Artist Universal Mood, Artist Playlist Tags, Artist Universal Tags, Artist Length, Artist Playlist Times Played, Artist Universal Times Played, Artist Playlist Total Time Played,  Artist Universal Total Time Played, Last Time Played (date), [SIDx, SIDx, SIDx, SIDx,.... ]
    int VariableNumAlbum = 18;
    //AlID: Album Name, Album Artist Name, Album Art, Album Playlist Rank, Album Universal Rank, Album Playlist Mood, Album Universal Mood, Album Playlist Tags, Album Universal Tags, Album Length, Album Playlist Times Played,  Album Universal Times Played, Album Playlist Total Time Played ,Album Universal Total Time Played, Last Time Played, AlbumID.  [SIDx, SIDx, SIDx, SIDx,....]

    int MasterVariableNumSong = 16;
    int MasterVariableNumArtist = 13;
    int MasterVariableNumAlbum = 15;

    ArrayList<ArrayList<String>> PlaylistSongID = new ArrayList<>();
    ArrayList<ArrayList<String>> PlaylistAlbumID = new ArrayList<>();
    ArrayList<ArrayList<String>> PlaylistArtistID = new ArrayList<>();

    ArrayList<ArrayList<String>> SkinSongID = new ArrayList<>();
    ArrayList<ArrayList<String>> SkinAlbumID = new ArrayList<>();
    ArrayList<ArrayList<String>> SkinArtistID = new ArrayList<>();

    ArrayList<String> ObjectChosen = new ArrayList<>();

    ArrayList<String> AllTags = new ArrayList<>();
    ArrayList<String> AllMoods = new ArrayList<>();

    ArrayList<ArrayList<String>> ContainedPlaylists = new ArrayList<>();

    ArrayList<Integer> SongLengthChecker = new ArrayList<>();

    ArrayList<ArrayList<String>> LoadedPositions = new ArrayList<>();

    String ObjectID = "";
    String PlaylistFileName = "";
    String Code = "";
    //String ArtistID = "";
    //String AlbumID = "";

    String ObjectType = "Small EI";
    String ObjectMethod = "";
    String SortMethod = "";
    String ObjectMode = "";
    String SelectMethod = "";
    String AppAppearance;

    String SearchTerm;

    String FileID;

    ExpandableListView InfoList;

    //Sort option Header
    TextView SortOptionHeader;

    //Song Bars
    LinearLayout SongBar;

    ImageView SongImage;

    TextView SongName;
    TextView SongArtist;
    TextView SongLength;

    //Second Page
    GridView EditList;

    //
    //Info Displays
    //
    LinearLayout PlaylistContain;
    ImageView InfoDisplayImage;
    TextView InfoDisplaySongUniversalRank;
    TextView InfoDisplaySongPlaylistRank;
    TextView InfoDisplayArtistUniversalRank;
    TextView InfoDisplayArtistPlaylistRank;
    TextView InfoDisplayAlbumUniversalRank;
    TextView InfoDisplayAlbumPlaylistRank;
    TextView InfoDisplaySongName;
    TextView InfoDisplayArtistName;
    TextView InfoDisplayAlbumName;
    TextView InfoDisplayTotalTime;
    TextView InfoDisplayTimesPlayed;

    //Edit Extra Info
    // TextView EditFavStart;
    //  TextView EditFavEnd;

    //Edit Object
    TextView ObjectEdit;
    TextView EditRankType;
    LinearLayout EditRank;
    ImageView EditImageView;
    ImageView EditImageSelect;

    //SelectMode
    SearchView SearchBar;

    //Save Button
    Button SaveEdit;


    //Floating Menu Shit
    boolean PlayMenuOpen = false;
    boolean AddMenuOpen = false;

    BottomNavigationView BottomNavigation;

MiniPlayer MiniPlayerReceiver = new MiniPlayer();

    LinearLayout MusicPlayerMini;

    //Adapters
    CustomAdapter1 ExpandableAdapterAll = new CustomAdapter1();
    CustomAdapter2 InfoDisplayEditAdapter = new CustomAdapter2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_display);

        RelativeLayout InfoDisplayBackground = findViewById(R.id.InfoDisplayBackground);
        AccessSettings();

        InfoList = findViewById(R.id.InfoList);
        MusicPlayerMini = findViewById(R.id.MusicPlayerMini);
        MusicPlayerMini.setVisibility(View.INVISIBLE);


        //So gotta load all regular playlist info shit
        //Then gotta load all the skins from Master File. Unless... you save all the skins related to an object inside the playlist aswell, then you just call for Master File or Playlist File

        //Make a loop thing that checks every Skin in select category and spits a number back to tell
        //Then use the same system as the sorting screen to display and select the skin and stuff
        //Oh wait we can just just display one at a time it's probably a lot easier

        //Ah shit we gotta remake all of this it looks like because SongLength thing only works for displaying songs

SetUpNav();
        //Alright so gotta reset this system to work by compairing File Song ID's

        TabHost tabHost = findViewById(R.id.tabhost);
        InfoList = findViewById(R.id.InfoList);
        EditList = findViewById(R.id.EditList);

        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Info");
        spec.setIndicator("Info");
        spec.setContent(R.id.ObjectInfo);
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Edit");
        spec.setIndicator("Edit");
        spec.setContent(R.id.Edit);
        tabHost.addTab(spec);

        GetInfo();
        ReadPlaylistInfoFile();
        AntennaSetup();
        CheckAudioPlayer();
        //loadMasterData();
        //SortEverything();         //Might not need this
        DetectOwnedObjects();

        InfoList.setAdapter(ExpandableAdapterAll);
        EditList.setAdapter(InfoDisplayEditAdapter);
        GetSearchHeader();
        SaveEverything();

        if (AppAppearance.equals("Dark")) {
            InfoDisplayBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.background_alex3));
            for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setTextColor(Color.rgb(255, 255, 255));
            }
        }

    }

    public void SetUpNav () {

        BottomNavigation = findViewById(R.id.bottom_navigation);

        BottomNavigation.setSelectedItemId(R.id.LibraryNav);

        BottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.HomeNav:
                        Intent Home = new Intent(getApplicationContext(), PlaylistView.class);
                        startActivity(Home);
                        break;
                    case R.id.LibraryNav:
                        Intent SongLibrary = new Intent(getApplicationContext(), MusicLibrarySong.class);
                        startActivity(SongLibrary);
                        break;
                    case R.id.NPlaylistNav:
                        Intent NewPlay = new Intent(getApplicationContext(), CreateNewPlaylist.class);
                        startActivity(NewPlay);
                        break;
                }
                return false;
            }
        });
    }

    public void CheckAudioPlayer() {

        Intent AudioExist = new Intent();
        AudioExist.setAction("CheckExist");
        sendBroadcast(AudioExist);

    }

    public void AntennaSetup() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("AudioExist");
        registerReceiver(MiniPlayerReceiver, intentFilter);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetMiniPlayer(Intent intent) {

        String FPath = intent.getStringExtra("FilePath");

        int SongNum = 0;
        int SongPos = intent.getIntExtra("Position", 0);
        ArrayList<ArrayList<String>> PlaylistSongID = new ArrayList<>();
        String SongID = intent.getStringExtra("SongID");
        boolean Paused = intent.getBooleanExtra("Paused", false);

        ImageView MiniPlayerImage = findViewById(R.id.MiniPlayerImage);
        ImageView MPPlayPause = findViewById(R.id.MPPlayPause);
        TextView MiniPlayerSongName = findViewById(R.id.MiniPlayerSongName);
        TextView MiniPlayerSongArtist = findViewById(R.id.MiniPlayerSongArtist);
        MusicPlayerMini.setVisibility(View.VISIBLE);

        if (AppAppearance.equals("Dark")) {
            MiniPlayerSongName.setTextColor(Color.rgb(255, 255, 255));
            MiniPlayerSongArtist.setTextColor(Color.rgb(255, 255, 255));
            MusicPlayerMini.setBackground(ContextCompat.getDrawable(this, R.drawable.songbarobjectdark));
            MPPlayPause.setColorFilter(Color.rgb(255,255,255));
        }

        try {
            JSONObject InfoFile = new JSONObject(loadJsonFile(FPath + ".json"));
            JSONArray TotalSongNum = InfoFile.getJSONArray("TotalSongNum");

            SongNum = (int) TotalSongNum.get(0);

            //
            //Initialize Arrays
            //
            if (FPath.equals("MasterSongDataFile")) {
                //Initialize Array PlaylistSongID
                for (int i = 0; i < 16; i++) {
                    PlaylistSongID.add(new ArrayList<String>());
                }

                //Load All Song Info into 2D Array
                for (int i = 0; i < SongNum; i++) {
                    JSONArray SID = InfoFile.getJSONArray("SID" + i);
                    for (int e = 0; e < 16; e++) {
                        PlaylistSongID.get(e).add(SID.get(e).toString());
                    }
                }
            } else {
                //Initialize Array SongID
                for (int i = 0; i < 19; i++) {
                    PlaylistSongID.add(new ArrayList<String>());
                }

                //Load All Song Info into 2D Array
                for (int i = 0; i < SongNum; i++) {
                    JSONArray SID = InfoFile.getJSONArray("SID" + i);
                    for (int e = 0; e < 19; e++) {
                        PlaylistSongID.get(e).add(SID.get(e).toString());
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < SongNum; i++) {


            if (FPath.equals("MasterSongDataFile")) {
                if (PlaylistSongID.get(15).get(i).equals(SongID)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Bitmap bitmap = null;
                        try {

                            bitmap = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(PlaylistSongID.get(4).get(i)), new Size(200, 200), null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (bitmap != null) {
                            //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            // bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                            MiniPlayerImage.setImageBitmap(bitmap);
                        } else {
                            Drawable drawable;
                            if (AppAppearance.equals("Dark")) {
                                drawable = getResources().getDrawable(R.drawable.placeholderdark);
                            } else {
                                drawable = getResources().getDrawable(R.drawable.placeholder);
                            }
                            MiniPlayerImage.setImageDrawable(drawable);
                        }
                    } else {
                        if (AppAppearance.equals("Dark")) {
                            Glide.with(this).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholderdark).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(MiniPlayerImage);
                        } else {
                            Glide.with(this).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(MiniPlayerImage);
                        }
                    }

                    MiniPlayerSongName.setText(PlaylistSongID.get(0).get(i));
                    MiniPlayerSongArtist.setText(PlaylistSongID.get(1).get(i));

                    if (Paused) {
                        MPPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    } else {
                        MPPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
                    }

                    MPPlayPause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Paused) {
                                Intent MiniPlayerAction = new Intent();
                                MiniPlayerAction.setAction("PlayMP");
                                sendBroadcast(MiniPlayerAction);
                            } else {
                                Intent MiniPlayerAction = new Intent();
                                MiniPlayerAction.setAction("PauseMP");
                                sendBroadcast(MiniPlayerAction);
                            }
                        }
                    });

                    MusicPlayerMini.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // v.setTransitionName("image_transition");
                            Intent MiniPlayerAction = new Intent();
                            MiniPlayerAction.setAction("PlayMP");
                            sendBroadcast(MiniPlayerAction);
                            Intent MusicPlayer = new Intent(getApplicationContext(), MusicPlayer.class);
                            MusicPlayer.putExtra("MiniPlayerStart", true);
                            MusicPlayer.putExtra("SongID", SongID);
                            MusicPlayer.putExtra("SongPos", SongPos);
                            MusicPlayer.putExtra("Paused", Paused);
                            // ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AllPlaylistInArea.this);
                            // ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getApplicationContext(), v, v.getTransitionName());
                            overridePendingTransition(R.transition.image_transition, R.anim.nav_default_enter_anim);
                            startActivity(MusicPlayer);
                        }
                    });
                }
            } else {
                if (PlaylistSongID.get(18).get(i).equals(SongID)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Bitmap bitmap = null;
                        try {

                            bitmap = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(PlaylistSongID.get(4).get(i)), new Size(100, 100), null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (bitmap != null) {
                            //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            // bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                            MiniPlayerImage.setImageBitmap(bitmap);
                        } else {
                            Drawable drawable;
                            if (AppAppearance.equals("Dark")) {
                                drawable = getResources().getDrawable(R.drawable.placeholderdark);
                            } else {
                                drawable = getResources().getDrawable(R.drawable.placeholder);
                            }
                            MiniPlayerImage.setImageDrawable(drawable);
                        }
                    } else {
                        if (AppAppearance.equals("Dark")) {
                            Glide.with(this).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholderdark).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(MiniPlayerImage);
                        } else {
                            Glide.with(this).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(MiniPlayerImage);
                        }
                    }

                    MiniPlayerSongName.setText(PlaylistSongID.get(0).get(i));
                    MiniPlayerSongArtist.setText(PlaylistSongID.get(1).get(i));

                    if (Paused) {
                        MPPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    } else {
                        MPPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
                    }

                    MPPlayPause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Paused) {
                                Intent MiniPlayerAction = new Intent();
                                MiniPlayerAction.setAction("PlayMP");
                                sendBroadcast(MiniPlayerAction);
                            } else {
                                Intent MiniPlayerAction = new Intent();
                                MiniPlayerAction.setAction("PauseMP");
                                sendBroadcast(MiniPlayerAction);
                            }
                        }
                    });

                    MusicPlayerMini.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // v.setTransitionName("image_transition");
                            Intent MiniPlayerAction = new Intent();
                            MiniPlayerAction.setAction("PlayMP");
                            sendBroadcast(MiniPlayerAction);
                            Intent MusicPlayer = new Intent(getApplicationContext(), MusicPlayer.class);
                            MusicPlayer.putExtra("MiniPlayerStart", true);
                            MusicPlayer.putExtra("SongID", SongID);
                            MusicPlayer.putExtra("SongPos", SongPos);
                            MusicPlayer.putExtra("Paused", Paused);
                            // ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AllPlaylistInArea.this);
                            // ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getApplicationContext(), v, v.getTransitionName());
                            overridePendingTransition(R.transition.image_transition, R.anim.nav_default_enter_anim);
                            startActivity(MusicPlayer);
                        }
                    });

                }
            }
        }
    }

    public void AccessSettings() {
        try {
            JSONObject InfoFile = new JSONObject(loadJsonFile("Settings" + ".json"));
            JSONArray AppMode = InfoFile.getJSONArray("AppearanceMode");
            AppAppearance = AppMode.get(0).toString();
        } catch (JSONException e) {
            int hi = 0;
        }
    }

    /*
    public void FloatingMenuSetup() {
       // GenButtonsMenus();
        FloatingActionMenus();
        PlayMenu.hideMenu(true);
        AddMenu.hideMenu(true);
        PlayMenuAdvanced.hideMenu(true);
    }

     */

    public void GetInfo() {
        Intent Stuff = getIntent();

        PlaylistFileName = Stuff.getStringExtra("PlaylistName");
        FileID = Stuff.getStringExtra("FileID");
        Playlist = Stuff.getBooleanExtra("Playlist", false);

        if (FileID.contains("AlID")) {
            //Album
            ObjectMode = "Album";
        } else if (FileID.contains("AID")) {
            //Artist
            ObjectMode = "Artist";
        } else if (FileID.contains("SID")) {
            //Song
            ObjectMode = "Song";
        }

        switch (ObjectMethod) {
            case "Song":
                if (Playlist) {
                    for (int i = 0; i < VariableNumSong; i++) {
                        ObjectChosen.add(Stuff.getStringExtra("Value" + i));
                    }
                } else {
                    for (int i = 0; i < MasterVariableNumSong; i++) {
                        ObjectChosen.add(Stuff.getStringExtra("Value" + i));
                    }
                }
                break;
            case "Artist":
                if (Playlist) {
                    for (int i = 0; i < VariableNumArtist; i++) {
                        ObjectChosen.add(Stuff.getStringExtra("Value" + i));
                    }
                } else {
                    for (int i = 0; i < MasterVariableNumArtist; i++) {
                        ObjectChosen.add(Stuff.getStringExtra("Value" + i));
                    }
                }
                break;
            case "Album":
                if (Playlist) {
                    for (int i = 0; i < VariableNumAlbum; i++) {
                        ObjectChosen.add(Stuff.getStringExtra("Value" + i));
                    }
                } else {
                    for (int i = 0; i < MasterVariableNumAlbum; i++) {
                        ObjectChosen.add(Stuff.getStringExtra("Value" + i));
                    }
                }
                break;
        }
        //Master File Position

        //I guess we can just send through all the Info or even just all the important info

    }

    public void DetectOwnedObjects() {
        OwnedObjects.clear();
        SkinAlbumID.clear();
        SkinSongID.clear();
        SkinArtistID.clear();

        //Detect FilePos
        if (FileID.contains("AlID")) {
            //Album
            for (int i = 0; i < AlbumNum; i++) {
                if (Playlist) {
                    if (FileID.equals(PlaylistAlbumID.get(16).get(i))) {
                        FilePos = i;
                    }
                } else {
                    if (FileID.equals(PlaylistAlbumID.get(13).get(i))) {
                        FilePos = i;
                    }
                }
            }
        } else if (FileID.contains("AID")) {
            //Artist
            for (int i = 0; i < ArtistNum; i++) {
                if (Playlist) {
                    if (FileID.equals(PlaylistArtistID.get(14).get(i))) {
                        FilePos = i;
                    }
                } else {
                    if (FileID.equals(PlaylistArtistID.get(11).get(i))) {
                        FilePos = i;
                    }
                }
            }
        } else if (FileID.contains("SID")) {
            //Song
            for (int i = 0; i < SongNum; i++) {
                if (Playlist) {
                    if (FileID.equals(PlaylistSongID.get(18).get(i))) {
                        FilePos = i;
                    }
                } else {
                    if (FileID.equals(PlaylistSongID.get(15).get(i))) {
                        FilePos = i;
                    }
                }
            }
        }

        switch (ObjectMode) {
            case "Song":
                for (int i = 0; i < PlaylistSongID.get(0).size(); i++) {
                    if (Playlist) {
                        if (FileID.equals(PlaylistSongID.get(18).get(i))) {
                            //Skins
                            OwnedObjects.add(new ArrayList<Integer>());
                            for (int e = 0; e < SkinSongNum; e++) {
                                if (SkinSongID.get(6).get(e).contains(Code + ObjectID + ",") || SkinSongID.get(6).get(e).contains(Code + ObjectID + "]")) {
                                    OwnedObjects.get(0).add(e);
                                }
                            }
                        }
                    } else {
                        if (FileID.equals(PlaylistSongID.get(15).get(i))) {
                            //Skins
                            OwnedObjects.add(new ArrayList<Integer>());
                            for (int e = 0; e < SkinSongNum; e++) {
                                if (SkinSongID.get(6).get(e).contains(Code + ObjectID + ",") || SkinSongID.get(6).get(e).contains(Code + ObjectID + "]")) {
                                    OwnedObjects.get(0).add(e);
                                }
                            }
                        }
                    }

                }
                break;
            case "Artist":

                for (int e = 0; e < 3; e++) {
                    OwnedObjects.add(new ArrayList<Integer>());
                }
                //Skins
                for (int e = 0; e < SkinSongNum; e++) {
                    if (SkinArtistID.get(3).get(e).contains(Code + FileID + ",") || SkinArtistID.get(3).get(e).contains(Code + FileID + "]")) {
                        OwnedObjects.get(0).add(e);
                    }
                }

                if (Playlist) {
                    //Playlist
                    String ArtistID;
                    for (int i = 0; i < ArtistNum; i++) {
                        if (FileID.equals(PlaylistArtistID.get(14).get(i))) {
                            ArtistID = PlaylistArtistID.get(12).get(i);

                            //Songs
                            for (int e = 0; e < SongNum; e++) {
                                if (PlaylistSongID.get(16).get(e).equals(ArtistID)) {
                                    OwnedObjects.get(1).add(e);
                                }
                            }
                            //Albums
                            for (int e = 0; e < AlbumNum; e++) {
                                if (PlaylistAlbumID.get(14).get(e).equals(ArtistID)) {
                                    OwnedObjects.get(2).add(e);
                                }
                            }
                        }
                    }

                } else {
                    //Master
                    String ArtistID;
                    for (int i = 0; i < ArtistNum; i++) {
                        if (FileID.equals(PlaylistArtistID.get(11).get(i))) {
                            ArtistID = PlaylistArtistID.get(9).get(i);

                            //Songs
                            for (int e = 0; e < SongNum; e++) {
                                if (PlaylistSongID.get(13).get(e).equals(ArtistID)) {
                                    OwnedObjects.get(1).add(e);
                                }
                            }
                            //Albums
                            for (int e = 0; e < AlbumNum; e++) {
                                if (PlaylistAlbumID.get(11).get(e).equals(ArtistID)) {
                                    OwnedObjects.get(2).add(e);
                                }
                            }
                        }
                    }
                }
                break;
            case "Album":

                for (int e = 0; e < 2; e++) {
                    OwnedObjects.add(new ArrayList<Integer>());
                }
                //Skins
                for (int e = 0; e < SkinSongNum; e++) {
                    if (SkinArtistID.get(3).get(e).contains(Code + ObjectID + ",") || SkinArtistID.get(3).get(e).contains(Code + ObjectID + "]")) {
                        OwnedObjects.get(0).add(e);
                    }
                }

                if (Playlist) {
                    //Playlist
                    String AlbumID;
                    for (int i = 0; i < AlbumNum; i++) {
                        if (FileID.equals(PlaylistAlbumID.get(16).get(i))) {
                            AlbumID = PlaylistAlbumID.get(13).get(i);
                            //Songs
                            for (int e = 0; e < SongNum; e++) {
                                if (PlaylistSongID.get(15).get(e).equals(AlbumID)) {
                                    OwnedObjects.get(1).add(e);
                                }
                            }
                        }
                    }
                } else {
                    //Master
                    String AlbumID;
                    for (int i = 0; i < AlbumNum; i++) {
                        if (FileID.equals(PlaylistAlbumID.get(13).get(i))) {
                            AlbumID = PlaylistAlbumID.get(10).get(i);
                            //Songs
                            for (int e = 0; e < SongNum; e++) {
                                if (PlaylistSongID.get(12).get(e).equals(AlbumID)) {
                                    OwnedObjects.get(1).add(e);
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    public String loadJsonFile(String Filename) {
        String json = null;
        try {
            InputStream is = openFileInput(Filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void ReadPlaylistInfoFile() {
        PlaylistSongID.clear();
        PlaylistAlbumID.clear();
        PlaylistArtistID.clear();
        AllMoods.clear();
        AllTags.clear();

        if (Playlist) {
            try {
                JSONObject InfoFile = new JSONObject(loadJsonFile(PlaylistFileName + ".json"));
                JSONArray TotalSongNum = InfoFile.getJSONArray("TotalSongNum");
                JSONArray TotalArtistNum = InfoFile.getJSONArray("TotalArtistNum");
                JSONArray TotalAlbumNum = InfoFile.getJSONArray("TotalAlbumNum");
                JSONArray DefaultObjectSize = InfoFile.getJSONArray("ObjectDisplaySize");
                JSONArray DefaultSortMethod = InfoFile.getJSONArray("SortMethod");
                JSONArray DefaultDisplayMethod = InfoFile.getJSONArray("ObjectDisplayMethod");
                JSONArray AllTagsArray = InfoFile.getJSONArray("AllTags");
                JSONArray AllMoodsArray = InfoFile.getJSONArray("AllMoods");

                SongNum = (int) TotalSongNum.get(0);
                ArtistNum = (int) TotalArtistNum.get(0);
                AlbumNum = (int) TotalAlbumNum.get(0);
                ObjectType = DefaultObjectSize.get(0).toString();
                SortMethod = DefaultSortMethod.get(0).toString();
                ObjectMethod = DefaultDisplayMethod.get(0).toString();


                for (int i = 0; i < AllTagsArray.length(); i++) {
                    AllTags.add(AllTagsArray.get(i).toString());
                }

                for (int i = 0; i < AllMoodsArray.length(); i++) {
                    AllMoods.add(AllMoodsArray.get(i).toString());
                }

                //
                //Initialize Arrays
                //

                InitializeArrays();

                //
                //Load Arrays
                //

                //Load All Song Info into 2D Array
                for (int i = 0; i < SongNum; i++) {
                    JSONArray SID = InfoFile.getJSONArray("SID" + i);
                    for (int e = 0; e < VariableNumSong; e++) {
                        PlaylistSongID.get(e).add(SID.get(e).toString());
                    }
                }

                //Load All Artist Info into 2D Array
                for (int i = 0; i < ArtistNum; i++) {
                    JSONArray AID = InfoFile.getJSONArray("AID" + i);
                    for (int e = 0; e < VariableNumArtist; e++) {
                        PlaylistArtistID.get(e).add(AID.get(e).toString());
                    }
                }

                //Load All Album Info into 2D Array
                for (int i = 0; i < AlbumNum; i++) {
                    JSONArray AlID = InfoFile.getJSONArray("AlID" + i);
                    for (int e = 0; e < VariableNumAlbum; e++) {
                        PlaylistAlbumID.get(e).add(AlID.get(e).toString());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            try {
                JSONObject MasterFile = new JSONObject(loadJsonFile("MasterSongDataFile.json"));
                JSONArray TotalSongNum = MasterFile.getJSONArray("TotalSongNum");
                JSONArray TotalArtistNum = MasterFile.getJSONArray("TotalArtistNum");
                JSONArray TotalAlbumNum = MasterFile.getJSONArray("TotalAlbumNum");
                JSONArray AllTagsArray = MasterFile.getJSONArray("AllTags");
                JSONArray AllMoodsArray = MasterFile.getJSONArray("AllMoods");

                SongNum = (int) TotalSongNum.get(0);
                ArtistNum = (int) TotalArtistNum.get(0);
                AlbumNum = (int) TotalAlbumNum.get(0);


                for (int i = 0; i < AllTagsArray.length(); i++) {
                    AllTags.add(AllTagsArray.get(i).toString());
                }

                for (int i = 0; i < AllMoodsArray.length(); i++) {
                    AllMoods.add(AllMoodsArray.get(i).toString());
                }

                //
                //Initialize Arrays
                //
                InitializeArrays();

                //
                //Load Arrays
                //
                //Load All Song Info into 2D Array
                for (int i = 0; i < SongNum; i++) {
                    JSONArray SID = MasterFile.getJSONArray("SID" + i);
                    for (int e = 0; e < MasterVariableNumSong; e++) {
                        PlaylistSongID.get(e).add(SID.get(e).toString());
                    }
                }

                //Load All Artist Info into 2D Array
                for (int i = 0; i < ArtistNum; i++) {
                    JSONArray AID = MasterFile.getJSONArray("AID" + i);
                    for (int e = 0; e < MasterVariableNumArtist; e++) {
                        PlaylistArtistID.get(e).add(AID.get(e).toString());
                    }
                }

                //Load All Album Info into 2D Array
                for (int i = 0; i < AlbumNum; i++) {
                    JSONArray AlID = MasterFile.getJSONArray("AlID" + i);
                    for (int e = 0; e < MasterVariableNumAlbum; e++) {
                        PlaylistAlbumID.get(e).add(AlID.get(e).toString());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void InitializeArrays() {
        //
        //Switch the numbers to the VariableNum Int value and maybe in the future have it detect the number by itself
        //

        if (Playlist) {
            //Initialize Array SongID
            for (int i = 0; i < 19; i++) {
                PlaylistSongID.add(new ArrayList<String>());
            }

            //Initialize Array AlbumID
            for (int i = 0; i < 18; i++) {
                PlaylistAlbumID.add(new ArrayList<String>());
            }

            //Initialize Array ArtistID
            for (int i = 0; i < 16; i++) {
                PlaylistArtistID.add(new ArrayList<String>());
            }
        } else {

            //Initialize Array PlaylistSongID
            for (int i = 0; i < 16; i++) {
                PlaylistSongID.add(new ArrayList<String>());
            }

            //Initialize Array PlaylistArtistID
            for (int i = 0; i < 13; i++) {
                PlaylistArtistID.add(new ArrayList<String>());
            }

            //Initialize Array PlaylistAlbumID
            for (int i = 0; i < 15; i++) {
                PlaylistAlbumID.add(new ArrayList<String>());
            }
        }
    }

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

    public String ClockDisplay(int Time) {

        String TimeDisplay = "";

        LengthCalc(Time);

        if (Hour > 0) {
            if (Min <= 9) {
                if (Sec <= 9) {
                    TimeDisplay = Hour + ":" + "0" + Min + ":" + "0" + Sec;
                } else {
                    TimeDisplay = Hour + ":" + "0" + Min + ":" + Sec;
                }
            } else {
                if (Sec <= 9) {
                    TimeDisplay = Hour + ":" + Min + ":" + "0" + Sec;
                } else {
                    TimeDisplay = Hour + ":" + Min + ":" + Sec;
                }
            }
        } else {
            if (Min > 0) {
                if (Sec <= 9) {
                    TimeDisplay = Min + ":" + "0" + Sec;
                } else {
                    TimeDisplay = Min + ":" + Sec;
                }
            } else {

                if (Sec <= 9) {
                    TimeDisplay = "0" + ":" + "0" + Sec;
                } else {
                    TimeDisplay = "0" + ":" + Sec;
                }
            }
        }
        return TimeDisplay;

    }

    public void GenSongLengthChecker() {

        SongLengthChecker.clear();

        //
        //Hmm this would only work if it was sorted alphabetically (Artist and Album) which means we would have to store SIDx inside the arrays, or wait does it need it? eh either way probably safe to add
        //

        switch (SelectMethod) {

            case "Album":
                for (int i = 0; i < AlbumNum; i++) {
                    if (SearchTerm == null || SearchTerm.equals("")) {
                        SongLengthChecker.add(i);
                    } else {
                        if (PlaylistAlbumID.get(0).get(i).toLowerCase().contains(SearchTerm.toLowerCase()) || PlaylistAlbumID.get(1).get(i).toLowerCase().contains(SearchTerm.toLowerCase())) {
                            SongLengthChecker.add(i);
                        }
                    }
                }
                break;
            case "Artist":
                for (int i = 0; i < ArtistNum; i++) {
                    if (SearchTerm == null || SearchTerm.equals("")) {
                        SongLengthChecker.add(i);
                    } else {
                        if (PlaylistArtistID.get(0).get(i).toLowerCase().contains(SearchTerm.toLowerCase())) {
                            SongLengthChecker.add(i);
                        }
                    }
                }
                break;
            case "Song":
                for (int i = 0; i < SongNum; i++) {
                    if (SearchTerm == null || SearchTerm.equals("")) {
                        SongLengthChecker.add(i);

                    } else {
                        if (PlaylistSongID.get(0).get(i).toLowerCase().contains(SearchTerm.toLowerCase()) || PlaylistSongID.get(1).get(i).toLowerCase().contains(SearchTerm.toLowerCase())) {
                            SongLengthChecker.add(i);
                        }
                    }
                }
                break;
            case "Image":
                for (int i = 0; i < AlbumNum; i++) {
                    if (SearchTerm == null || SearchTerm.equals("")) {
                        SongLengthChecker.add(i);
                    } else {
                        if (PlaylistAlbumID.get(0).get(i).toLowerCase().contains(SearchTerm.toLowerCase()) || PlaylistAlbumID.get(1).get(i).toLowerCase().contains(SearchTerm.toLowerCase())) {
                            SongLengthChecker.add(i);
                        }
                    }
                }
                break;
        }

    }

    public void SortEverything() {

        switch (SelectMethod) {
            case "Album":
                SortArray(0, "Album");
                break;
            case "Artist":
                SortArray(0, "Artist");
                break;
            case "Song":
                SortArray(0, "Song");
        }

    }

    public void SortArray(int position, String Object) {
        ArrayList<ArrayList<String>> Copy = new ArrayList<>();
        ArrayList<Integer> MiscCopy = new ArrayList<>();
        ArrayList<Integer> oldPlacement = new ArrayList<>();
        ArrayList<String> Combined = new ArrayList<>();
        int Loop;
        int ArrayNum;
        switch (Object) {
            case "Album":
                Combined.clear();
                MiscCopy.clear();
                Copy.clear();
                oldPlacement.clear();
                for (int i = 0; i < PlaylistAlbumID.size(); i++) {
                    Copy.add(new ArrayList<String>());
                    for (int e = 0; e < PlaylistAlbumID.get(0).size(); e++) {
                        Copy.get(i).add(PlaylistAlbumID.get(i).get(e));
                    }
                }

                for (int i = 0; i < PlaylistAlbumID.get(position).size(); i++) {
                    Combined.add(PlaylistAlbumID.get(position).get(i) + "  " + PlaylistAlbumID.get(13).get(i));
                }

                Collections.sort(Combined);
                Loop = PlaylistAlbumID.get(position).size();

                //Determine Old Placements
                for (int i = 0; i < Loop; i++) {
                    for (int e = 0; e < Loop; e++) {
                        if (Combined.get(i).contains(Copy.get(position).get(e)) && Combined.get(i).contains(Copy.get(13).get(e)) && (!oldPlacement.contains(e))) {
                            oldPlacement.add(e);
                        }
                    }
                }

                ArrayNum = PlaylistAlbumID.size();
                PlaylistAlbumID.clear();

                for (int i = 0; i < ArrayNum; i++) {
                    PlaylistAlbumID.add(new ArrayList<String>());
                }

                //Load info into Array
                for (int i = 0; i < ArrayNum; i++) {
                    for (int e = 0; e < Loop; e++) {
                        PlaylistAlbumID.get(i).add(Copy.get(i).get(oldPlacement.get(e)));
                    }
                }

                break;
            case "Artist":
                Combined.clear();
                MiscCopy.clear();
                Copy.clear();
                oldPlacement.clear();
                for (int i = 0; i < PlaylistArtistID.size(); i++) {
                    Copy.add(new ArrayList<String>());
                    for (int e = 0; e < PlaylistArtistID.get(0).size(); e++) {
                        Copy.get(i).add(PlaylistArtistID.get(i).get(e));
                    }
                }

                for (int i = 0; i < PlaylistArtistID.get(position).size(); i++) {
                    Combined.add(PlaylistArtistID.get(position).get(i) + "  " + PlaylistArtistID.get(12).get(i));
                }

                Collections.sort(Combined);
                Loop = PlaylistArtistID.get(position).size();

                //Determine Old Placements
                for (int i = 0; i < Loop; i++) {
                    for (int e = 0; e < Loop; e++) {
                        if (Combined.get(i).contains(Copy.get(position).get(e)) && Combined.get(i).contains(Copy.get(12).get(e)) && (!oldPlacement.contains(e))) {
                            //Fix this
                            oldPlacement.add(e);
                        }
                    }
                }

                ArrayNum = PlaylistArtistID.size();
                PlaylistArtistID.clear();

                for (int i = 0; i < ArrayNum; i++) {
                    PlaylistArtistID.add(new ArrayList<String>());
                }

                //Load info into Array
                for (int i = 0; i < ArrayNum; i++) {
                    for (int e = 0; e < Loop; e++) {
                        PlaylistArtistID.get(i).add(Copy.get(i).get(oldPlacement.get(e)));
                    }
                }

                break;
            case "Song":
                Combined.clear();
                MiscCopy.clear();
                //Gotta convert Length Arraylist to ints to correctly sort them
                Copy.clear();
                oldPlacement.clear();
                for (int i = 0; i < PlaylistSongID.size(); i++) {
                    Copy.add(new ArrayList<String>());
                    for (int e = 0; e < PlaylistSongID.get(0).size(); e++) {
                        Copy.get(i).add(PlaylistSongID.get(i).get(e));

                    }
                }

                for (int i = 0; i < PlaylistSongID.get(position).size(); i++) {
                    Combined.add(PlaylistSongID.get(position).get(i) + "  " + PlaylistSongID.get(15).get(i));
                }

                Collections.sort(Combined);
                Loop = PlaylistSongID.get(position).size();

                //Determine Old Placements
                for (int i = 0; i < Loop; i++) {
                    for (int e = 0; e < Loop; e++) {
                        if ((Combined.get(i).contains(Copy.get(position).get(e)) && Combined.get(i).contains(Copy.get(15).get(e))) && (!oldPlacement.contains(e))) {
                            oldPlacement.add(e);
                        }
                    }
                }

                ArrayNum = PlaylistSongID.size();
                PlaylistSongID.clear();

                for (int i = 0; i < ArrayNum; i++) {
                    PlaylistSongID.add(new ArrayList<String>());
                }

                //Load info into Array
                for (int i = 0; i < ArrayNum; i++) {
                    for (int e = 0; e < Loop; e++) {
                        PlaylistSongID.get(i).add(Copy.get(i).get(oldPlacement.get(e)));
                    }
                }
                //
                //
                //Gotta figure out how to do the same thing to the songs as the Album
                //
                //
                //
                //
                //Oh shit this entire system would break and not work if everything wasn't sorted alphabetically ID system only works on Albums because it's already sorted alphabetically
                //Gotta find a new way to do this
                //Idea 1: Make a new ArrayList and Combine whatever we are comparing with the AlbumID and it checks if it contains both
                //Actually i think this may work, have it check for the name &&
                //So far it's working***
                //Implement ArtistID to the system and do the same checking system with these sorters but with ArtistID for the Artist section to fix it, I guess while your at it add the Image Path for them
                //Also look at and try to implement Async tasks
                //
                //
                break;

        }
    }

    public void GetDisplayInfoHeader(View view) {

        //Later on add a tags and mood horizontal scroll and maybe other stuff

        //So the Album Playlist Rank doesnt want to work, guess we gotta check that out tomorrow (Jan 24 2021) 

        //Common Items
        PlaylistContain = view.findViewById(R.id.PlaylistContain);
        InfoDisplayImage = view.findViewById(R.id.InfoDisplayImage);
        InfoDisplayTotalTime = view.findViewById(R.id.InfoDisplayTotalTime);
        InfoDisplayTimesPlayed = view.findViewById(R.id.InfoDisplayTimesPlayed);              //It'll use the stats that it's currently on, whether Playlist or Universal

        if (AppAppearance.equals("Dark")) {
            InfoDisplayTotalTime.setTextColor(Color.rgb(255, 255, 255));
            InfoDisplayTimesPlayed.setTextColor(Color.rgb(255, 255, 255));
        }

        switch (ObjectMode) {
            case "Album":
                InfoDisplayArtistUniversalRank = view.findViewById(R.id.InfoDisplayArtistUniversalRank);
                InfoDisplayArtistPlaylistRank = view.findViewById(R.id.InfoDisplayArtistPlaylistRank);
                InfoDisplayAlbumUniversalRank = view.findViewById(R.id.InfoDisplayAlbumUniversalRank);
                InfoDisplayAlbumPlaylistRank = view.findViewById(R.id.InfoDisplayAlbumPlaylistRank);
                InfoDisplayArtistName = view.findViewById(R.id.InfoDisplayArtistName);
                InfoDisplayAlbumName = view.findViewById(R.id.InfoDisplayAlbumName);

                if (AppAppearance.equals("Dark")) {
                    InfoDisplayArtistUniversalRank.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayArtistPlaylistRank.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayAlbumUniversalRank.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayAlbumPlaylistRank.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayArtistName.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayAlbumName.setTextColor(Color.rgb(255, 255, 255));
                }
                break;

            case "Artist":
                InfoDisplayArtistUniversalRank = view.findViewById(R.id.InfoDisplayArtistUniversalRank);
                InfoDisplayArtistPlaylistRank = view.findViewById(R.id.InfoDisplayArtistPlaylistRank);
                InfoDisplayArtistName = view.findViewById(R.id.InfoDisplayArtistName);
                if (AppAppearance.equals("Dark")) {
                    InfoDisplayArtistUniversalRank.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayArtistPlaylistRank.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayArtistName.setTextColor(Color.rgb(255, 255, 255));
                }
                break;

            case "Song":
                InfoDisplaySongUniversalRank = view.findViewById(R.id.InfoDisplaySongUniversalRank);
                InfoDisplaySongPlaylistRank = view.findViewById(R.id.InfoDisplaySongPlaylistRank);
                InfoDisplayArtistUniversalRank = view.findViewById(R.id.InfoDisplayArtistUniversalRank);
                InfoDisplayArtistPlaylistRank = view.findViewById(R.id.InfoDisplayArtistPlaylistRank);
                InfoDisplayAlbumUniversalRank = view.findViewById(R.id.InfoDisplayAlbumUniversalRank);
                InfoDisplayAlbumPlaylistRank = view.findViewById(R.id.InfoDisplayAlbumPlaylistRank);
                InfoDisplaySongName = view.findViewById(R.id.InfoDisplaySongName);
                InfoDisplayArtistName = view.findViewById(R.id.InfoDisplayArtistName);
                InfoDisplayAlbumName = view.findViewById(R.id.InfoDisplayAlbumName);
                //InfoDisplayFavStart = view.findViewById(R.id.InfoDisplayFavStart);
                //InfoDisplayFavEnd = view.findViewById(R.id.InfoDisplayFavEnd);
                if (AppAppearance.equals("Dark")) {
                    InfoDisplaySongUniversalRank.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplaySongPlaylistRank.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayArtistUniversalRank.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayArtistPlaylistRank.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayAlbumUniversalRank.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayAlbumPlaylistRank.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplaySongName.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayArtistName.setTextColor(Color.rgb(255, 255, 255));
                    InfoDisplayAlbumName.setTextColor(Color.rgb(255, 255, 255));
                    //InfoDisplayFavStart.setTextColor(Color.rgb(255, 255, 255));
                    //InfoDisplayFavEnd.setTextColor(Color.rgb(255, 255, 255));
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetDisplayInfoHeader() {

        ContainedPlaylists.clear();
        ContainedPlaylists.add(new ArrayList<String>());
        ContainedPlaylists.add(new ArrayList<String>());
        ContainedPlaylists.get(0).add("Master File");
        ContainedPlaylists.get(1).add("MasterSongDataFile");
        DetectPlaylist();

        for (int i = 0; i < ContainedPlaylists.get(0).size(); i++) {
            View Button;
            if (ContainedPlaylists.get(1).get(i).equals(PlaylistFileName)) {
                Button = getLayoutInflater().inflate(R.layout.horizontal_view_object_selected, null);
                if (AppAppearance.equals("Dark")) {
                    LinearLayout Background = Button.findViewById(R.id.TMAHorizonViewObject);
                    Background.setBackground(ContextCompat.getDrawable(this, R.drawable.object_selectdark));
                }
            } else {
                Button = getLayoutInflater().inflate(R.layout.horizontal_view_object, null);
                if (AppAppearance.equals("Dark")) {
                    LinearLayout Background = Button.findViewById(R.id.TMAHorizonViewObject);
                    Background.setBackground(ContextCompat.getDrawable(this, R.drawable.objectdark));
                }
            }
            TextView SortType = Button.findViewById(R.id.TMAObject);
            SortType.setText(ContainedPlaylists.get(0).get(i));
            if (AppAppearance.equals("Dark")) {
                SortType.setTextColor(Color.rgb(255, 255, 255));
            }
            final int finalI = i;
            Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContainedPlaylists.get(0).get(finalI).equals("Master File")) {
                        Playlist = false;
                    } else {
                        Playlist = true;
                    }
                    PlaylistFileName = ContainedPlaylists.get(1).get(finalI);
                    ReadPlaylistInfoFile();
                    DetectOwnedObjects();
                    InfoList.setAdapter(ExpandableAdapterAll);
                    EditList.setAdapter(InfoDisplayEditAdapter);
                    ContainedPlaylists.clear();
                }
            });
            PlaylistContain.addView(Button);
        }

        switch (ObjectMode) {
            case "Album":
                for (int i = 0; i < AlbumNum; i++) {

                    if (Playlist) {
                        if (FileID.equals(PlaylistAlbumID.get(16).get(i))) {
                            if (PlaylistAlbumID.get(2).get(i).equals(null)) {
                                if (AppAppearance.equals("Dark")) {
                                    Glide.with(this).load(R.drawable.placeholderdark).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                                } else {
                                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                                }
                            } else {
                                GlideDisplay(i, InfoDisplayImage, "Album");
                                //Glide.with(getApplicationContext()).load(PlaylistAlbumID.get(2).get(i)).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                            }

                            InfoDisplayAlbumName.setText(PlaylistAlbumID.get(0).get(i));
                            InfoDisplayAlbumPlaylistRank.setText(PlaylistAlbumID.get(3).get(i));
                            InfoDisplayAlbumUniversalRank.setText(PlaylistAlbumID.get(4).get(i));

                            for (int e = 0; e < PlaylistArtistID.get(0).size(); e++) {
                                if (PlaylistAlbumID.get(14).get(i).equals(PlaylistArtistID.get(12).get(e))) {
                                    InfoDisplayArtistName.setText(PlaylistArtistID.get(0).get(e));
                                    InfoDisplayArtistPlaylistRank.setText(PlaylistArtistID.get(2).get(e));
                                    InfoDisplayArtistUniversalRank.setText(PlaylistArtistID.get(3).get(e));
                                }
                            }

                            InfoDisplayTotalTime.setText(ClockDisplay(Integer.parseInt(PlaylistAlbumID.get(11).get(i))));
                            InfoDisplayTimesPlayed.setText(PlaylistAlbumID.get(10).get(i));
                        }
                    } else {
                        //
                        //Master File
                        //
                        if (FileID.equals(PlaylistAlbumID.get(13).get(i))) {
                            if (PlaylistAlbumID.get(2).get(i).equals(null)) {
                                if (AppAppearance.equals("Dark")) {
                                    Glide.with(this).load(R.drawable.placeholderdark).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                                } else {
                                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                                }
                            } else {
                                GlideDisplay(i, InfoDisplayImage, "Album");
                                //Glide.with(getApplicationContext()).load(PlaylistAlbumID.get(2).get(i)).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                            }

                            InfoDisplayAlbumName.setText(PlaylistAlbumID.get(0).get(i));
                            InfoDisplayAlbumUniversalRank.setText(PlaylistAlbumID.get(3).get(i));
                            InfoDisplayAlbumPlaylistRank.setText("");

                            for (int e = 0; e < PlaylistArtistID.get(0).size(); e++) {
                                if (PlaylistAlbumID.get(11).get(i).equals(PlaylistArtistID.get(9).get(e))) {
                                    InfoDisplayArtistName.setText(PlaylistArtistID.get(0).get(e));
                                    InfoDisplayArtistUniversalRank.setText(PlaylistArtistID.get(2).get(e));
                                    InfoDisplayArtistPlaylistRank.setText("");
                                }
                            }

                            InfoDisplayTotalTime.setText(ClockDisplay(Integer.parseInt(PlaylistAlbumID.get(8).get(i))));
                            InfoDisplayTimesPlayed.setText(PlaylistAlbumID.get(7).get(i));
                        }
                    }
                }
                break;
            case "Artist":
                for (int i = 0; i < ArtistNum; i++) {

                    if (Playlist) {
                        if (FileID.equals(PlaylistArtistID.get(14).get(i))) {
                            if (PlaylistArtistID.get(1).get(i).equals(null)) {
                                if (AppAppearance.equals("Dark")) {
                                    Glide.with(this).load(R.drawable.placeholderdark).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                                } else {
                                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                                }
                            } else {
                                GlideDisplay(i, InfoDisplayImage, "Artist");
                                //Glide.with(getApplicationContext()).load(PlaylistArtistID.get(1).get(i)).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                            }

                            InfoDisplayTimesPlayed.setText(PlaylistArtistID.get(9).get(i));
                            InfoDisplayTotalTime.setText(ClockDisplay(Integer.parseInt(PlaylistArtistID.get(10).get(i))));     //Gonna have to add this variable to all of them sometime soon               Maybe replace the Universal times played for this and add a spot in the master file


                            InfoDisplayArtistUniversalRank.setText(PlaylistArtistID.get(3).get(i));
                            InfoDisplayArtistPlaylistRank.setText(PlaylistArtistID.get(2).get(i));
                            InfoDisplayArtistName.setText(PlaylistArtistID.get(0).get(i));
                        }
                    } else {
                        //
                        //Master File
                        //
                        if (FileID.equals(PlaylistArtistID.get(11).get(i))) {
                            if (PlaylistArtistID.get(1).get(i).equals(null)) {
                                if (AppAppearance.equals("Dark")) {
                                    Glide.with(this).load(R.drawable.placeholderdark).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                                } else {
                                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                                }
                            } else {
                                GlideDisplay(i, InfoDisplayImage, "Artist");
                                //Glide.with(getApplicationContext()).load(PlaylistArtistID.get(1).get(i)).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                            }

                            InfoDisplayTimesPlayed.setText(PlaylistArtistID.get(6).get(i));
                            InfoDisplayTotalTime.setText(ClockDisplay(Integer.parseInt(PlaylistArtistID.get(7).get(i))));    //Already done for Artist and Album  (Master File)

                            InfoDisplayArtistUniversalRank.setText(PlaylistArtistID.get(2).get(i));
                            InfoDisplayArtistPlaylistRank.setText("");
                            InfoDisplayArtistName.setText(PlaylistArtistID.get(0).get(i));
                        }
                    }
                }
                break;
            case "Song":

                for (int i = 0; i < SongNum; i++) {
                    //Replace SongLengthChecker with i

                    if (Playlist) {
                        if (FileID.equals(PlaylistSongID.get(18).get(i))) {
                            if (PlaylistSongID.get(4).get(i).equals(null)) {
                                if (AppAppearance.equals("Dark")) {
                                    Glide.with(this).load(R.drawable.placeholderdark).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                                } else {
                                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                                }
                            } else {
                                GlideDisplay(i, InfoDisplayImage, "Song");
                                //Glide.with(getApplicationContext()).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                            }

                            InfoDisplaySongName.setText(PlaylistSongID.get(0).get(i));
                            InfoDisplaySongUniversalRank.setText(PlaylistSongID.get(6).get(i));
                            InfoDisplaySongPlaylistRank.setText(PlaylistSongID.get(5).get(i));

                            InfoDisplayTotalTime.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(13).get(i))));
                            InfoDisplayTimesPlayed.setText(PlaylistSongID.get(12).get(i));


                            // InfoDisplayFavStart.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(18).get(i))));
                            //InfoDisplayFavEnd.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(19).get(i))));

                            //Artist Stuff    (if this doesn't work just copy the SetObjectID mechanism if this does work though, possibly replace everything with this, this could save RAM and lots of time)
                            for (int e = 0; e < PlaylistArtistID.get(0).size(); e++) {
                                if (PlaylistSongID.get(16).get(i).equals(PlaylistArtistID.get(12).get(e))) {
                                    InfoDisplayArtistUniversalRank.setText(PlaylistArtistID.get(3).get(e));
                                    InfoDisplayArtistPlaylistRank.setText(PlaylistArtistID.get(2).get(e));
                                    InfoDisplayArtistName.setText(PlaylistArtistID.get(0).get(e));
                                }
                            }

                            //Album Stuff
                            for (int e = 0; e < PlaylistAlbumID.get(0).size(); e++) {
                                if (PlaylistSongID.get(15).get(i).equals(PlaylistAlbumID.get(13).get(e))) {
                                    InfoDisplayAlbumUniversalRank.setText(PlaylistAlbumID.get(4).get(e));
                                    InfoDisplayAlbumPlaylistRank.setText(PlaylistAlbumID.get(3).get(e));
                                    InfoDisplayAlbumName.setText(PlaylistAlbumID.get(0).get(e));
                                }
                            }
                        }
                    } else {
                        //
                        //Master File
                        //
                        if (FileID.equals(PlaylistSongID.get(15).get(i))) {
                            if (PlaylistSongID.get(4).get(i).equals(null)) {
                                if (AppAppearance.equals("Dark")) {
                                    Glide.with(this).load(R.drawable.placeholderdark).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                                } else {
                                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                                }
                            } else {
                                GlideDisplay(i, InfoDisplayImage, "Song");
                                //Glide.with(getApplicationContext()).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(InfoDisplayImage);
                            }
                            InfoDisplaySongName.setText(PlaylistSongID.get(0).get(i));
                            InfoDisplaySongUniversalRank.setText(PlaylistSongID.get(5).get(i));
                            InfoDisplaySongPlaylistRank.setText("");

                            InfoDisplayTotalTime.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(10).get(i))));
                            InfoDisplayTimesPlayed.setText(PlaylistSongID.get(9).get(i));


                            // InfoDisplayFavStart.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(15).get(i))));
                            // InfoDisplayFavEnd.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(16).get(i))));

                            //Artist Stuff    (if this doesn't work just copy the SetObjectID mechanism if this does work though, possibly replace everything with this, this could save RAM and lots of time)
                            for (int e = 0; e < PlaylistArtistID.get(0).size(); e++) {
                                if (PlaylistSongID.get(13).get(i).equals(PlaylistArtistID.get(9).get(e))) {
                                    InfoDisplayArtistUniversalRank.setText(PlaylistArtistID.get(2).get(e));
                                    InfoDisplayArtistPlaylistRank.setText("");
                                    InfoDisplayArtistName.setText(PlaylistArtistID.get(0).get(e));
                                }
                            }

                            //Album Stuff
                            for (int e = 0; e < PlaylistAlbumID.get(0).size(); e++) {
                                if (PlaylistSongID.get(12).get(i).equals(PlaylistAlbumID.get(10).get(e))) {
                                    InfoDisplayAlbumUniversalRank.setText(PlaylistAlbumID.get(3).get(e));
                                    InfoDisplayAlbumPlaylistRank.setText("");
                                    InfoDisplayAlbumName.setText(PlaylistAlbumID.get(0).get(e));
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    public void DetectPlaylist() {

        String fileName = "viewPlaylistInfo.json";
        int NumPlaylist;
        ArrayList<ArrayList<String>> PlaylistID = new ArrayList<>();

        try {
            JSONObject InfoFile = new JSONObject(loadJsonFile(fileName));
            JSONArray PlayNum = InfoFile.getJSONArray("NumOfPlaylist");
            NumPlaylist = (int) PlayNum.get(0);

            //Initialize Array PlaylistID
            for (int i = 0; i < 7; i++) {
                PlaylistID.add(new ArrayList<String>());
            }

            //
            //Load Playlist and Area Array
            //
            for (int i = 0; i < NumPlaylist; i++) {
                JSONArray Play = InfoFile.getJSONArray("PID" + i);
                for (int e = 0; e < 7; e++) {
                    PlaylistID.get(e).add(Play.get(e).toString());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < PlaylistID.get(0).size(); i++) {
            ArrayList<String> PlaylistObjectID = new ArrayList<>();

            try {
                JSONObject PlaylistFile = new JSONObject(loadJsonFile(PlaylistID.get(1).get(i) + ".json"));
                switch (ObjectMode) {
                    case "Album":
                        JSONArray TotalAlbumNum = PlaylistFile.getJSONArray("TotalAlbumNum");
                        int AlbumNum = (int) TotalAlbumNum.get(0);

                        //Load All Album Info into 2D Array
                        for (int e = 0; e < AlbumNum; e++) {
                            JSONArray AlID = PlaylistFile.getJSONArray("AlID" + e);
                            PlaylistObjectID.add(AlID.get(16).toString());
                        }

                        break;
                    case "Artist":
                        JSONArray TotalArtistNum = PlaylistFile.getJSONArray("TotalArtistNum");
                        int ArtistNum = (int) TotalArtistNum.get(0);

                        //Load All Artist Info into 2D Array
                        for (int e = 0; e < ArtistNum; e++) {
                            JSONArray AID = PlaylistFile.getJSONArray("AID" + e);
                            PlaylistObjectID.add(AID.get(14).toString());
                        }
                        break;
                    case "Song":
                        JSONArray TotalSongNum = PlaylistFile.getJSONArray("TotalSongNum");
                        int SongNum = (int) TotalSongNum.get(0);

                        //Load All Song Info into 2D Array
                        for (int e = 0; e < SongNum; e++) {
                            JSONArray SID = PlaylistFile.getJSONArray("SID" + e);
                            PlaylistObjectID.add(SID.get(19).toString());
                        }
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (PlaylistObjectID.contains(FileID)) {
                ContainedPlaylists.get(0).add(PlaylistID.get(0).get(i));
                ContainedPlaylists.get(1).add(PlaylistID.get(1).get(i));
            }
        }
    }

    public void GetSearchHeader() {
        SearchBar = findViewById(R.id.SearchBar);
    }

    public void SetSearchHeader() {

        SearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                SearchTerm = newText;
                GenSongLengthChecker();
                EditList.setAdapter(InfoDisplayEditAdapter);

                return false;
            }
        });

    }

    public String Title(String Name) {

        if (Name.length() >= 18) {
            String tooLong = "";
            for (int e = 0; e < 18; e++) {
                tooLong += Name.charAt(e);
            }
            tooLong += "...";
            return tooLong;
        } else {
            return Name;
        }
    }

    public void GetRegularBarsSongs(View view) {
        SongBar = view.findViewById(R.id.SongBar);
        SongImage = view.findViewById(R.id.SongImage);
        SongName = view.findViewById(R.id.SongName);
        SongArtist = view.findViewById(R.id.SongArtist);
        SongLength = view.findViewById(R.id.SongLength);

        if (AppAppearance.equals("Dark")) {
            SongName.setTextColor(Color.rgb(255, 255, 255));
            SongArtist.setTextColor(Color.rgb(255, 255, 255));
            SongLength.setTextColor(Color.rgb(255, 255, 255));
            SongBar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark2));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetRegularInfoSongs(int i, String Mode) {
        //SongImage.setImageResource(Integer.parseInt(PlaylistSongID.get(4).get(i)));

        switch (Mode) {
            case "Song":

                if (PlaylistSongID.get(4).get(i).equals(null)) {
                    if (AppAppearance.equals("Dark")) {
                        Glide.with(this).load(R.drawable.placeholderdark).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                    } else {
                        Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                    }
                } else {
                    GlideDisplay(i, SongImage, "Song");
                    //Glide.with(this).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                }
                SongName.setText(Title(PlaylistSongID.get(0).get(i)));
                SongArtist.setText(Title(PlaylistSongID.get(1).get(i)));

                if (Playlist) {
                    //Playlist
                    SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(11).get(i))));
                } else {
                    //Master File
                    SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(8).get(i))));
                }
                break;
            case "Artist":

                if (PlaylistArtistID.get(1).get(i).equals(null)) {
                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                } else {
                    GlideDisplay(i, SongImage, "Artist");
                    //Glide.with(this).load(PlaylistArtistID.get(1).get(i)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                }
                SongName.setText(Title(PlaylistArtistID.get(0).get(i)));
                SongArtist.setText("");

                if (Playlist) {
                    //Playlist
                    SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistArtistID.get(8).get(i))));
                } else {
                    //Master File
                    SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistArtistID.get(5).get(i))));
                }
                break;
            case "Album":

                if (PlaylistAlbumID.get(2).get(i).equals(null)) {
                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                } else {
                    GlideDisplay(i, SongImage, "Album");
                    //Glide.with(this).load(PlaylistAlbumID.get(2).get(i)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                }
                SongName.setText(Title(PlaylistAlbumID.get(0).get(i)));
                SongArtist.setText(Title(PlaylistAlbumID.get(1).get(i)));

                if (Playlist) {
                    SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistAlbumID.get(9).get(i))));
                } else {
                    //Master File
                    SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistAlbumID.get(6).get(i))));
                }
                break;
        }
    }

    public void SetPossibleSkin(int i, String Mode) {

        ArrayList<String> TempSkin = new ArrayList<>();
        int[] Positions;
        for (int e = 0; e < ObjectChosen.size(); e++) {
            TempSkin.add(ObjectChosen.get(e));
        }
        switch (Mode) {
            case "Song":
                if (Playlist) {
                    Positions = new int[]{0, 1, 2, 4, 15, 16};
                } else {
                    Positions = new int[]{0, 1, 2, 4, 12, 13};
                }

                for (int e = 0; e < SkinSongID.size() - 1; e++) {
                    if (SkinSongID.get(e).get(i).equals(null)) {
                    } else {
                        TempSkin.set(Positions[e], SkinSongID.get(e).get(i));
                    }
                }

                if (TempSkin.get(4).equals(null)) {
                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                } else {
                    Glide.with(this).load(TempSkin.get(4)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                }
                SongName.setText(Title(TempSkin.get(0)));
                SongArtist.setText(Title(TempSkin.get(1)));

                if (Playlist) {
                    //Playlist
                    SongLength.setText(ClockDisplay(Integer.parseInt(TempSkin.get(11))));
                } else {
                    //Master File
                    SongLength.setText(ClockDisplay(Integer.parseInt(TempSkin.get(8))));
                }
                break;
            case "Artist":

                if (Playlist) {
                    Positions = new int[]{0, 1, 12};
                } else {
                    Positions = new int[]{0, 1, 9};
                }

                for (int e = 0; e < SkinArtistID.size() - 1; e++) {
                    if (SkinArtistID.get(e).get(i).equals(null)) {
                    } else {
                        TempSkin.set(Positions[e], SkinArtistID.get(e).get(i));
                    }
                }

                if (TempSkin.get(1).equals(null)) {
                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                } else {
                    Glide.with(this).load(TempSkin.get(1)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                }
                SongName.setText(Title(TempSkin.get(0)));
                SongArtist.setText("");

                if (Playlist) {
                    //Playlist
                    SongLength.setText(ClockDisplay(Integer.parseInt(TempSkin.get(8))));
                } else {
                    //Master File
                    SongLength.setText(ClockDisplay(Integer.parseInt(TempSkin.get(5))));
                }
                break;
            case "Album":
                if (Playlist) {
                    Positions = new int[]{0, 1, 2, 13, 14};
                } else {
                    Positions = new int[]{0, 1, 2, 10, 11};
                }

                for (int e = 0; e < SkinAlbumID.size() - 1; e++) {
                    if (SkinAlbumID.get(e).get(i).equals(null)) {
                    } else {
                        TempSkin.set(Positions[e], SkinAlbumID.get(e).get(i));
                    }
                }

                if (TempSkin.get(2).equals(null)) {
                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                } else {
                    Glide.with(this).load(TempSkin.get(2)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                }
                SongName.setText(Title(TempSkin.get(0)));
                SongArtist.setText(Title(TempSkin.get(1)));

                if (Playlist) {
                    SongLength.setText(ClockDisplay(Integer.parseInt(TempSkin.get(9))));
                } else {
                    //Master File
                    SongLength.setText(ClockDisplay(Integer.parseInt(TempSkin.get(6))));
                }
                break;
        }
    }
/*
    public void GenButtonsMenus() {
        //
        //Play Add Menu
        //
        PlayAddMenu = (FloatingActionMenu) findViewById(R.id.Play_AddMenu);
        AddMenuBTN = (FloatingActionButton) findViewById(R.id.AddMenuBTN);
        PlayMenuBTN = (FloatingActionButton) findViewById(R.id.PlayMenuBTN);

        //
        //Menus
        //
        PlayMenu = findViewById(R.id.PlayMenu);
        AddMenu = findViewById(R.id.AddMenu);
        PlayMenuAdvanced = findViewById(R.id.PlayMenuAdvanced);

        //
        //Play Menu
        //
        PlayAlbum = findViewById(R.id.PlayAlbum);
        PlayArtist = findViewById(R.id.PlayArtist);
        PlayRepeat = findViewById(R.id.PlayRepeat);
        PlayQueue = findViewById(R.id.PlayQueue);
        PlayShuffle = findViewById(R.id.PlayShuffle);
        PlayRegular = findViewById(R.id.PlayRegular);
        PlayMenuAdvancedBTN = findViewById(R.id.PlayMenuAdvancedBTN);
        PlayReturn = findViewById(R.id.PlayMenuReturn);

        //
        //Add Menu
        //
        AddTags = findViewById(R.id.AddTags);
        AddSongs = findViewById(R.id.AddSongs);
        AddAudioMux = findViewById(R.id.AddAudioMux);
        AddRule = findViewById(R.id.AddRule);
        AddPlaylist = findViewById(R.id.AddPlaylist);
        AddReturn = findViewById(R.id.AddMenuReturn);

        //
        //Play Advanced Menu
        //
        PlayAdvAudioMux = findViewById(R.id.PlayAudioMux);
        PlayAdvMix = findViewById(R.id.PlayMix);
        PlayAdvTimeConstraint = findViewById(R.id.PlayTimeConstraint);
        PlayAdvRule = findViewById(R.id.PlayRule);
        PlayAdvReturn = findViewById(R.id.PlayMenuAdvancedReturn);

        //
        //Navigation Menu
        //
        NavigationMenu = findViewById(R.id.NavigationMenu);

        Home = findViewById(R.id.Home);
        SongLibrary = findViewById(R.id.Songs);
        NewPlaylist = findViewById(R.id.NewPlaylist);
        Settings = findViewById(R.id.Settings);

    }

 */
/*
    public void FloatingActionMenus() {

        //
        //Navigation menu
        //
        NavigationMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationMenu.open(true);
            }
        });

        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Home = new Intent(getApplicationContext(), PlaylistView.class);
                startActivity(Home);
            }
        });

        SongLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SongLibrary = new Intent(getApplicationContext(), MusicLibrarySong.class);
                startActivity(SongLibrary);
            }
        });

        NewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NewPlay = new Intent(getApplicationContext(), CreateNewPlaylist.class);
                startActivity(NewPlay);
            }
        });

        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //
        //Play and Add Menu
        //
        AddMenuBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayAddMenu.hideMenu(true);
                //PlayAddMenu.showMenu(true);
                AddMenu.showMenu(true);
                AddMenu.open(true);
                AddMenuOpen = true;
            }
        });

        PlayMenuBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayAddMenu.hideMenu(true);
                //PlayAddMenu.showMenu(true);
                PlayMenu.showMenu(true);
                PlayMenu.open(true);
                PlayMenuOpen = true;
            }
        });

        //
        //Play Menu
        //
        PlayAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });

        PlayArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });

        PlayRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });

        PlayQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });

        PlayShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });

        PlayRegular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });

        PlayMenuAdvancedBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenu.hideMenu(true);
                PlayMenuAdvanced.showMenu(true);
                PlayMenuAdvanced.open(true);
            }
        });

        PlayReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });


        //
        //Add Menu
        //
        AddTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });

        AddSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });

        AddAudioMux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });

        AddRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });

        AddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });

        AddReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMenu.hideMenu(true);
                PlayAddMenu.showMenu(true);
                PlayAddMenu.open(true);
            }
        });


        //
        //Play Menu Advanced
        //
        PlayAdvAudioMux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenuAdvanced.hideMenu(true);
                PlayMenu.showMenu(true);
                PlayMenu.open(true);
            }
        });

        PlayAdvMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenuAdvanced.hideMenu(true);
                PlayMenu.showMenu(true);
                PlayMenu.open(true);
            }
        });

        PlayAdvTimeConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenuAdvanced.hideMenu(true);
                PlayMenu.showMenu(true);
                PlayMenu.open(true);
            }
        });

        PlayAdvRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenuAdvanced.hideMenu(true);
                PlayMenu.showMenu(true);
                PlayMenu.open(true);
            }
        });

        PlayAdvReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add a timer between animations?
                PlayMenuAdvanced.hideMenu(true);
                PlayMenu.showMenu(true);
                PlayMenu.open(true);
            }
        });
    }

 */

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void GlideDisplay(int i, ImageView Image, String Type) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Bitmap bitmap = null;
            try {
                switch (Type) {
                    case "Album":
                        bitmap = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(PlaylistAlbumID.get(2).get(i)), new Size(1024, 1024), null);
                        break;
                    case "Artist":
                        bitmap = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(PlaylistArtistID.get(1).get(i)), new Size(1024, 1024), null);
                        break;
                    case "Song":
                        bitmap = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(PlaylistSongID.get(4).get(i)), new Size(1024, 1024), null);
                        break;
                }
                // ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                if (bitmap != null) {
                    //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    Image.setImageBitmap(bitmap);
                } else {
                    Drawable drawable;
                    if (AppAppearance.equals("Dark")) {
                        drawable = getResources().getDrawable(R.drawable.placeholderdark);
                    } else {
                        drawable = getResources().getDrawable(R.drawable.placeholder);
                    }
                    Image.setImageDrawable(drawable);
                }
                /*
                if (AppAppearance.equals("Dark")) {
                    Glide.with(this).load(stream.toByteArray()).asBitmap().error(R.drawable.placeholderdark).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(Image);
                } else {
                    Glide.with(this).load(stream.toByteArray()).asBitmap().error(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(Image);
                }

                 */
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            switch (Type) {
                case "Album":
                    if (AppAppearance.equals("Dark")) {
                        Glide.with(this).load(PlaylistAlbumID.get(2).get(i)).placeholder(R.drawable.placeholderdark).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(Image);
                    } else {
                        Glide.with(this).load(PlaylistAlbumID.get(2).get(i)).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(Image);
                    }
                    break;
                case "Artist":
                    if (AppAppearance.equals("Dark")) {
                        Glide.with(this).load(PlaylistArtistID.get(1).get(i)).placeholder(R.drawable.placeholderdark).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(Image);
                    } else {
                        Glide.with(this).load(PlaylistArtistID.get(1).get(i)).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(Image);
                    }
                    break;
                case "Song":
                    if (AppAppearance.equals("Dark")) {
                        Glide.with(this).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholderdark).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(Image);
                    } else {
                        Glide.with(this).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(Image);
                    }
                    break;
            }
        }
    }

    private class CustomAdapter1 extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {

            //Display thing
            //Other Skins
            //Related items (Songs)

            if (ObjectMode.equals("Song")) {
                return 2;
            } else if (ObjectMode.equals("Artist")) {
                return 4;
            } else if (ObjectMode.equals("Album")) {
                return 3;
            } else {
                return 0;
            }
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            if (groupPosition == 0) {
                return 0;
            } else if (groupPosition == 1) {
                return OwnedObjects.get(0).size();
            } else if (groupPosition == 2) {
                return OwnedObjects.get(1).size();
            } else if (groupPosition == 3) {
                return OwnedObjects.get(2).size();
            } else {
                return 0;
            }
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
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition * childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View Header = getLayoutInflater().inflate(R.layout.info_display_song, null);

            //Song =  Info, Applicable Skins

            //Artist = Info, Applicable Skins, Songs Owned, Albums owned

            //Album = Info, Applicable Skins, Songs Owned

            if (groupPosition == 0) {
                //Info
                if (ObjectMode.equals("Song")) {
                    Header = getLayoutInflater().inflate(R.layout.info_display_song, null);
                } else if (ObjectMode.equals("Artist")) {
                    Header = getLayoutInflater().inflate(R.layout.info_display_artist, null);
                } else if (ObjectMode.equals("Album")) {
                    Header = getLayoutInflater().inflate(R.layout.info_display_album, null);
                }
                GetDisplayInfoHeader(Header);
                SetDisplayInfoHeader();
            } else {
                Header = getLayoutInflater().inflate(R.layout.sort_options_header, null);
                RelativeLayout SortOptionHeaderBackground = Header.findViewById(R.id.SortOptionHeaderBackground);
                if (AppAppearance.equals("Dark")) {
                    SortOptionHeaderBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark));
                }
                SortOptionHeader = Header.findViewById(R.id.SortOptionHeader);
                if (groupPosition == 1) {
                    if (OwnedObjects.get(0).size() <= 1) {
                        SortOptionHeader.setText("Applicable Skin");
                    } else {
                        SortOptionHeader.setText("Applicable Skins");
                    }
                    InfoList.expandGroup(groupPosition);
                } else if (groupPosition == 2) {
                    if (OwnedObjects.get(0).size() <= 1) {
                        SortOptionHeader.setText("Owned Song");
                    } else {
                        SortOptionHeader.setText("Owned Songs");
                    }
                    InfoList.expandGroup(groupPosition);
                } else if (groupPosition == 3) {
                    if (OwnedObjects.get(0).size() <= 1) {
                        SortOptionHeader.setText("Owned Album");
                    } else {
                        SortOptionHeader.setText("Owned Albums");
                    }
                    InfoList.expandGroup(groupPosition);
                }
            }

            return Header;
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View Child = getLayoutInflater().inflate(R.layout.song_bar_object_small, null);
            //Song =  Info, Applicable Skins

            //Artist = Info, Applicable Skins, Songs Owned, Albums owned

            //Album = Info, Applicable Skins, Songs Owned


            switch (groupPosition) {

                case 1:
                    //Skins
                    if (ObjectMode.equals("Song")) {
                        Child = getLayoutInflater().inflate(R.layout.song_bar_object_small, null);
                    } else {
                        Child = getLayoutInflater().inflate(R.layout.song_bar_object_big, null);
                    }
                    GetRegularBarsSongs(Child);
                    SetPossibleSkin(OwnedObjects.get(groupPosition - 1).get(childPosition), ObjectMethod);

                    if (AppAppearance.equals("Dark")) {
                        SongBar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark2));
                    }

                    break;
                case 2:
                    //Songs Owned
                    Child = getLayoutInflater().inflate(R.layout.song_bar_object_small, null);
                    GetRegularBarsSongs(Child);
                    SetRegularInfoSongs(OwnedObjects.get(groupPosition - 1).get(childPosition), "Song");

                    if (AppAppearance.equals("Dark")) {
                        SongBar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark2));
                    }
                    break;
                case 3:
                    //Albums Owned
                    Child = getLayoutInflater().inflate(R.layout.song_bar_object_big, null);
                    GetRegularBarsSongs(Child);
                    SetRegularInfoSongs(OwnedObjects.get(groupPosition - 1).get(childPosition), "Album");

                    if (AppAppearance.equals("Dark")) {
                        SongBar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark2));
                    }
                    break;
            }

            return Child;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    public void RankScroll(String Rank, final boolean Universal, final String RankType, final int SavePos) {

        for (int i = 0; i < 11; i++) {
            View Button;
            if (Integer.parseInt(Rank) == (i)) {
                Button = getLayoutInflater().inflate(R.layout.horizontal_view_object_selected, null);
                if (AppAppearance.equals("Dark")) {
                    LinearLayout Background = Button.findViewById(R.id.TMAHorizonViewObject);
                    Background.setBackground(ContextCompat.getDrawable(this, R.drawable.object_selectdark));
                }
            } else {
                Button = getLayoutInflater().inflate(R.layout.horizontal_view_object, null);
                if (AppAppearance.equals("Dark")) {
                    LinearLayout Background = Button.findViewById(R.id.TMAHorizonViewObject);
                    Background.setBackground(ContextCompat.getDrawable(this, R.drawable.objectdark));
                }
            }
            TextView SortType = Button.findViewById(R.id.TMAObject);
            if (AppAppearance.equals("Dark")) {
                SortType.setTextColor(Color.rgb(255, 255, 255));
            }
            SortType.setText(String.valueOf(i));
            final int finalI = i;
            Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Universal) {
                        switch (RankType) {
                            case "Album":
                                if (Playlist) {
                                    PlaylistAlbumID.get(4).set(SavePos, String.valueOf(finalI));
                                } else {
                                    PlaylistAlbumID.get(3).set(SavePos, String.valueOf(finalI));
                                }
                                break;
                            case "Artist":
                                if (Playlist) {
                                    PlaylistArtistID.get(3).set(SavePos, String.valueOf(finalI));
                                } else {
                                    PlaylistArtistID.get(2).set(SavePos, String.valueOf(finalI));
                                }
                                break;
                            case "Song":
                                if (Playlist) {
                                    PlaylistSongID.get(6).set(SavePos, String.valueOf(finalI));
                                } else {
                                    PlaylistSongID.get(5).set(SavePos, String.valueOf(finalI));
                                }
                                break;
                        }
                    } else {
                        switch (RankType) {
                            case "Album":
                                if (Playlist) {
                                    PlaylistAlbumID.get(3).set(SavePos, String.valueOf(finalI));
                                }
                                break;
                            case "Artist":
                                if (Playlist) {
                                    PlaylistArtistID.get(2).set(SavePos, String.valueOf(finalI));
                                }
                                break;
                            case "Song":
                                if (Playlist) {
                                    PlaylistSongID.get(5).set(SavePos, String.valueOf(finalI));
                                }
                                break;
                        }
                    }
                    EditList.setAdapter(InfoDisplayEditAdapter);
                }
            });
            EditRank.addView(Button);
        }
    }

    public void GetEditView(View view, int pos) {

        /*
            //Edit Extra Info
            EditFavStart = view.findViewById(R.id.EditFavStart);
            EditFavEnd = view.findViewById(R.id.EditFavEnd);

            if (AppAppearance.equals("Dark")) {
                EditFavStart.setTextColor(Color.rgb(255, 255, 255));
                EditFavEnd.setTextColor(Color.rgb(255, 255, 255));
                LinearLayout InfoDisplayEditExtraBackground = view.findViewById(R.id.InfoDisplayEditExtraBackground);
                InfoDisplayEditExtraBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.placeholderdark));
            }

         */

        if (pos == 0) {
            EditImageView = view.findViewById(R.id.EditImageView);
            if (AppAppearance.equals("Dark")) {
                LinearLayout InfoDisplayEditImageBackground = view.findViewById(R.id.InfoDisplayEditImageBackground);
                InfoDisplayEditImageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.placeholderdark));
            }
        } else {
            ObjectEdit = view.findViewById(R.id.ObjectEdit);
            EditRank = view.findViewById(R.id.EditRank);
            EditRankType = view.findViewById(R.id.EditRankType);
            if (AppAppearance.equals("Dark")) {
                EditRankType.setTextColor(Color.rgb(255, 255, 255));
                ObjectEdit.setTextColor(Color.rgb(255, 255, 255));
                LinearLayout InfoDisplayEditObjectBackground = view.findViewById(R.id.InfoDisplayEditObjectBackground);
                InfoDisplayEditObjectBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.placeholderdark));
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetEditView(int pos) {

        /*
        if (Extra) {

            if (AppAppearance.equals("Dark")) {
                EditFavStart.setTextColor(Color.rgb(255, 255, 255));
                EditFavEnd.setTextColor(Color.rgb(255, 255, 255));

            }

            if (Playlist) {
                EditFavStart.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(18).get(FilePos))));
                EditFavEnd.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(19).get(FilePos))));
            } else {
                EditFavStart.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(15).get(FilePos))));
                EditFavEnd.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(16).get(FilePos))));
            }



            final AlertDialog dialog;
            final EditText NewEdit;

            dialog = new AlertDialog.Builder(this).create();
            NewEdit = new EditText(this);

            EditFavStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.setTitle("Fav Start");
                    dialog.setView(NewEdit);
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditFavStart.setText(NewEdit.getText());
                            if (Playlist) {
                                PlaylistSongID.get(18).set(FilePos, NewEdit.getText().toString());
                            } else {
                                PlaylistSongID.get(15).set(FilePos, NewEdit.getText().toString());
                            }
                        }
                    });
                }
            });

            EditFavEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.setTitle("Fav End");
                    dialog.setView(NewEdit);
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditFavEnd.setText(NewEdit.getText());
                            if (Playlist) {
                                PlaylistSongID.get(19).set(FilePos, NewEdit.getText().toString());
                            } else {
                                PlaylistSongID.get(16).set(FilePos, NewEdit.getText().toString());
                            }
                        }
                    });
                }
            });

        } else {

         */
        switch (ObjectMode) {
            case "Album":
                switch (pos) {
                    case 0:
                        GlideDisplay(FilePos, EditImageView, ObjectMode);
                        SelectModeSwitch("Image");
                        EditImageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        EditImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        break;
                    case 1:
                        ObjectEdit.setText(PlaylistAlbumID.get(0).get(FilePos));

                        if (Playlist) {
                            EditRankType.setText("PR");
                            RankScroll(PlaylistAlbumID.get(3).get(FilePos), false, "Album", FilePos);
                        } else {
                            EditRankType.setText("UR");
                            RankScroll(PlaylistAlbumID.get(3).get(FilePos), true, "Album", FilePos);
                        }
                        SelectModeSwitch("Album");
                        break;
                    case 2:
                        ObjectEdit.setText(PlaylistAlbumID.get(1).get(FilePos));
                        for (int g = 0; g < ArtistNum; g++) {

                            if (Playlist) {
                                if (PlaylistAlbumID.get(14).get(FilePos).equals(PlaylistArtistID.get(12).get(g))) {
                                    EditRankType.setText("PR");
                                    RankScroll(PlaylistArtistID.get(2).get(g), false, "Artist", g);
                                }
                            } else {
                                if (PlaylistAlbumID.get(11).get(FilePos).equals(PlaylistArtistID.get(9).get(g))) {
                                    EditRankType.setText("UR");
                                    RankScroll(PlaylistArtistID.get(2).get(g), true, "Artist", g);
                                }
                            }
                        }
                        SelectModeSwitch("Artist");
                        break;
                }
                break;
            case "Artist":
                switch (pos) {
                    case 0:
                        GlideDisplay(FilePos, EditImageView, ObjectMode);
                        SelectModeSwitch("Image");
                        EditImageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        EditImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        break;
                    case 1:
                        if (Playlist) {
                            ObjectEdit.setText(PlaylistArtistID.get(0).get(FilePos));
                            EditRankType.setText("PR");
                            RankScroll(PlaylistArtistID.get(2).get(FilePos), false, "Artist", FilePos);
                        } else {
                            ObjectEdit.setText(PlaylistArtistID.get(0).get(FilePos));
                            EditRankType.setText("UR");
                            RankScroll(PlaylistArtistID.get(2).get(FilePos), true, "Artist", FilePos);
                        }

                        SelectModeSwitch("Artist");
                        break;
                }
                break;
            case "Song":

                //
                //We don't really need to edit the songs name but whatevs remove later
                //

                switch (pos) {
                    case 0:
                        GlideDisplay(FilePos, EditImageView, ObjectMode);
                        SelectModeSwitch("Image");
                        EditImageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        EditImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        break;
                    case 1:
                        ObjectEdit.setText(PlaylistSongID.get(0).get(FilePos));
                        if (Playlist) {
                            EditRankType.setText("PR");
                            RankScroll(PlaylistSongID.get(5).get(FilePos), false, "Song", FilePos);
                        } else {
                            EditRankType.setText("UR");
                            RankScroll(PlaylistSongID.get(5).get(FilePos), true, "Song", FilePos);
                        }

                        SelectModeSwitch("Song");
                        break;
                    case 2:
                        ObjectEdit.setText(PlaylistSongID.get(1).get(FilePos));
                        for (int g = 0; g < ArtistNum; g++) {
                            if (Playlist) {

                                if (PlaylistSongID.get(16).get(FilePos).equals(PlaylistArtistID.get(12).get(g))) {
                                    EditRankType.setText("PR");
                                    RankScroll(PlaylistArtistID.get(2).get(g), false, "Artist", g);
                                }
                            } else {
                                if (PlaylistSongID.get(13).get(FilePos).equals(PlaylistArtistID.get(9).get(g))) {
                                    EditRankType.setText("UR");
                                    RankScroll(PlaylistArtistID.get(2).get(g), true, "Artist", g);
                                }
                            }
                        }

                        SelectModeSwitch("Artist");
                        break;
                    case 3:
                        ObjectEdit.setText(PlaylistSongID.get(2).get(FilePos));
                        for (int g = 0; g < AlbumNum; g++) {
                            if (Playlist) {
                                if (PlaylistSongID.get(15).get(FilePos).equals(PlaylistAlbumID.get(13).get(g))) {
                                    EditRankType.setText("PR");
                                    RankScroll(PlaylistAlbumID.get(3).get(g), false, "Album", g);
                                }
                            } else {
                                if (PlaylistSongID.get(12).get(FilePos).equals(PlaylistAlbumID.get(10).get(g))) {
                                    EditRankType.setText("UR");
                                    RankScroll(PlaylistAlbumID.get(3).get(g), true, "Album", g);
                                }
                            }
                        }

                        SelectModeSwitch("Album");
                        break;
                }
                break;
        }
        //}
    }

    public void SelectModeSwitch(final String Type) {

        //Reset FilePos since position has changed
        switch (ObjectMode) {
            case "Album":
                for (int i = 0; i < PlaylistAlbumID.get(0).size(); i++) {
                    if (Playlist) {
                        if (FileID.equals(PlaylistAlbumID.get(16).get(i))) {
                            FilePos = i;
                        }
                    } else {
                        if (FileID.equals(PlaylistAlbumID.get(13).get(i))) {
                            FilePos = i;
                        }
                    }
                }
                break;
            case "Artist":
                for (int i = 0; i < PlaylistArtistID.get(0).size(); i++) {
                    if (Playlist) {
                        if (FileID.equals(PlaylistArtistID.get(14).get(i))) {
                            FilePos = i;
                        }
                    } else {
                        if (FileID.equals(PlaylistArtistID.get(11).get(i))) {
                            FilePos = i;
                        }
                    }
                }
                break;
            case "Song":
                for (int i = 0; i < PlaylistSongID.get(0).size(); i++) {
                    if (Playlist) {
                        if (FileID.equals(PlaylistSongID.get(18).get(i))) {
                            FilePos = i;
                        }
                    } else {
                        if (FileID.equals(PlaylistSongID.get(15).get(i))) {
                            FilePos = i;
                        }
                    }
                }
                break;
        }

        if (Type.equals("Image")) {
            EditImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectMode = true;
                    SelectMethod = Type;


                    GenSongLengthChecker();
                    EditList.setAdapter(InfoDisplayEditAdapter);
                }
            });
        } else {
            ObjectEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectMode = true;
                    SelectMethod = Type;

                    SetSearchHeader();
                    SearchBar = findViewById(R.id.SearchBar);
                    SearchBar.setVisibility(View.VISIBLE);
                    LoadedPositions.clear();

                    SortEverything();
                    GenSongLengthChecker();
                    EditList.setAdapter(InfoDisplayEditAdapter);
                }
            });
        }
    }

    public void GetRegularSelectBar(View view, boolean Image) {

        if (Image) {
            EditImageSelect = view.findViewById(R.id.EditImageSelect);
        } else {
            GetRegularBarsSongs(view);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetRegularSelectBar(final int i, final String Mode, final boolean First) {

        AlertDialog dialog = null;
        EditText NewEdit = null;

        if (First) {

            Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
            switch (Mode) {
                case "Album":
                    SongName.setText("Generate New Album");
                    SongArtist.setText("");
                    SongLength.setText("");
                    break;
                case "Artist":
                    SongName.setText("Generate New Artist");
                    SongArtist.setText("");
                    SongLength.setText("");
                    break;
                case "Song":
                    SongName.setText("Generate New Song"); //Remove
                    SongArtist.setText("");
                    SongLength.setText("");
                    break;

            }

            dialog = new AlertDialog.Builder(InfoDisplay.this).create();
            dialog.setTitle("Do you wan't to save your modified file");

            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Save everything then leave
                    SaveInfo();
                    Intent NewSkin = new Intent(getApplicationContext(), CreateNewSkin.class);
                    NewSkin.putExtra("ObjectMode", Mode);
                    startActivity(NewSkin);
                }
            });

            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Don't save and leave
                    Intent NewSkin = new Intent(getApplicationContext(), CreateNewSkin.class);
                    NewSkin.putExtra("ObjectMode", Mode);
                    startActivity(NewSkin);
                }
            });


        } else {
            switch (Mode) {
                case "Image":
                    GlideDisplay(i, EditImageSelect, "Album");

                    EditImageSelect.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    EditImageSelect.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                    EditImageSelect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (ObjectMode) {
                                case "Album":
                                    switch (Mode) {
                                        case "Image":
                                            PlaylistAlbumID.get(2).set(FilePos, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                            break;
                                    }
                                    break;
                                case "Artist":
                                    switch (Mode) {
                                        case "Image":
                                            PlaylistArtistID.get(1).set(FilePos, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                            break;
                                    }
                                    break;
                                case "Song":
                                    //Maybe just switch this to the alert thing and edit Song Name
                                    switch (Mode) {
                                        case "Image":
                                            PlaylistSongID.get(4).set(FilePos, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                            break;
                                    }
                                    break;
                            }
                            SelectMode = false;
                            SearchBar.setVisibility(View.INVISIBLE);
                            EditList.setAdapter(InfoDisplayEditAdapter);
                        }
                    });
                    break;
                case "Song":

                    if (PlaylistSongID.get(4).get(i).equals(null)) {
                        Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                    } else {
                        GlideDisplay(i, SongImage, "Song");
                        //Glide.with(this).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                    }
                    SongName.setText(Title(PlaylistSongID.get(0).get(i)));
                    SongArtist.setText(Title(PlaylistSongID.get(1).get(i)));

                    if (Playlist) {
                        //Playlist
                        SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(11).get(i))));
                    } else {
                        //Master File
                        SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(8).get(i))));
                    }
                    break;
                case "Artist":

                    if (PlaylistArtistID.get(1).get(i).equals(null)) {
                        Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                    } else {
                        GlideDisplay(i, SongImage, "Artist");
                        //Glide.with(this).load(PlaylistArtistID.get(1).get(i)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                    }
                    SongName.setText(Title(PlaylistArtistID.get(0).get(i)));
                    SongArtist.setText("");

                    if (Playlist) {
                        //Playlist
                        SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistArtistID.get(8).get(i))));
                    } else {
                        //Master File
                        SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistArtistID.get(5).get(i))));
                    }
                    break;
                case "Album":

                    if (PlaylistAlbumID.get(2).get(i).equals(null)) {
                        Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                    } else {
                        GlideDisplay(i, SongImage, "Album");
                        //Glide.with(this).load(PlaylistAlbumID.get(2).get(i)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                    }
                    SongName.setText(Title(PlaylistAlbumID.get(0).get(i)));
                    SongArtist.setText(Title(PlaylistAlbumID.get(1).get(i)));

                    if (Playlist) {
                        SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistAlbumID.get(9).get(i))));
                    } else {
                        //Master File
                        SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistAlbumID.get(6).get(i))));
                    }
                    break;
            }
        }

        if (!Mode.equals("Image")) {
            final AlertDialog finalDialog = dialog;
            SongBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (First) {
                        finalDialog.show();

                    } else {
                        switch (ObjectMode) {
                            case "Album":
                                switch (Mode) {
                                    case "Image":
                                        PlaylistAlbumID.get(2).set(FilePos, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                        break;
                                    case "Album":
                                        PlaylistAlbumID.get(0).set(FilePos, PlaylistAlbumID.get(0).get(SongLengthChecker.get(i)));
                                        if (Playlist) {
                                            PlaylistAlbumID.get(13).set(FilePos, PlaylistAlbumID.get(13).get(SongLengthChecker.get(i)));
                                        } else {
                                            PlaylistAlbumID.get(10).set(FilePos, PlaylistAlbumID.get(10).get(SongLengthChecker.get(i)));
                                        }
                                        break;
                                    case "Artist":
                                        PlaylistAlbumID.get(1).set(FilePos, PlaylistArtistID.get(0).get(SongLengthChecker.get(i)));
                                        if (Playlist) {
                                            PlaylistAlbumID.get(14).set(FilePos, PlaylistArtistID.get(12).get(SongLengthChecker.get(i)));
                                        } else {
                                            PlaylistAlbumID.get(11).set(FilePos, PlaylistArtistID.get(9).get(SongLengthChecker.get(i)));
                                        }
                                        break;
                                }
                                break;
                            case "Artist":
                                switch (Mode) {
                                    case "Image":
                                        PlaylistArtistID.get(1).set(FilePos, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                        break;
                                    case "Artist":
                                        PlaylistArtistID.get(0).set(FilePos, PlaylistArtistID.get(0).get(SongLengthChecker.get(i)));
                                        if (Playlist) {
                                            PlaylistArtistID.get(12).set(FilePos, PlaylistArtistID.get(12).get(SongLengthChecker.get(i)));
                                        } else {
                                            PlaylistArtistID.get(9).set(FilePos, PlaylistArtistID.get(9).get(SongLengthChecker.get(i)));
                                        }
                                }
                                break;
                            case "Song":
                                //Maybe just switch this to the alert thing and edit Song Name
                                switch (Mode) {
                                    case "Image":
                                        PlaylistSongID.get(4).set(FilePos, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                        break;
                                    case "Album":
                                        PlaylistSongID.get(2).set(FilePos, PlaylistAlbumID.get(0).get(SongLengthChecker.get(i)));
                                        if (Playlist) {
                                            PlaylistSongID.get(15).set(FilePos, PlaylistAlbumID.get(13).get(SongLengthChecker.get(i)));
                                        } else {
                                            PlaylistSongID.get(12).set(FilePos, PlaylistAlbumID.get(10).get(SongLengthChecker.get(i)));
                                        }
                                        break;
                                    case "Artist":
                                        PlaylistSongID.get(1).set(FilePos, PlaylistArtistID.get(0).get(SongLengthChecker.get(i)));
                                        if (Playlist) {
                                            PlaylistSongID.get(16).set(FilePos, PlaylistArtistID.get(12).get(SongLengthChecker.get(i)));
                                        } else {
                                            PlaylistSongID.get(13).set(FilePos, PlaylistArtistID.get(9).get(SongLengthChecker.get(i)));
                                        }
                                        break;
                                    case "Song":
                                        //Maybe just switch this to the alert thing and edit Song Name
                                        PlaylistSongID.get(0).set(FilePos, PlaylistSongID.get(0).get(SongLengthChecker.get(i)));

                                }
                                break;
                        }
                        SelectMode = false;

                        EditList.setAdapter(InfoDisplayEditAdapter);
                    }
                }
            });
        }

    }

    public String GenNewIDs(String Mode) {

        ArrayList<String> AllID = new ArrayList<>();
        int ID = 0;
        int random;
        random = new Random().nextInt(30000) + 10000;

        switch (Mode) {
            case "Album":
                for (int i = 0; i < PlaylistAlbumID.get(0).size(); i++) {
                    if (Playlist) {
                        AllID.add(PlaylistAlbumID.get(13).get(i));
                    } else {
                        AllID.add(PlaylistAlbumID.get(10).get(i));
                    }
                }
                break;
            case "Artist":
                for (int i = 0; i < PlaylistArtistID.get(0).size(); i++) {
                    if (Playlist) {
                        AllID.add(PlaylistArtistID.get(12).get(i));
                    } else {
                        AllID.add(PlaylistArtistID.get(9).get(i));
                    }
                }
                break;
        }

        while (!AllID.contains(String.valueOf(random))) {
            random = new Random().nextInt(30000) + 10000;
        }

        return String.valueOf(ID);
    }

    public void SaveEverything() {
        SaveEdit = findViewById(R.id.SaveEdit);
        SaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveInfo();
                if (Playlist) {
                    Intent PlaylistView = new Intent(getApplicationContext(), PlaylistSongView.class);
                    PlaylistView.putExtra("FilePath", PlaylistFileName);
                    startActivity(PlaylistView);
                } else {
                    Intent Library = new Intent(getApplicationContext(), MusicLibrarySong.class);
                    Library.putExtra("FilePath", PlaylistFileName);
                    startActivity(Library);
                }

            }
        });

    }

    public void SaveInfo() {

        JSONObject NewPlaylist = new JSONObject();

        ArrayList<String> Album = new ArrayList<>();
        ArrayList<String> Artist = new ArrayList<>();

        for (int i = 0; i < PlaylistSongID.get(0).size(); i++) {
            if (Playlist) {
                if (!Album.contains(PlaylistSongID.get(15).get(i))) {
                    Album.add(PlaylistSongID.get(15).get(i));
                }
            } else {
                if (!Album.contains(PlaylistSongID.get(12).get(i))) {
                    Album.add(PlaylistSongID.get(12).get(i));
                }
            }
        }

        for (int i = 0; i < PlaylistSongID.get(0).size(); i++) {
            if (Playlist) {
                if (!Artist.contains(PlaylistSongID.get(16).get(i))) {
                    Artist.add(PlaylistSongID.get(16).get(i));
                }
            } else {
                if (!Artist.contains(PlaylistSongID.get(13).get(i))) {
                    Artist.add(PlaylistSongID.get(13).get(i));
                }
            }
        }

        JSONArray SongNum = new JSONArray();
        JSONArray AlbumNum = new JSONArray();
        JSONArray ArtistNum = new JSONArray();
        JSONArray ObjectSize = new JSONArray();
        JSONArray SortMethodArray = new JSONArray();
        JSONArray DisplayMethod = new JSONArray();
        JSONArray RankingMethod = new JSONArray();
        JSONArray PlaylistName = new JSONArray();

        SongNum.put(PlaylistSongID.get(0).size());
        ArtistNum.put(Artist.size());
        AlbumNum.put(Album.size());

        if (Playlist == true) {
            ObjectSize.put("Small");
            SortMethodArray.put(SortMethod);
            DisplayMethod.put(ObjectMethod);
            RankingMethod.put("Method1");
        }

        PlaylistName.put(PlaylistFileName.replace("_Playlist", ""));

        JSONArray Tags = new JSONArray();
        JSONArray Mood = new JSONArray();
        for (int i = 0; i < AllTags.size(); i++) {
            Tags.put(AllTags.get(i));
        }
        for (int i = 0; i < AllMoods.size(); i++) {
            Mood.put(AllMoods.get(i));
        }

        try {
            //Json.put("SortMethod", "Default");           //Add more info later like view Type (Dark or light mode ect)
            NewPlaylist.put("TotalSongNum", SongNum);
            NewPlaylist.put("TotalArtistNum", ArtistNum);
            NewPlaylist.put("TotalAlbumNum", AlbumNum);

            NewPlaylist.put("AllTags", Tags);
            NewPlaylist.put("AllMoods", Mood);
            NewPlaylist.put("PlaylistName", PlaylistName);
            if (Playlist == true) {
                NewPlaylist.put("ObjectDisplaySize", ObjectSize);
                NewPlaylist.put("SortMethod", SortMethodArray);
                NewPlaylist.put("ObjectDisplayMethod", DisplayMethod);                //Maybe change name
                NewPlaylist.put("UniversalRankingMethod", RankingMethod);        //Maybe change later, meth1 = 1-10, meth2 = 1-100, meth3 = 1-1000?
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //
        //
        //Going to have to change all this
        //
        //

        for (int i = 0; i < PlaylistSongID.get(0).size(); i++) {
            JSONArray ConstantData = new JSONArray();             //In this loop so that it resets all data every time
            String ID = "SID" + i;  //stands for Playlist Song ID


            for (int e = 0; e < PlaylistSongID.size(); e++) {
                ConstantData.put(PlaylistSongID.get(e).get(i));
            }

            try {
                NewPlaylist.put(ID, ConstantData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < PlaylistArtistID.get(0).size(); i++) {                                       //Saves all the info
            JSONArray ArtistStuff = new JSONArray();
            String ID = "AID" + i;         //Stands for Artist ID

            for (int e = 0; e < PlaylistArtistID.size(); e++) {
                ArtistStuff.put(PlaylistArtistID.get(e).get(i));
            }

            try {
                NewPlaylist.put(ID, ArtistStuff);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        for (int i = 0; i < PlaylistAlbumID.get(0).size(); i++) {                                       //Saves all the info
            JSONArray AlbumStuff = new JSONArray();
            String ID = "AlID" + i;         //Stands for Album ID

            for (int e = 0; e < PlaylistAlbumID.size(); e++) {
                AlbumStuff.put(PlaylistAlbumID.get(e).get(i));
            }

            try {
                NewPlaylist.put(ID, AlbumStuff);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //
        //Complete and Save
        //
        saveToStorage(PlaylistFileName, NewPlaylist);

    }

    public void saveToStorage(String fileName, JSONObject jsonObj) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput(fileName + ".json", Context.MODE_PRIVATE));
            writer.write(jsonObj.toString());
            writer.close();
            //Toast.makeText(this, "Succesfully Saved File", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private class CustomAdapter2 extends BaseAdapter {
        @Override
        public int getCount() {
            if (SelectMode) {
                if (SelectMethod.equals("Image")) {
                    return SongLengthChecker.size();
                } else {
                    return SongLengthChecker.size() + 1;
                }
            } else {
                if (ObjectMode.equals("Album")) {
                    return 3;
                } else if (ObjectMode.equals("Artist")) {
                    return 2;
                } else if (ObjectMode.equals("Song")) {
                    return 4;
                } else {
                    return 0;
                }
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View EditView = null;

            if (SelectMode) {
                SearchBar.getLayoutParams().height = 120;
                //Display all Songs/Albums/Artists
                if (position == 0 && SelectMethod != "Image") {
                    EditView = getLayoutInflater().inflate(R.layout.song_bar_object_small, null);
                    GetRegularSelectBar(EditView, false);
                    SetRegularSelectBar(position, SelectMethod, true);

                } else {
                    if (SelectMethod.equals("Song")) {
                        EditView = getLayoutInflater().inflate(R.layout.song_bar_object_small, null);
                        GetRegularSelectBar(EditView, false);
                        if (AppAppearance.equals("Dark")) {
                            SongBar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark2));
                        }
                    } else if (SelectMethod.equals("Image")) {
                        EditView = getLayoutInflater().inflate(R.layout.info_display_select_image, null);
                        GetRegularSelectBar(EditView, true);
                        EditList.setNumColumns(2);
                        EditList.setColumnWidth(EditView.getWidth());
                        //
                        //This may be what I am looking for
                        //
                    } else {
                        EditView = getLayoutInflater().inflate(R.layout.song_bar_object_big, null);
                        GetRegularSelectBar(EditView, false);
                        if (AppAppearance.equals("Dark")) {
                            SongBar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark2));
                        }
                    }
                    if (SelectMethod.equals("Image")) {
                        SetRegularSelectBar(SongLengthChecker.get(position), SelectMethod, false);
                    } else {
                        SetRegularSelectBar(SongLengthChecker.get(position - 1), SelectMethod, false);
                    }
                    SetSearchHeader();
                }

            } else {
                EditList.setNumColumns(1);
                //Display All editable fields
                EditView = getLayoutInflater().inflate(R.layout.info_display_edit_object, null);
                SearchBar.getLayoutParams().height = 0;
                switch (position) {
                    case 0:
                        EditView = getLayoutInflater().inflate(R.layout.info_display_edit_image, null);
                        GetEditView( EditView, position);
                        SetEditView(position);
                        break;
                    case 1:

                        GetEditView(EditView, position);
                        SetEditView(position);
                        break;
                    case 2:
                        GetEditView(EditView, position);
                        SetEditView(position);
                        break;
                    case 3:
                        GetEditView(EditView, position);
                        SetEditView(position);
                        break;
                        /*
                    case 4:
                        EditView = getLayoutInflater().inflate(R.layout.info_display_edit_extra_info, null);
                        GetEditView(EditView, position);
                        SetEditView(position);
                        break;

                         */
                }
            }
            return EditView;
        }
    }

    private class MiniPlayer extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onReceive(Context context, Intent intent) {

            String Action = intent.getAction();
            if ("AudioExist".equals(Action)) {
                SetMiniPlayer(intent);
            }

        }
    }
}
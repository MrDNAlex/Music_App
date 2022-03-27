
package com.example.dnamusicapp;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class PlaylistSongView extends AppCompatActivity {
    //
    //Clean All this shit up lol
    //

    boolean Android10;

    int GP;

    //Time ints
    int TotalSec;
    int Modulus;
    int Sec;
    int Min;
    int Hour;


    //Loop Numbers
    int SongNum;
    int ArtistNum;
    int AlbumNum;
    int VariableNumSong = 19;
    //PSID:  Song Name, Artist Name, Album Name, Song Path, Album Art, Playlist Rank, Universal Rank, Playlist Music Mood, Universal Music Mood, Playlist Music Tags, Universal Music Tags, Song Length,Playlist Times Played, Universal Times played, Last Time Played (date), AlbumID
    int VariableNumArtist = 16;
    //AID: Artist Name, Artist Playlist Rank,Artist Universal Rank, Artist Playlist Mood, Artist Universal Mood, Artist Playlist Tags, Artist Universal Tags, Artist Length, Artist Playlist Times Played, Artist Universal Times Played, Artist Playlist Total Time Played,  Artist Universal Total Time Played, Last Time Played (date), [SIDx, SIDx, SIDx, SIDx,.... ]
    int VariableNumAlbum = 18;
    //AlID: Album Name, Album Artist Name, Album Art, Album Playlist Rank, Album Universal Rank, Album Playlist Mood, Album Universal Mood, Album Playlist Tags, Album Universal Tags, Album Length, Album Playlist Times Played,  Album Universal Times Played, Album Playlist Total Time Played ,Album Universal Total Time Played, Last Time Played, AlbumID.  [SIDx, SIDx, SIDx, SIDx,....]

    //Add a total song completion percentage? and a total music playing time

    String[] SortMethodAll = {"Alphabetical", "Date", "Length", "Playlist Rank", "Universal Rank", "Tags", "Mood"};      //Remove tags and Moods in future?
    String[] ObjectMethodAll = {"Album", "Artist", "Song", "Playlist Rank", "Universal Rank", "Tags", "Mood"};

    ArrayList<ArrayList<String>> PlaylistSongID = new ArrayList<>();
    ArrayList<ArrayList<String>> PlaylistAlbumID = new ArrayList<>();
    ArrayList<ArrayList<String>> PlaylistArtistID = new ArrayList<>();

    ArrayList<String> AllTags = new ArrayList<>();
    ArrayList<String> AllMoods = new ArrayList<>();

    ArrayList<ArrayList<Integer>> SongLengthChecker = new ArrayList<>();
    ArrayList<Integer> SelectedQueue = new ArrayList<>();

    String ObjectType = "Small EI";
    String ObjectMethod;
    String SortMethod;
    String QueueMethod;
    String SearchTerm;
    String FilePath;
    String PlayName;
    String AppAppearance;

    SearchView SearchBar;

    ExpandableListView SongGrid;

    LinearLayout PlaylistSongViewLinear;

    //Object Header
    ImageView ObjectHeaderImage;

    TextView ObjectHeaderName;
    TextView ObjectHeaderArtist;
    TextView ObjectHeaderSongNum;
    TextView ObjectHeaderLength;

    LinearLayout ObjectBar;

    //Object Header
    TextView PlaylistName;
    TextView NumOfSongs;
    TextView PlaylistLength;

    ImageView PlaylistImage;

    //Button SortButton;

    //LinearLayout TagsHorizonView;
    //LinearLayout MoodHorizonView;
    //LinearLayout ArtistHorizonView;

    LinearLayout SortTypeDefault;
    LinearLayout ObjectTypeDefault;

    //Song Bars
    LinearLayout SongBar;

    ImageView SongImage;

    TextView SongName;
    TextView SongArtist;
    TextView SongLength;

    //Song Bar Extras
    TextView SongPR;
    TextView SongUR;

    //Horizontal Objects
    TextView TMAObject;

    //View
    View SongView;

    //Floating Menu Shit

    boolean PlayMenuOpen = false;
    boolean AddMenuOpen = false;

    BottomNavigationView BottomNavigation;

   MiniPlayer MiniPlayerReceiver = new MiniPlayer();

    LinearLayout MusicPlayerMini;


    //Adapters
    CustomAdapter1 ExpandableAdapterAll = new CustomAdapter1();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_song_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Android10 = true;
        } else {
            Android10 = false;
        }

        SetUpNav();
        AntennaSetup();
        CheckAudioPlayer();

        AccessSettings();
        SongGrid = findViewById(R.id.SongViewGrid);
        PlaylistSongViewLinear = findViewById(R.id.PlaylistSongViewLinear);
        MusicPlayerMini = findViewById(R.id.MusicPlayerMini);
        MusicPlayerMini.setVisibility(View.INVISIBLE);
        GetInfo();
        ReadPlaylistInfoFile();

        if (ObjectMethod == null || ObjectMethod == "") {
            ObjectMethod = "Album";          //Determines to use Array for songs, album or Artist or use tags mood ect
        }
        if (SortMethod == null || SortMethod == "") {
            SortMethod = "Alphabetical";     //Determines the method of sorting Alpha, Date, Length ect
        }

        SortEverything();
        GenSongLengthChecker();

        SongGrid.setAdapter(ExpandableAdapterAll);
        GetSearchHeader();
        SetSearchHeader();

        if (AppAppearance.equals("Dark")) {
            PlaylistSongViewLinear.setBackground(ContextCompat.getDrawable(this, R.drawable.background_alex3));
            // SongGrid.setBackground(ContextCompat.getDrawable(this, R.drawable.background_alex3));
        }

        //new Task1().execute();
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

    public void GetInfo() {
        Intent Stuff = getIntent();
        FilePath = Stuff.getStringExtra("FilePath");
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
        PlaylistArtistID.clear();
        PlaylistAlbumID.clear();
        AllMoods.clear();
        AllTags.clear();

        try {
            JSONObject InfoFile = new JSONObject(loadJsonFile(FilePath + ".json"));
            JSONArray TotalSongNum = InfoFile.getJSONArray("TotalSongNum");
            JSONArray TotalArtistNum = InfoFile.getJSONArray("TotalArtistNum");
            JSONArray TotalAlbumNum = InfoFile.getJSONArray("TotalAlbumNum");
            JSONArray DefaultObjectSize = InfoFile.getJSONArray("ObjectDisplaySize");
            JSONArray DefaultSortMethod = InfoFile.getJSONArray("SortMethod");
            JSONArray DefaultDisplayMethod = InfoFile.getJSONArray("ObjectDisplayMethod");
            JSONArray AllTagsArray = InfoFile.getJSONArray("AllTags");
            JSONArray AllMoodsArray = InfoFile.getJSONArray("AllMoods");
            JSONArray Name = InfoFile.getJSONArray("PlaylistName");

            SongNum = (int) TotalSongNum.get(0);
            ArtistNum = (int) TotalArtistNum.get(0);
            AlbumNum = (int) TotalAlbumNum.get(0);
            ObjectType = DefaultObjectSize.get(0).toString();
            PlayName = Name.get(0).toString();

            if (SortMethod == null) {
                SortMethod = DefaultSortMethod.get(0).toString();
            }

            if (ObjectMethod == null) {
                ObjectMethod = DefaultDisplayMethod.get(0).toString();
            }

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

            switch (ObjectMethod) {
                case "Song":
                    //Load All Song Info into 2D Array
                    for (int i = 0; i < SongNum; i++) {
                        JSONArray SID = InfoFile.getJSONArray("SID" + i);
                        for (int e = 0; e < VariableNumSong; e++) {
                            PlaylistSongID.get(e).add(SID.get(e).toString());
                        }
                    }
                    break;
                case "Artist":
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
                    break;
                case "Album":
                    //Load All Song Info into 2D Array
                    for (int i = 0; i < SongNum; i++) {
                        JSONArray SID = InfoFile.getJSONArray("SID" + i);
                        for (int e = 0; e < VariableNumSong; e++) {
                            PlaylistSongID.get(e).add(SID.get(e).toString());
                        }
                    }

                    //Load All Album Info into 2D Array
                    for (int i = 0; i < AlbumNum; i++) {
                        JSONArray AlID = InfoFile.getJSONArray("AlID" + i);
                        for (int e = 0; e < VariableNumAlbum; e++) {
                            PlaylistAlbumID.get(e).add(AlID.get(e).toString());
                        }
                    }
                    break;
                default:
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

                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void InitializeArrays() {
        //
        //Switch the numbers to the VariableNum Int value and maybe in the future have it detect the number by itself
        //

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

        switch (ObjectMethod) {

            case "Album":
                for (int i = 0; i < AlbumNum; i++) {
                    if (SearchTerm == null || SearchTerm.equals("")) {
                        SongLengthChecker.add(new ArrayList<Integer>());
                        for (int e = 0; e < SongNum; e++) {
                            //Double check with ID
                            if (PlaylistAlbumID.get(13).get(i).equals(PlaylistSongID.get(15).get(e))) {
                                SongLengthChecker.get(i).add(e);
                            }
                        }
                    } else {
                        SongLengthChecker.add(new ArrayList<Integer>());
                        if (PlaylistAlbumID.get(0).get(i).toLowerCase().contains(SearchTerm.toLowerCase()) || PlaylistAlbumID.get(1).get(i).toLowerCase().contains(SearchTerm.toLowerCase())) {
                            for (int e = 0; e < SongNum; e++) {
                                //Double check with ID
                                if (PlaylistAlbumID.get(13).get(i).equals(PlaylistSongID.get(15).get(e))) {
                                    SongLengthChecker.get(i).add(e);
                                }
                            }
                        }
                    }
                }
                break;
            case "Artist":
                for (int i = 0; i < ArtistNum; i++) {
                    if (SearchTerm == null || SearchTerm.equals("")) {
                        SongLengthChecker.add(new ArrayList<Integer>());
                        for (int e = 0; e < SongNum; e++) {
                            //Double check with ID
                            if (PlaylistArtistID.get(12).get(i).equals(PlaylistSongID.get(16).get(e))) {
                                SongLengthChecker.get(i).add(e);
                            }

                        }
                    } else {
                        SongLengthChecker.add(new ArrayList<Integer>());
                        if (PlaylistArtistID.get(0).get(i).toLowerCase().contains(SearchTerm.toLowerCase())) {
                            for (int e = 0; e < SongNum; e++) {
                                //Double check with ID
                                if (PlaylistArtistID.get(12).get(i).equals(PlaylistSongID.get(16).get(e))) {
                                    SongLengthChecker.get(i).add(e);
                                }
                            }
                        }
                    }

                }
                break;
            case "Song":
                for (int i = 0; i < 1; i++) {
                    if (SearchTerm == null || SearchTerm.equals("")) {
                        SongLengthChecker.add(new ArrayList<Integer>());
                        for (int e = 0; e < SongNum; e++) {
                            SongLengthChecker.get(i).add(e);
                        }
                    } else {
                        SongLengthChecker.add(new ArrayList<Integer>());
                        for (int e = 0; e < SongNum; e++) {
                            if (PlaylistSongID.get(0).get(e).toLowerCase().contains(SearchTerm.toLowerCase()) || PlaylistSongID.get(1).get(e).toLowerCase().contains(SearchTerm.toLowerCase())) {
                                SongLengthChecker.get(i).add(e);
                            }
                        }
                    }
                }
                break;
            case "Playlist Rank":
                //Have system that detects if rank is out of 10 or 100                  + 1 so that it reaches 10?
                for (int i = 0; i < 10 + 1; i++) {
                    SongLengthChecker.add(new ArrayList<Integer>());
                    for (int e = 0; e < SongNum; e++) {
                        if (PlaylistSongID.get(5).get(e).equals("" + i)) {
                            SongLengthChecker.get(i).add(e);
                        }
                    }
                }
                break;
            case "Universal Rank":
                //Have system that detects if rank is out of 10 or 100                  + 1 so that it reaches 10?
                for (int i = 0; i < 10 + 1; i++) {
                    SongLengthChecker.add(new ArrayList<Integer>());
                    for (int e = 0; e < SongNum; e++) {
                        if (PlaylistSongID.get(6).get(e).equals("" + i)) {
                            SongLengthChecker.get(i).add(e);
                        }
                    }
                }
                break;
            case "Tag":
                //Have array list for unique Tag         10 is temporary stand in
                for (int i = 0; i < 10; i++) {
                    SongLengthChecker.add(new ArrayList<Integer>());

                    for (int e = 0; e < SongNum; e++) {
                        if (PlaylistSongID.get(9).get(e).contains("Blah Blah" + ",") || PlaylistSongID.get(9).get(e).contains("Blah Blah" + "]")) {        //Replace blah blah with the array list for unique Tags
                            SongLengthChecker.get(i).add(e);
                        }
                    }
                }
                break;
            case "Mood":
                for (int i = 0; i < 10; i++) {
                    SongLengthChecker.add(new ArrayList<Integer>());

                    for (int e = 0; e < SongNum; e++) {
                        if (PlaylistSongID.get(7).get(i).contains("Blah Blah" + ",") || PlaylistSongID.get(7).get(i).contains("Blah Blah" + "]")) {
                            SongLengthChecker.get(i).add(e);
                        }
                    }
                }
                break;
        }

        //Clean Up
        int Loop = SongLengthChecker.size();
        for (int i = 0; i < Loop; i++) {
            for (int e = 0; e < SongLengthChecker.size(); e++) {
                if (SongLengthChecker.get(e).size() == 0) {
                    SongLengthChecker.remove(e);
                }
            }
        }


    }

    public void SortEverything() {

        switch (ObjectMethod) {
            case "Album":
                switch (SortMethod) {
                    case "Alphabetical":
                        SortArray(0, "Album");
                        break;
                    case "Date":
                        SortArray(12, "Album");
                        break;
                    case "Length":
                        SortArray(9, "Album");
                        break;
                    case "Playlist Rank":
                        SortArray(3, "Album");
                        break;
                    case "Universal Rank":
                        SortArray(4, "Album");
                        break;
                    case "Tags":
                        SortArray(1, "Album");
                        //Make a custom thing here
                        break;
                    case "Mood":
                        SortArray(1, "Album");
                        //Make a custom thing here
                        break;
                    default:
                        SortArray(0, "Album");
                        break;
                }
                break;
            case "Artist":
                switch (SortMethod) {

                    case "Alphabetical":
                        SortArray(0, "Artist");
                        break;
                    case "Date":
                        SortArray(10, "Artist");
                        break;
                    case "Length":
                        SortArray(8, "Artist");
                        break;
                    case "Playlist Rank":
                        SortArray(2, "Artist");
                        break;
                    case "Universal Rank":
                        SortArray(3, "Artist");
                        break;
                    case "Tags":
                        SortArray(2, "Artist");
                        //Make a custom thing here
                        break;
                    case "Mood":
                        SortArray(2, "Artist");
                        //Make a custom thing here
                        break;
                    default:
                        SortArray(0, "Artist");
                        break;
                }
                break;
            case "Song":
                switch (SortMethod) {

                    case "Alphabetical":
                        SortArray(0, "Song");
                        break;
                    case "Date":
                        SortArray(14, "Song");
                        break;
                    case "Length":
                        SortArray(11, "Song");
                        break;
                    case "Playlist Rank":
                        SortArray(5, "Song");
                        break;
                    case "Universal Rank":
                        SortArray(6, "Song");
                        break;
                    case "Tags":
                        SortArray(1, "Song");
                        //Make a custom thing here
                        break;
                    case "Mood":
                        SortArray(1, "Song");
                        //Make a custom thing here
                        break;
                    default:
                        SortArray(0, "Song");
                        break;
                }
                break;
            case "Playlist Rank":
                switch (SortMethod) {

                    case "Alphabetical":
                        SortArray(0, "Song");
                        break;
                    case "Date":
                        SortArray(14, "Song");
                        break;
                    case "Length":
                        SortArray(11, "Song");
                        break;
                    case "Universal Rank":
                        SortArray(6, "Song");
                        break;
                    case "Tags":
                        SortArray(1, "Song");
                        //Make a custom thing here
                        break;
                    case "Mood":
                        SortArray(1, "Song");
                        //Make a custom thing here
                        break;
                    default:
                        SortArray(0, "Song");
                        break;
                }
                break;
            case "Universal Rank":
                switch (SortMethod) {

                    case "Alphabetical":
                        SortArray(0, "Song");
                        break;
                    case "Date":
                        SortArray(14, "Song");
                        break;
                    case "Length":
                        SortArray(11, "Song");
                        break;
                    case "Playlist Rank":
                        SortArray(5, "Song");
                        break;
                    case "Tags":
                        SortArray(1, "Song");
                        //Make a custom thing here
                        break;
                    case "Mood":
                        SortArray(1, "Song");
                        //Make a custom thing here
                        break;
                    default:
                        SortArray(0, "Song");
                        break;

                }
                break;
            case "Mood":
                switch (SortMethod) {

                    case "Alphabetical":
                        SortArray(0, "Song");
                        break;
                    case "Date":
                        SortArray(14, "Song");
                        break;
                    case "Length":
                        SortArray(11, "Song");
                        break;
                    case "Playlist Rank":
                        SortArray(5, "Song");
                        break;
                    case "Universal Rank":
                        SortArray(6, "Song");
                        break;
                    case "Tags":
                        SortArray(1, "Song");
                        //Make a custom thing here
                        break;
                    default:
                        SortArray(0, "Song");
                        break;

                }
                break;
            case "Tag":
                switch (SortMethod) {

                    case "Alphabetical":
                        SortArray(0, "Song");
                        break;
                    case "Date":
                        SortArray(14, "Song");
                        break;
                    case "Length":
                        SortArray(11, "Song");
                        break;
                    case "Playlist Rank":
                        SortArray(5, "Song");
                        break;
                    case "Universal Rank":
                        SortArray(6, "Song");
                        break;
                    case "Mood":
                        SortArray(1, "Song");
                        //Make a custom thing here
                        break;
                    default:
                        SortArray(0, "Song");
                        break;
                }
                break;
            default:
                SortArray(0, "Song");
                switch (SortMethod) {
                    case "Alphabetical":
                        SortArray(0, "Song");
                        break;
                    case "Date":
                        SortArray(14, "Song");
                        break;
                    case "Length":
                        SortArray(11, "Song");
                        break;
                    case "Playlist Rank":
                        SortArray(5, "Song");
                        break;
                    case "Universal Rank":
                        SortArray(6, "Song");
                        break;
                    case "Tags":
                        SortArray(1, "Song");
                        //Make a custom thing here
                        break;
                    case "Mood":
                        SortArray(1, "Song");
                        //Make a custom thing here
                        break;
                    default:
                        SortArray(0, "Song");
                        break;
                }
                break;

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
                int hi = 0;
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
                SongGrid.setAdapter(ExpandableAdapterAll);

                return false;
            }
        });

    }

    public void FindIds(View view) {
        //TextViews
        PlaylistName = view.findViewById(R.id.PlaylistNameText);
        NumOfSongs = view.findViewById(R.id.NumOfSongs);
        PlaylistLength = view.findViewById(R.id.PlaylistLength);

        //TagsHorizonText = view.findViewById(R.id.TagsHorizonText);
        //MoodHorizonText = view.findViewById(R.id.MoodHorizonText);
        //ArtistHorizonText = view.findViewById(R.id.PRArtistHorizonText);
        //ImageButtons
        PlaylistImage = view.findViewById(R.id.PlaylistImage);
        //Buttons
        //SortButton = view.findViewById(R.id.SortButton);
        //LinearLayout
        //TagsHorizonView = view.findViewById(R.id.TagsHorizontalScrollView);
        //MoodHorizonView = view.findViewById(R.id.MoodHorizontalScrollView);
        SortTypeDefault = view.findViewById(R.id.SortTypeDefault);
        ObjectTypeDefault = view.findViewById(R.id.ObjectTypeDefault);


        //ArtistHorizonView = view.findViewById(R.id.ArtistHorizontalScrollView);
    }

    public void SetHeaderInfo() {

        Glide.with(this).load(R.drawable.dfltplayimg).placeholder(R.drawable.placeholder).fitCenter().into(PlaylistImage);

        PlaylistName.setText(PlayName);

        if (SongNum == 1) {
            NumOfSongs.setText(SongNum + " Song");
        } else {
            NumOfSongs.setText(SongNum + " Songs");
        }

        //Set total length in seconds
        TotalSec = 0;
        for (int i = 0; i < SongNum; i++) {
            TotalSec = TotalSec + Integer.parseInt(PlaylistSongID.get(11).get(i));
        }

        PlaylistLength.setText(ClockDisplay(TotalSec));


        if (AppAppearance.equals("Dark")) {
            PlaylistImage.setImageResource(R.drawable.dfltplayimgdark);
            PlaylistName.setTextColor(Color.rgb(255, 255, 255));
            NumOfSongs.setTextColor(Color.rgb(255, 255, 255));
            PlaylistLength.setTextColor(Color.rgb(255, 255, 255));
        } else {
            PlaylistImage.setImageResource(R.drawable.dfltplayimg);
        }

        for (int i = 0; i < SortMethodAll.length; i++) {
            View Button;
            if (SortMethodAll[i].equals(SortMethod)) {
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
            SortType.setText(SortMethodAll[i]);
            if (AppAppearance.equals("Dark")) {
                SortType.setTextColor(Color.rgb(255, 255, 255));
            }
            final int finalI = i;
            Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SortMethod = SortMethodAll[finalI];
                    ReadPlaylistInfoFile();
                    SortEverything();
                    GenSongLengthChecker();
                    SongGrid.setAdapter(ExpandableAdapterAll);
                }
            });
            SortTypeDefault.addView(Button);
        }

        for (int i = 0; i < ObjectMethodAll.length; i++) {
            View Button;
            if (ObjectMethodAll[i].equals(ObjectMethod)) {
                if (AppAppearance.equals("Dark")) {
                    Button = getLayoutInflater().inflate(R.layout.horizontal_view_object_selected, null);
                    LinearLayout Background = Button.findViewById(R.id.TMAHorizonViewObject);
                    Background.setBackground(ContextCompat.getDrawable(this, R.drawable.object_selectdark));
                } else {
                    Button = getLayoutInflater().inflate(R.layout.horizontal_view_object_selected, null);
                }

            } else {
                if (AppAppearance.equals("Dark")) {
                    Button = getLayoutInflater().inflate(R.layout.horizontal_view_object, null);
                    LinearLayout Background = Button.findViewById(R.id.TMAHorizonViewObject);
                    Background.setBackground(ContextCompat.getDrawable(this, R.drawable.objectdark));
                } else {
                    Button = getLayoutInflater().inflate(R.layout.horizontal_view_object, null);
                }

            }
            TextView SortType = Button.findViewById(R.id.TMAObject);
            SortType.setText(ObjectMethodAll[i]);
            if (AppAppearance.equals("Dark")) {
                SortType.setTextColor(Color.rgb(255, 255, 255));
            }
            final int finalI = i;
            Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ObjectMethod = ObjectMethodAll[finalI];
                    ReadPlaylistInfoFile();
                    SortEverything();
                    GenSongLengthChecker();
                    SongGrid.setAdapter(ExpandableAdapterAll);
                }
            });
            ObjectTypeDefault.addView(Button);
        }
    }

    public void GetExtraInfoBarsSongs(View view) {
        GetRegularBarsSongs(view);
        SongPR = view.findViewById(R.id.SongPlaylistRank);
        SongUR = view.findViewById(R.id.SongUniversalRank);
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
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetRegularInfoSongs(int i) {
        //SongImage.setImageResource(Integer.parseInt(PlaylistSongID.get(4).get(i)));

        GlideDisplay(i, SongImage, "Song");
        SongName.setText(Title(PlaylistSongID.get(0).get(i)));
        SongArtist.setText(Title(PlaylistSongID.get(1).get(i)));
        SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(11).get(i))));

        if (AppAppearance.equals("Dark")) {
            SongName.setTextColor(Color.rgb(255, 255, 255));
            SongArtist.setTextColor(Color.rgb(255, 255, 255));
            SongLength.setTextColor(Color.rgb(255, 255, 255));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetExtraInfoSongs(int i) {
        SetRegularInfoSongs(i);
        SongPR.setText(PlaylistSongID.get(5).get(i));
        SongUR.setText(PlaylistSongID.get(6).get(i));

        if (AppAppearance.equals("Dark")) {
            SongPR.setTextColor(Color.rgb(255, 255, 255));
            SongUR.setTextColor(Color.rgb(255, 255, 255));
        }
    }

    public void GetObjectID(View view) {
        ObjectHeaderImage = view.findViewById(R.id.ObjectHeaderImage);

        ObjectHeaderName = view.findViewById(R.id.ObjectHeaderName);
        ObjectHeaderArtist = view.findViewById(R.id.ObjectHeaderArtist);
        ObjectHeaderSongNum = view.findViewById(R.id.ObjectHeaderSongNum);
        ObjectHeaderLength = view.findViewById(R.id.ObjectHeaderPlaylistLength);
        ObjectBar = view.findViewById(R.id.ObjectBar);

        if (AppAppearance.equals("Dark")) {
            ObjectHeaderName.setTextColor(Color.rgb(255, 255, 255));
            ObjectHeaderArtist.setTextColor(Color.rgb(255, 255, 255));
            ObjectHeaderSongNum.setTextColor(Color.rgb(255, 255, 255));
            ObjectHeaderLength.setTextColor(Color.rgb(255, 255, 255));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetObjectID(int i) {
        //Wait a second I can probably remove all of the for loops and if statements I think since SongLengthChecker already does all this sorting shit for us

        //
        //Something is not working somewhere here when it changes to different ObjectMethods it doesn't do the right things
        //
        switch (ObjectMethod) {
            case "Album":
                for (int e = 0; e < PlaylistAlbumID.get(0).size(); e++) {
                    if (PlaylistAlbumID.get(13).get(e).equals(PlaylistSongID.get(15).get(SongLengthChecker.get(i - 1).get(0)))) {
                        ObjectHeaderName.setText(PlaylistAlbumID.get(0).get(e));
                        ObjectHeaderArtist.setText(PlaylistAlbumID.get(1).get(e));
                        GlideDisplay(e, ObjectHeaderImage, "Album");

                    }
                }
                ObjectID(i);
                break;
            case "Artist":
                for (int e = 0; e < PlaylistArtistID.get(0).size(); e++) {
                    if (PlaylistArtistID.get(12).get(e).equals(PlaylistSongID.get(16).get(SongLengthChecker.get(i - 1).get(0)))) {
                        ObjectHeaderName.setText(PlaylistArtistID.get(0).get(e));
                        ObjectHeaderArtist.setText("");
                        GlideDisplay(e, ObjectHeaderImage, "Artist");
                    }
                }
                ObjectID(i);
                break;
            case "Song":
                //Remove this later?
                ObjectID(i);
                break;
            case "Playlist Rank":
                //Change later to variable for rank out of 100 or 10 or whatever
                ObjectHeaderName.setText("Playlist Rank " + (i - 1));
                ObjectID(i);
                break;
            case "Universal Rank":
                ObjectHeaderName.setText("Universal Rank " + (i - 1));
                ObjectID(i);
                break;
            case "Tag":
                String TagName = "";
                ObjectHeaderName.setText(TagName);
                ObjectID(i);
                break;
            case "Mood":
                String MoodName = "";
                ObjectHeaderName.setText(MoodName);
                ObjectID(i);
                break;
        }
    }

    public void ObjectID(int i) {

        int TotalTimeSec = 0;
        Hour = 0;
        Min = 0;
        Sec = 0;

        for (int e = 0; e < SongLengthChecker.get(i - 1).size(); e++) {
            TotalTimeSec = TotalTimeSec + Integer.parseInt(PlaylistSongID.get(11).get(SongLengthChecker.get(i - 1).get(e)));
        }

        if (SongLengthChecker.get(i - 1).size() == 1) {
            ObjectHeaderSongNum.setText(SongLengthChecker.get(i - 1).size() + " Song");
        } else {
            ObjectHeaderSongNum.setText(SongLengthChecker.get(i - 1).size() + " Songs");
        }

        ObjectHeaderLength.setText(ClockDisplay(TotalTimeSec));

    }

    public void PlayQueue(int GP, int CP) {

        Intent intent = new Intent(getApplicationContext(), MusicPlayer.class);
        //Ok so something we are sending is too big, limit to send is 1 mb. Alright so I need to chop up all of the loops into smaller bits to fix this. Or worse case scenario just copy the grab from file void.

        //
        //Alright so yeah we are going to have to just recall the void for getting all the info out of the playlist file
        //

        for (int i = 0; i < SongLengthChecker.size(); i++) {
            intent.putExtra("SongChecker" + i, SongLengthChecker.get(i));
        }

        //For selected queue specify what was selected (AlbumID#, ArtistID#, SongID#, or Playlist)
        intent.putExtra("SelectQueue", SelectedQueue);
        intent.putExtra("QueueMethod", QueueMethod);
        intent.putExtra("GroupPos", GP);
        intent.putExtra("ChildPos", CP);

        intent.putExtra("SongCheckerLength", SongLengthChecker.size());
        intent.putExtra("ObjectMethod", ObjectMethod);
        intent.putExtra("SortMethod", SortMethod);
        intent.putExtra("Playlist", true);
        intent.putExtra("FilePath", FilePath);
        intent.putExtra("MiniPlayerStart", false);

        //This stops the music and resets for a new queue
        stopService(new Intent(this, AudioPlayer.class));

        startActivity(intent);

    }

    public void SendInfo(int GPos, int CPos, String ObjectMode) {
        boolean Playlist = true;

        Intent InfoDisplay = new Intent(getApplicationContext(), InfoDisplay.class);

        InfoDisplay.putExtra("PlaylistName", FilePath);
        InfoDisplay.putExtra("Playlist", true);

        switch (ObjectMode) {
            case "Song":

                if (Playlist) {
                    InfoDisplay.putExtra("FileID", PlaylistSongID.get(18).get(SongLengthChecker.get(GPos).get(CPos)));
                    for (int i = 0; i < PlaylistSongID.size(); i++) {
                        InfoDisplay.putExtra("Value" + i, PlaylistSongID.get(i).get(SongLengthChecker.get(GPos).get(CPos)));
                    }

                } else {
                    InfoDisplay.putExtra("FileID", PlaylistSongID.get(15).get(SongLengthChecker.get(GPos).get(CPos)));
                    for (int i = 0; i < PlaylistSongID.size(); i++) {
                        InfoDisplay.putExtra("Value" + i, PlaylistSongID.get(i).get(SongLengthChecker.get(GPos).get(CPos)));
                    }
                }
                break;
            case "Artist":
                if (Playlist) {
                    String ArtistID = PlaylistSongID.get(16).get(SongLengthChecker.get(GPos).get(CPos)); //CPos should be 0
                    for (int i = 0; i < ArtistNum; i++) {
                        if (PlaylistArtistID.get(12).get(i).equals(ArtistID)) {
                            InfoDisplay.putExtra("FileID", PlaylistArtistID.get(14).get(i));
                            for (int e = 0; e < PlaylistArtistID.size(); e++) {
                                InfoDisplay.putExtra("Value" + e, PlaylistArtistID.get(e).get(i));
                            }
                        }
                    }
                } else {
                    String ArtistID = PlaylistSongID.get(13).get(SongLengthChecker.get(GPos).get(CPos)); //CPos should be 0
                    for (int i = 0; i < ArtistNum; i++) {
                        if (PlaylistArtistID.get(9).get(i).equals(ArtistID)) {
                            InfoDisplay.putExtra("FileID", PlaylistArtistID.get(11).get(i));
                            for (int e = 0; e < PlaylistArtistID.size(); e++) {
                                InfoDisplay.putExtra("Value" + e, PlaylistArtistID.get(e).get(i));
                            }
                        }
                    }
                }
                break;
            case "Album":
                if (Playlist) {
                    String AlbumID = PlaylistSongID.get(15).get(SongLengthChecker.get(GPos).get(CPos)); //CPos should be 0
                    for (int i = 0; i < AlbumNum; i++) {
                        if (PlaylistAlbumID.get(13).get(i).equals(AlbumID)) {
                            InfoDisplay.putExtra("FileID", PlaylistAlbumID.get(16).get(i));
                            for (int e = 0; e < PlaylistAlbumID.size(); e++) {
                                InfoDisplay.putExtra("Value" + e, PlaylistAlbumID.get(e).get(i));
                            }
                        }
                    }
                } else {
                    String AlbumID = PlaylistSongID.get(12).get(SongLengthChecker.get(GPos).get(CPos)); //CPos should be 0
                    for (int i = 0; i < AlbumNum; i++) {
                        if (PlaylistAlbumID.get(10).get(i).equals(AlbumID)) {
                            InfoDisplay.putExtra("FileID", PlaylistAlbumID.get(13).get(i));
                            for (int e = 0; e < PlaylistAlbumID.size(); e++) {
                                InfoDisplay.putExtra("Value" + e, PlaylistAlbumID.get(e).get(i));
                            }
                        }
                    }
                }
                break;
        }
        startActivity(InfoDisplay);
    }



    public Bitmap getAlbumart(Long album_id) {
        Bitmap bm = null;
        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = getApplicationContext().getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void GlideDisplay(int i, ImageView Image, String Type) {

        if (Android10) {
            Bitmap bitmap = null;
            try {

                switch (Type) {
                    case "Album":
                        bitmap = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(PlaylistAlbumID.get(2).get(i)), new Size(256, 256), null);
                        // bitmap = getAlbumart(Long.parseLong(PlaylistAlbumID.get(13).get(i)));
                        break;
                    case "Artist":
                        bitmap = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(PlaylistArtistID.get(1).get(i)), new Size(256, 256), null);
                        break;
                    case "Song":
                        bitmap = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(PlaylistSongID.get(4).get(i)), new Size(100, 100), null);
                        // bitmap = getAlbumart(Long.parseLong(PlaylistSongID.get(15).get(i)));
                        break;
                }

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

    //
//All Expandable Adapter
//
    private class CustomAdapter1 extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            if (ObjectMethod.equals("Song")) {
                return SongLengthChecker.size();
            } else {
                return SongLengthChecker.size() + 1;
            }
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            if (ObjectMethod.equals("Song")) {
                return SongLengthChecker.get(groupPosition).size();
            } else if (groupPosition > 0) {
                return SongLengthChecker.get(groupPosition - 1).size();
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
        public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
            View HeaderView = getLayoutInflater().inflate(R.layout.playlist_object_header, null);

            if (groupPosition == 0) {
                //Playlist Header
                HeaderView = getLayoutInflater().inflate(R.layout.playlist_song_view_header, null);
                FindIds(HeaderView);
                SetHeaderInfo();
                //TMAGrid.setAdapter(TMAAdapter);
                SongGrid.expandGroup(groupPosition);
            } else {
                if (ObjectMethod.equals("Song")) {
                    //Do nothing
                } else {
                    GP = groupPosition;
                    //Album Header
                    if (HeaderView != getLayoutInflater().inflate(R.layout.playlist_object_header, parent, false)) {
                        HeaderView = getLayoutInflater().inflate(R.layout.playlist_object_header, parent, false);
                        GetObjectID(HeaderView);
                    }
                    SetObjectID(groupPosition);
                    if (ObjectHeaderImage != null) {
                        ObjectHeaderImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SendInfo(groupPosition - 1, 0, ObjectMethod);
                            }
                        });
                    }

                    if (AppAppearance.equals("Dark")) {
                        ObjectBar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark2));
                    }

                    ObjectBar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isExpanded) {
                                SongGrid.collapseGroup(groupPosition);
                            } else {
                                SongGrid.expandGroup(groupPosition, true);
                                //SongGrid.setSelection(groupPosition);
                            }
                        }
                    });
                }
            }


            return HeaderView;
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            //SongView = getLayoutInflater().inflate(R.layout.song_bar_object_small_extrainfo, null);
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

            if (AppAppearance.equals("Dark")) {
                SongBar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark2));
            }

            if (ObjectMethod.equals("Song")) {

                switch (ObjectType) {
                    case "Small EI":
                        SetExtraInfoSongs(SongLengthChecker.get(groupPosition).get(childPosition));
                        if (SongImage != null) {
                            SongImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SendInfo(groupPosition, childPosition, "Song");
                                }
                            });
                        }
                        SongBar.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {
                                //
                                //Play song and shit
                                //
                                PlayQueue(groupPosition, childPosition);

                            }
                        });
                        break;
                    case "Small":
                        SetRegularInfoSongs(SongLengthChecker.get(groupPosition).get(childPosition));
                        if (SongImage != null) {
                            SongImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SendInfo(groupPosition, childPosition, "Song");
                                }
                            });
                        }
                        SongBar.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {
                                //
                                //Play song and shit
                                //
                                PlayQueue(groupPosition, childPosition);

                            }
                        });
                        break;
                    case "Big EI":
                        SetExtraInfoSongs(SongLengthChecker.get(groupPosition).get(childPosition));
                        if (SongImage != null) {
                            SongImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SendInfo(groupPosition, childPosition, "Song");
                                }
                            });
                        }
                        SongBar.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {
                                //
                                //Play song and shit
                                //
                                PlayQueue(groupPosition, childPosition);

                            }
                        });
                        break;
                    case "Big":
                        SetRegularInfoSongs(SongLengthChecker.get(groupPosition).get(childPosition));
                        if (SongImage != null) {
                            SongImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SendInfo(groupPosition, childPosition, "Song");
                                }
                            });
                        }
                        SongBar.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {
                                //
                                //Play song and shit
                                //
                                PlayQueue(groupPosition, childPosition);

                            }
                        });
                        break;
                }

            } else {

                switch (ObjectType) {
                    case "Small EI":
                        SetExtraInfoSongs(SongLengthChecker.get(groupPosition - 1).get(childPosition));
                        if (SongImage != null) {
                            SongImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SendInfo(groupPosition - 1, childPosition, "Song");
                                }
                            });
                        }
                        SongBar.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {

                                PlayQueue(groupPosition - 1, childPosition);

                            }
                        });
                        break;
                    case "Small":
                        SetRegularInfoSongs(SongLengthChecker.get(groupPosition - 1).get(childPosition));
                        if (SongImage != null) {
                            SongImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SendInfo(groupPosition - 1, childPosition, "Song");
                                }
                            });
                        }
                        SongBar.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {

                                PlayQueue(groupPosition - 1, childPosition);

                            }
                        });
                        break;
                    case "Big EI":
                        SetExtraInfoSongs(SongLengthChecker.get(groupPosition - 1).get(childPosition));
                        if (SongImage != null) {
                            SongImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SendInfo(groupPosition - 1, childPosition, "Song");
                                }
                            });
                        }
                        SongBar.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {

                                PlayQueue(groupPosition - 1, childPosition);

                            }
                        });
                        break;
                    case "Big":
                        SetRegularInfoSongs(SongLengthChecker.get(groupPosition - 1).get(childPosition));
                        if (SongImage != null) {
                            SongImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SendInfo(groupPosition - 1, childPosition, "Song");
                                }
                            });
                        }
                        SongBar.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {

                                PlayQueue(groupPosition - 1, childPosition);

                            }
                        });
                        break;
                }
            }

            return SongView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
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




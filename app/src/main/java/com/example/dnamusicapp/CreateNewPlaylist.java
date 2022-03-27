package com.example.dnamusicapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class CreateNewPlaylist extends AppCompatActivity {

    boolean Playlist = false;
    boolean AddToPlaylist = false;      //Going to have to come back to this

    int MasterVariableNumSong = 16;
    int MasterVariableNumArtist = 13;
    int MasterVariableNumAlbum = 15;

    //Loop Numbers
    int SongNum;
    int ArtistNum;
    int AlbumNum;

    //Time ints
    int TotalSec;
    int Modulus;
    int Sec;
    int Min;
    int Hour;

    ArrayList<Integer> SelectedSongs = new ArrayList<>();

    ArrayList<ArrayList<String>> PlaylistSongID = new ArrayList<>();
    ArrayList<ArrayList<String>> PlaylistAlbumID = new ArrayList<>();
    ArrayList<ArrayList<String>> PlaylistArtistID = new ArrayList<>();

    ArrayList<String> AllTags = new ArrayList<>();
    ArrayList<String> AllMoods = new ArrayList<>();

    ArrayList<ArrayList<Integer>> SongLengthChecker = new ArrayList<>();

    String ObjectMethod;
    String SortMethod;
    String AppAppearance;

    String[] SortMethodAll = {"Alphabetical", "Date", "Length", "Playlist Rank", "Universal Rank", "Tags", "Mood"};      //Remove tags and Moods in future?
    String[] ObjectMethodAll = {"Album", "Artist", "Song", "Playlist Rank", "Universal Rank", "Tags", "Mood"};

    String SearchTerm = "";

    //New Playlist Header
    String NewPlaylistName;
    String SortMethodDefault;
    String ObjectMethodDefault;

    //Song Bars
    LinearLayout SongBar;

    ImageView SongImage;
    ImageView SongSelected;

    TextView SongName;
    TextView SongArtist;
    TextView SongLength;

    //First Page
    SearchView SearchBar;
    //Button SortButton;
    GridView AllObjects;
    LinearLayout SortTypeSongs;

    //Second Page
    GridView SelectedObjects;
    Button GenPlaylistButton;

    //Gen/Edit Playlist Header
    ImageButton PlaylistImage;
    TextView PlaylistNameText;
    TextView NumOfSongs;
    TextView PlaylistLength;
    LinearLayout SortTypeDefault;
    LinearLayout ObjectTypeDefault;


    //Adapters
    CustomAdapter1 SearchScreen = new CustomAdapter1();
    CustomAdapter2 SelectedScreen = new CustomAdapter2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_playlist);
        AccessSettings();
        RelativeLayout CreateNewPlaylistBackground = findViewById(R.id.CreateNewPlaylistBackground);

        if (AppAppearance.equals("Dark")) {
            CreateNewPlaylistBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.background_alex3));
        }

        AllObjects = findViewById(R.id.AllObjects);
        SelectedObjects = findViewById(R.id.SelectedObjects);
        GenPlaylistButton = findViewById(R.id.GenPlaylistButton);

        TabHost tabHost = findViewById(R.id.tabhost);

        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Add Music");
        spec.setIndicator("Add Music");
        spec.setContent(R.id.Search);
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Selected Music");
        spec.setIndicator("Selected Music");

        spec.setContent(R.id.SelectedObjects);
        tabHost.addTab(spec);

        if (ObjectMethod == null) {
            ObjectMethod = "Song";          //Determines to use Array for songs, album or Artist or use tags mood ect
        }
        if (SortMethod == null) {
            SortMethod = "Alphabetical";     //Determines the method of sorting Alpha, Date, Length ect
        }

        GetHeader();
        SetHeader();

        ReadPlaylistInfoFile();

        SortEverything();

        GenSongLengthChecker();

        //AllObjects.setAdapter(SearchScreen);
        AllObjects.setAdapter(SearchScreen);
        SelectedObjects.setAdapter(SelectedScreen);

        GeneratePlaylist();
        //maybe make this page only song display

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

    public String loadJsonFile(String Filename) {
        String json;
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

    public void InitializeArrays() {
        //
        //Switch the numbers to the VariableNum Int value and maybe in the future have it detect the number by itself
        //

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

    public void GenSongLengthChecker() {

        SongLengthChecker.clear();

        //
        //Hmm this would only work if it was sorted alphabetically (Artist and Album) which means we would have to store SIDx inside the arrays, or wait does it need it? eh either way probably safe to add
        //

        //Going to have to come back to this to make it if (Playlist)

        //
        //Actually we might not need this here
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
                        if (PlaylistAlbumID.get(0).get(i).toLowerCase().contains(SearchTerm.toLowerCase()) || PlaylistAlbumID.get(1).get(i).toLowerCase().contains(SearchTerm.toLowerCase())) {
                            SongLengthChecker.add(new ArrayList<Integer>());
                            for (int e = 0; e < SongNum; e++) {
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
                            if (PlaylistArtistID.get(12).get(i).equals(PlaylistSongID.get(16).get(e))) {
                                SongLengthChecker.get(i).add(e);
                            }
                        }
                    } else {
                        if (PlaylistArtistID.get(0).get(i).toLowerCase().contains(SearchTerm.toLowerCase())) {
                            SongLengthChecker.add(new ArrayList<Integer>());
                            for (int e = 0; e < SongNum; e++) {
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
                            if (PlaylistSongID.get(0).get(e).toLowerCase().contains(SearchTerm.toLowerCase()) || PlaylistSongID.get(1).get(e).toLowerCase().contains(SearchTerm.toLowerCase()) || PlaylistSongID.get(2).get(e).toLowerCase().contains(SearchTerm.toLowerCase())) {
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
                        if (PlaylistSongID.get(5).get(e).contains("" + i)) {
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
                        if (PlaylistSongID.get(6).get(e).contains("" + i)) {
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
        //
        //Will need to expand upon
        //
        if (Playlist) {
            //Playlist
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
                            SortArray(11, "Artist");
                            break;
                        case "Length":
                            SortArray(7, "Artist");
                            break;
                        case "Playlist Rank":
                            SortArray(1, "Artist");
                            break;
                        case "Universal Rank":
                            SortArray(2, "Artist");
                            break;
                        case "Tags":
                            SortArray(1, "Artist");
                            //Make a custom thing here
                            break;
                        case "Mood":
                            SortArray(1, "Artist");
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
        } else {
            //Master File
            switch (ObjectMethod) {
                case "Album":
                    switch (SortMethod) {
                        case "Alphabetical":
                            SortArray(0, "Album");
                            break;
                        case "Date":
                            SortArray(9, "Album");
                            break;
                        case "Length":
                            SortArray(6, "Album");
                            break;
                        case "Universal Rank":
                            SortArray(3, "Album");
                            break;
                        case "Tags":
                            SortArray(5, "Album");
                            //Make a custom thing here
                            break;
                        case "Mood":
                            SortArray(4, "Album");
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
                            SortArray(8, "Artist");
                            break;
                        case "Length":
                            SortArray(5, "Artist");
                            break;
                        case "Universal Rank":
                            SortArray(3, "Artist");
                            break;
                        case "Tags":
                            SortArray(4, "Artist");
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
                            SortArray(10, "Song");
                            break;
                        case "Length":
                            SortArray(8, "Song");
                            break;
                        case "Universal Rank":
                            SortArray(5, "Song");
                            break;
                        case "Tags":
                            SortArray(7, "Song");
                            //Make a custom thing here
                            break;
                        case "Mood":
                            SortArray(6, "Song");
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
                            SortArray(10, "Song");
                            break;
                        case "Length":
                            SortArray(8, "Song");
                            break;
                        case "Universal Rank":
                            SortArray(5, "Song");
                            break;
                        case "Tags":
                            SortArray(7, "Song");
                            //Make a custom thing here
                            break;
                        case "Mood":
                            SortArray(6, "Song");
                            //Make a custom thing here
                            break;
                        default:
                            SortArray(0, "Song");
                            break;
                    }
                    break;

            }
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
                    if (Playlist) {
                        Combined.add(PlaylistAlbumID.get(position).get(i) + "  " + PlaylistAlbumID.get(15).get(i));
                    } else {
                        Combined.add(PlaylistAlbumID.get(position).get(i) + "  " + PlaylistAlbumID.get(10).get(i));
                    }
                }

                Collections.sort(Combined);
                Loop = PlaylistAlbumID.get(position).size();

                //Determine Old Placements
                for (int i = 0; i < Loop; i++) {
                    for (int e = 0; e < Loop; e++) {
                        if (Playlist) {
                            if (Combined.get(i).contains(Copy.get(position).get(e)) && Combined.get(i).contains(Copy.get(15).get(e)) && (!oldPlacement.contains(e))) {
                                oldPlacement.add(e);
                            }
                        } else {
                            if (Combined.get(i).contains(Copy.get(position).get(e)) && Combined.get(i).contains(Copy.get(10).get(e)) && (!oldPlacement.contains(e))) {
                                oldPlacement.add(e);
                            }
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
                    if (Playlist) {
                        Combined.add(PlaylistArtistID.get(position).get(i) + "  " + PlaylistArtistID.get(12).get(i));
                    } else {
                        Combined.add(PlaylistArtistID.get(position).get(i) + "  " + PlaylistArtistID.get(9).get(i));
                    }

                }

                Collections.sort(Combined);
                Loop = PlaylistArtistID.get(position).size();

                //Determine Old Placements
                for (int i = 0; i < Loop; i++) {
                    for (int e = 0; e < Loop; e++) {
                        if (Playlist) {
                            if (Combined.get(i).contains(Copy.get(position).get(e)) && Combined.get(i).contains(Copy.get(12).get(e)) && (!oldPlacement.contains(e))) {
                                oldPlacement.add(e);
                            }
                        } else {
                            if (Combined.get(i).contains(Copy.get(position).get(e)) && Combined.get(i).contains(Copy.get(9).get(e)) && (!oldPlacement.contains(e))) {
                                oldPlacement.add(e);
                            }
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
                    if (Playlist) {
                        Combined.add(PlaylistSongID.get(position).get(i) + "  " + PlaylistSongID.get(15).get(i));
                    } else {
                        Combined.add(PlaylistSongID.get(position).get(i) + "  " + PlaylistSongID.get(12).get(i));
                    }

                }

                Collections.sort(Combined);
                Loop = PlaylistSongID.get(position).size();

                //Determine Old Placements
                for (int i = 0; i < Loop; i++) {
                    for (int e = 0; e < Loop; e++) {
                        if (Playlist) {
                            if (Combined.get(i).contains(Copy.get(position).get(e)) && Combined.get(i).contains(Copy.get(15).get(e)) && (!oldPlacement.contains(e))) {
                                oldPlacement.add(e);
                            }
                        } else {
                            if (Combined.get(i).contains(Copy.get(position).get(e)) && Combined.get(i).contains(Copy.get(12).get(e)) && (!oldPlacement.contains(e))) {
                                oldPlacement.add(e);
                            }
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

                break;
        }
    }

    public void GetSelectedHeader(View view) {
        PlaylistImage = view.findViewById(R.id.PlaylistImage);
        PlaylistNameText = view.findViewById(R.id.PlaylistNameText);
        NumOfSongs = view.findViewById(R.id.NumOfSongs);
        PlaylistLength = view.findViewById(R.id.PlaylistLength);
        SortTypeDefault = view.findViewById(R.id.SortTypeDefault);
        ObjectTypeDefault = view.findViewById(R.id.ObjectTypeDefault);
    }

    public void SetSelectedHeader() {

        final AlertDialog dialog;
        final EditText NewEdit;

        if (AppAppearance.equals("Dark")) {
            PlaylistNameText.setTextColor(Color.rgb(255,255,255));
            PlaylistNameText.setHintTextColor(Color.rgb(255,255,255));
            PlaylistLength.setTextColor(Color.rgb(255,255,255));
            NumOfSongs.setTextColor(Color.rgb(255,255,255));
            Glide.with(this).load(R.drawable.dfltplayimgdark).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(PlaylistImage);
        } else {
            Glide.with(this).load(R.drawable.dfltplayimg).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(PlaylistImage);
        }

        dialog = new AlertDialog.Builder(this).create();
        NewEdit = new EditText(this);
        dialog.setTitle("Playlist Name");
        dialog.setView(NewEdit);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PlaylistNameText.setText(NewEdit.getText());
                NewPlaylistName = NewEdit.getText().toString();
            }
        });

        PlaylistNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewEdit.setText(NewPlaylistName);
                dialog.show();
            }
        });

        if (NewPlaylistName == (null) || NewPlaylistName == ("")) {
            PlaylistNameText.setText("");
            PlaylistNameText.setHint("Playlist Name");
        } else {
            PlaylistNameText.setText(NewPlaylistName);
        }

        if (SelectedSongs.size() == 1) {
            NumOfSongs.setText(SelectedSongs.size() + " Song");
        } else {
            NumOfSongs.setText(SelectedSongs.size() + " Songs");
        }
        int TotalTime = 0;
        for (int i = 0; i < SelectedSongs.size(); i++) {
            TotalTime = TotalTime + Integer.parseInt(PlaylistSongID.get(8).get(SelectedSongs.get(i)));
        }
        PlaylistLength.setText(ClockDisplay(TotalTime));

        for (int i = 0; i < SortMethodAll.length; i++) {
            View Button;
            if (SortMethodAll[i].equals(SortMethodDefault)) {
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
                    SortMethodDefault = SortMethodAll[finalI];
                    SelectedObjects.setAdapter(SelectedScreen);
                }
            });
            SortTypeDefault.addView(Button);
        }

        for (int i = 0; i < ObjectMethodAll.length; i++) {
            View Button;
            if (ObjectMethodAll[i].equals(ObjectMethodDefault)) {
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
            SortType.setText(ObjectMethodAll[i]);
            if (AppAppearance.equals("Dark")) {
                SortType.setTextColor(Color.rgb(255, 255, 255));
            }
            final int finalI = i;
            Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ObjectMethodDefault = ObjectMethodAll[finalI];
                    SelectedObjects.setAdapter(SelectedScreen);
                }
            });
            ObjectTypeDefault.addView(Button);
        }
    }

    public void GetHeader() {
        SearchBar = findViewById(R.id.SearchBar);
        //SortButton = findViewById(R.id.SortButton);
        SortTypeSongs = findViewById(R.id.SortTypeSongs);
    }

    public void SetHeader() {

        SearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                SearchTerm = newText;
                GenSongLengthChecker();
                AllObjects.setAdapter(SearchScreen);

                return false;
            }

        });

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
                    ArrayList<String> IDs = new ArrayList<>();
                    for (int e = 0; e < SelectedSongs.size(); e++) {
                        IDs.add(PlaylistSongID.get(17).get(SelectedSongs.get(e)));
                    }
                    SortMethod = SortMethodAll[finalI];
                    SortEverything();
                    GenSongLengthChecker();
                    AllObjects.setAdapter(SearchScreen);
                    SelectedSongs.clear();
                    for (int e = 0; e < IDs.size(); e++) {
                        for (int g = 0; g < PlaylistSongID.get(17).size(); g++) {
                            if (IDs.get(e).equals(PlaylistSongID.get(17).get(g))) {
                                SelectedSongs.add(g);
                            }
                        }
                    }
                    SelectedObjects.setAdapter(SelectedScreen);
                    SortTypeSongs.removeAllViews();
                    SetHeader();
                }
            });
            SortTypeSongs.addView(Button);
        }

    }

    public void GetRegularBarsSongs(View view) {
        SongBar = view.findViewById(R.id.SongBar);
        SongImage = view.findViewById(R.id.SongImage);
        SongName = view.findViewById(R.id.SongName);
        SongArtist = view.findViewById(R.id.SongArtist);
        SongLength = view.findViewById(R.id.SongLength);
        SongSelected = view.findViewById(R.id.SongSelected);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetRegularInfoSongs(final int i) {
        //SongImage.setImageResource(Integer.parseInt(PlaylistSongID.get(4).get(i)));
        GlideDisplay(i, SongImage, "Song");
        //Glide.with(this).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
        SongName.setText(Title(PlaylistSongID.get(0).get(i)));
        SongArtist.setText(Title(PlaylistSongID.get(1).get(i)));
        SongLength.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(8).get(i))));

        if (SelectedSongs.contains(i)) {
            Glide.with(getApplicationContext()).load(R.drawable.ic_baseline_check_24).placeholder(R.drawable.ic_baseline_check_24).fitCenter().into(SongSelected);
        } else {
            Glide.with(getApplicationContext()).load(R.drawable.ic_baseline_add_24).placeholder(R.drawable.ic_baseline_add_24).fitCenter().into(SongSelected);
        }
        if (AppAppearance.equals("Dark")) {
            SongName.setTextColor(Color.rgb(255, 255, 255));
            SongArtist.setTextColor(Color.rgb(255, 255, 255));
            SongLength.setTextColor(Color.rgb(255, 255, 255));
        }

    }

    public void GeneratePlaylist() {
        GenPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyAllPlaylistFile();
                GenerateNewPlaylist();
                Intent Home = new Intent(getApplicationContext(), PlaylistView.class);
                startActivity(Home);
            }
        });
    }

    public void ModifyAllPlaylistFile() {
        int NumPlaylist;
        try {
            JSONObject AllPlaylistFile = new JSONObject(loadJsonFile("viewPlaylistInfo.json"));
            JSONArray PlayNum = AllPlaylistFile.getJSONArray("NumOfPlaylist");

            //Add top PlaylistNum
            NumPlaylist = (int) PlayNum.get(0);
            NumPlaylist++;
            JSONArray NewPlayNum = new JSONArray();
            NewPlayNum.put(NumPlaylist);
            AllPlaylistFile.put("NumOfPlaylist", NewPlayNum);


            //Gen New Playlist ID
            JSONArray PlaylistArray = new JSONArray();
            JSONArray AllKeyWords = new JSONArray();

            String keyWord1 = "All";
            AllKeyWords.put(keyWord1);

            String ID = "PID" + (NumPlaylist - 1);
            String Name = NewPlaylistName;
            String ImagePath = "Default";
            String PlaylistPath = NewPlaylistName + "_Playlist";
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");
            Date currentTime = Calendar.getInstance().getTime();
            String LastUse = format.format(currentTime);
            int TimesUsed = 0;

            PlaylistArray.put(Name);
            PlaylistArray.put(PlaylistPath);
            PlaylistArray.put(ImagePath);
            PlaylistArray.put(AllKeyWords);
            PlaylistArray.put(LastUse);
            PlaylistArray.put(TimesUsed);
            PlaylistArray.put(ID);

            AllPlaylistFile.put(ID, PlaylistArray);

            saveToStorage("viewPlaylistInfo", AllPlaylistFile);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void GenerateNewPlaylist() {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");

        JSONObject NewPlaylist = new JSONObject();

        ArrayList<String> Album = new ArrayList<>();
        ArrayList<String> Artist = new ArrayList<>();

        for (int i = 0; i < PlaylistAlbumID.get(0).size(); i++) {
            Album.add(PlaylistAlbumID.get(10).get(i));
        }

        for (int i = 0; i < PlaylistArtistID.get(0).size(); i++) {
            Artist.add(PlaylistArtistID.get(9).get(i));
        }

        JSONArray SongNum = new JSONArray();
        JSONArray AlbumNum = new JSONArray();
        JSONArray ArtistNum = new JSONArray();
        JSONArray ObjectSize = new JSONArray();
        JSONArray SortMethod = new JSONArray();
        JSONArray DisplayMethod = new JSONArray();
        JSONArray RankingMethod = new JSONArray();
        JSONArray PlaylistName = new JSONArray();

        SongNum.put(SelectedSongs.size());
        ArtistNum.put(Artist.size());
        AlbumNum.put(Album.size());

        ObjectSize.put("Small");
        if (SortMethodDefault == null) {
            SortMethodDefault = "Alphabetical";
        }
        if (ObjectMethodDefault == null) {
            ObjectMethodDefault = "Song";
        }
        SortMethod.put(SortMethodDefault);
        DisplayMethod.put(ObjectMethodDefault);
        RankingMethod.put("Method1");
        PlaylistName.put(NewPlaylistName);

        JSONArray Tags = new JSONArray();
        JSONArray Mood = new JSONArray();
        Tags.put("Default");
        Tags.put("Music");
        Tags.put("Hello");
        Mood.put("Default");
        Mood.put("Music");
        Mood.put("Hello");

        try {
            //Json.put("SortMethod", "Default");           //Add more info later like view Type (Dark or light mode ect)
            NewPlaylist.put("TotalSongNum", SongNum);
            NewPlaylist.put("TotalArtistNum", ArtistNum);
            NewPlaylist.put("TotalAlbumNum", AlbumNum);
            NewPlaylist.put("ObjectDisplaySize", ObjectSize);
            NewPlaylist.put("SortMethod", SortMethod);
            NewPlaylist.put("ObjectDisplayMethod", DisplayMethod);                //Maybe change name
            NewPlaylist.put("UniversalRankingMethod", RankingMethod);        //Maybe change later, meth1 = 1-10, meth2 = 1-100, meth3 = 1-1000?
            NewPlaylist.put("AllTags", Tags);
            NewPlaylist.put("AllMoods", Mood);
            NewPlaylist.put("PlaylistName", PlaylistName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < SelectedSongs.size(); i++) {
            JSONArray ConstantData = new JSONArray();             //In this loop so that it resets all data every time
            String ID = "SID" + i;  //stands for Playlist Song ID
            Date currentTime = Calendar.getInstance().getTime();
            String Time = format.format(currentTime);
            //double TimeNum = Integer.parseInt(Time);
            //TimeNum++;

            //Not this, make the Id as the name and the rest of the info into JSON Array
            ConstantData.put(PlaylistSongID.get(0).get(SelectedSongs.get(i))); //Song Name
            ConstantData.put(PlaylistSongID.get(1).get(SelectedSongs.get(i))); //Song Artist(s)
            ConstantData.put(PlaylistSongID.get(2).get(SelectedSongs.get(i))); //Song Album
            ConstantData.put(PlaylistSongID.get(3).get(SelectedSongs.get(i))); //Song path
            ConstantData.put(PlaylistSongID.get(4).get(SelectedSongs.get(i))); //Song Album Art
            ConstantData.put(0);
            ConstantData.put(PlaylistSongID.get(5).get(SelectedSongs.get(i)));
            ConstantData.put(PlaylistSongID.get(6).get(SelectedSongs.get(i))); //Playlist            //Delete the playlist ones
            ConstantData.put(PlaylistSongID.get(6).get(SelectedSongs.get(i))); //Uni
            ConstantData.put(PlaylistSongID.get(7).get(SelectedSongs.get(i))); //Playlist
            ConstantData.put(PlaylistSongID.get(7).get(SelectedSongs.get(i))); //Uni
            ConstantData.put(PlaylistSongID.get(8).get(SelectedSongs.get(i))); //Song Duration
            ConstantData.put(0);          //Playlist Num of Times played
            ConstantData.put(0);                      //Playlist Total Time played
            ConstantData.put(Time);          //Last time played in time format Y/M/D/H/M/S
            ConstantData.put(PlaylistSongID.get(12).get(SelectedSongs.get(i))); //Song AlbumID
            ConstantData.put(PlaylistSongID.get(13).get(SelectedSongs.get(i))); //ArtistID
            ConstantData.put("None");             //Active Skin ID
            ConstantData.put(0);                  //Fav start
            ConstantData.put(PlaylistSongID.get(8).get(SelectedSongs.get(i)));  //Fav End
           // ConstantData.put(PlaylistSongID.get(17).get(SelectedSongs.get(i)));

            try {
                NewPlaylist.put(ID, ConstantData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Integer> SaveArtist = new ArrayList<>();
        ArrayList<Integer> ArtistLength = new ArrayList<>();

        for (int i = 0; i < Artist.size(); i++) {
            //for (int e = 0; e < PlaylistArtistID.get(0).size(); e++) {
            //  if (Artist.get(i).equals(PlaylistArtistID.get(9).get(e))) {
            SaveArtist.add(i);
            // }
            // }
        }

        for (int i = 0; i < Artist.size(); i++) {
            ArtistLength.add(0);
            for (int e = 0; e < SelectedSongs.size(); e++) {
                if (Artist.get(i).equals(PlaylistSongID.get(13).get(SelectedSongs.get(e)))) {
                    ArtistLength.set(i, ArtistLength.get(i) + Integer.parseInt(PlaylistSongID.get(8).get(SelectedSongs.get(e))));
                }
            }
        }

        for (int i = 0; i < SaveArtist.size(); i++) {                                       //Saves all the info
            JSONArray ArtistStuff = new JSONArray();
            ArrayList<String> SongsOwned = new ArrayList<>();
            String ID = "AID" + i;         //Stands for Artist ID
            Date currentTime = Calendar.getInstance().getTime();
            String Time = format.format(currentTime);
            //int TimeNum = Integer.parseInt(Time);
            //TimeNum++;

            ArtistStuff.put(PlaylistArtistID.get(0).get(SaveArtist.get(i)));    //Artist Name
            ArtistStuff.put(null);       //Artist Image
            ArtistStuff.put(0);          //Artist Playlist Rank
            ArtistStuff.put(PlaylistArtistID.get(2).get(SaveArtist.get(i)));          //Artist Universal Rank
            ArtistStuff.put("Default");  //Artist Playlist Mood                This and next 3 will be arrays
            ArtistStuff.put(PlaylistArtistID.get(3).get(SaveArtist.get(i)));  //Artist Universal Mood
            ArtistStuff.put("Default");  //Artist Playlist Tags
            ArtistStuff.put(PlaylistArtistID.get(4).get(SaveArtist.get(i)));  //Artist Universal Tags
            ArtistStuff.put(ArtistLength.get(i));  //Artist all songs Length Secs
            ArtistStuff.put(0);          //Playlist Times listened
            ArtistStuff.put(0);          //Playlist Total Time Played   (sec)
            ArtistStuff.put(Time);          //Last Time Played                  Last Time Artist was played in playlist
            ArtistStuff.put(Artist.get(i)); //ArtistID
            ArtistStuff.put("None");         //Active Skin ID
            ArtistStuff.put(PlaylistArtistID.get(11).get(SaveArtist.get(i)));

            for (int e = 0; e < SelectedSongs.size(); e++) {
                if (Artist.get(i).equals(PlaylistSongID.get(13).get(SelectedSongs.get(e)))) {
                    SongsOwned.add("SID" + e);
                }
            }
            ArtistStuff.put(SongsOwned);
            try {
                NewPlaylist.put(ID, ArtistStuff);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Integer> SaveAlbum = new ArrayList<>();
        ArrayList<Integer> AlbumLength = new ArrayList<>();

        for (int i = 0; i < Album.size(); i++) {
            //for (int e = 0; e < PlaylistAlbumID.get(0).size(); e++) {
            //  if (Album.get(i).equals(PlaylistAlbumID.get(10).get(e))) {
            SaveAlbum.add(i);
            //  }
            // }
        }

        for (int i = 0; i < Album.size(); i++) {
            AlbumLength.add(0);
            for (int e = 0; e < SelectedSongs.size(); e++) {
                if (Album.get(i).equals(PlaylistSongID.get(12).get(SelectedSongs.get(e)))) {
                    AlbumLength.set(i, AlbumLength.get(i) + Integer.parseInt(PlaylistSongID.get(8).get(SelectedSongs.get(e))));
                }
            }
        }

        for (int i = 0; i < SaveAlbum.size(); i++) {                                       //Saves all the info
            JSONArray AlbumStuff = new JSONArray();
            ArrayList<String> SongsOwned = new ArrayList<>();
            String ID = "AlID" + i;         //Stands for Album ID
            Date currentTime = Calendar.getInstance().getTime();
            String Time = format.format(currentTime);
            //int TimeNum = Integer.parseInt(Time);
            //TimeNum++;


            AlbumStuff.put(PlaylistAlbumID.get(0).get(SaveAlbum.get(i)));          //Album Name
            AlbumStuff.put(PlaylistAlbumID.get(1).get(SaveAlbum.get(i)));     //Album Artist
            AlbumStuff.put(PlaylistAlbumID.get(2).get(SaveAlbum.get(i)));     //Album Art
            AlbumStuff.put(0);                    //Album Playlist Rank
            AlbumStuff.put(PlaylistAlbumID.get(3).get(SaveAlbum.get(i)));                    //Album Universal Rank
            AlbumStuff.put("Default");            //Album Playlist Mood          This and next 3 are going to be arrays
            AlbumStuff.put(PlaylistAlbumID.get(4).get(SaveAlbum.get(i)));            //Album Universal Mood
            AlbumStuff.put("Default");            //Album Playlist Tags
            AlbumStuff.put(PlaylistAlbumID.get(5).get(SaveAlbum.get(i)));            //Album Universal Tags
            AlbumStuff.put(AlbumLength.get(i));          //Album Length Secs
            AlbumStuff.put(0);                    //Playlist Times Played
            AlbumStuff.put(0);                    //Playlist Total Time Played     (sec)
            AlbumStuff.put(Time);                    //Last Time Played               Last time song from Album was played in playlist
            AlbumStuff.put(PlaylistAlbumID.get(10).get(SaveAlbum.get(i))); //AlbumID
            AlbumStuff.put(PlaylistAlbumID.get(11).get(SaveAlbum.get(i))); //Album Artist ID
            AlbumStuff.put("None");
            AlbumStuff.put(PlaylistAlbumID.get(13).get(SaveAlbum.get(i)));

            for (int e = 0; e < SelectedSongs.size(); e++) {
                if (Album.get(i).equals(PlaylistSongID.get(12).get(SelectedSongs.get(e)))) {
                    SongsOwned.add("SID" + e);
                }
            }

            AlbumStuff.put(SongsOwned);

            try {
                NewPlaylist.put(ID, AlbumStuff);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //
        //Complete and Save
        //
        saveToStorage(NewPlaylistName + "_Playlist", NewPlaylist);

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

    public void Reload(int i) {
        if (SelectedSongs.contains(i)) {
            for (int e = 0; e < SelectedSongs.size(); e++) {
                if (SelectedSongs.get(e).equals(i)) {
                    SelectedSongs.remove(e);
                    int position = AllObjects.getFirstVisiblePosition();
                    int position2 = SelectedObjects.getFirstVisiblePosition();
                    AllObjects.setAdapter(SearchScreen);
                    SelectedObjects.setAdapter(SelectedScreen);
                    AllObjects.setSelection(position);
                    SelectedObjects.setSelection(position2);
                }
            }
        } else {
            SelectedSongs.add(i);
            int position = AllObjects.getFirstVisiblePosition();
            int position2 = SelectedObjects.getFirstVisiblePosition();
            AllObjects.setAdapter(SearchScreen);
            SelectedObjects.setAdapter(SelectedScreen);
            AllObjects.setSelection(position);
            SelectedObjects.setSelection(position2);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void GlideDisplay(int i, ImageView Image, String Type) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Bitmap bitmap = null;
            try {
                switch (Type) {
                    case "Album":
                        bitmap = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(PlaylistAlbumID.get(2).get(i)), new Size(512, 512), null);
                        break;
                    case "Artist":
                        bitmap = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(PlaylistArtistID.get(1).get(i)), new Size(512, 512), null);
                        break;
                    case "Song":
                        bitmap = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(PlaylistSongID.get(4).get(i)), new Size(512, 512), null);
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
                /*
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                if (AppAppearance.equals("Dark")) {
                    Glide.with(this).load(stream.toByteArray()).asBitmap().error(R.drawable.placeholderdark).fitCenter().into(Image);
                } else {
                    Glide.with(this).load(stream.toByteArray()).asBitmap().error(R.drawable.placeholder).fitCenter().into(Image);
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

    private class CustomAdapter1 extends BaseAdapter {
        @Override
        public int getCount() {
            //Fix crashing bug when it can't find anything
            if (SongLengthChecker.size() == 0) {
                return 0;
            } else {
                return SongLengthChecker.get(0).size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View SearchChild = getLayoutInflater().inflate(R.layout.gen_playlist_bar_small, null);
            GetRegularBarsSongs(SearchChild);

            if (AppAppearance.equals("Dark")) {
                SongBar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark2));
            }

            SetRegularInfoSongs(SongLengthChecker.get(0).get(position));
            SongBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Reload(SongLengthChecker.get(0).get(position));
                }

            });

            return SearchChild;
        }
    }

    private class CustomAdapter2 extends BaseAdapter {
        @Override
        public int getCount() {
            return SelectedSongs.size() + 1;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View SelectedChild;

            if (position == 0) {
                SelectedChild = getLayoutInflater().inflate(R.layout.new_playlist_edit_header, parent, false);
                GetSelectedHeader(SelectedChild);
                SetSelectedHeader();

            } else {
                SelectedChild = getLayoutInflater().inflate(R.layout.gen_playlist_bar_small, parent, false);
                GetRegularBarsSongs(SelectedChild);

                SetRegularInfoSongs(SelectedSongs.get(position - 1));
                SongBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Reload(SelectedSongs.get(position - 1));
                    }

                });
            }

            if (AppAppearance.equals("Dark")) {
                SongBar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark2));
            }

            return SelectedChild;
        }
    }
}
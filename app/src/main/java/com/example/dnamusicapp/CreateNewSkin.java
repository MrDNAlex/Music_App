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
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicapp.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

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
import java.util.Random;

public class CreateNewSkin extends AppCompatActivity {

    boolean Playlist = false;

    boolean SelectMode = false;


    ArrayList<ArrayList<Integer>> OwnedObjects = new ArrayList<>(); //Skins, Songs, Album

    int FilePos;

    int NewImage;

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

    ArrayList<String> NewObject = new ArrayList<>();

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
    TextView InfoDisplayFavStart;
    TextView InfoDisplayFavEnd;
    TextView InfoDisplayTotalTime;
    TextView InfoDisplayTimesPlayed;

    //Edit Extra Info
    TextView EditFavStart;
    TextView EditFavEnd;

    //Edit Object
    TextView NewObjectEdit;
    ImageView NewObjectImage;
    //TextView EditRankType;
    //LinearLayout EditRank;

    ImageView EditImageSelect;

    //SelectMode
    SearchView SearchBar;

    //Generate New Object
    GridView NewObjectGenGrid;

    //Save Button
    Button SaveNewEdit;


    //Floating Menu Shit
    boolean PlayMenuOpen = false;
    boolean AddMenuOpen = false;

    FloatingActionMenu PlayAddMenu;
    FloatingActionMenu PlayMenu;
    FloatingActionMenu AddMenu;
    FloatingActionMenu PlayMenuAdvanced;
    FloatingActionMenu NavigationMenu;

    //
    //Navigation Menu Buttons
    //
    FloatingActionButton Home;
    FloatingActionButton SongLibrary;
    FloatingActionButton NewPlaylist;
    FloatingActionButton Settings;

    //
    //Play Add Menu
    //
    FloatingActionButton AddMenuBTN;
    FloatingActionButton PlayMenuBTN;

    //
    //Play Menu
    //
    FloatingActionButton PlayAlbum;
    FloatingActionButton PlayArtist;
    FloatingActionButton PlayRepeat;
    FloatingActionButton PlayQueue;
    FloatingActionButton PlayShuffle;
    FloatingActionButton PlayRegular;
    FloatingActionButton PlayMenuAdvancedBTN;
    FloatingActionButton PlayReturn;

    //
    //Add Menu
    //
    FloatingActionButton AddTags;
    FloatingActionButton AddSongs;
    FloatingActionButton AddAudioMux;
    FloatingActionButton AddRule;
    FloatingActionButton AddPlaylist;
    FloatingActionButton AddReturn;

    //
    //Play Advanced Menu
    //
    FloatingActionButton PlayAdvAudioMux;
    FloatingActionButton PlayAdvMix;
    FloatingActionButton PlayAdvTimeConstraint;
    FloatingActionButton PlayAdvRule;
    FloatingActionButton PlayAdvReturn;

    //Adapters
    CustomAdapter1 InfoDisplayEditAdapter = new CustomAdapter1();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_skin);

        RelativeLayout CreateNewSkinBackground = findViewById(R.id.CreateNewSkinBackground);

        AccessSettings();
        NewObjectGenGrid = findViewById(R.id.NewObjectGenGrid);

        GetInfo();
        ReadPlaylistInfoFile();

        GetSearchHeader();

        int Loop = 0;
        switch (ObjectMode) {
            case "Album":
                Loop = 15;
                break;
            case "Artist":
                Loop = 13;
                break;
            case "Song":
                Loop = 18;
                break;
        }
        for (int i = 0; i < Loop; i++) {
            NewObject.add("Select New");
        }

        SaveEverything();

        NewObjectGenGrid.setAdapter(InfoDisplayEditAdapter);

        if (AppAppearance.equals("Dark")) {
            CreateNewSkinBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.background_alex3));
        }


        //Probably a wise choice to lock this to only Album and Artist to make it easier on me

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
        ObjectMode = Stuff.getStringExtra("ObjectMode");

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

    public void GetRegularSelectBar(View view, boolean Image) {

        if (Image) {
            EditImageSelect = view.findViewById(R.id.EditImageSelect);
        } else {
            GetRegularBarsSongs(view);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetRegularSelectBar(final int i, final String Mode) {

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
                                        NewObject.set(2, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                        NewImage = SongLengthChecker.get(i);

                                        //PlaylistAlbumID.get(2).set(FilePos, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                        break;
                                }
                                break;
                            case "Artist":
                                switch (Mode) {
                                    case "Image":
                                        NewObject.set(1, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                        NewImage = SongLengthChecker.get(i);
                                        //PlaylistArtistID.get(1).set(FilePos, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                        break;
                                }
                                break;
                            case "Song":
                                //Maybe just switch this to the alert thing and edit Song Name
                                switch (Mode) {
                                    case "Image":
                                        NewObject.set(4, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                        NewImage = SongLengthChecker.get(i);
                                        //PlaylistSongID.get(4).set(FilePos, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                        break;
                                }
                                break;
                        }
                        SelectMode = false;
                        SearchBar.setVisibility(View.INVISIBLE);
                        NewObjectGenGrid.setAdapter(InfoDisplayEditAdapter);
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

        if (Mode == "Image") {

        } else {
            SongBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog dialog = new AlertDialog.Builder(getApplicationContext()).create();
                    final EditText NewEdit = new EditText(getApplicationContext());
                    switch (ObjectMode) {
                        case "Album":
                            switch (Mode) {
                                case "Image":
                                    NewObject.set(2, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                    break;
                                case "Album":
                                    //PlaylistAlbumID.get(0).set(FilePos, PlaylistAlbumID.get(0).get(SongLengthChecker.get(i)));
                                    break;
                                case "Artist":
                                    NewObject.set(1, PlaylistArtistID.get(0).get(SongLengthChecker.get(i)));
                                    NewObject.set(11, PlaylistArtistID.get(9).get(SongLengthChecker.get(i)));
                                    break;
                            }
                            break;
                        case "Artist":
                            switch (Mode) {
                                case "Image":
                                    NewObject.set(1, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                    //PlaylistArtistID.get(1).set(FilePos, PlaylistAlbumID.get(2).get(SongLengthChecker.get(i)));
                                    break;
                                case "Artist":
                                    //PlaylistArtistID.get(0).set(FilePos, PlaylistArtistID.get(0).get(SongLengthChecker.get(i)));
                                    break;
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
                                    break;
                                case "Artist":
                                    PlaylistSongID.get(1).set(FilePos, PlaylistArtistID.get(0).get(SongLengthChecker.get(i)));

                                    break;
                                case "Song":
                                    //Maybe just switch this to the alert thing and edit Song Name
                                    //PlaylistSongID.get(0).set(FilePos, PlaylistSongID.get(0).get(SongLengthChecker.get(i)));
                            }
                            break;
                    }
                    SelectMode = false;
                    NewObjectGenGrid.setAdapter(InfoDisplayEditAdapter);
                }
            });
        }
    }

    public void GetEditView(View view, int pos) {

        if (pos == 0) {
            NewObjectImage = view.findViewById(R.id.NewObjectImage);
        } else {
            NewObjectEdit = view.findViewById(R.id.NewObjectEdit);
            if (AppAppearance.equals("Dark")) {
                NewObjectEdit.setTextColor(Color.rgb(255,255,255));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetEditView(int pos) {

        //Don't need Extra shit just del?

        //
        //Gotta switch these to Alert Dialog for the appropriate ones
        //

        switch (ObjectMode) {
            case "Album":
                //Image, Album Name, Artist
                switch (pos) {
                    case 0:
                        GlideDisplay(NewImage, NewObjectImage, ObjectMode);
                        NewObjectImage.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        NewObjectImage.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        SelectModeSwitch("Image");
                        break;
                    case 1:
                        NewObjectEdit.setText(NewObject.get(0));
                        SelectModeSwitch("Album");
                        break;
                    case 2:
                        NewObjectEdit.setText(NewObject.get(1));
                        SelectModeSwitch("Artist");
                        break;
                }
                break;
            case "Artist":
                switch (pos) {
                    case 0:
                        GlideDisplay(NewImage, NewObjectImage, "Album");

                        NewObjectImage.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        NewObjectImage.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        SelectModeSwitch("Image");

                        break;
                    case 1:
                        NewObjectEdit.setText(NewObject.get(0));
                        SelectModeSwitch("Artist");
                        break;
                }
                break;
            case "Song":
                switch (pos) {
                    case 0:
                        GlideDisplay(NewImage, NewObjectImage, "Album");
                        NewObjectImage.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        NewObjectImage.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        SelectModeSwitch("Image");

                        break;
                    case 1:
                        NewObjectEdit.setText(PlaylistSongID.get(0).get(FilePos));
                        SelectModeSwitch("Song");
                        break;
                    case 2:
                        NewObjectEdit.setText(PlaylistSongID.get(1).get(FilePos));
                        SelectModeSwitch("Artist");
                        break;
                    case 3:
                        NewObjectEdit.setText(PlaylistSongID.get(2).get(FilePos));
                        SelectModeSwitch("Album");
                        break;
                }
                break;
        }
    }

    public void SelectModeSwitch(final String Type) {
        if (Type.equals("Image")) {
            NewObjectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectMode = true;
                    SelectMethod = Type;
                    SetSearchHeader();
                    SearchBar = findViewById(R.id.SearchBar);
                    SearchBar.setVisibility(View.VISIBLE);
                    SortEverything();
                    GenSongLengthChecker();
                    NewObjectGenGrid.setAdapter(InfoDisplayEditAdapter);
                }
            });
        } else {
            NewObjectEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog dialog = new AlertDialog.Builder(CreateNewSkin.this).create();
                    final EditText NewEdit = new EditText(getApplicationContext());

                    dialog.setTitle("New " + ObjectMode + " Name");
                    dialog.setView(NewEdit);
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NewObject.set(0, NewEdit.getText().toString());
                            NewObjectEdit.setText(NewEdit.getText());
                        }
                    });

                    switch (ObjectMode) {
                        case "Album":
                            switch (Type) {
                                case "Album":
                                    dialog.show();
                                    break;
                                case "Artist":
                                    SelectMode = true;
                                    SelectMethod = Type;

                                    SetSearchHeader();
                                    SearchBar = findViewById(R.id.SearchBar);
                                    SearchBar.setVisibility(View.VISIBLE);

                                    SortEverything();

                                    GenSongLengthChecker();
                                    NewObjectGenGrid.setAdapter(InfoDisplayEditAdapter);
                                    break;
                            }
                            break;
                        case "Artist":
                            switch (Type) {
                                case "Album":
                                    SelectMode = true;
                                    SelectMethod = Type;

                                    SetSearchHeader();
                                    SearchBar = findViewById(R.id.SearchBar);
                                    SearchBar.setVisibility(View.VISIBLE);

                                    SortEverything();

                                    GenSongLengthChecker();
                                    NewObjectGenGrid.setAdapter(InfoDisplayEditAdapter);
                                    break;
                                case "Artist":
                                    dialog.show();
                                    break;
                            }
                            break;
                    }
                }
            });
        }
    }

    public void SaveEverything() {
        SaveNewEdit = findViewById(R.id.SaveNewEdit);
        SaveNewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveInfo();
            }
        });

    }

    public void SaveInfo() {

        try {
            JSONObject NewMasterFile = new JSONObject(loadJsonFile("MasterSongDataFile.json"));

            //Add extra info to array
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");
            Date currentTime = Calendar.getInstance().getTime();
            int NumOfPlaylist;
            JSONArray Num;
            JSONObject PlaylistInfo = new JSONObject(loadJsonFile("viewPlaylistInfo.json"));
            JSONArray PlaylistNum = PlaylistInfo.getJSONArray("NumOfPlaylist");
            ArrayList<String> ConvertObject;
            String ID = "";
            switch (ObjectMode) {
                case "Album":
                    ID = "AlID" + AlbumNum;
                    //Universal Rank, Mood, Tags, Length, Times Played, Total Time Played, LastTime Played, AlbumID, Artist Id, Active Skin, File ID, SIDx
                    NewObject.set(3, "0"); //Unirank
                    NewObject.set(4, AllMoods.get(0));
                    NewObject.set(5, AllTags.get(0));
                    NewObject.set(6, "0"); //Length
                    NewObject.set(7, "0");//Times Played
                    NewObject.set(8, "0");//Total Time Played
                    NewObject.set(9, format.format(currentTime)); //Last time Played
                    NewObject.set(10, GenNewIDs("Album"));
                    //NewObject.set(3, "0"); //Artist
                    NewObject.set(12, "None");
                    NewObject.set(13, ID);
                    NewObject.set(14, "Null");

                    //Add object to Array list
                    for (int i = 0; i < PlaylistAlbumID.size(); i++) {
                        PlaylistAlbumID.get(i).add(NewObject.get(i));
                    }

                    //Enter new Number of Albums
                    Num = new JSONArray();
                    Num.put(PlaylistAlbumID.get(0).size());
                    NewMasterFile.put("TotalAlbumNum", Num);

                    ConvertObject = new ArrayList<>();

                    //Save to every other file
                    NumOfPlaylist = (int) PlaylistNum.get(0);

                    for (int i = 0; i < NumOfPlaylist; i++) {
                        JSONArray PlaylistArray = PlaylistInfo.getJSONArray("PID" + i);

                        JSONObject AddToPlaylist = new JSONObject(loadJsonFile(PlaylistArray.get(1).toString() + ".json"));

                        AddToPlaylist.put("TotalAlbumNum", Num);

                        JSONArray Data = new JSONArray();

                        //Rewrite all Album Data

/*
                            Album Name
                            Album Artist Name
                            Album Art
                            Album Playlist Rank
                            Album Universal Rank
                            Album Playlist Mood
                            Album Universal Mood
                            Album Playlist Tags
                            Album Universal Tags
                            Album Length
                            Album Playlist Times Played
                            Album Playlist Total Time Played
                            Last Time Played
                                AlbumID
                            Artist ID
                            Active Skin ID
                            File Album ID
                                SIDx, SIDx,

 */
                        ConvertObject.add(NewObject.get(0));
                        ConvertObject.add(NewObject.get(1));
                        ConvertObject.add(NewObject.get(2));
                        ConvertObject.add("0");//Playlist Rank
                        ConvertObject.add(NewObject.get(3));
                        ConvertObject.add("Default"); //Playlist Mood
                        ConvertObject.add(NewObject.get(4));
                        ConvertObject.add("Default"); //Playlist Tag
                        ConvertObject.add(NewObject.get(5));
                        ConvertObject.add(NewObject.get(6));
                        ConvertObject.add("0"); // Album Playlist Times Played
                        ConvertObject.add("0"); //Album Playlist Total Time Played
                        ConvertObject.add(NewObject.get(9));
                        ConvertObject.add(NewObject.get(10));
                        ConvertObject.add(NewObject.get(11));
                        ConvertObject.add(NewObject.get(12));
                        ConvertObject.add(NewObject.get(13));
                        ConvertObject.add(NewObject.get(14));

                        for (int e = 0; e < ConvertObject.size(); e++) {
                            Data.put(ConvertObject.get(e));
                        }

                        AddToPlaylist.put(ID, Data);

                        saveToStorage(PlaylistArray.get(1).toString(), AddToPlaylist);
                    }

                    //SortEverything
                    //SortEverything();

                    //Rewrite all Album Data in Master File
                    for (int i = 0; i < PlaylistAlbumID.get(0).size(); i++) {
                        JSONArray Data = new JSONArray();
                        for (int e = 0; e < PlaylistAlbumID.size(); e++) {
                            ID = "AlID" + i;
                            Data.put(PlaylistAlbumID.get(e).get(i));
                        }
                        NewMasterFile.put(ID, Data);
                    }

                    break;
                case "Artist":
                    ID = "AID" + ArtistNum;
                    NewObject.set(2, "0"); //Unirank
                    NewObject.set(3, AllMoods.get(0));
                    NewObject.set(4, AllTags.get(0));
                    NewObject.set(5, "0"); //Length
                    NewObject.set(6, "0");//Times Played
                    NewObject.set(7, "0");//Total Time Played
                    NewObject.set(8, format.format(currentTime)); //Last time Played
                    NewObject.set(9, GenNewIDs("Artist")); //Artist
                    NewObject.set(10, "None");
                    NewObject.set(11, ID);
                    NewObject.set(12, "Null");

                    //Add object to Arraylist
                    for (int i = 0; i < PlaylistArtistID.size(); i++) {
                        PlaylistArtistID.get(i).add(NewObject.get(i));
                    }

                    //Enter new number of Artists
                    Num = new JSONArray();
                    Num.put(PlaylistArtistID.get(0).size());
                    NewMasterFile.put("TotalArtistNum", Num);

                    ConvertObject = new ArrayList<>();

                    //Save to every other file
                    NumOfPlaylist = (int) PlaylistNum.get(0);

                    for (int i = 0; i < NumOfPlaylist; i++) {
                        JSONArray PlaylistArray = PlaylistInfo.getJSONArray("PID" + i);

                        JSONObject AddToPlaylist = new JSONObject(loadJsonFile(PlaylistArray.get(1).toString() + ".json"));

                        AddToPlaylist.put("TotalArtistNum", Num);

                        JSONArray Data = new JSONArray();

                        //Rewrite all Album Data

/*
                           Artist Name
Artist Image
Artist Playlist Rank
Artist Universal Rank
Artist Playlist Mood
Artist Universal Mood
Artist Playlist Tags
Artist Universal Tags
Artist Length
Artist Playlist Times Played
Artist Playlist Total Time Played
Last Time Played
ArtistID
Active Skin ID
File Artist ID
SIDx, SIDx

 */
                        ConvertObject.add(NewObject.get(0));
                        ConvertObject.add(NewObject.get(1));
                        ConvertObject.add("0");//Playlist Rank
                        ConvertObject.add(NewObject.get(2));
                        ConvertObject.add("Default"); //Playlist Mood
                        ConvertObject.add(NewObject.get(3));
                        ConvertObject.add("Default"); //Playlist Tag
                        ConvertObject.add(NewObject.get(4));
                        ConvertObject.add(NewObject.get(5));
                        ConvertObject.add("0"); // Artist Playlist Times Played
                        ConvertObject.add("0"); //Artist Playlist Total Time Played
                        ConvertObject.add(NewObject.get(8));
                        ConvertObject.add(NewObject.get(9));
                        ConvertObject.add(NewObject.get(10));
                        ConvertObject.add(NewObject.get(11));
                        ConvertObject.add(NewObject.get(12));

                        for (int e = 0; e < ConvertObject.size(); e++) {
                            Data.put(ConvertObject.get(e));
                        }

                        AddToPlaylist.put(ID, Data);

                        saveToStorage(PlaylistArray.get(1).toString(), AddToPlaylist);
                    }


                    //Rewrite all Artist Data Master File
                    for (int i = 0; i < PlaylistArtistID.get(0).size(); i++) {
                        JSONArray Data = new JSONArray();
                        for (int e = 0; e < PlaylistArtistID.size(); e++) {
                            ID = "AID" + i;
                            Data.put(PlaylistArtistID.get(e).get(i));
                        }
                        NewMasterFile.put(ID, Data);
                    }

                    break;
            }
            //Save to Master file
            saveToStorage("MasterSongDataFile", NewMasterFile);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent Home = new Intent(getApplicationContext(), PlaylistView.class);
        startActivity(Home);

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

        while (AllID.contains(String.valueOf(random))) {
            random = new Random().nextInt(30000) + 10000;
        }

        ID = random;

        return String.valueOf(ID);
    }

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

    //
    ///
    // Remove Song from everything
    //
    //
    private class CustomAdapter1 extends BaseAdapter {
        @Override
        public int getCount() {
            if (SelectMode) {
                return SongLengthChecker.size();
            } else {
                if (ObjectMode.equals("Album")) {
                    return 3;
                    //Name, Artist, Image,
                } else if (ObjectMode.equals("Artist")) {
                    return 2; //Artist, Image
                } else if (ObjectMode.equals("Song")) {
                    return 5;  //Just in case I guess Name, Artist, Album, Image, Path       Probably not going to use though
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

                if (SelectMethod.equals("Image")) {
                    EditView = getLayoutInflater().inflate(R.layout.info_display_select_image, null);
                    GetRegularSelectBar(EditView, true);
                } else {
                    EditView = getLayoutInflater().inflate(R.layout.song_bar_object_big, null);
                    GetRegularSelectBar(EditView, false);
                }

                SetRegularSelectBar(SongLengthChecker.get(position), SelectMethod);

            } else {
                //Display All editable fields
                EditView = getLayoutInflater().inflate(R.layout.new_skin_info, null);
                if (AppAppearance.equals("Dark")) {
                    LinearLayout NewSkinInfoBackground = EditView.findViewById(R.id.NewSkinInfoBackground);
                   NewSkinInfoBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark2));
                }
                SearchBar.getLayoutParams().height = 0;
                switch (position) {
                    case 0:
                        EditView = getLayoutInflater().inflate(R.layout.new_skin_header, null);
                        GetEditView(EditView, position);
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
                        GetEditView( EditView, position);
                        SetEditView( position);
                        break;

                         */
                }
            }
            return EditView;
        }
    }
}










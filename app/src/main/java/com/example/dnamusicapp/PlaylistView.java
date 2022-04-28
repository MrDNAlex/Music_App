package com.example.dnamusicapp;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class PlaylistView extends AppCompatActivity {
    @SuppressLint("WrongViewCast")

    String fileName = "viewPlaylistInfo.json";

    int AreaIDLength = 3;
    int PlaylistIDLength = 7;

    int NumArea;
    int NumPlaylist;

    String SortMethod = "Alphabetical";

    String AppAppearance = "";

    String NameAccount = "";

    ArrayList<ArrayList<String>> PlaylistID = new ArrayList<>();
    ArrayList<ArrayList<String>> AreaID = new ArrayList<>();

    ArrayList<ArrayList<Integer>> PlaylistLengthChecker = new ArrayList<>();

    ArrayList<String> AllKeyWords = new ArrayList<>();

    //have only the names here for the menu and buttons, but determine them in the on create

    BottomNavigationView BottomNavigation;

    boolean PlayMenuOpen = false;
    boolean AddMenuOpen = false;

    //
    //Grids, Layouts ect
    //

    LinearLayout PlaylistBTN;
    GridView PlaylistViewGrid;
    ImageView playlistClick;
    LinearLayout PlaylistAreaHorizon;

    LinearLayout PlaylistViewLinear;

    MiniPlayer MiniPlayerReceiver = new MiniPlayer();

    LinearLayout MusicPlayerMini;

    CustomAdapter2 CustAdaptAreaGrid = new CustomAdapter2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_view);
        //TextView NumPlay = findViewById(R.id.playlistNum);
        AccessSettings();

        PlaylistBTN = findViewById(R.id.PlayListBTN);
        PlaylistViewGrid = findViewById(R.id.PlaylistViewGrid);
        playlistClick = findViewById(R.id.playlistButton);

        PlaylistViewLinear = findViewById(R.id.PlaylistViewLinear);
        MusicPlayerMini = findViewById(R.id.MusicPlayerMini);
        MusicPlayerMini.setVisibility(View.INVISIBLE);

        ReadPlaylistInfoFile();

       // FloatingMenuSetup();

        SetUpNav();
        AntennaSetup();
        CheckAudioPlayer();

        SortPlaylists();

        GenPlaylistLengthChecker();

        if (AppAppearance.equals("Dark")) {
           // PlaylistViewGrid.setBackground(ContextCompat.getDrawable(this, R.drawable.background_alex3));
            PlaylistViewLinear.setBackground(ContextCompat.getDrawable(this, R.drawable.background_alex3));
        }

        //Set the the Setting for either popular or most recent to display top x amount of playlists (specify x in settings) and then display the top 2 of each playlist Area
        PlaylistViewGrid.setAdapter(CustAdaptAreaGrid);

    }

    public void SetUpNav () {

        BottomNavigation = findViewById(R.id.bottom_navigation);

        BottomNavigation.setSelectedItemId(R.id.HomeNav);

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
            JSONObject InfoFile = new JSONObject(loadJsonFile(fileName));
            JSONArray PlayNum = InfoFile.getJSONArray("NumOfPlaylist");
            JSONArray AreaNum = InfoFile.getJSONArray("NumOfArea");
            JSONArray Sort = InfoFile.getJSONArray("SortMethod");
            JSONArray Name = InfoFile.getJSONArray("AccountName");
            NumPlaylist = (int) PlayNum.get(0);
            NumArea = (int) AreaNum.get(0);
            SortMethod = (String) Sort.get(0);
            NameAccount = (String) Name.get(0);

            JSONArray keyWords = InfoFile.getJSONArray("AllKeyWords");
            for (int i = 0; i < keyWords.length(); i++) {
                AllKeyWords.add(keyWords.get(i).toString());
            }

            InitializeArray();

            //
            //Load Playlist and Area Array
            //

            for (int i = 0; i < NumArea; i++) {
                JSONArray Area = InfoFile.getJSONArray("ArID" + i);
                for (int e = 0; e < AreaIDLength; e++) {
                    AreaID.get(e).add(Area.get(e).toString());
                }
            }

            for (int i = 0; i < NumPlaylist; i++) {
                JSONArray Play = InfoFile.getJSONArray("PID" + i);
                for (int e = 0; e < PlaylistIDLength; e++) {
                    PlaylistID.get(e).add(Play.get(e).toString());

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void InitializeArray() {

        //Initialize Array PlaylistID
        for (int i = 0; i < 7; i++) {
            PlaylistID.add(new ArrayList<String>());
        }

        //Initialize Array AreaID
        for (int i = 0; i < 3; i++) {
            AreaID.add(new ArrayList<String>());
        }

    }


    public void SortArrays(int pos) {

        ArrayList<ArrayList<String>> Copy = new ArrayList<>();
        ArrayList<String> Combined = new ArrayList<>();
        ArrayList<Integer> oldPlacement = new ArrayList<>();
        int Loop;
        int ArrayNum;

        for (int i = 0; i < PlaylistID.size(); i++) {
            Copy.add(new ArrayList<String>());
            for (int e = 0; e < PlaylistID.get(0).size(); e++) {
                Copy.get(i).add(PlaylistID.get(i).get(e));
            }
        }

        for (int i = 0; i < PlaylistID.get(pos).size(); i++) {
            Combined.add(PlaylistID.get(pos).get(i) + "  " + PlaylistID.get(6).get(i));
        }

        Collections.sort(Combined);
        Loop = PlaylistID.get(pos).size();

        //Determine Old Placements
        for (int i = 0; i < Loop; i++) {
            for (int e = 0; e < Loop; e++) {
                if (Combined.get(i).contains(Copy.get(pos).get(e)) && Combined.get(i).contains(Copy.get(6).get(e)) && (!oldPlacement.contains(e))) {
                    oldPlacement.add(e);
                }
            }
        }

        ArrayNum = PlaylistID.size();
        PlaylistID.clear();

        for (int i = 0; i < ArrayNum; i++) {
            PlaylistID.add(new ArrayList<String>());
        }

        //Load info into Array
        for (int i = 0; i < ArrayNum; i++) {
            for (int e = 0; e < Loop; e++) {
                PlaylistID.get(i).add(Copy.get(i).get(oldPlacement.get(e)));
            }
        }

    }


    public void SortPlaylists() {
        //Sort all array information using the settings (Example: alphabetical order, numerical ect)
        switch (SortMethod) {
            case "Alphabetical":
                SortArrays(0);
                break;
            case "Date":
                SortArrays(5);
                break;
            case "Popular":
                SortArrays(4);
                break;
            default:
                SortArrays(0);
                break;
        }
    }

    public void GenPlaylistLengthChecker() {
        PlaylistLengthChecker.clear();

        for (int i = 0; i < NumArea; i++) {
            PlaylistLengthChecker.add(new ArrayList<Integer>());

            for (int e = 0; e < NumPlaylist; e++) {

                if (PlaylistID.get(3).get(e).contains(AreaID.get(1).get(i) + ",") || PlaylistID.get(3).get(e).contains(AreaID.get(1).get(i) + "]") || PlaylistID.get(3).get(e).contains(AreaID.get(1).get(i))) {
                    PlaylistLengthChecker.get(i).add(e);
                }
            }
        }
    }

    public void saveToStorage(String fileName, JSONObject jsonObj) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_PRIVATE));
            writer.write(jsonObj.toString());
            writer.close();
            //Toast.makeText(this, "Succesfully Saved File", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private class CustomAdapter2 extends BaseAdapter {
        @Override
        // + 1 for header
        public int getCount() {
            return PlaylistLengthChecker.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //Displays Playlist Areas
            View view2;

            if (position == 0) {
                view2 = getLayoutInflater().inflate(R.layout.playlistview_header, null);

                ImageView AccountSettings = view2.findViewById(R.id.AccountSettings);
                TextView AccountName = view2.findViewById(R.id.AccountName);

                AccountName.setText(NameAccount);

                final AlertDialog dialog;
                final EditText NewEdit;

                dialog = new AlertDialog.Builder(PlaylistView.this).create();
                NewEdit = new EditText(getApplicationContext());
                dialog.setTitle("Playlist Name");
                dialog.setView(NewEdit);
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        NameAccount = NewEdit.getText().toString();

                        AccountName.setText(NameAccount);

                        try {
                            JSONObject InfoFile = new JSONObject(loadJsonFile(fileName));

                            JSONArray Name = new JSONArray();
                            Name.put(NewEdit.getText());

                            InfoFile.put("AccountName", Name);

                            saveToStorage(fileName, InfoFile);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                AccountSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent Settings = new Intent(getApplicationContext(), SettingsFiles.class);
                        startActivity(Settings);
                    }
                });

                AccountName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NewEdit.setText("Account Name");
                        dialog.show();

                    }
                });






            } else {
                view2 = getLayoutInflater().inflate(R.layout.playlist_area, null);
                PlaylistAreaHorizon = view2.findViewById(R.id.PlaylistAreaHorizontal);
                TextView PlaylistAreaName = view2.findViewById(R.id.PlaylistAreaName);

                PlaylistAreaName.setText(AreaID.get(0).get(position - 1) + " Playlists");
                final String AreaName = AreaID.get(0).get(position - 1) + " Playlists";
                PlaylistAreaName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Go to all Playlist Area
                        Intent PlaylistView = new Intent(getApplicationContext(), AllPlaylistInArea.class);
                        PlaylistView.putExtra("PlaylistAreaName", AreaName);
                        PlaylistView.putExtra("SortMethod", SortMethod);
                        PlaylistView.putExtra("Position", position - 1);
                        startActivity(PlaylistView);
                    }
                });

                //Displays buttons
                for (int i = 0; i < PlaylistLengthChecker.get(position - 1).size(); i++) {
                    View view1 = getLayoutInflater().inflate(R.layout.playlist_image_button, null);
                    TextView PlaylistNameButton = view1.findViewById(R.id.PlaylistName);
                    playlistClick = view1.findViewById(R.id.playlistButton);
                    ImageView PlaylistImageBar = view1.findViewById(R.id.PlaylistImageBar);

                    if (AppAppearance.equals("Dark")) {
                        Glide.with(getApplicationContext()).load(R.drawable.dfltplayimgdark).placeholder(R.drawable.placeholderdark).fitCenter().into(playlistClick);
                        Glide.with(getApplicationContext()).load(R.drawable.songbarobjectdark).placeholder(R.drawable.songbarobjectdark).fitCenter().into(PlaylistImageBar);
                    } else {
                        Glide.with(getApplicationContext()).load(R.drawable.dfltplayimg).placeholder(R.drawable.placeholder).fitCenter().into(playlistClick);
                    }

                    PlaylistNameButton.setText(PlaylistID.get(0).get(PlaylistLengthChecker.get(position - 1).get(i)));

                    final int finalI = i;
                    playlistClick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Go to actual Playlist
                            Intent PlaylistView = new Intent(getApplicationContext(), PlaylistSongView.class);
                            PlaylistView.putExtra("FilePath", PlaylistID.get(1).get(PlaylistLengthChecker.get(position - 1).get(finalI)));
                            startActivity(PlaylistView);
                        }
                    });

                    PlaylistAreaHorizon.addView(view1);

                }
            }
            return view2;
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


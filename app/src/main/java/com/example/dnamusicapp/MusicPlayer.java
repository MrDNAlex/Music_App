package com.example.dnamusicapp;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
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
import java.util.ArrayList;
import java.util.Collections;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MusicPlayer extends AppCompatActivity {

    boolean Playlist = true;
    Boolean MiniPlayer;

    ArrayList<Integer> Red = new ArrayList<>();
    ArrayList<Integer> Green = new ArrayList<>();
    ArrayList<Integer> Blue = new ArrayList<>();


    ArrayList<ArrayList<String>> PlaylistSongID = new ArrayList<>();
    ArrayList<ArrayList<String>> PlaylistArtistID = new ArrayList<>();
    ArrayList<ArrayList<String>> PlaylistAlbumID = new ArrayList<>();

    ArrayList<String> AllTags = new ArrayList<>();
    ArrayList<String> AllMoods = new ArrayList<>();

    ArrayList<ArrayList<Integer>> SongChecker = new ArrayList<>();

    ArrayList<String> SelectedQueue = new ArrayList<>();

    ArrayList<ArrayList<Integer>> FinalBarQueue = new ArrayList<>();
    ArrayList<Integer> FinalHeaderQueue = new ArrayList<>();
    ArrayList<Integer> FinalSongQueue = new ArrayList<>();

    String QueueMethod, ObjectType, ObjectMethod, SortMethod, FilePath;
    String AppAppearance;
    String ImagePath;
    String SongID;

    int AnimationCode = R.anim.fab_scale_up;

    int GroupPosition, ChildPosition, LoopLength;

    int SongPos;

    int width;

    //Time ints
    int Sec, Min, Hour;

    int LRed, LGreen, LBlue = 0;

    //Loop Numbers
    int SongNum;
    int ArtistNum;
    int AlbumNum;
    int VariableNumSong = 19;
    //PSID:  Song Name, Artist Name, Album Name, Song Path, Album Art, Playlist Rank, Universal Rank, Playlist Music Mood, Universal Music Mood, Playlist Music Tags, Universal Music Tags, Song Length,Playlist Times Played, Universal Times played, Last Time Played (date), AlbumID, ArtistID
    int VariableNumArtist = 16;
    //AID: Artist Name, Artist Playlist Rank,Artist Universal Rank, Artist Playlist Mood, Artist Universal Mood, Artist Playlist Tags, Artist Universal Tags, Artist Length, Artist Playlist Times Played, Artist Universal Times Played, Artist Playlist Total Time Played,  Artist Universal Total Time Played, Last Time Played (date), ArtistID, [SIDx, SIDx, SIDx, SIDx,.... ]
    int VariableNumAlbum = 18;
    //AlID: Album Name, Album Artist Name, Album Art, Album Playlist Rank, Album Universal Rank, Album Playlist Mood, Album Universal Mood, Album Playlist Tags, Album Universal Tags, Album Length, Album Playlist Times Played,  Album Universal Times Played, Album Playlist Total Time Played ,Album Universal Total Time Played, Last Time Played, AlbumID.  [SIDx, SIDx, SIDx, SIDx,....]
    int MasterVariableNumSong = 16;
    //PSID:  Song Name, Artist Name, Album Name, Song Path, Album Art, Playlist Rank, Universal Rank, Playlist Music Mood, Universal Music Mood, Playlist Music Tags, Universal Music Tags, Song Length,Playlist Times Played, Universal Times played, Last Time Played (date), AlbumID, ArtistID
    int MasterVariableNumArtist = 13;
    //AID: Artist Name, Artist Playlist Rank,Artist Universal Rank, Artist Playlist Mood, Artist Universal Mood, Artist Playlist Tags, Artist Universal Tags, Artist Length, Artist Playlist Times Played, Artist Universal Times Played, Artist Playlist Total Time Played,  Artist Universal Total Time Played, Last Time Played (date), ArtistID, [SIDx, SIDx, SIDx, SIDx,.... ]
    int MasterVariableNumAlbum = 15;

    boolean Paused = false;
    boolean ManualSeek = false;

    TabHost tabHost;

    //Page PlayInfo
    ImageView PISongImage;

    ImageView PILastSong;
    ImageView PIRewind;
    ImageView PIPlayPause;
    ImageView PIForward;
    ImageView PINextSong;

    TextView PISongName;
    TextView PISongPlaylistRank;
    TextView PISongUniversalRank;
    TextView PISongArtist;
    TextView PICurrentTime;
    TextView PITotalTime;
    TextView PIPercentageComplete;

    SeekBar PISeekBar;

    //Page Queue
    ExpandableListView QueueList;

    //Song Bars
    LinearLayout SongBar;

    ImageView SongImage;

    TextView SongName;
    TextView SongArtist;
    TextView SongLength;

    //Song Bar Extras
    TextView SongPR;
    TextView SongUR;

    //View
    View QueueBar;

    LinearLayout PlayInfo;

    LinearLayout MusicPlayerBackground;

    public AntennaMusicPlayer AntennaReceiver = new AntennaMusicPlayer();
    //Maybe gotta make the media player at the very first screen and transport it everywhere?
    //Maybe need to make a Background service? Something like that


    // https://stackoverflow.com/questions/6831671/is-there-a-way-to-programmatically-scroll-a-scroll-view-to-a-specific-edit-text

    //Adapters
    CustomAdapter0 QueueListAdapter = new CustomAdapter0();

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music_player);
        MusicPlayerBackground = findViewById(R.id.MusicPlayerBackground);
        AccessSettings();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        //
        //Oh shit we do need to sort everything the same way
        //

        //Add the Master file version too
        tabHost = findViewById(R.id.tabhost);
        QueueList = findViewById(R.id.QueueList);

        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("PlayInfo");
        spec.setIndicator("PlayInfo");
        spec.setContent(R.id.PlayInfo);
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Queue");
        spec.setIndicator("Queue");
        spec.setContent(R.id.Queue);
        tabHost.addTab(spec);

        ObjectType = "Small";
        GetInfo();
        loadColours();

        if (MiniPlayer) {

            ReadPlaylistInfoFile();

            SortEverything();

            QueueList.setAdapter(QueueListAdapter);

            AntennaSetup();

            GetPlayInfoActivity();

            SetPlayInfoActivity(SongPos);

            Controller();

            //StartSaveData();

            //PlayMusic(GroupPosition, ChildPosition);


        } else {

            ReadPlaylistInfoFile();

            SortEverything();

            Setup();

            QueueList.setAdapter(QueueListAdapter);

            AntennaSetup();

            StartSaveData();

            PlayMusic(GroupPosition, ChildPosition);


        }

        PlayInfo = findViewById(R.id.PlayInfo);
        if (AppAppearance.equals("Dark")) {
            MusicPlayerBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.background_alex3));
            PlayInfo.setBackground(ContextCompat.getDrawable(this, R.drawable.musicplayerforeground4));
            for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setTextColor(Color.rgb(255, 255, 255));
            }
        }

        //Gotta setup a broadcast receiver

        //this will require fragments to play on all other screens (NVM)
//https://stackoverflow.com/questions/30054419/gridview-inside-tab-fragment

        //https://github.com/augusto/android-sandbox/blob/master/src/com/augusto/mymediaplayer/services/AudioPlayer.java   This is probably it

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

    public void loadColours() {

        try {
            JSONObject Colours = new JSONObject(loadJsonFile("Colours" + ".json"));
            JSONArray RedCol = Colours.getJSONArray("Red");
            JSONArray GreenCol = Colours.getJSONArray("Green");
            JSONArray BlueCol = Colours.getJSONArray("Blue");

            for (int i = 0; i < RedCol.length(); i++) {
                Red.add((Integer) RedCol.get(i));
                Green.add((Integer) GreenCol.get(i));
                Blue.add((Integer) BlueCol.get(i));
            }

        } catch (JSONException e) {
            int hi = 0;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void SetBackgroundColor() {

        if (AppAppearance.equals("Dark")) {
            int Pos = 0;

            PlayInfo = findViewById(R.id.PlayInfo);

            ArrayList<Integer> RedImg = new ArrayList<>();
            ArrayList<Integer> BlueImg = new ArrayList<>();
            ArrayList<Integer> GreenImg = new ArrayList<>();

            Bitmap myBitmap = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    myBitmap = getApplicationContext().getContentResolver().loadThumbnail(Uri.parse(ImagePath), new Size(1024, 1024), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                myBitmap = BitmapFactory.decodeFile(ImagePath);
            }
            if (myBitmap != null) {
                int width = myBitmap.getWidth();
                int height = myBitmap.getHeight();

                boolean Grey = true;

                for (int i = 0; i < width / 2; i++) {
                    for (int e = 0; e < height / 2; e++) {

                        int pixel = myBitmap.getPixel(i * 2, e * 2);

                        if (Color.red(pixel) - 50 <= Color.green(pixel) && Color.green(pixel) <= Color.red(pixel) + 50 && Color.red(pixel) - 50 <= Color.blue(pixel) && Color.blue(pixel) <= Color.red(pixel) + 50) {

                        } else {
                            RedImg.add(Color.red(pixel));
                            GreenImg.add(Color.green(pixel));
                            BlueImg.add(Color.blue(pixel));
                            Grey = false;
                        }
                    }
                    //Colors.add(myBitmap.getColor((myBitmap.getWidth()/10)*i, (myBitmap.getHeight()/10)*i));
                }

                int RedAv = 0;
                int GreenAv = 0;
                int BlueAv = 0;

                if (Grey) {

                    for (int i = 0; i < width / 2; i++) {
                        for (int e = 0; e < height / 2; e++) {
                            int pixel = myBitmap.getPixel(i * 2, e * 2);
                            RedImg.add(Color.red(pixel));
                            GreenImg.add(Color.green(pixel));
                            BlueImg.add(Color.blue(pixel));
                        }
                    }
                }

                for (int i = 0; i < RedImg.size(); i++) {
                    RedAv = RedAv + RedImg.get(i);
                }

                for (int i = 0; i < GreenImg.size(); i++) {
                    GreenAv = GreenAv + GreenImg.get(i);
                }

                for (int i = 0; i < BlueImg.size(); i++) {
                    BlueAv = BlueAv + BlueImg.get(i);
                }

                RedAv = RedAv / RedImg.size();
                GreenAv = GreenAv / GreenImg.size();
                BlueAv = BlueAv / BlueImg.size();

                double Dif = 10000; //Make a high difference

                if (Grey) {

                    int Dif1 = 0;
                    int Dif2 = 0;
                    int Dif3 = 0;

                    Dif1 = RedAv - 53;
                    Dif2 = RedAv - 102;
                    Dif3 = RedAv - 153;

                    if (Dif1 < Dif2 && Dif1 < Dif3) {
                        if (AppAppearance.equals("Dark")) {
                            // MusicPlayerBackground.setBackgroundColor(Color.rgb(53, 53, 53));
                            Pos = 53;
                            AnimateBackground(Pos, true);
                            LRed = 53;
                            LGreen = 53;
                            LBlue = 53;
                        }
                    } else if (Dif2 < Dif1 && Dif2 < Dif3) {
                        if (AppAppearance.equals("Dark")) {
                            //MusicPlayerBackground.setBackgroundColor(Color.rgb(102, 102, 102));
                            Pos = 102;
                            AnimateBackground(Pos, true);
                            LRed = 102;
                            LGreen = 102;
                            LBlue = 102;
                        }
                    } else if (Dif3 < Dif1 && Dif3 < Dif2) {
                        if (AppAppearance.equals("Dark")) {
                            // MusicPlayerBackground.setBackgroundColor(Color.rgb(153, 153, 153));
                            Pos = 153;
                            AnimateBackground(Pos, true);
                            LRed = 153;
                            LGreen = 153;
                            LBlue = 153;
                        }
                    }

                    ChangeTextColour(true);

                    // }
                } else {
                    for (int i = 0; i < Red.size(); i++) {

                        double CurDif = 0;

                        double RedMean = (RedAv + Red.get(i)) / 2;

                        // double RedDif = (RedAv - CLBRed[i]) *0.30;
                        //double GreenDif = (GreenAv - CLBGreen[i]) *0.59;
                        //double BlueDif = (BlueAv - CLBBlue[i]) *0.11;

                        double RedDif = Math.pow(((RedAv - Red.get(i))), 2);
                        double GreenDif = Math.pow(((GreenAv - Green.get(i))), 2);
                        double BlueDif = Math.pow(((BlueAv - Blue.get(i))), 2);

                        CurDif = Math.sqrt(((2 + (RedMean / 256)) * RedDif) + (4 * GreenDif) + (2 + ((255 - RedMean) / 256)) * BlueDif);

                        if (Dif > CurDif) {
                            Dif = CurDif;
                            Pos = i;
                        }
                    }

                    if (AppAppearance.equals("Dark")) {
                        // MusicPlayerBackground.setBackgroundColor(Color.rgb(Red.get(Pos), Green.get(Pos), Blue.get(Pos)));
                        AnimateBackground(Pos, false);
                        LRed = Red.get(Pos);
                        LGreen = Green.get(Pos);
                        LBlue = Blue.get(Pos);
                        if (LRed + LGreen + LBlue > 128) {
                            ChangeTextColour(false);
                        } else {
                            ChangeTextColour(true);
                        }
                    }
                }
            } else {

                if (AppAppearance.equals("Dark")) {
                    Pos = 153;
                    AnimateBackground(Pos, true);
                    LRed = 153;
                    LGreen = 153;
                    LBlue = 153;
                    ChangeTextColour(true);
                    //MusicPlayerBackground.setBackgroundColor(Color.rgb(153, 153, 153));
                }
            }
        }
    }

    public void AnimateBackground(int Pos, boolean Grey) {
        if (Grey) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.rgb(LRed, LGreen, LBlue), Color.rgb(Pos, Pos, Pos));
            colorAnimation.setDuration(2000); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    MusicPlayerBackground.setBackgroundColor((int) animator.getAnimatedValue());
                    QueueList.setBackgroundColor((int) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();
        } else {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.rgb(LRed, LGreen, LBlue), Color.rgb(Red.get(Pos), Green.get(Pos), Blue.get(Pos)));
            colorAnimation.setDuration(2000); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    MusicPlayerBackground.setBackgroundColor((int) animator.getAnimatedValue());
                    QueueList.setBackgroundColor((int) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void ChangeTextColour(boolean White) {
        if (White) {
            MusicPlayerBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.background_alex3));
            PlayInfo.setBackground(ContextCompat.getDrawable(this, R.drawable.musicplayerforeground4));
            for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setTextColor(Color.rgb(255, 255, 255));
            }
            //tabHost.getTabWidget().setBackgroundColor(Color.rgb(255, 255, 255));
            tabHost.getTabWidget().setOutlineAmbientShadowColor(Color.rgb(255, 255, 255));
        } else {
            MusicPlayerBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.background_alex3));
            PlayInfo.setBackground(ContextCompat.getDrawable(this, R.drawable.musicplayerforeground4));
            for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setTextColor(Color.rgb(0, 0, 0));
            }
            //tabHost.getTabWidget().setBackgroundColor(Color.rgb(0, 0, 0));
            tabHost.getTabWidget().setOutlineAmbientShadowColor(Color.rgb(0, 0, 0));
        }

    }

    @Override
    public void onDestroy() {

        //MusicAudioPlayer.release();
        super.onDestroy();
        //SaveInfo();
        //Release();
    }

    public void AntennaSetup() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UpdateTime");
        intentFilter.addAction("UpdateDisplayInfo");
        intentFilter.addAction("UpdateFileTime");
        intentFilter.addAction("UpdateFilePlays");
        intentFilter.addAction("SaveFile");
        registerReceiver(AntennaReceiver, intentFilter);
    }

    public void GetInfo() {
        //Get all this shitty info
        Intent Stuff = getIntent();

        MiniPlayer = Stuff.getBooleanExtra("MiniPlayerStart", false);
        Paused = Stuff.getBooleanExtra("Paused", false);

        if (MiniPlayer) {
            SongID = Stuff.getStringExtra("SongID");
            SongPos = Stuff.getIntExtra("SongPos", 0);

            /*
            TryCatch(Json, "Queue", Queue);
            TryCatch(Json, "QueuePath", QueuePath);
            TryCatch(Json, "ArrayLength", ArrayLength);
            TryCatch(Json, "QueueID", QueueIDs);
            TryCatch(Json, "Position", Position);
            TryCatch(Json, "FilePath", FPath);
            TryCatch(Json, "SortMethod", Sort);
            TryCatch(Json, "FinalSongQueue", FSongQueue);
            TryCatch(Json, "Playlist", IsPlaylist);
            TryCatch(Json, "SongChecker", CheckSong);
            TryCatch(Json, "GCPos", GCPos);

             */

            try {
                JSONObject QueueFile = new JSONObject(loadJsonFile("QueueList" + ".json"));
                JSONArray Sort = QueueFile.getJSONArray("SortMethod");
                JSONArray Path = QueueFile.getJSONArray("FilePath");
                JSONArray SongQueue = QueueFile.getJSONArray("FinalSongQueue");
                JSONArray PlayL = QueueFile.getJSONArray("Playlist");
                JSONArray SongCheckLength = QueueFile.getJSONArray("SongCheckerLength");
                JSONArray ObMeth = QueueFile.getJSONArray("ObjectMethod");
                JSONArray HeaderQueue = QueueFile.getJSONArray("FinalHeaderQueue");

                JSONArray BarQueueLength = QueueFile.getJSONArray("FinalBarQueueLength");

                int length = (int) SongCheckLength.get(0);
                int length2 = (int) BarQueueLength.get(0);

                for (int i = 0; i < length; i ++) {
                    SongChecker.add(new ArrayList<Integer>());
                    JSONArray CheckSong = QueueFile.getJSONArray("SongChecker" +i);
                    for (int e = 0; e < CheckSong.length(); e ++) {
                        SongChecker.get(i).add((Integer) CheckSong.get(e));
                    }
                    //SongChecker.get(i).add((Integer) CheckSong.get(i));
                }

                for (int i = 0; i < length2; i ++) {
                    FinalBarQueue.add(new ArrayList<Integer>());
                    JSONArray BarQueue = QueueFile.getJSONArray("FinalBarQueue"+i);
                    for (int e = 0; e < BarQueue.length(); e ++) {
                       FinalBarQueue.get(i).add((Integer) BarQueue.get(e));
                    }
                }

                for (int i = 0; i < SongQueue.length(); i ++) {
                    FinalSongQueue.add((Integer) SongQueue.get(i));
                }

                for (int i = 0; i < HeaderQueue.length(); i ++) {
                    FinalHeaderQueue.add((Integer) HeaderQueue.get(i));
                }

                //SongChecker = (ArrayList<ArrayList<Integer>>) CheckSong.get(0);
                SortMethod = (String) Sort.get(0);
                FilePath = (String) Path.get(0);
                Playlist = (Boolean) PlayL.get(0);
                ObjectMethod = (String) ObMeth.get(0);

                int hi = 0;


            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            LoopLength = Stuff.getIntExtra("SongCheckerLength", 0);

            for (int i = 0; i < LoopLength; i++) {
                SongChecker.add(Stuff.getIntegerArrayListExtra("SongChecker" + i));
            }

            SelectedQueue = Stuff.getStringArrayListExtra("SelectedQueue");
            //AllTags = Stuff.getStringArrayListExtra("AllTags");
            //AllMoods = Stuff.getStringArrayListExtra("AllMoods");

            QueueMethod = Stuff.getStringExtra("QueueMethod");
            GroupPosition = Stuff.getIntExtra("GroupPos", 0);
            ChildPosition = Stuff.getIntExtra("ChildPos", 0);
            ObjectMethod = Stuff.getStringExtra("ObjectMethod");
            SortMethod = Stuff.getStringExtra("SortMethod");
            Playlist = Stuff.getBooleanExtra("Playlist", false);
            FilePath = Stuff.getStringExtra("FilePath");
        }

        //File Path, Playlist,

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

    public void TryCatch(JSONObject Json, String Name, JSONArray JsonA) {
        try {
            Json.put(Name, JsonA);
        } catch (JSONException e) {
            e.printStackTrace();
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

        if (Playlist) {
            //Playlist
            try {
                JSONObject InfoFile = new JSONObject(loadJsonFile(FilePath + ".json"));
                JSONArray TotalSongNum = InfoFile.getJSONArray("TotalSongNum");
                JSONArray TotalArtistNum = InfoFile.getJSONArray("TotalArtistNum");
                JSONArray TotalAlbumNum = InfoFile.getJSONArray("TotalAlbumNum");
                JSONArray AllTagsArray = InfoFile.getJSONArray("AllTags");
                JSONArray AllMoodsArray = InfoFile.getJSONArray("AllMoods");

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
                FilePath = "MasterSongDataFile";
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

        String TimeDisplay;

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

    public void SortEverything() {

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
                        Combined.add(PlaylistAlbumID.get(position).get(i) + "  " + PlaylistAlbumID.get(13).get(i));
                    } else {
                        Combined.add(PlaylistAlbumID.get(position).get(i) + "  " + PlaylistAlbumID.get(10).get(i));
                    }
                }

                Collections.sort(Combined);
                Loop = PlaylistAlbumID.get(position).size();

                //Determine Old Placements
                if (Playlist) {
                    for (int i = 0; i < Loop; i++) {
                        for (int e = 0; e < Loop; e++) {
                            if (Combined.get(i).contains(Copy.get(position).get(e)) && Combined.get(i).contains(Copy.get(13).get(e)) && (!oldPlacement.contains(e))) {
                                oldPlacement.add(e);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < Loop; i++) {
                        for (int e = 0; e < Loop; e++) {
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

    public void GetPlayInfoActivity() {

        //View view = getLayoutInflater().inflate(R.layout.activity_music_player, null);

        PISongImage = findViewById(R.id.PISongImage);

        PILastSong = findViewById(R.id.PILastSong);
        PIRewind = findViewById(R.id.PIRewind);
        PIPlayPause = findViewById(R.id.PIPlayPause);
        PIForward = findViewById(R.id.PIForward);
        PINextSong = findViewById(R.id.PINextSong);

        PISongName = findViewById(R.id.PISongName);
        //PIArtistPlaylistRank = findViewById(R.id.PIArtistPlaylistRank);
        //PIArtistUniversalRank = findViewById(R.id.PIArtistUniversalRank);
        PISongPlaylistRank = findViewById(R.id.PISongPlaylistRank);
        PISongUniversalRank = findViewById(R.id.PISongUniversalRank);
        PISongArtist = findViewById(R.id.PISongArtist);
        PICurrentTime = findViewById(R.id.PICurrentTime);
        PITotalTime = findViewById(R.id.PITotalTime);
        PIPercentageComplete = findViewById(R.id.PIPercentageComplete);

        PISeekBar = findViewById(R.id.PISeekBar);

        if (AppAppearance.equals("Dark")) {
            PILastSong.setColorFilter(Color.rgb(255, 255, 255));
            PIRewind.setColorFilter(Color.rgb(255, 255, 255));
            PIPlayPause.setColorFilter(Color.rgb(255, 255, 255));
            PIForward.setColorFilter(Color.rgb(255, 255, 255));
            PINextSong.setColorFilter(Color.rgb(255, 255, 255));
            PISongName.setTextColor(Color.rgb(255, 255, 255));
            PISongPlaylistRank.setTextColor(Color.rgb(255, 255, 255));
            PISongUniversalRank.setTextColor(Color.rgb(255, 255, 255));
            PISongArtist.setTextColor(Color.rgb(255, 255, 255));
            PICurrentTime.setTextColor(Color.rgb(255, 255, 255));
            PITotalTime.setTextColor(Color.rgb(255, 255, 255));
            PIPercentageComplete.setTextColor(Color.rgb(255, 255, 255));
        }

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

        if (AppAppearance.equals("Dark")) {
            SongName.setTextColor(Color.rgb(255, 255, 255));
            SongArtist.setTextColor(Color.rgb(255, 255, 255));
            SongLength.setTextColor(Color.rgb(255, 255, 255));
            SongBar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.songbarobjectdark2));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetPlayInfoActivity(final int pos) {

        PISongImage.getLayoutParams().width = (width * 4) / 5;
        PISongImage.getLayoutParams().height = (width * 4) / 5;

        if (PlaylistSongID.get(4).get(FinalSongQueue.get(pos)).equals(null)) {
            Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(PISongImage);
        } else {
            GlideDisplay(FinalSongQueue.get(pos), PISongImage, "Song");
            ImagePath = PlaylistSongID.get(4).get(FinalSongQueue.get(pos));

            SetBackgroundColor();
            //Glide.with(this).load(PlaylistSongID.get(4).get(FinalSongQueue.get(pos))).placeholder(R.drawable.placeholder).animate(AnimationCode).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(PISongImage);
        }

        PISongName.setText(PlaylistSongID.get(0).get(FinalSongQueue.get(pos)));
        String Length = PlaylistSongID.get(0).get(FinalSongQueue.get(pos));
        if (Length.length() >= 18) {
            PISongName.setSelected(true);
            PISongName.setMaxEms(18);
        }
        PISongArtist.setText(PlaylistSongID.get(1).get(FinalSongQueue.get(pos)));

        if (Playlist) {
            PISongPlaylistRank.setText(PlaylistSongID.get(5).get(FinalSongQueue.get(pos)));
            PISongUniversalRank.setText(PlaylistSongID.get(6).get(FinalSongQueue.get(pos)));
            PITotalTime.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(11).get(FinalSongQueue.get(pos)))));
        } else {
            PISongUniversalRank.setText(PlaylistSongID.get(5).get(FinalSongQueue.get(pos)));
            PITotalTime.setText(ClockDisplay(Integer.parseInt(PlaylistSongID.get(8).get(FinalSongQueue.get(pos)))));
        }

    }

    public void UpdateTimeInfo(Intent intent) {

        double Percent = intent.getDoubleExtra("Percentage", 0);
        int Time = intent.getIntExtra("CurrentTime", 0);
        PIPercentageComplete.setText((int) Percent + "%");
        PICurrentTime.setText(ClockDisplay(Time));
        PISeekBar.setProgress((int) Percent);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetRegularInfoSongs(int i, String Mode) {
        //SongImage.setImageResource(Integer.parseInt(PlaylistSongID.get(4).get(i)));

        switch (Mode) {
            case "Song":

                // if (PlaylistSongID.get(4).get(i).equals(null)) {
                //   Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                // } else {
                GlideDisplay(i, SongImage, "Song");
                //Glide.with(this).load(PlaylistSongID.get(4).get(i)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                // }
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

                // if (PlaylistArtistID.get(1).get(i).equals(null)) {
                //   Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                //} else {
                GlideDisplay(i, SongImage, "Artist");
                // Glide.with(this).load(PlaylistArtistID.get(1).get(i)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                // }
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

                //  if (PlaylistAlbumID.get(2).get(i).equals(null)) {
                Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                // } else {
                GlideDisplay(i, SongImage, "Album");
                //Glide.with(this).load(PlaylistAlbumID.get(2).get(i)).placeholder(R.drawable.placeholder).fitCenter().into(SongImage);
                // }
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetExtraInfoSongs(int i) {
        SetRegularInfoSongs(i, "Song");
        SongPR.setText(PlaylistSongID.get(5).get(i));
        SongUR.setText(PlaylistSongID.get(6).get(i));
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void SetHeaderBarInfo(String Type, int pos) {

        int TotalLength = 0;
        switch (Type) {
            case "Album":
                if (PlaylistAlbumID.get(2).get(FinalHeaderQueue.get(pos)).equals(null)) {
                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().diskCacheStrategy(DiskCacheStrategy.NONE).into(SongImage);
                } else {
                    GlideDisplay(FinalHeaderQueue.get(pos), SongImage, "Album");

                    //Glide.with(this).load(PlaylistAlbumID.get(2).get(FinalHeaderQueue.get(pos))).placeholder(R.drawable.placeholder).fitCenter().diskCacheStrategy(DiskCacheStrategy.NONE).into(SongImage);
                }
                //Glide.with(this).load(PlaylistAlbumID.get(2).get(FinalHeaderQueue.get(pos))).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(SongImage);
                SongName.setText(Title(PlaylistAlbumID.get(0).get(FinalHeaderQueue.get(pos))));
                SongArtist.setText(Title(PlaylistAlbumID.get(1).get(FinalHeaderQueue.get(pos))));

                if (Playlist) {
                    TotalLength = Integer.parseInt(PlaylistAlbumID.get(9).get(FinalHeaderQueue.get(pos)));
                } else {
                    TotalLength = Integer.parseInt(PlaylistAlbumID.get(6).get(FinalHeaderQueue.get(pos)));
                }
                SongLength.setText(ClockDisplay(TotalLength));


                break;
            case "Artist":

                if (PlaylistArtistID.get(1).get(FinalHeaderQueue.get(pos)).equals(null)) {
                    Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().diskCacheStrategy(DiskCacheStrategy.NONE).into(SongImage);
                } else {
                    GlideDisplay(FinalHeaderQueue.get(pos), SongImage, "Artist");

                    //Glide.with(this).load(PlaylistArtistID.get(1).get(FinalHeaderQueue.get(pos))).placeholder(R.drawable.placeholder).fitCenter().diskCacheStrategy(DiskCacheStrategy.NONE).into(SongImage);
                }
                SongName.setText(Title(PlaylistArtistID.get(0).get(FinalHeaderQueue.get(pos))));
                SongArtist.setText(" ");
                if (Playlist) {
                    TotalLength = Integer.parseInt(PlaylistArtistID.get(8).get(FinalHeaderQueue.get(pos)));
                } else {
                    TotalLength = Integer.parseInt(PlaylistArtistID.get(5).get(FinalHeaderQueue.get(pos)));
                }
                SongLength.setText(ClockDisplay(TotalLength));

                break;
            case "Song":

                Glide.with(this).load(R.drawable.dfltplayimg).placeholder(R.drawable.placeholder).fitCenter().diskCacheStrategy(DiskCacheStrategy.NONE).into(SongImage);

                SongName.setText("Songs");
                SongArtist.setText("");
                if (Playlist) {
                    for (int i = 0; i < FinalBarQueue.get(pos).size(); i++) {
                        TotalLength = TotalLength + Integer.parseInt(PlaylistSongID.get(11).get(FinalBarQueue.get(pos).get(i)));
                    }
                } else {
                    for (int i = 0; i < FinalBarQueue.get(pos).size(); i++) {
                        TotalLength = TotalLength + Integer.parseInt(PlaylistSongID.get(8).get(FinalBarQueue.get(pos).get(i)));
                    }
                }
                SongLength.setText(ClockDisplay(TotalLength));


                break;
            case "Playlist Rank":

                Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).fitCenter().diskCacheStrategy(DiskCacheStrategy.NONE).into(SongImage);

                SongName.setText("Playlist Rank " + FinalHeaderQueue.get(pos));

                SongArtist.setText(" ");

                TotalLength = 0;

                for (int i = 0; i < FinalBarQueue.get(pos).size(); i++) {
                    TotalLength = TotalLength + Integer.parseInt(PlaylistSongID.get(1).get(FinalBarQueue.get(pos).get(i)));
                }
                SongLength.setText(ClockDisplay(TotalLength));

                break;
            case "Universal Rank":

                Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(SongImage);

                SongName.setText("Universal Rank " + FinalHeaderQueue.get(pos));

                SongArtist.setText(" ");

                TotalLength = 0;

                for (int i = 0; i < FinalBarQueue.get(pos).size(); i++) {
                    TotalLength = TotalLength + Integer.parseInt(PlaylistSongID.get(11).get(FinalBarQueue.get(pos).get(i)));
                }
                SongLength.setText(ClockDisplay(TotalLength));
                break;
            case "Tag":

                Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(SongImage);

                SongName.setText(AllTags.get(FinalHeaderQueue.get(pos)));

                SongArtist.setText(" ");

                TotalLength = 0;

                for (int i = 0; i < FinalBarQueue.get(pos).size(); i++) {
                    TotalLength = TotalLength + Integer.parseInt(PlaylistSongID.get(11).get(FinalBarQueue.get(pos).get(i)));
                }
                SongLength.setText(ClockDisplay(TotalLength));

                break;
            case "Mood":

                Glide.with(this).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(SongImage);

                SongName.setText(AllMoods.get(FinalHeaderQueue.get(pos)));

                SongArtist.setText(" ");

                TotalLength = 0;

                for (int i = 0; i < FinalBarQueue.get(pos).size(); i++) {
                    TotalLength = TotalLength + Integer.parseInt(PlaylistSongID.get(11).get(FinalBarQueue.get(pos).get(i)));
                }
                SongLength.setText(ClockDisplay(TotalLength));

                break;

        }

        SongLength.setText(ClockDisplay(TotalLength));

    }

    public void Setup() {

        //All queue methods
        //Album, Artist, Audio Mux, Mix, Time constraint, Follow Rule, Regular Queue, Tap (Tap on a song)

        //Sorting methods
        //Regular Play (Do nothing), Universal rank, Playlist Rank, A-Z, Z-A, Tags, Mood, Shuffle,
        QueueMethod = "Tap";

        switch (QueueMethod) {
            case "Album":
                //Group songs from same album up

                break;
            case "Artist":
                //Group songs from same Artist up

                break;
            case "Audio Mux":
                //Audio mux, Adjust speed, audio settings (example: Bass Boosted, Nightcore and other types)
                //Add these in future updates/focus on basic settings first
                //Make these customizable for individual songs, albums, ect.

                break;
            case "Mix":
                //kinda like time constraint but tries to calculate the hype moments/best moments and only plays those kinda like variable time constraint
                //Add these in future updates/focus on basic settings first
                //Make these customizable for individual songs, albums, ect.
                break;

            case "Time Constraint":
                //Fit x amount of songs in y amount of minutes (by cutting parts of the beginning and end and shit)
                //Add these in future updates/focus on basic settings first
                //Make these customizable for individual songs, albums, ect.
                break;
            case "Follow Rule":
                //Setup rules like repeat x song every y songs or some shit. ect, get unique with it.
                //Add these in future updates/focus on basic settings first
                //Make these customizable for individual songs, albums, ect.
                break;
            case "Queue":

                break;

            case "Tap":
                //Just click on a random song
                TapMethod();
                break;
        }

    }


    public void TapMethod() {

        ArrayList<Integer> HeaderIDPos = new ArrayList<>();
        ArrayList<Integer> HeaderArrayPos = new ArrayList<>();
        if (Playlist) {
            //Playlist
            switch (ObjectMethod) {

                case "Album":

                    for (int i = 0; i < SongChecker.size(); i++) {
                        HeaderIDPos.add(Integer.parseInt(PlaylistSongID.get(15).get(SongChecker.get(i).get(0))));
                    }

                    for (int i = 0; i < HeaderIDPos.size(); i++) {
                        for (int e = 0; e < PlaylistAlbumID.get(13).size(); e++) {
                            if (HeaderIDPos.get(i).equals(Integer.parseInt(PlaylistAlbumID.get(13).get(e)))) {
                                HeaderArrayPos.add(e);
                                e = PlaylistAlbumID.get(13).size();
                            }
                        }
                    }

                    break;
                case "Artist":

                    for (int i = 0; i < SongChecker.size(); i++) {
                        HeaderIDPos.add(Integer.parseInt(PlaylistSongID.get(16).get(SongChecker.get(i).get(0))));
                    }

                    for (int i = 0; i < HeaderIDPos.size(); i++) {
                        for (int e = 0; e < PlaylistArtistID.get(12).size(); e++) {
                            if (HeaderIDPos.get(i).equals(Integer.parseInt(PlaylistArtistID.get(12).get(e)))) {
                                HeaderArrayPos.add(e);
                                e = PlaylistArtistID.get(12).size();
                            }
                        }
                    }

                    break;
                case "Song":
                    //Do nothing?
                    //Well it's not do nothing, Maybe?

                    break;
                case "Playlist Rank":

                    for (int i = 0; i < SongChecker.size(); i++) {
                        HeaderArrayPos.add(Integer.parseInt(PlaylistSongID.get(5).get(SongChecker.get(i).get(0))));
                    }

                    break;
                case "Universal Rank":

                    for (int i = 0; i < SongChecker.size(); i++) {
                        HeaderArrayPos.add(Integer.parseInt(PlaylistSongID.get(6).get(SongChecker.get(i).get(0))));
                    }

                    break;
                case "Tag":
                    //Maybe a little to complicated

                    for (int i = 0; i < SongChecker.size(); i++) {
                        for (int e = 0; e < AllTags.size(); e++) {
                            if ((PlaylistSongID.get(9).get(SongChecker.get(i).get(0))).contains(AllTags.get(e))) //Going to have to make a system to check both Universal and Playlist
                            {
                                HeaderArrayPos.add(e);
                            }
                        }
                    }

                    break;
                case "Mood":
                    //Maybe a little too complicated
                    for (int i = 0; i < SongChecker.size(); i++) {
                        for (int e = 0; e < AllMoods.size(); e++) {
                            if ((PlaylistSongID.get(7).get(SongChecker.get(i).get(0))).contains(AllMoods.get(e))) //Going to have to make a system to check both Universal and Playlist
                            {
                                HeaderArrayPos.add(e);
                            }
                        }
                    }

                    break;
            }

        } else {
            //Master
            switch (ObjectMethod) {

                case "Album":

                    for (int i = 0; i < SongChecker.size(); i++) {
                        HeaderIDPos.add(Integer.parseInt(PlaylistSongID.get(12).get(SongChecker.get(i).get(0))));
                    }

                    for (int i = 0; i < HeaderIDPos.size(); i++) {
                        for (int e = 0; e < PlaylistAlbumID.get(10).size(); e++) {
                            if (HeaderIDPos.get(i).equals(Integer.parseInt(PlaylistAlbumID.get(10).get(e)))) {
                                HeaderArrayPos.add(e);
                                e = PlaylistAlbumID.get(10).size();
                            }
                        }

                    }

                    break;
                case "Artist":

                    for (int i = 0; i < SongChecker.size(); i++) {
                        HeaderIDPos.add(Integer.parseInt(PlaylistSongID.get(13).get(SongChecker.get(i).get(0))));
                    }

                    for (int i = 0; i < HeaderIDPos.size(); i++) {
                        for (int e = 0; e < PlaylistArtistID.get(9).size(); e++) {
                            if (HeaderIDPos.get(i).equals(Integer.parseInt(PlaylistArtistID.get(9).get(e)))) {
                                HeaderArrayPos.add(e);
                                e = PlaylistArtistID.get(9).size();
                            }
                        }
                    }

                    break;
                case "Song":
                    //Do nothing?
                    //Well it's not do nothing, Maybe?

                    break;
                case "Universal Rank":

                    for (int i = 0; i < SongChecker.size(); i++) {
                        HeaderArrayPos.add(Integer.parseInt(PlaylistSongID.get(5).get(SongChecker.get(i).get(0))));
                    }

                    break;
                case "Tag":
                    //Maybe a little to complicated

                    for (int i = 0; i < SongChecker.size(); i++) {
                        for (int e = 0; e < AllTags.size(); e++) {
                            if ((PlaylistSongID.get(7).get(SongChecker.get(i).get(0))).contains(AllTags.get(e))) //Going to have to make a system to check both Universal and Playlist
                            {
                                HeaderArrayPos.add(e);
                            }
                        }
                    }

                    break;
                case "Mood":
                    //Maybe a little too complicated
                    for (int i = 0; i < SongChecker.size(); i++) {
                        for (int e = 0; e < AllMoods.size(); e++) {
                            if ((PlaylistSongID.get(6).get(SongChecker.get(i).get(0))).contains(AllMoods.get(e))) //Going to have to make a system to check both Universal and Playlist
                            {
                                HeaderArrayPos.add(e);
                            }
                        }
                    }

                    break;
            }
        }

        //
        //Transfer to Finals
        //

        FinalHeaderQueue.clear();
        for (int i = 0; i < HeaderArrayPos.size(); i++) {
            FinalHeaderQueue.add(HeaderArrayPos.get(i));
        }

        for (int i = 0; i < SongChecker.size(); i++) {
            FinalBarQueue.add(new ArrayList<Integer>());
            for (int e = 0; e < SongChecker.get(i).size(); e++) {
                FinalBarQueue.get(i).add(SongChecker.get(i).get(e));
            }
        }

        for (int i = 0; i < SongChecker.size(); i++) {
            for (int e = 0; e < SongChecker.get(i).size(); e++) {
                FinalSongQueue.add(SongChecker.get(i).get(e));
            }
        }
        //FinalHeaderQueue.equals(HeaderPos);
        //FinalSongQueue.equals(SongChecker);

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void PlayMusic(int GP, int CP) {

        stopService(new Intent(this, AudioPlayer.class));

        //Position calculator
        int Pos = 0;
        boolean Count = true;

        for (int i = 0; i < SongChecker.size(); i++) {
            for (int e = 0; e < SongChecker.get(i).size(); e++) {
                if (i >= GP && e >= CP) {
                    //Do nothing?
                    Count = false;
                } else {
                    if (Count) {
                        Pos++;
                    }
                }
            }
        }

        GetPlayInfoActivity();
        SetPlayInfoActivity(Pos);
        StartMusicPlayer(Pos);

        //Update PlayInfo information and update Queue look?

    }

    public void StartSaveData() {

        stopService(new Intent(this, SaveSongData.class));

        JSONObject Json = new JSONObject();

        JSONArray PlaylistBool = new JSONArray();
        JSONArray FilePathString = new JSONArray();

        PlaylistBool.put(Playlist);
        FilePathString.put(FilePath);

        TryCatch(Json, "Playlist", PlaylistBool);
        TryCatch(Json, "FilePath", FilePathString);

        saveToStorage("SaveSong", Json);

        startService(new Intent(this, SaveSongData.class));
    }

    public void StartMusicPlayer(int pos) {

        //Just so that we can corralate (we can delete this later)
        JSONObject Json = new JSONObject();

        JSONArray Queue = new JSONArray();
        JSONArray QueuePath = new JSONArray();
        JSONArray QueueIDs = new JSONArray();
        JSONArray ArrayLength = new JSONArray();
        JSONArray Position = new JSONArray();
        JSONArray FPath = new JSONArray();

        JSONArray Sort = new JSONArray();
        JSONArray FSongQueue = new JSONArray();
        JSONArray IsPlaylist = new JSONArray();
        JSONArray GCPos = new JSONArray();
JSONArray SongCheckLength = new JSONArray();
JSONArray ObMeth = new JSONArray();
JSONArray HeaderQueue = new JSONArray();
JSONArray FinalBarQueueLength = new JSONArray();

        //Queue.equals(FinalSongQueue);
        for (int i = 0; i < FinalSongQueue.size(); i++) {
            Queue.put(FinalSongQueue.get(i));
            QueuePath.put(PlaylistSongID.get(3).get(FinalSongQueue.get(i)));
            if (Playlist) {
                QueueIDs.put(PlaylistSongID.get(18).get(FinalSongQueue.get(i)));
            } else {
                QueueIDs.put(PlaylistSongID.get(15).get(FinalSongQueue.get(i)));
            }
        }



        for (int i = 0; i < FinalHeaderQueue.size(); i ++) {
           HeaderQueue.put(FinalHeaderQueue.get(i));
        }

        for (int i = 0; i < FinalSongQueue.size(); i ++) {
            FSongQueue.put(FinalSongQueue.get(i));
        }

        for (int i = 0; i < SongChecker.size(); i++) {
            JSONArray CheckSong = new JSONArray();
            for (int e = 0; e < SongChecker.get(i).size(); e ++) {
                CheckSong.put(SongChecker.get(i).get(e));
            }
            TryCatch(Json, "SongChecker" + i, CheckSong);
        }

        for (int i = 0; i < FinalBarQueue.size(); i++) {
            JSONArray BarQueue = new JSONArray();
            for (int e = 0; e <FinalBarQueue.get(i).size(); e ++) {
                BarQueue.put(SongChecker.get(i).get(e));
            }
            TryCatch(Json, "FinalBarQueue" + i, BarQueue);
        }

        ArrayLength.put(FinalSongQueue.size());
        Position.put(pos);

        FPath.put(FilePath);
        Sort.put(SortMethod);



        IsPlaylist.put(Playlist);
        SongCheckLength.put(SongChecker.size());
        FinalBarQueueLength.put(FinalBarQueue.size());


        GCPos.put(GroupPosition);
        GCPos.put(ChildPosition);
        ObMeth.put(ObjectMethod);

        TryCatch(Json, "Queue", Queue);
        TryCatch(Json, "QueuePath", QueuePath);
        TryCatch(Json, "ArrayLength", ArrayLength);
        TryCatch(Json, "QueueID", QueueIDs);
        TryCatch(Json, "Position", Position);
        TryCatch(Json, "FilePath", FPath);
        TryCatch(Json, "SortMethod", Sort);
        TryCatch(Json, "FinalSongQueue", FSongQueue);
        TryCatch(Json, "Playlist", IsPlaylist);
        //TryCatch(Json, "SongChecker", CheckSong);
        TryCatch(Json, "SongCheckerLength", SongCheckLength);
        TryCatch(Json, "GCPos", GCPos);
        TryCatch(Json, "ObjectMethod", ObMeth);
        TryCatch(Json, "FinalHeaderQueue", HeaderQueue);
        TryCatch(Json, "FinalBarQueueLength", FinalBarQueueLength);

        saveToStorage("QueueList", Json);

        PIPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);

        startService(new Intent(this, AudioPlayer.class));

        Controller();

    }

    public void Controller() {

        if (Paused) {
            PIPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        } else if (Paused == false) {
            PIPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
        }

        PILastSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Signal = new Intent();
                Signal.setAction("LastSong");
                sendBroadcast(Signal);
                AnimationCode = R.anim.fab_slide_in_from_left;
            }
        });

        PIRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Replace unit by number of seconds (10, 15, ect)
                Intent Signal = new Intent();
                Signal.setAction("Rewind");
                Signal.putExtra("Unit", 10);
                sendBroadcast(Signal);
            }
        });

        PIPlayPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Paused) {
                    //Resume Playing
                    Intent Signal = new Intent();
                    Signal.setAction("Resume");
                    sendBroadcast(Signal);
                    Paused = false;
                    PIPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
                } else if (Paused == false) {
                    //Pause
                    Intent Signal = new Intent();
                    Signal.setAction("Pause");
                    sendBroadcast(Signal);
                    Paused = true;
                    PIPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);

                }
                //
                //This doesn't want to work? either way figured it out, use broadcast receiver to send the signals for pause rewind ect
                //https://developer.android.com/guide/components/broadcasts#sending-broadcasts
                //

            }
        });

        PIForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Signal = new Intent();
                Signal.setAction("Forward");
                Signal.putExtra("Unit", 10);
                sendBroadcast(Signal);
            }
        });

        PINextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Signal = new Intent();
                Signal.setAction("NextSong");
                //Signal.putExtra("Unit", 10);
                sendBroadcast(Signal);
                AnimationCode = R.anim.fab_slide_in_from_right;
            }
        });

        PISeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (ManualSeek) {
                    Intent Signal = new Intent();
                    Signal.setAction("SeekBarChange");
                    double PercentSeek = PISeekBar.getProgress();
                    Signal.putExtra("Percent", PercentSeek);
                    sendBroadcast(Signal);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ManualSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ManualSeek = false;

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

    public void SendToInfoDisplay(final int GPos, final int CPos, final String ObjectMode) {

        if (SongImage != null) {
            SongImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendInfo(GPos, CPos, ObjectMode);
                }
            });
        }

    }

    public void SendInfo(int GPos, int CPos, String ObjectMode) {
        Intent InfoDisplay = new Intent(getApplicationContext(), InfoDisplay.class);

        InfoDisplay.putExtra("PlaylistName", FilePath);
        InfoDisplay.putExtra("Playlist", Playlist);

        switch (ObjectMode) {
            case "Song":
                if (Playlist) {
                    InfoDisplay.putExtra("FileID", PlaylistSongID.get(18).get(SongChecker.get(GPos).get(CPos)));
                    for (int i = 0; i < PlaylistSongID.size(); i++) {
                        InfoDisplay.putExtra("Value" + i, PlaylistSongID.get(i).get(SongChecker.get(GPos).get(CPos)));
                    }

                } else {
                    InfoDisplay.putExtra("FileID", PlaylistSongID.get(15).get(SongChecker.get(GPos).get(CPos)));
                    for (int i = 0; i < PlaylistSongID.size(); i++) {
                        InfoDisplay.putExtra("Value" + i, PlaylistSongID.get(i).get(SongChecker.get(GPos).get(CPos)));
                    }
                }
                break;
            case "Artist":

                if (Playlist) {
                    String ArtistID = PlaylistSongID.get(16).get(SongChecker.get(GPos).get(CPos)); //CPos should be 0
                    for (int i = 0; i < ArtistNum; i++) {
                        if (PlaylistArtistID.get(12).get(i).equals(ArtistID)) {
                            InfoDisplay.putExtra("FileID", PlaylistArtistID.get(14).get(i));
                            for (int e = 0; e < PlaylistArtistID.size(); e++) {
                                InfoDisplay.putExtra("Value" + e, PlaylistArtistID.get(e).get(i));
                            }
                        }
                    }
                } else {
                    String ArtistID = PlaylistSongID.get(13).get(SongChecker.get(GPos).get(CPos)); //CPos should be 0
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
                    String AlbumID = PlaylistSongID.get(15).get(SongChecker.get(GPos).get(CPos)); //CPos should be 0
                    for (int i = 0; i < AlbumNum; i++) {
                        if (PlaylistAlbumID.get(13).get(i).equals(AlbumID)) {
                            InfoDisplay.putExtra("FileID", PlaylistAlbumID.get(16).get(i));
                            for (int e = 0; e < PlaylistAlbumID.size(); e++) {
                                InfoDisplay.putExtra("Value" + e, PlaylistAlbumID.get(e).get(i));
                            }
                        }
                    }
                } else {
                    String AlbumID = PlaylistSongID.get(12).get(SongChecker.get(GPos).get(CPos)); //CPos should be 0
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

                // if (bitmap != null) {
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                Image.setImageBitmap(bitmap);
                // } else {

                // }
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
                Drawable drawable;
                if (AppAppearance.equals("Dark")) {
                    drawable = getResources().getDrawable(R.drawable.placeholderdark);
                } else {
                    drawable = getResources().getDrawable(R.drawable.placeholder);
                }
                Image.setImageDrawable(drawable);
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

    private class CustomAdapter0 extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            if (ObjectMethod.equals("Song")) {
                return 1;
            } else {
                return FinalHeaderQueue.size();
            }
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            return FinalBarQueue.get(groupPosition).size();
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
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            View GroupHeader = getLayoutInflater().inflate(R.layout.song_bar_object_big, parent, false);

            if (ObjectMethod.equals("Song")) {
                GetRegularBarsSongs(GroupHeader);
                SetHeaderBarInfo(ObjectMethod, groupPosition);
                QueueList.expandGroup(groupPosition);
            } else {
                GetRegularBarsSongs(GroupHeader);
                SetHeaderBarInfo(ObjectMethod, groupPosition);
                QueueList.expandGroup(groupPosition);
                SendToInfoDisplay(groupPosition, 0, ObjectMethod);
            }

            SongBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayMusic(groupPosition, 0);
                    AnimationCode = R.anim.fab_scale_up;
                }
            });

            return GroupHeader;
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            //View QueueBar = getLayoutInflater().inflate(R.layout.song_bar_object_small, parent, false);

            switch (ObjectType) {
                case "Small":
                    QueueBar = getLayoutInflater().inflate(R.layout.song_bar_object_small, parent, false);
                    GetRegularBarsSongs(QueueBar);
                    SetRegularInfoSongs(FinalBarQueue.get(groupPosition).get(childPosition), "Song");
                    SendToInfoDisplay(groupPosition, childPosition, "Song");
                    break;
                case "Small EI":
                    QueueBar = getLayoutInflater().inflate(R.layout.song_bar_object_small_extrainfo, parent, false);
                    GetExtraInfoBarsSongs(QueueBar);
                    SetExtraInfoSongs(FinalBarQueue.get(groupPosition).get(childPosition));
                    SendToInfoDisplay(groupPosition, childPosition, "Song");
                    break;
                case "Big":
                    QueueBar = getLayoutInflater().inflate(R.layout.song_bar_object_big, parent, false);
                    GetRegularBarsSongs(QueueBar);
                    SetRegularInfoSongs(FinalBarQueue.get(groupPosition).get(childPosition), "Song");
                    SendToInfoDisplay(groupPosition, childPosition, "Song");
                    break;
                case "Big EI":
                    QueueBar = getLayoutInflater().inflate(R.layout.song_bar_object_big_extrainfo, parent, false);
                    GetExtraInfoBarsSongs(QueueBar);
                    SetExtraInfoSongs(FinalBarQueue.get(groupPosition).get(childPosition));
                    SendToInfoDisplay(groupPosition, childPosition, "Song");
                    break;
            }

            //Somehow transport this to AudioPlayer?
            SongBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayMusic(groupPosition, childPosition);
                    AnimationCode = R.anim.fab_scale_up;
                }
            });

            return QueueBar;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    private class AntennaMusicPlayer extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onReceive(Context context, Intent intent) {

            String Action = intent.getAction();
            if ("UpdateDisplayInfo".equals(Action)) {
                int Pos = intent.getIntExtra("Position", 0);
                SetPlayInfoActivity(Pos);
                //Update Song Info
            } else if ("UpdateTime".equals(Action)) {
                //Update all the times and bars
                UpdateTimeInfo(intent);
            }

        }
    }

}
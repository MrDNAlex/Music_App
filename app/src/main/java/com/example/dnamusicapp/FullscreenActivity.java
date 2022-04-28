package com.example.dnamusicapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class FullscreenActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_MEDIA_LOCATION = 124;

    int[] CLBRed = {153, 204, 255, 255, 255, 255, 255, 153, 153, 102, 51, 0, 0, 51, 0, 0, 0, 0, 0, 0, 51, 0, 0, 102, 102, 51, 153, 204, 255, 255, 153, 255, 255, 204, 102, 255, 102, 255, 255, 204};
    int[] CLBGreen = {0, 0, 0, 51, 102, 204, 255, 255, 255, 255, 255, 255, 255, 204, 204, 255, 204, 153, 51, 102, 51, 0, 0, 51, 0, 0, 51, 0, 0, 102, 0, 51, 0, 0, 0, 102, 0, 153, 0, 0};
    int[] CLBBlue = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 204, 204, 204, 255, 255, 255, 102, 255, 204, 102, 204, 255, 255, 153, 255, 255, 255, 255, 51, 102, 51, 51, 255, 255, 153, 255, 102, 51};

    public Button button;
    Button WriteShit;
    TextView Message;
    //Switch Album to be right before, and since those last 3 are the exact same thing I just copy the same Info into them
    ArrayList<ArrayList<String>> AllData = new ArrayList<ArrayList<String>>();                //Set all data into this Arraylist                  Title, Artist, Duration, Path, Album, ArtistId, AlbumID, AlbumArt
    ArrayList<ArrayList<String>> UniqueDataToStorage = new ArrayList<ArrayList<String>>();//Title, Artist, Duration, Path, Album, ArtistID, AlbumID, AlbumArt, Album Artist Name, Album Artist ID
    String PlayVid = "DNA Music";
    VideoView IntroVid;

   // ProgressBar FirstProgress;
   // ProgressBar SecondProgress;
    ImageView Temp;
    SongClass Unique = new SongClass();



    public AntennaMessage AntennaReceiver = new AntennaMessage();

    //Have the unique stuff check both song name and artist. Because Alone by Marshmello and Alan Walker think they are the same

    //Remake entire Data management thing by Saturday Nov 14!!!!!!!!!!!********
    //So... ROG phone 2 peaks at 16 MB during the entire Saving process? at like 612 songs? So it probably isnt the number of songs that is causing Dads phone to crash
    //So new method goes to about 12.4 MB in RAM meaning there is around 20 - 25% more efficiency in RAM savings

    //Alright So all of this seems to working fine after the entire structure was changed, so it seems that the problem is somewhere in PlaylistSongView Activity, in the future also transport Album ID in the section to save eache individual songs and Album 
    LinearLayout Background;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);



        Background = findViewById(R.id.Background);
        Message = findViewById(R.id.Message);
        IntroVid = findViewById(R.id.IntroVideoView);
        //FirstProgress = findViewById(R.id.FirstProgress);
       // SecondProgress = findViewById(R.id.SecondProgress);
        Temp = findViewById(R.id.Temp);

        AntennaSetup();

        GetSettings();

        //Plays Video
        if (PlayVid != "None") {
            VideoStuff();

            Thread MessageThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //Make this variable for when it releases
                    SimpleDateFormat Sec = new SimpleDateFormat("ss");
                    Date currentTime = Calendar.getInstance().getTime();
                    String time1 = Sec.format(currentTime);
                    String time2;
                    boolean loop = true;
                    while (loop) {
                        currentTime = Calendar.getInstance().getTime();
                        time2 = Sec.format(currentTime);
                        if (Integer.parseInt(time2) > (Integer.parseInt(time1) + 5)) {
                            loop = false;
                            Intent DisMes = new Intent();
                            DisMes.setAction("DisplayMessage");
                            sendBroadcast(DisMes);
                        }
                    }
                }
            });
            MessageThread.start();
        } else {
            IntroVid.setVisibility(View.INVISIBLE);
        }

        IntroVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions( FullscreenActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                OpenPlaylistView();
            }
        });
        //DetectFiles();
    }

    public void AskPermission () {

        if (!Permissions.StoragePermissionGranted(this)) {
            new AlertDialog.Builder(this).setTitle("Access Storage Permission").setMessage("This App requires permissions to access your Music Files.").setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getPermission();
                }
            }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setIcon(R.drawable.dna_logo_circular).show();
        } else {
            Toast.makeText(this, "Already have Permission", Toast.LENGTH_LONG).show();
        }


    }

    private void getPermission () {
        //Settings.ACTION_MANAGE_APP_FILES_ACCESS_PERMISSION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }

        } else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {           //Asks for permission and shit
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted (ML)", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission NOT Granted (ML)", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }



        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0) {
            if (requestCode==101) {
                boolean readExt = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!readExt) {
                    getPermission();
                }
            }
        }
    }

    public void AntennaSetup() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("DisplayMessage");
        registerReceiver(AntennaReceiver, intentFilter);
    }

    public void DisplayMes() {

        Message.setText("Click anywhere to continue");
    }

    public void VideoStuff() {
        String videoPath = "";
        // IntroVid= findViewById(R.id.IntroVideoView);
        if (PlayVid.equals("DNA Music")) {
            videoPath = "android.resource://" + getPackageName() + "/" + R.raw.introvid;
        } else if (PlayVid.equals("OG")) {
            videoPath = "android.resource://" + getPackageName() + "/" + R.raw.idk;
        }

        Uri uri = Uri.parse(videoPath);
        IntroVid.setVideoURI(Uri.parse(videoPath));
        IntroVid.start();

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

    public void GetSettings() {
        try {
            InputStream InpStr = this.openFileInput("Settings.json");
            JSONObject InfoFile = new JSONObject(loadJsonFile("Settings" + ".json"));
            JSONArray PlayVideo = InfoFile.getJSONArray("PlayVideo");
            PlayVid = PlayVideo.get(0).toString();


        } catch (FileNotFoundException | JSONException e) {


        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void OpenPlaylistView() {
        try {
            InputStream InpStr = this.openFileInput("Settings.json");

        } catch (FileNotFoundException e) {
            getAllMusic();
            writeJSONDefaultData();
        }

        Intent intent = new Intent(this, PlaylistView.class);
        startActivity(intent);

    }

    public void writeJSONDefaultData() {
       // FirstProgress.setVisibility(View.VISIBLE);
       // SecondProgress.setVisibility(View.VISIBLE);
        int NumOfFiles = 7;
        for (int i = 0; i < NumOfFiles; i++) {
            JSONObject Json = new JSONObject();

            try {

                switch (i) {
                    case 0:
                        //Settings data

                        JSONArray Appearance = new JSONArray();
                        JSONArray Language = new JSONArray();
                        JSONArray Video = new JSONArray();

                        Appearance.put("DNA");
                        Language.put("English");
                        Video.put("DNA Music");

                        Json.put("AppearanceMode", Appearance);   //Dark mode, light mode, or custom
                        Json.put("Language", Language);   //All supported language options
                        Json.put("PlayVideo", Video);
                        saveToStorage("Settings", Json);

                        break;
                    case 1:
                        //viewPlaylistInfo data

                        viewPlaylistDefaultData(Json);
                        saveToStorage("viewPlaylistInfo", Json);

                        break;
                    case 2:
                        //MasterSongDataFile

                        masterSongDataFile(Json);
                        saveToStorage("MasterSongDataFile", Json);

                        break;
                    case 3:
                        //Gen MasterAlbumDataFile

                        // masterAlbumDataFile(Json);
                        // saveToStorage("MasterAlbumDataFile", Json);

                        break;
                    case 4:
                        //Gen MasterArtistDataFile

                        // masterArtistDataFile(Json);
                        // saveToStorage("MasterArtistDataFile", Json);

                        break;
                    case 5:
                        //Gen Everything Playlist

                        genEverythingPlaylist(Json);
                        saveToStorage("Everything_Playlist", Json);

                        break;
                    case 6:
                        //Other extra tasks
                        //Save Colours
                        saveColours();
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
          //  FirstProgress.setProgress(i/7);
        }
        Message.setText("Good to Go");
    }

    public void viewPlaylistDefaultData(JSONObject Json) {


        JSONArray SortMethod = new JSONArray();
        String Sort = "Alphabetical";
        SortMethod.put(Sort);


        //Number of Playlists
        JSONArray PlayLength = new JSONArray();
        int PlayNum = 1;
        PlayLength.put(PlayNum);

        //Number of Playlists
        JSONArray AreaLength = new JSONArray();
        int AreaNum = 1;
        AreaLength.put(AreaNum);

        //Account Name
        JSONArray AccountName = new JSONArray();
        AccountName.put("AccountName");

        try {
            Json.put("SortMethod", SortMethod);
            Json.put("NumOfPlaylist", PlayLength);
            Json.put("NumOfArea", AreaLength);
            Json.put("AccountName", AccountName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //
        //AllKeyWords
        //
        JSONArray AllKeyWords = new JSONArray();
        String keyWord1 = "All";
        AllKeyWords.put(keyWord1);
        TryCatch(Json, "AllKeyWords", AllKeyWords);

        //Save Areas
        String AreaID;
        String[] AreaNames = {"All My"};
        for (int i = 0; i < AreaNames.length; i++) {
            JSONArray AreaIDArray = new JSONArray();
            JSONArray KeyWords = new JSONArray();
            if (i == 0) {
                KeyWords.put(keyWord1);
            }
            AreaID = "ArID" + i;

            AreaIDArray.put(AreaNames[i]);
            AreaIDArray.put(KeyWords);
            AreaIDArray.put(AreaID);

            TryCatch(Json, AreaID, AreaIDArray);
        }

        String[] DefaultPlaylistNames = new String[PlayNum];
        DefaultPlaylistNames[0] = "Everything";

        String Name;
        String ImagePath;
        String PlaylistPath;
        int TimesUsed;
        String LastUse;
        String ID;

        String ending = "_Playlist";

        for (int i = 0; i < PlayNum; i++) {
            JSONArray PlaylistView = new JSONArray();

            ID = "PID" + i;
            Name = DefaultPlaylistNames[i];
            ImagePath = "Default";
            PlaylistPath = DefaultPlaylistNames[i] + ending;
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");
            Date currentTime = Calendar.getInstance().getTime();
            LastUse = format.format(currentTime);
            TimesUsed = 0;

            PlaylistView.put(Name);
            PlaylistView.put(PlaylistPath);
            PlaylistView.put(ImagePath);
            PlaylistView.put(AllKeyWords);
            PlaylistView.put(LastUse);
            PlaylistView.put(TimesUsed);
            PlaylistView.put(ID);

            TryCatch(Json, ID, PlaylistView);


        }
    }

    @SuppressLint("Range")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getAllMusic() {   //gets all music inside Music storage Library         String[] SongTitle, String[] Artist, String[] Album, int[] SongDur

        String[] projection = {MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.AudioColumns.ARTIST, MediaStore.Audio.AudioColumns.DURATION, MediaStore.Audio.Media.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.AudioColumns.ARTIST_ID, MediaStore.Audio.AudioColumns.ALBUM_ID};
        String selection = MediaStore.Audio.AudioColumns.IS_MUSIC;
       // String selection = null;
        String sortOrder = null;
        /*
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sortOrder = null;
        } else {
            sortOrder = MediaStore.Audio.Albums.ALBUM;
        }

         */
        sortOrder = MediaStore.Audio.Albums.ALBUM;

       // File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
       // File[] allFiles = musicDirectory.listFiles();


       //MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        for (int i = 0; i < projection.length; i++) {
            AllData.add(new ArrayList<String>());
            int Length;
            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortOrder);
           // Cursor songCursorInternal = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, projection, selection, null, sortOrder);
           // Cursor cursor = new MergeCursor(new Cursor[]{songCursor, songCursorInternal});

            if (cursor != null) {
                int num = 0;
                while (cursor.moveToNext()) {

                    //Dont forget to add option to /1000 for the duration version********
                    if (projection[i].equals(MediaStore.Audio.AudioColumns.DURATION)) {
                        Length = cursor.getInt(cursor.getColumnIndex((projection[i]))) / 1000;
                        AllData.get(i).add("" + Length);
                    } else {
                        AllData.get(i).add(cursor.getString(cursor.getColumnIndex(projection[i])));
                    }
                    num++;
                 //   SecondProgress.setProgress(num/projection.length);
                }
            }
        }

        for (int i = 0; i < projection.length; i++) {
            UniqueDataToStorage.add(new ArrayList<String>());
        }
        //Make the UniqueDataToStorage Arraylist
        //Alright so this part fucking works Now just translate all it's fucking garbage into the App Storage to save as JSON File Nov 10 Morning
        for (int i = 0; i < AllData.get(0).size(); i++) {
            for (int e = 0; e < projection.length; e++) {
                switch (e) {
                    case 1:
                        if (!UniqueDataToStorage.get(e).contains(AllData.get(e).get(i))) {
                            UniqueDataToStorage.get(e).add(AllData.get(e).get(i));
                        } else {
                            //Double check to see if it's a different Album (Looking at you Alone - Single Alan Walker, Marshmello)
                            if (!UniqueDataToStorage.get(5).contains(AllData.get(5).get(i))) {
                                UniqueDataToStorage.get(e).add(AllData.get(e).get(i));
                            }
                        }
                        break;

                    case 5:
                        if (!UniqueDataToStorage.get(e).contains(AllData.get(e).get(i))) {
                            UniqueDataToStorage.get(e).add(AllData.get(e).get(i));
                        } else {
                            //Double check to see if it's a different Album (Looking at you Alone - Single Alan Walker, Marshmello)
                            if (!UniqueDataToStorage.get(5).contains(AllData.get(5).get(i))) {
                                UniqueDataToStorage.get(e).add(AllData.get(e).get(i));
                            }
                        }
                        break;
                    default:
                        if (!UniqueDataToStorage.get(e).contains(AllData.get(e).get(i))) {
                            UniqueDataToStorage.get(e).add(AllData.get(e).get(i));
                        } else {
                            //Double check to see if it's a different Album (Looking at you Alone - Single Alan Walker, Marshmello)
                            if (!UniqueDataToStorage.get(6).contains(AllData.get(6).get(i))) {
                                UniqueDataToStorage.get(e).add(AllData.get(e).get(i));
                            }
                        }
                        break;
                }
            }
        }

        //
        //Load Album Art
        //
        String[] ArtProj = {MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.ARTIST_ID};

        for (int i = 0; i < ArtProj.length; i++) {
            UniqueDataToStorage.add(new ArrayList<String>());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (int i = 0; i < UniqueDataToStorage.get(6).size(); i ++) {
                UniqueDataToStorage.get(7).add(ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, Long.parseLong(UniqueDataToStorage.get(6).get(i))).toString());
            }
        }

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, ArtProj, null, null, sortOrder);
        //Cursor ArtCursorInternal = getContentResolver().query(MediaStore.Audio.Albums.INTERNAL_CONTENT_URI, ArtProj, null, null, sortOrder);
       // Cursor cursor = new MergeCursor(new Cursor[]{ArtCursor, ArtCursorInternal});
        if (cursor != null) {
            int num = 0;
            while (cursor.moveToNext()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                } else {
                    UniqueDataToStorage.get(7).add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
                }
                UniqueDataToStorage.get(8).add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
                UniqueDataToStorage.get(9).add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST_ID)));
                num++;
                //SecondProgress.setProgress(num/ArtProj.length);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AllData.add(new ArrayList<String>());
            for (int i = 0; i < AllData.get(0).size(); i++) {
                AllData.get(7).add(ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, Long.parseLong(AllData.get(6).get(i))).toString());
            }
        } else {
            AllData.add(new ArrayList<String>());
            for (int i = 0; i < AllData.get(0).size(); i++) {
                for (int e = 0; e < UniqueDataToStorage.get(6).size(); e++) {
                    if (AllData.get(6).get(i).equals(UniqueDataToStorage.get(6).get(e))) {
                        AllData.get(7).add(UniqueDataToStorage.get(7).get(e));
                    }
                }
            }
        }

        //Alone - Single by Alan Walker and Marshmello repeating 4 times find out why Nov 9 or Nov 10

        //Adds the Album Image Array

        //I guess we don't need this part, it's too hard to figure out, keep in code though since you may figure it out one day
        //Convert everything to Ints I guess?
        //for (int i = 0; i < AllData.size(); i++) {
        //    for (int e = 0; e < AllData.get(i).size(); e++) {
        //        for (int g = 0; g < UniqueDataToStorage.get(i).size(); g++) {
        //            if (AllData.get(i).get(e).equals(UniqueDataToStorage.get(i).get(g))) {
        //                AllData.get(i).set(e, "" + g);
        //            }
        //        }
        //    }
        //}
    }

    public void masterSongDataFile(JSONObject Json) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");

        String ID;
        int UniRank;
        String MusicType;
        ArrayList<String> MusicTags = new ArrayList<>();                                                                //100 tags should be enough Right?
        int UniTimePlay;
        String LastTimePlay = "";

        JSONArray SongNum = new JSONArray();
        JSONArray AlbumNum = new JSONArray();
        JSONArray ArtistNum = new JSONArray();
        JSONArray ObjectSize = new JSONArray();
        JSONArray SortMethod = new JSONArray();
        JSONArray DisplayMethod = new JSONArray();
        JSONArray RankingMethod = new JSONArray();

        SongNum.put(AllData.get(0).size());
        ArtistNum.put(UniqueDataToStorage.get(1).size());
        AlbumNum.put(UniqueDataToStorage.get(6).size());
        ObjectSize.put("Small");
        SortMethod.put("Alphabetical");
        DisplayMethod.put("Song");
        RankingMethod.put("Method1");

        JSONArray Tags = new JSONArray();
        JSONArray Mood = new JSONArray();
        Tags.put("Default");
        Tags.put("Music");
        Tags.put("Hello");
        Mood.put("Default");
        Mood.put("Music");
        Mood.put("Hello");

        //contains all the data for each song, other than playlist ranking and times played, includes last 100? 1000? timers of time listened to song and how many skips forward or backwards in seconds
        //id is the name of the Object, name, artist, imagepath, universal ranking, musictype, mood, songlength, universal times played,
        //id as name, length of time listened to song (in seconds)
        //id as name, skip type, time at start of skip JSON Array may be needed
        try {
            Json.put("SortMethod", SortMethod);           //Add more info later like view Type (Dark or light mode ect)
            Json.put("TotalSongNum", SongNum);
            Json.put("UniversalRankingMethod", RankingMethod);        //Maybe change later, meth1 = 1-10, meth2 = 1-100, meth3 = 1-1000?
            Json.put("TotalArtistNum", ArtistNum);
            Json.put("TotalAlbumNum", AlbumNum);
            Json.put("AllTags", Tags);
            Json.put("AllMoods", Mood);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < AllData.get(0).size(); i++) {          //generates songs and their ID's
            Date currentTime = Calendar.getInstance().getTime();
            JSONArray ConstantData = new JSONArray();             //In this loop so that it resets all data every time
            ID = "SID" + i;  //stands for Song ID

            UniRank = 0;
            MusicType = "Default";
            if (!MusicTags.contains("Default")) {                    //So it doesn't put x amounts of them by accident
                MusicTags.add("Default");
            }

            UniTimePlay = 0;
            LastTimePlay = format.format(currentTime);

            //ConstantData.put(ID); //Not this, make the Id as the name and the rest of the info into JSON Array
            ConstantData.put(AllData.get(0).get(i)); //Song Title
            ConstantData.put(AllData.get(1).get(i)); //Song Artist
            ConstantData.put(AllData.get(4).get(i)); //Song Album
            ConstantData.put(AllData.get(3).get(i)); //Song Path
            ConstantData.put(AllData.get(7).get(i)); //Song AlbumArt
            ConstantData.put(UniRank);
            ConstantData.put(MusicType);
            ConstantData.put(MusicTags);
            ConstantData.put(Integer.parseInt(AllData.get(2).get(i)));               //In seconds  Duration
            ConstantData.put(UniTimePlay);     //Number of times played (Universal)
            ConstantData.put(0);               //Total Time played (Universal)
            ConstantData.put(LastTimePlay);
            ConstantData.put(AllData.get(6).get(i)); //AlbumID
            ConstantData.put(AllData.get(5).get(i)); //ArtistID
            ConstantData.put("None");                //Skin active
               // ConstantData.put(null); //Background Colour

            //
            //ConstantData.put(0);                     //Fav Start
            //ConstantData.put(Integer.parseInt(AllData.get(2).get(i)));     //Fav End
            ConstantData.put(ID);                    //File Song ID

            TryCatch(Json, ID, ConstantData);

          //  SecondProgress.setProgress((i/AllData.get(0).size())/3);
        }

        for (int i = 0; i < AllData.get(0).size(); i++) {           //generates memory of how long you listen to the songs
            Date currentTime = Calendar.getInstance().getTime();
            JSONArray songLengthListened = new JSONArray();
            long tempLong = 0;
            ID = "LLSID" + i;                //Listen Length Song ID
            ArrayList<Long> lengthListened = new ArrayList();
            LastTimePlay = format.format(currentTime);
            lengthListened.add(Long.parseLong(LastTimePlay));
            for (int e = 0; e < 5; e++) {
                lengthListened.add(tempLong);
            }
            songLengthListened.put(lengthListened); //All empty for now other than date

            TryCatch(Json, ID, songLengthListened);
        }

        String[] SecondAddOns = new String[6];
        SecondAddOns[0] = "@-15s";
        SecondAddOns[1] = "@-10s";
        SecondAddOns[2] = "@-5s";
        SecondAddOns[3] = "@5s";
        SecondAddOns[4] = "@10s";
        SecondAddOns[5] = "@15s";
        for (int i = 0; i < AllData.get(0).size(); i++) {          //generates memory of when you skip/rewind during a song for which song

            for (int g = 0; g < SecondAddOns.length; g++) {
                Date currentTime = Calendar.getInstance().getTime();
                JSONArray Skips = new JSONArray(); //make empty for now but later in the program will be called up to add the skip and times. make string. Store all the seconds as Int's instead of strings
                ID = "SSID" + i + SecondAddOns[g];  //Stands for Skip Song ID
                long tempLong = 0;
                ArrayList<Long> SkipAdd = new ArrayList();
                LastTimePlay = format.format(currentTime);
                SkipAdd.add(Long.parseLong(LastTimePlay));
                for (int e = 0; e < 5; e++) {
                    SkipAdd.add(tempLong);
                }
                //1 = -15s, 2 = -10s, 3 = -5s, 4 = 5s, 5 = 10s, 6 = 15s
                Skips.put(SkipAdd);

                TryCatch(Json, ID, Skips);
            }
        }

        //
        //
        //   Set Artist Ranks and all songs that they Own aswell as their total play time in seconds
        //
        //

        //Determines Songs it owns
        ArrayList<ArrayList<Integer>> OwnSongArt = new ArrayList<>();
        ArrayList<Integer> ArtistLength = new ArrayList<>();
        for (int i = 0; i < UniqueDataToStorage.get(1).size(); i++) {
            ArtistLength.add(0);
            OwnSongArt.add(new ArrayList<Integer>());
            for (int e = 0; e < AllData.get(1).size(); e++) {
                //Maybe switch to Contain since there are double artists
                if (AllData.get(1).get(e).contains(UniqueDataToStorage.get(1).get(i))) {
                    OwnSongArt.get(i).add(1);
                    ArtistLength.set(i, ArtistLength.get(i) + Integer.parseInt(AllData.get(2).get(e)));
                } else {
                    OwnSongArt.get(i).add(0);
                }
            }
        }

        for (int i = 0; i < UniqueDataToStorage.get(1).size(); i++) {                                       //Saves all the info
            JSONArray ArtistStuff = new JSONArray();
            ArrayList<String> SongsOwned = new ArrayList<>();
            ID = "AID" + i;         //Stands for Artist ID
            ArtistStuff.put(UniqueDataToStorage.get(1).get(i));
            ArtistStuff.put(null);       //Artist Image
            ArtistStuff.put(0);          //Artist Universal Rank
            ArtistStuff.put("Default");  //Artist Universal Moods
            ArtistStuff.put("Default");  //Artist Universal Tags
            ArtistStuff.put(ArtistLength.get(i));  //Artist Length of all songs Seconds
            ArtistStuff.put(0);          //Universal Times listened
            ArtistStuff.put(0);          //Universal Total Time Played
            ArtistStuff.put(LastTimePlay);          //Last time Played                   Last time any of their songs have been played
            ArtistStuff.put(UniqueDataToStorage.get(5).get(i)); //ArtistID
            ArtistStuff.put("None");
            ArtistStuff.put(ID);                     //File Artist ID
            for (int e = 0; e < AllData.get(1).size(); e++) {
                if (OwnSongArt.get(i).get(e).equals(1)) {
                    SongsOwned.add("SID" + e);
                }
            }
            ArtistStuff.put(SongsOwned);
            try {
                Json.put(ID, ArtistStuff);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //SecondProgress.setProgress(1/3+(i/UniqueDataToStorage.get(1).size())/3);
        }

        //Checks for songs it owns and determines that albums length
        ArrayList<ArrayList<Integer>> OwnSongAlb = new ArrayList<>();
        ArrayList<Integer> AlbumLength = new ArrayList<>();
        for (int i = 0; i < UniqueDataToStorage.get(4).size(); i++) {
            AlbumLength.add(0);
            OwnSongAlb.add(new ArrayList<Integer>());
            for (int e = 0; e < AllData.get(4).size(); e++) {
                //Maybe switch to Contain since there are double artists
                if (AllData.get(4).get(e).equals(UniqueDataToStorage.get(4).get(i))) {
                    for (int g = 0; g < UniqueDataToStorage.get(6).size(); g++) {
                        //Double checks if it's the right Album
                        if (AllData.get(6).get(e).equals(UniqueDataToStorage.get(6).get(g))) {
                            OwnSongAlb.get(i).add(1);
                            AlbumLength.set(i, AlbumLength.get(i) + Integer.parseInt(AllData.get(2).get(e)));
                        }
                    }
                } else {
                    OwnSongAlb.get(i).add(0);
                }
            }
        }


        //Checks for Artists it Owns
        ArrayList<ArrayList<Integer>> OwnArtistAlb = new ArrayList<>();
        for (int i = 0; i < UniqueDataToStorage.get(4).size(); i++) {
            OwnArtistAlb.add(new ArrayList<Integer>());
            for (int e = 0; e < AllData.get(4).size(); e++) {
                if (AllData.get(4).get(e).equals(UniqueDataToStorage.get(4).get(i))) {
                    for (int g = 0; g < UniqueDataToStorage.get(6).size(); g++) {
                        if (AllData.get(6).get(e).equals(UniqueDataToStorage.get(6).get(g))) {
                            OwnArtistAlb.get(i).add(1);
                        } else {
                            OwnArtistAlb.get(i).add(0);
                        }
                    }
                } else {
                    OwnArtistAlb.get(i).add(0);
                }
            }
        }

        for (int i = 0; i < UniqueDataToStorage.get(4).size(); i++) {                                       //Saves all the info
            JSONArray AlbumStuff = new JSONArray();
            ID = "AlID" + i;         //Stands for Album ID
            ArrayList<String> SongsOwned = new ArrayList<>();
            AlbumStuff.put(UniqueDataToStorage.get(4).get(i));          //Album Name
            AlbumStuff.put(UniqueDataToStorage.get(8).get(i));     //Album Artists
            AlbumStuff.put(UniqueDataToStorage.get(7).get(i));     //Album Art
            AlbumStuff.put(0);                    //Album Universal Rank
            AlbumStuff.put("Default");            //Album Universal Mood            This is an array like Songs Owned         or will be
            AlbumStuff.put("Default");            //Album Universal Tags            This is an array like Songs Owned
            AlbumStuff.put(AlbumLength.get(i));          //Album Length of all Songs (seconds)
            AlbumStuff.put(0);                    //Universal Times listened
            AlbumStuff.put(0);                    //Universal Total Time Played
            AlbumStuff.put(LastTimePlay);                    //Last Time played                   Last time any song in the album has been played
            AlbumStuff.put(UniqueDataToStorage.get(6).get(i)); //AlbumID
            AlbumStuff.put(UniqueDataToStorage.get(9).get(i)); //Artist Album ID
            AlbumStuff.put("None");
            AlbumStuff.put(ID);                          //File Album ID

            for (int e = 0; e < AllData.get(4).size(); e++) {
                if (OwnSongAlb.get(i).get(e).equals(1)) {
                    SongsOwned.add("SID" + e);
                }
            }
            AlbumStuff.put(SongsOwned);
            try {
                Json.put(ID, AlbumStuff);
            } catch (JSONException e) {
                e.printStackTrace();
            }
           // SecondProgress.setProgress(2/3+(i/UniqueDataToStorage.get(4).size())/3);
        }
    }

    public void masterAlbumDataFile(JSONObject Json) {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");

        String ID;
        String LastTimePlay = "";

        JSONArray AlbumNum = new JSONArray();
        //Number of Album's
        AlbumNum.put(UniqueDataToStorage.get(4).size());

        //Save to JSON
        try {
            Json.put("TotalAlbumNum", AlbumNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Checks for songs it owns and determines that albums length
        ArrayList<ArrayList<Integer>> OwnSongAlb = new ArrayList<>();
        ArrayList<Integer> AlbumLength = new ArrayList<>();
        for (int i = 0; i < UniqueDataToStorage.get(4).size(); i++) {
            AlbumLength.add(0);
            OwnSongAlb.add(new ArrayList<Integer>());
            for (int e = 0; e < AllData.get(4).size(); e++) {
                //Maybe switch to Contain since there are double artists
                if (AllData.get(4).get(e).equals(UniqueDataToStorage.get(4).get(i))) {
                    for (int g = 0; g < UniqueDataToStorage.get(6).size(); g++) {
                        //Double checks if it's the right Album
                        if (AllData.get(6).get(e).equals(UniqueDataToStorage.get(6).get(g))) {
                            OwnSongAlb.get(i).add(1);
                            AlbumLength.set(i, AlbumLength.get(i) + Integer.parseInt(AllData.get(2).get(e)));
                        }
                    }
                } else {
                    OwnSongAlb.get(i).add(0);
                }
            }
        }

        //Checks for Artists it Owns
        ArrayList<ArrayList<Integer>> OwnArtistAlb = new ArrayList<>();
        for (int i = 0; i < UniqueDataToStorage.get(4).size(); i++) {
            OwnArtistAlb.add(new ArrayList<Integer>());
            for (int e = 0; e < AllData.get(4).size(); e++) {
                if (AllData.get(4).get(e).equals(UniqueDataToStorage.get(4).get(i))) {
                    for (int g = 0; g < UniqueDataToStorage.get(6).size(); g++) {
                        if (AllData.get(6).get(e).equals(UniqueDataToStorage.get(6).get(g))) {
                            OwnArtistAlb.get(i).add(1);
                        } else {
                            OwnArtistAlb.get(i).add(0);
                        }
                    }
                } else {
                    OwnArtistAlb.get(i).add(0);
                }
            }
        }

        for (int i = 0; i < UniqueDataToStorage.get(4).size(); i++) {                                       //Saves all the info
            JSONArray AlbumStuff = new JSONArray();
            ID = "AlID" + i;         //Stands for Album ID
            ArrayList<String> SongsOwned = new ArrayList<>();
            AlbumStuff.put(UniqueDataToStorage.get(4).get(i));          //Album Name
            AlbumStuff.put(UniqueDataToStorage.get(8).get(i));     //Album Artists
            AlbumStuff.put(UniqueDataToStorage.get(7).get(i));     //Album Art
            AlbumStuff.put(0);                    //Album Universal Rank
            AlbumStuff.put("Default");            //Album Universal Mood            This is an array like Songs Owned         or will be
            AlbumStuff.put("Default");            //Album Universal Tags            This is an array like Songs Owned
            AlbumStuff.put(AlbumLength.get(i));          //Album Length of all Songs (seconds)
            AlbumStuff.put(0);                    //Universal Times listened
            AlbumStuff.put(0);                    //Universal Total Time Played
            AlbumStuff.put(LastTimePlay);                    //Last Time played                   Last time any song in the album has been played
            AlbumStuff.put(UniqueDataToStorage.get(6).get(i)); //AlbumID
            AlbumStuff.put(UniqueDataToStorage.get(9).get(i)); //Artist Album ID
            AlbumStuff.put("None");
            AlbumStuff.put(ID);                          //File Album ID

            for (int e = 0; e < AllData.get(4).size(); e++) {
                if (OwnSongAlb.get(i).get(e).equals(1)) {
                    SongsOwned.add("SID" + e);
                }
            }
            AlbumStuff.put(SongsOwned);
            try {
                Json.put(ID, AlbumStuff);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void masterArtistDataFile(JSONObject Json) {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");

        String ID;
        String LastTimePlay = "";

        JSONArray ArtistNum = new JSONArray();

        ArtistNum.put(UniqueDataToStorage.get(1).size());

        try {
            Json.put("TotalArtistNum", ArtistNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Determines Songs it owns
        ArrayList<ArrayList<Integer>> OwnSongArt = new ArrayList<>();
        ArrayList<Integer> ArtistLength = new ArrayList<>();
        for (int i = 0; i < UniqueDataToStorage.get(1).size(); i++) {
            ArtistLength.add(0);
            OwnSongArt.add(new ArrayList<Integer>());
            for (int e = 0; e < AllData.get(1).size(); e++) {
                //Maybe switch to Contain since there are double artists
                if (AllData.get(1).get(e).contains(UniqueDataToStorage.get(1).get(i))) {
                    OwnSongArt.get(i).add(1);
                    ArtistLength.set(i, ArtistLength.get(i) + Integer.parseInt(AllData.get(2).get(e)));
                } else {
                    OwnSongArt.get(i).add(0);
                }
            }
        }

        for (int i = 0; i < UniqueDataToStorage.get(1).size(); i++) {                                       //Saves all the info
            JSONArray ArtistStuff = new JSONArray();
            ArrayList<String> SongsOwned = new ArrayList<>();
            ID = "AID" + i;         //Stands for Artist ID
            ArtistStuff.put(UniqueDataToStorage.get(1).get(i));
            ArtistStuff.put(null);       //Artist Image
            ArtistStuff.put(0);          //Artist Universal Rank
            ArtistStuff.put("Default");  //Artist Universal Moods
            ArtistStuff.put("Default");  //Artist Universal Tags
            ArtistStuff.put(ArtistLength.get(i));  //Artist Length of all songs Seconds
            ArtistStuff.put(0);          //Universal Times listened
            ArtistStuff.put(0);          //Universal Total Time Played
            ArtistStuff.put(LastTimePlay);          //Last time Played                   Last time any of their songs have been played
            ArtistStuff.put(UniqueDataToStorage.get(5).get(i)); //ArtistID
            ArtistStuff.put("None");
            ArtistStuff.put(ID);                     //File Artist ID
            for (int e = 0; e < AllData.get(1).size(); e++) {
                if (OwnSongArt.get(i).get(e).equals(1)) {
                    SongsOwned.add("SID" + e);
                }
            }
            ArtistStuff.put(SongsOwned);
            try {
                Json.put(ID, ArtistStuff);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void genEverythingPlaylist(JSONObject Json) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");

        String ID;
        String Name = "";
        String Artist = "";
        String Album = "";
        String MusicType = "";
        String SongLength = "";        //fix to int if duration isnt what we want for the song length or do the Parse.toint or whatever bullshit
        String LastTimePlayed = "";
        int UniRank = 0;
        int PlayRank = 0;
        int PlayTimesPlay = 0;
        int UniTimesPlay = 0;
        ArrayList<String> MusicTags = new ArrayList<>();


        JSONArray SongNum = new JSONArray();
        JSONArray AlbumNum = new JSONArray();
        JSONArray ArtistNum = new JSONArray();
        JSONArray ObjectSize = new JSONArray();
        JSONArray SortMethod = new JSONArray();
        JSONArray DisplayMethod = new JSONArray();
        JSONArray RankingMethod = new JSONArray();
        JSONArray PlaylistName = new JSONArray();

        String[] DefaultPlaylistNames = new String[2];
        DefaultPlaylistNames[0] = "Everything";
        DefaultPlaylistNames[1] = "UniRankZero";

        SongNum.put(AllData.get(0).size());
        ArtistNum.put(UniqueDataToStorage.get(1).size());
        AlbumNum.put(UniqueDataToStorage.get(6).size());
        ObjectSize.put("Small");
        SortMethod.put("Alphabetical");
        DisplayMethod.put("Song");
        RankingMethod.put("Method1");
        PlaylistName.put("Everything");

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
            Json.put("TotalSongNum", SongNum);
            Json.put("TotalArtistNum", ArtistNum);
            Json.put("TotalAlbumNum", AlbumNum);
            Json.put("ObjectDisplaySize", ObjectSize);
            Json.put("SortMethod", SortMethod);
            Json.put("ObjectDisplayMethod", DisplayMethod);                //Maybe change name
            Json.put("UniversalRankingMethod", RankingMethod);        //Maybe change later, meth1 = 1-10, meth2 = 1-100, meth3 = 1-1000?
            Json.put("AllTags", Tags);
            Json.put("AllMoods", Mood);
            Json.put("PlaylistName", PlaylistName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < AllData.get(0).size(); i++) {
            Date currentTime = Calendar.getInstance().getTime();
            JSONArray ConstantData = new JSONArray();             //In this loop so that it resets all data every time
            ID = "SID" + i;  //stands for Playlist Song ID
            UniRank = 0;
            MusicType = "Default";
            if (!MusicTags.contains("Default")) {                    //So it doesn't put x amounts of them by accident
                MusicTags.add("Default");
            }

            PlayRank = 0;
            PlayTimesPlay = 0;
            UniTimesPlay = 0;
            LastTimePlayed = format.format(currentTime);

            //Not this, make the Id as the name and the rest of the info into JSON Array
            ConstantData.put(AllData.get(0).get(i)); //Song Name
            ConstantData.put(AllData.get(1).get(i)); //Song Artist(s)
            ConstantData.put(AllData.get(4).get(i)); //Song Album
            ConstantData.put(AllData.get(3).get(i)); //Song path
            ConstantData.put(AllData.get(7).get(i)); //Song Album Art
            ConstantData.put(PlayRank);
            ConstantData.put(UniRank);
            ConstantData.put(MusicType); //Playlist            //Delete the playlist ones
            ConstantData.put(MusicType); //Uni
            ConstantData.put(MusicTags); //Playlist
            ConstantData.put(MusicTags); //Uni
            ConstantData.put(AllData.get(2).get(i)); //Song Duration
            ConstantData.put(PlayTimesPlay);          //Playlist Num of Times played
            ConstantData.put(0);                      //Playlist Total Time played
            ConstantData.put(LastTimePlayed);
            ConstantData.put(AllData.get(6).get(i)); //Song AlbumID
            ConstantData.put(AllData.get(5).get(i)); //ArtistID
            ConstantData.put("None");             //Active Skin ID
               // ConstantData.put(null); //Background Colour

            // ConstantData.put(0);                  //Fav start
            //ConstantData.put(AllData.get(2).get(i));  //Fav End
            ConstantData.put(ID);

            TryCatch(Json, ID, ConstantData);
        }

        for (int i = 0; i < AllData.get(0).size(); i++) {           //generates memory of how long you listen to the songs
            Date currentTime = Calendar.getInstance().getTime();
            JSONArray songLengthListened = new JSONArray();
            long tempLong = 0;
            ID = "PLLSID" + i;                //Listen Length Song ID
            ArrayList<Long> lengthListened = new ArrayList();
            LastTimePlayed = format.format(currentTime);
            lengthListened.add(Long.parseLong(LastTimePlayed));
            for (int e = 0; e < 5; e++) {
                lengthListened.add(tempLong);
            }
            songLengthListened.put(lengthListened); //All empty for now other than date

            TryCatch(Json, ID, songLengthListened);
        }

        String[] SecondAddOns = new String[6];
        SecondAddOns[0] = "@-15s";
        SecondAddOns[1] = "@-10s";
        SecondAddOns[2] = "@-5s";
        SecondAddOns[3] = "@5s";
        SecondAddOns[4] = "@10s";
        SecondAddOns[5] = "@15s";
        for (int i = 0; i < AllData.get(0).size(); i++) {          //generates memory of when you skip/rewind during a song for which song

            for (int g = 0; g < SecondAddOns.length; g++) {
                Date currentTime = Calendar.getInstance().getTime();
                JSONArray Skips = new JSONArray(); //make empty for now but later in the program will be called up to add the skip and times. make string. Store all the seconds as Int's instead of strings
                ID = "PSSID" + i + SecondAddOns[g];  //Stands for Skip Song ID
                long tempLong = 0;
                ArrayList<Long> SkipAdd = new ArrayList();
                LastTimePlayed = format.format(currentTime);
                SkipAdd.add(Long.parseLong(LastTimePlayed));
                for (int e = 0; e < 5; e++) {
                    SkipAdd.add(tempLong);
                }
                //1 = -15s, 2 = -10s, 3 = -5s, 4 = 5s, 5 = 10s, 6 = 15s
                Skips.put(SkipAdd);

                TryCatch(Json, ID, Skips);
            }
        }
        //
        //
        //   Set Artist Ranks and all songs that they Own aswell as their total play time in seconds
        //
        //

        ArrayList<ArrayList<Integer>> OwnSongArt = new ArrayList<>();
        ArrayList<Integer> ArtistLength = new ArrayList<>();
        for (int i = 0; i < UniqueDataToStorage.get(1).size(); i++) {
            ArtistLength.add(0);
            OwnSongArt.add(new ArrayList<Integer>());
            for (int e = 0; e < AllData.get(1).size(); e++) {
                //Maybe switch to Contain since there can be multiple artists in a song
                if (AllData.get(1).get(e).contains(UniqueDataToStorage.get(1).get(i))) {
                    OwnSongArt.get(i).add(1);
                    ArtistLength.set(i, ArtistLength.get(i) + Integer.parseInt(AllData.get(2).get(e)));
                } else {
                    OwnSongArt.get(i).add(0);
                }
            }
        }

        for (int i = 0; i < UniqueDataToStorage.get(1).size(); i++) {                                       //Saves all the info
            JSONArray ArtistStuff = new JSONArray();
            ArrayList<String> SongsOwned = new ArrayList<>();
            ID = "AID" + i;         //Stands for Artist ID
            ArtistStuff.put(UniqueDataToStorage.get(1).get(i));    //Artist Name
            ArtistStuff.put(null);       //Artist Image
            ArtistStuff.put(0);          //Artist Playlist Rank
            ArtistStuff.put(0);          //Artist Universal Rank
            ArtistStuff.put("Default");  //Artist Playlist Mood                This and next 3 will be arrays
            ArtistStuff.put("Default");  //Artist Universal Mood
            ArtistStuff.put("Default");  //Artist Playlist Tags
            ArtistStuff.put("Default");  //Artist Universal Tags
            ArtistStuff.put(ArtistLength.get(i));  //Artist all songs Length Secs
            ArtistStuff.put(0);          //Playlist Times listened
            ArtistStuff.put(0);          //Playlist Total Time Played   (sec)
            ArtistStuff.put(LastTimePlayed);          //Last Time Played                  Last Time Artist was played in playlist
            ArtistStuff.put(UniqueDataToStorage.get(5).get(i)); //ArtistID
            ArtistStuff.put("None");         //Active Skin ID
            ArtistStuff.put(ID);

            for (int e = 0; e < AllData.get(1).size(); e++) {
                if (OwnSongArt.get(i).get(e).equals(1)) {
                    SongsOwned.add("SID" + e);
                }
            }
            ArtistStuff.put(SongsOwned);
            try {
                Json.put(ID, ArtistStuff);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        //
        //
        //Set Album Ranks and songs and Artists they own as well as total play time in seconds
        //
        //

        //Checks for songs it owns and determines that albums length
        ArrayList<ArrayList<Integer>> OwnSongAlb = new ArrayList<>();
        ArrayList<Integer> AlbumLength = new ArrayList<>();
        for (int i = 0; i < UniqueDataToStorage.get(4).size(); i++) {
            AlbumLength.add(0);
            OwnSongAlb.add(new ArrayList<Integer>());
            for (int e = 0; e < AllData.get(4).size(); e++) {
                //Maybe switch to Contain since there are double artists
                if (AllData.get(4).get(e).equals(UniqueDataToStorage.get(4).get(i))) {
                    for (int g = 0; g < UniqueDataToStorage.get(6).size(); g++) {
                        //Double checks if it's the right Album
                        if (AllData.get(6).get(e).equals(UniqueDataToStorage.get(6).get(g))) {
                            OwnSongAlb.get(i).add(1);
                            AlbumLength.set(i, AlbumLength.get(i) + Integer.parseInt(AllData.get(2).get(e)));
                        }
                    }
                } else {
                    OwnSongAlb.get(i).add(0);
                }
            }
        }

        //Checks for Artists it Owns
        ArrayList<ArrayList<Integer>> OwnArtistAlb = new ArrayList<>();
        for (int i = 0; i < UniqueDataToStorage.get(4).size(); i++) {
            OwnArtistAlb.add(new ArrayList<Integer>());
            for (int e = 0; e < AllData.get(4).size(); e++) {
                if (AllData.get(4).get(e).equals(UniqueDataToStorage.get(4).get(i))) {
                    for (int g = 0; g < UniqueDataToStorage.get(6).size(); g++) {
                        if (AllData.get(6).get(e).equals(UniqueDataToStorage.get(6).get(g))) {
                            OwnArtistAlb.get(i).add(1);
                        } else {
                            OwnArtistAlb.get(i).add(0);
                        }
                    }
                } else {
                    OwnArtistAlb.get(i).add(0);
                }
            }
        }

        //Gets All the Unique Artists it owns
        ArrayList<ArrayList<String>> Artists = new ArrayList<>();
        for (int i = 0; i < UniqueDataToStorage.get(4).size(); i++) {
            Artists.add(new ArrayList<String>());
            for (int e = 0; e < AllData.get(0).size(); e++) {
                if (OwnArtistAlb.get(i).get(e).equals(1)) {
                    if (!Artists.get(i).contains(AllData.get(1).get(e))) {
                        Artists.get(i).add(AllData.get(1).get(e));
                    }
                }
            }
        }

        //Adds Various Artists if there are more than 1 in it
        for (int i = 0; i < Artists.size(); i++) {
            if (Artists.size() >= 2) {
                Artists.get(i).add("Various Artists");
            }
        }

        for (int i = 0; i < UniqueDataToStorage.get(4).size(); i++) {                                       //Saves all the info
            JSONArray AlbumStuff = new JSONArray();
            ID = "AlID" + i;         //Stands for Album ID
            ArrayList<String> SongsOwned = new ArrayList<>();
            AlbumStuff.put(UniqueDataToStorage.get(4).get(i));          //Album Name
            AlbumStuff.put(UniqueDataToStorage.get(8).get(i));     //Album Artist
            AlbumStuff.put(UniqueDataToStorage.get(7).get(i));     //Album Art
            AlbumStuff.put(0);                    //Album Playlist Rank
            AlbumStuff.put(0);                    //Album Universal Rank
            AlbumStuff.put("Default");            //Album Playlist Mood          This and next 3 are going to be arrays
            AlbumStuff.put("Default");            //Album Universal Mood
            AlbumStuff.put("Default");            //Album Playlist Tags
            AlbumStuff.put("Default");            //Album Universal Tags
            AlbumStuff.put(AlbumLength.get(i));          //Album Length Secs
            AlbumStuff.put(0);                    //Playlist Times Played
            AlbumStuff.put(0);                    //Playlist Total Time Played     (sec)
            AlbumStuff.put(LastTimePlayed);                    //Last Time Played               Last time song from Album was played in playlist
            AlbumStuff.put(UniqueDataToStorage.get(6).get(i)); //AlbumID
            AlbumStuff.put(UniqueDataToStorage.get(9).get(i)); //Album Artist ID
            AlbumStuff.put("None");
            AlbumStuff.put(ID);

            for (int e = 0; e < AllData.get(0).size(); e++) {
                if (OwnSongAlb.get(i).get(e).equals(1)) {
                    SongsOwned.add("SID" + e);
                }
            }
            AlbumStuff.put(SongsOwned);

            try {
                Json.put(ID, AlbumStuff);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public int SetBackgroundColor(String ImagePath) {

        int Pos = 0;
        if (ImagePath != null) {
            int[] GreyCol = {53, 102, 153, 204};

            ArrayList<Integer> Red = new ArrayList<>();
            ArrayList<Integer> Blue = new ArrayList<>();
            ArrayList<Integer> Green = new ArrayList<>();

            Glide.with(this).load(ImagePath).placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(Temp);

            //if (Temp.get)

            Bitmap myBitmap = BitmapFactory.decodeFile(ImagePath);
            int hi = 0;

            if (myBitmap != null) {
                int width = myBitmap.getWidth();
                int height = myBitmap.getHeight();

                boolean Grey = true;

                for (int i = 0; i < width / 2; i++) {
                    for (int e = 0; e < height / 2; e++) {

                        int pixel = myBitmap.getPixel(i * 2, e * 2);

                        if (Color.red(pixel) - 50 <= Color.green(pixel) && Color.green(pixel) <= Color.red(pixel) + 50 && Color.red(pixel) - 50 <= Color.blue(pixel) && Color.blue(pixel) <= Color.red(pixel) + 50) {

                        } else {
                            Red.add(Color.red(pixel));
                            Green.add(Color.green(pixel));
                            Blue.add(Color.blue(pixel));
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
                            Red.add(Color.red(pixel));
                            Green.add(Color.green(pixel));
                            Blue.add(Color.blue(pixel));
                        }
                    }
                }

                for (int i = 0; i < Red.size(); i++) {
                    RedAv = RedAv + Red.get(i);
                }

                for (int i = 0; i < Green.size(); i++) {
                    GreenAv = GreenAv + Green.get(i);
                }

                for (int i = 0; i < Blue.size(); i++) {
                    BlueAv = BlueAv + Blue.get(i);
                }

                RedAv = RedAv / Red.size();
                GreenAv = GreenAv / Green.size();
                BlueAv = BlueAv / Blue.size();

                double Dif = 1000; //Make a high difference


                if (Grey) {

                    for (int i = 0; i < GreyCol.length; i++) {

                        double CurDif = 0;

                        double RedMean = (RedAv + GreyCol[i]) / 2;

                        // double RedDif = (RedAv - CLBRed[i]) *0.30;
                        //double GreenDif = (GreenAv - CLBGreen[i]) *0.59;
                        //double BlueDif = (BlueAv - CLBBlue[i]) *0.11;

                        double RedDif = Math.pow(((RedAv - GreyCol[i])), 2);
                        double GreenDif = Math.pow(((GreenAv - GreyCol[i])), 2);
                        double BlueDif = Math.pow(((BlueAv - GreyCol[i])), 2);

                        CurDif = Math.sqrt(((2 + (RedMean / 256)) * RedDif) + (4 * GreenDif) + (2 + ((255 - RedMean) / 256)) * BlueDif);

                        if (Dif > CurDif) {
                            Dif = CurDif;
                            Pos = i;

                        }
                    }

                } else {

                    for (int i = 0; i < CLBRed.length; i++) {

                        double CurDif = 0;

                        double RedMean = (RedAv + CLBRed[i]) / 2;

                        // double RedDif = (RedAv - CLBRed[i]) *0.30;
                        //double GreenDif = (GreenAv - CLBGreen[i]) *0.59;
                        //double BlueDif = (BlueAv - CLBBlue[i]) *0.11;

                        double RedDif = Math.pow(((RedAv - CLBRed[i])), 2);
                        double GreenDif = Math.pow(((GreenAv - CLBGreen[i])), 2);
                        double BlueDif = Math.pow(((BlueAv - CLBBlue[i])), 2);

                        CurDif = Math.sqrt(((2 + (RedMean / 256)) * RedDif) + (4 * GreenDif) + (2 + ((255 - RedMean) / 256)) * BlueDif);

                        if (Dif > CurDif) {
                            Dif = CurDif;
                            Pos = i;

                        }
                    }

                }
            }

        }



        /*
        PlayInfo = findViewById(R.id.PlayInfo);
        if (AppAppearance.equals("Dark")) {
            MusicPlayerBackground.setBackgroundColor(Color.rgb(CLBRed[Pos], CLBGreen[Pos], CLBBlue[Pos]));
        }

         */

        return Pos;
    }

    public void saveColours () {

        JSONObject Json = new JSONObject();

        JSONArray RedCol = new JSONArray();
        JSONArray GreenCol = new JSONArray();
        JSONArray BlueCol = new JSONArray();

        for (int i = 0; i < CLBRed.length; i ++) {
            RedCol.put(CLBRed[i]);
            GreenCol.put(CLBGreen[i]);
            BlueCol.put(CLBBlue[i]);
        }

        try {
            Json.put("Red", RedCol);
            Json.put("Green", GreenCol);
            Json.put("Blue", BlueCol);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveToStorage("Colours", Json);



    }

    public void saveToStorage(String fileName, JSONObject jsonObj) {
        try {

            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput(fileName + ".json", Context.MODE_PRIVATE));
            writer.write(jsonObj.toString());
            writer.close();

            Message.setText("Saved A File Succesfully");
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

    private class AntennaMessage extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String Action = intent.getAction();

            if (Action.equals("DisplayMessage")) {
                DisplayMes();
            }
        }
    }


}
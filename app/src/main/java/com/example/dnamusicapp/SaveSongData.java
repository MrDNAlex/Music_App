package com.example.dnamusicapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SaveSongData extends Service {

    boolean Playlist;

    ArrayList<ArrayList<String>> PlaylistSongID = new ArrayList<>();
    ArrayList<ArrayList<String>> PlaylistArtistID = new ArrayList<>();
    ArrayList<ArrayList<String>> PlaylistAlbumID = new ArrayList<>();

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

    String FilePath;

    public AntennaSaveFiles AntennaReceiver = new AntennaSaveFiles();

    public SaveSongData() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();

        GetInfoStart();

        ReadPlaylistInfoFile();

        AntennaSetup();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onDestroy() {
        SaveInfo();
    }

    public void GetInfoStart() {
        try {
            JSONObject SaveInfo = new JSONObject(loadJsonFile("SaveSong.json"));
            JSONArray PlaylistBool = SaveInfo.getJSONArray("Playlist");
            JSONArray FilePathString = SaveInfo.getJSONArray("FilePath");

            Playlist = (boolean) PlaylistBool.get(0);
            FilePath = FilePathString.get(0).toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void AntennaSetup() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SaveFile");
        intentFilter.addAction("UpdateFileTime");
        intentFilter.addAction("UpdateFilePlays");
        intentFilter.addAction("SaveColours");
        registerReceiver(AntennaReceiver, intentFilter);
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
                // JSONArray AllTagsArray = InfoFile.getJSONArray("AllTags");
                //JSONArray AllMoodsArray = InfoFile.getJSONArray("AllMoods");

                SongNum = (int) TotalSongNum.get(0);
                ArtistNum = (int) TotalArtistNum.get(0);
                AlbumNum = (int) TotalAlbumNum.get(0);

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

                int hi = 0;

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
                //JSONArray AllTagsArray = MasterFile.getJSONArray("AllTags");
                //JSONArray AllMoodsArray = MasterFile.getJSONArray("AllMoods");

                SongNum = (int) TotalSongNum.get(0);
                ArtistNum = (int) TotalArtistNum.get(0);
                AlbumNum = (int) TotalAlbumNum.get(0);


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

    public void UpdateFileTime(Intent intent) {
        String ID = intent.getStringExtra("SongID");
        long Time = intent.getLongExtra("Time", 0);
        long ActualTime;

        //Time = Time /100;
        //Hours
        ActualTime = (Time / 10000) * 3600;
        Time = Time % 10000;
        ActualTime = ActualTime + ((Time / 100) * 60);
        Time = Time % 100;
        ActualTime = ActualTime + Time;

        for (int i = 0; i < PlaylistSongID.get(0).size(); i++) {
            if (Playlist) {
                if (PlaylistSongID.get(18).get(i).equals(ID)) {
                    ActualTime = Integer.parseInt(PlaylistSongID.get(13).get(i)) + ActualTime;
                    PlaylistSongID.get(13).set(i, "" + ActualTime);
                }
            } else {
                if (PlaylistSongID.get(15).get(i).equals(ID)) {
                    ActualTime = Integer.parseInt(PlaylistSongID.get(10).get(i)) + ActualTime;
                    PlaylistSongID.get(10).set(i, "" + ActualTime);
                }
            }
        }
    }

    public void UpdateFilePlays(Intent intent) {
        String ID = intent.getStringExtra("SongID");


        for (int i = 0; i < PlaylistSongID.get(0).size(); i++) {
            if (Playlist) {
                if (PlaylistSongID.get(18).get(i).equals(ID)) {
                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");
                    String Time = format.format(currentTime);
                    int TimesPlayed = Integer.parseInt(PlaylistSongID.get(12).get(i)) + 1;
                    PlaylistSongID.get(12).set(i, "" + TimesPlayed);
                    PlaylistSongID.get(14).set(i, Time);
                }
            } else {
                if (PlaylistSongID.get(15).get(i).equals(ID)) {
                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");
                    String Time = format.format(currentTime);
                    int TimesPlayed = Integer.parseInt(PlaylistSongID.get(9).get(i)) + 1;
                    PlaylistSongID.get(9).set(i, "" + TimesPlayed);
                    PlaylistSongID.get(11).set(i, Time);
                }
            }
        }
    }

    public void SaveInfo() {

        try {
            JSONObject SavePlaylist = new JSONObject(loadJsonFile(FilePath + ".json"));

            if (Playlist) {
                //
                //Playlist
                //

                //Reset All values of Artist
                for (int i = 0; i < PlaylistArtistID.get(0).size(); i++) {
                    PlaylistArtistID.get(9).set(i, "0");
                    PlaylistArtistID.get(10).set(i, "0");
                }

                //Calculate and set Artists values
                for (int i = 0; i < PlaylistArtistID.get(0).size(); i++) {
                    for (int e = 0; e < PlaylistSongID.get(0).size(); e++) {
                        if (PlaylistArtistID.get(12).get(i).equals(PlaylistSongID.get(16).get(e))) {
                            PlaylistArtistID.get(9).set(i, String.valueOf(Integer.parseInt(PlaylistArtistID.get(9).get(i)) + Integer.parseInt(PlaylistSongID.get(12).get(e))));
                            PlaylistArtistID.get(10).set(i, String.valueOf(Integer.parseInt(PlaylistArtistID.get(10).get(i)) + Integer.parseInt(PlaylistSongID.get(13).get(e))));
                            if (Long.parseLong(PlaylistArtistID.get(11).get(i)) < Long.parseLong(PlaylistSongID.get(14).get(e))) {
                                PlaylistArtistID.get(11).set(i, PlaylistSongID.get(14).get(e));
                            }
                        }
                    }
                }

                //Reset All values of Album
                for (int i = 0; i < PlaylistAlbumID.get(0).size(); i++) {
                    PlaylistAlbumID.get(10).set(i, "0");
                    PlaylistAlbumID.get(11).set(i, "0");
                }

                //Calculate and set Album values
                for (int i = 0; i < PlaylistAlbumID.get(0).size(); i++) {
                    for (int e = 0; e < PlaylistSongID.get(0).size(); e++) {
                        if (PlaylistAlbumID.get(13).get(i).equals(PlaylistSongID.get(15).get(e))) {
                            PlaylistAlbumID.get(10).set(i, String.valueOf(Integer.parseInt(PlaylistAlbumID.get(10).get(i)) + Integer.parseInt(PlaylistSongID.get(12).get(e))));
                            PlaylistAlbumID.get(11).set(i, String.valueOf(Integer.parseInt(PlaylistAlbumID.get(11).get(i)) + Integer.parseInt(PlaylistSongID.get(13).get(e))));
                            if (Long.parseLong(PlaylistAlbumID.get(12).get(i)) < Long.parseLong(PlaylistSongID.get(14).get(e))) {
                                PlaylistAlbumID.get(12).set(i, PlaylistSongID.get(14).get(e));
                            }
                        }
                    }
                }
            } else {
                //
                //Master File
                //

                //Reset All values of Artist
                for (int i = 0; i < PlaylistArtistID.get(0).size(); i++) {
                    PlaylistArtistID.get(6).set(i, "0");
                    PlaylistArtistID.get(7).set(i, "0");
                }

                //Calculate and set Artists values
                for (int i = 0; i < PlaylistArtistID.get(0).size(); i++) {
                    for (int e = 0; e < PlaylistSongID.get(0).size(); e++) {
                        if (PlaylistArtistID.get(9).get(i).equals(PlaylistSongID.get(13).get(e))) {
                            PlaylistArtistID.get(6).set(i, String.valueOf(Integer.parseInt(PlaylistArtistID.get(6).get(i)) + Integer.parseInt(PlaylistSongID.get(9).get(e))));
                            PlaylistArtistID.get(7).set(i, String.valueOf(Integer.parseInt(PlaylistArtistID.get(7).get(i)) + Integer.parseInt(PlaylistSongID.get(10).get(e))));
                            if (Long.parseLong(PlaylistArtistID.get(8).get(i)) < Long.parseLong(PlaylistSongID.get(11).get(e))) {
                                PlaylistArtistID.get(8).set(i, PlaylistSongID.get(11).get(e));
                            }
                        }
                    }
                }

                //Reset All values of Album
                for (int i = 0; i < PlaylistAlbumID.get(0).size(); i++) {
                    PlaylistAlbumID.get(7).set(i, "0");
                    PlaylistAlbumID.get(8).set(i, "0");
                }

                //Calculate and set Album values
                for (int i = 0; i < PlaylistAlbumID.get(0).size(); i++) {
                    for (int e = 0; e < PlaylistSongID.get(0).size(); e++) {
                        if (PlaylistAlbumID.get(10).get(i).equals(PlaylistSongID.get(12).get(e))) {
                            PlaylistAlbumID.get(7).set(i, String.valueOf(Integer.parseInt(PlaylistAlbumID.get(7).get(i)) + Integer.parseInt(PlaylistSongID.get(9).get(e))));
                            PlaylistAlbumID.get(8).set(i, String.valueOf(Integer.parseInt(PlaylistAlbumID.get(8).get(i)) + Integer.parseInt(PlaylistSongID.get(10).get(e))));
                            if (Long.parseLong(PlaylistAlbumID.get(9).get(i)) < Long.parseLong(PlaylistSongID.get(11).get(e))) {
                                PlaylistAlbumID.get(9).set(i, PlaylistSongID.get(11).get(e));
                            }
                        }
                    }
                }
            }


            //Save Songs
            for (int i = 0; i < PlaylistSongID.get(0).size(); i++) {
                JSONArray SID = new JSONArray();
                for (int e = 0; e < PlaylistSongID.size(); e++) {
                    SID.put(PlaylistSongID.get(e).get(i));
                }
                SavePlaylist.put("SID" + i, SID);
            }

            //Save Artist
            for (int i = 0; i < PlaylistArtistID.get(0).size(); i++) {
                JSONArray AID = new JSONArray();
                for (int e = 0; e < PlaylistArtistID.size(); e++) {
                    AID.put(PlaylistArtistID.get(e).get(i));
                }
                SavePlaylist.put("AID" + i, AID);
            }

            //Save Songs
            for (int i = 0; i < PlaylistAlbumID.get(0).size(); i++) {
                JSONArray AlID = new JSONArray();
                for (int e = 0; e < PlaylistAlbumID.size(); e++) {
                    AlID.put(PlaylistAlbumID.get(e).get(i));
                }
                SavePlaylist.put("AlID" + i, AlID);
            }

            //
            //Complete and Save
            //
            saveToStorage(FilePath, SavePlaylist);

            UpdateMasterFile();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void UpdateMasterFile() {

        try {
            JSONObject MasterFile = new JSONObject(loadJsonFile("MasterSongDataFile.json"));

            //Add extra info to array
            int NumOfPlaylist;
            int NumArtist;
            int NumAlbum;
            int NumSong;

            JSONObject PlaylistInfo = new JSONObject(loadJsonFile("viewPlaylistInfo.json"));
            JSONArray PlaylistNum = PlaylistInfo.getJSONArray("NumOfPlaylist");

            ArrayList<ArrayList<String>> MasterSongIDs = new ArrayList<>();
            ArrayList<ArrayList<String>> MasterArtistIDs = new ArrayList<>();
            ArrayList<ArrayList<String>> MasterAlbumIDs = new ArrayList<>();

            //Times Played, total time, date, ID
            ArrayList<ArrayList<String>> CombSong = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                CombSong.add(new ArrayList<String>());
            }

            JSONArray TotalSongNum = MasterFile.getJSONArray("TotalSongNum");
            JSONArray TotalArtistNum = MasterFile.getJSONArray("TotalArtistNum");
            JSONArray TotalAlbumNum = MasterFile.getJSONArray("TotalAlbumNum");

            NumSong = (int) TotalSongNum.get(0);
            NumArtist = (int) TotalArtistNum.get(0);
            NumAlbum = (int) TotalAlbumNum.get(0);

            //Initialize Array PlaylistSongID
            for (int i = 0; i < 16; i++) {
                MasterSongIDs.add(new ArrayList<String>());
            }

            //Initialize Array PlaylistArtistID
            for (int i = 0; i < 13; i++) {
                MasterArtistIDs.add(new ArrayList<String>());
            }

            //Initialize Array PlaylistAlbumID
            for (int i = 0; i < 15; i++) {
                MasterAlbumIDs.add(new ArrayList<String>());
            }

            //
            //Load Arrays
            //
            //Load All Song Info into 2D Array
            for (int g = 0; g < NumSong; g++) {
                JSONArray SID = MasterFile.getJSONArray("SID" + g);
                for (int e = 0; e < MasterVariableNumSong; e++) {
                    MasterSongIDs.get(e).add(SID.get(e).toString());
                }
            }

            //Load All Artist Info into 2D Array
            for (int g = 0; g < NumArtist; g++) {
                JSONArray AID = MasterFile.getJSONArray("AID" + g);
                for (int e = 0; e < MasterVariableNumArtist; e++) {
                    MasterArtistIDs.get(e).add(AID.get(e).toString());
                }
            }

            //Load All Album Info into 2D Array
            for (int g = 0; g < NumAlbum; g++) {
                JSONArray AlID = MasterFile.getJSONArray("AlID" + g);
                for (int e = 0; e < MasterVariableNumAlbum; e++) {
                    MasterAlbumIDs.get(e).add(AlID.get(e).toString());
                }
            }

            //Add everything to the combination Arrays
            for (int i = 0; i < NumSong; i++) {
                CombSong.get(0).add("0");
                CombSong.get(1).add("0");
                CombSong.get(2).add("0");
                CombSong.get(3).add(MasterSongIDs.get(15).get(i));
            }

            //Save to every other file
            NumOfPlaylist = (int) PlaylistNum.get(0);

            for (int i = 0; i < NumOfPlaylist; i++) {
                JSONArray PlaylistArray = PlaylistInfo.getJSONArray("PID" + i);

                JSONObject CurrentPlaylist = new JSONObject(loadJsonFile(PlaylistArray.get(1).toString() + ".json"));

                JSONArray CurrentTotalSongNum = CurrentPlaylist.getJSONArray("TotalSongNum");
                JSONArray CurrentTotalArtistNum = CurrentPlaylist.getJSONArray("TotalArtistNum");
                JSONArray CurrentTotalAlbumNum = CurrentPlaylist.getJSONArray("TotalAlbumNum");


                NumSong = (int) CurrentTotalSongNum.get(0);
                NumArtist = (int) CurrentTotalArtistNum.get(0);
                NumAlbum = (int) CurrentTotalAlbumNum.get(0);

                ArrayList<ArrayList<String>> CurrentSongIDs = new ArrayList<>();
                ArrayList<ArrayList<String>> CurrentArtistIDs = new ArrayList<>();
                ArrayList<ArrayList<String>> CurrentAlbumIDs = new ArrayList<>();

                //Initialize Array SongID
                for (int e = 0; e < 19; e++) {
                    CurrentSongIDs.add(new ArrayList<String>());
                }

                //Initialize Array AlbumID
                for (int e = 0; e < 18; e++) {
                    CurrentAlbumIDs.add(new ArrayList<String>());
                }

                //Initialize Array ArtistID
                for (int e = 0; e < 16; e++) {
                    CurrentArtistIDs.add(new ArrayList<String>());
                }

                //
                //Load Arrays
                //
                //Load All Song Info into 2D Array
                for (int g = 0; g < NumSong; g++) {
                    JSONArray SID = CurrentPlaylist.getJSONArray("SID" + g);
                    for (int e = 0; e < VariableNumSong; e++) {
                        CurrentSongIDs.get(e).add(SID.get(e).toString());
                    }
                }

                //Load All Artist Info into 2D Array
                for (int g = 0; g < NumArtist; g++) {
                    JSONArray AID = CurrentPlaylist.getJSONArray("AID" + g);
                    for (int e = 0; e < VariableNumArtist; e++) {
                        CurrentArtistIDs.get(e).add(AID.get(e).toString());
                    }
                }

                //Load All Album Info into 2D Array
                for (int g = 0; g < NumAlbum; g++) {
                    JSONArray AlID = CurrentPlaylist.getJSONArray("AlID" + g);
                    for (int e = 0; e < VariableNumAlbum; e++) {
                        CurrentAlbumIDs.get(e).add(AlID.get(e).toString());
                    }
                }

                //Update all Song infos

                //Times played = 9
                //Total time = 10

                //Times played = 12
                //Total time = 13

                //Update every song ID and it's info
                for (int e = 0; e < CombSong.get(0).size(); e++) {
                    for (int g = 0; g < CurrentSongIDs.get(0).size(); g++) {
                        if (CombSong.get(3).get(e).equals(CurrentSongIDs.get(18).get(g))) {
                            CombSong.get(0).set(e, String.valueOf(Integer.parseInt(CombSong.get(0).get(e)) + Integer.parseInt(CurrentSongIDs.get(12).get(g))));
                            CombSong.get(1).set(e, String.valueOf(Integer.parseInt(CombSong.get(1).get(e)) + Integer.parseInt(CurrentSongIDs.get(13).get(g))));
                            if (Long.parseLong(CombSong.get(2).get(e)) < Long.parseLong(CurrentSongIDs.get(14).get(g))) {
                                CombSong.get(2).set(e, CurrentSongIDs.get(14).get(g));
                            }
                        }
                    }
                }

                //Tomorrow make a system that goes through every file and creates an Array for Song ID, Album ID and Artist ID and contains their added value of total times played and Total time

            }

            //Transfer info to master
            for (int i = 0; i < MasterSongIDs.get(0).size(); i++) {
                for (int e = 0; e < CombSong.get(0).size(); e++) {
                    if (MasterSongIDs.get(15).get(i).equals(CombSong.get(3).get(e))) {
                        MasterSongIDs.get(9).set(i, CombSong.get(0).get(e));
                        MasterSongIDs.get(10).set(i, CombSong.get(1).get(e));
                        MasterSongIDs.get(11).set(i, CombSong.get(2).get(e));
                    }
                }
            }

            //Reset All values of Artist
            for (int i = 0; i < MasterArtistIDs.get(0).size(); i++) {
                MasterArtistIDs.get(6).set(i, "0");
                MasterArtistIDs.get(7).set(i, "0");
            }

            //Calculate and set Artists values
            for (int i = 0; i < MasterArtistIDs.get(0).size(); i++) {
                for (int e = 0; e < MasterSongIDs.get(0).size(); e++) {
                    if (MasterArtistIDs.get(9).get(i).equals(MasterSongIDs.get(13).get(e))) {
                        MasterArtistIDs.get(6).set(i, String.valueOf(Integer.parseInt(MasterArtistIDs.get(6).get(i)) + Integer.parseInt(MasterSongIDs.get(9).get(e))));
                        MasterArtistIDs.get(7).set(i, String.valueOf(Integer.parseInt(MasterArtistIDs.get(7).get(i)) + Integer.parseInt(MasterSongIDs.get(10).get(e))));
                        if (Long.parseLong(MasterArtistIDs.get(8).get(i)) < Long.parseLong(MasterSongIDs.get(11).get(e))) {
                            MasterArtistIDs.get(8).set(i, MasterSongIDs.get(11).get(e));
                        }
                    }
                }
            }

            //Reset All values of Album
            for (int i = 0; i < MasterAlbumIDs.get(0).size(); i++) {
                MasterAlbumIDs.get(7).set(i, "0");
                MasterAlbumIDs.get(8).set(i, "0");
            }

            //Calculate and set Album values
            for (int i = 0; i < MasterAlbumIDs.get(0).size(); i++) {
                for (int e = 0; e < MasterSongIDs.get(0).size(); e++) {
                    if (MasterAlbumIDs.get(10).get(i).equals(MasterSongIDs.get(12).get(e))) {
                        MasterAlbumIDs.get(7).set(i, String.valueOf(Integer.parseInt(MasterAlbumIDs.get(7).get(i)) + Integer.parseInt(MasterSongIDs.get(9).get(e))));
                        MasterAlbumIDs.get(8).set(i, String.valueOf(Integer.parseInt(MasterAlbumIDs.get(8).get(i)) + Integer.parseInt(MasterSongIDs.get(10).get(e))));
                        if (Long.parseLong(MasterAlbumIDs.get(9).get(i)) < Long.parseLong(MasterSongIDs.get(11).get(e))) {
                            MasterAlbumIDs.get(9).set(i, MasterSongIDs.get(11).get(e));
                        }
                    }
                }
            }

            //Save everything to the JSON

            //Save Songs
            for (int e = 0; e < MasterSongIDs.get(0).size(); e++) {
                JSONArray SID = new JSONArray();
                for (int g = 0; g < MasterSongIDs.size(); g++) {
                    SID.put(MasterSongIDs.get(g).get(e));
                }
                MasterFile.put("SID" + e, SID);
            }

            //Save Artist
            for (int e = 0; e < MasterArtistIDs.get(0).size(); e++) {
                JSONArray AID = new JSONArray();
                for (int g = 0; g < MasterArtistIDs.size(); g++) {
                    AID.put(MasterArtistIDs.get(g).get(e));
                }
                MasterFile.put("AID" + e, AID);
            }

            //Save Songs
            for (int e = 0; e < MasterAlbumIDs.get(0).size(); e++) {
                JSONArray AlID = new JSONArray();
                for (int g = 0; g < MasterAlbumIDs.size(); g++) {
                    AlID.put(MasterAlbumIDs.get(g).get(e));
                }
                MasterFile.put("AlID" + e, AlID);
            }

            //
            //Complete and Save
            //
            saveToStorage("MasterSongDataFile", MasterFile);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveToStorage(String fileName, JSONObject jsonObj) {
        try {
            //Slam dunk into storage
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput(fileName + ".json", Context.MODE_PRIVATE));
            writer.write(jsonObj.toString());
            writer.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private class AntennaSaveFiles extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String Action = intent.getAction();

            if (Action.equals("SaveFile")) {
                SaveInfo();
            } else if (Action.equals("UpdateFileTime")) {
                UpdateFileTime(intent);
            } else if (Action.equals("UpdateFilePlays")) {
                UpdateFilePlays(intent);
            }
        }
    }
}
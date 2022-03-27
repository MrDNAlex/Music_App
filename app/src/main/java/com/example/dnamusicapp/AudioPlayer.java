package com.example.dnamusicapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AudioPlayer extends Service implements MediaPlayer.OnCompletionListener {

    public MediaPlayer MusicAudioPlayer;
    public ArrayList<Integer> SongQueue = new ArrayList<>();
    public ArrayList<String> SongQueuePath = new ArrayList<>();
    public ArrayList<String> SongIDs = new ArrayList<>();


    public int Pos;

    public long MediaTimer; //Stored in milliseconds

    public boolean Paused;

    //public  SimpleDateFormat format = new SimpleDateFormat("ddHHmmssSS");
    public SimpleDateFormat Hour = new SimpleDateFormat("HH");
    public SimpleDateFormat Min = new SimpleDateFormat("mm");
    public SimpleDateFormat Sec = new SimpleDateFormat("ss");

    public Date currentTime = Calendar.getInstance().getTime();

    public ArrayList<String> TimeStamp = new ArrayList<>();


    String Hour1;
    String Min1;
    String Sec1;
    String Hour2;
    String Min2;
    String Sec2;

    String FilePath;

    public String Time1;
    public String Time2;
    public long CurrentTotalTime;


    public Thread UpdateThread;

    //Broadcast signals
    public static final String RESUME_SONG = "Resume";
    public static final String SEEK_BAR_CHANGE = "SeekBarChange";
    public static final String PAUSE_SONG = "Pause";
    public static final String NEXT_SONG = "NextSong";
    public static final String LAST_SONG = "LastSong";
    public static final String FORWARD = "Forward";
    public static final String REWIND = "Rewind";
    //public static final String PLAY_NEW_SONG = "ChangeSong";
    public static final String ADD_QUEUE = "AddQueue";     //Future
    public static final String REARRANGE_QUEUE = "RearrangeQueue";      //Future

    public AntennaAudioPlayer AntennaReceiver = new AntennaAudioPlayer();

    public boolean SignalLoop = true;


    public boolean Alive = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //MusicAudioPlayer.release();
        Release();
        UpdateFile(true);
        SaveFile();
        //Release();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();

        //Gotta initialize the MediaPlayer (put it in the Play button?) *
        //Then gotta send this the queue and all the info associated with it *
        //Then gotta setup the play, pause and other buttons
        //Gotta setup the seekbar updater
        //Setup the OnCompleteListener that will tell the PlayInfo screen to change the info to the correct song

        //
        //Gotta setup all the buttons and update commands in this area somehow
        //

        ReadPlaylistInfoFile();
        Play(Pos);
        UpdateFile(false);
        TimeUpdater();
        AntennaSetup();

    }

    public void TimeUpdater() {

        SignalLoop = true;
        UpdateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Make this variable for when it releases
                while (Alive) {
                    while (!Paused) {
                        try {
                            Thread.sleep(100);
                            MediaTimer = MediaTimer + 100;
                            UpdateTime();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        UpdateThread.start();

    }

    public void UpdateTime() {

        if (SignalLoop) {
            if (MusicAudioPlayer.isPlaying()) {
                Intent UpdateTimeSignal = new Intent();
                UpdateTimeSignal.setAction("UpdateTime");
                double CurrentTime = MusicAudioPlayer.getCurrentPosition() / 1000;
                double Total = MusicAudioPlayer.getDuration() / 1000;
                double Percent = (CurrentTime / Total) * 100;
                UpdateTimeSignal.putExtra("CurrentTime", MusicAudioPlayer.getCurrentPosition() / 1000); //Convert to seconds
                UpdateTimeSignal.putExtra("Percentage", Percent);
                sendBroadcast(UpdateTimeSignal);
                //
                //Dec 29 Fix the percentage things and have the Seekbar connect, As well as get the SongInfo updater made and connected
                //
            }
        }
    }

    public void UpdateDisplayInfo() {
        SignalLoop = false;
        Intent UpdateDisplayInfoSignal = new Intent();
        UpdateDisplayInfoSignal.setAction("UpdateDisplayInfo");
        UpdateDisplayInfoSignal.putExtra("Position", Pos);
        sendBroadcast(UpdateDisplayInfoSignal);
        SignalLoop = true;
    }

    public void AntennaSetup() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PAUSE_SONG);
        intentFilter.addAction(RESUME_SONG);
        intentFilter.addAction(NEXT_SONG);
        intentFilter.addAction(LAST_SONG);
        intentFilter.addAction(FORWARD);
        intentFilter.addAction(REWIND);
        intentFilter.addAction(ADD_QUEUE);
        intentFilter.addAction(REARRANGE_QUEUE);
        intentFilter.addAction(SEEK_BAR_CHANGE);
        intentFilter.addAction("CheckExist");
        intentFilter.addAction("PauseMP");
        intentFilter.addAction("PlayMP");
        registerReceiver(AntennaReceiver, intentFilter);
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

        try {
            JSONObject InfoFile = new JSONObject(loadJsonFile("QueueList.json"));

            JSONArray Queue = InfoFile.getJSONArray("Queue");
            JSONArray QueuePath = InfoFile.getJSONArray("QueuePath");
            JSONArray ArrayLength = InfoFile.getJSONArray("ArrayLength");
            JSONArray QueueID = InfoFile.getJSONArray("QueueID");
            JSONArray Position = InfoFile.getJSONArray("Position");
            JSONArray FPath = InfoFile.getJSONArray("FilePath");



            int Loop;
            // (int) ArrayLength.get(0)

            Loop = QueuePath.length();

            Pos = (int) Position.get(0);

            FilePath = (String)FPath.get(0);

            for (int i = 0; i < Loop; i++) {
                SongQueue.add(Integer.parseInt(Queue.get(i).toString()));
                SongQueuePath.add(QueuePath.get(i).toString());
                SongIDs.add(QueueID.get(i).toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Play(int pos) {

        if (MusicAudioPlayer != null) {
            MusicAudioPlayer.release();
        }
        try {
            MusicAudioPlayer = new MediaPlayer();
            MusicAudioPlayer.setDataSource(SongQueuePath.get(pos));
            MusicAudioPlayer.prepare();
            MusicAudioPlayer.start();
            MusicAudioPlayer.setOnCompletionListener(this);
            Alive = true;
            UpdateDisplayInfo();
            currentTime = Calendar.getInstance().getTime();
            Hour1 = Hour.format(currentTime);
            Min1 = Min.format(currentTime);
            Sec1 = Sec.format(currentTime);
UpdateMiniPlayer();
        } catch (IOException e) {
            // Toast.makeText(getApplicationContext(), "Something went wrong" + idk + "Hi = " + hi + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void SeekBarChange(double Percent) {
        boolean WasPaused = Paused;

        if (WasPaused) {
            Resume();
        }
        double DecPercent = Percent / 100;
        double Total = MusicAudioPlayer.getDuration();
        double MilliSec = (double) DecPercent * Total;
        MusicAudioPlayer.seekTo((int) MilliSec);
        if (WasPaused) {
            Pause();
        }
    }

    public void Resume() {

        if (MusicAudioPlayer != null && Paused) {
            MusicAudioPlayer.start();
            Paused = false;
            currentTime = Calendar.getInstance().getTime();
            Hour1 = Hour.format(currentTime);
            Min1 = Min.format(currentTime);
            Sec1 = Sec.format(currentTime);
            UpdateMiniPlayer();
        }
    }


    public void Pause() {

        if (MusicAudioPlayer != null) {
            /*
            MusicAudioPlayer.pause();
            Paused = true;
            currentTime = Calendar.getInstance().getTime();
            Hour2 = Hour.format(currentTime);
            Min2 = Min.format(currentTime);
            Sec2 = Sec.format(currentTime);
            CurrentTotalTime = CurrentTotalTime + ((Integer.parseInt(Hour2) - Integer.parseInt(Hour1) * 3600) + ((Integer.parseInt(Min2) - Integer.parseInt(Min1) * 60)) + ((Integer.parseInt(Sec2) - Integer.parseInt(Sec1))));
            //CurrentTotalTime = CurrentTotalTime + (Long.parseLong(Time2) - Long.parseLong(Time1));
            UpdateMiniPlayer();

             */
            if (Paused == false) {
                MusicAudioPlayer.pause();
                Paused = true;
                currentTime = Calendar.getInstance().getTime();
                Hour2 = Hour.format(currentTime);
                Min2 = Min.format(currentTime);
                Sec2 = Sec.format(currentTime);
                CurrentTotalTime = CurrentTotalTime + ((Integer.parseInt(Hour2) - Integer.parseInt(Hour1) * 3600) + ((Integer.parseInt(Min2) - Integer.parseInt(Min1) * 60)) + ((Integer.parseInt(Sec2) - Integer.parseInt(Sec1))));
                //CurrentTotalTime = CurrentTotalTime + (Long.parseLong(Time2) - Long.parseLong(Time1));
                UpdateMiniPlayer();
            }
        }
    }

    public void Release() {
        SignalLoop = false;
        if (MusicAudioPlayer == null) {

        } else {
            if (MusicAudioPlayer.isPlaying()) {
                MusicAudioPlayer.stop();
            }
            MusicAudioPlayer.release();
            MusicAudioPlayer = null;
            Alive = false;
        }

    }

    public void Forward(int Time) {

        Time = Time * 1000; //Convert from second to millisecond

        int CurrentTime = MusicAudioPlayer.getCurrentPosition();

        if ((CurrentTime + Time) <= MusicAudioPlayer.getDuration()) {
            MusicAudioPlayer.seekTo(CurrentTime + Time);
        } else {
            MusicAudioPlayer.seekTo(MusicAudioPlayer.getDuration() - 1);
        }

    }

    public void NextSong() {
        UpdateFile(true);

        if ((Pos + 1) <= SongQueue.size() - 1) {
            Release();
            Pos++;
            Play(Pos);
        } else {
            //Send Signal that it's the end of queue/request for new queue/check rules to see if it repeats ect
            Release();
            Pos = 0;
            Play(Pos);
        }
        UpdateFile(false);
        SaveFile();
    }

    public void LastSong() {
        UpdateFile(true);
        UpdateFile(false);
        if ((Pos - 1) >= 0) {
            Release();
            Pos--;
            Play(Pos);

        } else {

            //Send Signal that it's the end of queue/request for new queue/check rules to see if it repeats ect
            Release();
            Pos = SongQueue.size() - 1;
            Play(Pos);

        }
        SaveFile();
    }

    public void Rewind(int Time) {

        Time = Time * 1000; //Convert from second to millisecond

        MediaTimer = MediaTimer - 100;

        int CurrentTime = MusicAudioPlayer.getCurrentPosition();

        if ((CurrentTime - Time) <= 0) {
            MusicAudioPlayer.seekTo(0);
        } else {
            MusicAudioPlayer.seekTo(CurrentTime - Time);
        }

    }

    public void UpdateFile(boolean Time) {

        if (Time) {
            //Update Time
            currentTime = Calendar.getInstance().getTime();

            Hour2 = Hour.format(currentTime);
            Min2 = Min.format(currentTime);
            Sec2 = Sec.format(currentTime);

            CurrentTotalTime = CurrentTotalTime + (((Integer.parseInt(Hour2) - Integer.parseInt(Hour1)) * 3600) + ((Integer.parseInt(Min2) - Integer.parseInt(Min1)) * 60) + ((Integer.parseInt(Sec2) - Integer.parseInt(Sec1))));
            //Time2 = format.format(currentTime);
            // CurrentTotalTime = CurrentTotalTime + (Long.parseLong(Time2) - Long.parseLong(Time1));
            Intent UpdateFileTime = new Intent();
            UpdateFileTime.setAction("UpdateFileTime");
            UpdateFileTime.putExtra("Time", CurrentTotalTime);
            UpdateFileTime.putExtra("SongID", SongIDs.get(Pos));
            CurrentTotalTime = 0;
            sendBroadcast(UpdateFileTime);


        } else {
            //Update Played Times
            Intent UpdateFilePlays = new Intent();
            UpdateFilePlays.setAction("UpdateFilePlays");
            UpdateFilePlays.putExtra("SongID", SongIDs.get(Pos));
            sendBroadcast(UpdateFilePlays);
        }
    }

    public void UpdateMiniPlayer () {
        Intent MiniPlayer = new Intent();
        MiniPlayer.setAction("AudioExist");
        MiniPlayer.putExtra( "SongID",SongIDs.get(Pos));
        MiniPlayer.putExtra("FilePath", FilePath);
        MiniPlayer.putExtra("Paused", Paused);
        MiniPlayer.putExtra("Position", Pos);
        sendBroadcast(MiniPlayer);
    }

    public void LoadMusicPlayer () {


    }

    public void SaveFile() {
        Intent SaveFile = new Intent();
        SaveFile.setAction("SaveFile");
        sendBroadcast(SaveFile);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        NextSong();
    }

    private class AntennaAudioPlayer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String Action = intent.getAction();

            if (RESUME_SONG.equals(Action)) {
                Resume();
            } else if (PAUSE_SONG.equals(Action)) {
                Pause();
            } else if (NEXT_SONG.equals(Action)) {
                NextSong();
            } else if (LAST_SONG.equals(Action)) {
                LastSong();
            } else if (FORWARD.equals(Action)) {
                int Unit = intent.getIntExtra("Unit", 0);
                Forward(Unit);
            } else if (REWIND.equals(Action)) {
                int Unit = intent.getIntExtra("Unit", 0);
                Rewind(Unit);
            } else if (ADD_QUEUE.equals(Action)) {
                //Miscellanious shit
            } else if (REARRANGE_QUEUE.equals(Action)) {
                //Miscellanious shit
            } else if (SEEK_BAR_CHANGE.equals(Action)) {
                double Percent = intent.getDoubleExtra("Percent", 0);
                SeekBarChange(Percent);
            } else if ("CheckExist".equals(Action)) {
                LoadMusicPlayer();
               UpdateMiniPlayer();
            } else if ("PauseMP".equals(Action)) {
                Pause();
            } else if ("PlayMP".equals(Action)) {
                Resume();
            }

        }
    }
}

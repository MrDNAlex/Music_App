package com.example.dnamusicapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.musicapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class SettingsFiles extends AppCompatActivity {



    Button SaveButton;
    LinearLayout ScrollAppearance;
    LinearLayout ScrollPlayVideo;
    LinearLayout SettingsBackground;

    String PlayVideo;
    String CurrentAppearance;
    String AppAppearance;
    String[] AppearanceList = {"Dark", "DNA"};
    String[] PlayVideoList = {"OG", "DNA Music", "None"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SettingsBackground = findViewById(R.id.SettingsBackground);
        AccessSettings();
        ScrollAppearance = findViewById(R.id.ScrollAppearance);
        ScrollPlayVideo = findViewById(R.id.ScrollPlayVideo);

        ReadPlaylistInfoFile();
        UpdateScreen();

        /*
        use intro video





        */


        SaveButton = findViewById(R.id.SaveButton);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveInfo();

                Intent Home = new Intent(getApplicationContext(), PlaylistView.class);
                startActivity(Home);
            }
        });

        if (AppAppearance.equals("Dark")) {
           SettingsBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.background_alex3));
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
            JSONObject SettingsFile = new JSONObject(loadJsonFile("Settings" + ".json"));
            JSONArray VideoPlay = SettingsFile.getJSONArray("PlayVideo");
            JSONArray AppearanceMode = SettingsFile.getJSONArray("AppearanceMode");

            PlayVideo = VideoPlay.get(0).toString();
            CurrentAppearance = AppearanceMode.get(0).toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void SaveInfo() {

        try {
            JSONObject SettingsFile = new JSONObject(loadJsonFile("Settings" + ".json"));
            JSONArray VideoPlay = new JSONArray();
            JSONArray AppearanceMode = new JSONArray();

            VideoPlay.put(PlayVideo);
            AppearanceMode.put(CurrentAppearance);

            SettingsFile.put("PlayVideo", VideoPlay);
            SettingsFile.put("AppearanceMode", AppearanceMode);

            saveToStorage("Settings", SettingsFile);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void UpdateScreen() {


        //Update Appearance List
        for (int i = 0; i < AppearanceList.length; i++) {
            View Button;
            if (AppearanceList[i].equals(CurrentAppearance)) {
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
            TextView AppearanceType = Button.findViewById(R.id.TMAObject);
            if (AppAppearance.equals("Dark")) {
                AppearanceType.setTextColor(Color.rgb(255, 255, 255));
            }
            AppearanceType.setText(AppearanceList[i]);
            final int finalI = i;
            Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CurrentAppearance = AppearanceList[finalI];
                    ScrollAppearance.removeAllViews();
                    ScrollPlayVideo.removeAllViews();
                    UpdateScreen();
                }
            });

            ScrollAppearance.addView(Button);
        }

        //Update PlayVideo List
        for (int i = 0; i < PlayVideoList.length; i++) {
            View Button;
            if (PlayVideoList[i].equals(PlayVideo)) {
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
            TextView AppearanceType = Button.findViewById(R.id.TMAObject);
            if (AppAppearance.equals("Dark")) {
                AppearanceType.setTextColor(Color.rgb(255, 255, 255));
            }
            AppearanceType.setText(PlayVideoList[i]);
            final int finalI = i;
            Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayVideo = PlayVideoList[finalI];
                    ScrollAppearance.removeAllViews();
                    ScrollPlayVideo.removeAllViews();
                    UpdateScreen();
                }
            });

            ScrollPlayVideo.addView(Button);
        }

        if (AppAppearance.equals("Dark")) {
            SettingsBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.background_alex3));
        } else {
            SettingsBackground.setBackground(ContextCompat.getDrawable(this, R.drawable.bckgrnd));
        }

    }

    public void saveToStorage(String fileName, JSONObject jsonObj) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput(fileName + ".json", Context.MODE_PRIVATE));
            writer.write(jsonObj.toString());
            writer.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
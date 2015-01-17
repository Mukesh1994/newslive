package com.codegear.newslive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.codegear.newslive.utils.Const;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class PlayStreamActivity extends Activity {

    private String path;
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoview);


        if (!LibsChecker.checkVitamioLibs(this))
            return;

        Intent i = getIntent();
        String stream = i.getStringExtra("stream");
        String app = i.getStringExtra("app");
        String title = i.getStringExtra("title");

        if (app.equals("VOD")) {
            path = Const.VOD_APP + stream;
        } else if (app.equals("LIVE")) {
            path = Const.LIVE_APP + stream;
        }



        Log.v("Vitamio Path",path);
        mVideoView = (VideoView) findViewById(R.id.surface_view);

        mVideoView.setVideoPath(path);
        mVideoView.setVideoTitle(title);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });


    }
}

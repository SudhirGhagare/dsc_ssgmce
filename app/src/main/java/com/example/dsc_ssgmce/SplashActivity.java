package com.example.dsc_ssgmce;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;


public class SplashActivity extends Activity {

    VideoView videoView;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashfile);
        videoView = (VideoView) findViewById(R.id.videoView);
        String  path = "android.resource://"+getPackageName()+"/"+R.raw.splash_screen;
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,login_screen.class);
                startActivity(intent);
                finish();
            }
        },2000);

    }

}

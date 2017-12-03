package com.example.vidish.musicplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    AudioManager audio;
    MediaPlayer mediaPlayer;
    TextView volume;
    Button up, down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(this, R.raw.bohemian_rhapsody);
        setContentView(R.layout.activity_main);

        final Button play = (Button) findViewById(R.id.play_button);
        final Button reset = (Button) findViewById(R.id.stop_button);

        up = (Button) findViewById(R.id.volume_up);
        down = (Button) findViewById(R.id.volume_down);
        reset.setEnabled(false);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play.getText().equals("Play")) {
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Toast.makeText(MainActivity.this, "Song Finished", Toast.LENGTH_SHORT).show();
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bohemian_rhapsody);
                            play.setEnabled(true);
                            reset.setEnabled(false);
                        }
                    });
                    reset.setEnabled(true);
                    play.setText("Pause");
                }
                else if (play.getText().equals("Pause"))
                {
                    mediaPlayer.pause();
                    reset.setEnabled(true);
                    play.setText("Play");
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
                mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.bohemian_rhapsody);
                play.setEnabled(true);
                play.setText("Play");
                reset.setEnabled(false);
            }
        });
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volume = (TextView) findViewById(R.id.volume);
        volume.setText("" + audio.getStreamVolume(AudioManager.STREAM_MUSIC));
        if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            down.setEnabled(false);
        }
        if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
            up.setEnabled(false);
        }
    }

    public void raise(View view) {
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
        volume.setText("" + audio.getStreamVolume(AudioManager.STREAM_MUSIC));
        down.setEnabled(true);
        if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
            Toast.makeText(getApplicationContext(), "Maximum Volume Reached", Toast.LENGTH_SHORT).show();
            up.setEnabled(false);
        }
    }

    public void lower(View view) {
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
        volume.setText("" + audio.getStreamVolume(AudioManager.STREAM_MUSIC));
        up.setEnabled(true);
        if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            Toast.makeText(getApplicationContext(), "Minimum Volume Reached", Toast.LENGTH_SHORT).show();
            down.setEnabled(false);
        }
    }
}

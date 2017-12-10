package com.example.vidish.musicplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    AudioManager audio;
    MediaPlayer mediaPlayer;
    TextView volume;
    Button up, down;
    int songs[] = {R.raw.bohemian_rhapsody, R.raw.in_the_end};
    int curr = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(this, songs[curr]);
        setContentView(R.layout.activity_main);
        final Button play = (Button) findViewById(R.id.play_button);
        final Button reset = (Button) findViewById(R.id.stop_button);
        final Button next = (Button) findViewById(R.id.next_button);

        reset.setEnabled(false);

        final AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {
                switch (i)
                {
                    case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                        mediaPlayer.setVolume(0.2f,0.2f);
                        break;
                    case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) :
                        mediaPlayer.pause();
                        break;
                    case (AudioManager.AUDIOFOCUS_LOSS) :
                        mediaPlayer.pause();
                        break;
                    case (AudioManager.AUDIOFOCUS_GAIN) :
                        mediaPlayer.setVolume(1f, 1f);
                        mediaPlayer.start();
                        break;
                }
            }
        };

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play.getText().equals("Play")) {
                    int res = audio.requestAudioFocus(focusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
                    if(res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                    {
                        mediaPlayer.start();
                    }
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Toast.makeText(MainActivity.this, "Song Finished", Toast.LENGTH_SHORT).show();
                            setMediaPlayer(curr++%2);
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
                mediaPlayer.seekTo(0);
                mediaPlayer.pause();
                play.setEnabled(true);
                play.setText("Play");
                reset.setEnabled(false);
                audio.abandonAudioFocus(focusChangeListener);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.reset();
                setMediaPlayer((++curr)%2);
                play.setText("Pause");
                mediaPlayer.start();
                reset.setEnabled(true);
            }
        });
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volume = (TextView) findViewById(R.id.volume);
        volume.setText(""+audio.getStreamVolume(AudioManager.STREAM_MUSIC));
        if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            down.setEnabled(false);
        }
        if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
            up.setEnabled(false);
        }

        volume.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float y;
                float y0 = volume.getY();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        y = y0 - motionEvent.getY();
                        Log.v("AAAAAAAAAAAAA", "" + y / 250);
                        if (y < 0)
                            for (int i = 0; i < (int) y * -1 / 250; i++) {
                                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
                                volume.setText("" + audio.getStreamVolume(AudioManager.STREAM_MUSIC));
                            }
                        else if (y > 0)
                            for (int i = 0; i < (int) y / 250; i++) {
                                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
                                volume.setText("" + audio.getStreamVolume(AudioManager.STREAM_MUSIC));
                            }
                        return true;
                    case MotionEvent.ACTION_DOWN:
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void raise(View view) {
        try {
            audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
            volume.setText(""+audio.getStreamVolume(AudioManager.STREAM_MUSIC));
            down.setEnabled(true);
            if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                Toast.makeText(getApplicationContext(), "Maximum Volume Reached", Toast.LENGTH_SHORT).show();
                up.setEnabled(false);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void lower(View view) {
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
        volume.setText(""+audio.getStreamVolume(AudioManager.STREAM_MUSIC));
        up.setEnabled(true);
        if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            Toast.makeText(getApplicationContext(), "Minimum Volume Reached", Toast.LENGTH_SHORT).show();
            down.setEnabled(false);
        }
    }

    public void setMediaPlayer(int id)
    {
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),songs[id]);
        mediaPlayer.start();
    }
}

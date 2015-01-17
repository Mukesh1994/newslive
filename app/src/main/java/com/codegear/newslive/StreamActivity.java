package com.codegear.newslive;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.codegear.newslive.utils.Const;
import com.codegear.newslive.utils.CustomRequest;
import com.codegear.newslive.utils.PreferenceStorage;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;
import net.majorkernelpanic.streaming.video.VideoQuality;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamActivity extends Activity implements
        OnClickListener,
        RtspClient.Callback,
        Session.Callback,
        SurfaceHolder.Callback,
        OnCheckedChangeListener {

    public final static String TAG = "StreamActivity";

    private Button mButtonSave;
    private ImageButton mButtonStart;
    private ImageButton mButtonFlash;
    private ImageButton mButtonSettings;
    private RadioGroup mRadioGroup;
    private FrameLayout mLayoutVideoSettings;
    private FrameLayout mLayoutServerSettings;
    private SurfaceView mSurfaceView;
    private TextView mTextBitrate;
    private ProgressBar mProgressBar;
    private Session mSession;
    private RtspClient mClient;
    private String stream;
    private ImageButton mButtonTitle;
    private EditText mEditText;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stream);
        queue = Volley.newRequestQueue(this);
        mButtonSettings = (ImageButton) findViewById(R.id.videosettings);
        mButtonSave = (Button) findViewById(R.id.save);
        mButtonStart = (ImageButton) findViewById(R.id.start);
        mButtonFlash = (ImageButton) findViewById(R.id.flash);
        mButtonTitle = (ImageButton) findViewById(R.id.setTitle);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mTextBitrate = (TextView) findViewById(R.id.bitrate);
        mLayoutVideoSettings = (FrameLayout) findViewById(R.id.video_layout);
        mLayoutServerSettings = (FrameLayout) findViewById(R.id.server_layout);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEditText = (EditText) findViewById(R.id.uri);

        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioGroup.setOnClickListener(this);

        mButtonStart.setOnClickListener(this);
        mButtonSave.setOnClickListener(this);
        mButtonFlash.setOnClickListener(this);
        mButtonTitle.setOnClickListener(this);
        mButtonSettings.setOnClickListener(this);
        mButtonFlash.setTag("off");

        stream = UUID.randomUUID().toString();
        stream = stream.replaceAll("-", "");


        // Configures the SessionBuilder
        mSession = SessionBuilder.getInstance()
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_AAC)
                .setAudioQuality(new AudioQuality(8000, 16000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setSurfaceView(mSurfaceView)
                .setPreviewOrientation(0)
                .setCallback(this)
                .build();

        // Configures the RTSP client
        mClient = new RtspClient();
        mClient.setSession(mSession);
        mClient.setCallback(this);

        // Use this to force streaming with the MediaRecorder API
        //mSession.getVideoTrack().setStreamingMethod(MediaStream.MODE_MEDIARECORDER_API);

        // Use this to stream over TCP, EXPERIMENTAL!
        //mClient.setTransportMode(RtspClient.TRANSPORT_TCP);

        // Use this if you want the aspect ratio of the surface view to
        // respect the aspect ratio of the camera preview
        mSurfaceView.setAspectRatioMode(SurfaceView.ASPECT_RATIO_PREVIEW);

        mSurfaceView.getHolder().addCallback(this);

        selectQuality();

    }

    @Override
    protected void onPause() {
        super.onPause();
        onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onDestroy();
    }

    @Override
    public boolean onNavigateUp() {
        onDestroy();
        return true;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mLayoutVideoSettings.setVisibility(View.GONE);
        selectQuality();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                mLayoutServerSettings.setVisibility(View.GONE);
                toggleStream();
                break;
            case R.id.flash:
                if (mButtonFlash.getTag().equals("on")) {
                    mButtonFlash.setTag("off");
                    mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
                } else {
                    mButtonFlash.setImageResource(R.drawable.ic_flash_off_holo_light);
                    mButtonFlash.setTag("on");
                }
                mSession.toggleFlash();
                break;
            case R.id.setTitle:
                mLayoutVideoSettings.setVisibility(View.GONE);
                if (mLayoutServerSettings.getVisibility() == View.GONE) {
                    mLayoutServerSettings.setVisibility(View.VISIBLE);
                } else {
                    mLayoutServerSettings.setVisibility(View.GONE);
                }
                break;
            case R.id.videosettings:
                mRadioGroup.clearCheck();
                mLayoutServerSettings.setVisibility(View.GONE);
                if (mLayoutVideoSettings.getVisibility() == View.GONE) {
                    mLayoutVideoSettings.setVisibility(View.VISIBLE);
                } else {
                    mLayoutVideoSettings.setVisibility(View.GONE);
                }

                break;
            case R.id.save:
                mLayoutServerSettings.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mClient.isStreaming()) {
            mClient.stopStream();
        }

        String temp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("stream",null);
        if(temp!=null){
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            edit.remove("stream");
            Date d1 = new Date( );
            SimpleDateFormat ft =
                    new SimpleDateFormat ("yyyy-MM-dd  hh:mm:ss");
            Date d2 = null;
            try {
                d2 = ft.parse(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("started_at",null));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            edit.remove("started_at");
            edit.apply();

            long duration  = d1.getTime() - d2.getTime();

            long diffInSeconds = Math.abs(TimeUnit.MILLISECONDS.toSeconds(duration));
            long diffInMinutes = Math.abs(TimeUnit.MILLISECONDS.toMinutes(duration));
            long diffInHours = Math.abs(TimeUnit.MILLISECONDS.toHours(duration));
            Map<String, String> params = new HashMap<String, String>();
            params.put("action","stop");
            params.put("stream",stream);
            params.put("length",diffInHours+":"+diffInMinutes+":"+diffInSeconds);

            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.LIVE_URL, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Response: ", response.toString());
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError response) {
                    Log.d("Response: ", response.toString());
                }
            });

            queue.add(jsObjRequest);

            mClient.release();
            mSession.release();
            mSurfaceView.getHolder().removeCallback(this);

        }

    }

    private void selectQuality() {
        int id = mRadioGroup.getCheckedRadioButtonId();
        RadioButton button = (RadioButton) findViewById(id);
        if (button == null) return;

        String text = button.getText().toString();
        Pattern pattern = Pattern.compile("(\\d+)x(\\d+)\\D+(\\d+)\\D+(\\d+)");
        Matcher matcher = pattern.matcher(text);

        matcher.find();
        int width = Integer.parseInt(matcher.group(1));
        int height = Integer.parseInt(matcher.group(2));
        int framerate = Integer.parseInt(matcher.group(3));
        int bitrate = Integer.parseInt(matcher.group(4)) * 1000;

        mSession.setVideoQuality(new VideoQuality(width, height, framerate, bitrate));
        Toast.makeText(this, ((RadioButton) findViewById(id)).getText(), Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Selected resolution: " + width + "x" + height);
    }

    private void enableUI() {
        mButtonStart.setEnabled(true);
    }

    // Connects/disconnects to the RTSP server and starts/stops the stream
    public void toggleStream() {
        mProgressBar.setVisibility(View.VISIBLE);
        if (!mClient.isStreaming()) {
            String ip, port, path;


            Pattern uri = Pattern.compile("rtsp://(.+):(\\d*)/(.+)");
            Matcher m = uri.matcher(Const.LIVE_APP + stream);
            m.find();
            ip = m.group(1);
            port = m.group(2);
            path = m.group(3);


            mClient.setServerAddress(ip, Integer.parseInt(port));
            mClient.setStreamPath("/" + path);
            mClient.startStream();

            SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
           // SharedPreferences.Editor edit = pref.edit();
            String text = pref.getString("stream",null);
            if(text == null){
                Date dNow = new Date( );
                SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd  hh:mm:ss");
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("stream",stream);
                edit.putString("started_at",ft.format(dNow));
                edit.apply();

                Map<String, String> params = new HashMap<String, String>();
                params.put("action","add");
                params.put("stream",stream);
                params.put("user", new PreferenceStorage(getApplicationContext()).getUserDetails().get(PreferenceStorage.KEY_USERNAME));

                params.put("started_at", ft.format(dNow));
                params.put("title",mEditText.getText().toString());

                CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.LIVE_URL, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError response) {
                        Log.d("Response: ", response.toString());
                    }
                });

                queue.add(jsObjRequest);

            }

        } else {
            // Stops the stream and disconnects from the RTSP server
            mClient.stopStream();
            //mSession.stop();
        }
    }

    private void logError(final String msg) {
        final String error = (msg == null) ? "Error unknown" : msg;
        // Displays a popup to report the eror to the user
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBitrateUpdate(long bitrate) {
        mTextBitrate.setText("" + bitrate / 1000 + " kbps");
    }

    @Override
    public void onPreviewStarted() {
        if (mSession.getCamera() == CameraInfo.CAMERA_FACING_FRONT) {
            mButtonFlash.setEnabled(false);
            mButtonFlash.setTag("off");
            mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
        } else {
            mButtonFlash.setEnabled(true);
        }
    }

    @Override
    public void onSessionConfigured() {

    }

    @Override
    public void onSessionStarted() {
        enableUI();
        mButtonStart.setImageResource(R.drawable.ic_switch_video_active);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSessionStopped() {
        enableUI();
        mButtonStart.setImageResource(R.drawable.ic_switch_video);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        mProgressBar.setVisibility(View.GONE);
        switch (reason) {
            case Session.ERROR_CAMERA_ALREADY_IN_USE:
                break;
            case Session.ERROR_CAMERA_HAS_NO_FLASH:
                mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
                mButtonFlash.setTag("off");
                break;
            case Session.ERROR_INVALID_SURFACE:
                break;
            case Session.ERROR_STORAGE_NOT_READY:
                break;
            case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
                VideoQuality quality = mSession.getVideoTrack().getVideoQuality();
                logError("The following settings are not supported on this phone: " +
                        quality.toString() + " " +
                        "(" + e.getMessage() + ")");
                e.printStackTrace();
                return;
            case Session.ERROR_OTHER:
                break;
        }

        if (e != null) {
            logError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onRtspUpdate(int message, Exception e) {
        switch (message) {
            case RtspClient.ERROR_CONNECTION_FAILED:
            case RtspClient.ERROR_WRONG_CREDENTIALS:
                mProgressBar.setVisibility(View.GONE);
                enableUI();
                logError(e.getMessage());
                e.printStackTrace();
                break;
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSession.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mClient.stopStream();
    }
}

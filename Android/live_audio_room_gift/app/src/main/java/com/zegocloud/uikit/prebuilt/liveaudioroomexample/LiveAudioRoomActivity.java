package com.zegocloud.uikit.prebuilt.liveaudioroomexample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAParser.ParseCompletion;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.plugin.common.ZegoSignalingInRoomTextMessage;
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomConfig;
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomFragment;
import com.zegocloud.uikit.prebuilt.liveaudioroom.core.ZegoLiveAudioRoomRole;
import com.zegocloud.uikit.service.defines.ZegoInRoomCommandListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.utils.Utils;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import org.json.JSONException;
import org.json.JSONObject;

public class LiveAudioRoomActivity extends AppCompatActivity {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_audio_room);
        addFragment();
    }

    private void addFragment() {
        long appID = getIntent().getLongExtra("appID", 0L);
        String appSign = getIntent().getStringExtra("appSign");
        String userID = getIntent().getStringExtra("userID");
        String userName = getIntent().getStringExtra("userName");
        boolean isHost = getIntent().getBooleanExtra("host", false);
        String roomID = getIntent().getStringExtra("roomID");
        String serverSecret = getIntent().getStringExtra("serverSecret");

        ZegoUIKitPrebuiltLiveAudioRoomConfig config;
        if (isHost) {
            config = ZegoUIKitPrebuiltLiveAudioRoomConfig.host();
        } else {
            config = ZegoUIKitPrebuiltLiveAudioRoomConfig.audience();
        }

        ZegoUIKitPrebuiltLiveAudioRoomFragment fragment = ZegoUIKitPrebuiltLiveAudioRoomFragment.newInstance(appID,
            appSign, userID, userName, roomID, config);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commitNow();

        // set a view as roomBackground
        AudioRoomBackgroundView roomBackgroundView = new AudioRoomBackgroundView(this);
        roomBackgroundView.setRoomID(roomID);
        roomBackgroundView.setRoomName("Live Audio Room");
        fragment.setBackgroundView(roomBackgroundView);

        // init svg animation engine
        SVGAParser.Companion.shareParser().init(this);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(com.zegocloud.uikit.R.drawable.icon_hangup);
        int size = Utils.dp2px(36f, getResources().getDisplayMetrics());
        int marginTop = Utils.dp2px(10f, getResources().getDisplayMetrics());
        int marginBottom = Utils.dp2px(16f, getResources().getDisplayMetrics());
        int marginEnd = Utils.dp2px(8, getResources().getDisplayMetrics());
        LayoutParams layoutParams = new LayoutParams(size, size);
        layoutParams.topMargin = marginTop;
        layoutParams.bottomMargin = marginBottom;
        layoutParams.rightMargin = marginEnd;
        imageView.setLayoutParams(layoutParams);
        // add a gift button to liveAudioRoom audience
        fragment.addButtonToBottomMenuBar(Collections.singletonList(imageView), ZegoLiveAudioRoomRole.AUDIENCE);

        imageView.setOnClickListener(v -> {
            final String path = "https://zego-example-server-nextjs.vercel.app/api/send_gift";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("app_id", appID);
                jsonObject.put("server_secret", serverSecret);
                jsonObject.put("room_id", roomID);
                jsonObject.put("user_id", userID);
                jsonObject.put("user_name", userName);
                jsonObject.put("gift_type", 1001);
                jsonObject.put("gift_count", 1);
                jsonObject.put("timestamp", System.currentTimeMillis());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String jsonString = jsonObject.toString();
            new Thread() {
                public void run() {
                    // when post success,show send gift animation
                    httpPost(path, jsonString, () -> showAnimation("sports-car.svga", roomBackgroundView));
                }
            }.start();
        });

        // when someone send gift,will receive InRoomCommand or InRoomTextMessage
        ZegoUIKit.addInRoomCommandListener(new ZegoInRoomCommandListener() {
            @Override
            public void onInRoomCommandReceived(ZegoUIKitUser fromUser, String command) {
                Log.d(ZegoUIKit.TAG,
                    "onInRoomCommandReceived() called with: fromUser = [" + fromUser + "], command = [" + command
                        + "]");
                if (!fromUser.userID.equals(userID) && command.contains("gift_type")) {
                    showAnimation("sports-car.svga", roomBackgroundView);
                }
            }
        });

        // when someone send gift,will receive InRoomCommand or InRoomTextMessage
        ZegoUIKit.getSignalingPlugin().addInRoomTextMessageListener(messages -> {
            Log.d(ZegoUIKit.TAG, "onInRoomTextMessageReceived() called with: messages = [" + messages + "]");
            if (!messages.isEmpty()) {
                ZegoSignalingInRoomTextMessage message = messages.get(0);
                if (!message.senderUserID.equals(userID)) {
                    showAnimation("sports-car.svga", roomBackgroundView);
                }
            }
        });

    }

    /**
     * post json to server,will receive InRoomCommand or InRoomTextMessage
     *
     * @param path
     * @param jsonString
     * @param successCallback
     */
    private void httpPost(String path, String jsonString, Runnable successCallback) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            byte[] writebytes = jsonString.getBytes();
            conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
            OutputStream outwritestream = conn.getOutputStream();
            outwritestream.write(jsonString.getBytes());
            outwritestream.flush();
            outwritestream.close();
            int code = conn.getResponseCode();
            Log.d(ZegoUIKit.TAG, "run: " + code);
            if (code == 200) {
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buffer = new byte[1024]; //1kb
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                String content = baos.toString();
                Log.d(ZegoUIKit.TAG, "run() called:" + content);
                handler.post(successCallback);
            } else {
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
    }

    private void showAnimation(String filename, AudioRoomBackgroundView roomBackgroundView) {
        SVGAParser.Companion.shareParser().decodeFromAssets(filename, new ParseCompletion() {
            @Override
            public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                SVGAImageView svgaImageView = new SVGAImageView(LiveAudioRoomActivity.this);
                svgaImageView.setLoops(1);
                roomBackgroundView.addView(svgaImageView);
                svgaImageView.setVideoItem(svgaVideoEntity);
                svgaImageView.stepToFrame(0, true);
                svgaImageView.setCallback(new SVGACallback() {
                    @Override
                    public void onPause() {

                    }

                    @Override
                    public void onFinished() {
                        roomBackgroundView.removeView(svgaImageView);
                    }

                    @Override
                    public void onRepeat() {

                    }

                    @Override
                    public void onStep(int i, double v) {

                    }
                });
            }

            @Override
            public void onError() {

            }
        }, null);
    }
}
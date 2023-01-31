package com.zegocloud.uikit.prebuilt.liveaudioroomexample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

        // set a view as roomBackground
        AudioRoomBackgroundView roomBackgroundView = new AudioRoomBackgroundView(this);
        roomBackgroundView.setRoomID(roomID);
        roomBackgroundView.setRoomName("Live Audio Room");
        fragment.setBackgroundView(roomBackgroundView);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commitNow();

        ViewGroup rootLayout = findViewById(R.id.layout);
        GiftHelper helper = new GiftHelper(this, appID, appSign, serverSecret, userID, userName, roomID, rootLayout);
        helper.addGiftButton(this, fragment);
    }
}
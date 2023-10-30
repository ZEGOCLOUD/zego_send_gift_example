package com.zegocloud.uikit.prebuilt.liveaudioroomexample;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomConfig;
import com.zegocloud.uikit.prebuilt.liveaudioroom.ZegoUIKitPrebuiltLiveAudioRoomFragment;
import com.zegocloud.uikit.prebuilt.liveaudioroom.core.ZegoLiveAudioRoomRole;
import com.zegocloud.uikit.prebuilt.liveaudioroomexample.gift.GiftHelper;
import java.util.Collections;

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

        // add a gift button to liveAudioRoom audience
        GiftHelper giftHelper = new GiftHelper(findViewById(R.id.layout), userID, userName);
        View giftButton = giftHelper.getGiftButton(this, appID, serverSecret, roomID);
        fragment.addButtonToBottomMenuBar(Collections.singletonList(giftButton), ZegoLiveAudioRoomRole.AUDIENCE);


    }
}
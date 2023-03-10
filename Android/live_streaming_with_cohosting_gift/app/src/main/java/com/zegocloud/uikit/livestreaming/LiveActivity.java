package com.zegocloud.uikit.livestreaming;

import android.os.Bundle;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.zegocloud.uikit.plugin.common.IZegoUIKitPlugin;
import com.zegocloud.uikit.plugin.signaling.ZegoSignalingPlugin;
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingConfig;
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingFragment;
import java.util.Collections;
import java.util.List;

public class LiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        addFragment();
    }

    private void addFragment() {
        long appID = getIntent().getLongExtra("appID", 0L);
        String appSign = getIntent().getStringExtra("appSign");
        String userID = getIntent().getStringExtra("userID");
        String userName = getIntent().getStringExtra("userName");

        boolean isHost = getIntent().getBooleanExtra("host", false);
        String liveID = getIntent().getStringExtra("liveID");
        String serverSecret = getIntent().getStringExtra("serverSecret");

        ZegoUIKitPrebuiltLiveStreamingConfig config;
        List<IZegoUIKitPlugin> plugins = Collections.singletonList(ZegoSignalingPlugin.getInstance());
        if (isHost) {
            config = ZegoUIKitPrebuiltLiveStreamingConfig.host(plugins);
        } else {
            config = ZegoUIKitPrebuiltLiveStreamingConfig.audience(plugins);
            ;
        }

        ZegoUIKitPrebuiltLiveStreamingFragment fragment = ZegoUIKitPrebuiltLiveStreamingFragment.newInstance(appID,
            appSign, userID, userName, liveID, config);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commitNow();

        ViewGroup rootLayout = findViewById(R.id.layout);
        GiftHelper helper = new GiftHelper(this, appID, appSign, serverSecret, userID, userName, liveID, rootLayout);
        helper.addGiftButton(this, fragment);
    }
}
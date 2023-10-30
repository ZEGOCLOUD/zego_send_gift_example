package com.zegocloud.uikit.prebuilt.liveaudioroomexample.gift;

import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.tencent.qgame.animplayer.AnimConfig;
import com.tencent.qgame.animplayer.AnimView;
import com.tencent.qgame.animplayer.inter.IAnimListener;
import com.tencent.qgame.animplayer.util.ScaleType;

public class VAPAnimation implements GiftAnimation {

    private String animationFileName = "demo.mp4";
    private ViewGroup parentView;
    private AnimView animView;
    private static final String TAG = "VAPAnimation";

    public VAPAnimation(ViewGroup animationViewParent) {
        parentView = animationViewParent;

        animView = new AnimView(animationViewParent.getContext());
        animView.setScaleType(ScaleType.FIT_CENTER);
        animationViewParent.addView(animView);
        animView.setAnimListener(new IAnimListener() {
            @Override
            public boolean onVideoConfigReady(@NonNull AnimConfig animConfig) {
                return true;
            }

            @Override
            public void onVideoStart() {
                Log.d(TAG, "onVideoStart() called");
            }

            @Override
            public void onVideoRender(int i, @Nullable AnimConfig animConfig) {
                Log.d(TAG, "onVideoRender() called with: i = [" + i + "], animConfig = [" + animConfig + "]");
            }

            @Override
            public void onVideoComplete() {
                Log.d(TAG, "onVideoComplete() called");
            }

            @Override
            public void onVideoDestroy() {
                Log.d(TAG, "onVideoDestroy() called");
            }

            @Override
            public void onFailed(int i, @Nullable String s) {
                Log.d(TAG, "onFailed() called with: i = [" + i + "], s = [" + s + "]");
            }
        });
    }

    @Override
    public void startPlay() {
        animView.startPlay(parentView.getContext().getAssets(), animationFileName);
    }
}

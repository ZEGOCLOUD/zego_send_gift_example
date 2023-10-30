package com.zegocloud.uikit.prebuilt.liveaudioroomexample.gift;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAParser.ParseCompletion;
import com.opensource.svgaplayer.SVGAVideoEntity;

public class SVGAAnimation implements GiftAnimation {

    private String animationFileName = "sports-car.svga";
    private ViewGroup parentView;

    public SVGAAnimation(ViewGroup animationViewParent) {
        this.parentView = animationViewParent;
        SVGAParser.Companion.shareParser().init(animationViewParent.getContext());
    }

    @Override
    public void startPlay() {
        SVGAParser.Companion.shareParser().decodeFromAssets(animationFileName, new ParseCompletion() {
            @Override
            public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                SVGAImageView svgaImageView = new SVGAImageView(parentView.getContext());
                svgaImageView.setLoops(1);
                parentView.addView(svgaImageView);
                svgaImageView.setVideoItem(svgaVideoEntity);
                svgaImageView.stepToFrame(0, true);
                svgaImageView.setCallback(new SVGACallback() {
                    @Override
                    public void onPause() {

                    }

                    @Override
                    public void onFinished() {
                        parentView.removeView(svgaImageView);
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

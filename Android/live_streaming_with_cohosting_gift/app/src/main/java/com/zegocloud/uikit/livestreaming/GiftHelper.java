package com.zegocloud.uikit.livestreaming;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import androidx.annotation.NonNull;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAParser.ParseCompletion;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.plugin.common.ZegoSignalingInRoomTextMessage;
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingFragment;
import com.zegocloud.uikit.prebuilt.livestreaming.core.ZegoLiveStreamingRole;
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

public class GiftHelper {

    private Handler handler = new Handler(Looper.getMainLooper());
    private long appID;
    private String appSign;
    private String serverSecret;
    private String userID;
    private String userName;
    private String roomID;
    private String animationFileName = "sports-car.svga";
    private ViewGroup animationViewParent;

    public GiftHelper(Context context, long appID, String appSign, String serverSecret, String userID, String userName,
        String roomID, ViewGroup animationViewParent) {
        initGiftAnimation(context, appID, appSign, serverSecret, userID, userName, roomID, animationViewParent);
    }

    // init svg animation engine
    private void initGiftAnimation(Context context, long appID, String appSign, String serverSecret, String userID,
        String userName, String roomID, ViewGroup animationViewParent) {
        this.appID = appID;
        this.appSign = appSign;
        this.serverSecret = serverSecret;
        this.userID = userID;
        this.userName = userName;
        this.roomID = roomID;
        this.animationViewParent = animationViewParent;

        SVGAParser.Companion.shareParser().init(context);

        // when someone send gift,will receive InRoomCommand or InRoomTextMessage
        ZegoUIKit.addInRoomCommandListener(new ZegoInRoomCommandListener() {
            @Override
            public void onInRoomCommandReceived(ZegoUIKitUser fromUser, String command) {
                if (!fromUser.userID.equals(userID) && command.contains("gift_type")) {
                    showAnimation();
                }
            }
        });

        // when someone send gift,will receive InRoomCommand or InRoomTextMessage
        ZegoUIKit.getSignalingPlugin().addInRoomTextMessageListener((messages) -> {
            if (!messages.isEmpty()) {
                ZegoSignalingInRoomTextMessage message = messages.get(0);
                if (!message.senderUserID.equals(userID)) {
                    showAnimation();
                }
            }
        });

    }

    public void addGiftButton(Context context, ZegoUIKitPrebuiltLiveStreamingFragment fragment) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.presents_icon);
        int size = Utils.dp2px(36f, context.getResources().getDisplayMetrics());
        int marginTop = Utils.dp2px(10f, context.getResources().getDisplayMetrics());
        int marginBottom = Utils.dp2px(16f, context.getResources().getDisplayMetrics());
        int marginEnd = Utils.dp2px(8, context.getResources().getDisplayMetrics());
        LayoutParams layoutParams = new LayoutParams(size, size);
        layoutParams.topMargin = marginTop;
        layoutParams.bottomMargin = marginBottom;
        layoutParams.rightMargin = marginEnd;
        imageView.setLayoutParams(layoutParams);

        // add a gift button to liveAudioRoom audience
        fragment.addButtonToBottomMenuBar(Collections.singletonList(imageView), ZegoLiveStreamingRole.AUDIENCE);

        // click will post json to server
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
                    httpPost(path, jsonString, () -> showAnimation());
                }
            }.start();
        });
    }

    /**
     * post json to server,will receive InRoomCommand or InRoomTextMessage callback
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

    private void showAnimation() {

        SVGAParser.Companion.shareParser().decodeFromAssets(animationFileName, new ParseCompletion() {
            @Override
            public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
                SVGAImageView svgaImageView = new SVGAImageView(animationViewParent.getContext());
                svgaImageView.setLoops(1);
                animationViewParent.addView(svgaImageView);
                svgaImageView.setVideoItem(svgaVideoEntity);
                svgaImageView.stepToFrame(0, true);
                svgaImageView.setCallback(new SVGACallback() {
                    @Override
                    public void onPause() {

                    }

                    @Override
                    public void onFinished() {
                        animationViewParent.removeView(svgaImageView);
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

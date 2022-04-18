package com.example.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    //视频
    private StandardVideoPlayer videoPlayer;
    private Button btInputRtmp;

    private EditText etRtmpUrl;

    private String urlAddress;//流地址
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        videoPlayer = findViewById(R.id.id_videoview);
        btInputRtmp = findViewById(R.id.btInputRtmp);
        btInputRtmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        videoPlayer.setLiveViewGone();
        //title不显示
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.getBackButton().setVisibility(View.GONE);

        urlAddress = PreferenceUtils.getString(mContext, "URL", "");
        Log.d(TAG, "onCreate: " + urlAddress);
        if (urlAddress.equals("")) {
            showDialog();
        } else {
            initPlayer(urlAddress);
            DisplayUtil.hideNavBar(MainActivity.this);
        }
    }

    public void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.my_dialoge, null);
        dialog.setView(layout);
        dialog.setCancelable(false);
        etRtmpUrl = (EditText)layout.findViewById(R.id.searchC);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                urlAddress = etRtmpUrl.getText().toString();
                DisplayUtil.hideNavBar(MainActivity.this);

                GSYVideoManager.releaseAllVideos();

                initPlayer(urlAddress);
                PreferenceUtils.putString(mContext, "URL", urlAddress);
            }
        });

        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DisplayUtil.hideNavBar(MainActivity.this);
                dialog.dismiss();
            }

        });

        dialog.show();
    }


    //播放视频数据
    private void initPlayer(String url) {
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "udp");
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);

        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp");
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "allowed_media_types", "video"); //根据媒体类型来配置
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 20000);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1024000);//TODO===花屏问题----UDP播放时，加大此参数来解决花屏问题
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1);  // 无限读
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1);
        list.add(videoOptionModel);

        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 0);//最大缓存数
        list.add(videoOptionModel);

        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 0);//最大缓存时长
        list.add(videoOptionModel);

        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5);//丢帧,太卡可以尝试丢帧
        list.add(videoOptionModel);

//        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);//关闭硬解码
//        list.add(videoOptionModel);
//
//        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "videotoolbox", 1);//打开软件解码
//        list.add(videoOptionModel);


        //  关闭播放器缓冲，这个必须关闭，否则会出现播放一段时间后，一直卡主，控制台打印 FFP_MSG_BUFFERING_START
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
        list.add(videoOptionModel);

        GSYVideoManager.instance().setOptionModelList(list);

        String source1 = url;
        videoPlayer.setUp(source1, false, "");
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.startPlayLogic();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

}
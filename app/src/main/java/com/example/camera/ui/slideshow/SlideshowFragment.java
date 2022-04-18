package com.example.camera.ui.slideshow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.camera.DisplayUtil;
import com.example.camera.MainActivity;
import com.example.camera.PreferenceUtils;
import com.example.camera.R;
import com.example.camera.StandardVideoPlayer;
import com.example.camera.databinding.FragmentSlideshowBinding;
import com.example.camera.util.PublicClient;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    //视频
    private StandardVideoPlayer videoPlayer;
    private Button btInputRtmp;
    private Button button2;
    private EditText etRtmpUrl;

    private String urlAddress;//流地址
    private Context mContext;
    private EditText editText;
    private PublicClient publicClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        button2= root.findViewById(R.id.button2);
//        editText=root.findViewById(R.id.editText);
        videoPlayer = root.findViewById(R.id.videoplayer);
        Switch douyuSwitch=root.findViewById(R.id.douyu_switch);
        publicClient = new PublicClient();
        douyuSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    publicClient.sendMessage("1","douyu");
                }
                else{
                    publicClient.sendMessage("0","douyu");
                }
            }
        });
        final TextView textView = binding.editText;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                urlAddress=editText.getText().toString();
                urlAddress="rtmp://119.3.231.146/live/mawang";
                GSYVideoManager.releaseAllVideos();
                initPlayer(urlAddress);
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GSYVideoManager.releaseAllVideos();
        binding = null;
    }

//    public void showDialog() {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.my_dialoge, null);
//        dialog.setView(layout);
//        dialog.setCancelable(false);
//        etRtmpUrl = (EditText)layout.findViewById(R.id.searchC);
//        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                urlAddress = etRtmpUrl.getText().toString();
//                DisplayUtil.hideNavBar(MainActivity.this);
//
//
//                PreferenceUtils.putString(mContext, "URL", urlAddress);
//            }
//        });
//
//        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                DisplayUtil.hideNavBar(MainActivity.this);
//                dialog.dismiss();
//            }
//
//        });
//
//        dialog.show();
//    }


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

}
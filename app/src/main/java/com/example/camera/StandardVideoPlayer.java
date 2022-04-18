package com.example.camera;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.ListGSYVideoPlayer;

/**
 * @Author: yuqingfan
 * @Description:
 * @date: 2020/11/16
 */
public class StandardVideoPlayer extends ListGSYVideoPlayer {
    private static final String TAG = "StandardVideoPlayer";
    public StandardVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public StandardVideoPlayer(Context context) {
        super(context);
    }

    public StandardVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public void clickStartIcon() {
        super.clickStartIcon();
    }

    @Override
    public int getLayoutId() {
        return R.layout.standard_video_view;
    }

    //直播时隐藏相关按钮
    public void setLiveViewGone() {
        mCurrentTimeTextView.setVisibility(INVISIBLE);
        mTotalTimeTextView.setVisibility(INVISIBLE);
        mProgressBar.setVisibility(INVISIBLE);
        mFullscreenButton.setVisibility(INVISIBLE);
        mStartButton.setVisibility(INVISIBLE);

    }

    @Override
    protected void touchSurfaceMove(float deltaX, float deltaY, float y) {
        int curWidth = CommonUtil.getCurrentScreenLand((Activity) getActivityContext()) ? mScreenHeight : mScreenWidth;
        int curHeight = CommonUtil.getCurrentScreenLand((Activity) getActivityContext()) ? mScreenWidth : mScreenHeight;

        if (mChangePosition) {

        } else if (mChangeVolume) {
            deltaY = -deltaY;
            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int deltaV = (int) (max * deltaY * 3 / curHeight);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
            int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / curHeight);

            showVolumeDialog(-deltaY, volumePercent);
        } else if (!mChangePosition && mBrightness) {
            if (Math.abs(deltaY) > mThreshold) {
                float percent = (-deltaY / curHeight);
                onBrightnessSlide(percent);
                mDownY = y;
            }
        }
    }

    @Override
    protected void touchSurfaceUp() {
        if (mChangePosition) {
        }

        mTouchingProgressBar = false;
        dismissProgressDialog();
        dismissVolumeDialog();
        dismissBrightnessDialog();
        if (mChangePosition && getGSYVideoManager() != null && (mCurrentState == CURRENT_STATE_PLAYING || mCurrentState == CURRENT_STATE_PAUSE)) {
        } else if (mBrightness) {
            if (mVideoAllCallBack != null && isCurrentMediaListener()) {
                Debuger.printfLog("onTouchScreenSeekLight");
                mVideoAllCallBack.onTouchScreenSeekLight(mOriginUrl, mTitle, this);
            }
        } else if (mChangeVolume) {
            if (mVideoAllCallBack != null && isCurrentMediaListener()) {
                Debuger.printfLog("onTouchScreenSeekVolume");
                mVideoAllCallBack.onTouchScreenSeekVolume(mOriginUrl, mTitle, this);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}

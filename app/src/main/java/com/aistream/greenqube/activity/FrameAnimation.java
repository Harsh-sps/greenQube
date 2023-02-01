package com.aistream.greenqube.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

public class FrameAnimation {

    private boolean mIsRepeat;

    private AnimationListener mAnimationListener;

    private ImageView mImageView;

    private int[] mFrameRess;

    private int mDuration;

    private int mLastFrame;

    private boolean mNext;

    private boolean mPause;

    private int mCurrentFrame;

    private Bitmap mBitmap = null;
    private BitmapFactory.Options mBitmapOptions;

    /**
     * @param iv
     * @param frameRes img res array
     * @param duration each frame show duration
     * @param isRepeat whether repeat
     */
    public FrameAnimation(ImageView iv, int[] frameRes, int duration, boolean isRepeat) {
        this.mImageView = iv;
        this.mFrameRess = frameRes;
        this.mDuration = duration;
        this.mLastFrame = frameRes.length - 1;
        this.mIsRepeat = isRepeat;
        play(0);
    }

    private void initImgBitMap() {
        if (Build.VERSION.SDK_INT >= 11) {
            mImageView.setImageResource(mFrameRess[0]);
            Bitmap bmp = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            Log.d("FrameAnimation", "width: "+width+", height: "+height+", bmp: "+bmp);
            Bitmap.Config config = bmp.getConfig()!= null ? bmp.getConfig() : Bitmap.Config.ARGB_8888;
            Log.d("FrameAnimation", "width: "+width+", height: "+height+", config: "+config);
            mBitmap = Bitmap.createBitmap(width, height, config);
            mBitmapOptions = new BitmapFactory.Options();
            //set bitmap memory reuse
            mBitmapOptions.inBitmap = mBitmap;
            mBitmapOptions.inMutable = true;
            mBitmapOptions.inSampleSize = 1;
        }
    }

    private void play(final int i) {
        mImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                    initImgBitMap();
                } else {
                    int imageRes = mFrameRess[i];
                    if (mBitmap != null) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeResource(mImageView.getResources(), imageRes, mBitmapOptions);
                        } catch (Exception e) {
                        }

                        if (bitmap != null) {
                            mImageView.setImageBitmap(bitmap);
                        } else {
                            mImageView.setImageResource(imageRes);
                            mBitmap.recycle();
                            mBitmap = null;
                        }
                    } else {
                        mImageView.setImageResource(imageRes);
                    }
                }

                if (i == mLastFrame) {
                    if (mIsRepeat) {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationRepeat();
                        }
                        play(0);
                    } else {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationEnd();
                        }
                    }
                } else {
                    int pos = i + 2;
                    play(pos > mLastFrame? mLastFrame: pos);
                }
            }
        }, mDuration);
    }

    public static interface AnimationListener {

        /**
         * <p>Notifies the start of the animation.</p>
         */
        void onAnimationStart();

        /**
         * <p>Notifies the end of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         */
        void onAnimationEnd();

        /**
         * <p>Notifies the repetition of the animation.</p>
         */
        void onAnimationRepeat();
    }

    /**
     * <p>Binds an animation listener to this animation. The animation listener
     * is notified of animation events such as the end of the animation or the
     * repetition of the animation.</p>
     *
     * @param listener the animation listener to be notified
     */
    public void setAnimationListener(AnimationListener listener) {
        this.mAnimationListener = listener;
    }

    public void release() {
        pauseAnimation();
    }

    public void pauseAnimation() {
        this.mPause = true;
    }

    public boolean isPause() {
        return this.mPause;
    }
}

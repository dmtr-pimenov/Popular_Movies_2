package com.example.android.popularmovies.ui;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class ViewPagerChanger implements LifecycleObserver {

    private static final String TAG = ViewPagerChanger.class.getSimpleName();

    private static final long DEFAULT_TIME_INTERVAL_TO_CHANGE_PAGE = 5_000L;

    private Timer mTimer;
    private final Handler mHandler;
    private final Lifecycle mLifecycle;
    private final ViewPager mViewPager;
    private long mChangeTimeInterval = DEFAULT_TIME_INTERVAL_TO_CHANGE_PAGE;
    private boolean mChangerStarted = false;

    public ViewPagerChanger(@NonNull LifecycleOwner lifecycleOwner, @NonNull ViewPager viewPager) {
        mViewPager = viewPager;
        mHandler = new Handler(Looper.getMainLooper());
        setTouchListener();
        mLifecycle = lifecycleOwner.getLifecycle();
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    public ViewPagerChanger(@NonNull LifecycleOwner lifecycleOwner, @NonNull ViewPager viewPager, long changeTimeInterval) {
        this(lifecycleOwner, viewPager);
        mChangeTimeInterval = changeTimeInterval;
    }

    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showNextPage();
                            }
                        });
                        startTimer();
                    }
                });
            }
        };
    }

    @UiThread
    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(createTimerTask(), mChangeTimeInterval);
    }

    @UiThread
    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    private void setTouchListener() {
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cancelTimer();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    startTimer();
                }
                return false;
            }
        });
    }

    private void showNextPage() {
        int currentItem = mViewPager.getCurrentItem();
        int count = mViewPager.getAdapter().getCount();
        if (count > 0) {
            int nextItem = (currentItem < (count - 1)) ? currentItem + 1 : 0;
            mViewPager.setCurrentItem(nextItem, true);
        }
    }

    public long getChangeTimeInterval() {
        return mChangeTimeInterval;
    }

    public void setChangeTimeInterval(long changeTimeInterval) {
        mChangeTimeInterval = changeTimeInterval;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void stopChanges() {
        Log.d(TAG, "stopChanges: ");
        cancelTimer();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void startChanges() {
        Log.d(TAG, "startChanges: ");
        if (mChangerStarted) {
            startTimer();
        }
    }

    public void start() {
        if (!mChangerStarted) {
            mChangerStarted = true;
            if (mLifecycle.getCurrentState() == Lifecycle.State.RESUMED) {
                startTimer();
            }
        }
    }

    public void stop() {
        if (mChangerStarted) {
            mChangerStarted = false;
            cancelTimer();
        }
    }

    public boolean isStarted() {
        return mChangerStarted;
    }
}

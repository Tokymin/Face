package com.guo.android_extend.tools;

import android.util.Log;

public class FrameHelper {
	private final String TAG = this.getClass().getSimpleName();
	
	//FOR DEBUG
	private long mStartTime;
	private long mFrames;
	private boolean isShow;
	
	public FrameHelper() {
		// TODO Auto-generated constructor stub
		isShow = true;
		reset();
	}
	
	public void printFPS() {
		if (isShow) {
			if (mStartTime == 0) {
				mStartTime = System.currentTimeMillis();
				mFrames = 0;
			} else {
				mFrames++;
				if (mFrames >= 10) {
					Log.e(TAG, "FPS = " + (int)(1000.0*mFrames/(System.currentTimeMillis() - mStartTime)));
					mStartTime = 0;
				}
			}
		}
	}
	
	public void reset() {
		 mStartTime = 0;
		 mFrames = 0;
	}
	
	public void enable(boolean show) {
		if (isShow) {
			reset();
		}
		this.isShow = show;
	}
}

package com.guo.android_extend.device;

import java.util.LinkedList;

import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.tools.FrameHelper;
import com.guo.android_extend.image.ImageConverter;

import android.os.Handler;
import android.os.Message;

public class VideoClient extends AbsLoop {
	private final String TAG = this.getClass().getSimpleName();
	
	public static final int VIDEO_CODE = 0x5000;
	public static final int START_MSG = 0x5001;

	private Video mVideo;
	private Handler mHandler;
	private int mPreviewWidth, mPreviewHeight, mFormat;
	private FrameHelper mFrameHelper;
	private int mCameraID;
	
	private LinkedList<byte[]> mBufferQueue;
	
	private OnCameraListener mOnCameraListener;
	
	private boolean isPreviewStart;
	
	public interface OnCameraListener {
		/**
		 *
		 * @param data
		 * @param size
		 * @param camera
		 */
		public void onPreview(byte[] data, int size, int camera);
	}
	
	public VideoClient(Handler handle, int port) {
		// TODO Auto-generated constructor stub
		super();
		mHandler = handle;
		mPreviewWidth = 640;
		mPreviewHeight = 480;
		mFormat = ImageConverter.CP_PAF_NV21;
		mCameraID = port;
		try {
			mVideo = new Video(port);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Create VideoClient ERROR");
		}

		mFrameHelper = new FrameHelper();
		
		mBufferQueue = new LinkedList<byte[]>();
		int size = ImageConverter.calcImageSize(mPreviewWidth, mPreviewHeight, mFormat);
		mBufferQueue.clear();
		mBufferQueue.add(new byte[size]);
		
		isPreviewStart = false;
	}

	public void setPreviewSize(int w, int h) {
		mPreviewWidth = w;
		mPreviewHeight = h;
	}
	
	/**
	 * @see ImageConverter
	 * @param format
	 */
	public void setPreviewFormat(int format) {
		mFormat = format;
	}

	@Override
	public void setup() {
		Message msg = new Message();
		msg.what = VIDEO_CODE;
		msg.arg1 = START_MSG;
		mHandler.sendMessage(msg);

		int size = ImageConverter.calcImageSize(mPreviewWidth, mPreviewHeight, mFormat);
		mBufferQueue.clear();
		mBufferQueue.add(new byte[size]);
		mVideo.setVideo(mPreviewWidth, mPreviewHeight, mFormat);
	}

	@Override
	public void loop() {
		byte[] data = mBufferQueue.poll();
		int size = mVideo.readFrame(data);
		if (mOnCameraListener != null && isPreviewStart) {
			mOnCameraListener.onPreview(data, size, mCameraID);
		}
		mBufferQueue.offer(data);
		mFrameHelper.printFPS();
	}

	@Override
	public void over() {
		mVideo.destroy();
		mBufferQueue.clear();
	}
	
	public void addCallbackBuffer(byte[] data) {
		mBufferQueue.offer(data);
	}
	
	public void startPreview() {
		isPreviewStart = true;
	}
	
	public void stopPreview() {
		isPreviewStart = false;
	}
	
	public void setOnCameraListener(OnCameraListener l) {
		mOnCameraListener = l;
	}
}

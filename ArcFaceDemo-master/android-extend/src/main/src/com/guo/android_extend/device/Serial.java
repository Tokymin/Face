package com.guo.android_extend.device;

public class Serial {
	private final String TAG = this.getClass().getSimpleName();

	public static final int TYPE_SERIAL = 0;
	public static final int TYPE_USB_SERIAL = 1;
	private static final int TYPE_FREE_SERIAL = 2;

	public static final int MAX_RECEIVE_SIZE = 255;
	public static final int MAX_RECEIVE_TIMEOUT = 1;	//S

	/**
	 *
	 * @param port		0 == ttyS0
	 * @param type		ttyUSB0 / ttyS0
	 * @return
	 */
	private native long initSerial(int port, byte[] dev, int type);

	/**
	 *
	 * @param handle
	 * @param baud_rate		9600 19200 115200
	 * @param data_bits		7 8
	 * @param parity			'N' 'O' 'E' 'S'
	 * @param stop_bits		1 2
	 * @param vtime			1 (100ms), 0 (0ms)
	 * @param vmin				255  (read buffer size.)
	 * @param vtime			read wait time. 1=(100ms)
	 * @param vmin				read min buffer size.
	 * @return
	 */
	private native int setSerial(long handle, int baud_rate, int data_bits, byte parity, int stop_bits, int vtime, int vmin);
	private native int sendData(long handle, byte[] data, int length);
	private native int receiveData(long handle, byte[] data, int max, int timeout);
	private native int uninitSerial(long handle);
	
	private long mHandle;
	private byte[] mReceive;
	
	static {
		System.loadLibrary("serial");
	}

	public Serial(String dev) {
		mHandle = initSerial(-1, dev.getBytes(), TYPE_FREE_SERIAL);
		if (mHandle == 0) {
			throw new RuntimeException("Open Serial device error!");
		}
		mReceive = new byte[MAX_RECEIVE_SIZE];
	}

	public Serial(int port, int type) {
		mHandle = initSerial(port, null, type);
		if (mHandle == 0) {
			throw new RuntimeException("Open Serial device error!");
		}
		mReceive = new byte[MAX_RECEIVE_SIZE];
	}

	/**
	 *
	 * @param rate				9600 19200 115200
	 * @param data_bits		7 8
	 * @param parity			'N' 'O' 'E' 'S'
	 * @param stop_bits		1 2
	 * @param vtime			1 (100ms), 0 (0ms)
	 * @param vmin				255  (read buffer size.)
	 * @return
	 */
	public boolean setConfig(int rate, int data_bits, byte parity, int stop_bits, int vtime, int vmin ) {
		return 0 == setSerial(mHandle, rate, data_bits, parity, stop_bits, vtime, vmin);
	}

	public boolean send(byte[] data) {
		if (mHandle != 0) {
			sendData(mHandle, data, data.length);
			return true;
		}
		return false;
	}

	public byte[] receive() {
		if (mHandle != 0) {
			int size = receiveData(mHandle, mReceive, MAX_RECEIVE_SIZE, MAX_RECEIVE_TIMEOUT);
			if (size > 0) {
				byte[] raw = new byte[size];
				for (int i = 0; i < raw.length; i++) {
					raw[i] = mReceive[i];
				}
				return raw;
			}
		}
		return null;
	}
	
	public void destroy() {
		if (mHandle != 0) {
			uninitSerial(mHandle);
		}
	}
	
}

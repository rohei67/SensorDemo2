package com.example.android.sensor2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class AbstractSensorView extends SurfaceView implements
		SurfaceHolder.Callback {
	private Handler mHandler = new Handler();
	private UpdaterThread mThread = new UpdaterThread();
	protected float[] mValues;
	private int mWidth;

	protected AbstractSensorView(Context context) {
		super(context);
		getHolder().addCallback(this);
		mThread.start();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// nothing to do
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mWidth = width;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mThread.interrupt();
	}

	void onValueChanged(float[] values) {
		this.mValues = values;
		synchronized (mThread) {
			mThread.notifyAll();
		}
	}

	abstract void repaint(Canvas canvas, Paint paint);

	private class UpdaterThread extends Thread {
		private boolean mIsRunning = false;
		private Runnable mRunnable = new Runnable() {
			private long mPrev = System.currentTimeMillis();

			@Override
			public void run() {
				Canvas canvas = getHolder().lockCanvas();
				if (canvas != null) {
					long now = System.currentTimeMillis();
					canvas.drawColor(Color.BLACK);
					Paint paint = new Paint();
					paint.setAntiAlias(true);
					paint.setColor(Color.YELLOW);
					paint.setTextSize(24);

					repaint(canvas, paint);

					String fps = String.valueOf(1000.0 / (now - mPrev));
					int index = fps.indexOf('.');
					if (index == -1) {
						index = fps.indexOf(',');
					}
					if (index != -1) {
						fps = fps.substring(0, index + 2);
					}
					fps = "fps:" + fps;

					paint.setColor(Color.RED);
					canvas.drawText(fps, mWidth - paint.measureText(fps),
							paint.getTextSize() * 1, paint);

					getHolder().unlockCanvasAndPost(canvas);
					mPrev = now;
				}
				mIsRunning = false;
			}
		};

		private UpdaterThread() {
			setDaemon(true);
		}

		@Override
		public void run() {
			while (!interrupted()) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						break;
					}
					if (!mIsRunning) {
						mIsRunning = true;
						mHandler.post(mRunnable);
					}
				}
			}
		}
	}
}

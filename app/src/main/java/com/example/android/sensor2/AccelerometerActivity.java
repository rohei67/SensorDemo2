package com.example.android.sensor2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.view.SurfaceHolder;

public class AccelerometerActivity extends AbstractSensorActivity {
	private float[] mGravity = new float[3];
	private float[] mLinearAcceleration = new float[3];

	@Override
	AbstractSensorView getSensorView(Context context) {
		return new SensorView(context);
	}

	@Override
	int getSensorType() {
		return Sensor.TYPE_ACCELEROMETER;
	}

	private class SensorView extends AbstractSensorView {
		private static final int SAMPLING = 100;
		private static final int HOLDING = 500 * 1000 * 1000; // 500ms
		private Bitmap mPlain, mFiring;
		private float[] mPrevX;
		private int mShakeCounter;
		private int mDegrees;
		private int mX, mY;
		private int mCount;
		private long mStart;

		private SensorView(Context context) {
			super(context);
			mPlain = BitmapFactory.decodeResource(getResources(),
					R.drawable.lander_plain);
			mFiring = BitmapFactory.decodeResource(getResources(),
					R.drawable.lander_firing);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			super.surfaceChanged(holder, format, width, height);
			mX = (getWidth() - mPlain.getWidth()) / 2;
			mY = (getHeight() - mPlain.getHeight()) / 2;
		}

		@Override
		void repaint(Canvas canvas, Paint paint) {
			if (mPrevX == null) {
				if (mCount < SAMPLING) {
					if (mStart == 0) {
						mStart = System.nanoTime();
					}
					mCount++;
					return;
				} else if (mCount == SAMPLING) {
					long now = System.nanoTime();
					long ave = (now - mStart) / SAMPLING;
					mPrevX = new float[(int) (HOLDING / ave) + 1];
					mCount = 0;
				}
			}
			Utils.extractGravity(mValues, mGravity, mLinearAcceleration);
			if (mShakeCounter == 0) {
				float dx = 0;
				for (int i = 0; i < mPrevX.length; i++) {
					dx += mPrevX[i];
				}
				dx /= mPrevX.length;

				if (dx > 0 && mLinearAcceleration[0] > 0) {
					if (mLinearAcceleration[0] - dx > 6) {
						mShakeCounter = mPrevX.length;
						mDegrees = 90;
					}
				} else if (dx < 0 && mLinearAcceleration[0] < 0) {
					if (mLinearAcceleration[0] - dx < -6) {
						mShakeCounter = mPrevX.length;
						mDegrees = 270;
					}
				}
			}

			if (canvas != null) {
				Bitmap bitmap = mShakeCounter > 0 ? mFiring : mPlain;
				if (mDegrees != 0) {
					canvas.save();
					canvas.rotate(mDegrees, getWidth() / 2, getHeight() / 2);
				}
				canvas.drawBitmap(bitmap, mX, mY, null);
				if (mDegrees != 0) {
					canvas.restore();
				}

				canvas.drawText("x:" + mValues[0], 0, paint.getTextSize() * 1,
						paint);
				canvas.drawText("y:" + mValues[1], 0, paint.getTextSize() * 2,
						paint);
				canvas.drawText("z:" + mValues[2], 0, paint.getTextSize() * 3,
						paint);
			}
			if (mShakeCounter > 0) {
				mShakeCounter--;
				if (mShakeCounter == 0) {
					for (int i = 0; i < mPrevX.length; i++) {
						mPrevX[i] = 0;
					}
					mDegrees = 0;
				}
			} else {
				mPrevX[mCount++] = mLinearAcceleration[0];
				if (mCount == mPrevX.length) {
					mCount = 0;
				}
			}
		}
	}
}
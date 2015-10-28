package com.example.android.sensor2;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;

public class GyroscopeActivity extends AbstractSensorActivity {
	private Sensor mSensor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensor = mSensorManager.getDefaultSensor(getSensorType());
	}

	@Override
	AbstractSensorView getSensorView(Context context) {
		return new SensorView(context);
	}

	@Override
	int getSensorType() {
		return Sensor.TYPE_GYROSCOPE;
	}

	private class SensorView extends AbstractSensorView implements
			OnTouchListener {
		private float[][] mPoints;
		private final int mCount = 400;
		private int mRadius;
		private final int mScale = 90;
		private float mPx, mPy;

		private SensorView(Context context) {
			super(context);
			setOnTouchListener(this);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			super.surfaceCreated(holder);
			mRadius = Math.min(getWidth(), getHeight()) / 2;
			initPoints();
		}

		private void rotate(double theta, double phi) {
			float sinTheta = (float) Math.sin(theta);
			float cosTheta = (float) Math.cos(theta);
			float sinPhi = (float) Math.sin(phi);
			float cosPhi = (float) Math.cos(phi);
			int size = mPoints.length;
			for (int i = 0; i < size; i++) {
				float tempy = mPoints[i][0] * cosTheta + mPoints[i][2]
						* sinTheta;
				float tempz = -mPoints[i][0] * sinTheta + mPoints[i][2]
						* cosTheta;
				mPoints[i][0] = tempy;
				mPoints[i][2] = tempz;
				float tempx = mPoints[i][1] * cosPhi + mPoints[i][2] * sinPhi;
				tempz = -mPoints[i][1] * sinPhi + mPoints[i][2] * cosPhi;
				mPoints[i][1] = tempx;
				mPoints[i][2] = tempz;
			}
		}

		private void initPoints() {
			Random random = new Random();
			mPoints = new float[mCount][3];
			for (int i = 0; i < mPoints.length; i++) {
				mPoints[i][0] = random.nextFloat();
				mPoints[i][1] = random.nextFloat();
				mPoints[i][2] = random.nextFloat();

				while ((mPoints[i][0] * mRadius) * (mPoints[i][0] * mRadius)
						+ (mPoints[i][1] * mRadius) * (mPoints[i][1] * mRadius)
						+ (mPoints[i][2] * mRadius) * (mPoints[i][2] * mRadius) > mRadius
						* mRadius) {
					mPoints[i][0] -= 0.01;
					mPoints[i][1] -= 0.01;
					mPoints[i][2] -= 0.01;
				}
				while ((mPoints[i][0] * mRadius) * (mPoints[i][0] * mRadius)
						+ (mPoints[i][1] * mRadius) * (mPoints[i][1] * mRadius)
						+ (mPoints[i][2] * mRadius) * (mPoints[i][2] * mRadius) < mRadius
						* mRadius * 0.9) {
					mPoints[i][0] += 0.01;
					mPoints[i][1] += 0.01;
					mPoints[i][2] += 0.01;
				}

				if (random.nextInt() > 0) {
					mPoints[i][0] = -mPoints[i][0];
				}
				if (random.nextInt() > 0) {
					mPoints[i][1] = -mPoints[i][1];
				}
				if (random.nextInt() > 0) {
					mPoints[i][2] = -mPoints[i][2];
				}
			}
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			boolean result = false;
			if (action == MotionEvent.ACTION_DOWN) {
				mSensorManager.unregisterListener(GyroscopeActivity.this);
				mPx = event.getX();
				mPy = event.getY();
				result = true;
			} else if (action == MotionEvent.ACTION_UP) {
				mSensorManager.registerListener(GyroscopeActivity.this,
						mSensor, SensorManager.SENSOR_DELAY_FASTEST);
				result = true;
			} else if (action == MotionEvent.ACTION_MOVE) {
				float[] values = new float[] { (event.getY() - mPy) / 10,
						(event.getX() - mPx) / 10, 0 };
				onValueChanged(values);
				result = true;
			}
			return result;
		}

		@Override
		void repaint(Canvas canvas, Paint paint) {
			if (mPoints != null) {
				float dx = mValues[1];
				float dy = mValues[0];
				double theta = dy * 0.1;
				double phi = dx * 0.1;
				rotate(theta, phi);

				if (canvas != null) {
					canvas.drawText("x:" + mValues[0], 0,
							paint.getTextSize() * 1, paint);
					canvas.drawText("y:" + mValues[1], 0,
							paint.getTextSize() * 2, paint);
					canvas.drawText("z:" + mValues[2], 0,
							paint.getTextSize() * 3, paint);
					for (int i = 0; i < mPoints.length; i++) {
						float x = mPoints[i][1] * mScale * 4 + mRadius;
						float y = mPoints[i][0] * mScale * 4 + mRadius;
						float ratio = Math.abs((mPoints[i][2] - 1) / 3);

						int color = Color.argb((int) (ratio * 0xff), 0xff,
								0xff, 0xff);
						paint.setColor(color);
						canvas.drawRect(x, y, x + 5, y + 5, paint);
					}
				}
			}
		}
	}
}

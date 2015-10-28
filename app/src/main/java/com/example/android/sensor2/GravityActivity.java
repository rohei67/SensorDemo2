package com.example.android.sensor2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.view.SurfaceHolder;

public class GravityActivity extends AbstractSensorActivity {
	@Override
	AbstractSensorView getSensorView(Context context) {
		return new SensorView(context);
	}

	@Override
	int getSensorType() {
		return Sensor.TYPE_GRAVITY;
	}

	private class SensorView extends AbstractSensorView {
		private Bitmap mPlain, mFiring;
		private float mX, mY;
		private int mCount;
		private float[] mPrevX = new float[5];
		private float[] mPrevY = new float[5];

		public SensorView(Context context) {
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
			mX = (width - mPlain.getWidth()) / 2;
			mY = (height - mPlain.getHeight()) / 2;
			for (int i = 0; i < mPrevX.length; i++) {
				mPrevX[i] = mX;
				mPrevY[i] = mY - 1;
			}
		}

		@Override
		void repaint(Canvas canvas, Paint paint) {
			if (canvas != null) {
				float xg = mValues[0] * Math.abs(mValues[0]);
				float yg = mValues[1] * Math.abs(mValues[1]);
				mX -= xg;
				mY += yg;

				float dx = 0, dy = 0;
				for (int i = 0; i < mPrevX.length; i++) {
					dx += mPrevX[i];
					dy += mPrevY[i];
				}

				dx = dx / mPrevX.length - mX;
				dy = dy / mPrevY.length - mY;

				boolean hasFire = Math.sqrt(dx * dx + dy * dy) > 100;

				float radian = (float) Math.atan2(dy, dx);
				float degrees = (float) (radian * 180 / Math.PI) - 90;

				Bitmap bitmap = hasFire ? mFiring : mPlain;

				canvas.save();
				canvas.rotate(degrees, mX + bitmap.getWidth() / 2,
						mY + bitmap.getHeight() / 2);
				canvas.drawBitmap(bitmap, mX, mY, null);
				canvas.restore();

				canvas.drawText("x:" + mValues[0], 0, paint.getTextSize() * 1,
						paint);
				canvas.drawText("y:" + mValues[1], 0, paint.getTextSize() * 2,
						paint);
				canvas.drawText("z:" + mValues[2], 0, paint.getTextSize() * 3,
						paint);
				mPrevX[mCount] = mX;
				mPrevY[mCount] = mY;
				mCount++;
				if (mCount == mPrevX.length) {
					mCount = 0;
				}
			}
		}
	}
}
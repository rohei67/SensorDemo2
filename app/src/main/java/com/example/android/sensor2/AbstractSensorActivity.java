package com.example.android.sensor2;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

public abstract class AbstractSensorActivity extends Activity implements
		SensorEventListener {
	protected SensorManager mSensorManager;
	protected AbstractSensorView mView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mView = getSensorView(this);
		setContentView(mView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Sensor sensor = mSensorManager.getDefaultSensor(getSensorType());
		mSensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Toast.makeText(this, Utils.onAccuracyChangedHelper(sensor, accuracy),
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		mView.onValueChanged(event.values);
	}

	abstract AbstractSensorView getSensorView(Context context);

	abstract int getSensorType();
}

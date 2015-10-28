package com.example.android.sensor2;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CheckerActivity extends Activity implements SensorEventListener {
	@SuppressWarnings("unchecked")
	private static final Pair<String, Integer>[] DELAYS = new Pair[] {
			Pair.create("SENSOR_DELAY_FASTEST", SensorManager.SENSOR_DELAY_FASTEST),
			Pair.create("SENSOR_DELAY_GAME", SensorManager.SENSOR_DELAY_GAME),
			Pair.create("SENSOR_DELAY_UI", SensorManager.SENSOR_DELAY_UI),
			Pair.create("SENSOR_DELAY_NORMAL", SensorManager.SENSOR_DELAY_NORMAL),
	};

	private List<Sensor> mSensorList;
	private Spinner mSensors;
	private Spinner mDelays;
	private TextView mScore;
	private int mCount;
	private long[] mTimes = new long[1000];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checker);

		mSensorList = Utils.mSensorManager.getSensorList(Sensor.TYPE_ALL);

		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		for (Sensor sensor : mSensorList) {
			adapter1.add(sensor.getName());
		}

		mSensors = (Spinner) findViewById(R.id.sensors);
		mSensors.setAdapter(adapter1);

		mDelays = (Spinner) findViewById(R.id.delays);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		for (Pair<String, Integer> delay : DELAYS) {
			adapter2.add(delay.first);
		}
		mDelays.setAdapter(adapter2);

		mScore = (TextView) findViewById(R.id.score);
	}

	public void onClick(View view) {
		Sensor sensor = mSensorList.get(mSensors.getSelectedItemPosition());
		int delay = DELAYS[mDelays.getSelectedItemPosition()].second;

		mScore.setText("Checking...");
		mCount = 0;
		Utils.mSensorManager.registerListener(this, sensor, delay);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Toast.makeText(this, Utils.onAccuracyChangedHelper(sensor, accuracy),
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (mCount < mTimes.length) {
			mTimes[mCount++] = System.nanoTime();
		} else {
			Utils.mSensorManager.unregisterListener(this);

			long max = Long.MIN_VALUE;
			long min = Long.MAX_VALUE;
			long total = 0;

			for (int i = 1; i < mTimes.length; i++) {
				long time = mTimes[i] - mTimes[i - 1];
				max = Math.max(max, time);
				min = Math.min(min, time);
				total += time;
			}

			String score = "Average:" + (double) total / (mTimes.length - 1)
					/ 1000 / 1000 + " ms\n" + "Minimum:" + (double) min / 1000
					/ 1000 + " ms\n" + "Maximum:" + (double) max / 1000 / 1000
					+ " ms";

			mScore.setText(score);
		}
	}
}

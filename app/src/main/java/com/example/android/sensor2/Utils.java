package com.example.android.sensor2;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class Utils {
	static SensorManager mSensorManager;

	static String onAccuracyChangedHelper(Sensor sensor, int accuracy) {
		StringBuilder sb = new StringBuilder("onAccuracyChanged:");
		switch (accuracy) {
		case SensorManager.SENSOR_STATUS_UNRELIABLE:
			sb.append("SENSOR_STATUS_UNRELIABLE");
			break;
		case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
			sb.append("SENSOR_STATUS_ACCURACY_LOW");
			break;
		case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
			sb.append("SENSOR_STATUS_ACCURACY_MEDIUM");
			break;
		case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
			sb.append("SENSOR_STATUS_ACCURACY_HIGH");
			break;
		}
		return new String(sb);
	}

	/**
	 * 加速度から重力加速度を取り除く。
	 * 
	 * @param values
	 *            センサーの加速度
	 * @param gravity
	 *            前回の重力加速度が渡され、今回の重力加速度が格納される配列
	 * @param linearAccelatation
	 *            加速度から重力加速度が取り除かれた値が格納される配列
	 */
	static void extractGravity(float[] values, float[] gravity,
			float[] linearAccelatation) {
		// 加速度から重力の影響を取り除く。以下参照。
		// http://developer.android.com/intl/ja/reference/android/hardware/SensorEvent.html#values
		final float alpha = 0.8f;

		gravity[0] = alpha * gravity[0] + (1 - alpha) * values[0];
		gravity[1] = alpha * gravity[1] + (1 - alpha) * values[1];
		gravity[2] = alpha * gravity[2] + (1 - alpha) * values[2];

		linearAccelatation[0] = values[0] - gravity[0];
		linearAccelatation[1] = values[1] - gravity[1];
		linearAccelatation[2] = values[2] - gravity[2];
	}
}

class SensorItem {
	final String label;
	final Class<? extends Activity> clazz;
	String summary = "";
	boolean supported;

	SensorItem(int type, String label, Class<? extends Activity> clazz) {
		this.label = label;
		this.clazz = clazz;

		List<Sensor> sensors = Utils.mSensorManager.getSensorList(type);
		if (sensors.size() > 0) {
			Sensor sensor = sensors.get(0);
			setSupported(true);
			summary = sensor.getName() + " ver." + sensor.getVersion()
					+ " powered by " + sensor.getVendor();
		}
	}

	void setSupported(boolean supported) {
		this.supported = supported;
	}
}

class SensorActivityListAdapter extends ArrayAdapter<SensorItem> {
	private final LayoutInflater mmInflator;

	SensorActivityListAdapter(Context context) {
		super(context, R.layout.list);
		mmInflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		View view = null;
		if (contentView != null) {
			view = contentView;
		} else {
			view = mmInflator.inflate(R.layout.list, null);
		}
		SensorItem item = getItem(position);
		TextView text1 = (TextView) view.findViewById(R.id.text1);
		TextView text2 = (TextView) view.findViewById(R.id.text2);
		text1.setText(item.label);
		text2.setText(item.summary);

		text1.setEnabled(item.supported);
		text2.setEnabled(item.supported);
		view.setEnabled(item.supported);
		return view;
	}
}

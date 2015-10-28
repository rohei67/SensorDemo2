package com.example.android.sensor2;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectorActivity extends Activity implements OnItemClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		ArrayAdapter<SensorItem> adapter = new SensorActivityListAdapter(this);
		adapter.add(new SensorItem(Sensor.TYPE_ACCELEROMETER, "Accelerometer", AccelerometerActivity.class));
		adapter.add(new SensorItem(Sensor.TYPE_GRAVITY, "Gravity", GravityActivity.class));
		adapter.add(new SensorItem(Sensor.TYPE_GYROSCOPE, "Gyroscope", GyroscopeActivity.class));
		
		ListView listView = (ListView)findViewById(R.id.listView);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (view.isEnabled()) {
			Intent intent = new Intent(this, ((SensorItem)parent.getAdapter().getItem(position)).clazz);
			startActivity(intent);
		}
	}
}

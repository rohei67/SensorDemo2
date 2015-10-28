package com.example.android.sensor2;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener {
	@SuppressWarnings("unchecked")
	private static final Pair<String, Class<? extends Activity>>[] ACTIVITIES = new Pair[] {
			Pair.create("Checker", CheckerActivity.class),
			Pair.create("Selector", SelectorActivity.class), };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Utils.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		for (int i = 0; i < ACTIVITIES.length; i++) {
			adapter.add(ACTIVITIES[i].first);
		}

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (view.isEnabled()) {
			Intent intent = new Intent(this, ACTIVITIES[position].second);
			startActivity(intent);
		}
	}
}

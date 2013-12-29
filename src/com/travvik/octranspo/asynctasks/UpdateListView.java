package com.travvik.octranspo.asynctasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.travvik.octranspo.MapPane;
import com.travvik.octranspo.nouns.Trip;
import com.travvik.octranspo.nouns.adapter.TripArrayAdapter;

public class UpdateListView extends AsyncTask<Void, Void, Void> {

	private ListView lv;

	public void SetListView(ListView lv) {
		this.lv = lv;
	}

	public void updateListView(ListView listView) {

		// Simple hack. Get the adapter and then reinsert it.
		// This will cause ListView to execute the TripArrayAdapters so it can
		// update the screen.
		// ie: Countdown of seconds till next bus shows up, or reordering the
		// list.
		TripArrayAdapter itemAdapter = (TripArrayAdapter) listView.getAdapter();
		if (itemAdapter == null) {
			return;
		}
		listView.setAdapter(itemAdapter);

		TripArrayAdapter item = (TripArrayAdapter) listView.getAdapter();
		if (item.isEmpty()) {
			return;
		}

		// TODO: In the future, I want the user to be able to set a reminder
		// for this item's schedule.
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Trip item = (Trip) parent.getItemAtPosition(position);

				Intent intent = new Intent(parent.getContext(), MapPane.class);
				Bundle b = new Bundle();

				b.putCharSequence("BUS_NUMBER", (item.route.Number));
				b.putCharSequence("BUS_SPEED", (item.GpsSpeed));
				b.putCharSequence("BUS_LONG", (item.Longitude));
				b.putCharSequence("BUS_LAT", (item. Latitude));
				b.putCharSequence("BUS_LABEL", item.route.Label);
				b.putCharSequence("BUS_DIRECTION", item.route.Direction);
				b.putCharSequence("BUS_STOP_NUMBER", item.route.stop.stopNo);
				b.putCharSequence("BUS_TYPE", item.BusType);
				b.putCharSequence("BUS_DESTINATION", item.Destination);
				b.putCharSequence("BUS_ADJ_SCHED_TIME", item.AdjustedScheduleTime);
				b.putCharSequence("BUS_STOP_LABEL", item.route.stop.stopLabel);
				b.putCharSequence("BUS_ARRIVAL", "Next bus in "
						+ item.AdjustedScheduleTime + " minutes.");

				intent.putExtras(b);

				view.getContext().startActivity(intent);
			}
		});
	}

	@Override
	protected Void doInBackground(Void... params) {
		//Log.d("Update lv async", "Executing");
		return null;
	}

	@Override
	protected void onPostExecute(Void paran) {
		//Log.d("Update lv async", "Drawing");
		updateListView(lv);
		return;
	}
}

package com.travvik.octranspo.asynctasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.travvik.octranspo.R;
import com.travvik.octranspo.nouns.Trip;
import com.travvik.octranspo.nouns.adapter.TripArrayAdapter;
import com.travvik.octranspo.verbs.OcTranspoDeserializer;

public class AsyncTaskGetJsonSchedule extends
		AsyncTask<String, Integer, String> {

	private View rootView;
	private ListView lv;
	private Button button;
	private Trip[] trips;
	private ProgressBar progressBar;

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	private AtomicBoolean canRefreshListView = new AtomicBoolean(false);

	public AtomicBoolean getCanRefreshListView() {
		return canRefreshListView;
	}

	public void setCanRefreshListView(AtomicBoolean canRefreshListView) {
		this.canRefreshListView = canRefreshListView;
	}

	public void SetContextView(Context context, View rootView) {
		this.rootView = rootView;
	}

	public void SetListView(ListView lv) {
		this.lv = lv;
	}

	public void SetButton(Button button) {
		this.button = button;
	}

	protected void onProgressUpdate(Integer... progress) {
		progressBar.setProgress(progress[0]);
	}

	@Override
	protected String doInBackground(String... urls) {
		String response = "";
		for (String url : urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			publishProgress(5);

			try {
				HttpResponse execute = client.execute(httpGet);
				InputStream content = execute.getEntity().getContent();

				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(content));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					response += s;
				}
				publishProgress(25);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	@Override
	protected void onPostExecute(String jsonTimeTable) {
		// Re-enable the button so user can request a new time table.
		button.setEnabled(true);

		// Stop an UI thread from reading the ListView object
		canRefreshListView.set(false);

		Log.d("JBUG", "Got from URL: " + jsonTimeTable);
		if (jsonTimeTable.compareTo("-1") == 0) {
			Log.e("Download task",
					"Server parser could not understand request.");
			onProgressUpdate(0);
			Toast.makeText(rootView.getContext(),
					"Sorry, No schedule could be found.", Toast.LENGTH_LONG)
					.show();
			return;
		}

		// Get the HashSet of trips.
		HashSet<Trip> x;

		x = OcTranspoDeserializer.deserialize(jsonTimeTable);
		onProgressUpdate(50);
		/*
		 * Check for errors. If errors are found, display a toast and remove
		 * items from ListView and tell the UI threads that refreshing the
		 * listview is unnecessary.
		 */
		if (x == null) {
			Log.d("JBUG", "fail to query");
			Toast.makeText(rootView.getContext(),
					"Sorry, No schedule could be found.", Toast.LENGTH_LONG)
					.show();
			lv.setAdapter(null);
			canRefreshListView.set(false);
			return;
		}

		trips = x.toArray(new Trip[x.size()]);

		trips = Trip.bubbleSortAsc(trips);

		onProgressUpdate(75);
		// Set listview adapter.
		TripArrayAdapter itemAdapter = new TripArrayAdapter(lv.getContext(),
				R.layout.bus_item, trips);
		lv.setAdapter(itemAdapter);

		canRefreshListView.set(true);
		onProgressUpdate(0);
	}
	
	

}

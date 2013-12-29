package com.travvik.octranspo;

import java.util.concurrent.atomic.AtomicBoolean;

//import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.travvik.octranspo.asynctasks.AsyncTaskGetJsonSchedule;
import com.travvik.octranspo.asynctasks.UpdateListView;

public class MainActivity extends Activity {

	private Thread thread;
	private AtomicBoolean pauseThread = new AtomicBoolean(true);
	private AtomicBoolean canRefreshListView = new AtomicBoolean(false);
	public final static String EXTRA_MESSAGE = "com.travvil.octranspo.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_postlist);

		final Button getNewData = (Button) findViewById(R.id.loadNewData);
		final ListView listView = (ListView) this
				.findViewById(R.id.postListView);

		final EditText busNumber = (EditText) this.findViewById(R.id.busNumber);
		final EditText busStop = (EditText) this.findViewById(R.id.StopNumber);
		final ProgressBar pg = (ProgressBar) this
				.findViewById(R.id.progressBarNewData);

		getNewData.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				AsyncTaskGetJsonSchedule task = new AsyncTaskGetJsonSchedule();
				task.SetContextView(v.getContext(), getCurrentFocus());
				task.SetListView(listView);
				task.SetButton(getNewData);
				task.setProgressBar(pg);
				task.setCanRefreshListView(canRefreshListView);
				final String URI = "http://ottawa.travvik.com/wip/scratch/json.php?routeno={0}&stopno={1}";
				String curURI = URI.replace("{0}", busNumber.getText());
				curURI = curURI.replace("{1}", busStop.getText());

				getNewData.setEnabled(false);

				task.execute(curURI);
			}
		});

		thread = new Thread() {

			@Override
			public void run() {
				Log.d("Main activity", "Started listview updater thread");
				while (true) {
					if (pauseThread.get() == false) {
//						Log.d("UpdateLV",
//								"Checking if I can update the listview: "
//										+ canRefreshListView.get());
						if (canRefreshListView.get()) {
							UpdateListView update = new UpdateListView();
							update.SetListView(listView);
							update.execute();
							Log.d("UpdateLV", "Updating Lv from thread.");
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							Log.d("UpdateLV", "Thread could not sleep.");
						}
					}
				}
			}
		};
		thread.start();

		pauseThread.set(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause(); // Always call the superclass method first

		pauseThread.set(true);
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first

		pauseThread.set(false);
	}

}

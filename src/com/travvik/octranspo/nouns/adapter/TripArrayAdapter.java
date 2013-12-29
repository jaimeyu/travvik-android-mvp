package com.travvik.octranspo.nouns.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.travvik.octranspo.R;
import com.travvik.octranspo.nouns.Trip;

public class TripArrayAdapter extends ArrayAdapter<Trip> {
	private Activity myContext;
	private Trip[] trips;

	public TripArrayAdapter(Context context, int textViewResourceId,
			Trip[] objects) {
		super(context, textViewResourceId, objects);
		myContext = (Activity) context;
		this.trips = objects;
	}

	static class ViewHolder {
		TextView routeDestination;
		TextView countdown;
		ImageView busImage;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		Long mins;
		Long seconds;
		/*
		 * If converview not set, initialize the layout.
		 */
		if (convertView == null) {
			LayoutInflater inflater = myContext.getLayoutInflater();

			// Use the postitem layout as a base
			convertView = inflater.inflate(R.layout.bus_item, null);

			// Find all the layout's ID and store them in viewHolder
			// for quick reference.
			viewHolder = new ViewHolder();
			viewHolder.countdown = (TextView) convertView
					.findViewById(R.id.countdown);
			viewHolder.routeDestination = (TextView) convertView
					.findViewById(R.id.routeDestination);
			viewHolder.busImage = (ImageView) convertView.findViewById(R.id.imgThumb);
			
			/*
			 * Basically you can store any kind of object as tag (and cast it
			 * back when calling getTag). This can be a simple ID or some
			 * complex data. It's some information which you associate with this
			 * view.
			 * 
			 * In the case of lists and the view holder pattern it's a simple
			 * object which contains references to views of the tagged view
			 * (group). So you don't have to call findViewById every time when
			 * you're updating the content of the view. It's just an performance
			 * optimization.
			 * 
			 * Can we store data of list item in the view tag? No. Because of
			 * view recycling you have (e.g.) 10 views which are reused for 1000
			 * list items. Storing data in the tag makes no sense here. It's
			 * better to use an custom data object to store the list item state
			 * (probably the same array which contains the displayed data) or
			 * you persist it right away on list item change.
			 */
			convertView.setTag(viewHolder);
		} else {
			// convertView is already setup.
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String busType = trips[position].BusType;
		
		switch (busType.charAt(0)) {
		case '4':
			viewHolder.busImage.setImageResource(R.drawable.obus_normal);
			break;
		case '6':
			viewHolder.busImage.setImageResource(R.drawable.obus_articulated);
			break;
		case 'D':
			viewHolder.busImage.setImageResource(R.drawable.obus_double_decker);
			break;
		default:
			viewHolder.busImage.setImageResource(R.drawable.obus);
			break;
		}
		
		String expectedArrivalMinutes = trips[position].AdjustedScheduleTime;
		long expectedTime = Long
				.parseLong(expectedArrivalMinutes) * 60 * 1000;
		long diff = trips[position].TimeReceived + expectedTime
				- System.currentTimeMillis();
		if ( diff < 0)
		{
			seconds = (long) 0;
			mins = (long) 0;
			// TODO? Perhaps pop this trip out of the array. 
		}
		else {
			diff = diff / 1000;
			mins = diff / 60;
			seconds = diff % 59;
		}
		
		
		
		String countdown = mins  + ":" + String.format("%02d", seconds);
		viewHolder.countdown.setText(countdown);
		viewHolder.routeDestination.setText(trips[position].route.Direction
				+ " to " + trips[position].Destination + " from "
				+ trips[position].route.stop.stopLabel);
		viewHolder.routeDestination.setSelected(true);


		return convertView;
	}
}
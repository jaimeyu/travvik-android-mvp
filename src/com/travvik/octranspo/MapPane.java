package com.travvik.octranspo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapPane extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_where_is_my_bus);
		
		
		final TextView whereBusNumber = (TextView) this.findViewById(R.id.whereBusNumber);
		final TextView whereBusSpeed = (TextView) this.findViewById(R.id.whereBusSpeed);
		final TextView whereCoord = (TextView) this.findViewById(R.id.whereCoord);
		final TextView whereBusLabel = (TextView) this.findViewById(R.id.whereBusLabel);
		final TextView whereBusArrival = (TextView) this.findViewById(R.id.whereBusArrival);

		Bundle arguments = getIntent().getExtras();
		CharSequence speed = arguments.getCharSequence("BUS_SPEED");
		CharSequence strlatitude = arguments.getCharSequence("BUS_LAT");
		CharSequence strlongitude = arguments.getCharSequence("BUS_LONG");
		CharSequence number = arguments.getCharSequence("BUS_NUMBER");
		CharSequence label = arguments.getCharSequence("BUS_LABEL");
		CharSequence arrival = arguments.getCharSequence("BUS_ARRIVAL");
		
		whereBusNumber.setText(number);
		whereBusArrival.setText("Arriving in: " + arrival);
		whereBusSpeed.setText(speed + " km/h");
		whereCoord.setText("LAT/LONG: " + strlatitude + ", " + strlongitude);
		whereBusLabel.setText("Destination: " + label);

		// Get a handle to the Map Fragment
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.mapview)).getMap();
		Double latitude;
		Double longitude;
		try {
			latitude = Double.parseDouble(strlatitude.toString());
			longitude = Double.parseDouble(strlongitude.toString());
		} catch (NumberFormatException e) {
			latitude = 45.417 ;
			longitude = -75.7;
		}

		LatLng curBusLocation = new LatLng(latitude, longitude);

		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(curBusLocation, 18));

		map.addMarker(new MarkerOptions()
				.title(number + " to " + label)
				.snippet(
						"Speed: " + speed + "km/h. " + arrival.toString()
						).draggable(false).visible(true)
				.position(curBusLocation));
	}
}

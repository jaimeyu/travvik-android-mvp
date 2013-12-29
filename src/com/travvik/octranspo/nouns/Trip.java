package com.travvik.octranspo.nouns;

public class Trip {
	public int x;

	public String Destination;
	public String StartTime;
	public String AdjustedScheduleTime;
	public String AdjustmentAge;
	public String LastTripOfSchedule;
	public String BusType;
	public String Latitude;
	public String Longitude;
	public String GpsSpeed;
	public long TimeReceived;

	public Route route;

	/**
	 * 4 or 40 = 40-foot buses 6 or 60 = 60-foot buses 4 and 6 = trips can be
	 * done by 60 or 40-foot buses DD = Double-Decker buses E, L, A, EA means
	 * low-floor Easy Access B = Bike Rack DEH = Diesel Electric Hybrid IN =
	 * INVIRO (bus type) ON = ORION (bus type)
	 */
	public static final String BUS_TYPE_SHORT = "4";
	public static final String BUS_TYPE_LONG = "6";
	public static final String BUS_TYPE_DOUBLE_DECKER = "DD";

	public static Trip[] bubbleSortAsc(Trip[] trips) {
		// Lets organize the array by closest arriving bus.
		// bubble sort?
		for (int i = 0; i < trips.length; i++) {
			for (int y = 0; y < trips.length - 1; y++) {
				if (Integer.parseInt(trips[y + 1].AdjustedScheduleTime) < Integer
						.parseInt(trips[y].AdjustedScheduleTime)) {
					// SWAP objects now.
					Trip curTrip = trips[y + 1];
					trips[y + 1] = trips[y];
					trips[y] = curTrip;
				}
			}
		}
		return trips;
	}

	public Trip() {
		this.route = new Route();
	}

}

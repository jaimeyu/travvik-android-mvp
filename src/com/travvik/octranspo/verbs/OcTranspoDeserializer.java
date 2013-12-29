package com.travvik.octranspo.verbs;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.google.common.collect.ImmutableMap;
import com.travvik.octranspo.nouns.Route;
import com.travvik.octranspo.nouns.Stop;
import com.travvik.octranspo.nouns.Trip;

public class OcTranspoDeserializer {

	static public final String DEBUG_95 = "{\"GetNextTripsForStopResponse\":{\"GetNextTripsForStopResult\":{\"StopNo\":\"3017\",\"StopLabel\":\"BASELINE\",\"Error\":{},\"Route\":{\"RouteDirection\":[{\"RouteNo\":\"95\",\"RouteLabel\":\"Orl\\u00e9ans\",\"Direction\":\"Eastbound\",\"Error\":{},\"RequestProcessingTime\":\"20130911222740\",\"Trips\":{\"Trip\":[{\"TripDestination\":\"Trim\",\"TripStartTime\":\"22:16\",\"AdjustedScheduleTime\":\"10\",\"AdjustmentAge\":\"0.59\",\"LastTripOfSchedule\":\"false\",\"BusType\":\"6EB - 60\",\"Latitude\":\"45.299323\",\"Longitude\":\"-75.735586\",\"GPSSpeed\":\"24.6\"},{\"TripDestination\":\"Place d\'Orl\\u00e9ans\",\"TripStartTime\":\"22:40\",\"AdjustedScheduleTime\":\"12\",\"AdjustmentAge\":\"-1\",\"LastTripOfSchedule\":\"false\",\"BusType\":\"6EB - 60\",\"Latitude\":{},\"Longitude\":{},\"GPSSpeed\":{}},{\"TripDestination\":\"Trim\",\"TripStartTime\":\"22:32\",\"AdjustedScheduleTime\":\"20\",\"AdjustmentAge\":\"0.62\",\"LastTripOfSchedule\":\"false\",\"BusType\":\"6EB - 60\",\"Latitude\":\"45.267121\",\"Longitude\":\"-75.741625\",\"GPSSpeed\":\"0.5\"}]}},{\"RouteNo\":\"95\",\"RouteLabel\":\"Barrhaven Centre\",\"Direction\":\"Westbound\",\"Error\":{},\"RequestProcessingTime\":\"20130911222740\",\"Trips\":{\"Trip\":[{\"TripDestination\":\"Barrhaven Centre\",\"TripStartTime\":\"21:47\",\"AdjustedScheduleTime\":\"12\",\"AdjustmentAge\":\"0.54\",\"LastTripOfSchedule\":\"false\",\"BusType\":\"6EB - 60\",\"Latitude\":\"45.408878\",\"Longitude\":\"-75.722956\",\"GPSSpeed\":\"50.0\"},{\"TripDestination\":\"Barrhaven Centre\",\"TripStartTime\":\"22:02\",\"AdjustedScheduleTime\":\"28\",\"AdjustmentAge\":\"1.20\",\"LastTripOfSchedule\":\"false\",\"BusType\":\"6EB - 60\",\"Latitude\":\"45.411880\",\"Longitude\":\"-75.663826\",\"GPSSpeed\":\"24.6\"},{\"TripDestination\":\"Barrhaven Centre\",\"TripStartTime\":\"22:17\",\"AdjustedScheduleTime\":\"45\",\"AdjustmentAge\":\"1.50\",\"LastTripOfSchedule\":\"false\",\"BusType\":\"6EB - 60\",\"Latitude\":\"45.479330\",\"Longitude\":\"-75.517675\",\"GPSSpeed\":\"14.8\"}]}}]}}}}";
	static public final String DEBUG_ERROR_10 = "{\"GetNextTripsForStopResponse\":{\"GetNextTripsForStopResult\":{\"StopNo\":{},\"StopLabel\":{},\"Error\":\"10\",\"Route\":{}}}}";
	static public final String DEBUG_ERROR_5 = "{\"GetNextTripsForStopResponse\":{\"GetNextTripsForStopResult\":{\"StopNo\":{},\"StopLabel\":{},\"Error\":\"5\",\"Route\":{}}}}";
	static public final String DEBUG_SINGLE_ROUTE_14 = "{\"GetNextTripsForStopResponse\":{\"GetNextTripsForStopResult\":{\"StopNo\":\"7674\",\"StopLabel\":\"ELGIN COOPER\",\"Error\":{},\"Route\":{\"RouteDirection\":{\"RouteNo\":\"14\",\"RouteLabel\":\"St Laurent\",\"Direction\":\"Eastbound\",\"Error\":{},\"RequestProcessingTime\":\"20130913220357\",\"Trips\":{\"Trip\":[{\"TripDestination\":\"St. Laurent\",\"TripStartTime\":\"21:45\",\"AdjustedScheduleTime\":\"8\",\"AdjustmentAge\":\"1.21\",\"LastTripOfSchedule\":\"false\",\"BusType\":\"4L - DEH\",\"Latitude\":\"45.407402\",\"Longitude\":\"-75.704734\",\"GPSSpeed\":\"44.5\"},{\"TripDestination\":\"St. Laurent\",\"TripStartTime\":\"22:15\",\"AdjustedScheduleTime\":\"36\",\"AdjustmentAge\":\"-1\",\"LastTripOfSchedule\":\"false\",\"BusType\":\"4L - IN\",\"Latitude\":{},\"Longitude\":{},\"GPSSpeed\":{}},{\"TripDestination\":\"St. Laurent\",\"TripStartTime\":\"22:45\",\"AdjustedScheduleTime\":\"64\",\"AdjustmentAge\":\"-1\",\"LastTripOfSchedule\":\"false\",\"BusType\":\"4LB - IN\",\"Latitude\":{},\"Longitude\":{},\"GPSSpeed\":{}}]}}}}}}";

	static final Map<String, String> ERROR_MAP = ImmutableMap
			.<String, String> builder().put("1", "Invalid API key")
			.put("2", "Unable to query data source")
			.put("10", "Invalid stop number").put("11", "Invalid route number")
			.put("12", "Stop does not service route").build();

	private static String getErrorDetails(String error) {
		/*
		 * Error Note 1 Invalid API key 2 Unable to query data source 10 Invalid
		 * stop number 11 Invalid route number 12 Stop does not service route
		 */

		String message = ERROR_MAP.get(error);

		return message;
	}

	public static HashSet<Trip> deserialize(String json) {
		try {
			JSONObject object = (JSONObject) new JSONTokener(json).nextValue();

			// First step pull out GetNextTripsForStopResponse which is the
			// master container.
			JSONObject StopResponse = object
					.getJSONObject("GetNextTripsForStopResponse");

			// Now get the smaller container which actually holds stuff.
			// Contains stop number, label, ROUTE container, and top level error
			// code
			JSONObject jsonStop = StopResponse
					.getJSONObject("GetNextTripsForStopResult");

			// Throw the Route container down
			HashSet<Trip> trips = getTripSet(jsonStop);
			
			/** 
			 * Check if the server detected an error with the request.
			 */
			if ( hasDataError(jsonStop)) {
				Trip errorTrip = new Trip();
				errorTrip.route.stop = getStopDetails(jsonStop);
				trips.add(errorTrip);
				return trips;
			}
			
		
			return trips;
			// return getStopDetails(StopResult);
		} catch (JSONException e) {
			Log.e(e.getStackTrace()[0].getMethodName(),
					"JSON Encountered a bug.: " + e.getLocalizedMessage());
		} catch (IOException e) {
			Log.e(e.getStackTrace()[0].getMethodName(),
					"Server detected an error with request");
		} 
		return null;

	}
	
	private static Boolean hasDataError(JSONObject jsonStop) {
		String error = null;
		try {
			error = jsonStop.getString("Error");
			String message = getErrorDetails(error);
			if ( message != null) {
				return true;
			}
			return false;
		} catch (JSONException e) {
			Log.d("checkIfError", "Could not parse stop details");

		}
		return true;
	}


	private static Stop getStopDetails(JSONObject jsonStop)
			throws IOException {
		Stop curStop = new Stop();

		try {
			curStop.stopLabel = jsonStop.getString("StopLabel");
			Log.d("getStopDetails", "stopLabel: " + curStop.stopLabel);
			curStop.stopNo = jsonStop.getString("StopNo");
			Log.d("getStopDetails", "StopNo: " + curStop.stopNo);
			curStop.error = jsonStop.getString("Error");
			Log.d("getStopDetails", "error: " + curStop.error);

		} catch (JSONException e) {
			String message = getErrorDetails(curStop.error);
			if (message == null) {
				message = "Unknown error when parsing.";
			}
			throw new IOException(message);
		}
		return curStop;
	}

	private static Route getRoute(JSONObject object) throws JSONException {
		Route curRoute = new Route();
		Log.d("Jbug", "Trying to query route details");

		curRoute.Number = object.getString("RouteNo");
		Log.d("Jbug", "RouteNo:: " + curRoute.Number);

		curRoute.Label = object.getString("RouteLabel");
		Log.d("Jbug", "RouteNo:: " + curRoute.Label);

		curRoute.Direction = object.getString("Direction");
		Log.d("Jbug", "Direction:: " + curRoute.Direction);

		curRoute.Error = object.getString("Error");
		Log.d("Jbug", "triperror:: " + curRoute.Error);

		curRoute.RequestProcessingTime = object
				.getString("RequestProcessingTime");
		Log.d("Jbug", "RequestProcessingTime:: "
				+ curRoute.RequestProcessingTime);
		return curRoute;
	}

	/**
	 * 
	 * @param jsonRouteObject
	 *            May have multiple routes
	 * @return
	 * @throws JSONException
	 */
	private static HashSet<Trip> getTripSet(JSONObject jsonStop)
			throws JSONException {
		HashSet<Trip> setOfTrip = new HashSet<Trip>();
		// Route container contains either an array of routes or a single route
		JSONObject jsonRouteObject = jsonStop.getJSONObject("Route");
		JSONArray jsonArray = null;

		// # of routes = # of directions
		int curDirection = 0;
		JSONObject curRouteObject;
		/**
		 * Try to pull the route direction as an array. If its not an jsonarray
		 * pull it out as an json object
		 * 
		 * Looks like a weird "feature" of the json library. I would rather have
		 * all values come back as an array and do length check. Much more
		 * predictable and I wouldn't have to use this crazy exception logic.
		 * 
		 * Note it won't return a null to signify it couldn't find an array,
		 * throws an exception instead so be careful of your states.
		 */
		Log.d("getTripSet",
				"Determining whether multiple or single route object.");
		try {
			jsonArray = jsonRouteObject.getJSONArray("RouteDirection");
		} catch (JSONException e1) {
			Log.d("getTripSet", "Not an array of routes");
			jsonArray = null;
		}

		if (jsonArray != null) {
			Log.d("getTripSet", "Found array of routes. Parsing.");
			while (curDirection < jsonArray.length()) {
				Log.d("getTripSet", "Current route Direction: " + curDirection);
				curRouteObject = jsonArray.getJSONObject(curDirection);
				setOfTrip.addAll(getTripsFromRoute(curRouteObject, jsonStop));
				curDirection++;
			}
		} else {
			Log.d("getTripSet", "Single route found, parsing.");
			curRouteObject = jsonRouteObject.getJSONObject("RouteDirection");
			setOfTrip.addAll(getTripsFromRoute(curRouteObject, jsonStop));
		}
		return setOfTrip;
	}

	private static HashSet<Trip> getTripsFromRoute(JSONObject jsonRouteObject,
			JSONObject jsonStop) {
		JSONObject direction = jsonRouteObject;
		HashSet<Trip> setOfTrip = new HashSet<Trip>();
		Route curRoute = new Route();
		Stop stop = new Stop();
		try {
			stop = getStopDetails(jsonStop);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			Log.d("getTripsFromRoute", "Could not extract stop details");
		}
		try {
			curRoute = getRoute(jsonRouteObject);
			curRoute.stop = stop;
		} catch (JSONException e) {
			Log.d("getTripsFromRoute",
					"Couldn't find any associated routes for this trip.");
		}

		/* Trip data */
		try {

			setOfTrip = getTrips(direction.getJSONObject("Trips"), curRoute);
		} catch (JSONException e1) {
			Log.d("getTripsFromRoute", "Couldn't find any trips");
		}
		return setOfTrip;
	}

	private static HashSet<Trip> getTrips(JSONObject jsonObject, Route route)
			throws JSONException {

		HashSet<Trip> trips = new HashSet<Trip>();

		JSONArray triplist = jsonObject.getJSONArray("Trip");

		int curTripIndex = 0;
		while (curTripIndex < triplist.length()) {
			JSONObject trip = triplist.getJSONObject(curTripIndex);
			Trip curTrip = new Trip();

			Log.d("getTrips", "*****TRIP NUMBER:: " + curTripIndex);

			curTrip.Destination = trip.getString("TripDestination");
			Log.d("getTrips", "TripDestination:: " + curTrip.Destination);

			curTrip.StartTime = trip.getString("TripStartTime");
			Log.d("getTrips", "TripStartTime:: " + curTrip.StartTime);

			curTrip.AdjustedScheduleTime = trip
					.getString("AdjustedScheduleTime");
			Log.d("getTrips", "AdjustedScheduleTime:: "
					+ curTrip.AdjustedScheduleTime);

			curTrip.AdjustmentAge = trip.getString("AdjustmentAge");
			Log.d("getTrips", "AdjustmentAge:: " + curTrip.AdjustmentAge);

			curTrip.LastTripOfSchedule = trip.getString("LastTripOfSchedule");
			Log.d("getTrips", "LastTripOfSchedule:: "
					+ curTrip.LastTripOfSchedule);

			curTrip.BusType = trip.getString("BusType");
			Log.d("getTrips", "BusType:: " + curTrip.BusType);

			curTrip.Latitude = trip.getString("Latitude");
			Log.d("getTrips", "Latitude:: " + curTrip.Latitude);

			curTrip.Longitude = trip.getString("Longitude");
			Log.d("getTrips", "Longitude:: " + curTrip.Longitude);

			curTrip.GpsSpeed = trip.getString("GPSSpeed");
			Log.d("getTrips", "GPSSpeed:: " + curTrip.GpsSpeed);

			curTrip.TimeReceived = System.currentTimeMillis();
			curTrip.route = route;
			trips.add(curTrip);

			curTripIndex++;
		}
		return trips;
	}

}

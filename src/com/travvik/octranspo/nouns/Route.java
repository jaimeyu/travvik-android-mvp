package com.travvik.octranspo.nouns;


// Note that it is called RouteDirection in the XML but
// that is a poor descriptive noun.
public class Route {

	// Contains how many trips we got from server.
	
	public String Number;
	public String Label;
	public String Direction;
	public String Error;
	public String RequestProcessingTime;
	public Stop stop;
	//public HashSet<Trip> Trips;
	
	public Route() {
		this.stop = new Stop();
	}
	
	
}

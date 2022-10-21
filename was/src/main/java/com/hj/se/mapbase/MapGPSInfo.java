package com.hj.se.mapbase;
public class MapGPSInfo {
	private double _lon, _lat;
	private int _zoomLevel;
	
	public void setZoomLevel(int zoomLevel) { _zoomLevel = zoomLevel;}
	public int getZoomLevel() { return _zoomLevel ;}
	public void setLon(double lon) { _lon = lon;}
	public double getLon() { return _lon;}
	public void setLat(double lat) { _lat = lat;}
	public double getLat() { return _lat;}
	
	public MapGPSInfo(double lon, double lat, int zoomLevel) {
		_lon = lon;
		_lat = lat;
		_zoomLevel = zoomLevel;
	}
    
}


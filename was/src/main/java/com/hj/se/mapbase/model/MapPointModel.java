package com.hj.se.mapbase.model;

import java.awt.*;

/**
 * map 관련 기본 모델.
 * @author snowstock
 *
 */
public class MapPointModel {
	
	/**
	 * pixel 관련 
	 * @author snowstock
	 *
	 */
	public static class PixelPoint {
		public int _pixelX;
		public int _pixelY;
		
		public int getPixelX() {return _pixelX;}
		public int getPixelY() {return _pixelY;}
		public void setPixelX(int pixel) {_pixelX = pixel;}
		public void setPixelY(int pixel) {_pixelY = pixel;}
	}

	/** tile 관련
	 * 
	 * @author snowstock
	 *
	 */
	public static class TilePoint {
		public int _tileX;
		public int _tileY;
		
		/**
		 * constructor
		 */
		public TilePoint()
		{
			_tileX = 0;
			_tileY = 0;
		}
		
		public int getTileX() {return _tileX;}
		public int getTileY() {return _tileY;}
		public void setTileX(int tile) {_tileX = tile;}
		public void setTileY(int tile) {_tileY = tile;}
	}
	
	/**
	 * lonlat 관련
	 * @author snowstock
	 *
	 */
	public static class LonLatPoint {
		public double _lon;
		public double _lat;
		
		public double getLon() {return _lon;}
		public double getLat() {return _lat;}
		public void setLon(double lon) {_lon = lon;}
		public void setLat(double lat) {_lat = lat;}
	}
	
	
	/** zoomLevel관련
	 * 
	 * @author snowstock
	 *
	 */
	public static class LevelOfDetail {
		public int _levelOfDetail;
		
		/**
		 * get zoom level
		 * @return
		 */
		public int getZoomLevel() {return _levelOfDetail;}
		
		/**
		 * set zoomlevel
		 * @param level
		 */
		public void setZoomLevel(int level) {_levelOfDetail = level;}
	}
	
	/**
	 * lonlat 데이터 표시용.
	 * @author snowstock
	 *
	 */
	public static class LonLatData{
		public double _lon;
		public double _lat;
		public double _value;
		public Color _color;
		
		/**
		 * 초기화
		 * @param lon
		 * @param lat
		 * @param value
		 * @param color
		 */
		public LonLatData(double lon, double lat, double value, Color color)
		{
			_lon = lon;
			_lat = lat;
			_value = value;
			_color = color;
		}
		
		public double getLon(){return _lon;}
		public double getLat(){return _lat;}	
		public double getValue(){return _value;}
		public Color getColor(){return _color;}
	
	}
}
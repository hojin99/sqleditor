package com.hj.se.mapbase.utils;

import com.hj.se.mapbase.model.MapPointModel;
import com.hj.se.mapbase.model.TileLonLatModel;
import com.hj.se.mapbase.model.TilePositionModel;

import java.awt.*;

/***
 * tile system
 * @author snowstock
 *
 */
public class TileSystem {
	
    private final static double EARTH_RADIUS = 6378137;
    private final static double MIN_LATITUDE = -85.05112878;
    private final static double MAX_LATITUDE = 85.05112878;
    private final static double MIN_LONGITUDE = -180;
    private final static double MAX_LONGITUDE = 180;
    
    /**
     * clips a number to the specified minimum and maximum values. 
     * @param n : The number to clip.
     * @param minValue : Minimum allowable value.
     * @param maxValue : Maximum allowable value.
     * @return The clipped value.
     */
    private static double clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }


    /**
     * Determines the map width and height (in pixels) at a specified level of detail.
     * @param levelOfDetail
     * @return The map width and height in pixels.
     */
    public static int mapSize(int levelOfDetail)
    {
    	int vInt = Integer.parseUnsignedInt("256");
        return vInt << levelOfDetail;
    }


    /**
     * Determines the ground resolution (in meters per pixel) at a specified latitude and level of detail.
     * @param latitude
     * @param levelOfDetail
     * @return The ground resolution, in meters per pixel.
     */
    public static double groundResolution(double latitude, int levelOfDetail)
    {
        double lat = clip(latitude, MIN_LATITUDE, MAX_LATITUDE);
        return Math.cos(lat * Math.PI / 180) * 2 * Math.PI * EARTH_RADIUS / mapSize(levelOfDetail);
    }


    /**
     * Determines the map scale at a specified latitude, level of detail, and screen resolution.
     * @param latitude Latitude (in degrees) at which to measure the map scale.
     * @param levelOfDetail
     * @param screenDpi Resolution of the screen, in dots per inch.
     * @return The map scale, expressed as the denominator N of the ratio 1 : N.
     */
    public static double mapScale(double latitude, int levelOfDetail, int screenDpi)
    {
        return groundResolution(latitude, levelOfDetail) * screenDpi / 0.0254;
    }

    /**
     * 
     * @param x
     * @param y
     * @param zoom
     * @return
     */
    public static String googleXYZToQuadKey(int x, int y, int zoom)
    {
        String quad = "";
        for (int i = zoom; i > 0; i--)
        {
            long mask = 1 << (i - 1);
            long cell = 0;
            if ((x & mask) != 0)
                cell++;
            if ((y & mask) != 0)
                cell += 2;
            quad += cell;
        }
        return quad;
    }
    

    /**
     * Converts a point from latitude/longitude WGS-84 coordinates (in degrees) into pixel XY coordinates at a specified level of detail
     * @param latitude
     * @param longitude
     * @param levelOfDetail 1 to 23
     * @param pixel output parameter receiving the x/y coordinates in pixels
     */
    public static void latLongToPixelXY(double latitude, double longitude, int levelOfDetail, MapPointModel.PixelPoint pixel )
    {
        double lat = clip(latitude, MIN_LATITUDE, MAX_LATITUDE);
        double lon = clip(longitude, MIN_LONGITUDE, MAX_LONGITUDE);

        double x = (lon + 180) / 360;
        double sinLatitude = Math.sin(lat * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

        int mapSize = mapSize(levelOfDetail);
        pixel.setPixelX((int)clip(x * mapSize + 0.5, 0, mapSize - 1));
        pixel.setPixelY((int)clip(y * mapSize + 0.5, 0, mapSize - 1));
    }

    /**
     * Converts a point from latitude/longitude WGS-84 coordinates (in degrees) 
     * into pixel XY coordinates at a specified level of detail.
     * 
     * @param latitude : Latitude of the point, in degrees.
     * @param longitude : Longitude of the point, in degrees.
     * @param levelOfDetail : Level of detail, from 1 (lowest detail) to 23 (highest detail).
     * @return Output parameter receiving the X,Y coordinate in pixels.
     */
    public static Point latLongToPixelXY(double latitude, double longitude, int levelOfDetail)
    {
    	int pixelX , pixelY;
        double clippedLatitude = clip(latitude, MIN_LATITUDE, MAX_LATITUDE);
        double clippedLongitude = clip(longitude, MIN_LONGITUDE, MAX_LONGITUDE);

        double x = (clippedLongitude + 180) / 360;
        double sinLatitude = Math.sin(clippedLatitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

        long mapSize = mapSize(levelOfDetail);
        pixelX = (int)clip(x * mapSize + 0.5, 0, mapSize - 1);
        pixelY = (int)clip(y * mapSize + 0.5, 0, mapSize - 1);
        
        return new Point(pixelX, pixelY);
    }


    /***
     * Converts a pixel from pixel XY coordinates at a specified level of detail into lat/long wgs84 coordinates(in degrees)
     * @param pixelX
     * @param pixelY
     * @param levelOfDetail 1 to 23
     * @param point output parameter receiving the lat/lon in degrees
     */
    public static void pixelXYToLatLong(int pixelX, int pixelY, int levelOfDetail, MapPointModel.LonLatPoint point)
    {
        double mapSize = mapSize(levelOfDetail);
        double x = (clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
        double y = 0.5 - (clip(pixelY, 0, mapSize - 1) / mapSize);

        point.setLat(90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI);
        point.setLon(360 * x);
    }

    /**
     * 
     * @param pixelX
     * @param pixelY
     * @param levelOfDetail
     * @return
     */
    public static PointD pixelXYToLatLong(int pixelX, int pixelY, int levelOfDetail )
    {
    	double latitude, longitude;
        double mapSize = mapSize(levelOfDetail);
        double x = (clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
        double y = 0.5 - (clip(pixelY, 0, mapSize - 1) / mapSize);

        latitude = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        longitude = 360 * x;
        
        return new PointD (longitude, latitude);
    }


    /***
     * Converts pixel XY coordinates into tile XY coordinates of the tile containing the specified pixel
     * @param pixelX 
     * @param pixelY
     * @param point output parameter receiving the tile x/y coordinates
     */
    public static void pixelXYToTileXY(int pixelX, int pixelY, MapPointModel.TilePoint point)
    {
    	point.setTileX(pixelX / 256);
        point.setTileY(pixelY / 256);
    }



    /**
     * Converts tile XY coordinates into pixel XY coordinates of the upper-left pixel of the specified tile
     * @param tileX Tile X coordinate
     * @param tileY Tile Y coordinate.
     * @param point Output parameter receiving the pixel X/Y coordinate.
     */
    public static void tileXYToPixelXY(int tileX, int tileY, MapPointModel.PixelPoint point)
    {
    	point.setPixelX(tileX * 256);
        point.setPixelY(tileY * 256);
    }
    
    /**
     * Converts tile XY coordinates into pixel XY coordinates of the upper-left pixel of the specified tile.
     * @param tileX : Tile X coordinate.
     * @param tileY : Tile Y coordinate.
     * @return Output parameter receiving the pixel X, Y coordinate.
     */
    public static Point tileXYToPixelXY(int tileX, int tileY )
    {
    	int pixelX, pixelY;
        pixelX = tileX * 256;
        pixelY = tileY * 256;
        
        return new Point (pixelX, pixelY);
    }


    /**
     * Converts tile XY coordinates into a QuadKey at a specified level of detail.
     * @param tileX
     * @param tileY
     * @param levelOfDetail 1 to 23
     * @return A String containing the Quadkey
     */
    public static String tileXYToQuadKey(int tileX, int tileY, int levelOfDetail)
    {
        StringBuilder quadKey = new StringBuilder();
        for (int i = levelOfDetail; i > 0; i--)
        {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0)
            {
                digit++;
            }
            if ((tileY & mask) != 0)
            {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }



    /**
     *  Converts a QuadKey into tile XY coordinates.
     * @param quadKey QuadKey of the tile
     * @param point Output parameter receiving the tile X/Y coordinate
     * @param level Output parameter receiving the level of detail.
     * @throws Exception
     */
    public static void quadKeyToTileXY(String quadKey, MapPointModel.TilePoint point, MapPointModel.LevelOfDetail level) throws Exception 
    {
    	point.setTileX(0);
    	point.setTileY(0);
    	level.setZoomLevel(quadKey.length());
    	
    	char[] quadKeys = quadKey.toCharArray();
        for (int i = level.getZoomLevel(); i > 0; i--)
        {
            int mask = 1 << (i - 1);
            switch (quadKeys[level.getZoomLevel() - i])
            {
                case '0':
                    break;
                case '1':
                	point.setTileX(point.getTileX() | mask);
                    break;

                case '2':
                	point.setTileY(point.getTileY() | mask);
                    break;

                case '3':
                	point.setTileX(point.getTileX() | mask);
                	point.setTileY(point.getTileY() | mask);
                    break;

                default:
                    throw new Exception("Invalid QuadKey digit sequence.");
            }
        }
    }


    /**
     * 기본적인 quadkey의 bound정보를 가져오기
     * @param quadKey
     * @param point
     * @param point2
     * @param level
     * @throws Exception
     */
    public static void quadKeyToLonLat(String quadKey,
          MapPointModel.LonLatPoint point,
          MapPointModel.LonLatPoint point2,
          MapPointModel.LevelOfDetail level) throws Exception{
    	
        MapPointModel.TilePoint tilePoint = new MapPointModel.TilePoint();
        
        quadKeyToTileXY(quadKey, tilePoint, level);

        point.setLon(0); 
        point.setLat(0);

        MapPointModel.PixelPoint pxPoint = new MapPointModel.PixelPoint(), pxPoint2 = new MapPointModel.PixelPoint();
        tileXYToPixelXY(tilePoint.getTileX(), tilePoint.getTileY(), pxPoint);
        tileXYToPixelXY(tilePoint.getTileX() + 1, tilePoint.getTileY() + 1, pxPoint2);

        pixelXYToLatLong(pxPoint.getPixelX(), pxPoint2.getPixelY(), level.getZoomLevel(), point);
        pixelXYToLatLong(pxPoint2.getPixelX(), pxPoint.getPixelY(), level.getZoomLevel(), point2);
    }


    /**
     *  이건 quadKeyToLonLat보다 5%정도 범위가 더 넓다
     * @param quadKey
     * @param point
     * @param point2
     * @param level
     * @throws Exception
     */
    public static void quadKeyToLonLat2(String quadKey,
    		MapPointModel.LonLatPoint point,
            MapPointModel.LonLatPoint point2,
            MapPointModel.LevelOfDetail level) throws Exception
    {
        MapPointModel.TilePoint tilePoint = new MapPointModel.TilePoint();
        quadKeyToTileXY(quadKey, tilePoint, level);

        point.setLon(0); point.setLat(0);

        MapPointModel.PixelPoint pxPoint = new MapPointModel.PixelPoint(), pxPoint2 = new MapPointModel.PixelPoint();
        tileXYToPixelXY(tilePoint.getTileX(), tilePoint.getTileY(), pxPoint);
        tileXYToPixelXY(tilePoint.getTileX() + 1, tilePoint.getTileY() + 1, pxPoint2);

        pixelXYToLatLong(pxPoint.getPixelX(), pxPoint2.getPixelY(), level.getZoomLevel(), point);
        pixelXYToLatLong(pxPoint2.getPixelX(), pxPoint.getPixelY(), level.getZoomLevel(), point2);

        double offsetX = Math.abs(point2.getLon() - point.getLon()) / 20.0;
        double offsetY = Math.abs(point2.getLat() - point.getLat()) / 20.0;

        if (point.getLon() > point2.getLon())
        {
        	point.setLon(point.getLon() + offsetX);
        	point2.setLon(point2.getLon() - offsetX);
        }
        else
        {
        	point.setLon(point.getLon() - offsetX);
        	point2.setLon(point2.getLon() + offsetX);
        }

        if (point.getLat() > point2.getLat())
        {
        	point.setLat(point.getLat() + offsetY);
        	point2.setLat(point2.getLat() - offsetY);
        }
        else
        {
        	point.setLat(point.getLat() - offsetY);
        	point2.setLat(point2.getLat() + offsetY);
        }
    }


    /** 이건 quadKeyToLonLat보다 20%정도 범위가 더 넓다
    **/
    public static void quadKeyToLonLat3(String quadKey,
    		MapPointModel.LonLatPoint point,
            MapPointModel.LonLatPoint point2,
            MapPointModel.LevelOfDetail level) throws Exception
    {
        MapPointModel.TilePoint tilePoint = new MapPointModel.TilePoint();
        quadKeyToTileXY(quadKey, tilePoint, level);

        point.setLon(0); point.setLat(0);

        MapPointModel.PixelPoint pxPoint = new MapPointModel.PixelPoint(), pxPoint2 = new MapPointModel.PixelPoint();
        tileXYToPixelXY(tilePoint.getTileX(), tilePoint.getTileY(), pxPoint);
        tileXYToPixelXY(tilePoint.getTileX() + 1, tilePoint.getTileY() + 1, pxPoint2);

        pixelXYToLatLong(pxPoint.getPixelX(), pxPoint2.getPixelY(), level.getZoomLevel(), point);
        pixelXYToLatLong(pxPoint2.getPixelX(), pxPoint.getPixelY(), level.getZoomLevel(), point2);

        double offsetX = Math.abs(point2.getLon() - point.getLon());
        double offsetY = Math.abs(point2.getLat() - point.getLat());

        if (point.getLon() > point2.getLon())
        {
        	point.setLon(point.getLon() + offsetX);
        	point2.setLon(point2.getLon() - offsetX);
        }
        else
        {
        	point.setLon(point.getLon() - offsetX);
        	point2.setLon(point2.getLon() + offsetX);
        }

        if (point.getLat() > point2.getLat())
        {
        	point.setLat(point.getLat() + offsetY);
        	point2.setLat(point2.getLat() - offsetY);
        }
        else
        {
        	point.setLat(point.getLat() - offsetY);
        	point2.setLat(point2.getLat() + offsetY);
        }
    }

    /**
     * Converts a QuadKey into tile XY coordinates.
     * @param quadKey : QuadKey of the tile.
     * @return
     * @throws Exception
     */
	public static TilePositionModel quadKeyToTileXY(String quadKey) throws Exception
	{
	    int tileX = 0,  tileY = 0;
	    int levelOfDetail = quadKey.length();
	    
	    for (int i = levelOfDetail; i > 0; i--)
	    {
	        int mask = 1 << (i - 1);
	        switch (quadKey.charAt(levelOfDetail - i))
	        {
	            case '0':
	                break;

	            case '1':
	                tileX |= mask;
	                break;

	            case '2':
	                tileY |= mask;
	                break;

	            case '3':
	                tileX |= mask;
	                tileY |= mask;
	                break;

	            default:
	            	throw new Exception("Invalid QuadKey digit sequence.");
	        }
	    }
	    
	    return new TilePositionModel(tileX, tileY, levelOfDetail);
	    
	}
	
	/**
	 * 
	 * @param quadKey
	 * @return
	 * @throws Exception
	 */
	public static TileLonLatModel quadKeyToLonLat(String quadKey) throws Exception
	{
	    try {
	    	TilePositionModel model = quadKeyToTileXY(quadKey);
	        		        
	        Point pixel1 = tileXYToPixelXY(model.getTileX(), model.getTileY());
	        Point pixel2 = tileXYToPixelXY(model.getTileX() + 1, model.getTileY() + 1);

	        PointD lonlat1 = pixelXYToLatLong(pixel1.x, pixel2.y, model.getLevelOfDetail());
	        PointD lonlat2 = pixelXYToLatLong(pixel2.x, pixel1.y, model.getLevelOfDetail());
	        
	        return new TileLonLatModel(lonlat1.getX(), lonlat1.getY(), lonlat2.getX(), lonlat2.getY(), model.getLevelOfDetail());
	        
	    }
	    catch (Exception e) {
	    	throw e;
	    }
	}

	/**
	 * quadKeyToLonLat보다 5%정도 범위가 더 넓다 
	 * @param quadKey
	 * @return
	 * @throws Exception
	 */
	public static TileLonLatModel quadKeyToLonLat2(String quadKey) throws Exception
	{
      try {
	    	TilePositionModel model = quadKeyToTileXY(quadKey);
	        		        
	        Point pixel1 = tileXYToPixelXY(model.getTileX(), model.getTileY());
	        Point pixel2 = tileXYToPixelXY(model.getTileX() + 1, model.getTileY() + 1);

	        PointD lonlat1 = pixelXYToLatLong(pixel1.x, pixel2.y, model.getLevelOfDetail());
	        PointD lonlat2 = pixelXYToLatLong(pixel2.x, pixel1.y, model.getLevelOfDetail());
	        
	        double x1 = lonlat1.getX(), y1 = lonlat1.getY(), x2 = lonlat2.getX(), y2 = lonlat2.getY();
	        double offsetX = Math.abs(x2 - x1) / 20.0;
	        double offsetY = Math.abs(y2 - y1) / 20.0;

	        if (x1 > x2)
	        {
	        	lonlat1.setX(x1 + offsetX);
	        	lonlat2.setX(x2 - offsetX);
	        }
	        else
	        {
	        	lonlat1.setX(x1 - offsetX);
	        	lonlat2.setX(x2 + offsetX);
	        }

	        if (y1 > y2)
	        {
	        	lonlat1.setY(y1 + offsetY);
	        	lonlat2.setY(y2 - offsetY);
	        }
	        else
	        {
	        	lonlat1.setY(y1 - offsetY);
	        	lonlat2.setY(y2 + offsetY);
	        }
	        
	        return new TileLonLatModel(lonlat1.getX(), lonlat1.getY(), lonlat2.getX(), lonlat2.getY(), model.getLevelOfDetail());
	        
	    }
	    catch (Exception e) {
	    	throw e;
	    }

	}


	/**
	 * quadKeyToLonLat보다 20%정도 범위가 더 넓다
	 * @param quadKey
	 * @return
	 * @throws Exception
	 */
	public static TileLonLatModel quadKeyToLonLat3(String quadKey) throws Exception
	{
    	try {
	    	TilePositionModel model = quadKeyToTileXY(quadKey);
	        		        
	        Point pixel1 = tileXYToPixelXY(model.getTileX(), model.getTileY());
	        Point pixel2 = tileXYToPixelXY(model.getTileX() + 1, model.getTileY() + 1);
	
	        PointD lonlat1 = pixelXYToLatLong(pixel1.x, pixel2.y, model.getLevelOfDetail());
	        PointD lonlat2 = pixelXYToLatLong(pixel2.x, pixel1.y, model.getLevelOfDetail());
	        
	        double x1 = lonlat1.getX(), y1 = lonlat1.getY(), x2 = lonlat2.getX(), y2 = lonlat2.getY();
	        double offsetX = Math.abs(x2 - x1) / 2.0;
	        double offsetY = Math.abs(y2 - y1) / 2.0;
	
	        if (x1 > x2)
	        {
	        	lonlat1.setX(x1 + offsetX);
	        	lonlat2.setX(x2 - offsetX);
	        }
	        else
	        {
	        	lonlat1.setX(x1 - offsetX);
	        	lonlat2.setX(x2 + offsetX);
	        }
	
	        if (y1 > y2)
	        {
	        	lonlat1.setY(y1 + offsetY);
	        	lonlat2.setY(y2 - offsetY);
	        }
	        else
	        {
	        	lonlat1.setY(y1 - offsetY);
	        	lonlat2.setY(y2 + offsetY);
	        }
	        
	        return new TileLonLatModel(lonlat1.getX(), lonlat1.getY(), lonlat2.getX(), lonlat2.getY(), model.getLevelOfDetail());
	        
	    }
	    catch (Exception e) {
	    	throw e;
	    }
	}
	
	/**
	 * 
	 * @author owner
	 *
	 */
	public static class PointD
	{
	    private double _x;
	    private double _y;
	    
	    /**
	     * 
	     * @param x
	     * @param y
	     */
	    public PointD(double x, double y) {
	    	this._x = x;
	    	this._y = y;
	    }
	    
	    public void setX(double x) {
	    	this._x = x;
	    }
	    
	    public void setY(double y) {
	    	this._y = y;
	    }
	    
	    public double getX() {
	    	return this._x;
	    }
	    
	    public double getY() {
	    	return this._y;
	    }
	}

}
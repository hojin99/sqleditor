package com.hj.se.mapbase;

import com.hj.se.mapbase.utils.TileSystem;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKTReader;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class MapCommon {
	 
	private static final double EarthRadius = 6378137;
    private static final double MinLongitude = -180;
    private static final double MinLatitude = -85.05112878;
    private static final double MaxLatitude = 85.05112878;
    private static final double MaxLongitude = 180;
	
    /**
     * min / max 좌표값과 이미지의 width/height를 보내면 적절한 center lon/lat과 zoom level 정보를 return
     * width, height는 실제 image상의 사이즈
     * /// http://rbrundritt.wordpress.com/2008/10/25/ve-imagery-service-and-custom-icons/ 의 소스.
     * @param minLon
     * @param minLat
     * @param maxLon
     * @param maxLat
     * @param width
     * @param height
     * @return
     */
    public static MapGPSInfo calculateMapView(double minLon, double minLat, double maxLon, double maxLat,int width, int height)            
    {
    	
		double centerLon, centerLat;
		centerLon = 0;
        centerLat = 0;
        //Calculate bounding rectangle


        //default zoom scales in km/pixel from http://msdn2.microsoft.com/en-us/library/aa940990.aspx
        // 이부분은 bingmap 참조부분이어서 openlayers의 zoomlevel이 더 깊다면 지원되지 않을 수 있음..
        double[] defaultScales = { 78.27152, 39.13576, 19.56788, 9.78394, 4.89197, 2.44598, 1.22299, 0.61150, 0.30575, 0.15287, .07644, 0.03822, 0.01911, 0.00955, 0.00478, 0.00239, 0.00119, 0.0006, 0.0003 };


        //calculate center coordinate of bounding box
        centerLat = (maxLat + minLat) / 2.0;
        centerLon = (maxLon + minLon) / 2.0;

        //want to calculate the distance in km along the center latitude between the two longitudes
        //double meanDistanceX = HaversineDistance(centerLat, minLon, centerLat, maxLon);
        double meanDistanceX = getGPSDistance(centerLat, minLon, centerLat, maxLon);

        //want to calculate the distance in km along the center longitude between the two latitudes
        //double meanDistanceY = HaversineDistance(maxLat, centerLon, minLat, centerLon) * 2;
        double meanDistanceY = getGPSDistance(maxLat, centerLon, minLat, centerLon);

        //calculates the x and y scales
        double meanScaleValueX = meanDistanceX / (double)width;
        double meanScaleValueY = meanDistanceY / (double)height;

//        System.out.println(meanDistanceX+"," + meanDistanceY + ", " + meanScaleValueX + "," + meanScaleValueY);
        
        double meanScale;

        //gets the largest scale value to work with
        if (meanScaleValueX > meanScaleValueY) meanScale = meanScaleValueX;
        else
            meanScale = meanScaleValueY;

        //intialize zoom level variable
        int zoom = 0;

        //calculate zoom level
        for (int i = 1; i < defaultScales.length; i++)
        {
            if (meanScale >= defaultScales[i])
            {
                zoom = i;
                break;
            }
        }
        if (zoom == 0)
        {
            zoom = 19;
        }

        // 예외처리해보기..
        Rectangle rect = new Rectangle(0, 0, width, height);
        for (int i = zoom; i > 1; i--)
        {
            Point point1 = latLongToPixel(minLon, minLat, centerLon, centerLat, i, width, height);
            Point point2 = latLongToPixel(minLon, maxLat, centerLon, centerLat, i, width, height);
            Point point3 = latLongToPixel(maxLon, minLat, centerLon, centerLat, i, width, height);
            Point point4 = latLongToPixel(maxLon, maxLat, centerLon, centerLat, i, width, height);

            
            //System.out.println(point1+"," + point2 + ", " + point3+ "," + point4+"," + i);
            
            if (rect.contains(point1) &&
                rect.contains(point2) &&
                rect.contains(point3) &&
                rect.contains(point4))
            {
                zoom = i;
                break;
            }

        }

        
        return new MapGPSInfo(centerLon, centerLat, zoom);
    }

    
    /// <summary>
    /// km단위 리턴
    /// </summary>
    /// <param name="startX"></param>
    /// <param name="startY"></param>
    /// <param name="endX"></param>
    /// <param name="endY"></param>
    /// <returns></returns>
    public static double getGPSDistance(double startX, double startY, double endX, double endY)
    {
        double termX = Math.abs(Math.max(startX, endX) - Math.min(startX, endX));
        double termY = Math.abs(Math.max(startY, endY) - Math.min(startY, endY));
        double lon = Math.abs(termX * Math.PI / 360.0);
        double lat = Math.abs(termY * Math.PI / 360.0);
        double tempValue = Math.abs((Math.sin(lat)) * (Math.sin(lat)) + Math.cos(startY * Math.PI / 180.0) * Math.cos(endY * Math.PI / 180.0) * (Math.sin(lon)) * (Math.sin(lon)));
        double distance = EarthRadius * 2.0 * Math.asin(Math.min(1.0, Math.sqrt(tempValue)));
        return distance /1000.0 ;
    }
    
    
    //Formulas based on following article:
    //http://msdn.microsoft.com/en-us/library/bb259689.aspx
    //http://rbrundritt.wordpress.com/2008/10/25/ve-imagery-service-and-custom-icons/
    public static Point latLongToPixel(double lon, double lat,
            double centerLon, double centerLat, int zoomLevel, int width, int height)
    {    

        //calcuate pixel coordinates of center point of map
        double sinLatitudeCenter = Math.sin(centerLat * Math.PI / 180);
        double pixelXCenter = ((centerLon + 180) / 360) * 256 * Math.pow(2, zoomLevel);
        double pixelYCenter = (0.5 - Math.log((1 + sinLatitudeCenter) / (1 - sinLatitudeCenter)) / (4 * Math.PI)) * 256 * Math.pow(2, zoomLevel);

        //calculate pixel coordinate of location
        double sinLatitude = Math.sin(lat * Math.PI / 180);
        double pixelX = ((lon + 180) / 360) * 256 * Math.pow(2, zoomLevel);
        double pixelY = (0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI)) * 256 * Math.pow(2, zoomLevel);

        //calculate top left corner pixel coordiates of map image
        double topLeftPixelX = pixelXCenter - (width / 2.0);
        double topLeftPixelY = pixelYCenter - (height / 2.0);

        //calculate relative pixel cooridnates of location
        double x = pixelX - topLeftPixelX; double y = pixelY - topLeftPixelY;

        return new Point((int)Math.floor(x), (int)Math.floor(y));
    }

    
    public static Point2D get4326From3857(Point2D lonlat) throws Exception {
    	
    	CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
    	CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");

    	MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
    	
    	GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    	Coordinate coord = new Coordinate(lonlat.getX(), lonlat.getY());
    	Geometry sourceGeometry = (Geometry) geometryFactory.createPoint(coord);
    	
    	Geometry targetGeo = JTS.transform(sourceGeometry,  transform);
    	//Point targetPoint = (Point)JTS.transform(sourceGeometry, transform)
    	
    	Point2D pointD = new Point2D.Double(targetGeo.getCentroid().getX(), targetGeo.getCentroid().getY());
    	//return targetGeo.getCentroid();
    	return pointD;
    }

    
    public static Point2D get3857From4326(Point2D binXY) throws Exception {
    	
    	CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:3857");
    	CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");

    	MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
    	
    	GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    	Coordinate coord = new Coordinate(binXY.getX(), binXY.getY());
    	Geometry sourceGeometry = (Geometry) geometryFactory.createPoint(coord);
    	
    	Geometry targetGeo = JTS.transform(sourceGeometry,  transform);
    	//Point targetPoint = (Point)JTS.transform(sourceGeometry, transform)
    	
    	Point2D pointD = new Point2D.Double(targetGeo.getCentroid().getX(), targetGeo.getCentroid().getY());
    	//return targetGeo.getCentroid();
    	return pointD;
    }

    
    public static Point2D[] get4326From3857(Point2D[] lonlat) throws Exception{
    	
    	CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
    	CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");

    	MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
    	
    	GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

    	Point2D[] result = new Point2D[lonlat.length];
    	for (int i=0;i<lonlat.length;i++) {
	    	Coordinate coord = new Coordinate(lonlat[i].getX(), lonlat[i].getY());
	    	Geometry sourceGeometry = (Geometry) geometryFactory.createPoint(coord);
	    	
	    	Geometry targetGeo = JTS.transform(sourceGeometry,  transform);
	    	result[i] = new Point2D.Double(targetGeo.getCentroid().getX(), targetGeo.getCentroid().getY());
    	}
    	//return targetGeo.getCentroid();
    	return result;
    }

    
    public static Point2D[] get3857From4326(Point2D[] lonlat) throws Exception{
    	
    	CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:3857");
    	CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");

    	MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
    	
    	GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

    	Point2D[] result = new Point2D[lonlat.length];
    	for (int i=0;i<lonlat.length;i++) {
	    	Coordinate coord = new Coordinate(lonlat[i].getX(), lonlat[i].getY());
	    	Geometry sourceGeometry = (Geometry) geometryFactory.createPoint(coord);
	    	
	    	Geometry targetGeo = JTS.transform(sourceGeometry,  transform);
	    	result[i] = new Point2D.Double(targetGeo.getCentroid().getX(), targetGeo.getCentroid().getY());
    	}
    	return result;
    }
    
    /**
     * 4326 lon,lat과 binSize를 넘겨주면 3857로 변환 계산해서 해당 좌표가 포함되는 4326좌표의 rectangle을 return. 
     * @return
     */
    public static Rectangle2D get4326BinRectangle(double lon, double lat, int binSize) throws Exception {
    	
    	CoordinateReferenceSystem crs3857 = CRS.decode("EPSG:3857");    	
    	//CoordinateReferenceSystem crs4326 = CRS.decode("EPSG:4326");
    	
    	Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
    	CRSAuthorityFactory factory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
    	CoordinateReferenceSystem crs4326 = factory.createCoordinateReferenceSystem("EPSG:4326");
    	//CoordinateReferenceSystem crs4326 = factory.createCoordinateReferenceSystem("urn:x-ogc:def:crs:EPSG:4326");

    	/*System.out.print("lonlat : " + CRS.getAxisOrder(crs3857) +" \r\n");
    	System.out.print("lonlat : " + CRS.getAxisOrder(crs4326) +" \r\n");
    	*/
    	
    	MathTransform transTo3857 = CRS.findMathTransform(crs4326, crs3857, false);
    	MathTransform transTo4326 = CRS.findMathTransform(crs3857, crs4326, false);
    	/*
    	double[] input = new double[2];
    	double[] output = new double[2];
    	
    	input[0] = lon;
    	input[1] = lat;
    	
    	transTo3857.transform(input, 0, output,0, 1);
    	
    	System.out.print("lonlat : " + output[0]+ " ," + output[1]+ " \r\n");
    	transTo4326.transform(output, 0, input,0, 1);
    	*/
    	GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    	
    	
    	//System.out.print("lonlat : " + input[0]+ " ," + input[1]+ " \r\n");
    	

    	//System.out.print("lonlat : " + lon + " ," + lat + " \r\n");
    	org.locationtech.jts.geom.Point point = geometryFactory.createPoint(new Coordinate(lon, lat));
    	//System.out.print("lonlat : " + point.getX()+ " ," + lat + " \r\n");
    	
	    // 해당 좌표가 포함되는 bin
    	org.locationtech.jts.geom.Point targetPoint = (org.locationtech.jts.geom.Point) JTS.transform(point,  transTo3857);
	    
	    
	    //System.out.print("bin : " + targetPoint.getCentroid().getX()+ " ," + targetPoint.getCentroid().getY());
	    Point binMin = new Point((int)(targetPoint.getCentroid().getX()/binSize)*binSize, (int)(targetPoint.getCentroid().getY()/binSize)*binSize);
    	Point binMax = new Point(binMin.x+binSize, binMin.y+binSize);
    	
    	
    	Coordinate coordBinMin = new Coordinate(binMin.x, binMin.y);
    	Coordinate coordBinMax = new Coordinate(binMax.x, binMax.y);
    	
    	Geometry lonlatBinMin = JTS.transform(geometryFactory.createPoint(coordBinMin),  transTo4326);
    	Geometry lonlatBinMax= JTS.transform(geometryFactory.createPoint(coordBinMax),  transTo4326);
    	
    	double width = Math.abs(lonlatBinMin.getCentroid().getX() - lonlatBinMax.getCentroid().getX());
    	double height = Math.abs(lonlatBinMin.getCentroid().getY() - lonlatBinMax.getCentroid().getY());
    	
    	
    	
    	Rectangle2D rect=  new Rectangle2D.Double(lonlatBinMax.getCentroid().getX(),lonlatBinMin.getCentroid().getY(),width, height);
    	
    	//System.out.print("lonlat : " + lon + "," + lat +  " ==> : " + rect.getMinX() + "," + rect.getMinY() + "," + rect.getMaxX() + "," + rect.getMaxY() + "\r\n");
    	
	    return rect;
    }
    
    /**
     * 4326 polgyon WKT을 input으로 받아서.coordinates를 가져온다.  
     * @return
     */
    public static Coordinate[][] getPolygonCoordinates(String polygonWKT) throws Exception {
    	//4326
    	CoordinateReferenceSystem crs = CRS.decode(("EPSG:4326"));
    	Hints hints = new Hints(Hints.CRS, crs);
    	GeometryFactory geometryFactoryWKT = JTSFactoryFinder.getGeometryFactory(hints);
    	WKTReader wktReader = new WKTReader(geometryFactoryWKT);
    	Geometry geom =  wktReader.read(polygonWKT);
    	geom.setSRID(4326);
    	
    	if (geom.getNumGeometries()>0) {
	    	Coordinate[][] coordList = new Coordinate[geom.getNumGeometries()][]; 
	    	for (int i=0;i<geom.getNumGeometries();i++) {
	    		coordList[i] = geom.getGeometryN(i).getCoordinates();
	    	}
	    	return coordList;
    	}
    	
    	return null;
    }

    public static Integer getCircleDiameter(Integer pointSize, Integer zoomLevel){

        int [] zoomPointSizeArr = {
                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, // zoom level : 0 ~ 14 => point size medium 1
                1,2,4,10,22,44,44,40,22,10,4,2,1,0 // zoom level : 15 ~ 23
        };

        return  ((pointSize + 1) * ( zoomPointSizeArr[zoomLevel] + 1 ));
    }

    public static String getClusteringDefaultLon(Integer pointSize){
        String [] zoomPointSizeArr = {
                "1","1","1","1","0.1","0.1","0.1","0.01","0.01","0.01","0.001","0.001","0.001","0.0001","0.0001","0.0001", // zoom level : 0 ~ 14 => point size medium 1
                "0.00001","0.00001","0.00001","0.00001","0.00001","0.00001","0.00001","0.00001","0.00001" ,"0.00001" ,"0.00001","0.00001","0.00001","0.00001","0.00001"   // zoom level : 15 ~ 23
        };
        return zoomPointSizeArr[pointSize];
    }

    public static String getClickBinBetweenOffsetByZoom(Integer zoom){
        String zoomOffset = "0.00007";
        switch (zoom){
            case 10 : zoomOffset = "0.0001";
                break;
            case 11 : zoomOffset = "0.0003";
                break;
            case 12 : zoomOffset = "0.0005";
                break;
            case 13 : zoomOffset = "0.0007";
                break;
            case 14 : zoomOffset = "0.00001";
                break;
            case 15 : zoomOffset = "0.00003";
                break;
            case 16 : zoomOffset = "0.00005";
                break;
            case 17 : zoomOffset = "0.00007";
                break;
        }
        return zoomOffset;
    }

    public static TileSystem.PointD transformLonLatOffset(double lon, double lat, Integer zoom, Integer xOffset, Integer yOffset){
        Point outPixel = TileSystem.latLongToPixelXY(lat, lon, zoom);
        Integer x = outPixel.x - xOffset;
        Integer y = outPixel.y - yOffset;
        return TileSystem.pixelXYToLatLong(x, y, zoom);
    }
}


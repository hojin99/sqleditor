package com.hj.se.mapbase.utils;

import com.hj.se.mapbase.MapCommon;
import com.hj.se.mapbase.model.TileLonLatModel;
import com.hj.se.mapbase.model.TilePositionModel;
import org.locationtech.jts.geom.Coordinate;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 * @author snowstock
 *
 */
public class TileSystemModule
{
	private static final int CANVAS_WIDTH  = 256;
	private static final int CANVAS_HEIGHT = 256;
	private final BufferedImage _image;
	private final Graphics _graphics;
	final private int _pixelX, _pixelY;
	
	private Color _fillColor;
	private Color _drawColor;
	private Font _font;
	private int _tileX, _tileY, _zoom;
	
	private final Stroke _line = new BasicStroke(3,BasicStroke.CAP_BUTT, 0);
	private final Stroke _dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
    //Site map
    private Color _outLineColor = Color.WHITE;
    private Color _siteColor = new Color(255,0,0); //red
    private Font _textFont = new Font(Font.SERIF, Font.PLAIN, 8);
    private boolean _isShowText = true;
    private boolean _isBlurBackground = false;
    private int _maxYPos = 0;
    private int _circleSize = 8;

    private final String[] _symbols = { "●", "■", "▲", "X", "*", "-", "D", "E", "F", "P", "S", "T", "O", "R" };

	/**
	 * 
	 * @return
	 */
	public BufferedImage getImage() {
		return _image;
	}
    
	/**
	 * 
	 * @return
	 */
	public Color getFillColor() {
		return _fillColor;
	}

	/**
	 * 
	 * @param _fillColor
	 */
	public void setFillColor(Color _fillColor) {
		this._fillColor = _fillColor;
	}

	/**
	 * 
	 * @return
	 */
	public Color getDrawColor() {
		return _drawColor;
	}

	/**
	 * 
	 * @param _drawColor
	 */
	public void setDrawColor(Color _drawColor) {
		this._drawColor = _drawColor;
	}

	/**
	 * 
	 * @return
	 */
	public Font getFont() {
		return _font;
	}

	/**
	 * 
	 * @param _font
	 */
	public void setFont(Font _font) {
		this._font = _font;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @throws Exception
	 */
	public TileSystemModule(int x, int y, int z) throws Exception
    {
        String quadkey = TileSystem.googleXYZToQuadKey(x, y, z);

        //quadkey에서 정보 추출
        try {
        	TilePositionModel model = TileSystem.quadKeyToTileXY(quadkey);
        	_tileX = model.getTileX();
        	_tileY = model.getTileY(); 
        	_zoom = model.getLevelOfDetail();

			System.out.println("_tileX: " + _tileX);
			System.out.println("_tileY: " + _tileY);
			System.out.println("_zoom: " + _zoom);

        }
        catch (Exception e) {
        	throw e;
        }
        
        _image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        _graphics = _image.getGraphics();
                
        Point pixel = TileSystem.tileXYToPixelXY(_tileX, _tileY);
        _pixelX = pixel.x;
        _pixelY = pixel.y;
		System.out.println("_pixelX: " + _pixelX);
		System.out.println("_pixelY: " + _pixelY);

    }

	/**
	 * 타일의 slon, slat, elon, elat을 구함
	 * @param x
	 * @param y
	 * @param z
	 */
	public static TileLonLatModel setExtenLonLat(int x, int y, int z) throws Exception {
		
		String quadkey = TileSystem.googleXYZToQuadKey(x, y, z);
		
		TileLonLatModel model = TileSystem.quadKeyToLonLat3(quadkey);
		return model;
	}

	
	/**
     * Conver Lon , Lat => Pixel X , Y
     * @param lon
     * @param lat
     * @return
     */
    private Point getPoint(double lon, double lat)
    {
        Point outPixel = TileSystem.latLongToPixelXY(lat, lon, _zoom);

        outPixel.x = outPixel.x - _pixelX;
        outPixel.y = outPixel.y - _pixelY;
        
        return outPixel;
    }

    /**
     * icon을 그릴 때 사용함.
     * @param lon
     * @param lat
     * @param img
     */
    public void drawImage(double lon, double lat, Image img)
    {
        Point point = getPoint(lon, lat); // Lon/Lat Convert to Pixel X/Y
    	
        _graphics.drawImage(img, point.x, point.y , null);
        
    }
	
	/**
	 * 
	 * @param lon
	 * @param lat
	 * @param text
	 */
    public void drawString(double lon, double lat, String text)
    {
    	Point point = getPoint(lon, lat); // Lon/Lat Convert to Pixel X/Y
    	
    	_graphics.setFont(getFont());
    	_graphics.setColor(getDrawColor());
    	_graphics.drawString(text, point.x, point.y);
    }


	public void drawEllipse(double lon, double lat, int width, int height, Integer offsetX, Integer offsetY, Color fillColor, Color drawColor)
	{
		Point point = getPoint(lon, lat); // Lon/Lat Convert to Pixel X/Y
		_graphics.setColor(fillColor);
		_graphics.fillArc(point.x - (width/2) + offsetX, point.y - (height/2)+ offsetY, width, height, 0, 360);
		_graphics.setColor(drawColor);
		_graphics.drawArc(point.x - (width/2) + offsetX, point.y - (height/2)+ offsetY, width, height, 0, 360);
	}

    /**
     * bin의 테두리를 그릴 때 사용
     * @param lon
     * @param lat
     * @param width
     * @param height
     */
    public void drawClickEllipse(double lon, double lat, int width, int height)
    {
        Point point = getPoint(lon, lat); // Lon/Lat Convert to Pixel X/Y
        
        _graphics.setColor(Color.BLACK);
        _graphics.fillArc(point.x - (width/2), point.y - (height/2), width, height, 0, 360);
        _graphics.drawArc(point.x - (width/2), point.y - (height/2), width, height, 0, 360);
    }
    
    public void drawFlag(double lon, double lat, Color fillColor, Color borderColor, Integer size, Integer offsetX, Integer offsetY){
		Point point = getPoint(lon, lat); // Lon/Lat Convert to Pixel X/Y
		Graphics2D g2 = (Graphics2D) _graphics;

		int x[]={point.x + offsetX ,point.x + size/4 + offsetX ,point.x + size / 8 + offsetX , point.x + size/8 + offsetX, point.x + offsetX, point.x + offsetX };
		int y[]={point.y - size / 4 + offsetY ,point.y + offsetY  ,point.y + offsetY  , point.y + size/2 + offsetY, point.y + size/2 + offsetY, point.y + offsetY };

		g2.setColor(fillColor);
		g2.fillPolygon(x, y, 6);
		g2.setColor(borderColor);
		g2.drawPolygon(x,y,6);
	}

	public void drawArrow(double lon, double lat, Color fillColor, Integer size, Integer offsetX, Integer offsetY, ArrayList<Point> points){
		Point point = getPoint(lon, lat); // Lon/Lat Convert to Pixel X/Y
		points.add(point);
		double r = getDegree(point, points.get(0));
		if(points.size() >= 10){
			Graphics2D g2 = (Graphics2D) _graphics;

			Integer originPointX = point.x + ( (  point.y > points.get(0).y ? -10 : 10) + offsetX);
			Integer originPointY = point.y + ( -10 + offsetY);
			Point originPoint = new Point(originPointX, originPointY);

			ArrayList<Point> offsetValues = new ArrayList<>();
			offsetValues.add( rotatePoint(originPoint, originPoint, r, 0,1));
			offsetValues.add( rotatePoint(originPoint, originPoint, r, size / 2,size/2));
			offsetValues.add( rotatePoint(originPoint, originPoint, r, -size / 2,0));
			offsetValues.add( rotatePoint(originPoint, originPoint, r, size / 2,-size/2));
			offsetValues.add( rotatePoint(originPoint, originPoint, r, 0,-1));

			int x[]={originPointX, offsetValues.get(0).x, offsetValues.get(1).x, offsetValues.get(2).x, offsetValues.get(3).x, offsetValues.get(4).x, originPointX};
			int y[]={originPointY, offsetValues.get(0).y, offsetValues.get(1).y, offsetValues.get(2).y, offsetValues.get(3).y, offsetValues.get(4).y, originPointY};

			g2.setColor(fillColor);
			g2.drawPolygon(x, y,6);
			g2.fillPolygon(x, y, 6);

			Point drawPoint = rotatePoint(originPoint, originPoint, r, size * 2,0);

			g2.drawLine(originPointX, originPointY, drawPoint.x, drawPoint.y);

			points.clear();
		}
	}

	public Point rotatePoint(Point pt, Point center, double angleDeg, Integer offSetX, Integer offSetY)
	{
		double angleRad = ((angleDeg/180)*Math.PI);
		double cosAngle = Math.cos(angleRad );
		double sinAngle = Math.sin(angleRad );
		double dx = (pt.x + offSetX - center.x);
		double dy = (pt.y + offSetY - center.y);

		Point originPoint = new Point();
		originPoint.x = center.x + (int) (dx*cosAngle-dy*sinAngle);
		originPoint.y = center.y + (int) (dx*sinAngle+dy*cosAngle);
		return originPoint;
	}

	public static double getDegree(Point p1, Point p2)
	{

		double dx = p2.x - p1.x;

		double dy = p2.y - p1.y;

		double r = Math.toDegrees(Math.atan2(dy, dx));

		return r;

	}
    
    /**
     * polygon을 그릴 때 사용함.
     * @param wktText
     * @param fillColor
     * @param lineWidth
     */
    public void drawPolygon(String wktText, Color fillColor, int lineWidth) {
		
		try {
			double minX = 1111110;double minY=11110; double maxX=-1000;double maxY=-10000;
			Graphics2D g2 = (Graphics2D) _graphics;
			Coordinate[][] coordList = MapCommon.getPolygonCoordinates(wktText);
			Stroke orgStroke = g2.getStroke();
			g2.setColor(fillColor);
			g2.setStroke(new BasicStroke(lineWidth));
			
			for (int i=0;i<coordList.length;i++) {
				Polygon poly = new Polygon();
				for (int k=0;k<coordList[i].length;k++) {
					Point point = getPoint(coordList[i][k].x, coordList[i][k].y); // Lon/Lat Convert to Pixel X/Y
			    	
			    	poly.addPoint(point.x, point.y);
			    	
			    	if (minX > coordList[i][k].x ) {minX = coordList[i][k].x;}
			    	if (minY > coordList[i][k].y ) {minY = coordList[i][k].y;}
			    	if (maxX < coordList[i][k].x ) {maxX = coordList[i][k].x;}
			    	if (maxY < coordList[i][k].y ) {maxY = coordList[i][k].y;}
			    	//System.out.println("coordinate :" + coordList[i]+","+coordList[i].y);
				}
				_graphics.drawPolygon(poly);
//				_graphics.fillPolygon(poly);
			}
			g2.setStroke(orgStroke);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    

	/**
	 * 점선 라인형태를 그릴때 사용함.
	 * @param slon
	 * @param lat
	 * @param width
	 * @param height
	 */
	public void drawDashedLine(double slon, double slat, double elon, double elat)
	{
		Point sPoint = getPoint(slon, slat); // Lon/Lat Convert to Pixel X/Y
		Point ePoint = getPoint(elon, elat); // Lon/Lat Convert to Pixel X/Y

		Graphics2D g2 = (Graphics2D) _graphics;

		//Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
		g2.setStroke(_dashed);
		g2.setPaint(getDrawColor());
		g2.drawLine(sPoint.x, sPoint.y, ePoint.x, ePoint.y);
	}
    
    /**
     * dt route를 그릴때 사용함.
     * @param lon
     * @param lat
     * @param width
     * @param height
     */
    public void drawDtRoute(double [][] dataList, int colorVal)
    {	
    	for(int i=0; i<dataList.length - 1; i++) {
    		Point sPoint = getPoint(dataList[i][0], dataList[i][1]); // Lon/Lat Convert to Pixel X/Y
            Point ePoint = getPoint(dataList[i+1][0], dataList[i+1][1]); // Lon/Lat Convert to Pixel X/Y
            int sCoordinatesSeq = (int) dataList[i][2];
            int eCoordinatesSeq = (int) dataList[i+1][2];
            
            if(sCoordinatesSeq == eCoordinatesSeq) {
            	Graphics2D g2 = (Graphics2D) _graphics;
                g2.setStroke(_line);
                g2.setColor(getColor(colorVal));
                g2.drawLine(sPoint.x, sPoint.y, ePoint.x, ePoint.y);
            }
            
            
    	}
    }

    //##str ##Site 그리기 ========>
    
    
    public void drawSettingArc(double lon, double lat, int azimuth, int angle, Color sectorColor, int arcSize){
    	boolean _isShowOutline = true;
    	
     	Point point = getPoint(lon, lat); // Lon/Lat Convert to Pixel X/Y
 		
 		int graphicsAzimuth = (int)getJavaGraphicAzimuth(azimuth);
 		arcSize += 50;
 		for(int i = 0; i < 5; i++) {
 			_graphics.setColor(sectorColor);
 	 		_graphics.fillArc(point.x- (arcSize/2), point.y-arcSize/2, arcSize, arcSize, (int)(graphicsAzimuth - angle/2), (int)(angle));
 	 	    
 	 	    // 시계방향으로 같고, 시작지점이 들어오는 값은 12시기준, 계산식쪽은 3시기준..
 	 	    double calcAzimuth = azimuth-90;
 	 	    if (calcAzimuth<0) {
 	 	    	calcAzimuth = 360 + calcAzimuth;
 	 	    }
 	 	    
 	 	     // arc와 text에서 2번하는데, 따로 빼기도 애매해서. 그냥 두	    
 	 	    Point pnStart = new Point(
 	 	    		point.x + (int)(Math.cos(Math.toRadians(calcAzimuth-angle/2)) * arcSize/2),
 	 	    		point.y + (int)(Math.sin(Math.toRadians(calcAzimuth-angle/2)) * arcSize/2));
 	 	    
 	 	    Point pnEnd = new Point(
 	 	    		point.x + (int)(Math.cos(Math.toRadians(calcAzimuth+angle/2)) * arcSize/2),
 	 	    		point.y + (int)(Math.sin(Math.toRadians(calcAzimuth+angle/2)) * arcSize/2));
 	 	    
 	 	    
 	 	  if (_isShowOutline && angle >0 && angle < 360) {// omni인경우에는 외곽선을 안그린다. 
 	 			Color outLineColor = _outLineColor;	//site in, out기준에 따라 테두리 색 변환
 	 			
 	 			_graphics.setColor(outLineColor);   
 	 			_graphics.drawLine(point.x, point.y, pnStart.x, pnStart.y);
 	 			_graphics.drawLine(point.x, point.y, pnEnd.x, pnEnd.y);
 	 			_graphics.drawArc(point.x- (arcSize/2), point.y-arcSize/2, arcSize, arcSize, (int)(graphicsAzimuth - angle/2), (int)(angle));
 	 		}
 	 	  
 	 	    arcSize -= 10;
 		}
 	}
    
    /**
     * site를 그릴 때 사용함.
     * @param lon
     * @param lat
     * @param siteText
     * @param cellText
     * @param azimuth
     * @param angle
     * @param sectorColor
     * @param arcSize
     */
     public void drawArc(double lon, double lat, String siteText, String cellText, int azimuth, int angle, Color sectorColor, int arcSize, int inOutType){
     	boolean _isShowOutline = true;
     	Point point = getPoint(lon, lat); // Lon/Lat Convert to Pixel X/Y
 		
 		int graphicsAzimuth = (int)getJavaGraphicAzimuth(azimuth);
 		
 		_graphics.setColor(sectorColor);
 		_graphics.fillArc(point.x- (arcSize/2), point.y-arcSize/2, arcSize, arcSize, (int)(graphicsAzimuth - angle/2), (int)(angle));
 	    
 	    // 시계방향으로 같고, 시작지점이 들어오는 값은 12시기준, 계산식쪽은 3시기준..
 	    double calcAzimuth = azimuth-90;
 	    if (calcAzimuth<0) {
 	    	calcAzimuth = 360 + calcAzimuth;
 	    }
 	    
 	     // arc와 text에서 2번하는데, 따로 빼기도 애매해서. 그냥 두	    
 	    Point pnAzimuth = new Point (
 	    		point.x + (int)(Math.cos(Math.toRadians(calcAzimuth)) * arcSize/2),
 	    		point.y + (int)(Math.sin(Math.toRadians(calcAzimuth)) * arcSize/2));
 	    Point pnStart = new Point(
 	    		point.x + (int)(Math.cos(Math.toRadians(calcAzimuth-angle/2)) * arcSize/2),
 	    		point.y + (int)(Math.sin(Math.toRadians(calcAzimuth-angle/2)) * arcSize/2));
 	    
 	    Point pnEnd = new Point(
 	    		point.x + (int)(Math.cos(Math.toRadians(calcAzimuth+angle/2)) * arcSize/2),
 	    		point.y + (int)(Math.sin(Math.toRadians(calcAzimuth+angle/2)) * arcSize/2));
 	    
 	    // 위의 3점에서 min/max값을 구한다.
 	    ArrayList<Integer> listXVal = new ArrayList<Integer>();
 	    listXVal.add(pnAzimuth.x);listXVal.add(pnStart.x);listXVal.add(pnEnd.x);
 	    ArrayList<Integer> listYVal = new ArrayList<Integer>();
 	    listYVal.add(pnAzimuth.y);listYVal.add(pnStart.y);listYVal.add(pnEnd.y);
 		
 		if (_isShowOutline && angle >0 && angle < 360) {// omni인경우에는 외곽선을 안그린다. 
 			Color outLineColor = inOutType == 1 ? _outLineColor : Color.black;	//site in, out기준에 따라 테두리 색 변환
 			
 			_graphics.setColor(outLineColor);   
 			_graphics.drawLine(point.x, point.y, pnStart.x, pnStart.y);
 			_graphics.drawLine(point.x, point.y, pnEnd.x, pnEnd.y);
 			_graphics.drawArc(point.x- (arcSize/2), point.y-arcSize/2, arcSize, arcSize, (int)(graphicsAzimuth - angle/2), (int)(angle));
 		}
 		
 		// 중심에 위치하는 circle...
 		_graphics.setColor(_siteColor); //navy default..
 		_graphics.fillArc(point.x-_circleSize/2, point.y-_circleSize/2, _circleSize, _circleSize, 0, 360);
 	    	
 	}

	/**
	 * site에 대한 text를 그릴 때 사용함.
	 * @param lon
	 * @param lat
	 * @param siteText
	 * @param cellText
	 * @param azimuth
	 * @param angle
	 * @param sectorColor
	 * @param arcSize
	 */
	public void drawSymbol(double lon, double lat, Integer symbolType, Color symbolColor, Color borderColor, int symbolSize, Integer offsetX, Integer offsetY){

		Point point = getPoint(lon, lat); // Lon/Lat Convert to Pixel X/Y
		FontMetrics metrics = _graphics.getFontMetrics(_textFont);
		int textWidth = metrics.stringWidth(_symbols[symbolType]);
		int textHeight = metrics.getHeight() ;

		Graphics2D g2 = (Graphics2D) _graphics;

		Point pnText = new Point(point.x - textWidth/2 + offsetX, point.y + textHeight/2 + offsetY);

		AttributedString textAS = new AttributedString(_symbols[symbolType]);
		Font font = new Font(Font.SERIF, Font.PLAIN, symbolSize + 4);
		textAS.addAttribute(TextAttribute.FONT, font);
		TextLayout textlayout = new TextLayout(textAS.getIterator(), g2.getFontRenderContext());
		Shape shape = textlayout.getOutline(AffineTransform.getTranslateInstance(pnText.x, pnText.y));
		g2.setColor(borderColor);
		g2.setStroke(new BasicStroke(font.getSize2D() / 10.0f));
		g2.draw(shape);
		g2.setColor(symbolColor);
		g2.fill(shape);
	}


	/**
	 * site에 대한 text를 그릴 때 사용함.
	 * @param lon
	 * @param lat
	 * @param siteText
	 * @param cellText
	 * @param azimuth
	 * @param angle
	 * @param sectorColor
	 * @param arcSize
	 */
	public void drawText(double lon, double lat, String text, Color symbolColor, int symbolSize, Integer offsetX, Integer offsetY){

		Point point = getPoint(lon, lat); // Lon/Lat Convert to Pixel X/Y
		FontMetrics metrics = _graphics.getFontMetrics(_textFont);
		int textWidth = metrics.stringWidth(text);
		int textHeight = metrics.getHeight() ;

		Graphics2D g2 = (Graphics2D) _graphics;

		Point pnText = new Point(point.x - textWidth/2 + offsetX, point.y + textHeight/2 + offsetY);

		AttributedString textAS = new AttributedString(text);
		Font font = new Font(Font.SERIF, Font.PLAIN, symbolSize + 4);
		textAS.addAttribute(TextAttribute.FONT, font);
		TextLayout textlayout = new TextLayout(textAS.getIterator(), g2.getFontRenderContext());
		Shape shape = textlayout.getOutline(AffineTransform.getTranslateInstance(pnText.x, pnText.y));
		g2.setColor(symbolColor);
		g2.setStroke(new BasicStroke(font.getSize2D() / 10.0f));
		g2.draw(shape);
		g2.fill(shape);
	}
    
    /**
     * site에 대한 text를 그릴 때 사용함.
     * @param lon
     * @param lat
     * @param siteText
     * @param cellText
     * @param azimuth
     * @param angle
     * @param sectorColor
     * @param arcSize
     */
    public void drawText(double lon, double lat, String siteText, String cellText, int azimuth, int angle, Color sectorColor, int arcSize) throws UnsupportedEncodingException {

    	Point point = getPoint(lon, lat); // Lon/Lat Convert to Pixel X/Y
	     // 시계방향으로 같고, 시작지점이 들어오는 값은 12시기준, 계산식쪽은 3시기준..
	     double calcAzimuth = azimuth-90;
	     if (calcAzimuth<0) {
	     	calcAzimuth = 360 + calcAzimuth;
	     }
	     
	     Point pnAzimuth = new Point (
	    		 point.x + (int)(Math.cos(Math.toRadians(calcAzimuth)) * arcSize/2),
	    		 point.y + (int)(Math.sin(Math.toRadians(calcAzimuth)) * arcSize/2));
	     Point pnStart = new Point(
	    		 point.x + (int)(Math.cos(Math.toRadians(calcAzimuth-angle/2)) * arcSize/2),
	    		 point.y + (int)(Math.sin(Math.toRadians(calcAzimuth-angle/2)) * arcSize/2));
	     
	     Point pnEnd = new Point(
	    		 point.x + (int)(Math.cos(Math.toRadians(calcAzimuth+angle/2)) * arcSize/2),
	    		 point.y + (int)(Math.sin(Math.toRadians(calcAzimuth+angle/2)) * arcSize/2));
	     
	     // 위의 3점에서 min/max값을 구한다.
	     ArrayList<Integer> listXVal = new ArrayList<Integer>();
	     listXVal.add(pnAzimuth.x);listXVal.add(pnStart.x);listXVal.add(pnEnd.x);
	     ArrayList<Integer> listYVal = new ArrayList<Integer>();
	     listYVal.add(pnAzimuth.y);listYVal.add(pnStart.y);listYVal.add(pnEnd.y);

	  // cellText
	 	if (_isShowText)
	 	{
	    	Point pnText = getAzimuthTextPoint(_graphics, azimuth, cellText, _textFont, pnAzimuth, 
	    			new Point(Collections.min(listXVal), Collections.min(listYVal)),
	    			new Point(Collections.max(listXVal), Collections.max(listYVal))); 
	    	
	    	AttributedString textAS = new AttributedString(cellText);
	    	textAS.addAttribute(TextAttribute.FONT, _textFont);
	    	if (_isBlurBackground) {
		    	
		    	textAS.addAttribute(TextAttribute.FOREGROUND, Color.WHITE);
		    	// blur효과용..
		    	_graphics.drawString(textAS.getIterator(), pnText.x+2, pnText.y);
		    	_graphics.drawString(textAS.getIterator(), pnText.x-2, pnText.y);
		    	_graphics.drawString(textAS.getIterator(), pnText.x, pnText.y+2);
		    	_graphics.drawString(textAS.getIterator(), pnText.x, pnText.y-2);
	    	}
	    	
	    	textAS.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
	    	_graphics.drawString(textAS.getIterator(), pnText.x,pnText.y);
	    	
	    	
			FontMetrics metrics = _graphics.getFontMetrics(_textFont);
			int textHeight = metrics.getHeight() ;

	    	
	    	listYVal.add(pnText.y + textHeight/2);
	 	}
	 	
	 	_maxYPos = Collections.max(listYVal);
	 	
	 	
	 	// siteText
    	if (_isShowText) {
	    	
			FontMetrics metrics = _graphics.getFontMetrics(_textFont);
			int textWidth = metrics.stringWidth(siteText);
			int textHeight = metrics.getHeight() ;
	
			int offsetY = 4;
			// omni랑 circle가 가장 하단인경우에...
			// 동그라미 표시한것보다 maxyPos가 작은 경우.
			if (_maxYPos < (_circleSize/2 + point.y)) {
				_maxYPos = _circleSize/2 + point.y + offsetY;
				
				//System.out.println(_textInfo + " " + String.valueOf(maxYPos));
			}
			
			if (angle == 360) {
				_maxYPos += offsetY;
			}
			
	    	Point pnText = new Point(point.x - textWidth/2, _maxYPos +textHeight/2);
			
	    	AttributedString textAS = new AttributedString(new String(siteText.getBytes("x-windows-949"),"euc-kr"));

	    	textAS.addAttribute(TextAttribute.FOREGROUND, Color.WHITE);
	    	textAS.addAttribute(TextAttribute.FONT, _textFont);

	    	// blur효과용..
	    	if (_isBlurBackground) {
	    		_graphics.drawString(textAS.getIterator(), pnText.x+1, pnText.y);
	    		_graphics.drawString(textAS.getIterator(), pnText.x-1, pnText.y);
	    		_graphics.drawString(textAS.getIterator(), pnText.x, pnText.y+1);
	    		_graphics.drawString(textAS.getIterator(), pnText.x, pnText.y-1);
	    	}
	    	textAS.addAttribute(TextAttribute.FOREGROUND, new Color(120,129,223));

	    	_graphics.drawString(textAS.getIterator(), pnText.x,pnText.y);
    	}
	 }

    /**
     * site에 대한 text를 그릴 때 사용함.
     * @param lon
     * @param lat
     * @param siteText
     * @param cellText
     * @param azimuth
     * @param angle
     * @param sectorColor
     * @param arcSize
     */
    public void drawMapViewText(double lon, double lat, String siteText, String cellText, int azimuth, int angle, Color sectorColor, int arcSize){
    	Font textFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    	Point point = getPoint(lon, lat); // Lon/Lat Convert to Pixel X/Y
	     // 시계방향으로 같고, 시작지점이 들어오는 값은 12시기준, 계산식쪽은 3시기준..
	     double calcAzimuth = azimuth-90;
	     if (calcAzimuth<0) {
	     	calcAzimuth = 360 + calcAzimuth;
	     }
	     
	     Point pnAzimuth = new Point (
	    		 point.x + (int)(Math.cos(Math.toRadians(calcAzimuth)) * arcSize/2),
	    		 point.y + (int)(Math.sin(Math.toRadians(calcAzimuth)) * arcSize/2));
	     Point pnStart = new Point(
	    		 point.x + (int)(Math.cos(Math.toRadians(calcAzimuth-angle/2)) * arcSize/2),
	    		 point.y + (int)(Math.sin(Math.toRadians(calcAzimuth-angle/2)) * arcSize/2));
	     
	     Point pnEnd = new Point(
	    		 point.x + (int)(Math.cos(Math.toRadians(calcAzimuth+angle/2)) * arcSize/2),
	    		 point.y + (int)(Math.sin(Math.toRadians(calcAzimuth+angle/2)) * arcSize/2));
	     
	     // 위의 3점에서 min/max값을 구한다.
	     ArrayList<Integer> listXVal = new ArrayList<Integer>();
	     listXVal.add(pnAzimuth.x);listXVal.add(pnStart.x);listXVal.add(pnEnd.x);
	     ArrayList<Integer> listYVal = new ArrayList<Integer>();
	     listYVal.add(pnAzimuth.y);listYVal.add(pnStart.y);listYVal.add(pnEnd.y);
	     
	  // cellText
	 	if (_isShowText)
	 	{
	    	Point pnText = getAzimuthTextPoint(_graphics, azimuth, cellText, textFont, pnAzimuth, 
	    			new Point(Collections.min(listXVal), Collections.min(listYVal)),
	    			new Point(Collections.max(listXVal), Collections.max(listYVal))); 
	    	
	    	AttributedString textAS = new AttributedString(cellText);
	    	textAS.addAttribute(TextAttribute.FONT, textFont);
	    	if (_isBlurBackground) {
		    	
		    	textAS.addAttribute(TextAttribute.FOREGROUND, Color.WHITE);
		    	// blur효과용..
		    	_graphics.drawString(textAS.getIterator(), pnText.x+2, pnText.y);
		    	_graphics.drawString(textAS.getIterator(), pnText.x-2, pnText.y);
		    	_graphics.drawString(textAS.getIterator(), pnText.x, pnText.y+2);
		    	_graphics.drawString(textAS.getIterator(), pnText.x, pnText.y-2);
	    	}
	    	
	    	textAS.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
	    	_graphics.drawString(textAS.getIterator(), pnText.x,pnText.y);
	    	
	    	
			FontMetrics metrics = _graphics.getFontMetrics(textFont);
			int textHeight = metrics.getHeight() ;

	    	
	    	listYVal.add(pnText.y + textHeight/2);
	 	}
	 	
	 	_maxYPos = Collections.max(listYVal);
	 	
	 	
	 	// siteText
    	if (_isShowText) {
	    	
			FontMetrics metrics = _graphics.getFontMetrics(textFont);
			int textWidth = metrics.stringWidth(siteText);
			int textHeight = metrics.getHeight() ;
	
			int offsetY = 4;
			// omni랑 circle가 가장 하단인경우에...
			// 동그라미 표시한것보다 maxyPos가 작은 경우.
			if (_maxYPos < (_circleSize/2 + point.y)) {
				_maxYPos = _circleSize/2 + point.y + offsetY;
				
				//System.out.println(_textInfo + " " + String.valueOf(maxYPos));
			}
			
			if (angle == 360) {
				_maxYPos += offsetY;
			}
			
	    	Point pnText = new Point(point.x - textWidth/2, _maxYPos +textHeight/2);
			
	    	AttributedString textAS = new AttributedString(siteText);
	    	textAS.addAttribute(TextAttribute.FOREGROUND, Color.WHITE);
	    	textAS.addAttribute(TextAttribute.FONT, textFont);
	    	// blur효과용..
	    	if (_isBlurBackground) {
	    		_graphics.drawString(textAS.getIterator(), pnText.x+1, pnText.y);
	    		_graphics.drawString(textAS.getIterator(), pnText.x-1, pnText.y);
	    		_graphics.drawString(textAS.getIterator(), pnText.x, pnText.y+1);
	    		_graphics.drawString(textAS.getIterator(), pnText.x, pnText.y-1);
	    	}
	    	textAS.addAttribute(TextAttribute.FOREGROUND, new Color(0,0,255));
	    	_graphics.drawString(textAS.getIterator(), pnText.x,pnText.y);
    	}
	 }
    
    /**
     * 
     * @param g
     * @param azimuth // UI기준과 계산식의 기준이 달라서 , UI12시기준, 계산식 3시기준, 시계방향은 동일.
     * @param textVal
     * @param font
     * @param pnAzimuth
     * @param pnMin arc위치상(azimuth, start, end 점 기준) 
     * @param pnMax arc위치상(azimuth, start, end 점 기준)
     * @return 실제 표시할 위치..
     */
    public Point getAzimuthTextPoint(Graphics g, double azimuth, String textVal, Font font, Point pnAzimuth, Point pnMin, Point pnMax) {
    	
    	// 먼저 arc상의 튀어나오고 좀 들어간 부분의 위치를 다시 확인..
        double directionX = 0.5;
        double directionY = 0.5;
        
       if (azimuth>360) {
    	   azimuth = azimuth - 360;
       }
       if (azimuth < 0) {
    	   azimuth = 360 + azimuth;
       }
        
        
        // text의 사이즈 계산..
		FontMetrics metrics = g.getFontMetrics(font);
		int textWidth = metrics.stringWidth(textVal);
		int textHeight = metrics.getHeight() ;

		//여기서 arc의 위치를 고려해서 값 수정.
		   //x축
        // 45씩 기준잡아서 처리...
        // 좌측은 약간의 offset, 우측은 글자수 만큼 나머지는 중간위치...
        // 	0~ 45, 315~360  : 0.1
        // azimuth자체는 시계반대방향임..
        if ((azimuth >= 45 && azimuth < 135) ){
            directionX = 0;
            pnAzimuth.x = pnMax.x;
        }
        // (180-45)~(270-45)
        else if (azimuth >= 225 && azimuth < 315) {
        	directionX = -1;
        	pnAzimuth.x = pnMin.x;
        }        	
        else{
            directionX = -0.5;
            // 위치조정 필요없음..
        }


        // y축

        // 45~ 135 : -1
        // 225~315 : 0
        // 나머지 : -0.5
        if ((azimuth >= 0 && azimuth < 45) || (azimuth >= 315 && azimuth <= 360) )
        {
            directionY =0;
            pnAzimuth.y = pnMin.y;
        }
        else if (azimuth >= 135 && azimuth < 225 )
        {
            directionY = 1;  
            pnAzimuth.y = pnMax.y;
        }
        else {
        	directionY = 0.5;
            // 위치조정 필요없음..
        }
		
        Point retPoint = new Point();
        retPoint.x = (int)Math.floor((pnAzimuth.x + textWidth * directionX));
        retPoint.y = (int)Math.floor((pnAzimuth.y + textHeight * directionY));
    	return retPoint;
    }
    
    /**
     * java graphics에서는 3시기준 0도이고 반시계 방향으로 증가 
     * @param azimuth
     * @return
     */
    public double getJavaGraphicAzimuth(double azimuth) {
    	double javaAzimuth = 360 - azimuth + 90;
    	if (javaAzimuth > 360 ) {
    		javaAzimuth = javaAzimuth-360;
    	}
    	if (javaAzimuth < 0) {
    		javaAzimuth = 360-javaAzimuth;
    	}
    	return javaAzimuth;
    }
    
  //==================>##end ##Site 그리기

	
	private Color getColor (int colorVal) {
		
        int colorR = colorVal / (256 * 256);
        int remain1 = colorVal % (256 * 256);
        int colorG = remain1 / 256;
        int colorB = colorVal % 256;
        
        return new Color(colorR, colorG, colorB);
	}	
}

package com.hj.se.mapbase.model;

import com.hj.se.mapbase.utils.TileSystem;

/**
 *
 * @author owner
 *
 */
public class TileLonLatModel
{
    private TileSystem.PointD _lonlat1;
    private TileSystem.PointD _lonlat2;
    private int _levelOfDetail;

    /**
     *
     * @param lon1
     * @param lat1
     * @param lon2
     * @param lat2
     * @param levelOfDetail
     */
    public TileLonLatModel(double lon1 ,double lat1 ,double lon2 ,double lat2 , int levelOfDetail) {

        this._lonlat1 = new TileSystem.PointD(lon1, lat1);
        this._lonlat2 = new TileSystem.PointD(lon2, lat2);
        this._levelOfDetail = levelOfDetail;
    }

    /**
     *
     * @param lon
     * @param lat
     */
    public void setLonlat1(double lon, double lat) {
        this._lonlat1 = new TileSystem.PointD(lon, lat);
    }

    /**
     *
     * @param lon
     * @param lat
     */
    public void setLonlat2(double lon, double lat) {
        this._lonlat2 = new TileSystem.PointD(lon, lat);
    }

    public void setLevelOfDetail(int levelOfDetail) {
        this._levelOfDetail = levelOfDetail;
    }

    public TileSystem.PointD getLonlat1() {
        return this._lonlat1;
    }

    public TileSystem.PointD getLonlat2() {
        return this._lonlat2;
    }

    public int getLevelOfDetail() {
        return this._levelOfDetail;
    }
}
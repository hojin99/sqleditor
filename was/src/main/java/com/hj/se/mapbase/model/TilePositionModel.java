package com.hj.se.mapbase.model;

/**
 *
 * @author owner
 *
 */
public class TilePositionModel
{
    private int _tileX;
    private int _tileY;
    private int _levelOfDetail;

    /**
     *
     * @param tileX
     * @param tileY
     * @param levelOfDetail
     */
    public TilePositionModel(int tileX, int tileY, int levelOfDetail) {
        this._tileX = tileX;
        this._tileY = tileY;
        this._levelOfDetail = levelOfDetail;
    }

    public void setTileX(int tileX) {
        this._tileX = tileX;
    }

    public void setTileY(int tileY) {
        this._tileY = tileY;
    }

    public void setLevelOfDetail(int levelOfDetail) {
        this._levelOfDetail = levelOfDetail;
    }

    public int getTileX() {
        return this._tileX;
    }

    public int getTileY() {
        return this._tileY;
    }

    public int getLevelOfDetail() {
        return this._levelOfDetail;
    }
}

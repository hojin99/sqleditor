<template>
<div :id="mId"/>  
</template>

<script>
import 'ol/ol.css';
import Map from 'ol/Map';
import View from 'ol/View';
import Projection from 'ol/proj/Projection';
import {getCenter} from 'ol/extent';

import GeoJSON from 'ol/format/GeoJSON';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import {Circle as CircleStyle, Fill, Stroke, Style} from 'ol/style';
import ImageLayer from 'ol/layer/Image';
import Static from 'ol/source/ImageStatic';
import TileLayer from 'ol/layer/Tile';
import TileWMS from 'ol/source/TileWMS';
import XYZ from 'ol/source/XYZ';

import { defaults as defaultControls} from 'ol/control';

export default {
    mounted() {
        const extent = [0, 0, 1024, 968];
        const projection = new Projection({
            code: 'EPSG:3857',
            units: 'pixels',
            extent: extent,
        });

        const baseLayer =  new ImageLayer({
            source: new Static({
                attributions: '© <a href="https://xkcd.com/license.html">xkcd</a>',
                url: 'https://imgs.xkcd.com/comics/online_communities.png',
                projection: projection,
                imageExtent: extent,
            }),
        });

        const geojsonObject = {
        'type': 'FeatureCollection',
        'features': [
            {
                'type': 'Feature',
                'geometry': {
                    'type': 'Point',
                    'coordinates': [0, 0],
                },
            },  
            {
                'type': 'Feature',
                'geometry': {
                    'type': 'Point',
                    'coordinates': [1024, 968],
                },
            },                       
            {
                'type': 'Feature',
                'geometry': {
                    'type': 'LineString',
                    'coordinates': [
                    [100, 100],
                    [500, 500],
                    ],
                },
            },
          
        ],
        };

        // 스타일
        const styles = new Style({
            fill: new Fill({
                color: 'rgba(255, 255, 0, 0.6)',
            }),
            stroke: new Stroke({
                color: 'rgba(255, 0, 0, 0.7)',
                width: 3,
            }),
            image: new CircleStyle({ // Point
                radius: 10,
                stroke: new Stroke({
                color: 'rgba(0, 0, 0, 0.7)',
                }),
                fill: new Fill({
                color: 'rgba(255, 255, 0, 0.2)',
                }),
            }),                
        });
        // 데이터 소스        
        const vectorSource = new VectorSource({
            features: new GeoJSON().readFeatures(geojsonObject)
        });
        // 레이어 (Tile, Image, Vector, VectorTile)
        const vectorLayer = new VectorLayer({
            source: vectorSource,
            style: styles,
        });

        const tileLayer = new TileLayer({
            source: new TileWMS({
                url: 'http://localhost:10012/geoserver/hojin/wms',
                params: {'LAYERS': 'hojin:vStatic', 'TILED': true, 'CRS':'EPSG:3857', 'SRS':'EPSG:3857'},
                serverType: 'geoserver',
                // Countries have transparency, so do not fade tiles:
                transition: 0,
                crossOrigin: 'anoymous'
            }),
        });

        // http://localhost:9011/api/map/tile/18/131069/131072
        // http://localhost:9011/api/map/tile/19/262139/262147
        const xyzLayer = new TileLayer({
            source: new XYZ({
                    url:
                    'http://localhost:10011/api/map/tile/{z}/{y}/{x}',
                }),            
        });

        // 뷰
        const view = new View({
            center: getCenter(extent),
            zoom: 2, // maxZoom(기본: 28), zoomFactor(기본: 2), maxResolution(자동계산)
            maxZoom: 8,
            projection: projection,
        });

        // 맵
        new Map({
            controls: defaultControls({
                attribution : false,
                attributionOptions : false,
                rotate : false,
                zoom : false
            }),
            layers: [baseLayer, vectorLayer, tileLayer, xyzLayer], // 레이어 지정
            view : view, // 뷰 지정
            target: this.mId, // rendering 대상 element id 지정
        });

    },
    computed :{        
        mId() {
            return `om${this._uid}`;
        }
    },    

}
</script>

<style>

#map {
    width: 100%;
    height: 100%;
}


</style>
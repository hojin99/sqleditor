<template>
<div :id="mId"/>  
</template>

<script>
import 'ol/ol.css';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import TileWMS from 'ol/source/TileWMS';
import OSM from 'ol/source/OSM';
import { defaults as defaultControls} from 'ol/control';
import { defaults as  defaultInteractions } from 'ol/interaction';

export default {
    mounted() {
        // 레이어 (Tile, Image, Vector, VectorTile)
        const baseSource = new OSM();
        const tile = new TileLayer({
                    source: baseSource // 원격 레이어를 위한 데이터를 얻기위한 source
                });
         
        const tileLayer = new TileLayer({
            source: new TileWMS({
                url: 'http://localhost:10012/geoserver/topp/wms',
                params: {'LAYERS': 'topp:states', 'TILED': true},
                serverType: 'geoserver',
                // Countries have transparency, so do not fade tiles:
                transition: 0,
            }),
        });


        // 뷰
        const view = new View({
            center: [127, 35],
            zoom: 2, // maxZoom(기본: 28), zoomFactor(기본: 2), maxResolution(자동계산)
            // projection : 'EPSG:4326' // projection은 center나 map resolution 계산 단위의 좌표계 (기본값은 EPSG:3857)
        });

        // 맵
        new Map({
            controls: defaultControls({
                attribution : false,
                attributionOptions : false,
                rotate : false,
                zoom : false
            }),
            interaction : defaultInteractions({
                altShiftDragRotate : false,
                onFocusOnly : false,
                doubleClickZoom : false,
                keyboard : false,
                shiftDragZoom : false,
                dragPan : false,
                pinchRotate : false,
                pinchZoom : false,
                zoomDelta : false,
                mouseWheelZoom : false
            }),
            layers: [tile, tileLayer], // 레이어 지정
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
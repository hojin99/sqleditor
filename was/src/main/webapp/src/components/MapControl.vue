<template>
<div :id="mId"/>  
</template>

<script>
import 'ol/ol.css';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import { Control, OverviewMap, FullScreen, defaults as defaultControls} from 'ol/control';
import { defaults as  defaultInteractions } from 'ol/interaction';
// import MapControlCustom from './MapControlCustom.js';

class RotateNorthControl extends Control {
    /**
     * @param {Object} [opt_options] Control options.
     */
    constructor(opt_options) {
      const options = opt_options || {};
  
      const button = document.createElement('button');
      button.innerHTML = 'N';
  
      const element = document.createElement('div');
      element.className = 'rotate-north ol-unselectable ol-control';
      element.appendChild(button);
  
      super({
        element: element,
        target: options.target,
      });
  
      button.addEventListener('click', this.handleRotateNorth.bind(this), false);
    }
  
    handleRotateNorth() {

        alert('123');
    }
  }


export default {
    mounted() {
        // 레이어 (Tile, Image, Vector, VectorTile)
        const baseSource = new OSM();
        const tile = new TileLayer({
                    source: baseSource // 원격 레이어를 위한 데이터를 얻기위한 source
                });
        const overviewMapControl = new OverviewMap({
        layers: [
            new TileLayer({
            source: baseSource,
            }),
        ],
        });         
         
        // 뷰
        const view = new View({
            center: [127, 35],
            zoom: 2, // maxZoom(기본: 28), zoomFactor(기본: 2), maxResolution(자동계산)
            projection : 'EPSG:4326' // projection은 center나 map resolution 계산 단위의 좌표계 (기본값은 EPSG:3857)
        });

        // 맵
        new Map({
            controls: defaultControls().extend([new RotateNorthControl(), new FullScreen(), overviewMapControl]),
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
            layers: [tile], // 레이어 지정
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
<template>
<div>
    <v-btn @click="addVectorLayer">Add Vector Layer</v-btn><span style="margin: 0px 0px 0px 20px;" id="info">selected states:</span>
    <div :id="mId" class="map" />  
</div>
</template>

<script>
// https://openlayers.org/en/latest/examples/box-selection.html

import 'ol/ol.css';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import GeoJSON from 'ol/format/GeoJSON';
import OSM from 'ol/source/OSM';
import {Fill, Stroke, Style} from 'ol/style';
import { defaults as defaultControls} from 'ol/control';
import { DragBox, Select, defaults as  defaultInteractions } from 'ol/interaction';
import {platformModifierKeyOnly} from 'ol/events/condition';

export default {
    data() {
        return {
            map: undefined,
        }
    },
    mounted() {
        const tile = new TileLayer({
                    source: new OSM()
                });

        const view = new View({
            center: [127, 35],
            zoom: 2,
            // projection : 'EPSG:4326',
            // constrainRotation: 16,
        });

        this.map = new Map({
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
            layers: [tile],
            target: this.mId,
            view : view
        });
    },
    computed :{        
        mId() {
            return `om${this._uid}`;
        }
    },
    methods: {
        addVectorLayer() {
            // ################## Vector 레이어 추가 Start ##################
            // const style = new Style({
            //     fill: new Fill({
            //         color: '#eeeeee',
            //     }),
            // });

            const vectorSource = new VectorSource({
                    format: new GeoJSON(),
                    url: 'http://localhost:10012/geoserver/topp/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=topp%3Astates&maxFeatures=50&outputFormat=application%2Fjson',
                });
            const vector = new VectorLayer({
                source:vectorSource,
                // background: '#1a2b39',
                // style: function (feature) {
                //     const color = feature.get('COLOR_BIO') || '#eeeeee';
                //     style.getFill().setColor(color);
                //     return style;
                // },                
            });
            
            this.map.addLayer(vector);
            // ################## Vector 레이어 추가 End ##################

            // ################## Select 설정 Start ##################
            // Vector Feature를 선택하기 위한 Interaction

            // Select 시 Style
            const selectedStyle = new Style({
                fill: new Fill({
                    color: 'rgba(255, 255, 0, 0.6)',
                }),
                stroke: new Stroke({
                    color: 'rgba(0, 0, 0, 0.7)',
                    width: 2,
                }),
            });

            const select = new Select({
                style: function () {
                    return selectedStyle;
                },
            });
            this.map.addInteraction(select);

            // Collection<Feature> : 선택된 Feature 목록
            const selectedFeatures = select.getFeatures();
            // ################## Select 설정 End ##################

            // ################## Drag Box 설정 Start ##################
            // a DragBox interaction used to select features by drawing boxes
            const dragBox = new DragBox({
                condition: platformModifierKeyOnly, // Ctrl Key와 함께 Drag 시 동작
            });
            this.map.addInteraction(dragBox);

            const map = this.map;
            dragBox.on('boxend', function () {
                // dragBox에 의해서 선택된 범위 조회
                const extent = dragBox.getGeometry().getExtent();
                console.log('[boxend]extent', extent);

                // vectorsource에서 선택된 범위와 겹치는 features 조회
                const boxFeatures = vectorSource
                    .getFeaturesInExtent(extent);
                    // .filter((feature) => feature.getGeometry().intersectsExtent(extent));

                console.log('[boxend]boxFeatures', boxFeatures);

                // features that intersect the box geometry are added to the
                // collection of selected features

                // if the view is not obliquely rotated the box geometry and
                // its extent are equalivalent so intersecting features can
                // be added directly to the collection

                const rotation = map.getView().getRotation();
                const oblique = rotation % (Math.PI / 2) !== 0;

                console.log('[boxend]oblique', oblique);
                // when the view is obliquely rotated the box extent will
                // exceed its geometry so both the box and the candidate
                // feature geometries are rotated around a common anchor
                // to confirm that, with the box geometry aligned with its
                // extent, the geometries intersect
                if (oblique) {
                    const anchor = [0, 0];
                    const geometry = dragBox.getGeometry().clone();
                    geometry.rotate(-rotation, anchor);
                    const extent = geometry.getExtent();
                    boxFeatures.forEach(function (feature) {
                        const geometry = feature.getGeometry().clone();
                        geometry.rotate(-rotation, anchor);
                        if (geometry.intersectsExtent(extent)) {
                            selectedFeatures.push(feature);
                        }
                    });
                } else {
                    selectedFeatures.extend(boxFeatures);
                }
            });

            // clear selection when drawing a new box and when clicking on the map
            dragBox.on('boxstart', function () {
                selectedFeatures.clear();
            });            
            // ################## Drag Box 설정 End ##################

            // ################## Info Box 설정 Start ##################
            const infoBox = document.getElementById('info');

            selectedFeatures.on(['add', 'remove'], function () {
                const names = selectedFeatures.getArray().map(function (feature) {
                    return feature.get('STATE_NAME');
                });

                if (names.length > 0) {
                    infoBox.innerHTML = 'selected states:' + names.join(', ');
                } else {
                    infoBox.innerHTML = 'selected states:None';
                }
            });
            // ################## Info Box 설정 End ##################

        }
    }
}
</script>

<style>

.map {
    width: 100%;
    height: 900px;
}


</style>
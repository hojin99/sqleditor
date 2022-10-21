<template>
  <v-app>
    <v-app-bar
      app
      color="primary"
      dark
    >
      <div class="d-flex align-center">
        Openlayers
      </div>

      <v-tabs
        v-model="tab"
        align-with-title
      >
        <v-tabs-slider color="yellow"></v-tabs-slider>

        <v-tab
          v-for="item in items"
          :key="item.title"
        >
          {{ item.title }}
        </v-tab>
      </v-tabs>

      <v-spacer></v-spacer>


    </v-app-bar>

    <v-main>
      <v-tabs-items v-model="tab">
        <v-tab-item
          v-for="item in items"
          :key="item.title"
        >
          <component style="width:100%; height:900px" :is="item.comp"></component>
        </v-tab-item>
      </v-tabs-items>

    </v-main>
  </v-app>
</template>

<script>
import MapBasic from './components/MapBasic';
import MapControl from './components/MapControl';
import MapVector from './components/MapVector';
import MapCustom from './components/MapCustom';

export default {
  name: 'App',

  components: {
    MapBasic,
    MapControl,
    MapVector,
    MapCustom,
  },

  data() {
    return {
      tab:null,
      items:[
        {"title":'basic', "comp":"MapBasic"},
        {"title":'control', "comp":"MapControl"},
        {"title":'vector', "comp":"MapVector"},
        {"title":'custom', "comp":"MapCustom"},

      ]
    }
  },
};
</script>

<style>


</style>
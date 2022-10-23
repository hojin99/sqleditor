<template>
  <div>
    <sql-editor></sql-editor>
  </div>
</template>

<script>
import SqlEditor from './components/sqleditor/SqlEditorFrame.vue';

export default {
  name: 'App',

  components: {
    SqlEditor,
  },

  data() {
    return {
      tab:null,
      items:[
        {"title":'editor', "comp":"SqlEditor"},

      ]
    }
  },
};
</script>

<style>


</style>
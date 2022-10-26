<template>
  <div ref="core" style="height:100%"></div>
</template>

<script>
import * as monaco from 'monaco-editor';

export default {

  mounted() {
    this.init()
  },
  methods: {
    init() {

      this.editor = monaco.editor.create(this.$refs.core, {
        value: `const foo = () => 0;`,
        language: 'javascript',
        theme: 'vs-dark'
      });

    }
  }

}
</script>

<style>

</style>
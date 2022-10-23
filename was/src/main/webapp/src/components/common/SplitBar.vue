<template>
  <div class="hj-split">
    <slot />
  </div>
</template>

<script>
import Split from 'split.js';

export default {
  name: 'SplitBar',
  props: {
    options: { type: Object } 
  },
  mounted() {
    this.$nextTick(() => this.init());
  },
  beforeDestory() {
      if(this.split !== undefined)
        this.split.destroy();
  },
  watch: {
    options() {
      this.$nextTick(() => this.init());
    }
  },
  methods: {
    init() {
      if(this.split !== undefined)
        this.split.destroy();
      
      const elements = [];
      // $refs로 변경 필요
      for(let elem = this.$el.firstElementChild; elem; elem = elem.nextElementSibling)
        elements.push(elem);
      this.split = Split(elements, this.options);
    }
  }
}
</script>

<style lang='scss' scoped>
  .hj-split {
    > ::v-deep.gutter {
      &.gutter-horizontal {
        border-left: 1px solid $hj-line-color;
        border-right: 1px solid $hj-line-color;
        cursor: ew-resize;

        &:hover {
          background-color: #bfbfbf;
        }

      }
      &.gutter-vertical {
        border-top: 1px solid $hj-line-color;
        border-bottom: 1px solid $hj-line-color;
        cursor: ns-resize;

        &:hover {
          background-color: #bfbfbf;
        }

      }
    }  
  }

</style>
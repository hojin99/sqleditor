<template>
  <div class="hj-sql-editor-pane">
    <split-bar
        class="hj-outer-area"
        :options="outerSplitOptions"
    >
      <split-bar>
        <div class="hj-db-navigator-area">
          tree
        </div>
      </split-bar>
      <split-bar
        :options="tabSplitOptions"
      >
        <div class="hj-editor-area">
          editor
        </div>
        <div class="hj-output-area">
          grid
        </div>
      </split-bar>    
    </split-bar>
  </div>
</template>

<script>
import SplitBar from '../common/SplitBar.vue';

export default {
  components: { SplitBar },
  data() {
    return {
      outerSplitOptions: {
          cursor: 'ew-resize',
          direction: 'horizontal',
          gutterSize: 7,
          sizes: [ 20, 80 ],
          snapOffset: 0
      },
      tabSplitOptions: {
          cursor: 'ns-resize',
          direction: 'vertical',
          gutterSize: 7,
          sizes: [ 70, 30 ],
          snapOffset: 0
      },
    }
  }

}
</script>

<style lang="scss" scoped>
    .hj-sql-editor-pane {
        @include hj-absolute-fill();
        display: flex;
        flex-flow: column;

        > .hj-outer-area {
            flex: 1 1 auto;
            display: flex;
            flex-flow: row;

            > .hj-list-area {
                height: 100%;
                display: flex;
                flex-flow: column;

                > .hj-db-navigator-area {
                    height: 100%;

                    > .hj-db-navigator {
                        width: 100%;
                        height: 100%;
                    }
                }
            }
            > .hj-tab-area {
                height: 100%;
                display: flex;
                flex-flow: column;

                > .hj-editor-area {
                    width: 100%;

                    > .hj-editor {
                        width: 100%;
                        height: 100%;
                    }
                }
                > .hj-output-area {
                    width: 100%;

                    > .hj-output {
                        width: 100%;
                        height: 100%;
                    }
                }
            }
        }

    }
</style>
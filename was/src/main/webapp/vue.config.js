const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  css: {
    loaderOptions: {
      sass: {
        additionalData : `@import "@/assets/css/common.scss";`
      }
    }
  },
  devServer: {
    proxy: {
      '^/api/': {
        target: 'http://localhost:10011',
      }
    },
    host: 'localhost',
    port: 10013
}
})

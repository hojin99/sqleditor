const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
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

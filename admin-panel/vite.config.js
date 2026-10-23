import { defineConfig } from 'vite'

export default defineConfig({
  base: '/admin/',
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://45.41.204.21:3000',
        changeOrigin: true
      }
    }
  }
})

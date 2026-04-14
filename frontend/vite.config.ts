import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig } from "vite";

export default defineConfig({
  plugins: [tailwindcss(), reactRouter()],
  resolve: {
    tsconfigPaths: true,
  },
  server: {
    proxy: {
      "/api/v1": {
        target: "https://localhost:8443/api/v1",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/v1/, ""),
        secure: false
      },
    },
  },
});

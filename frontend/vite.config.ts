import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig } from "vite";

const routerBasename = process.env.REACT_ROUTER_BASENAME ?? "/new";
const viteBase = routerBasename === "/"
  ? "/"
  : routerBasename.endsWith("/")
    ? routerBasename
    : `${routerBasename}/`;

export default defineConfig({
  base: viteBase,
  plugins: [tailwindcss(), reactRouter()],
  resolve: {
    tsconfigPaths: true,
  },
  server: {
    proxy: {
      "/api": {
        target: "https://localhost:8443",
        changeOrigin: true,
        secure: false
      }
    },
  },
});

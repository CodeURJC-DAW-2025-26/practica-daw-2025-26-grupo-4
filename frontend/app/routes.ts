import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  index("routes/home.tsx"),
  route("product/:id", "routes/product.tsx"),
  route("recommendations", "routes/recommendations.tsx"),
  route("user", "routes/user.tsx"),
  route("login", "routes/login.tsx"),
  route("register", "routes/register.tsx")
] satisfies RouteConfig;

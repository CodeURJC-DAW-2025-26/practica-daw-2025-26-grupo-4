import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  index("routes/home.tsx"),
  route("admin", "routes/admin/dashboard.tsx"),
  route("admin/products", "routes/admin/products.tsx"),
  route("product/:id", "routes/product.tsx"),
  route("cart", "routes/cart.tsx"),
  route("orders", "routes/orders.tsx"),
  route("recommendations", "routes/recommendations.tsx"),
  route("user", "routes/user.tsx"),
  route("login", "routes/login.tsx"),
  route("register", "routes/register.tsx"),
] satisfies RouteConfig;

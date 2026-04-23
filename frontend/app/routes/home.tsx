import type { Route } from "./+types/home";
import { getProducts } from "~/services/products-service";
import { Link, Form, useNavigate } from "react-router";
import { useCallback } from "react";
import { useInfiniteScroll } from "~/hooks/useInfiniteScroll";
import { useCart } from "~/hooks/useCart";
import { Header } from "~/components/header";
import { Footer } from "~/components/footer";

import "~/styles/tokens.css";
import "~/styles/components.css";
import "~/styles/home.css";
import { getCategories } from "~/services/category-service";
import { getRecommendations } from "~/services/recommendation-service";

export function links(): Route.LinkDescriptors {
  return [
    {
      rel: "stylesheet",
      href: "https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap",
    },
    {
      rel: "stylesheet",
      href: "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css",
    },
  ];
}

export async function clientLoader({ request }: Route.ClientLoaderArgs) {
  const url = new URL(request.url);
  const q = url.searchParams.get("q");
  let categoryId = url.searchParams.get("categoryId");

  const categories = await getCategories();

  if (!categoryId && categories && categories.length > 0) {
    categoryId = String(categories[0].id);
  }

  // Load the first page (0) with 9 items
  const initialPage = await getProducts(0, 9, q, categoryId);

  // Recommendations logic matching backends logic for root page
  let popularProducts = [];
  if (!q && !url.searchParams.get("categoryId")) {
    try {
      const recRes = await getRecommendations();
      // On the home page we only want singles (singleProduct == true) to simulate best-sellers in the top row
      // We will map their singleProduct directly.
      popularProducts = recRes.recommendations
        .filter((item: any) => !item.isCombo && item.products?.length > 0)
        .map((item: any) => item.products[0])
        .slice(0, 3);
    } catch (e) {
      console.error("Failed to fetch recommendations", e);
    }
  }

  return { initialPage, q, categoryId, categories, popularProducts };
}

export default function Home({ loaderData }: Route.ComponentProps) {
  const { initialPage, q, categoryId, categories, popularProducts } =
    loaderData;

  const { addItem } = useCart();
  const navigate = useNavigate();

  const handleAddToCart = async (productId: number) => {
    try {
      await addItem(productId, 1);
    } catch (err: any) {
      if (err.message.includes("UNAUTHORIZED")) {
        navigate("/login");
      } else {
        alert("Error al añadir producto");
      }
    }
  };

  // Encapsulate the dynamic product request in a callback
  const fetchMoreProducts = useCallback(
    (pageIdx: number) => getProducts(pageIdx, 9, q, categoryId),
    [q, categoryId],
  );

  // Reuse the custom hook:
  const {
    items: products,
    loading,
    hasMore,
    observerTarget,
  } = useInfiniteScroll(
    fetchMoreProducts,
    initialPage,
    [initialPage, q, categoryId], // Reset when initialPage changes
  );

  return (
    <div className="app-container">
      <Header />

      <div className="content-row">
        <div className="sidebar-column">
          <aside className="sidebar">
            <h3 className="menu-title">Categorías</h3>
            <nav className="menu">
              {categories.map((category: any) => {
                const isActive = categoryId === String(category.id);
                return (
                  <Link
                    to={`/?categoryId=${category.id}`}
                    className={`menu-item ${isActive ? "menu-item--active" : ""}`}
                    key={category.id}
                  >
                    <i className={`fa-solid ${category.icon}`}></i>{" "}
                    {category.name}
                  </Link>
                );
              })}
              <hr className="menu-divider" />
              <Link to="/recommendations" className="menu-item menu-item--rec">
                <i className="fa-solid fa-wand-magic-sparkles"></i>{" "}
                Recomendaciones
              </Link>
            </nav>
          </aside>
        </div>

        <main className="main-content">
          <div className="content-body">
            <div className="page-title-row">
              <h1 className="page-title">
                {categoryId
                  ? categories.find((c: any) => String(c.id) === categoryId)
                      ?.name || "Productos"
                  : "Todos los productos"}
              </h1>
              <Form
                className="search-bar search-bar--home"
                action="/"
                method="get"
              >
                <i className="fa-solid fa-magnifying-glass"></i>
                <input type="text" name="q" placeholder="Buscar productos" />
              </Form>
            </div>
            <p className="page-subtitle">Nuestra colección de plantas</p>

            <div className="product-grid" id="product-grid">
              {products && products.length > 0 ? (
                products.map((product: any) => (
                  <div className="product-card" key={product.id}>
                    <Link to={`/product/${product.id}`}>
                      <div className="image-container">
                        <img
                          src={product.images?.[0]?.url}
                          alt={product.name}
                        />
                      </div>
                      <div className="card-details">
                        <h3>{product.name}</h3>
                        <p className="description">{product.description}</p>
                        <div className="tags">
                          {product.tags &&
                            product.tags.map((tag: string) => (
                              <span className="tag" key={tag}>
                                {tag}
                              </span>
                            ))}
                        </div>
                        <div className="price">
                          €{product.formattedPrice || product.price}
                        </div>
                        <div className="card-actions">
                          <button
                            onClick={(e) => {
                              e.preventDefault();
                              e.stopPropagation();
                              handleAddToCart(product.id);
                            }}
                            className="btn btn--primary btn--sm btn--grow"
                          >
                            <i className="fa-solid fa-cart-plus"></i> Añadir al
                            carrito
                          </button>
                        </div>
                      </div>
                    </Link>
                  </div>
                ))
              ) : (
                <p>No hay productos disponibles.</p>
              )}
            </div>

            <div
              id="products-sentinel"
              className="scroll-sentinel"
              ref={observerTarget}
            ></div>
            {loading && (
              <div
                id="products-spinner"
                className="scroll-spinner"
                style={{ display: "flex" }}
              >
                <i className="fa-solid fa-spinner"></i> Cargando más
                productos...
              </div>
            )}
            {!hasMore && products.length > 0 && (
              <p
                style={{
                  textAlign: "center",
                  marginTop: "20px",
                  color: "gray",
                }}
              >
                No hay más productos.
              </p>
            )}
          </div>
        </main>
      </div>

      <Footer />
    </div>
  );
}

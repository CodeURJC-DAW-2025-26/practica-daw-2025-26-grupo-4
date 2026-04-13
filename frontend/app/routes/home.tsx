import type { Route } from "./+types/home";
import { getProducts } from "~/services/products-service";
import { Link, Form } from "react-router";
import { useCallback } from "react";
import { useInfiniteScroll } from "~/hooks/useInfiniteScroll";

import "~/styles/tokens.css";
import "~/styles/components.css";
import "~/styles/home.css";

export function links(): Route.LinkDescriptors {
  return [
    { rel: "stylesheet", href: "https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" },
    { rel: "stylesheet", href: "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" },
  ];
}

export async function clientLoader({ request }: Route.ClientLoaderArgs) {
  const url = new URL(request.url);
  const q = url.searchParams.get("q");
  const categoryId = url.searchParams.get("categoryId");

  // Load the first page (0) with 9 items
  const initialPage = await getProducts(0, 9, q, categoryId);
  return { initialPage, q, categoryId };
}

export default function Home({ loaderData }: Route.ComponentProps) {
  const { initialPage, q, categoryId } = loaderData;

  // Encapsulate the dynamic product request in a callback
  const fetchMoreProducts = useCallback(
    (pageIdx: number) => getProducts(pageIdx, 9, q, categoryId),
    [q, categoryId]
  );

  // Reuse the custom hook:
  const {
    items: products,
    loading,
    hasMore,
    observerTarget
  } = useInfiniteScroll(
    fetchMoreProducts,
    initialPage,
    [initialPage, q, categoryId] // Reset when initialPage changes
  );

  return (
    <div className="app-container">

      <div className="content-row">
        <div className="sidebar-column">
          <aside className="sidebar">
            <h3 className="menu-title">Categorías</h3>
            <nav className="menu">
              <Link to="/?categoryId=1" className="menu-item">
                <i className="fa-solid fa-leaf"></i> Plantas de Interior
              </Link>
              <Link to="/?categoryId=2" className="menu-item">
                <i className="fa-solid fa-tree"></i> Plantas de Exterior
              </Link>
              <hr className="menu-divider" />
              <Link to="/recommendations" className="menu-item menu-item--rec">
                <i className="fa-solid fa-wand-magic-sparkles"></i> Recomendaciones
              </Link>
            </nav>
          </aside>
        </div>

        <main className="main-content">
          <div className="content-body">
            <div className="page-title-row">
              <h1 className="page-title">Todos los productos</h1>
              <Form className="search-bar search-bar--home" action="/" method="get">
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
                          {product.tags && product.tags.map((tag: string) => (
                            <span className="tag" key={tag}>{tag}</span>
                          ))}
                        </div>
                        <div className="price">€{product.formattedPrice || product.price}</div>
                        <div className="card-actions">
                          <Form action="/cart/add" method="post" style={{ display: "contents" }}>
                            <input type="hidden" name="productId" value={product.id} />
                            <button type="submit" className="btn btn--primary btn--sm btn--grow">
                              <i className="fa-solid fa-cart-plus"></i> Añadir al carrito
                            </button>
                          </Form>
                        </div>
                      </div>
                    </Link>
                  </div>
                ))
              ) : (
                <p>No hay productos disponibles.</p>
              )}
            </div>

            <div id="products-sentinel" className="scroll-sentinel" ref={observerTarget}></div>
            {loading && (
              <div id="products-spinner" className="scroll-spinner" style={{ display: 'flex' }}>
                <i className="fa-solid fa-spinner"></i> Cargando más productos...
              </div>
            )}
            {!hasMore && products.length > 0 && (
              <p style={{ textAlign: 'center', marginTop: '20px', color: 'gray' }}>No hay más productos.</p>
            )}
          </div>
        </main>
      </div>
    </div>
  );
}

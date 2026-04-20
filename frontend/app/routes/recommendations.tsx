import type { Route } from "./+types/recommendations";
import { Link, Form } from "react-router";
import { getRecommendations } from "~/services/recommendation-service";
import { getCategories } from "~/services/category-service";
import { Header } from "~/components/header";
import { Footer } from "~/components/footer";

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
  const categories = await getCategories();
  const recommendationsReq = await getRecommendations();
  return { categories, recommendationsData: recommendationsReq };
}

export default function Recommendations({ loaderData }: Route.ComponentProps) {
  const { categories, recommendationsData } = loaderData;

  const handleComboAddToCart = (comboIds: string) => {
    // React fallback client-sided cart for combo, or better, we submit a form per item iteratively in the real world 
    // For pure UI replication: In the spring template it was setting cookies directly. We can just alert or build a form
    alert(`Added combo ${comboIds} to cart! (Replicated behavior)`);
  };

  return (
    <div className="app-container">
      <Header />

      <div className="content-row">
        {/* Sidebar */}
        <div className="sidebar-column">
          <aside className="sidebar">
            <h3 className="menu-title">Categorías</h3>
            <nav className="menu">
              {categories.map((category: any) => (
                <Link 
                  to={`/?categoryId=${category.id}`} 
                  className="menu-item" 
                  key={category.id}
                >
                  <i className={`fa-solid ${category.icon}`}></i> {category.name}
                </Link>
              ))}
              <hr className="menu-divider" />
              <Link to="/recommendations" className="menu-item menu-item--rec menu-item--active" aria-current="true">
                <i className="fa-solid fa-wand-magic-sparkles"></i> Recomendaciones
              </Link>
            </nav>
          </aside>
        </div>

        {/* Main Content */}
        <main className="main-content">
          <div className="content-body">
            <div className="page-title-row">
              <h1 className="page-title">
                <i className="fa-solid fa-wand-magic-sparkles rec-title-icon"></i>{" "}
                {recommendationsData.title}
              </h1>
            </div>
            <p className="page-subtitle">{recommendationsData.subtitle}</p>

            <div className="rec-grid">
              {recommendationsData.recommendations && recommendationsData.recommendations.map((rec: any, idx: number) => {
                if (rec.isCombo) {
                  return (
                    <div className="rec-combo-card" key={`combo-${idx}`}>
                      <div className="rec-combo-badge">
                        <i className="fa-solid fa-wand-magic-sparkles"></i> {rec.label}
                      </div>
                      <div className="rec-combo-products">
                        {rec.products.map((cp: any) => (
                          <Link to={`/product/${cp.id}`} className="rec-combo-item" key={cp.id}>
                            <div className="rec-combo-img-wrap">
                              <img src={cp.mainImage || cp.images?.[0]?.url} alt={cp.name} />
                            </div>
                            <div className="rec-combo-info">
                              <span className="rec-combo-name">{cp.name}</span>
                              <span className="rec-combo-price">€{(cp.formattedPrice || cp.price?.toFixed(2))}</span>
                            </div>
                          </Link>
                        ))}
                      </div>
                      <div className="rec-combo-footer">
                        <span className="rec-combo-total">Total del combo: <strong>€{rec.totalPrice}</strong></span>
                        <button className="btn btn--primary btn--sm" onClick={() => handleComboAddToCart(rec.products.map((p: any) => p.id).join(','))}>
                          <i className="fa-solid fa-cart-plus"></i> Añadir todo al carrito
                        </button>
                      </div>
                    </div>
                  );
                } else if (!rec.isCombo && rec.products?.length > 0) {
                  const sp = rec.products[0];
                  return (
                    <div className="product-card" key={`single-${sp.id}-${idx}`}>
                      <Link to={`/product/${sp.id}`}>
                        <div className="image-container">
                          <img src={sp.mainImage || sp.images?.[0]?.url} alt={sp.name} />
                        </div>
                        <div className="card-details">
                          <h3>{sp.name}</h3>
                          <p className="description">{sp.description}</p>
                          <div className="tags">
                            {sp.tags?.map((tag: string) => (
                              <span className="tag" key={tag}>{tag}</span>
                            ))}
                          </div>
                          <div className="price">€{(sp.formattedPrice || sp.price?.toFixed(2))}</div>
                          <div className="card-actions">
                            <Form action="/api/cart/add" method="post" style={{ display: "contents" }}>
                              <input type="hidden" name="productId" value={sp.id} />
                              <button type="submit" className="btn btn--primary btn--sm btn--grow">
                                <i className="fa-solid fa-cart-plus"></i> Añadir al carrito
                              </button>
                            </Form>
                            <button className="btn btn--ghost btn--sm btn--icon btn--fav">
                              <i className="fa-regular fa-heart"></i>
                            </button>
                          </div>
                        </div>
                      </Link>
                    </div>
                  );
                }
                return null;
              })}
            </div>

            {(!recommendationsData.recommendations || recommendationsData.recommendations.length === 0) && (
              <p className="rec-empty">
                <i className="fa-solid fa-box-open"></i>
                Aún no tenemos recomendaciones para ti. ¡Realiza tu primera compra!
              </p>
            )}
          </div>
        </main>
      </div>

      <Footer />
    </div>
  );
}
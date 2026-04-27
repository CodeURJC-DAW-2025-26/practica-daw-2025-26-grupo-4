import { useState, useEffect, type SyntheticEvent } from "react";
import { Link, useNavigate } from "react-router";
import { useCart } from "~/hooks/useCart";
import { useAuth } from "~/hooks/useAuth";
import type { Route } from "./+types/product";
import { getProduct, getProducts } from "~/services/products-service";
import { orderService } from "~/services/order-service";
import { createReview, deleteReview, updateReview } from "~/services/review-service";
import { Header } from "~/components/header";
import { Footer } from "~/components/footer";
import type { ProductDTO, ReviewDTO } from "~/api/dtos";
import { notifyError } from "~/stores/global-notification-store";

import "~/styles/tokens.css";
import "~/styles/components.css";
import "~/styles/product.css";

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

export async function clientLoader({ params }: Route.LoaderArgs) {
  const productId = Number.parseInt(params.id, 10);
  const product = await getProduct(productId);

  let recommendedProduct: ProductDTO | null = null;

  if (product.category?.id) {
    // Try to get recommended from same category
    const catProducts = await getProducts(
      0,
      10,
      null,
      product.category.id.toString(),
    );
    const candidates = catProducts.content.filter(
      (p: ProductDTO) => p.id !== product.id,
    );
    if (candidates.length > 0) {
      recommendedProduct =
        candidates[Math.floor(Math.random() * candidates.length)];
    }
  }

  if (!recommendedProduct) {
    // Fallback
    const allProducts = await getProducts(0, 10);
    const candidates = allProducts.content.filter(
      (p: ProductDTO) => p.id !== product.id,
    );
    if (candidates.length > 0) {
      recommendedProduct =
        candidates[Math.floor(Math.random() * candidates.length)];
    }
  }

  return { product, recommendedProduct };
}

export default function Product({ loaderData }: Route.ComponentProps) {
  const { product, recommendedProduct } = loaderData as any;
  const navigate = useNavigate();
  const { addItem } = useCart();
  const { user } = useAuth();

  const handleAddToCart = async (productId: number, qtyToAdd: number) => {
    try {
      await addItem(productId, qtyToAdd);
    } catch (err: any) {
      if (err.message.includes("UNAUTHORIZED")) {
        navigate("/login");
      }
    }
  };

  const completeOrder = async () => {
    try {
      const order = await orderService.createDirectOrder({
        productId: product.id,
        quantity: qty,
      });

      if (order === null) {
        navigate("/login");
        return;
      }

      navigate("/orders");
    } catch (err: any) {
      if (err.message.includes("UNAUTHORIZED")) {
        navigate("/login");
      }
    }
  };

  const [qty, setQty] = useState(1);
  const [mainImageUrl, setMainImageUrl] = useState(
    product?.images && product.images.length > 0 ? product.images[0].url : "",
  );
  const [reviews, setReviews] = useState<ReviewDTO[]>(product.reviews || []);
  const [averageRating, setAverageRating] = useState(product.averageRating || 0);
  const [reviewRating, setReviewRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [reviewContent, setReviewContent] = useState("");
  const [isEditingReview, setIsEditingReview] = useState(false);
  const [isSubmittingReview, setIsSubmittingReview] = useState(false);

  useEffect(() => {
    // Reset state and image when navigating between products
    setQty(1);
    setMainImageUrl(
      product?.images && product.images.length > 0 ? product.images[0].url : "",
    );
    setReviews(product.reviews || []);
    setAverageRating(product.averageRating || 0);
    setReviewRating(0);
    setHoverRating(0);
    setReviewContent("");
    setIsEditingReview(false);
  }, [product]);

  const currentUserReview = user
    ? reviews.find((review) => review.userId === user.id) ?? null
    : null;

  useEffect(() => {
    if (currentUserReview) {
      if (!isEditingReview) {
        setReviewRating(currentUserReview.rating);
        setReviewContent(currentUserReview.content);
      }
    } else {
      setReviewRating(0);
      setReviewContent("");
      setIsEditingReview(false);
    }
  }, [currentUserReview, isEditingReview]);

  // Helper for averages
  const calcStars = (rating: number) => {
    const stars = [];
    const full = Math.floor(rating);
    const half = rating - full >= 0.5 ? 1 : 0;
    const empty = 5 - full - half;
    for (let i = 0; i < full; i++) stars.push("fa-solid fa-star");
    for (let i = 0; i < half; i++) stars.push("fa-solid fa-star-half-stroke");
    for (let i = 0; i < empty; i++) stars.push("fa-regular fa-star");
    return stars;
  };

  const avgStars = calcStars(averageRating || 0);

  // Helper for rating bars
  const processRatingBars = () => {
    let counts = [0, 0, 0, 0, 0];
    if (reviews && reviews.length > 0) {
      reviews.forEach((r: any) => {
        const idx = Math.floor(r.rating) - 1;
        if (idx >= 0 && idx < 5) counts[idx]++;
      });
    }
    const total = reviews.length || 1;
    return counts.map((c) => (c / total) * 100);
  };

  const percents = processRatingBars(); // [percent1, percent2, percent3, percent4, percent5]

  const handleChangeQty = (delta: number) => {
    setQty((prev) => Math.max(1, prev + delta));
  };

  const handleReviewSubmit = async (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (reviewRating === 0) {
      notifyError("Por favor, selecciona una puntuación para tu reseña.");
      return;
    }

    const trimmedContent = reviewContent.trim();
    if (!trimmedContent) {
      notifyError("Escribe un comentario para tu reseña.");
      return;
    }

    try {
      setIsSubmittingReview(true);

      if (currentUserReview && isEditingReview) {
        const updatedReview = await updateReview(currentUserReview.id, {
          productId: product.id,
          rating: reviewRating,
          content: trimmedContent,
        });

        const nextAverageRating =
          ((averageRating * reviews.length) - currentUserReview.rating + updatedReview.rating) /
          reviews.length;
        setReviews((currentReviews) =>
          currentReviews.map((review) => (review.id === updatedReview.id ? updatedReview : review)),
        );
        setAverageRating(nextAverageRating);
        setIsEditingReview(false);
      } else {
        const createdReview = await createReview({
          productId: product.id,
          rating: reviewRating,
          content: trimmedContent,
        });

        const previousReviewCount = reviews.length;
        const nextAverageRating =
          ((averageRating * previousReviewCount) + createdReview.rating) /
          (previousReviewCount + 1);
        setReviews((currentReviews) => [...currentReviews, createdReview]);
        setAverageRating(nextAverageRating);
      }

      setReviewContent("");
      setReviewRating(0);
      setHoverRating(0);
    } catch (error: any) {
      if (error.message?.includes("UNAUTHORIZED")) {
        navigate("/login");
        return;
      }

      notifyError(error.message || "No se pudo publicar la reseña");
    } finally {
      setIsSubmittingReview(false);
    }
  };

  const handleEditReview = () => {
    if (!currentUserReview) {
      return;
    }

    setReviewRating(currentUserReview.rating);
    setReviewContent(currentUserReview.content);
    setIsEditingReview(true);
  };

  const handleCancelEditReview = () => {
    if (currentUserReview) {
      setReviewRating(currentUserReview.rating);
      setReviewContent(currentUserReview.content);
    } else {
      setReviewRating(0);
      setReviewContent("");
    }

    setIsEditingReview(false);
    setHoverRating(0);
  };

  const handleDeleteReview = async () => {
    if (!currentUserReview) {
      return;
    }

    try {
      await deleteReview(currentUserReview.id);

      const remainingReviews = reviews.filter((review) => review.id !== currentUserReview.id);
      setReviews(remainingReviews);

      if (remainingReviews.length === 0) {
        setAverageRating(0);
      } else {
        const totalRating = remainingReviews.reduce((sum, review) => sum + review.rating, 0);
        setAverageRating(totalRating / remainingReviews.length);
      }

      setReviewRating(0);
      setReviewContent("");
      setIsEditingReview(false);
      setHoverRating(0);
    } catch (error: any) {
      if (error.message?.includes("UNAUTHORIZED")) {
        navigate("/login");
        return;
      }

      notifyError(error.message || "No se pudo eliminar la reseña");
    }
  };

  let reviewFormButtonLabel = "Publicar reseña";
  if (currentUserReview && isEditingReview) {
    reviewFormButtonLabel = isSubmittingReview ? "Guardando..." : "Guardar cambios";
  } else if (isSubmittingReview) {
    reviewFormButtonLabel = "Publicando...";
  }

  return (
    <div className="app-container">
      <Header />

      <main className="main-content">
        <div className="product-layout">
          {/* Gallery */}
          <section className="gallery">
            <div className="main-image">
              <img src={mainImageUrl} alt="Imagen principal del producto" />
            </div>
            <div className="thumbnails">
              {product.images?.map((img: any, i: number) => (
                <div
                  key={img.id}
                  className={`thumbnail ${img.url === mainImageUrl ? "active" : ""}`}
                  onClick={() => setMainImageUrl(img.url)}
                >
                  <img src={img.url} alt={`Miniatura ${i}`} />
                </div>
              ))}
            </div>
          </section>

          {/* Info */}
          <section className="product-info">
            <h1>{product.name}</h1>
            <p className="short-desc">{product.description}</p>

            <div className="tags">
              {product.tags?.map((tag: string, i: number) => (
                <span key={i} className="tag">
                  {tag}
                </span>
              ))}
            </div>

            <div className="price-row">
              <span className="price">€{(product.price * qty).toFixed(2)}</span>
            </div>

            <div className="quantity">
              <button type="button" onClick={() => handleChangeQty(-1)}>
                -
              </button>
              <span>{qty}</span>
              <button type="button" onClick={() => handleChangeQty(1)}>
                +
              </button>
            </div>

            <button
              type="button"
              className="btn-buy"
              style={{ display: "inline" }}
              onClick={completeOrder}
            >
              Comprar ahora
            </button>

            <button
              type="button"
              className="btn-cart"
              onClick={() => handleAddToCart(product.id, qty)}
            >
              Añadir al carrito
            </button>

            {recommendedProduct && (
              <div className="recommend">
                <h3>Completa tu pedido con</h3>
                <div className="recommend-card">
                  <Link
                    to={`/product/${recommendedProduct.id}`}
                    style={{
                      display: "flex",
                      alignItems: "center",
                      textDecoration: "none",
                      color: "inherit",
                      flexGrow: 1,
                    }}
                  >
                    <img
                      src={recommendedProduct.images?.[0]?.url || ""}
                      alt="Recomendado"
                    />
                    <div style={{ marginLeft: "15px" }}>
                      <strong>{recommendedProduct.name}</strong>
                      <div
                        className="tags small"
                        style={{ marginTop: "5px", marginBottom: "5px" }}
                      >
                        {recommendedProduct.tags
                          ?.slice(0, 2)
                          .map((tag: string, i: number) => (
                            <span
                              key={i}
                              className="tag"
                              style={{ padding: "4px 8px", fontSize: "0.7rem" }}
                            >
                              {tag}
                            </span>
                          ))}
                      </div>
                      <span className="rec-price">
                        €{recommendedProduct.price.toFixed(2)}/u
                      </span>
                    </div>
                  </Link>
                  <div className="rec-actions">
                    <button
                      type="button"
                      onClick={(e) => {
                        e.preventDefault();
                        handleAddToCart(recommendedProduct.id, 1);
                      }}
                    >
                      <i className="fa-solid fa-cart-plus"></i>
                    </button>
                  </div>
                </div>
              </div>
            )}
          </section>
        </div>

        {/* Reviews */}
        <section className="reviews">
          <div className="rating-summary">
            <span className="score">{averageRating.toFixed(1)} / 5</span>
            <div>
              <div className="stars">
                {avgStars.map((s, i) => (
                  <i key={i} className={s}></i>
                ))}
              </div>
            </div>
            <small>{reviews.length} valoraciones</small>
          </div>

          <div className="rating-bars">
            {[5, 4, 3, 2, 1].map((star, idx) => {
              // idx 0 -> percent5 (which is index 4 in percents array)
              const percentIdx = 4 - idx;
              return (
                <div key={star}>
                  <span>{star} ★</span>
                  <div className="bar">
                    <div
                      className="fill"
                      style={{ width: `${percents[percentIdx]}%` }}
                    ></div>
                  </div>
                </div>
              );
            })}
          </div>

          {(!currentUserReview || isEditingReview) && (
            <div className="add-review-section">
              <h3>{currentUserReview ? "Edita tu reseña" : "Escribe tu reseña"}</h3>
              <form
                id="add-review-form"
                onSubmit={handleReviewSubmit}
              >
                <div className="form-group">
                  <label>Puntuación</label>
                  <div
                    className="star-rating"
                    onMouseLeave={() => setHoverRating(0)}
                  >
                    {[1, 2, 3, 4, 5].map((star) => (
                      <i
                        key={star}
                        className={
                          star <= (hoverRating || reviewRating)
                            ? "fa-solid fa-star"
                            : "fa-regular fa-star"
                        }
                        onMouseEnter={() => setHoverRating(star)}
                        onClick={() => setReviewRating(star)}
                      ></i>
                    ))}
                  </div>
                  <input
                    type="hidden"
                    name="rating"
                    value={reviewRating}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Comentario</label>
                  <textarea
                    name="content"
                    rows={4}
                    placeholder="Cuéntanos qué te ha parecido..."
                    value={reviewContent}
                    onChange={(event) => setReviewContent(event.target.value)}
                    required
                  ></textarea>
                </div>

                <button
                  type="submit"
                  className="save-btn"
                  style={{ marginTop: "15px" }}
                  disabled={isSubmittingReview}
                >
                  {reviewFormButtonLabel}
                </button>

                {currentUserReview && isEditingReview && (
                  <button
                    type="button"
                    className="btn-delete-review"
                    style={{ marginLeft: "12px", marginTop: "15px" }}
                    onClick={handleCancelEditReview}
                  >
                    Cancelar
                  </button>
                )}
              </form>
            </div>
          )}

          {currentUserReview && !isEditingReview && (
            <div className="user-review-section">
              <h3>Tu reseña</h3>
              <div className="comment user-comment">
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "start" }}>
                  <div>
                    <strong>{currentUserReview.authorName}</strong>
                    <div className="comment-stars">
                      {calcStars(currentUserReview.rating).map((s, i) => (
                        <i key={s + i} className={s}></i>
                      ))}
                    </div>
                  </div>
                  <div style={{ display: "flex", gap: "10px" }}>
                    <button type="button" className="btn-edit-review" onClick={handleEditReview}>
                      <i className="fa-solid fa-pen"></i> Editar
                    </button>
                    <button type="button" className="btn-delete-review" onClick={handleDeleteReview}>
                      <i className="fa-solid fa-trash"></i> Eliminar
                    </button>
                  </div>
                </div>
                <p>{currentUserReview.content}</p>
              </div>
            </div>
          )}

          <div className="comments" id="reviews-list">
            {reviews.map((review) => {
              const rStars = calcStars(review.rating);
              return (
                <div key={review.id} className="comment">
                  <strong>{review.authorName}</strong>
                  <div className="comment-stars">
                    {rStars.map((s, i) => (
                      <i key={i} className={s}></i>
                    ))}
                  </div>
                  <p>{review.content}</p>
                </div>
              );
            })}
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
}

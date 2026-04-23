import { useCart } from "~/hooks/useCart";
import { Header } from "~/components/header";
import { Footer } from "~/components/footer";
import { Link, useNavigate } from "react-router";
import type { Route } from "./+types/cart";
import { useEffect } from "react";

import "~/styles/tokens.css";
import "~/styles/components.css";
import "~/styles/cart.css";

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

export default function Cart() {
  const { cart, loading, error, addItem, removeItem, clearCart, checkout } =
    useCart();
  const navigate = useNavigate();

  const handleCheckout = async () => {
    try {
      await checkout();
      navigate("/order");
      alert("¡Pedido realizado con éxito!");
    } catch (err: any) {
      if (err.message.includes("UNAUTHORIZED")) {
        navigate("/login");
      } else {
        alert("Error al tramitar pedido");
      }
    }
  };

  const calculateTotal = () => {
    let count = 0;
    if (!cart || !cart.items) return "0.00";
    for (const item of cart.items) {
      count += item.product.price * item.quantity;
    }
    count += cart.shippingCost || 0;
    return count.toFixed(2);
  };

  const calculateSubTotal = () => {
    let count = 0;
    if (!cart || !cart.items) return "0.00";
    for (const item of cart.items) {
      count += item.product.price * item.quantity;
    }
    return count.toFixed(2);
  };

  return (
    <div className="app-container">
      <Header />

      <main className="main-content">
        <div className="content-body">
          <h1 className="page-title">Tu Carrito</h1>
          <p className="page-subtitle">
            Revisa tus plantas antes de llevarlas a casa
          </p>

          <div className="cart-layout">
            <div className="cart-items">
              {cart && cart.hasItems ? (
                cart.items.map((item) => (
                  <div className="cart-item" key={item.product.id}>
                    <Link
                      to={`/product/${item.product.id}`}
                      style={{
                        display: "contents",
                        textDecoration: "none",
                        color: "inherit",
                      }}
                    >
                      <div className="item-image">
                        <img
                          src={item.product.images?.[0]?.url}
                          alt={item.product.name}
                        />
                      </div>
                      <div className="item-details">
                        <h3>{item.product.name}</h3>
                        <div className="item-price">
                          €{(item.product.price * item.quantity).toFixed(2)}
                        </div>
                      </div>
                    </Link>
                    <div className="item-actions">
                      <div className="quantity-control">
                        <button
                          className="qty-btn"
                          onClick={() => removeItem(item.product.id)}
                        >
                          <i className="fa-solid fa-minus"></i>
                        </button>
                        <span className="qty-val">{item.quantity}</span>
                        <button
                          className="qty-btn"
                          onClick={() => addItem(item.product.id, 1)}
                        >
                          <i className="fa-solid fa-plus"></i>
                        </button>
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <div className="empty-cart-message">
                  <i
                    className="fa-solid fa-basket-shopping"
                    style={{
                      fontSize: "3rem",
                      color: "#ccc",
                      marginBottom: "1rem",
                    }}
                  ></i>
                  <p>No hay productos en el carrito.</p>
                  <Link
                    to="/"
                    className="btn-buy"
                    style={{
                      marginTop: "1rem",
                      display: "inline-block",
                      padding: "10px 20px",
                    }}
                  >
                    Ir a comprar
                  </Link>
                </div>
              )}
            </div>

            {cart && cart.hasItems && (
              <div className="cart-summary">
                <h2>Resumen del pedido</h2>

                <div className="summary-row">
                  <span>Subtotal</span>
                  <span>€{calculateSubTotal()}</span>
                </div>
                <div className="summary-row">
                  <span>Envío</span>
                  <span>{cart.shippingCost || "0.00"}</span>
                </div>
                <div className="summary-divider"></div>
                <div className="summary-row total">
                  <span>Total</span>
                  <span>€{calculateTotal()}</span>
                </div>

                <button className="checkout-btn" onClick={handleCheckout}>
                  Tramitar pedido <i className="fa-solid fa-arrow-right"></i>
                </button>
                <div style={{ textAlign: "center", marginTop: "10px" }}>
                  <button className="delete-btn" onClick={clearCart}>
                    <i className="fa-solid fa-trash"></i> Vaciar carrito
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}

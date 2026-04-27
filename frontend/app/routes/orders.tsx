import { Header } from "~/components/header";
import { Footer } from "~/components/footer";
import { orderService } from "~/services/order-service";
import type { OrderDTO, OrderItemDTO } from "~/api/dtos";
import type { Route } from "./+types/orders";
import { Link, useHref } from "react-router";
import { useState } from "react";

import "~/styles/tokens.css";
import "~/styles/components.css";
import "~/styles/orders.css";

type OrdersPageData = {
  orders: {
    content: OrderDTO[];
  } | null;
};

function formatOrderDate(rawDate: string): string {
  const date = new Date(rawDate);

  if (Number.isNaN(date.getTime())) {
    return rawDate;
  }

  return date.toLocaleDateString("es-ES", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

function getStatusClass(status: OrderDTO["status"]): string {
  if (status === "Entregado") {
    return "badge--success";
  }

  if (status === "En reparto") {
    return "badge--warning";
  }

  return "badge--info";
}

function calculateSubtotal(items: OrderItemDTO[]): number {
  return items.reduce((acc, item) => acc + item.price * item.quantity, 0);
}

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

export async function clientLoader() {
  const orders = await orderService.getOrders(0, 10);
  return { orders };
}

export default function OrderPage({ loaderData }: Route.ComponentProps) {
  const { orders } = loaderData as unknown as OrdersPageData;
  const brandLogoSrc = useHref("/images/logoDAW.png");
  const [expandedOrders, setExpandedOrders] = useState<number[]>([]);

  const toggleOrderDetails = (orderId: number) => {
    setExpandedOrders((current) =>
      current.includes(orderId)
        ? current.filter((id) => id !== orderId)
        : [...current, orderId],
    );
  };

  return (
    <div className="app-container orders-page">
      <Header />
      <main className="main-content">
        <div className="content-body">
          <h1 className="page-title">Mis pedidos</h1>

          {orders === null && (
            <div className="orders-empty-state">
              Debes iniciar sesión para ver tus pedidos.
            </div>
          )}

          {orders?.content.length === 0 && (
            <div className="orders-empty-state">Aún no tienes pedidos.</div>
          )}

          {orders?.content.map((order) => {
            const isExpanded = expandedOrders.includes(order.id);
            const subtotal = calculateSubtotal(order.items);

            return (
              <article
                className={`order-card ${isExpanded ? "" : "collapsed"}`}
                key={order.id}
              >
                <button
                  className="order-header"
                  type="button"
                  onClick={() => toggleOrderDetails(order.id)}
                  aria-expanded={isExpanded}
                  aria-controls={`order-details-${order.id}`}
                >
                  <div className="order-info">
                    <h3>Pedido #{order.orderNumber}</h3>
                    <p className="order-date">
                      Realizado el {formatOrderDate(order.date)}
                    </p>
                  </div>

                  <div className="order-status-right">
                    <span className={`badge ${getStatusClass(order.status)}`}>
                      {order.status}
                    </span>
                    <span className="order-total">
                      €{order.totalPrice.toFixed(2)}
                    </span>
                    <span className="product-count">
                      {order.items.length} productos
                    </span>
                  </div>
                </button>

                <div id={`order-details-${order.id}`} className="order-details">
                  {order.items.map((item) => (
                    <div
                      className="order-item"
                      key={`${order.id}-${item.productId}`}
                    >
                      <Link
                        to={`/product/${item.productId}`}
                        className="item-image"
                      >
                        <img
                          src={item.imageUrl || brandLogoSrc}
                          alt={item.name}
                        />
                      </Link>

                      <div className="item-info">
                        <Link to={`/product/${item.productId}`}>
                          <h4>{item.name}</h4>
                        </Link>
                        <p className="item-subtitle">
                          Cantidad: {item.quantity}
                        </p>
                        <p className="item-price">
                          €{item.price.toFixed(2)} c/u
                        </p>
                      </div>
                    </div>
                  ))}

                  <div className="order-summary">
                    <div className="summary-row">
                      <span>Subtotal</span>
                      <span>€{subtotal.toFixed(2)}</span>
                    </div>
                    <div className="summary-row">
                      <span>Envío</span>
                      <span className="free-shipping">
                        {order.shippingCost === 0
                          ? "Gratis"
                          : `€${order.shippingCost.toFixed(2)}`}
                      </span>
                    </div>
                    <div className="summary-total">
                      <span>Total</span>
                      <span>€{order.totalPrice.toFixed(2)}</span>
                    </div>
                  </div>
                </div>

                <button
                  className="expand-btn"
                  type="button"
                  onClick={() => toggleOrderDetails(order.id)}
                  aria-label={
                    isExpanded
                      ? "Ocultar productos del pedido"
                      : "Mostrar productos del pedido"
                  }
                >
                  <i
                    className={`fa-solid fa-chevron-${
                      isExpanded ? "up" : "down"
                    }`}
                  ></i>
                </button>
              </article>
            );
          })}
        </div>
      </main>
      <Footer />
    </div>
  );
}

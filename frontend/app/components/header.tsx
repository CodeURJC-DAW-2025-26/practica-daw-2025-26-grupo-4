import { Link, useHref, useNavigate } from "react-router";
import { useState, useEffect } from "react";
import { useAuth } from "~/hooks/useAuth";
import { useCart } from "~/hooks/useCart";
import { useAuthStore } from "~/stores/auth-store";
import "~/styles/header.css";

export function Header() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const navigate = useNavigate();
  const brandLogoSrc = useHref("/images/logoDAW.png");
  const { isLogged, isAdmin, user, loading } = useAuth();
  const clearSession = useAuthStore((state) => state.clearSession);
  const { cart } = useCart();

  const [animateCart, setAnimateCart] = useState(false);
  const cartItemsCount =
    cart?.items?.reduce((total, item) => total + item.quantity, 0) || 0;

  useEffect(() => {
    if (cartItemsCount > 0) {
      setAnimateCart(true);
      const timer = setTimeout(() => setAnimateCart(false), 300);
      return () => clearTimeout(timer);
    }
  }, [cartItemsCount]);

  const handleLogout = async () => {
    try {
      await fetch("/api/v1/auth/logout", {
        method: "POST",
        credentials: "include",
      });
      clearSession();
      setIsMenuOpen(false);
      navigate("/");
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  return (
    <header className="top-bar">
      <div className="top-bar__left">
        <Link to="/" className="logo-link">
          <div className="logo logo--header">
            <img
              className="brand-logo"
              src={brandLogoSrc}
              alt="Logo PlantaZon"
            />
          </div>
        </Link>
      </div>
      <div className="top-bar__right">
        <div className="top-bar__spacer"></div>
        <div className="user-actions">
          <div className={`cart-icon-container ${animateCart ? "bump" : ""}`}>
            <Link to="/cart">
              <i className="fa-solid fa-cart-shopping"></i>
              {cartItemsCount > 0 && (
                <span className="cart-badge">{cartItemsCount}</span>
              )}
            </Link>
          </div>
          <div className="user-menu-container">
            <button
              className="user-menu-btn"
              onClick={() => setIsMenuOpen(!isMenuOpen)}
            >
              {isLogged && user?.profileImageUrl ? (
                <img
                  src={user.profileImageUrl}
                  alt="Foto de perfil"
                  className="header-profile-img"
                />
              ) : (
                <i className="fa-regular fa-circle-user profile-icon"></i>
              )}
            </button>
            <div
              className={`user-menu-dropdown ${isMenuOpen ? "active" : ""}`}
              onClick={(event) => event.stopPropagation()}
            >
              {!loading && (
                <>
                  {isAdmin && (
                    <>
                      <Link to="/admin" className="dropdown-item">
                        <i className="fa-solid fa-gauge"></i> Panel de
                        administración
                      </Link>
                      <Link to="/admin/products" className="dropdown-item">
                        <i className="fa-solid fa-boxes-stacked"></i> Gestor de
                        productos
                      </Link>
                    </>
                  )}
                  {isLogged && !isAdmin && (
                    <>
                      <Link to="/user" className="dropdown-item">
                        <i className="fa-solid fa-user"></i> Ver perfil
                      </Link>
                      <Link to="/orders" className="dropdown-item">
                        <i className="fa-solid fa-bag-shopping"></i> Ver mis
                        pedidos
                      </Link>
                    </>
                  )}
                  {!isLogged && (
                    <>
                      <Link to="/login" className="dropdown-item">
                        <i className="fa-solid fa-right-to-bracket"></i> Iniciar
                        sesión
                      </Link>
                      <Link to="/register" className="dropdown-item">
                        <i className="fa-solid fa-user-plus"></i> Registrarse
                      </Link>
                    </>
                  )}
                  {isLogged && (
                    <button
                      onClick={handleLogout}
                      className="dropdown-item logout-btn"
                      style={{
                        cursor: "pointer",
                        background: "none",
                        border: "none",
                        padding: "inherit",
                        width: "100%",
                        textAlign: "left",
                      }}
                    >
                      <i className="fa-solid fa-sign-out-alt"></i> Cerrar sesión
                    </button>
                  )}
                </>
              )}
            </div>
          </div>
        </div>
      </div>
    </header>
  );
}

import { Link } from "react-router";
import { useState } from "react";
import { useAuth } from "~/hooks/useAuth";
import "~/styles/header.css";

export function Header() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { isLogged, isAdmin, loading } = useAuth();

  const handleLogout = async () => {
    try {
      await fetch("/api/v1/auth/logout", {
        method: "POST",
        credentials: "include"
      });
      window.location.href = "/";
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  return (
    <header className="top-bar">
      <div className="top-bar__left">
        <Link to="/" className="logo-link">
          <div className="logo logo--header">
            <img className="brand-logo" src="/images/logoDAW.png" alt="Logo PlantaZon" />
          </div>
        </Link>
      </div>
      <div className="top-bar__right">
        <div className="top-bar__spacer"></div>
        <div className="user-actions">
          <div className="cart-icon-container">
            <Link to="/cart">
              <i className="fa-solid fa-cart-shopping"></i>
              <span className="cart-badge">0</span>
            </Link>
          </div>
          <div className="user-menu-container" onClick={() => setIsMenuOpen(!isMenuOpen)}>
            <button className="user-menu-btn">
              <i className="fa-regular fa-circle-user profile-icon"></i>
            </button>
            <div className={`user-menu-dropdown ${isMenuOpen ? "active" : ""}`}>
              {!loading && (
                <>
                  {isAdmin && (
                    <>
                      <a href="/admin" className="dropdown-item">
                        <i className="fa-solid fa-gauge"></i> Panel de administración
                      </a>
                      <a href="/admin/products" className="dropdown-item">
                        <i className="fa-solid fa-boxes-stacked"></i> Gestor de productos
                      </a>
                    </>
                  )}
                  {isLogged && !isAdmin && (
                    <>
                      <Link to="/user" className="dropdown-item">
                        <i className="fa-solid fa-user"></i> Ver perfil
                      </Link>
                      <Link to="/order" className="dropdown-item">
                        <i className="fa-solid fa-bag-shopping"></i> Ver mis pedidos
                      </Link>
                    </>
                  )}
                  {!isLogged && (
                    <>
                      <Link to="/login" className="dropdown-item">
                        <i className="fa-solid fa-right-to-bracket"></i> Iniciar sesión
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
                      style={{ cursor: "pointer", background: "none", border: "none", padding: "inherit", width: "100%", textAlign: "left" }}
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

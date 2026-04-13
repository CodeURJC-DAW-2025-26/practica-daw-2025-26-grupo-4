import { Link } from "react-router";
import { useState } from "react";
import "~/styles/header.css";

export function Header() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

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
              <Link to="/login" className="dropdown-item">
                <i className="fa-solid fa-right-to-bracket"></i> Iniciar sesión
              </Link>
              <Link to="/login?register=true" className="dropdown-item">
                <i className="fa-solid fa-user-plus"></i> Registrarse
              </Link>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
}

import { Link } from "react-router";
import "~/styles/footer.css";

export function Footer() {
  return (
    <footer className="site-footer">
      <div className="footer-content">
        <div className="footer-column">
          <h4>Acerca de nosotros</h4>
          <ul>
            <li><Link to="/quienes-somos">Quiénes somos</Link></li>
            <li><Link to="/nuestra-mision">Nuestra misión</Link></li>
            <li><Link to="/equipo">Equipo</Link></li>
          </ul>
        </div>
        <div className="footer-column">
          <h4>Redes sociales</h4>
          <ul>
            <li><a href="https://www.instagram.com/PlantazonInstagram" target="_blank" rel="noreferrer">Instagram</a></li>
            <li><a href="https://www.facebook.com/PlantazonFacebook" target="_blank" rel="noreferrer">Facebook</a></li>
            <li><a href="https://twitter.com/PlantazonTwitter" target="_blank" rel="noreferrer">Twitter</a></li>
          </ul>
        </div>
        <div className="footer-column">
          <h4>Información legal</h4>
          <ul>
            <li><Link to="/aviso-legal">Aviso legal</Link></li>
            <li><Link to="/politica-privacidad">Política de privacidad</Link></li>
            <li><Link to="/cookies">Cookies</Link></li>
          </ul>
        </div>
      </div>
      <div className="footer-bottom">
        <span>© 2026 PlantaZon. Todos los derechos reservados.</span>
      </div>
    </footer>
  );
}

import { useState } from "react";
import { Link, useHref, useNavigate } from "react-router";
import { loginUser, registerUser } from "~/services/auth-service";
import { useAuthStore } from "~/stores/auth-store";

import "~/styles/tokens.css";
import "~/styles/components.css";
import "~/styles/auth.css";

type AuthMode = "login" | "register";

type AuthPageProps = {
  initialMode: AuthMode;
};

type LoginFormState = {
  username: string;
  password: string;
};

type RegisterFormState = {
  fullName: string;
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
};

const EMPTY_LOGIN: LoginFormState = {
  username: "",
  password: ""
};

const EMPTY_REGISTER: RegisterFormState = {
  fullName: "",
  username: "",
  email: "",
  password: "",
  confirmPassword: ""
};

export function AuthPage({ initialMode }: AuthPageProps) {
  const navigate = useNavigate();
  const brandLogoSrc = useHref("/images/logoDAW.png");
  const loadSession = useAuthStore((state) => state.loadSession);
  const [mode, setMode] = useState<AuthMode>(initialMode);
  const [loginForm, setLoginForm] = useState<LoginFormState>(EMPTY_LOGIN);
  const [registerForm, setRegisterForm] = useState<RegisterFormState>(EMPTY_REGISTER);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const onLoginInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setLoginForm((prev) => ({ ...prev, [name]: value }));
  };

  const onRegisterInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setRegisterForm((prev) => ({ ...prev, [name]: value }));
  };

  const validateRegister = () => {
    if (!registerForm.fullName || !registerForm.username || !registerForm.email || !registerForm.password || !registerForm.confirmPassword) {
      return "Todos los campos son obligatorios";
    }

    if (registerForm.password.length < 6) {
      return "La contrasena debe tener al menos 6 caracteres";
    }

    if (registerForm.password !== registerForm.confirmPassword) {
      return "Las contrasenas no coinciden";
    }

    return null;
  };

  const onSubmitLogin = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setErrorMessage(null);
    setSubmitting(true);

    try {
      await loginUser({
        username: loginForm.username,
        password: loginForm.password
      });
      await loadSession(true);
      navigate("/");
    } catch (error) {
      console.error(error);
    } finally {
      setSubmitting(false);
    }
  };

  const onSubmitRegister = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setErrorMessage(null);

    const validationError = validateRegister();
    if (validationError) {
      setErrorMessage(validationError);
      return;
    }

    setSubmitting(true);

    try {
      await registerUser({
        fullName: registerForm.fullName,
        username: registerForm.username,
        email: registerForm.email,
        password: registerForm.password,
        confirmPassword: registerForm.confirmPassword
      });

      // Auto-login después del registro
      await loginUser({
        username: registerForm.username,
        password: registerForm.password
      });
      await loadSession(true);

      setRegisterForm(EMPTY_REGISTER);
      navigate("/");
    } catch (error) {
      console.error(error);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="auth-page">
      <Link to="/" className="back-home-btn">
        <i className="fa-solid fa-arrow-left"></i> Volver al inicio
      </Link>

      <div className="main-container">
        <div className={`login-card ${mode === "register" ? "mode-register" : "mode-login"}`}>
          <div className="header">
            <div className="logo-wrap brand-logo-wrap">
              <img className="logo brand-logo" src={brandLogoSrc} alt="Logo PlantaZon" />
            </div>
          </div>

          <div className="tabs">
            <button type="button" id="tab-login" className="tab-btn" onClick={() => setMode("login")}>Iniciar sesion</button>
            <button type="button" id="tab-register" className="tab-btn" onClick={() => setMode("register")}>Registrarse</button>
            <div className="tab-indicator"></div>
          </div>

          {errorMessage && <div className="alert alert-error">{errorMessage}</div>}

          <div className="forms-wrapper">
            <div className="forms-slider">
              <form className="auth-form" onSubmit={onSubmitLogin}>
                <div className="input-group">
                  <label>Usuario</label>
                  <input
                    type="text"
                    name="username"
                    placeholder="Tu usuario..."
                    value={loginForm.username}
                    onChange={onLoginInputChange}
                    required
                  />
                </div>

                <div className="input-group">
                  <label>Contrasena</label>
                  <input
                    type="password"
                    name="password"
                    placeholder="Tu contrasena..."
                    value={loginForm.password}
                    onChange={onLoginInputChange}
                    required
                  />
                </div>

                <button type="submit" disabled={submitting}>{submitting ? "Entrando..." : "Entrar"}</button>
              </form>

              <form className="auth-form" onSubmit={onSubmitRegister}>
                <div className="input-group">
                  <label>Nombre completo</label>
                  <input
                    type="text"
                    name="fullName"
                    placeholder="Tu nombre..."
                    value={registerForm.fullName}
                    onChange={onRegisterInputChange}
                    required
                  />
                </div>

                <div className="input-group">
                  <label>Usuario</label>
                  <input
                    type="text"
                    name="username"
                    placeholder="Tu usuario..."
                    value={registerForm.username}
                    onChange={onRegisterInputChange}
                    required
                  />
                </div>

                <div className="input-group">
                  <label>Email</label>
                  <input
                    type="email"
                    name="email"
                    placeholder="Tu email..."
                    value={registerForm.email}
                    onChange={onRegisterInputChange}
                    required
                  />
                </div>

                <div className="input-group">
                  <label>Contrasena</label>
                  <input
                    type="password"
                    name="password"
                    placeholder="Tu contrasena..."
                    value={registerForm.password}
                    onChange={onRegisterInputChange}
                    required
                  />
                </div>

                <div className="input-group">
                  <label>Confirmar contrasena</label>
                  <input
                    type="password"
                    name="confirmPassword"
                    placeholder="Confirma tu contrasena..."
                    value={registerForm.confirmPassword}
                    onChange={onRegisterInputChange}
                    required
                  />
                </div>

                <button type="submit" disabled={submitting}>{submitting ? "Creando..." : "Crear cuenta"}</button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}

import {
  isRouteErrorResponse,
  Links,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
  Link,
  useNavigation,
} from "react-router";
import { useEffect } from "react";
import type { ReactNode } from "react";

import type { Route } from "./+types/root";
import "bootstrap/dist/css/bootstrap.min.css";
import "./app.css";
import { GlobalSpinner } from "~/components/global-spinner";
import { GlobalNotifications } from "~/components/global-notifications";
import { useGlobalLoadingStore } from "~/stores/global-loading-store";
import { notifyError } from "~/stores/global-notification-store";
import { getApiErrorMessage } from "~/utils/api-error";

export function Layout({ children }: Readonly<{ children: ReactNode }>) {
  return (
    <html lang="en">
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <Meta />
        <Links />
      </head>
      <body>
        {children}
        <ScrollRestoration />
        <Scripts />
      </body>
    </html>
  );
}

export default function App() {
  const navigation = useNavigation();
  const setNavLoading = useGlobalLoadingStore((state) => state.setNavLoading);
  const startRequest = useGlobalLoadingStore((state) => state.startRequest);
  const finishRequest = useGlobalLoadingStore((state) => state.finishRequest);

  useEffect(() => {
    setNavLoading(navigation.state !== "idle");
  }, [navigation.state, setNavLoading]);

  useEffect(() => {
    const originalFetch = globalThis.fetch.bind(globalThis);

    globalThis.fetch = async (input: RequestInfo | URL, init?: RequestInit) => {
      const mergedHeaders = new Headers(
        init?.headers ?? (input instanceof Request ? input.headers : undefined)
      );
      const skipSpinner = mergedHeaders.get("x-skip-global-spinner") === "true";
      const skipGlobalError = mergedHeaders.get("x-skip-global-error") === "true";

      if (!skipSpinner) {
        startRequest();
      }

      try {
        const response = await originalFetch(input, init);

        if (!skipGlobalError && !response.ok && response.status !== 401 && response.status !== 404) {
          const fallbackMessage = response.statusText || "Se produjo un error en la solicitud";
          const message = await getApiErrorMessage(response, fallbackMessage);
          notifyError(message);
        }

        return response;
      } catch (error) {
        if (!skipGlobalError) {
          const message = error instanceof Error ? error.message : "No se pudo conectar con el servidor";
          notifyError(message);
        }

        throw error;
      } finally {
        if (!skipSpinner) {
          finishRequest();
        }
      }
    };

    return () => {
      globalThis.fetch = originalFetch;
    };
  }, [finishRequest, startRequest]);

  return (
    <>
      <GlobalSpinner />
      <GlobalNotifications />
      <Outlet />
    </>
  );
}

export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
  let code = "Error";
  let message = "Ha ocurrido un error inesperado";
  let details = "Intenta volver al inicio o recarga la pagina.";
  let iconClass = "fa-solid fa-circle-exclamation";
  let stack: string | undefined;

  if (isRouteErrorResponse(error)) {
    code = String(error.status);

    if (error.status === 404) {
      message = "Pagina no encontrada";
      details = "La pagina que buscas no existe o fue movida.";
      iconClass = "fa-solid fa-magnifying-glass";
    } else if (error.status === 403) {
      message = "Acceso denegado";
      details = "No tienes permisos para acceder a este contenido.";
      iconClass = "fa-solid fa-lock";
    } else {
      message = "Error de aplicacion";
      details = error.statusText || details;
      iconClass = "fa-solid fa-triangle-exclamation";
    }
  } else if (import.meta.env.DEV && error && error instanceof Error) {
    details = error.message;
    stack = error.stack;
  }

  return (
    <main className="spa-error-page">
      <div className="spa-error-card">
        <div className="spa-error-logo-wrap">
          <img className="spa-error-logo" src="/images/logoDAW.png" alt="Logo PlantaZon" />
        </div>
        <div className="spa-error-icon" aria-hidden="true">
          <i className={iconClass}></i>
        </div>
        <p className="spa-error-code">{code}</p>
        <h1 className="spa-error-title">{message}</h1>
        <p className="spa-error-details">{details}</p>
        <Link to="/" className="spa-error-cta">
          <i className="fa-solid fa-house"></i> Volver al inicio
        </Link>

        {stack && (
          <pre className="spa-error-stack">
            <code>{stack}</code>
          </pre>
        )}
      </div>
    </main>
  );
}

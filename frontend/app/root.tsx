import {
  isRouteErrorResponse,
  Links,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
  useNavigation,
} from "react-router";
import { useEffect } from "react";

import type { Route } from "./+types/root";
import "bootstrap/dist/css/bootstrap.min.css";
import "./app.css";
import { GlobalSpinner } from "~/components/global-spinner";
import { useGlobalLoadingStore } from "~/stores/global-loading-store";

export function Layout({ children }: { children: React.ReactNode }) {
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
    const originalFetch = window.fetch.bind(window);

    window.fetch = async (input: RequestInfo | URL, init?: RequestInit) => {
      const mergedHeaders = new Headers(
        init?.headers ?? (input instanceof Request ? input.headers : undefined)
      );
      const skipSpinner = mergedHeaders.get("x-skip-global-spinner") === "true";

      if (!skipSpinner) {
        startRequest();
      }

      try {
        return await originalFetch(input, init);
      } finally {
        if (!skipSpinner) {
          finishRequest();
        }
      }
    };

    return () => {
      window.fetch = originalFetch;
    };
  }, [finishRequest, startRequest]);

  return (
    <>
      <GlobalSpinner />
      <Outlet />
    </>
  );
}

export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
  let message = "Oops!";
  let details = "An unexpected error occurred.";
  let stack: string | undefined;

  if (isRouteErrorResponse(error)) {
    message = error.status === 404 ? "404" : "Error";
    details =
      error.status === 404
        ? "The requested page could not be found."
        : error.statusText || details;
  } else if (import.meta.env.DEV && error && error instanceof Error) {
    details = error.message;
    stack = error.stack;
  }

  return (
    <main className="pt-16 p-4 container mx-auto">
      <h1>{message}</h1>
      <p>{details}</p>
      {stack && (
        <pre className="w-full p-4 overflow-x-auto">
          <code>{stack}</code>
        </pre>
      )}
    </main>
  );
}

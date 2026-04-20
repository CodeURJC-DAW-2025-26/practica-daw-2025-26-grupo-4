import { useEffect, useState } from "react";
import { selectIsGlobalLoading, useGlobalLoadingStore } from "~/stores/global-loading-store";

const SPINNER_DELAY_MS = 350;

export function GlobalSpinner() {
  const isLoading = useGlobalLoadingStore(selectIsGlobalLoading);
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    if (!isLoading) {
      setIsVisible(false);
      return;
    }

    const timer = window.setTimeout(() => {
      setIsVisible(true);
    }, SPINNER_DELAY_MS);

    return () => {
      window.clearTimeout(timer);
    };
  }, [isLoading]);

  if (!isVisible) {
    return null;
  }

  return (
    <div className="global-spinner-overlay" role="status" aria-live="polite" aria-label="Cargando">
      <div className="global-spinner" aria-hidden="true"></div>
      <span className="global-spinner-text">Cargando...</span>
    </div>
  );
}

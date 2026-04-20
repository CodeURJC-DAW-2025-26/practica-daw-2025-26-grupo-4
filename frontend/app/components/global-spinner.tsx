import { selectIsGlobalLoading, useGlobalLoadingStore } from "~/stores/global-loading-store";

export function GlobalSpinner() {
  const isLoading = useGlobalLoadingStore(selectIsGlobalLoading);

  if (!isLoading) {
    return null;
  }

  return (
    <div className="global-spinner-overlay" role="status" aria-live="polite" aria-label="Cargando">
      <div className="global-spinner" aria-hidden="true"></div>
      <span className="global-spinner-text">Cargando...</span>
    </div>
  );
}

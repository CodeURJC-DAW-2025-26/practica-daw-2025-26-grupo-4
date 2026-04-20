import { create } from "zustand";

interface GlobalLoadingState {
  activeRequests: number;
  navLoading: boolean;
  startRequest: () => void;
  finishRequest: () => void;
  setNavLoading: (loading: boolean) => void;
}

export const useGlobalLoadingStore = create<GlobalLoadingState>((set) => ({
  activeRequests: 0,
  navLoading: false,
  startRequest: () => set((state) => ({ activeRequests: state.activeRequests + 1 })),
  finishRequest: () =>
    set((state) => ({
      activeRequests: Math.max(0, state.activeRequests - 1)
    })),
  setNavLoading: (loading: boolean) => set({ navLoading: loading })
}));

export const selectIsGlobalLoading = (state: GlobalLoadingState): boolean =>
  state.navLoading || state.activeRequests > 0;

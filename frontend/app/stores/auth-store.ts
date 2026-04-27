import { create } from "zustand";

export interface AuthUserDTO {
  id: number;
  username: string;
  fullName: string;
  email: string;
  birthDate: string;
  shippingAddress: string;
  roles: string[];
  profileImageUrl: string | null;
}

interface AuthState {
  user: AuthUserDTO | null;
  isLogged: boolean;
  isAdmin: boolean;
  loading: boolean;
  hasLoaded: boolean;
  loadSession: (force?: boolean) => Promise<void>;
  setUser: (user: AuthUserDTO | null) => void;
  clearSession: () => void;
}

const buildAuthState = (user: AuthUserDTO | null) => ({
  user,
  isLogged: user !== null,
  isAdmin: user?.roles.includes("ADMIN") ?? false
});

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  isLogged: false,
  isAdmin: false,
  loading: true,
  hasLoaded: false,

  loadSession: async (force = false) => {
    if (!force && get().hasLoaded && !get().loading) {
      return;
    }

    set({ loading: true });

    try {
      const response = await fetch("/api/v1/user", {
        credentials: "include"
      });

      if (response.ok) {
        const user = (await response.json()) as AuthUserDTO;
        set({ ...buildAuthState(user), loading: false, hasLoaded: true });
        return;
      }

      set({ ...buildAuthState(null), loading: false, hasLoaded: true });
    } catch {
      set({ ...buildAuthState(null), loading: false, hasLoaded: true });
    }
  },

  setUser: (user) =>
    set({
      ...buildAuthState(user),
      loading: false,
      hasLoaded: true
    }),

  clearSession: () =>
    set({
      ...buildAuthState(null),
      loading: false,
      hasLoaded: true
    })
}));

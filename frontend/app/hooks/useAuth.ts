import { useEffect } from "react";

import { useAuthStore, type AuthUserDTO } from "../stores/auth-store";

export type UserProfileResponseDTO = AuthUserDTO;

export interface AuthStatus {
  isLogged: boolean;
  isAdmin: boolean;
  user: AuthUserDTO | null;
  loading: boolean;
}

export function useAuth(): AuthStatus {
  const isLogged = useAuthStore((state) => state.isLogged);
  const isAdmin = useAuthStore((state) => state.isAdmin);
  const user = useAuthStore((state) => state.user);
  const loading = useAuthStore((state) => state.loading);
  const hasLoaded = useAuthStore((state) => state.hasLoaded);
  const loadSession = useAuthStore((state) => state.loadSession);

  useEffect(() => {
    if (!hasLoaded) {
      loadSession().catch(() => {});
    }
  }, [hasLoaded, loadSession]);

  return { isLogged, isAdmin, user, loading };
}

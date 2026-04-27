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
    const checkAuth = async () => {
      try {
        const response = await fetch("/api/v1/user/me", {
          credentials: "include"
        });

        if (response.ok) {
          const user = (await response.json()) as UserProfileResponseDTO;
          setStatus({
            isLogged: true,
            isAdmin: user.roles.includes("ADMIN"),
            user,
            loading: false
          });
        } else {
          setStatus({
            isLogged: false,
            isAdmin: false,
            user: null,
            loading: false
          });
        }
      } catch {
        setStatus({
          isLogged: false,
          isAdmin: false,
          user: null,
          loading: false
        });
      }
    };

    checkAuth();
  }, []);

  return status;
    if (!hasLoaded) {
      loadSession().catch(() => {});
    }
  }, [hasLoaded, loadSession]);

  return { isLogged, isAdmin, user, loading };
}

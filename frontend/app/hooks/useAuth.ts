import { useEffect, useState } from "react";

export interface UserProfileResponseDTO {
  id: number;
  username: string;
  fullName: string;
  email: string;
  birthDate: string;
  shippingAddress: string;
  roles: string[];
  profileImageUrl: string | null;
}

export interface AuthStatus {
  isLogged: boolean;
  isAdmin: boolean;
  user: UserProfileResponseDTO | null;
  loading: boolean;
}

export function useAuth(): AuthStatus {
  const [status, setStatus] = useState<AuthStatus>({
    isLogged: false,
    isAdmin: false,
    user: null,
    loading: true
  });

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
}

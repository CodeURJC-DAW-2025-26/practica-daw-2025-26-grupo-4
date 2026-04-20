import type { RegisterRequestDTO, RegisterResponseDTO, ErrorResponseDTO } from "~/api/dtos";

const AUTH_API_URL = "/api/v1/auth";

export interface LoginRequestDTO {
  username: string;
  password: string;
}

export interface AuthResponseDTO {
  status: "SUCCESS" | "FAILURE";
  message: string;
  error?: string;
}

async function parseErrorMessage(response: Response, fallbackMessage: string) {
  try {
    const errorData = (await response.json()) as ErrorResponseDTO | AuthResponseDTO;
    if ("message" in errorData && errorData.message) {
      return errorData.message;
    }
    if ("error" in errorData && errorData.error) {
      return errorData.error;
    }
  } catch {
    // Keep fallback when body is not a valid JSON error.
  }

  return fallbackMessage;
}

export async function loginUser(payload: LoginRequestDTO): Promise<AuthResponseDTO> {
  const response = await fetch(`${AUTH_API_URL}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    const errorMessage = await parseErrorMessage(response, "No se pudo iniciar sesion");
    throw new Error(errorMessage);
  }

  return (await response.json()) as AuthResponseDTO;
}

export async function registerUser(payload: RegisterRequestDTO): Promise<RegisterResponseDTO> {
  const response = await fetch(`${AUTH_API_URL}/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    const errorMessage = await parseErrorMessage(response, "No se pudo completar el registro");
    throw new Error(errorMessage);
  }

  return (await response.json()) as RegisterResponseDTO;
}

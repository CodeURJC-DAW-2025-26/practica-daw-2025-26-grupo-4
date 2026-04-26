import type {
  AdminCategoryRequestDTO,
  AdminStatsResponseDTO,
  AdminUserDTO,
  AdminUserUpdateRequestDTO,
  CategoryDTO,
} from "~/api/dtos";
import { getApiErrorMessage } from "~/utils/api-error";

const ADMIN_API_URL = "/api/v1/admin";

export interface PagedResponseDTO<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
}

export async function getAdminStats(): Promise<AdminStatsResponseDTO> {
  const response = await fetch(`${ADMIN_API_URL}/stats`, {
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(
      await getApiErrorMessage(response, "No se pudieron cargar las estadísticas"),
    );
  }

  return await response.json();
}

export async function getAdminUsers(
  page = 0,
  size = 5,
): Promise<PagedResponseDTO<AdminUserDTO>> {
  const response = await fetch(`${ADMIN_API_URL}/users?page=${page}&size=${size}`, {
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(await getApiErrorMessage(response, "No se pudieron cargar los usuarios"));
  }

  return await response.json();
}

export async function updateAdminUser(
  id: number,
  payload: AdminUserUpdateRequestDTO,
): Promise<AdminUserDTO> {
  const response = await fetch(`${ADMIN_API_URL}/users/${id}`, {
    method: "PUT",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error(await getApiErrorMessage(response, "No se pudo actualizar el usuario"));
  }

  return await response.json();
}

export async function banAdminUser(id: number): Promise<AdminUserDTO> {
  const response = await fetch(`${ADMIN_API_URL}/users/${id}/ban`, {
    method: "PUT",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(await getApiErrorMessage(response, "No se pudo banear el usuario"));
  }

  return await response.json();
}

export async function unbanAdminUser(id: number): Promise<AdminUserDTO> {
  const response = await fetch(`${ADMIN_API_URL}/users/${id}/unban`, {
    method: "PUT",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(await getApiErrorMessage(response, "No se pudo desbanear el usuario"));
  }

  return await response.json();
}

export async function deleteAdminUser(id: number): Promise<void> {
  const response = await fetch(`${ADMIN_API_URL}/users/${id}`, {
    method: "DELETE",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(await getApiErrorMessage(response, "No se pudo eliminar el usuario"));
  }
}

export async function getAdminCategories(): Promise<CategoryDTO[]> {
  const response = await fetch(`${ADMIN_API_URL}/categories`, {
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(await getApiErrorMessage(response, "No se pudieron cargar las categorías"));
  }

  return await response.json();
}

export async function createAdminCategory(
  payload: AdminCategoryRequestDTO,
): Promise<CategoryDTO> {
  const response = await fetch(`${ADMIN_API_URL}/categories`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error(await getApiErrorMessage(response, "No se pudo crear la categoría"));
  }

  return await response.json();
}

export async function updateAdminCategory(
  id: number,
  payload: AdminCategoryRequestDTO,
): Promise<CategoryDTO> {
  const response = await fetch(`${ADMIN_API_URL}/categories/${id}`, {
    method: "PUT",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error(await getApiErrorMessage(response, "No se pudo actualizar la categoría"));
  }

  return await response.json();
}

export async function deleteAdminCategory(id: number): Promise<void> {
  const response = await fetch(`${ADMIN_API_URL}/categories/${id}`, {
    method: "DELETE",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(await getApiErrorMessage(response, "No se pudo eliminar la categoría"));
  }
}

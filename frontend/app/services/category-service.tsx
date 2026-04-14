import type { CategoryDTO } from "~/api/dtos";

const API_URL = "/api/v1/categories";

export async function getCategories(): Promise<CategoryDTO[]> {
  const res = await fetch(API_URL);
  return await res.json();
}

export async function getCategory(id: number): Promise<CategoryDTO> {
  const res = await fetch(`${API_URL}${id}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch category with id ${id}`);
  }
  return await res.json();
}

export async function addCategory(category: CategoryDTO): Promise<CategoryDTO> {
  const res = await fetch(API_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(category),
  });
  if (!res.ok) {
    throw new Error("Failed to add category");
  }
  return await res.json();
}

export async function updateCategory(id: number, category: CategoryDTO): Promise<CategoryDTO> {
  const res = await fetch(`${API_URL}${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(category),
  });
  if (!res.ok) {
    throw new Error(`Failed to update category with id ${id}`);
  }
  return await res.json();
}

export async function deleteCategory(id: number): Promise<void> {
  const res = await fetch(`${API_URL}${id}`, {
    method: "DELETE",
  });
  if (!res.ok) {
    throw new Error(`Failed to delete category with id ${id}`);
  }
}
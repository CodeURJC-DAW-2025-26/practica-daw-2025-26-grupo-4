import type { ProductDTO } from "~/api/dtos";



const API_URL = "/api/v1/products/";

export async function getProducts(): Promise<ProductDTO[]> {
  const res = await fetch(API_URL);
  if (!res.ok) {
    throw new Error("Failed to fetch products");
  }
  const data = await res.json();
  if (data.content && Array.isArray(data.content)) {
    return data.content;
  }
  return data;
}

export async function getProduct(id: number): Promise<ProductDTO> {
  const res = await fetch(`${API_URL}${id}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch product with id ${id}`);
  }
  return await res.json();
}

export async function addProduct(product: ProductDTO): Promise<ProductDTO> {
  const res = await fetch(API_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(product)
  });
  if (!res.ok) {
    throw new Error("Failed to add product");
  }
  return await res.json();
}

export async function updateProduct(id: number, product: ProductDTO): Promise<ProductDTO> {
  const res = await fetch(`${API_URL}${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(product)
  });
  if (!res.ok) {
    throw new Error(`Failed to update product with id ${id}`);
  }
  return await res.json();
}

export async function deleteProduct(id: number): Promise<void> {
  const res = await fetch(`${API_URL}${id}`, {
    method: "DELETE"
  });
  if (!res.ok) {
    throw new Error(`Failed to delete product with id ${id}`);
  }
}

import type { ProductDTO } from "~/api/dtos";



const API_URL = "/api/v1/products/";

export async function getProducts(
  page: number = 0,
  size: number = 9,
  q?: string | null,
  categoryId?: string | null
): Promise<any> {
  const queryParams = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
  });
  if (q) queryParams.append("q", q);
  if (categoryId) queryParams.append("categoryId", categoryId);

  const res = await fetch(`${API_URL}?${queryParams.toString()}`);
  if (!res.ok) {
    throw new Error("Failed to fetch products");
  }
  
  // Spring Data returns a 'Page' object. Return the full object
  // with the "content" (list of items) and "last" (boolean) properties for pagination.
  return await res.json();
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

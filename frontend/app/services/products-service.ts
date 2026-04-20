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
  });

  // Decide which endpoint to use. If filtering by category or query, 
  // utilize the endpoint exposed by HomeRestController.
  const isFiltering = Boolean(q) || Boolean(categoryId);
  const endpoint = isFiltering ? "/api/v1/home/products" : API_URL;

  if (!isFiltering) {
    queryParams.append("size", size.toString());
  }

  if (q) queryParams.append("q", q);
  if (categoryId) queryParams.append("categoryId", categoryId);

  const res = await fetch(`${endpoint}?${queryParams.toString()}`);
  if (!res.ok) {
    throw new Error("Failed to fetch products");
  }
  
  const data = await res.json();

  // Depending on whether we hit a normal endpoint or a Spring Data pagination one:
  // Spring Boot 3 returns pagination with the "page" object, not "last" natively.
  if (data.page) {
    return {
      content: data.content || [],
      last: data.page.number >= data.page.totalPages - 1
    };
  }

  // If it returns the hasMore format (in case HomeProductsResponseDTO is used in the future)
  if (data.hasMore !== undefined) {
    return {
      content: data.products || data.content || [],
      last: !data.hasMore
    };
  }

  // Fallback if it brings "last" directly or another scenario
  return {
    content: data.content || [],
    last: data.last !== undefined ? data.last : true
  };
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

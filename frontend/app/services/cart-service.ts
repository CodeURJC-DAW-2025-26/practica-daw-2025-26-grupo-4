import type { CartDTO } from "~/api/dtos";

const API_URL = "/api/v1/cart";

export const cartService = {
  async getCart(): Promise<CartDTO | null> {
    const response = await fetch(`${API_URL}/`, {
      method: "GET",
      credentials: "include",
      headers: {
        "x-skip-global-error": "true"
      }
    });

    if (response.status === 401 || response.status === 403) {
      return null;
    }

    if (!response.ok) throw new Error("Error al buscar el carrito");
    return response.json();
  },

  async addItem(productId: number, quantity: number = 1) {
    const response = await fetch(
      `${API_URL}/items/${productId}?quantity=${quantity}`,
      {
        method: "POST",
        credentials: "include",
      },
    );

    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    if (!response.ok) throw new Error("Error al añadir al carrito");
    return response.json();
  },

  async removeItem(productId: number) {
    const response = await fetch(`${API_URL}/items/${productId}`, {
      method: "DELETE",
      credentials: "include",
    });

    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    if (!response.ok) throw new Error("Error al eliminar del carrito");
    return response.json();
  },

  async clearCart() {
    const response = await fetch(`${API_URL}/`, {
      method: "DELETE",
      credentials: "include",
    });

    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    if (!response.ok) throw new Error("Error al eliminar el carrito");
  },

  async checkout() {
    const response = await fetch(`/api/v1/orders`, {
      method: "POST",
      credentials: "include",
    });

    if (response.status === 401) {
      throw new Error("UNAUTHORIZED");
    }

    if (response.status === 400) {
      throw new Error("El carrito está vacío");
    }

    if (!response.ok) throw new Error("Error al procesar el carrito");
  },
};

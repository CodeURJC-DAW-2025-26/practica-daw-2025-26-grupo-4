import type { OrderDTO } from "~/api/dtos";

const API_URL = "/api/v1/orders";

type DirectOrderPayload = {
  productId: number;
  quantity: number;
};

type PageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
};

export const orderService = {
  async getOrders(page = 0, size = 10): Promise<PageResponse<OrderDTO> | null> {
    const response = await fetch(`${API_URL}?page=${page}&size=${size}`, {
      method: "GET",
      credentials: "include",
    });

    if (response.status === 401) {
      return null;
    }

    if (!response.ok) throw new Error("Error al buscar los pedidos");
    return response.json();
  },

  async getOrderById(id: number): Promise<OrderDTO | null> {
    const response = await fetch(`${API_URL}/${id}`, {
      method: "GET",
      credentials: "include",
    });

    if (response.status === 401) {
      return null;
    }

    if (response.status === 404) {
      return null;
    }

    if (response.status === 403) {
      throw new Error("No tienes permisos para ver este pedido");
    }

    if (!response.ok) {
      throw new Error("Error al buscar el pedido");
    }

    return response.json();
  },

  async createOrder() {
    const response = await fetch(API_URL, {
      method: "POST",
      credentials: "include",
    });

    if (response.status === 401) {
      return null;
    }

    if (!response.ok) throw new Error("Error al crear el pedido");
    return response.json();
  },

  async createDirectOrder(
    payload: DirectOrderPayload,
  ): Promise<OrderDTO | null> {
    const response = await fetch(`${API_URL}/direct`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (response.status === 401) {
      return null;
    }

    if (!response.ok) {
      throw new Error("Error al crear el pedido directo");
    }

    return response.json();
  },
};

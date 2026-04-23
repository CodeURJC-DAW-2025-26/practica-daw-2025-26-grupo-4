import { useEffect } from "react";
import { create } from "zustand";
import type { CartDTO } from "~/api/dtos";
import { cartService } from "~/services/cart-service";

interface CartState {
  cart: CartDTO | null;
  loading: boolean;
  error: string | null;
  hasLoaded: boolean;
  loadCart: () => Promise<void>;
  addItem: (productId: number, quantity?: number) => Promise<void>;
  removeItem: (productId: number) => Promise<void>;
  clearCart: () => Promise<void>;
  checkout: () => Promise<void>;
}

export const useCartStore = create<CartState>((set, get) => ({
  cart: null,
  loading: false,
  error: null,
  hasLoaded: false,

  loadCart: async () => {
    set({ loading: true, error: null });
    try {
      const data = await cartService.getCart();
      set({ cart: data, hasLoaded: true });
    } catch (err: any) {
      set({ error: err.message });
      throw err;
    } finally {
      set({ loading: false });
    }
  },

  addItem: async (productId: number, quantity: number = 1) => {
    set({ loading: true, error: null });
    try {
      const updatedCart = await cartService.addItem(productId, quantity);
      set({ cart: updatedCart });
    } catch (err: any) {
      set({ error: err.message });
      throw err;
    } finally {
      set({ loading: false });
    }
  },

  removeItem: async (productId: number) => {
    set({ loading: true, error: null });
    try {
      const updatedCart = await cartService.removeItem(productId);
      set({ cart: updatedCart });
    } catch (err: any) {
      set({ error: err.message });
      throw err;
    } finally {
      set({ loading: false });
    }
  },

  clearCart: async () => {
    set({ loading: true, error: null });
    try {
      await cartService.clearCart();
      set({ cart: null });
    } catch (err: any) {
      set({ error: err.message });
      throw err;
    } finally {
      set({ loading: false });
    }
  },

  checkout: async () => {
    set({ loading: true, error: null });
    try {
      await cartService.checkout();
      set({ cart: null });
    } catch (err: any) {
      set({ error: err.message });
      throw err;
    } finally {
      set({ loading: false });
    }
  },
}));

export function useCart() {
  const store = useCartStore();

  useEffect(() => {
    if (!store.hasLoaded && !store.loading) {
      store.loadCart().catch(() => {});
    }
  }, [store.hasLoaded, store.loading, store.loadCart]);

  return store;
}

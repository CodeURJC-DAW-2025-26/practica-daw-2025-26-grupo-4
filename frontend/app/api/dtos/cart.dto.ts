import type { ProductSummaryDTO } from "./product.dto";

export interface CartItemDTO {
  id: number;
  product: ProductSummaryDTO;
  quantity: number;
}

export interface CartDTO {
  items: CartItemDTO[];
  shippingCost: number;
  count: number;
  hasItems: boolean;
}

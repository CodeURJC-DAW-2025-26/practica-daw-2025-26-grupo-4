import type { OrderStatus } from "./status.dto";

export interface OrderItemDTO {
  productId: number;
  name: string;
  quantity: number;
  price: number;
  imageUrl: string;
  canReview: boolean;
  hasReview: boolean;
}

export interface OrderDTO {
  id: number;
  orderNumber: string;
  date: string;
  status: OrderStatus;
  totalPrice: number;
  shippingCost: number;
  items: OrderItemDTO[];
}

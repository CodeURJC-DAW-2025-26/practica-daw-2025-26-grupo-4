export type OrderStatus =
  | "Procesando"
  | "En tránsito"
  | "En reparto"
  | "Entregado";

export const OrderStatusEnum = {
  PENDING: "Procesando" as OrderStatus,
  TRANSIT: "En tránsito" as OrderStatus,
  SHIPPING: "En reparto" as OrderStatus,
  DELIVERED: "Entregado" as OrderStatus,
};

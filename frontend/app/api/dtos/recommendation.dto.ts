import type { ProductDTO } from "./product.dto";

export interface RecommendationPackDTO {
  label: string;
  isCombo: boolean;
  products: ProductDTO[];
  totalPrice: string;
}

export interface RecommendationResponseDTO {
  title: string;
  subtitle: string;
  recommendations: RecommendationPackDTO[];
}

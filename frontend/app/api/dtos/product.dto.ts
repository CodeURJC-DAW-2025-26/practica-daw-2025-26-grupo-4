import type { CategoryDTO } from "./category.dto";
import type { ImageDTO } from "./image.dto";
import type { ProductReviewDTO } from "./review.dto";

export interface ProductDTO {
  id: number;
  name: string;
  price: number;
  description: string;
  tags: string[];
  averageRating: number;
  category: CategoryDTO;
  images: ImageDTO[];
  reviews: ProductReviewDTO[];
}

export interface ProductSummaryDTO {
  id: number;
  name: string;
  price: number;
  description: string;
  tags: string[];
  averageRating: number;
  category: CategoryDTO;
  images: ImageDTO[];
}

export interface ProductCreateRequestDTO {
  name: string;
  price: number;
  description: string;
  tags: string[];
  categoryId: number;
}

export interface ProductUpdateRequestDTO {
  name: string;
  price: number;
  description: string;
  tags: string[];
  categoryId: number;
}

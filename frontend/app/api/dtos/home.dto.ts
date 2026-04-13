import type { CategoryDTO } from "./category.dto";
import type { ProductDTO } from "./product.dto";

export interface HomeProductsResponseDTO {
  products: ProductDTO[];
  hasMore: boolean;
}

export interface HomeResponseDTO {
  categories: CategoryDTO[];
  products: ProductDTO[];
  selectedCategoryId: number | null;
  selectedCategoryName: string;
  searchQuery: string;
  hasMore: boolean;
}

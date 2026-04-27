export interface ReviewDTO {
  id: number;
  productId: number;
  userId: number | null;
  content: string;
  rating: number;
  date: string;
  authorName: string;
}

export interface CreateReviewRequestDTO {
  productId: number;
  content: string;
  rating: number;
}

export interface ProductReviewDTO {
  id: number;
  userId: number | null;
  content: string;
  rating: number;
  date: string;
  authorName: string;
}

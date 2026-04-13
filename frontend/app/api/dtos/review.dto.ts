export interface ReviewDTO {
  id: number;
  productId: number;
  content: string;
  rating: number;
  date: string;
  authorName: string;
}

export interface ProductReviewDTO {
  id: number;
  content: string;
  rating: number;
  date: string;
  authorName: string;
}

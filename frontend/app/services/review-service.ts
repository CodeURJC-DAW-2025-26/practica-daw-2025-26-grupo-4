import type { CreateReviewRequestDTO, ReviewDTO } from "~/api/dtos";
import { getApiErrorMessage } from "~/utils/api-error";

const REVIEWS_API_URL = "/api/v1/reviews/";

export async function createReview(payload: CreateReviewRequestDTO): Promise<ReviewDTO> {
  const requestBody: ReviewDTO = {
    id: 0,
    productId: payload.productId,
    userId: null,
    content: payload.content,
    rating: payload.rating,
    date: new Date().toISOString().slice(0, 10),
    authorName: "",
  };

  const response = await fetch(REVIEWS_API_URL, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestBody),
  });

  if (!response.ok) {
    throw new Error(await getApiErrorMessage(response, "No se pudo publicar la reseña"));
  }

  return await response.json();
}

export async function updateReview(reviewId: number, payload: CreateReviewRequestDTO): Promise<ReviewDTO> {
  const requestBody: ReviewDTO = {
    id: reviewId,
    productId: payload.productId,
    userId: null,
    content: payload.content,
    rating: payload.rating,
    date: new Date().toISOString().slice(0, 10),
    authorName: "",
  };

  const response = await fetch(`${REVIEWS_API_URL}${reviewId}`, {
    method: "PUT",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestBody),
  });

  if (!response.ok) {
    throw new Error(await getApiErrorMessage(response, "No se pudo actualizar la reseña"));
  }

  return await response.json();
}

export async function deleteReview(reviewId: number): Promise<void> {
  const response = await fetch(`${REVIEWS_API_URL}${reviewId}`, {
    method: "DELETE",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(await getApiErrorMessage(response, "No se pudo eliminar la reseña"));
  }
}
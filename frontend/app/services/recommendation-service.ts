import type { RecommendationResponseDTO } from "~/api/dtos";

const API_URL = "/api/v1/recommendations";

export async function getRecommendations(): Promise<RecommendationResponseDTO> {
  const res = await fetch(API_URL);
  if (!res.ok) {
    throw new Error("Failed to fetch recommendations: " + res.statusText);
  }
  return await res.json();
}
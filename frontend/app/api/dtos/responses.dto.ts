export interface ErrorResponseDTO {
  message: string;
}

export interface SuccessResponseDTO {
  message: string;
}

export interface RegisterResponseDTO {
  id: number;
  username: string;
  fullName: string;
  email: string;
  roles: string[];
}

export interface PagedResponseDTO<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
}

export interface UserProfileResponseDTO {
  id: number;
  username: string;
  fullName: string;
  email: string;
  birthDate: string; // LocalDate translates to string in JSON (e.g., 'YYYY-MM-DD')
  shippingAddress: string;
  roles: string[];
  profileImageUrl: string;
}

export interface ChartSeriesDTO {
  labels: string[];
  values: number[];
}

export interface AdminStatsResponseDTO {
  salesByCategory: ChartSeriesDTO;
  salesByTag: ChartSeriesDTO;
  monthlySales: ChartSeriesDTO;
  ordersByMonth: ChartSeriesDTO;
  reviewsByMonth: ChartSeriesDTO;
}

export interface AdminCategoryRequestDTO {
  name: string;
  icon: string;
}

export interface AdminUserDTO {
  id: number;
  username: string;
  fullName: string;
  email: string;
  birthDate: string;
  shippingAddress: string;
  roles: string[];
  banned: boolean;
  admin: boolean;
}

export interface AdminUserUpdateRequestDTO {
  username: string;
  email: string;
  fullName: string;
  birthDate: string;
  shippingAddress: string;
}

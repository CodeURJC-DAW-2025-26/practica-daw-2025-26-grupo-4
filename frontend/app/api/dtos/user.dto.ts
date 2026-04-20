export interface RegisterRequestDTO {
  fullName: string;
  username: string;
  email: string;
  password?: string;
  confirmPassword?: string;
}

export interface UserAccountUpdateRequestDTO {
  username: string;
  fullName: string;
  birthDate: string;
}

export interface UserAddressUpdateRequestDTO {
  street: string;
  additional: string;
  city: string;
  province: string;
  postalCode: string;
  country: string;
  phone: string;
}

export interface UserPasswordChangeRequestDTO {
  oldPassword?: string;
  newPassword?: string;
  confirmPassword?: string;
}

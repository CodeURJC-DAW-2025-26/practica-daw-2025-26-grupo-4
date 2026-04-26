import { useEffect, useMemo, useState } from "react";
import type { Route } from "./+types/user";
import { Link } from "react-router";
import { Header } from "~/components/header";
import { Footer } from "~/components/footer";
import { useAuth } from "~/hooks/useAuth";
import { notifyError, notifySuccess } from "~/stores/global-notification-store";

import "~/styles/tokens.css";
import "~/styles/components.css";
import "~/styles/user.css";

interface AccountFormState {
  username: string;
  fullName: string;
  birthDate: string;
}

interface PasswordFormState {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

interface AddressFormState {
  street: string;
  additional: string;
  city: string;
  province: string;
  postalCode: string;
  country: string;
  phone: string;
}

interface UserProfileResponseDTO {
  id: number;
  username: string;
  fullName: string;
  email: string;
  birthDate: string;
  shippingAddress: string;
  roles: string[];
  profileImageUrl: string | null;
}

const EMPTY_ADDRESS: AddressFormState = {
  street: "",
  additional: "",
  city: "",
  province: "",
  postalCode: "",
  country: "",
  phone: ""
};

async function submitUserUpdate(formData: FormData): Promise<UserProfileResponseDTO> {
  const response = await fetch("/api/users", {
    method: "PUT",
    credentials: "include",
    body: formData
  });

  const responseText = await response.text();

  if (!response.ok) {
    throw new Error(responseText || "No se pudo actualizar el usuario");
  }

  if (!responseText) {
    throw new Error("La respuesta del servidor está vacía");
  }

  return JSON.parse(responseText) as UserProfileResponseDTO;
}

function parseShippingAddress(address: string): AddressFormState {
  if (!address || !address.trim()) {
    return EMPTY_ADDRESS;
  }

  const lines = address
    .split("\n")
    .map((line) => line.trim())
    .filter((line) => line.length > 0);

  const [street = "", ...restLines] = lines;
  const remaining = [...restLines];

  let phone = "";
  const phoneIndex = remaining.findIndex((line) => /^Tel[eé]fono:\s*/i.test(line));
  if (phoneIndex >= 0) {
    phone = remaining[phoneIndex].replace(/^Tel[eé]fono:\s*/i, "").trim();
    remaining.splice(phoneIndex, 1);
  }

  let city = "";
  let province = "";
  let postalCode = "";
  const cityProvincePostalIndex = remaining.findIndex((line) => /^.+,\s*.+\s+\d{5}$/.test(line));
  if (cityProvincePostalIndex >= 0) {
    const locationLine = remaining[cityProvincePostalIndex];
    const locationMatch = locationLine.match(/^(.+),\s*(.+)\s+(\d{5})$/);

    if (locationMatch) {
      city = locationMatch[1].trim();
      province = locationMatch[2].trim();
      postalCode = locationMatch[3].trim();
    }

    remaining.splice(cityProvincePostalIndex, 1);
  }

  let country = "";
  if (remaining.length > 0) {
    country = remaining[remaining.length - 1];
    remaining.pop();
  }

  const additional = remaining.join("\n");

  return {
    street,
    additional,
    city,
    province,
    postalCode,
    country,
    phone
  };
}

export function links(): Route.LinkDescriptors {
  return [
    { rel: "stylesheet", href: "https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" },
    { rel: "stylesheet", href: "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" }
  ];
}

export default function UserPage() {
  const { user, isLogged, loading } = useAuth();

  const [profilePreview, setProfilePreview] = useState<string | null>(null);
  const [selectedPhoto, setSelectedPhoto] = useState<File | null>(null);

  const [accountForm, setAccountForm] = useState<AccountFormState>({
    username: "",
    fullName: "",
    birthDate: ""
  });

  const [passwordForm, setPasswordForm] = useState<PasswordFormState>({
    oldPassword: "",
    newPassword: "",
    confirmPassword: ""
  });

  const [showPassword, setShowPassword] = useState({
    old: false,
    next: false,
    confirm: false
  });

  const [shippingAddress, setShippingAddress] = useState("");
  const [isAddressModalOpen, setIsAddressModalOpen] = useState(false);
  const [addressForm, setAddressForm] = useState<AddressFormState>(EMPTY_ADDRESS);

  const syncUserProfile = (updatedUser: UserProfileResponseDTO) => {
    setAccountForm({
      username: updatedUser.username ?? "",
      fullName: updatedUser.fullName ?? "",
      birthDate: updatedUser.birthDate ?? ""
    });
    setShippingAddress(updatedUser.shippingAddress ?? "");
    setAddressForm(parseShippingAddress(updatedUser.shippingAddress ?? ""));
    setProfilePreview(updatedUser.profileImageUrl ?? null);
  };

  useEffect(() => {
    if (!user) {
      return;
    }

    setAccountForm({
      username: user.username ?? "",
      fullName: user.fullName ?? "",
      birthDate: user.birthDate ?? ""
    });

    setShippingAddress(user.shippingAddress ?? "");
    setAddressForm(parseShippingAddress(user.shippingAddress ?? ""));
    setProfilePreview(user.profileImageUrl ?? null);
  }, [user]);

  const displayName = useMemo(() => {
    if (user?.username) {
      return user.username;
    }

    if (user?.fullName) {
      return user.fullName;
    }

    return "usuario";
  }, [user]);

  const handlePhotoChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0] ?? null;
    if (!file) {
      return;
    }

    setSelectedPhoto(file);

    const fileReader = new FileReader();
    fileReader.onload = () => {
      const preview = typeof fileReader.result === "string" ? fileReader.result : null;
      setProfilePreview(preview);
    };
    fileReader.readAsDataURL(file);
  };

  const handleSavePhoto = async (event: React.FormEvent) => {
    event.preventDefault();

    if (!selectedPhoto) {
      return;
    }

    try {
      const formData = new FormData();
      formData.append("profileImage", selectedPhoto);
      const updatedUser = await submitUserUpdate(formData);
      syncUserProfile(updatedUser);
      setSelectedPhoto(null);
      notifySuccess("Foto de perfil actualizada correctamente");
    } catch (error) {
      console.error(error);
      notifyError(error instanceof Error ? error.message : "No se pudo guardar la foto de perfil");
    }
  };

  const handleSaveAccount = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      const formData = new FormData();
      formData.append("username", accountForm.username);
      formData.append("fullName", accountForm.fullName);
      formData.append("birthDate", accountForm.birthDate);

      const updatedUser = await submitUserUpdate(formData);
      syncUserProfile(updatedUser);
      notifySuccess("Datos de cuenta actualizados correctamente");
    } catch (error) {
      console.error(error);
      notifyError(error instanceof Error ? error.message : "No se pudieron guardar los datos de cuenta");
    }
  };

  const handleSavePassword = async (event: React.FormEvent) => {
    event.preventDefault();

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      notifyError("La nueva contraseña y su confirmación no coinciden.");
      return;
    }

    try {
      const formData = new FormData();
      formData.append("oldPassword", passwordForm.oldPassword);
      formData.append("newPassword", passwordForm.newPassword);
      formData.append("confirmPassword", passwordForm.confirmPassword);

      const updatedUser = await submitUserUpdate(formData);
      syncUserProfile(updatedUser);

      setPasswordForm({
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
      });
      notifySuccess("Contraseña actualizada correctamente");
    } catch (error) {
      console.error(error);
      notifyError(error instanceof Error ? error.message : "No se pudo actualizar la contraseña");
    }
  };

  const handleOpenAddressModal = () => {
    setAddressForm(parseShippingAddress(shippingAddress));
    setIsAddressModalOpen(true);
  };

  const handleCloseAddressModal = () => {
    setIsAddressModalOpen(false);
  };

  const handleSaveAddress = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      const formData = new FormData();
      formData.append("street", addressForm.street);
      formData.append("additional", addressForm.additional);
      formData.append("city", addressForm.city);
      formData.append("province", addressForm.province);
      formData.append("postalCode", addressForm.postalCode);
      formData.append("country", addressForm.country);
      formData.append("phone", addressForm.phone);

      const updatedUser = await submitUserUpdate(formData);
      syncUserProfile(updatedUser);
      setIsAddressModalOpen(false);
      notifySuccess("Dirección actualizada correctamente");
    } catch (error) {
      console.error(error);
      notifyError(error instanceof Error ? error.message : "No se pudo guardar la dirección");
    }
  };

  const handleDeleteAddress = async () => {
    const confirmed = window.confirm("¿Estás seguro de que quieres eliminar esta dirección?");
    if (!confirmed) {
      return;
    }

    try {
      const formData = new FormData();
      formData.append("clearAddress", "true");

      const updatedUser = await submitUserUpdate(formData);
      syncUserProfile(updatedUser);
      notifySuccess("Dirección eliminada correctamente");
    } catch (error) {
      console.error(error);
      notifyError(error instanceof Error ? error.message : "No se pudo eliminar la dirección");
    }
  };

  if (loading) {
    return (
      <div className="user-page user-page--centered-state">
        <p>Cargando perfil...</p>
      </div>
    );
  }

  if (!isLogged) {
    return (
      <div className="user-page user-page--centered-state">
        <p>Necesitas iniciar sesión para acceder a tu perfil.</p>
        <Link to="/login" className="save-btn">Ir a iniciar sesión</Link>
      </div>
    );
  }

  return (
    <div className="user-page">
      <div className="app-container">
        <Header />

        <main className="main-content">
          <div className="content-body">
            <h1 className="greeting-title">¡Hola {displayName}!</h1>

            <section className="settings-card">
              <h2>Foto de perfil</h2>
              <div className="profile-photo-container">
                <div className="profile-photo-wrapper">
                  {profilePreview ? (
                    <img src={profilePreview} alt="Foto de perfil" className="profile-photo" id="profile-photo-preview" />
                  ) : (
                    <div className="profile-photo-placeholder" id="profile-photo-placeholder">
                      <i className="fa-solid fa-user"></i>
                    </div>
                  )}
                </div>
                <div className="profile-photo-actions">
                  <form id="profile-image-form" onSubmit={handleSavePhoto}>
                    <label htmlFor="profileImageInput" className="save-btn profile-photo-label">
                      <i className="fa-solid fa-camera"></i> Cambiar foto
                    </label>
                    <input
                      type="file"
                      id="profileImageInput"
                      name="profileImage"
                      accept="image/jpeg,image/png,image/webp"
                      onChange={handlePhotoChange}
                    />
                    <button type="submit" id="save-photo-btn" className="save-btn" style={{ display: selectedPhoto ? "inline-flex" : "none" }}>
                      Guardar foto
                    </button>
                  </form>
                  <p className="profile-photo-hint">JPEG, PNG o WebP · max. 5 MB</p>
                </div>
              </div>
            </section>

            <section className="settings-card">
              <h2>Datos de mi cuenta</h2>
              <form className="settings-form" id="account-form" onSubmit={handleSaveAccount}>
                <div className="form-grid">
                  <div className="form-group">
                    <label htmlFor="username">Nombre de usuario:</label>
                    <input
                      id="username"
                      type="text"
                      name="username"
                      value={accountForm.username}
                      placeholder="Nombre de usuario"
                      onChange={(event) => setAccountForm((prev) => ({ ...prev, username: event.target.value }))}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="birthDate">Fecha de nacimiento:</label>
                    <input
                      id="birthDate"
                      type="date"
                      name="birthDate"
                      value={accountForm.birthDate}
                      onChange={(event) => setAccountForm((prev) => ({ ...prev, birthDate: event.target.value }))}
                    />
                  </div>
                  <div className="form-group full-width">
                    <label htmlFor="fullName">Nombre completo:</label>
                    <input
                      id="fullName"
                      type="text"
                      name="fullName"
                      value={accountForm.fullName}
                      placeholder="Nombre completo"
                      onChange={(event) => setAccountForm((prev) => ({ ...prev, fullName: event.target.value }))}
                    />
                  </div>
                </div>
                <div className="action-row">
                  <button className="save-btn" type="submit">Guardar</button>
                </div>
              </form>
            </section>

            <section className="settings-card">
              <h2>Contraseña</h2>
              <form className="settings-form" onSubmit={handleSavePassword}>
                <div className="form-grid three-columns">
                  <div className="form-group">
                    <label htmlFor="oldPassword">Antigua contraseña:</label>
                    <div className="input-with-icon">
                      <input
                        id="oldPassword"
                        type={showPassword.old ? "text" : "password"}
                        name="oldPassword"
                        placeholder="Introduce tu antigua contraseña"
                        value={passwordForm.oldPassword}
                        onChange={(event) => setPasswordForm((prev) => ({ ...prev, oldPassword: event.target.value }))}
                        required
                      />
                      <button
                        type="button"
                        className="icon-toggle-btn"
                        onClick={() => setShowPassword((prev) => ({ ...prev, old: !prev.old }))}
                        aria-label="Mostrar u ocultar antigua contraseña"
                      >
                        <i className={`fa-regular ${showPassword.old ? "fa-eye" : "fa-eye-slash"}`}></i>
                      </button>
                    </div>
                  </div>
                  <div className="form-group">
                    <label htmlFor="newPassword">Nueva contraseña:</label>
                    <div className="input-with-icon">
                      <input
                        id="newPassword"
                        type={showPassword.next ? "text" : "password"}
                        name="newPassword"
                        placeholder="Nueva contraseña"
                        value={passwordForm.newPassword}
                        onChange={(event) => setPasswordForm((prev) => ({ ...prev, newPassword: event.target.value }))}
                        required
                      />
                      <button
                        type="button"
                        className="icon-toggle-btn"
                        onClick={() => setShowPassword((prev) => ({ ...prev, next: !prev.next }))}
                        aria-label="Mostrar u ocultar nueva contraseña"
                      >
                        <i className={`fa-regular ${showPassword.next ? "fa-eye" : "fa-eye-slash"}`}></i>
                      </button>
                    </div>
                  </div>
                  <div className="form-group">
                    <label htmlFor="confirmPassword">Repetir contraseña:</label>
                    <div className="input-with-icon">
                      <input
                        id="confirmPassword"
                        type={showPassword.confirm ? "text" : "password"}
                        name="confirmPassword"
                        placeholder="Repite la nueva contraseña"
                        value={passwordForm.confirmPassword}
                        onChange={(event) => setPasswordForm((prev) => ({ ...prev, confirmPassword: event.target.value }))}
                        required
                      />
                      <button
                        type="button"
                        className="icon-toggle-btn"
                        onClick={() => setShowPassword((prev) => ({ ...prev, confirm: !prev.confirm }))}
                        aria-label="Mostrar u ocultar confirmación de contraseña"
                      >
                        <i className={`fa-regular ${showPassword.confirm ? "fa-eye" : "fa-eye-slash"}`}></i>
                      </button>
                    </div>
                  </div>
                </div>
                <div className="action-row">
                  <button className="save-btn" type="submit">Guardar</button>
                </div>
              </form>
            </section>

            <section className="settings-card">
              <h2>Mi dirección de envío</h2>
              <div className="address-container-single">
                {shippingAddress ? (
                  <div className="address-card">
                    <div className="card-actions">
                      <button type="button" className="icon-btn edit-btn" onClick={handleOpenAddressModal}>
                        <i className="fa-solid fa-pencil"></i>
                      </button>
                      <button type="button" className="icon-btn delete-btn" onClick={handleDeleteAddress}>
                        <i className="fa-regular fa-trash-can"></i>
                      </button>
                    </div>
                    <div id="address-display" style={{ whiteSpace: "pre-line" }}>
                      {shippingAddress}
                    </div>
                  </div>
                ) : (
                  <button type="button" className="add-address-btn" id="add-address-btn" onClick={handleOpenAddressModal}>
                    <i className="fa-solid fa-plus"></i>
                    <span>Añadir dirección de envío</span>
                  </button>
                )}
              </div>
            </section>
          </div>
        </main>
      </div>

      <div id="address-modal" className={`modal ${isAddressModalOpen ? "open" : ""}`}>
        <div className="modal-content">
          <div className="modal-header">
            <h2 id="modal-title">Añadir dirección de envío</h2>
            <button type="button" className="modal-close" id="close-modal" onClick={handleCloseAddressModal}>
              &times;
            </button>
          </div>
          <form id="address-form" onSubmit={handleSaveAddress}>
            <div className="modal-body">
              <div className="form-group">
                <label htmlFor="street">Calle y número*</label>
                <input
                  type="text"
                  id="street"
                  name="street"
                  required
                  placeholder="Ej: Calle Mayor 123"
                  value={addressForm.street}
                  onChange={(event) => setAddressForm((prev) => ({ ...prev, street: event.target.value }))}
                />
              </div>

              <div className="form-group">
                <label htmlFor="additional">Información adicional</label>
                <input
                  type="text"
                  id="additional"
                  name="additional"
                  placeholder="Piso, puerta, bloque, etc."
                  value={addressForm.additional}
                  onChange={(event) => setAddressForm((prev) => ({ ...prev, additional: event.target.value }))}
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="city">Ciudad*</label>
                  <input
                    type="text"
                    id="city"
                    name="city"
                    required
                    placeholder="Ej: Madrid"
                    value={addressForm.city}
                    onChange={(event) => setAddressForm((prev) => ({ ...prev, city: event.target.value }))}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="province">Provincia*</label>
                  <input
                    type="text"
                    id="province"
                    name="province"
                    required
                    placeholder="Ej: Madrid"
                    value={addressForm.province}
                    onChange={(event) => setAddressForm((prev) => ({ ...prev, province: event.target.value }))}
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="postalCode">Código Postal*</label>
                  <input
                    type="text"
                    id="postalCode"
                    name="postalCode"
                    required
                    pattern="\d{5}"
                    placeholder="Ej: 28001"
                    value={addressForm.postalCode}
                    onChange={(event) => setAddressForm((prev) => ({ ...prev, postalCode: event.target.value }))}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="country">País*</label>
                  <input
                    type="text"
                    id="country"
                    name="country"
                    required
                    placeholder="Ej: España"
                    value={addressForm.country}
                    onChange={(event) => setAddressForm((prev) => ({ ...prev, country: event.target.value }))}
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="phone">Teléfono*</label>
                <input
                  type="tel"
                  id="phone"
                  name="phone"
                  required
                  pattern="\d{9}"
                  placeholder="Ej: 612345678"
                  value={addressForm.phone}
                  onChange={(event) => setAddressForm((prev) => ({ ...prev, phone: event.target.value }))}
                />
              </div>
            </div>

            <div className="modal-footer">
              <button type="button" className="cancel-btn" id="cancel-btn" onClick={handleCloseAddressModal}>
                Cancelar
              </button>
              <button type="submit" className="save-btn">Guardar</button>
            </div>
          </form>
        </div>
      </div>

      <Footer />
    </div>
  );
}

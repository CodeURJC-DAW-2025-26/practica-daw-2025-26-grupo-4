import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import { Chart } from "chart.js/auto";
import type { Route } from "./+types/dashboard";
import { Header } from "~/components/header";
import { useAuth } from "~/hooks/useAuth";
import { useInfiniteScroll } from "~/hooks/useInfiniteScroll";
import {
  deleteAdminUser,
  getAdminStats,
  getAdminUsers,
  updateAdminUserBanStatus,
  updateAdminUser,
} from "~/services/admin-service";
import type {
  AdminStatsResponseDTO,
  AdminUserDTO,
  AdminUserUpdateRequestDTO,
} from "~/api/dtos";
import { notifyError, notifySuccess } from "~/stores/global-notification-store";

import "~/styles/tokens.css";
import "~/styles/components.css";
import "~/styles/admin.css";

type UserAddressFields = {
  street: string;
  additional: string;
  city: string;
  province: string;
  postalCode: string;
  country: string;
  phone: string;
};

const EMPTY_ADDRESS: UserAddressFields = {
  street: "",
  additional: "",
  city: "",
  province: "",
  postalCode: "",
  country: "",
  phone: "",
};

const KNOWN_ADDRESS_LABELS = new Set([
  "Calle",
  "Información adicional",
  "Ciudad",
  "Provincia",
  "Código Postal",
  "País",
  "Teléfono",
]);

function createAdminChart(
  canvas: HTMLCanvasElement | null,
  type: "doughnut" | "bar" | "line",
  title: string,
  series: { labels: string[]; values: number[] } | undefined,
  chartInstances: Chart[],
) {
  if (!canvas || !series?.labels?.length || !series?.values?.length) {
    return;
  }

  const ctx = canvas.getContext("2d");
  if (!ctx) {
    return;
  }

  chartInstances.push(
    new Chart(ctx, {
      type,
      data: {
        labels: series.labels,
        datasets: [
          {
            label: title,
            data: series.values,
            backgroundColor: [
              "rgba(255, 99, 132, 0.6)",
              "rgba(54, 162, 235, 0.6)",
              "rgba(255, 206, 86, 0.6)",
              "rgba(75, 192, 192, 0.6)",
              "rgba(153, 102, 255, 0.6)",
              "rgba(255, 159, 64, 0.6)",
              "rgba(199, 199, 199, 0.6)",
              "rgba(83, 102, 255, 0.6)",
            ],
            borderWidth: 1,
            fill: type === "line",
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales:
          type === "doughnut"
            ? undefined
            : {
                y: {
                  beginAtZero: true,
                },
              },
      },
    }),
  );
}

function parseLabeledAddress(lines: string[]): UserAddressFields | null {
  const parsed: UserAddressFields = { ...EMPTY_ADDRESS };
  let hasLabels = false;
  const positionalLines: string[] = [];

  for (const line of lines) {
    const separatorIndex = line.indexOf(":");
    if (separatorIndex > 0) {
      const label = line.slice(0, separatorIndex).trim();
      const value = line.slice(separatorIndex + 1).trim();

      if (KNOWN_ADDRESS_LABELS.has(label)) {
        hasLabels = true;
        assignAddressField(parsed, label, value);
        continue;
      }
    }

    positionalLines.push(line);
  }

  if (!hasLabels) {
    return null;
  }

  if (!parsed.street && positionalLines.length > 0) {
    parsed.street = positionalLines.shift() ?? "";
  }

  if (!parsed.additional && positionalLines.length > 0) {
    parsed.additional = positionalLines.join("\n");
  }

  return parsed;
}

function assignAddressField(
  parsed: UserAddressFields,
  label: string,
  value: string,
) {
  switch (label) {
    case "Calle":
      parsed.street = value;
      break;
    case "Información adicional":
      parsed.additional = value;
      break;
    case "Ciudad":
      parsed.city = value;
      break;
    case "Provincia":
      parsed.province = value;
      break;
    case "Código Postal":
      parsed.postalCode = value;
      break;
    case "País":
      parsed.country = value;
      break;
    case "Teléfono":
      parsed.phone = value;
      break;
    default:
      break;
  }
}

function parsePositionalAddress(lines: string[]): UserAddressFields {
  const [street = "", ...rest] = lines;
  const remaining = [...rest];

  const phoneIdx = remaining.findIndex((line) => /^Tel[eé]fono:\s*/i.test(line));
  const phone = phoneIdx >= 0 ? remaining.splice(phoneIdx, 1)[0].replace(/^Tel[eé]fono:\s*/i, "").trim() : "";

  const locIdx = remaining.findIndex((line) => /^.+,\s*.+\s+[^,\s]+$/.test(line));
  let city = "";
  let province = "";
  let postalCode = "";
  if (locIdx >= 0) {
    const match = /^(.+),\s*(.+)\s+([^,\s]+)$/.exec(remaining.splice(locIdx, 1)[0]);
    if (match) {
      city = match[1].trim();
      province = match[2].trim();
      postalCode = match[3].trim();
    }
  }

  let country = "";
  let additional = "";

  if (remaining.length === 1) {
    additional = remaining[0];
  } else if (remaining.length > 1) {
    additional = remaining.slice(0, -1).join("\n");
    country = remaining.at(-1) ?? "";
  }

  return {
    street,
    additional,
    city,
    province,
    postalCode,
    country,
    phone,
  };
}

function parseAddress(address: string): UserAddressFields {
  if (!address?.trim()) {
    return EMPTY_ADDRESS;
  }

  const lines = address
    .split("\n")
    .map((line) => line.trim())
    .filter((line) => line.length > 0);

  const labeledAddress = parseLabeledAddress(lines);
  if (labeledAddress) {
    return labeledAddress;
  }

  return parsePositionalAddress(lines);
}

function composeAddress(address: UserAddressFields): string {
  const lines: string[] = [];

  if (address.street.trim()) {
    lines.push(`Calle: ${address.street.trim()}`);
  }

  if (address.additional.trim()) {
    lines.push(`Información adicional: ${address.additional.trim()}`);
  }

  if (address.city.trim()) {
    lines.push(`Ciudad: ${address.city.trim()}`);
  }

  if (address.province.trim()) {
    lines.push(`Provincia: ${address.province.trim()}`);
  }

  if (address.postalCode.trim()) {
    lines.push(`Código Postal: ${address.postalCode.trim()}`);
  }

  if (address.country.trim()) {
    lines.push(`País: ${address.country.trim()}`);
  }

  if (address.phone.trim()) {
    lines.push(`Teléfono: ${address.phone.trim()}`);
  }

  return lines.join("\n");
}

export function links(): Route.LinkDescriptors {
  return [
    {
      rel: "stylesheet",
      href: "https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap",
    },
    {
      rel: "stylesheet",
      href: "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css",
    },
  ];
}

export async function clientLoader() {
  const [stats, usersPage] = await Promise.all([getAdminStats(), getAdminUsers(0, 5)]);

  return {
    stats,
    usersPage: {
      content: usersPage.content,
      last: !usersPage.hasNext,
    },
  };
}

export default function AdminDashboard({ loaderData }: Route.ComponentProps) {
  const { stats, usersPage } = loaderData as {
    stats: AdminStatsResponseDTO;
    usersPage: { content: AdminUserDTO[]; last: boolean };
  };

  const { loading, isLogged, isAdmin } = useAuth();

  const [expandedUserIds, setExpandedUserIds] = useState<Set<number>>(new Set());
  const [forms, setForms] = useState<Record<number, AdminUserUpdateRequestDTO & UserAddressFields>>({});
  const [busyUserIds, setBusyUserIds] = useState<Set<number>>(new Set());

  const categoryCanvasRef = useRef<HTMLCanvasElement>(null);
  const tagsCanvasRef = useRef<HTMLCanvasElement>(null);
  const monthlyCanvasRef = useRef<HTMLCanvasElement>(null);
  const visitorsCanvasRef = useRef<HTMLCanvasElement>(null);
  const reviewsCanvasRef = useRef<HTMLCanvasElement>(null);
  const initialUsersPageRef = useRef(usersPage);

  const fetchMoreUsers = useCallback(async (page: number) => {
    const data = await getAdminUsers(page, 5);
    return {
      content: data.content,
      last: !data.hasNext,
    };
  }, []);

const {
  items: users,
  setItems: setUsers,
  loading: usersLoading,
  hasMore,
} = useInfiniteScroll(fetchMoreUsers, initialUsersPageRef.current, []);

  useEffect(() => {
    const nextForms: Record<number, AdminUserUpdateRequestDTO & UserAddressFields> = {};

    for (const user of users) {
      const address = parseAddress(user.shippingAddress ?? "");
      nextForms[user.id] = {
        username: user.username ?? "",
        email: user.email ?? "",
        fullName: user.fullName ?? "",
        birthDate: user.birthDate ?? "",
        shippingAddress: user.shippingAddress ?? "",
        ...address,
      };
    }

    setForms(nextForms);
  }, [users]);

useLayoutEffect(() => {
  const chartInstances: Chart[] = [];

  const timeoutId = globalThis.setTimeout(() => {
    createAdminChart(categoryCanvasRef.current, "doughnut", "Unidades por Categoría", stats.salesByCategory, chartInstances);
    createAdminChart(tagsCanvasRef.current, "doughnut", "Unidades por Etiqueta", stats.salesByTag, chartInstances);
    createAdminChart(monthlyCanvasRef.current, "bar", "Ventas Mensuales (€)", stats.monthlySales, chartInstances);
    createAdminChart(visitorsCanvasRef.current, "line", "Total Pedidos", stats.ordersByMonth, chartInstances);
    createAdminChart(reviewsCanvasRef.current, "line", "Nuevas Reseñas", stats.reviewsByMonth, chartInstances);
  }, 100);

  return () => {
    globalThis.clearTimeout(timeoutId);
    chartInstances.forEach((chart) => chart.destroy());
  };
}, [stats]);

  const disabledUsers = useMemo(() => busyUserIds, [busyUserIds]);

  const toggleUserEdit = (userId: number) => {
    setExpandedUserIds((current) => {
      const next = new Set(current);
      if (next.has(userId)) {
        next.delete(userId);
      } else {
        next.add(userId);
      }
      return next;
    });
  };

  const setUserBusy = (userId: number, busy: boolean) => {
    setBusyUserIds((current) => {
      const next = new Set(current);
      if (busy) {
        next.add(userId);
      } else {
        next.delete(userId);
      }
      return next;
    });
  };

  const updateFormField = (
    userId: number,
    field: keyof (AdminUserUpdateRequestDTO & UserAddressFields),
    value: string,
  ) => {
    setForms((current) => ({
      ...current,
      [userId]: {
        ...current[userId],
        [field]: value,
      },
    }));
  };

  const handleSaveUser = async (user: AdminUserDTO) => {
    const form = forms[user.id];
    if (!form) {
      return;
    }

    const payload: AdminUserUpdateRequestDTO = {
      username: form.username,
      email: form.email,
      fullName: form.fullName,
      birthDate: form.birthDate,
      shippingAddress: composeAddress(form),
    };

    setUserBusy(user.id, true);
    try {
      const updatedUser = await updateAdminUser(user.id, payload);

setUsers((currentUsers) =>
  currentUsers.map((currentUser) =>
    currentUser.id === user.id ? updatedUser : currentUser,
  ),
);
      notifySuccess("Usuario actualizado correctamente.");
      setExpandedUserIds((current) => {
        const next = new Set(current);
        next.delete(user.id);
        return next;
      });
    } catch (error) {
      notifyError(error instanceof Error ? error.message : "No se pudo actualizar el usuario.");
    } finally {
      setUserBusy(user.id, false);
    }
  };

const handleBanToggle = async (user: AdminUserDTO) => {
  setUserBusy(user.id, true);

  try {
    const updatedUser = await updateAdminUserBanStatus(
      user.id,
      user.banned ? "unban" : "ban",
    );

    setUsers((currentUsers) =>
      currentUsers.map((currentUser) =>
        currentUser.id === user.id ? updatedUser : currentUser,
      ),
    );

    notifySuccess(
      updatedUser.banned
        ? "Usuario baneado correctamente."
        : "Usuario desbaneado correctamente.",
    );
  } catch (error) {
    notifyError(error instanceof Error ? error.message : "No se pudo actualizar el estado del usuario.");
  } finally {
    setUserBusy(user.id, false);
  }
};

  const handleDeleteUser = async (user: AdminUserDTO) => {
  if (!globalThis.confirm(`¿Eliminar al usuario ${user.username}?`)) {
    return;
  }

  setUserBusy(user.id, true);

  try {
    await deleteAdminUser(user.id);

    setUsers((currentUsers) =>
      currentUsers.filter((currentUser) => currentUser.id !== user.id),
    );

    notifySuccess("Usuario eliminado correctamente.");
  } catch (error) {
    notifyError(error instanceof Error ? error.message : "No se pudo eliminar el usuario.");
  } finally {
    setUserBusy(user.id, false);
  }
};

  if (loading) {
    return null;
  }

  if (!isLogged || !isAdmin) {
    return (
      <div className="app-container">
        <Header />
        <main className="main-content">
          <div className="content-body">
            <h1 className="page-title">Sección de administrador</h1>
            <p style={{ color: "var(--color-muted)" }}>No tienes permisos para acceder a esta sección.</p>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="app-container">
      <Header />
      <main className="main-content">
        <div className="content-body">
          <h1 className="page-title">Sección de administrador</h1>

          <section className="admin-card statistics-section">
            <h2>Estadísticas y gráficos</h2>
            <div className="stats-subsection" style={{ display: "flex", flexDirection: "column", gap: "20px" }}>
              <div
                className="charts-container"
                style={{ display: "flex", gap: "20px", justifyContent: "space-around", flexWrap: "wrap" }}
              >
                <div
                  className="chart-wrapper"
                  style={{
                    width: "45%",
                    minWidth: "300px",
                    height: "500px",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    padding: "10px",
                    background: "#fff",
                    borderRadius: "8px",
                    boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
                  }}
                >
                  <h4 style={{ marginBottom: "15px" }}>Productos más comprados (Categoría):</h4>
                  <div style={{ flex: 1, width: "100%", position: "relative", minHeight: 0 }}>
                    <canvas ref={categoryCanvasRef}></canvas>
                  </div>
                </div>
                <div
                  className="chart-wrapper"
                  style={{
                    width: "45%",
                    minWidth: "300px",
                    height: "500px",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    padding: "10px",
                    background: "#fff",
                    borderRadius: "8px",
                    boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
                  }}
                >
                  <h4 style={{ marginBottom: "15px" }}>Productos por etiqueta:</h4>
                  <div style={{ flex: 1, width: "100%", position: "relative", minHeight: 0 }}>
                    <canvas ref={tagsCanvasRef}></canvas>
                  </div>
                </div>
              </div>

              <div
                className="charts-container"
                style={{
                  display: "flex",
                  gap: "20px",
                  justifyContent: "space-around",
                  flexWrap: "wrap",
                  marginTop: "20px",
                }}
              >
                <div
                  className="chart-wrapper"
                  style={{
                    width: "45%",
                    minWidth: "300px",
                    height: "500px",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    padding: "10px",
                    background: "#fff",
                    borderRadius: "8px",
                    boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
                  }}
                >
                  <h4 style={{ marginBottom: "15px" }}>Ventas mensuales (€):</h4>
                  <div style={{ flex: 1, width: "100%", position: "relative", minHeight: 0 }}>
                    <canvas ref={monthlyCanvasRef}></canvas>
                  </div>
                </div>
                <div
                  className="chart-wrapper"
                  style={{
                    width: "45%",
                    minWidth: "300px",
                    height: "500px",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    padding: "10px",
                    background: "#fff",
                    borderRadius: "8px",
                    boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
                  }}
                >
                  <h4 style={{ marginBottom: "15px" }}>Relación visitas-compra (Pedidos):</h4>
                  <div style={{ flex: 1, width: "100%", position: "relative", minHeight: 0 }}>
                    <canvas ref={visitorsCanvasRef}></canvas>
                  </div>
                </div>
              </div>

              <div
                className="charts-container"
                style={{
                  display: "flex",
                  gap: "20px",
                  justifyContent: "space-around",
                  flexWrap: "wrap",
                  marginTop: "20px",
                }}
              >
                <div
                  className="chart-wrapper"
                  style={{
                    width: "90%",
                    minWidth: "300px",
                    height: "500px",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    padding: "10px",
                    background: "#fff",
                    borderRadius: "8px",
                    boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
                  }}
                >
                  <h4 style={{ marginBottom: "15px" }}>Evolución de reseñas:</h4>
                  <div style={{ flex: 1, width: "100%", position: "relative", minHeight: 0 }}>
                    <canvas ref={reviewsCanvasRef}></canvas>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <section className="admin-card user-management">
            <h2>Gestión de usuarios</h2>

            <div id="users-list">
              {users.map((user) => {
                const userRoles = user.roles.join(", ");
                const isExpanded = expandedUserIds.has(user.id);
                const form = forms[user.id];
                const isBusy = disabledUsers.has(user.id);

                return (
                  <div className="user-result-card" key={user.id}>
                    <div className="user-card-header">
                      <div className="user-info">
                        <i className="fa-solid fa-circle-user user-avatar"></i>
                        <div>
                          <div className="user-name">{user.username}</div>
                          <div style={{ fontSize: "var(--font-size-sm)", color: "var(--color-muted)" }}>
                            {user.email}
                          </div>
                          <div style={{ fontSize: "var(--font-size-xs)", color: "var(--color-muted)" }}>
                            Rol: {userRoles}
                          </div>
                          {user.admin && (
                            <div style={{ fontSize: "var(--font-size-xs)", color: "#1976d2", fontWeight: "bold" }}>
                              <i className="fa-solid fa-shield"></i> ADMINISTRADOR
                            </div>
                          )}
                          {user.banned && (
                            <div style={{ fontSize: "var(--font-size-xs)", color: "#d32f2f", fontWeight: "bold" }}>
                              <i className="fa-solid fa-ban"></i> USUARIO BANEADO
                            </div>
                          )}
                        </div>
                      </div>

                      <div className="user-card-actions">
                        {!user.admin && (
                          <div className="action-buttons">
                            <button
                              type="button"
                              className={user.banned ? "accept-btn" : "block-btn"}
                              title={user.banned ? "Desbanear usuario" : "Banear usuario"}
                              onClick={() => handleBanToggle(user)}
                              disabled={isBusy}
                            >
                              <i className={`fa-solid ${user.banned ? "fa-check" : "fa-ban"}`}></i>
                            </button>

                            <button
                              type="button"
                              className="block-btn"
                              title="Eliminar usuario"
                              onClick={() => handleDeleteUser(user)}
                              disabled={isBusy}
                            >
                              <i className="fa-solid fa-trash"></i>
                            </button>
                          </div>
                        )}

                        {!user.admin && (
                          <button
                            type="button"
                            className={`expand-btn ${isExpanded ? "expanded" : ""}`}
                            onClick={() => toggleUserEdit(user.id)}
                          >
                            <i className="fa-solid fa-chevron-down"></i>
                          </button>
                        )}
                      </div>
                    </div>

                    {!user.admin && isExpanded && form && (
                      <div className="user-edit-form">
                        <div className="edit-form-grid">
                          <div className="form-group">
                            <label htmlFor={`name-${user.id}`}>Nombre de usuario</label>
                            <input
                              type="text"
                              id={`name-${user.id}`}
                              className="form-input"
                              value={form.username}
                              onChange={(event) => updateFormField(user.id, "username", event.target.value)}
                              required
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor={`email-${user.id}`}>Email</label>
                            <input
                              type="email"
                              id={`email-${user.id}`}
                              className="form-input"
                              value={form.email}
                              onChange={(event) => updateFormField(user.id, "email", event.target.value)}
                              required
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor={`fullName-${user.id}`}>Nombre completo</label>
                            <input
                              type="text"
                              id={`fullName-${user.id}`}
                              className="form-input"
                              value={form.fullName}
                              onChange={(event) => updateFormField(user.id, "fullName", event.target.value)}
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor={`birthDate-${user.id}`}>Fecha de nacimiento</label>
                            <input
                              type="date"
                              id={`birthDate-${user.id}`}
                              className="form-input"
                              value={form.birthDate ?? ""}
                              onChange={(event) => updateFormField(user.id, "birthDate", event.target.value)}
                            />
                          </div>

                          <div className="form-group form-group--wide">
                            <label htmlFor={`street-${user.id}`}>Calle y número</label>
                            <input
                              type="text"
                              id={`street-${user.id}`}
                              className="form-input"
                              placeholder="Ej: Calle Mayor 123"
                              value={form.street}
                              onChange={(event) => updateFormField(user.id, "street", event.target.value)}
                            />
                          </div>

                          <div className="form-group form-group--wide">
                            <label htmlFor={`additional-${user.id}`}>Información adicional</label>
                            <input
                              type="text"
                              id={`additional-${user.id}`}
                              className="form-input"
                              placeholder="Piso, puerta, bloque, etc."
                              value={form.additional}
                              onChange={(event) => updateFormField(user.id, "additional", event.target.value)}
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor={`city-${user.id}`}>Ciudad</label>
                            <input
                              type="text"
                              id={`city-${user.id}`}
                              className="form-input"
                              placeholder="Ej: Madrid"
                              value={form.city}
                              onChange={(event) => updateFormField(user.id, "city", event.target.value)}
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor={`province-${user.id}`}>Provincia</label>
                            <input
                              type="text"
                              id={`province-${user.id}`}
                              className="form-input"
                              placeholder="Ej: Madrid"
                              value={form.province}
                              onChange={(event) => updateFormField(user.id, "province", event.target.value)}
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor={`postalCode-${user.id}`}>Código Postal</label>
                            <input
                              type="text"
                              id={`postalCode-${user.id}`}
                              className="form-input"
                              placeholder="Ej: 28001"
                              value={form.postalCode}
                              onChange={(event) => updateFormField(user.id, "postalCode", event.target.value)}
                            />
                          </div>

                          <div className="form-group">
                            <label htmlFor={`country-${user.id}`}>País</label>
                            <input
                              type="text"
                              id={`country-${user.id}`}
                              className="form-input"
                              placeholder="Ej: España"
                              value={form.country}
                              onChange={(event) => updateFormField(user.id, "country", event.target.value)}
                            />
                          </div>

                          <div className="form-group form-group--wide">
                            <label htmlFor={`phone-${user.id}`}>Teléfono</label>
                            <input
                              type="tel"
                              id={`phone-${user.id}`}
                              className="form-input"
                              placeholder="Ej: 612345678"
                              value={form.phone}
                              onChange={(event) => updateFormField(user.id, "phone", event.target.value)}
                            />
                          </div>
                        </div>

                        <div className="edit-form-actions">
                          <button
                            type="button"
                            className="accept-btn"
                            onClick={() => handleSaveUser(user)}
                            disabled={isBusy}
                          >
                            <i className="fa-solid fa-save"></i> Guardar cambios
                          </button>
                          <button
                            type="button"
                            className="block-btn"
                            onClick={() => toggleUserEdit(user.id)}
                            disabled={isBusy}
                          >
                            <i className="fa-solid fa-times"></i> Cancelar
                          </button>
                        </div>
                      </div>
                    )}
                  </div>
                );
              })}

              {users.length === 0 && <p style={{ color: "var(--color-muted)" }}>No hay usuarios registrados.</p>}
            </div>
          </section>

          {usersLoading && (
            <div id="users-spinner" className="scroll-spinner" style={{ display: "flex" }}>
              <i className="fa-solid fa-spinner"></i> Cargando más usuarios...
            </div>
          )}
          {!hasMore && users.length > 0 && (
            <p style={{ textAlign: "center", marginTop: "20px", color: "gray" }}>No hay más usuarios.</p>
          )}
        </div>
      </main>
    </div>
  );
}

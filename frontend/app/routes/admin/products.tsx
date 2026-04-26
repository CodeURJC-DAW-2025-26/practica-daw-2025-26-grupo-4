import { useCallback, useMemo, useState } from "react";
import type { Route } from "./+types/products";
import { Header } from "~/components/header";
import { useAuth } from "~/hooks/useAuth";
import { useInfiniteScroll } from "~/hooks/useInfiniteScroll";
import type {
  AdminCategoryRequestDTO,
  CategoryDTO,
  ImageDTO,
  ProductDTO,
  ProductUpdateRequestDTO,
} from "~/api/dtos";
import {
  createAdminCategory,
  deleteAdminCategory,
  getAdminCategories,
  updateAdminCategory,
} from "~/services/admin-service";
import {
  addProduct,
  deleteProduct,
  deleteProductImage,
  getProduct,
  getProducts,
  updateProduct,
  uploadProductImage,
} from "~/services/products-service";
import { notifyError, notifySuccess } from "~/stores/global-notification-store";

import "~/styles/tokens.css";
import "~/styles/components.css";
import "~/styles/admin.css";

const MAX_IMAGE_MB = 5;
const ALLOWED_TYPES = new Set(["image/jpeg", "image/png", "image/webp", "image/gif", "image/avif"]);

function readFileAsDataUrl(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      if (typeof reader.result === "string") {
        resolve(reader.result);
        return;
      }

      reject(new Error("No se pudo leer la imagen"));
    };
    reader.onerror = () => reject(new Error("No se pudo leer la imagen"));
    reader.readAsDataURL(file);
  });
}

type ProductFormState = {
  name: string;
  price: string;
  description: string;
  tags: string;
  categoryId: string;
};

type CategoryFormState = {
  name: string;
  icon: string;
};

type ProductFormErrors = {
  name?: string;
  price?: string;
  description?: string;
  image?: string;
};

const EMPTY_PRODUCT_FORM: ProductFormState = {
  name: "",
  price: "",
  description: "",
  tags: "",
  categoryId: "",
};

const EMPTY_CATEGORY_FORM: CategoryFormState = {
  name: "",
  icon: "",
};

function validateProductFormState(
  productForm: ProductFormState,
  editingProductId: number | null,
  selectedFiles: File[],
): ProductFormErrors {
  const nextErrors: ProductFormErrors = {};

  const name = productForm.name.trim();
  if (!name) {
    nextErrors.name = "El nombre es obligatorio.";
  } else if (name.length < 2) {
    nextErrors.name = "Mínimo 2 caracteres.";
  } else if (name.length > 100) {
    nextErrors.name = "Máximo 100 caracteres.";
  }

  const price = Number.parseFloat(productForm.price.trim());
  if (!productForm.price.trim()) {
    nextErrors.price = "El precio es obligatorio.";
  } else if (!Number.isFinite(price) || price <= 0) {
    nextErrors.price = "Introduce un precio mayor que 0.";
  } else if (price > 99999) {
    nextErrors.price = "El precio no puede superar 99.999 €.";
  }

  if (productForm.description.length > 500) {
    nextErrors.description = "Máximo 500 caracteres.";
  }

  if (editingProductId === null && selectedFiles.length === 0) {
    nextErrors.image = "Debes subir al menos una imagen.";
  }

  return nextErrors;
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
  const [productsPage, categories] = await Promise.all([getProducts(0, 10), getAdminCategories()]);

  return {
    productsPage,
    categories,
  };
}

export default function AdminProducts({ loaderData }: Route.ComponentProps) {
  const { productsPage, categories: initialCategories } = loaderData as {
    productsPage: { content: ProductDTO[]; last: boolean };
    categories: CategoryDTO[];
  };

  const { loading, isLogged, isAdmin } = useAuth();

  const [categories, setCategories] = useState<CategoryDTO[]>(initialCategories);
  const [productForm, setProductForm] = useState<ProductFormState>(EMPTY_PRODUCT_FORM);
  const [categoryForm, setCategoryForm] = useState<CategoryFormState>(EMPTY_CATEGORY_FORM);
  const [productFormErrors, setProductFormErrors] = useState<ProductFormErrors>({});
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [previewUrls, setPreviewUrls] = useState<string[]>([]);
  const [editingProductId, setEditingProductId] = useState<number | null>(null);
  const [editingProductName, setEditingProductName] = useState("");
  const [editingProductImages, setEditingProductImages] = useState<ImageDTO[]>([]);
  const [editingCategoryId, setEditingCategoryId] = useState<number | null>(null);
  const [isProductSubmitting, setIsProductSubmitting] = useState(false);
  const [isCategorySubmitting, setIsCategorySubmitting] = useState(false);

  const fetchMoreProducts = useCallback(async (page: number) => getProducts(page, 10), []);

const {
  items: products,
  setItems: setProducts,
  loading: productsLoading,
  hasMore,
} = useInfiniteScroll(fetchMoreProducts, productsPage, [productsPage]);

  const descriptionLength = productForm.description.length;
  const fileLabel = useMemo(() => {
    if (selectedFiles.length === 0) {
      return "Seleccionar imágenes...";
    }

    if (selectedFiles.length === 1) {
      return selectedFiles[0].name;
    }

    return `${selectedFiles.length} imágenes seleccionadas`;
  }, [selectedFiles]);

  const validateProductForm = () => {
    const nextErrors = validateProductFormState(productForm, editingProductId, selectedFiles);
    setProductFormErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const resetImageSelection = () => {
    setSelectedFiles([]);
    setPreviewUrls([]);
  };

  const resetProductForm = () => {
    setEditingProductId(null);
    setEditingProductName("");
    setEditingProductImages([]);
    setProductForm(EMPTY_PRODUCT_FORM);
    setProductFormErrors({});
    resetImageSelection();
  };

  const resetCategoryForm = () => {
    setEditingCategoryId(null);
    setCategoryForm(EMPTY_CATEGORY_FORM);
  };

  const refreshCategories = async () => {
    const refreshed = await getAdminCategories();
    setCategories(refreshed);
  };

  const handleImageInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(event.target.files ?? []);

    for (const file of files) {
      if (!ALLOWED_TYPES.has(file.type)) {
        setProductFormErrors((current) => ({
          ...current,
          image: "Formato no válido. Usa JPG, PNG, WEBP o GIF.",
        }));
        return;
      }

      if (file.size > MAX_IMAGE_MB * 1024 * 1024) {
        setProductFormErrors((current) => ({
          ...current,
          image: `Cada imagen no puede superar los ${MAX_IMAGE_MB} MB.`,
        }));
        return;
      }
    }

    setProductFormErrors((current) => ({ ...current, image: undefined }));
    setSelectedFiles(files);

    const readers = files.map((file) => readFileAsDataUrl(file));

    Promise.all(readers)
      .then((urls) => setPreviewUrls(urls))
      .catch(() => setPreviewUrls([]));
  };

  const handleProductSubmit = async (event: React.SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!validateProductForm()) {
      return;
    }

    const payloadBase = {
      name: productForm.name.trim(),
      price: Number.parseFloat(productForm.price.trim()),
      description: productForm.description,
      tags: productForm.tags
        .split(",")
        .map((tag) => tag.trim())
        .filter(Boolean),
      categoryId: productForm.categoryId ? Number(productForm.categoryId) : null,
    };

    setIsProductSubmitting(true);
    try {
      if (editingProductId === null) {
        let created = await addProduct(payloadBase);

        for (const file of selectedFiles) {
          await uploadProductImage(created.id, file);
        }
        created = await getProduct(created.id);
        setProducts((currentProducts) => [created, ...currentProducts]);
        notifySuccess("Producto creado correctamente.");
      } else {
        const updatePayload: ProductUpdateRequestDTO = payloadBase;
        await updateProduct(editingProductId, updatePayload);

        for (const file of selectedFiles) {
          await uploadProductImage(editingProductId, file);
        }
        const updatedProduct = await getProduct(editingProductId);
        setProducts((currentProducts) =>
          currentProducts.map((product) =>
            product.id === editingProductId ? updatedProduct : product,
          ),
        );
        notifySuccess("Producto actualizado correctamente.");
      }

      resetProductForm();

    } catch (error) {
      notifyError(error instanceof Error ? error.message : "No se pudo guardar el producto.");
    } finally {
      setIsProductSubmitting(false);
    }
  };

  const handleDeleteProduct = async (product: ProductDTO) => {
    if (!globalThis.confirm(`¿Eliminar ${product.name}?`)) {
      return;
    }

    try {
      await deleteProduct(product.id);

      setProducts((currentProducts) =>
        currentProducts.filter((currentProduct) => currentProduct.id !== product.id),
      );

      notifySuccess("Producto eliminado correctamente.");
      
    } catch (error) {
      notifyError(error instanceof Error ? error.message : "No se pudo eliminar el producto.");
    }
  };

  const handleEditProduct = (product: ProductDTO) => {
    setEditingProductId(product.id);
    setEditingProductName(product.name);
    setEditingProductImages(product.images ?? []);
    setProductFormErrors({});
    setProductForm({
      name: product.name,
      price: String(product.price),
      description: product.description ?? "",
      tags: (product.tags ?? []).join(", "),
      categoryId: product.category?.id ? String(product.category.id) : "",
    });
    resetImageSelection();
  };

  const handleDeleteImage = async (productId: number, imageId: number) => {
    if (!globalThis.confirm("¿Borrar esta imagen?")) {
      return;
    }

    try {
      await deleteProductImage(productId, imageId);
      setEditingProductImages((current) => current.filter((img) => img.id !== imageId));
      notifySuccess("Imagen eliminada correctamente.");
    } catch (error) {
      notifyError(error instanceof Error ? error.message : "No se pudo eliminar la imagen.");
    }
  };

  const handleCategorySubmit = async (event: React.SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();

    const payload: AdminCategoryRequestDTO = {
      name: categoryForm.name.trim(),
      icon: categoryForm.icon.trim(),
    };

    if (!payload.name) {
      notifyError("El nombre de la categoría es obligatorio.");
      return;
    }

    setIsCategorySubmitting(true);
    try {
      if (editingCategoryId === null) {
        await createAdminCategory(payload);
        notifySuccess("Categoría creada correctamente.");
      } else {
        await updateAdminCategory(editingCategoryId, payload);
        notifySuccess("Categoría actualizada correctamente.");
      }

      resetCategoryForm();
      await refreshCategories();
    } catch (error) {
      notifyError(error instanceof Error ? error.message : "No se pudo guardar la categoría.");
    } finally {
      setIsCategorySubmitting(false);
    }
  };

  const handleDeleteCategory = async (category: CategoryDTO) => {
    if (!globalThis.confirm(`¿Eliminar categoría ${category.name}?`)) {
      return;
    }

    try {
      await deleteAdminCategory(category.id);
      notifySuccess("Categoría eliminada correctamente.");
      await refreshCategories();
    } catch (error) {
      notifyError(error instanceof Error ? error.message : "No se pudo eliminar la categoría.");
    }
  };

  const handleEditCategory = (category: CategoryDTO) => {
    setEditingCategoryId(category.id);
    setCategoryForm({
      name: category.name,
      icon: category.icon ?? "",
    });
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
            <h1 className="page-title">Gestor de productos</h1>
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
          <h1 className="page-title">Gestor de productos</h1>

          <section className="admin-card product-management">
            <h2 id="productFormTitle">
              {editingProductId === null
                ? "Añadir nuevo producto"
                : `Editar producto: ${editingProductName}`}
            </h2>
            <form id="createProductForm" className="product-create-form" noValidate onSubmit={handleProductSubmit}>
              <div className="form-grid">
                <div className="form-group">
                  <label htmlFor="name">
                    Nombre <span className="required">*</span>
                  </label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    placeholder="Ej: Monstera Deliciosa"
                    className={`form-input ${productFormErrors.name ? "form-input--error" : ""}`}
                    maxLength={100}
                    autoComplete="off"
                    value={productForm.name}
                    onChange={(event) => setProductForm((current) => ({ ...current, name: event.target.value }))}
                  />
                  <span className="form-error" style={{ display: productFormErrors.name ? "block" : "none" }}>
                    {productFormErrors.name}
                  </span>
                </div>

                <div className="form-group">
                  <label htmlFor="price">
                    Precio (€) <span className="required">*</span>
                  </label>
                  <input
                    type="number"
                    id="price"
                    name="price"
                    step="0.01"
                    min="0.01"
                    max="99999"
                    placeholder="0.00"
                    className={`form-input ${productFormErrors.price ? "form-input--error" : ""}`}
                    value={productForm.price}
                    onChange={(event) => setProductForm((current) => ({ ...current, price: event.target.value }))}
                  />
                  <span className="form-error" style={{ display: productFormErrors.price ? "block" : "none" }}>
                    {productFormErrors.price}
                  </span>
                </div>

                <div className="form-group form-group--wide">
                  <label htmlFor="description">
                    Descripción{" "}
                    <span
                      className="form-hint"
                      id="desc-counter"
                      style={{ float: "right", color: descriptionLength > 450 ? "var(--color-danger)" : undefined }}
                    >
                      {descriptionLength} / 500
                    </span>
                  </label>
                  <textarea
                    id="description"
                    name="description"
                    rows={3}
                    placeholder="Describe el producto..."
                    className={`form-input form-textarea ${productFormErrors.description ? "form-input--error" : ""}`}
                    maxLength={500}
                    value={productForm.description}
                    onChange={(event) =>
                      setProductForm((current) => ({ ...current, description: event.target.value }))
                    }
                  ></textarea>
                  <span
                    className="form-error"
                    style={{ display: productFormErrors.description ? "block" : "none" }}
                  >
                    {productFormErrors.description}
                  </span>
                </div>

                <div className="form-group">
                  <label htmlFor="tags">Etiquetas</label>
                  <input
                    type="text"
                    id="tags"
                    name="tags"
                    placeholder="Verde, Interior, Tropical"
                    className="form-input"
                    value={productForm.tags}
                    onChange={(event) => setProductForm((current) => ({ ...current, tags: event.target.value }))}
                  />
                  <span className="form-hint">Separadas por comas</span>
                </div>

                <div className="form-group">
                  <label htmlFor="categoryId">Categoría</label>
                  <select
                    id="categoryId"
                    name="categoryId"
                    className="admin-select form-input"
                    value={productForm.categoryId}
                    onChange={(event) =>
                      setProductForm((current) => ({ ...current, categoryId: event.target.value }))
                    }
                  >
                    <option value="">— Sin categoría —</option>
                    {categories.map((category) => (
                      <option key={category.id} value={category.id}>
                        {category.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group form-group--wide">
                  {editingProductId !== null && (
                    <>
                      <p>Imágenes actuales</p>
                      <div id="current-images-container" className="current-images-grid">
                        {editingProductImages.length > 0 ? (
                          editingProductImages.map((image) => (
                            <div className="current-image-wrapper" key={image.id}>
                              <img src={image.url} alt="Imagen del producto" />
                              <button
                                type="button"
                                className="image-delete-btn"
                                onClick={() => handleDeleteImage(editingProductId, image.id)}
                              >
                                <i className="fa-solid fa-xmark"></i>
                              </button>
                            </div>
                          ))
                        ) : (
                          <p style={{ color: "#666", fontSize: "0.9em", fontStyle: "italic" }}>
                            Sin imágenes actuales
                          </p>
                        )}
                      </div>
                    </>
                  )}

                  <label htmlFor="images" style={{ marginTop: "1rem" }}>
                    Añadir nuevas imágenes
                  </label>
                  <div className="file-upload-wrapper">
                    <input
                      type="file"
                      id="images"
                      name="images"
                      accept="image/*"
                      multiple
                      className="file-input"
                      onChange={handleImageInputChange}
                    />
                    <label htmlFor="images" className="file-upload-label">
                      <i className="fa-solid fa-cloud-arrow-up"></i>
                      <span id="file-label-text">{fileLabel}</span>
                    </label>
                  </div>
                  <span className="form-hint">Puedes seleccionar varias. Máx. 5 MB por imagen.</span>
                  <span className="form-error" style={{ display: productFormErrors.image ? "block" : "none" }}>
                    {productFormErrors.image}
                  </span>
                  <div
                    id="image-preview-container"
                    className="image-preview-container"
                    style={{ display: previewUrls.length ? "flex" : "none" }}
                  >
                    {previewUrls.map((url, index) => (
                      <img key={`${url}-${index}`} src={url} className="image-preview" alt="Preview" />
                    ))}
                  </div>
                </div>
              </div>

              <div className="form-actions">
                <button type="submit" id="productSubmitBtn" className="action-btn primary-action" disabled={isProductSubmitting}>
                  {editingProductId === null ? (
                    <>
                      <i className="fa-solid fa-plus"></i> Crear producto
                    </>
                  ) : (
                    <>
                      <i className="fa-solid fa-floppy-disk"></i> Guardar Cambios
                    </>
                  )}
                </button>

                {editingProductId !== null && (
                  <button
                    type="button"
                    id="productCancelBtn"
                    className="action-btn secondary-action"
                    onClick={resetProductForm}
                    disabled={isProductSubmitting}
                  >
                    Cancelar
                  </button>
                )}

                {editingProductId === null && (
                  <button
                    type="button"
                    id="productResetBtn"
                    className="action-btn secondary-action"
                    onClick={resetProductForm}
                    disabled={isProductSubmitting}
                  >
                    <i className="fa-solid fa-rotate-left"></i> Limpiar
                  </button>
                )}
              </div>
            </form>
          </section>

          <section className="admin-card category-management">
            <h2>Gestión de Categorías</h2>
            <div className="category-manager-layout" style={{ display: "flex", gap: "2rem", flexWrap: "wrap" }}>
              <div className="create-category-col" style={{ flex: 1, minWidth: "300px" }}>
                <h3 id="categoryFormTitle">
                  {editingCategoryId === null ? "Añadir nueva categoría" : `Editar categoría: ${categoryForm.name}`}
                </h3>
                <form id="categoryForm" onSubmit={handleCategorySubmit}>
                  <div className="form-group">
                    <label htmlFor="catName">
                      Nombre <span className="required">*</span>
                    </label>
                    <input
                      type="text"
                      id="catName"
                      name="name"
                      className="form-input"
                      required
                      placeholder="Ej: Herramientas"
                      value={categoryForm.name}
                      onChange={(event) =>
                        setCategoryForm((current) => ({ ...current, name: event.target.value }))
                      }
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="catIcon">
                      Icono (Clase FontAwesome){" "}
                      <a
                        href="https://fontawesome.com/search?o=r&m=free"
                        target="_blank"
                        rel="noopener noreferrer"
                        style={{ fontSize: "0.85em", marginLeft: "0.5rem", textDecoration: "underline" }}
                      >
                        <i className="fa-solid fa-arrow-up-right-from-square"></i> Ver lista
                      </a>
                    </label>
                    <input
                      type="text"
                      id="catIcon"
                      name="icon"
                      className="form-input"
                      placeholder="Ej: fa-solid fa-seedling"
                      value={categoryForm.icon}
                      onChange={(event) =>
                        setCategoryForm((current) => ({ ...current, icon: event.target.value }))
                      }
                    />
                  </div>
                  <div style={{ display: "flex", gap: "0.5rem", marginTop: "1rem" }}>
                    <button type="submit" id="categorySubmitBtn" className="action-btn primary-action" disabled={isCategorySubmitting}>
                      {editingCategoryId === null ? (
                        <>
                          <i className="fa-solid fa-plus"></i> Añadir Categoría
                        </>
                      ) : (
                        <>
                          <i className="fa-solid fa-floppy-disk"></i> Guardar Cambios
                        </>
                      )}
                    </button>

                    {editingCategoryId !== null && (
                      <button
                        type="button"
                        id="categoryCancelBtn"
                        className="action-btn secondary-action"
                        onClick={resetCategoryForm}
                        disabled={isCategorySubmitting}
                      >
                        Cancelar
                      </button>
                    )}
                  </div>
                </form>
              </div>

              <div className="list-category-col" style={{ flex: 1, minWidth: "300px" }}>
                <h3>Categorías existentes</h3>
                <ul className="category-list" style={{ listStyle: "none", padding: 0 }}>
                  {categories.map((category) => (
                    <li
                      key={category.id}
                      style={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        padding: "0.75rem",
                        borderBottom: "1px solid var(--border-color)",
                      }}
                    >
                      <span>
                        {category.icon ? <i className={category.icon} style={{ marginRight: "0.5rem" }}></i> : null}
                        <strong>{category.name}</strong>
                      </span>
                      <div style={{ display: "flex", gap: "0.5rem" }}>
                        <button
                          type="button"
                          className="action-btn secondary-action icon-only"
                          onClick={() => handleEditCategory(category)}
                          title="Editar categoría"
                        >
                          <i className="fa-solid fa-pen"></i>
                        </button>
                        <button
                          type="button"
                          className="action-btn danger-action icon-only"
                          onClick={() => handleDeleteCategory(category)}
                          title="Eliminar categoría"
                        >
                          <i className="fa-solid fa-trash"></i>
                        </button>
                      </div>
                    </li>
                  ))}
                  {categories.length === 0 && (
                    <li style={{ padding: "1rem", color: "var(--text-color-light)", textAlign: "center" }}>
                      No hay categorías.
                    </li>
                  )}
                </ul>
              </div>
            </div>
          </section>

          <section className="admin-card">
            <h2>Productos</h2>
            <table className="admin-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Categoría</th>
                  <th>Precio</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody id="products-tbody">
                {products.map((product) => (
                  <tr key={product.id}>
                    <td>{product.id}</td>
                    <td>{product.name}</td>
                    <td>{product.category?.name ?? "Sin categoría"}</td>
                    <td>{product.price} €</td>
                    <td>
                      <div style={{ display: "flex", gap: "0.5rem", alignItems: "center" }}>
                        <button
                          type="button"
                          className="action-btn secondary-action"
                          onClick={() => handleEditProduct(product)}
                        >
                          <i className="fa-solid fa-pen"></i> Editar
                        </button>
                        <button
                          type="button"
                          className="action-btn danger-action"
                          onClick={() => handleDeleteProduct(product)}
                        >
                          <i className="fa-solid fa-trash"></i> Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
                {products.length === 0 && (
                  <tr>
                    <td colSpan={5} style={{ textAlign: "center", padding: "var(--space-5)" }}>
                      No hay productos disponibles.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
            {productsLoading && (
              <div id="products-spinner" className="scroll-spinner" style={{ display: "flex" }}>
                <i className="fa-solid fa-spinner"></i> Cargando más productos...
              </div>
            )}
            {!hasMore && products.length > 0 && (
              <p style={{ textAlign: "center", marginTop: "20px", color: "gray" }}>No hay más productos.</p>
            )}
          </section>
        </div>
      </main>
    </div>
  );
}

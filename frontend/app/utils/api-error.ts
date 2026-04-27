function getHttpStatusMessage(status: number) {
  switch (status) {
    case 400:
      return "Solicitud no valida";
    case 401:
      return "Debes iniciar sesion";
    case 403:
      return "No tienes permisos para realizar esta accion";
    case 404:
      return "Recurso no encontrado";
    case 409:
      return "Conflicto en la solicitud";
    case 413:
      return "El archivo supera el tamaño máximo permitido";
    case 422:
      return "Datos invalidos";
    case 500:
      return "Error interno del servidor";
    case 502:
    case 503:
    case 504:
      return "El servidor no esta disponible temporalmente";
    default:
      return "Se produjo un error en la solicitud";
  }
}

function looksLikeHtml(content: string) {
  const sample = content.trim().slice(0, 300).toLowerCase();
  return sample.includes("<!doctype html") || sample.includes("<html") || /<\/?[a-z][\s\S]*>/i.test(sample);
}

export async function getApiErrorMessage(response: Response, fallbackMessage: string) {
  const statusMessage = getHttpStatusMessage(response.status);

  try {
    const clonedResponse = response.clone();
    const contentType = clonedResponse.headers.get("content-type") ?? "";

    if (contentType.includes("application/json")) {
      const payload = (await clonedResponse.json()) as Record<string, unknown>;

      const candidate = payload.message ?? payload.error ?? payload.detail;
      if (typeof candidate === "string" && candidate.trim()) {
        return candidate.trim();
      }
    }

    if (contentType.includes("text/html")) {
      return statusMessage;
    }

    const text = (await clonedResponse.text()).trim();
    if (text) {
      if (looksLikeHtml(text)) {
        return statusMessage;
      }

      return text;
    }
  } catch {
    // Keep fallback when the body cannot be parsed.
  }

  if (fallbackMessage && !looksLikeHtml(fallbackMessage)) {
    return fallbackMessage;
  }

  return statusMessage;
}
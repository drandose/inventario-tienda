const API_BASE = "/api";

async function solicitar(ruta, opciones = {}) {
    const respuesta = await fetch(`${API_BASE}${ruta}`, {
        ...opciones,
        headers: {
            Accept: "application/json",
            ...opciones.headers
        }
    });

    if (!respuesta.ok) {
        let mensaje = `Error del servidor (${respuesta.status}).`;
        try {
            const datos = await respuesta.json();
            if (typeof datos.error === "string" && datos.error.trim()) {
                mensaje = datos.error;
            }
        } catch {
            // Conserva el mensaje genérico si el servidor no envía JSON.
        }
        throw new Error(mensaje);
    }

    if (respuesta.status === 204) {
        return null;
    }
    return respuesta.json();
}

function enviarJson(ruta, metodo, datos) {
    return solicitar(ruta, {
        method: metodo,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(datos)
    });
}

export const listarProductos = () => solicitar("/productos");
export const listarCategorias = () => solicitar("/categorias");
export const crearProducto = producto => enviarJson("/productos", "POST", producto);
export const actualizarProducto = (id, producto) => enviarJson(`/productos/${id}`, "PUT", producto);
export const borrarProducto = id => solicitar(`/productos/${id}`, { method: "DELETE" });
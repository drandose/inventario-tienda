import {
    actualizarProveedor,
    borrarProveedor,
    crearProveedor,
    listarProveedores
} from "./api.js";

const formulario = document.querySelector("#formulario-proveedor");
const filasProveedores = document.querySelector("#filas-proveedores");
const botonGuardar = document.querySelector("#guardar-proveedor");
const mensajes = document.querySelector("#mensajes");
let proveedores = [];
let proveedorEnEdicion = null;

function mostrarMensaje(texto = "", tipo = "") {
    mensajes.textContent = texto;
    mensajes.className = tipo ? `mensajes ${tipo}` : "mensajes";
}

function mostrarError(error) {
    mostrarMensaje(error.message || "Se produjo un error inesperado.", "error");
}

function crearCelda(texto) {
    const celda = document.createElement("td");
    celda.textContent = texto || "—";
    return celda;
}

function crearBoton(texto, accion, id, claseExtra = "") {
    const boton = document.createElement("button");
    boton.type = "button";
    boton.className = `boton-accion ${claseExtra}`.trim();
    boton.dataset.action = accion;
    boton.dataset.supplierId = String(id);
    boton.textContent = texto;
    return boton;
}

function renderizarProveedores() {
    filasProveedores.replaceChildren();
    if (proveedores.length === 0) {
        const fila = document.createElement("tr");
        const celda = crearCelda("No hay proveedores para mostrar.");
        celda.colSpan = 4;
        fila.append(celda);
        filasProveedores.append(fila);
        return;
    }

    for (const proveedor of proveedores) {
        const fila = document.createElement("tr");
        fila.append(
            crearCelda(proveedor.nombre),
            crearCelda(proveedor.telefono),
            crearCelda(proveedor.email)
        );
        const celdaAcciones = document.createElement("td");
        const acciones = document.createElement("div");
        acciones.className = "acciones-tabla";
        acciones.append(
            crearBoton("Editar", "editar", proveedor.id),
            crearBoton("Borrar", "borrar", proveedor.id, "boton-borrar")
        );
        celdaAcciones.append(acciones);
        fila.append(celdaAcciones);
        filasProveedores.append(fila);
    }
}

async function cargarProveedores() {
    const fila = document.createElement("tr");
    const celda = crearCelda("Cargando...");
    celda.colSpan = 4;
    fila.append(celda);
    filasProveedores.replaceChildren(fila);
    try {
        proveedores = await listarProveedores();
        renderizarProveedores();
        return true;
    } catch (error) {
        filasProveedores.replaceChildren();
        mostrarError(error);
        return false;
    }
}

function iniciarEdicion(proveedor) {
    proveedorEnEdicion = proveedor.id;
    formulario.elements.nombre.value = proveedor.nombre;
    formulario.elements.telefono.value = proveedor.telefono || "";
    formulario.elements.email.value = proveedor.email || "";
    document.querySelector("#titulo-formulario-proveedor").textContent = "Editar proveedor";
    botonGuardar.textContent = "Guardar cambios";
    mostrarMensaje(`Editando ${proveedor.nombre}.`);
    formulario.elements.nombre.focus();
}

function limpiarFormulario() {
    proveedorEnEdicion = null;
    document.querySelector("#titulo-formulario-proveedor").textContent = "Nuevo proveedor";
    botonGuardar.textContent = "Guardar proveedor";
}

formulario.addEventListener("reset", () => {
    limpiarFormulario();
    mostrarMensaje();
});

formulario.addEventListener("invalid", evento => {
    mostrarMensaje(evento.target.validationMessage, "error");
}, true);

formulario.addEventListener("submit", async evento => {
    evento.preventDefault();
    const datos = new FormData(formulario);
    const proveedor = {
        nombre: datos.get("nombre").trim(),
        telefono: datos.get("telefono").trim() || null,
        email: datos.get("email").trim() || null
    };
    botonGuardar.disabled = true;
    mostrarMensaje("Guardando proveedor...");
    try {
        const esNuevo = proveedorEnEdicion === null;
        if (esNuevo) {
            await crearProveedor(proveedor);
        } else {
            await actualizarProveedor(proveedorEnEdicion, proveedor);
        }
        formulario.reset();
        if (await cargarProveedores()) {
            mostrarMensaje(esNuevo ? "Proveedor creado correctamente." : "Proveedor actualizado correctamente.", "exito");
        }
    } catch (error) {
        mostrarError(error);
    } finally {
        botonGuardar.disabled = false;
    }
});

filasProveedores.addEventListener("click", async evento => {
    const boton = evento.target.closest("button[data-action]");
    if (!boton) {
        return;
    }
    const proveedor = proveedores.find(item => String(item.id) === boton.dataset.supplierId);
    if (!proveedor) {
        return;
    }
    if (boton.dataset.action === "editar") {
        iniciarEdicion(proveedor);
        return;
    }
    if (boton.dataset.action === "borrar"
        && window.confirm(`¿Seguro que quieres borrar el proveedor «${proveedor.nombre}»?`)) {
        mostrarMensaje("Borrando proveedor...");
        try {
            await borrarProveedor(proveedor.id);
            if (await cargarProveedores()) {
                mostrarMensaje("Proveedor borrado correctamente.", "exito");
            }
        } catch (error) {
            mostrarError(error);
        }
    }
});

cargarProveedores();
import {
    actualizarCategoria,
    borrarCategoria,
    crearCategoria,
    listarCategorias
} from "./api.js";

const formulario = document.querySelector("#formulario-categoria");
const filasCategorias = document.querySelector("#filas-categorias");
const botonGuardar = document.querySelector("#guardar-categoria");
const mensajes = document.querySelector("#mensajes");
let categorias = [];
let categoriaEnEdicion = null;

function mostrarMensaje(texto = "", tipo = "") {
    mensajes.textContent = texto;
    mensajes.className = tipo ? `mensajes ${tipo}` : "mensajes";
}

function mostrarError(error) {
    mostrarMensaje(error.message || "Se produjo un error inesperado.", "error");
}

function crearCelda(texto) {
    const celda = document.createElement("td");
    celda.textContent = texto;
    return celda;
}

function crearBoton(texto, accion, id, claseExtra = "") {
    const boton = document.createElement("button");
    boton.type = "button";
    boton.className = `boton-accion ${claseExtra}`.trim();
    boton.dataset.action = accion;
    boton.dataset.categoryId = String(id);
    boton.textContent = texto;
    return boton;
}

function renderizarCategorias() {
    filasCategorias.replaceChildren();
    if (categorias.length === 0) {
        const fila = document.createElement("tr");
        const celda = crearCelda("No hay categorías para mostrar.");
        celda.colSpan = 2;
        fila.append(celda);
        filasCategorias.append(fila);
        return;
    }

    for (const categoria of categorias) {
        const fila = document.createElement("tr");
        fila.append(crearCelda(categoria.nombre));
        const celdaAcciones = document.createElement("td");
        const acciones = document.createElement("div");
        acciones.className = "acciones-tabla";
        acciones.append(
            crearBoton("Editar", "editar", categoria.id),
            crearBoton("Borrar", "borrar", categoria.id, "boton-borrar")
        );
        celdaAcciones.append(acciones);
        fila.append(celdaAcciones);
        filasCategorias.append(fila);
    }
}

async function cargarCategorias(mostrarCarga = true) {
    if (mostrarCarga) {
        const fila = document.createElement("tr");
        const celda = crearCelda("Cargando...");
        celda.colSpan = 2;
        fila.append(celda);
        filasCategorias.replaceChildren(fila);
    }
    try {
        categorias = await listarCategorias();
        renderizarCategorias();
        return true;
    } catch (error) {
        filasCategorias.replaceChildren();
        mostrarError(error);
        return false;
    }
}

function iniciarEdicion(categoria) {
    categoriaEnEdicion = categoria.id;
    formulario.elements.nombre.value = categoria.nombre;
    document.querySelector("#titulo-formulario-categoria").textContent = "Editar categoría";
    botonGuardar.textContent = "Guardar cambios";
    mostrarMensaje(`Editando ${categoria.nombre}.`);
    formulario.elements.nombre.focus();
}

function limpiarFormulario() {
    categoriaEnEdicion = null;
    document.querySelector("#titulo-formulario-categoria").textContent = "Nueva categoría";
    botonGuardar.textContent = "Guardar categoría";
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
    const categoria = { nombre: new FormData(formulario).get("nombre").trim() };
    botonGuardar.disabled = true;
    mostrarMensaje("Guardando categoría...");
    try {
        const esNueva = categoriaEnEdicion === null;
        if (esNueva) {
            await crearCategoria(categoria);
            document.dispatchEvent(new Event("categorias-actualizadas"));
        } else {
            await actualizarCategoria(categoriaEnEdicion, categoria);
        }
        formulario.reset();
        if (await cargarCategorias(false)) {
            mostrarMensaje(esNueva ? "Categoría creada correctamente." : "Categoría actualizada correctamente.", "exito");
        }
    } catch (error) {
        mostrarError(error);
    } finally {
        botonGuardar.disabled = false;
    }
});

filasCategorias.addEventListener("click", async evento => {
    const boton = evento.target.closest("button[data-action]");
    if (!boton) {
        return;
    }
    const categoria = categorias.find(item => String(item.id) === boton.dataset.categoryId);
    if (!categoria) {
        return;
    }
    if (boton.dataset.action === "editar") {
        iniciarEdicion(categoria);
        return;
    }
    if (boton.dataset.action === "borrar"
        && window.confirm(`¿Seguro que quieres borrar la categoría «${categoria.nombre}»?`)) {
        mostrarMensaje("Borrando categoría...");
        try {
            await borrarCategoria(categoria.id);
            document.dispatchEvent(new Event("categorias-actualizadas"));
            if (await cargarCategorias(false)) {
                mostrarMensaje("Categoría borrada correctamente.", "exito");
            }
        } catch (error) {
            mostrarError(error);
        }
    }
});

cargarCategorias();
import {
    actualizarProducto,
    asignarProveedores,
    borrarProducto,
    crearProducto,
    listarCategorias,
    listarProductos,
    listarProveedores,
    listarProveedoresDeProducto
} from "./api.js";

const formulario = document.querySelector("#formulario-producto");
const filasProductos = document.querySelector("#filas-productos");
const selectorCategoria = document.querySelector("#categoriaId");
const botonGuardar = document.querySelector("#guardar-producto");
const mensajes = document.querySelector("#mensajes");
const botonesNavegacion = document.querySelectorAll("[data-section-target]");
const secciones = {
    productos: document.querySelector("#seccion-productos"),
    categorias: document.querySelector("#seccion-categorias"),
    proveedores: document.querySelector("#seccion-proveedores")
};

let productos = [];
let productoEnEdicion = null;
let productoProveedoresAbierto = null;

function mostrarMensaje(texto = "", tipo = "") {
    mensajes.textContent = texto;
    mensajes.className = tipo ? `mensajes ${tipo}` : "mensajes";
}

function mostrarError(error) {
    mostrarMensaje(error.message || "Se produjo un error inesperado.", "error");
}

function cambiarSeccion(nombre) {
    for (const [clave, seccion] of Object.entries(secciones)) {
        seccion.hidden = clave !== nombre;
    }
    for (const boton of botonesNavegacion) {
        const activo = boton.dataset.sectionTarget === nombre;
        boton.classList.toggle("activo", activo);
        boton.setAttribute("aria-pressed", String(activo));
    }
}

function cargarCategorias(categorias) {
    selectorCategoria.replaceChildren(new Option("Selecciona una categoría", ""));
    for (const categoria of categorias) {
        selectorCategoria.add(new Option(categoria.nombre, categoria.id));
    }
    selectorCategoria.disabled = categorias.length === 0;
    botonGuardar.disabled = categorias.length === 0;
    if (categorias.length === 0) {
        mostrarMensaje("Crea una categoría antes de añadir productos.", "error");
    }
}

function crearCelda(texto) {
    const celda = document.createElement("td");
    celda.textContent = texto;
    return celda;
}

function renderizarProductos() {
    filasProductos.replaceChildren();
    productoProveedoresAbierto = null;
    if (productos.length === 0) {
        const fila = document.createElement("tr");
        const celda = crearCelda("No hay productos para mostrar.");
        celda.colSpan = 6;
        fila.append(celda);
        filasProductos.append(fila);
        return;
    }

    const formatoPrecio = new Intl.NumberFormat("es-ES", {
        style: "currency",
        currency: "EUR"
    });

    for (const producto of productos) {
        const fila = document.createElement("tr");
        fila.append(
            crearCelda(producto.codigo),
            crearCelda(producto.nombre),
            crearCelda(producto.categoriaNombre || "—"),
            crearCelda(formatoPrecio.format(Number(producto.precio))),
            crearCelda(String(producto.stock))
        );

        const celdaAcciones = document.createElement("td");
        const acciones = document.createElement("div");
        acciones.className = "acciones-tabla";
        acciones.append(
            crearBotonAccion("Editar", "editar", producto.id),
            crearBotonAccion("Proveedores", "proveedores", producto.id),
            crearBotonAccion("Borrar", "borrar", producto.id, "boton-borrar")
        );
        celdaAcciones.append(acciones);
        fila.append(celdaAcciones);
        filasProductos.append(fila);
    }
}

function crearBotonAccion(texto, accion, id, claseExtra = "") {
    const boton = document.createElement("button");
    boton.type = "button";
    boton.className = `boton-accion ${claseExtra}`.trim();
    boton.dataset.action = accion;
    boton.dataset.productId = String(id);
    boton.textContent = texto;
    return boton;
}

function crearFilaSeleccionProveedores(productoId, proveedores, proveedoresAsignados) {
    const fila = document.createElement("tr");
    fila.dataset.providerProductId = String(productoId);
    const celda = document.createElement("td");
    celda.colSpan = 6;
    const grupo = document.createElement("fieldset");
    grupo.className = "selector-proveedores";
    const leyenda = document.createElement("legend");
    leyenda.textContent = "Proveedores del producto";
    grupo.append(leyenda);

    const idsAsignados = new Set(proveedoresAsignados.map(proveedor => String(proveedor.id)));
    for (const proveedor of proveedores) {
        const etiqueta = document.createElement("label");
        const casilla = document.createElement("input");
        casilla.type = "checkbox";
        casilla.value = String(proveedor.id);
        casilla.checked = idsAsignados.has(String(proveedor.id));
        etiqueta.append(casilla, document.createTextNode(` ${proveedor.nombre}`));
        grupo.append(etiqueta);
    }

    const botonGuardarProveedores = document.createElement("button");
    botonGuardarProveedores.type = "button";
    botonGuardarProveedores.className = "boton boton-principal";
    botonGuardarProveedores.dataset.action = "guardar-proveedores";
    botonGuardarProveedores.dataset.productId = String(productoId);
    botonGuardarProveedores.textContent = "Guardar selección";
    grupo.append(botonGuardarProveedores);
    celda.append(grupo);
    fila.append(celda);
    return fila;
}

async function alternarProveedores(productoId, boton) {
    const filaActual = filasProductos.querySelector("tr[data-provider-product-id]");
    if (Number(productoProveedoresAbierto) === Number(productoId)) {
        filaActual?.remove();
        productoProveedoresAbierto = null;
        boton.textContent = "Proveedores";
        return;
    }

    filaActual?.remove();
    productoProveedoresAbierto = productoId;
    boton.textContent = "Cerrar proveedores";
    const filaProducto = boton.closest("tr");
    const filaCarga = document.createElement("tr");
    filaCarga.dataset.providerProductId = String(productoId);
    const celdaCarga = crearCelda("Cargando proveedores...");
    celdaCarga.colSpan = 6;
    filaCarga.append(celdaCarga);
    filaProducto.after(filaCarga);
    mostrarMensaje("Cargando proveedores...");

    try {
        const [proveedores, asignados] = await Promise.all([
            listarProveedores(),
            listarProveedoresDeProducto(productoId)
        ]);
        if (Number(productoProveedoresAbierto) === Number(productoId) && filaCarga.isConnected) {
            filaCarga.replaceWith(crearFilaSeleccionProveedores(productoId, proveedores, asignados));
            mostrarMensaje();
        }
    } catch (error) {
        filaCarga.remove();
        if (Number(productoProveedoresAbierto) === Number(productoId)) {
            productoProveedoresAbierto = null;
            boton.textContent = "Proveedores";
            mostrarError(error);
        }
    }
}

async function cargarDatos() {
    mostrarMensaje("Cargando...");
    selectorCategoria.disabled = true;
    botonGuardar.disabled = true;
    const filaCarga = document.createElement("tr");
    const celdaCarga = crearCelda("Cargando...");
    celdaCarga.colSpan = 6;
    filaCarga.append(celdaCarga);
    filasProductos.replaceChildren(filaCarga);
    try {
        const [categorias, listadoProductos] = await Promise.all([
            listarCategorias(),
            listarProductos()
        ]);
        cargarCategorias(categorias);
        productos = listadoProductos;
        renderizarProductos();
        if (categorias.length > 0) {
            mostrarMensaje();
        }
        return true;
    } catch (error) {
        filasProductos.replaceChildren();
        mostrarError(error);
        return false;
    }
}

async function refrescarCategorias() {
    try {
        const categoriaSeleccionada = selectorCategoria.value;
        const categorias = await listarCategorias();
        cargarCategorias(categorias);
        if (categorias.some(categoria => String(categoria.id) === categoriaSeleccionada)) {
            selectorCategoria.value = categoriaSeleccionada;
        }
    } catch (error) {
        mostrarError(error);
    }
}

function iniciarEdicion(producto) {
    productoEnEdicion = producto.id;
    formulario.elements.codigo.value = producto.codigo;
    formulario.elements.nombre.value = producto.nombre;
    formulario.elements.precio.value = producto.precio;
    formulario.elements.stock.value = producto.stock;
    selectorCategoria.value = String(producto.categoriaId);
    document.querySelector("#titulo-formulario").textContent = "Editar producto";
    botonGuardar.textContent = "Guardar cambios";
    mostrarMensaje(`Editando ${producto.nombre}.`);
    formulario.elements.codigo.focus();
}

function limpiarFormulario() {
    productoEnEdicion = null;
    document.querySelector("#titulo-formulario").textContent = "Nuevo producto";
    botonGuardar.textContent = "Guardar producto";
}

botonesNavegacion.forEach(boton => {
    boton.addEventListener("click", () => cambiarSeccion(boton.dataset.sectionTarget));
});

document.addEventListener("categorias-actualizadas", refrescarCategorias);

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
    const producto = {
        codigo: datos.get("codigo").trim(),
        nombre: datos.get("nombre").trim(),
        precio: Number(datos.get("precio")),
        stock: Number(datos.get("stock")),
        categoriaId: Number(datos.get("categoriaId"))
    };

    botonGuardar.disabled = true;
    mostrarMensaje("Guardando producto...");
    try {
        let mensajeExito;
        if (productoEnEdicion === null) {
            await crearProducto(producto);
            mensajeExito = "Producto creado correctamente.";
        } else {
            await actualizarProducto(productoEnEdicion, producto);
            mensajeExito = "Producto actualizado correctamente.";
        }
        formulario.reset();
        if (await cargarDatos()) {
            mostrarMensaje(mensajeExito, "exito");
        }
    } catch (error) {
        mostrarError(error);
    } finally {
        botonGuardar.disabled = selectorCategoria.disabled;
    }
});

filasProductos.addEventListener("click", async evento => {
    const boton = evento.target.closest("button[data-action]");
    if (!boton) {
        return;
    }

    const producto = productos.find(item => String(item.id) === boton.dataset.productId);
    if (!producto) {
        return;
    }

    if (boton.dataset.action === "proveedores") {
        for (const botonProveedores of filasProductos.querySelectorAll('button[data-action="proveedores"]')) {
            botonProveedores.textContent = "Proveedores";
        }
        await alternarProveedores(producto.id, boton);
        return;
    }

    if (boton.dataset.action === "guardar-proveedores") {
        const filaProveedores = boton.closest("tr[data-provider-product-id]");
        const proveedorIds = Array.from(filaProveedores.querySelectorAll('input[type="checkbox"]:checked'))
            .map(casilla => Number(casilla.value));
        boton.disabled = true;
        mostrarMensaje("Guardando proveedores...");
        try {
            await asignarProveedores(producto.id, proveedorIds);
            mostrarMensaje("Proveedores asignados correctamente.", "exito");
        } catch (error) {
            mostrarError(error);
        } finally {
            boton.disabled = false;
        }
        return;
    }

    if (boton.dataset.action === "editar") {
        iniciarEdicion(producto);
        return;
    }

    if (boton.dataset.action === "borrar"
        && window.confirm(`¿Seguro que quieres borrar el producto «${producto.nombre}»?`)) {
        mostrarMensaje("Borrando producto...");
        try {
            await borrarProducto(producto.id);
            if (await cargarDatos()) {
                mostrarMensaje("Producto borrado correctamente.", "exito");
            }
        } catch (error) {
            mostrarError(error);
        }
    }
});

cargarDatos();
// project.js
document.addEventListener("DOMContentLoaded", function() {
    const modalTask = document.getElementById("modalTask");
    const btnNewTask = document.getElementById("btnNewTask");
    const formNewTask = document.getElementById("formNewTask");
    const projectID = document.getElementById("project-info").dataset.projectid;
    let currentTaskId = null; // Para guardar el id de la tarea seleccionada
    let clickInsideModal = false; // Para recoger si el usr hace clic dentro del modal

    // Función para asignar eventos a los botones
    function asignarEventosBotones() {
        // Botones "Más opciones"
        document.querySelectorAll(".btnMoreOptions").forEach(button => {
            button.removeEventListener("click", handleMoreOptionsClick);
            button.addEventListener("click", handleMoreOptionsClick);
        });
        // Botones "Eliminar"
        document.querySelectorAll(".btnDeleteTask").forEach(button => {
            button.removeEventListener("click", handleDeleteTask);
            button.addEventListener("click", handleDeleteTask);
        });
        // Botones "Editar"
        document.querySelectorAll(".btnEditTask").forEach(button => {
            button.removeEventListener("click", handleEditTask);
            button.addEventListener("click", handleEditTask);
        });
        // Cerrar modal haciendo clic en el fondo
        document.querySelectorAll(".modal").forEach(modal => {
            modal.removeEventListener("mousedown", handleModalMouseDown);
            modal.removeEventListener("mouseup", handleModalMouseUp);
            modal.addEventListener("mousedown", handleModalMouseDown);
            modal.addEventListener("mouseup", handleModalMouseUp);
        });
    }

    // Manejador de "Más opciones" (abre el modal)
    function handleMoreOptionsClick(event) {
        currentTaskId = event.currentTarget.dataset.taskid;
        console.log("currentTaskId = " + currentTaskId);

        const taskItem = event.currentTarget.closest(".task-item");
        const modal = taskItem.querySelector(".modal");

        if (modal) {
            modal.style.display = "flex";

            // 🔹 Solo asignamos el evento si no está ya asignado
            if (!modal.dataset.eventAssigned) {
                modal.addEventListener("mousedown", handleModalMouseDown);
                modal.addEventListener("mouseup", handleModalMouseUp);
                modal.dataset.eventAssigned = "true"; // Marcar como asignado
            }
        }
    }

    // Función de eliminación de tarea
    function handleDeleteTask(event) {
        const taskId = event.target.dataset.taskid;
        if (!taskId) {
            console.error("No se ha seleccionado una tarea para eliminar.");
            return;
        }
        console.log("Eliminando tarea con ID: " + taskId);

        // Aplicar animación de eliminación
        const taskItem = event.target.closest(".task-item");
        taskItem.style.transition = "opacity 0.3s ease-out";
        taskItem.style.opacity = "0";

        fetch(`/project/${projectID}/delete_task?taskId=${taskId}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.message) {
                    console.log("Tarea eliminada correctamente");
                    document.querySelector(`[data-taskid='${taskId}']`).remove();
                    actualizarListaTareas(); // 🔹 Llamar función que actualiza la lista de tareas
                } else {
                    console.error("Error al eliminar la tarea");
                }
            })
            .catch(error => console.error("Error en la petición:", error));
    }
    function actualizarListaTareas() {
        fetch(`/project/${projectID}`) // 🔹 Obtener las tareas actualizadas del backend
            .then(response => response.text())
            .then(html => {
                document.getElementById("task-list").innerHTML =
                    new DOMParser().parseFromString(html, "text/html")
                        .querySelector("#task-list").innerHTML;
                asignarEventosBotones(); // 🔹 Reasignar eventos
            })
            .catch(error => console.error("Error al actualizar la lista de tareas:", error));
    }

    function handleEditTask(event) {
        currentTaskId = event.currentTarget.dataset.taskid;

        const taskItem = event.currentTarget.closest(".task-item");
        const title = taskItem.querySelector(".task-content b").innerText;
        const description = taskItem.querySelector(".task-content").textContent.split(":")[1]?.trim() || "";

        // Obtener la imagen si existe
        const imageElement = taskItem.querySelector(".task-image");
        const imagePath = imageElement ? imageElement.style.backgroundImage.replace('url("', '').replace('")', '') : "";

        // Rellenar el formulario con los valores actuales
        formNewTask.querySelector("input[name='title']").value = title;
        formNewTask.querySelector("textarea[name='description']").value = description;
        formNewTask.querySelector("input[name='image']").value = null; // Limpiar input de imagen

        // Agregar o actualizar el campo oculto de imagePath
        let hiddenImageInput = formNewTask.querySelector("input[name='imagePath']");
        // Agregar imagePath solo si existe una imagen
        if (!hiddenImageInput && imagePath) {
            hiddenImageInput = document.createElement("input");
            hiddenImageInput.type = "hidden";
            hiddenImageInput.name = "imagePath";
            formNewTask.appendChild(hiddenImageInput);
        }
        if (hiddenImageInput) {
            hiddenImageInput.value = imagePath || ""; // No asigna "" si no hay imagen
        }

        // cerrar modal opciones
        const modal = taskItem.querySelector(".modalOptions");
        if (modal) {
            modal.style.display = "none";
        }
        // Mostrar el modal
        modalTask.style.display = "flex";
    }

    function handleModalMouseDown(event) {
        // Verifica si el clic empezó dentro del contenido del modal
        if (event.target.closest(".modal-content") || event.target.closest("#formNewTask")) {
            clickInsideModal = true;
        } else {
            clickInsideModal = false;
        }
    }
    function handleModalMouseUp(event) {
        // Solo cerrar si el clic NO empezó dentro del modal
        if (!clickInsideModal && event.target.classList.contains("modal")) {
            event.target.style.display = "none";
        }
    }

    // Mostrar el modal de "Nueva Tarea"
    btnNewTask.addEventListener("click", function() {
        currentTaskId = null; // Indicar que es una nueva tarea

        // Vaciar los campos del formulario
        formNewTask.querySelector("input[name='title']").value = "";
        formNewTask.querySelector("textarea[name='description']").value = "";
        formNewTask.querySelector("input[name='image']").value = ""; // Limpiar campo de imagen

        // Si existe un campo oculto de imagePath (de edición anterior), eliminarlo
        const hiddenImageInput = formNewTask.querySelector("input[name='imagePath']");
        if (hiddenImageInput) {
            hiddenImageInput.remove();
        }
        modalTask.style.display = "flex";
    });

    // Enviar datos por AJAX y actualizar la lista de tareas
    formNewTask.addEventListener("submit", function(event) {
        event.preventDefault();

        const formData = new FormData(formNewTask);
        formData.append("projectID", projectID);

        if (currentTaskId) {
            formData.append("taskId", currentTaskId);

            fetch(`/project/${projectID}/edit_task`, {
                method: "PUT",
                body: formData
            })
                .then(response => response.json())
                .then(data => {
                    console.log(data);
                    location.reload();
                })
                .catch(error => console.error("Error al actualizar la tarea:", error));
        } else {
            fetch(`/project/${projectID}/save_task`, {
                method: "POST",
                body: formData
            })
                .then(response => response.text())
                .then(data => {
                    console.log(data);
                    location.reload();
                })
                .catch(error => console.error("Error al guardar la tarea:", error));
        }
    });

    // Asignamos eventos al cargar la página
    asignarEventosBotones();
});

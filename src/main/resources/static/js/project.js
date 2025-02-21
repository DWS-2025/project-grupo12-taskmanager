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

        // Cerrar el modal antes de eliminar
        const taskItem = event.target.closest(".task-item");
        const modal = taskItem.querySelector(".modal");
        if (modal) {
            modal.style.display = "none"; // Ocultar modal antes de eliminar la tarea
        }

        // Buscar la tarea correcta
        if (!taskItem) {
            console.warn("No se encontró la tarea en el DOM.");
            return;
        }
        // Aplicar animación de eliminación
        taskItem.style.transition = "opacity 0.3s ease-out";
        taskItem.style.opacity = "0";

        fetch(`/project/${projectID}/delete_task?taskId=${taskId}`, {
            method: "DELETE",
        })
            .then(response => {
                if (response.ok) {
                    console.log("Tarea eliminada correctamente");

                    setTimeout(() => {
                        taskItem.remove(); // Eliminar la tarea del DOM
                        asignarEventosBotones(); // Reasignar eventos para evitar problemas futuros
                    }, 300);
                } else {
                    console.error("Error al eliminar la tarea");
                }
            })
            .catch(error => console.error("Error en la petición:", error));
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

        // Agregar imagePath a un campo oculto para mantener la imagen original si no se cambia
        let hiddenImageInput = formNewTask.querySelector("input[name='imagePath']");
        if (!hiddenImageInput) {
            hiddenImageInput = document.createElement("input");
            hiddenImageInput.type = "hidden";
            hiddenImageInput.name = "imagePath";
            formNewTask.appendChild(hiddenImageInput);
        }
        hiddenImageInput.value = imagePath;

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

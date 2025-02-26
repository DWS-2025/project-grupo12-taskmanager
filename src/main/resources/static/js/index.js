document.addEventListener("DOMContentLoaded", function() {
    const modalProject = document.getElementById("modalProject");
    const btnNewProject = document.getElementById("btnNewProject");
    const formNewProject = document.getElementById("formNewProject");
    let currentProjectId = null;
    let clickInsideModal = false;

    // Función para asignar eventos a los botones
    function asignarEventosBotonesProyectos() {
        document.querySelectorAll(".btnMoreOptions").forEach(button => {
            button.removeEventListener("click", handleMoreOptionsClick);
            button.addEventListener("click", handleMoreOptionsClick);
        });

        document.querySelectorAll(".btnDeleteProject").forEach(button => {
            button.removeEventListener("click", handleDeleteProject);
            button.addEventListener("click", handleDeleteProject);
        });

        document.querySelectorAll(".btnEditProject").forEach(button => {
            button.removeEventListener("click", handleEditProject);
            button.addEventListener("click", handleEditProject);
        });

        document.querySelectorAll(".modal").forEach(modal => {
            modal.removeEventListener("mousedown", handleModalMouseDown);
            modal.removeEventListener("mouseup", handleModalMouseUp);
            modal.addEventListener("mousedown", handleModalMouseDown);
            modal.addEventListener("mouseup", handleModalMouseUp);
        });
    }

    // Abrir modal con opciones (Editar/Eliminar)
    function handleMoreOptionsClick(event) {
        currentProjectId = event.currentTarget.dataset.projectid;
        const projectItem = event.currentTarget.closest(".project-item");
        const modal = projectItem.querySelector(".modalOptions");

        if (modal) {
            modal.classList.remove("hidden");
            modal.style.display = "flex";
        }
    }

    // Eliminar proyecto
    function handleDeleteProject(event) {
        const projectId = event.target.dataset.projectid;
        if (!projectId) {
            console.error("No se ha seleccionado un proyecto para eliminar.");
            return;
        }


        // Aplicar animación de eliminación
        const taskItem = event.target.closest(".project-item");
        taskItem.style.transition = "opacity 0.3s ease-out";
        taskItem.style.opacity = "0";


        fetch(`/project/${projectId}/delete_project`, {
            method: "DELETE",
        })
            .then(response => {
                if (response.ok) {
                    setTimeout(() => {
                        document.querySelector(`[data-projectid='${projectId}']`).remove(); // 🔹 Se elimina después del desvanecimiento
                    }, 300);
                } else {
                    console.error("Error al eliminar el proyecto");
                }
            })
            .catch(error => console.error("Error en la petición:", error));
    }

    // Editar proyecto
    function handleEditProject(event) {
        currentProjectId = event.currentTarget.dataset.projectid;
        const projectItem = event.currentTarget.closest(".project-item");
        const projectName = projectItem.querySelector("b").innerText;
        formNewProject.querySelector("input[name='name']").value = projectName;
        modalProject.style.display = "flex";
    }

    // Abrir modal para crear un nuevo proyecto
    btnNewProject.addEventListener("click", function() {
        currentProjectId = null;
        formNewProject.querySelector("input[name='name']").value = "";
        modalProject.style.display = "flex";
    });

    // Cerrar modales
    function handleModalMouseDown(event) {
        clickInsideModal = !!event.target.closest(".modal-content");
    }

    function handleModalMouseUp(event) {
        if (!clickInsideModal && event.target.classList.contains("modal")) {
            event.target.classList.add("hidden");
            event.target.style.display = "none";
        }
    }

    // Guardar proyecto (crear o editar)
    formNewProject.addEventListener("submit", function(event) {
        event.preventDefault();

        const formData = new URLSearchParams();
        formData.append("name", document.getElementById("name").value);
        formData.append("userId", document.querySelector("input[name='userId']").value);
        let url = "/save_project";
        let method = "POST";

        if (currentProjectId) {
            url = `/project/{id}/edit_project`;
            method = "PUT";
            formData.append("projectId", currentProjectId);
        }

        fetch(url, {
            method: method,
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: formData.toString()
        })
            .then(response => response.text())
            .then(data => location.reload())
            .catch(error => console.error("Error al guardar/actualizar proyecto:", error));
    });

    asignarEventosBotonesProyectos();
});

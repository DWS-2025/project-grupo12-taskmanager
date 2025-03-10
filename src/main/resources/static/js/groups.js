document.addEventListener("DOMContentLoaded", function () {
    const modalGroup = document.getElementById("modalGroup");
    const modalTitle = modalGroup.querySelector("h2");
    const btnNewGroup = document.getElementById("btnNewGroup");
    const formNewGroup = document.getElementById("formNewGroup");
    const inputGroupName = formNewGroup.querySelector("input[name='name']");
    let currentGroupId = null;
    let clickInsideModal = false;

    // Función para asignar eventos a los botones de grupos
    function assignGroupButtonEvents() {
        document.querySelectorAll(".btnMoreOptions").forEach(button => {
            button.removeEventListener("click", handleMoreOptionsClick);
            button.addEventListener("click", handleMoreOptionsClick);
        });

        document.querySelectorAll(".btnLeaveGroup").forEach(button => {
            button.removeEventListener("click", handleLeaveGroup);
            button.addEventListener("click", handleLeaveGroup);
        });

        document.querySelectorAll(".btnDeleteGroup").forEach(button => {
            button.removeEventListener("click", handleDeleteGroup);
            button.addEventListener("click", handleDeleteGroup);
        });

        document.querySelectorAll(".btnManageMembers").forEach(button => {
            button.removeEventListener("click", handleManageMembers);
            button.addEventListener("click", handleManageMembers);
        });

        document.querySelectorAll(".btnEditGroup").forEach(button => {
            button.removeEventListener("click", handleEditGroup);
            button.addEventListener("click", handleEditGroup);
        });

        document.querySelectorAll(".modal").forEach(modal => {
            modal.removeEventListener("mousedown", handleModalMouseDown);
            modal.removeEventListener("mouseup", handleModalMouseUp);
            modal.addEventListener("mousedown", handleModalMouseDown);
            modal.addEventListener("mouseup", handleModalMouseUp);
        });
    }

    // Abrir modal con opciones del grupo (Editar/Salir/Eliminar)
    function handleMoreOptionsClick(event) {
        currentGroupId = event.currentTarget.dataset.groupid;
        const groupItem = event.currentTarget.closest(".group-item");
        const modal = groupItem.querySelector(".modalOptions");

        if (modal) {
            modal.classList.remove("hidden");
            modal.style.display = "flex";
        }
    }

    // Salir del grupo
    function handleLeaveGroup(event) {
        const groupId = event.target.dataset.groupid;
        if (!groupId) {
            console.error("No se ha seleccionado un grupo para salir.");
            return;
        }

        if (confirm("¿Estás seguro de que deseas salir de este grupo?")) {
            fetch(`/leave_group/${groupId}`, {
                method: "POST",
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        location.reload();
                    } else {
                        console.error("Error al salir del grupo");
                    }
                })
                .catch(error => console.error("Error en la petición:", error));
        }
    }

    // Eliminar grupo
    function handleDeleteGroup(event) {
        const groupId = event.target.dataset.groupid;
        if (!groupId) {
            console.error("No se ha seleccionado un grupo para eliminar.");
            return;
        }

        if (confirm("¿Estás seguro de que deseas eliminar este grupo? Esta acción es irreversible.")) {
            fetch(`/delete_group/${groupId}`, {
                method: "POST",
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        location.reload();
                    } else {
                        alert("Error al eliminar el grupo");
                    }
                })
                .catch(error => console.error("Error en la petición:", error));
        }
    }

    // Gestionar miembros
    function handleManageMembers(event) {
        const groupId = event.target.dataset.groupid;
        if (!groupId) {
            console.error("No se ha seleccionado un grupo.");
            return;
        }
        window.location.href = `/manage_members/${groupId}`;
    }

    // Editar grupo
    function handleEditGroup(event) {
        currentGroupId = event.currentTarget.dataset.groupid;
        const groupItem = event.currentTarget.closest(".group-item");
        inputGroupName.value = groupItem.querySelector("b").innerText; // Mostrar nombre actual en el input
        modalTitle.innerText = "Cambiar Nombre"; // Cambiar título del modal
        modalGroup.style.display = "flex";

        const modal = groupItem.querySelector(".modalOptions");
        if (modal) {
            modal.classList.add("hidden");
            modal.style.display = "none";
        }
    }

    // Abrir modal para crear un nuevo grupo
    function openNewGroupModal() {
        currentGroupId = null;
        inputGroupName.value = ""; // Vaciar el input
        modalTitle.innerText = "Nuevo Grupo"; // Restablecer título del modal
        modalGroup.style.display = "flex";
    }

    // Cerrar modales al hacer clic fuera
    function handleModalMouseDown(event) {
        clickInsideModal = !!event.target.closest(".modal-content");
    }

    function handleModalMouseUp(event) {
        if (!clickInsideModal && event.target.classList.contains("modal")) {
            event.target.classList.add("hidden");
            event.target.style.display = "none";
        }
    }

    // Guardar grupo (crear o editar)
    function saveGroup(event) {
        event.preventDefault();

        const formData = new URLSearchParams();
        formData.append("name", document.getElementById("name").value);
        formData.append("userId", document.querySelector("input[name='userId']").value);
        let url = "/save_group";
        let method = "POST";

        if (currentGroupId) {
            url = `/edit_group/${currentGroupId}`;
            method = "POST";
            formData.append("groupId", currentGroupId);
        }

        fetch(url, {
            method: method,
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: formData.toString()
        })
            .then(response => response.text())
            .then(() => location.reload())
            .catch(error => console.error("Error al guardar/actualizar grupo:", error));
    }

    // Asignación de eventos
    function assignEvents() {
        btnNewGroup.addEventListener("click", openNewGroupModal);
        formNewGroup.addEventListener("submit", saveGroup);
        assignGroupButtonEvents();
    }

    assignEvents();
});

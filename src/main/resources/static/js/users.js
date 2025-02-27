document.addEventListener("DOMContentLoaded", function () {
    const btnNewMember = document.getElementById("btnNewMember");
    const modalSearchUsers = document.getElementById("modalSearchUsers");
    const searchUserInput = document.getElementById("searchUserInput");
    const userSearchResults = document.getElementById("userSearchResults");
    const btnAddSelectedUsers = document.getElementById("btnAddSelectedUsers");
    let selectedUsers = new Set();
    let currentUserId = null;
    let clickInsideModal = false;

    function assignUserButtonEvents() {
        document.querySelectorAll(".btnMoreOptions").forEach(button => {
            button.removeEventListener("click", handleMoreOptionsClick);
            button.addEventListener("click", handleMoreOptionsClick);
        });

        document.querySelectorAll(".btnDeleteMember").forEach(button => {
            button.removeEventListener("click", handleDeleteMember);
            button.addEventListener("click", handleDeleteMember);
        });

        document.querySelectorAll(".modal").forEach(modal => {
            modal.removeEventListener("mousedown", handleModalMouseDown);
            modal.removeEventListener("mouseup", handleModalMouseUp);
            modal.addEventListener("mousedown", handleModalMouseDown);
            modal.addEventListener("mouseup", handleModalMouseUp);
        });

        btnNewMember.removeEventListener("click", openSearchModal);
        btnNewMember.addEventListener("click", openSearchModal);

        searchUserInput.removeEventListener("input", handleSearchUsers);
        searchUserInput.addEventListener("input", handleSearchUsers);

        btnAddSelectedUsers.removeEventListener("click", handleAddSelectedUsers);
        btnAddSelectedUsers.addEventListener("click", handleAddSelectedUsers);
    }

    function openSearchModal() {
        modalSearchUsers.classList.remove("hidden");
        modalSearchUsers.style.display = "flex"
    }


    function handleMoreOptionsClick(event) {
        currentUserId = event.currentTarget.dataset.userid;
        const userItem = event.currentTarget.closest(".user-item");
        const modal = userItem.querySelector(".modalOptions");

        if (modal) {
            modal.classList.remove("hidden");
            modal.style.display = "flex";
        }
    }

    function handleDeleteMember(event) {
        const userId = event.target.dataset.userid;
        const groupId = document.body.dataset.groupid;
        if (!userId || !groupId) {
            console.error("No se ha seleccionado un usuario o grupo para eliminar.");
            return;
        }

        if (confirm("¿Estás seguro de que deseas eliminar este miembro del grupo?")) {
            fetch(`/delete_member/${userId}?groupId=${groupId}`, {
                method: "POST",
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        location.reload();
                    } else {
                        alert("Error al eliminar el miembro");
                    }
                })
                .catch(error => console.error("Error en la petición:", error));
        }
    }

    function handleModalMouseDown(event) {
        clickInsideModal = !!event.target.closest(".modal-content");
    }

    function handleModalMouseUp(event) {
        if (!clickInsideModal && event.target.classList.contains("modal")) {
            event.target.classList.add("hidden");
            event.target.style.display = "none";
        }
    }

    function handleSearchUsers() {
        const query = searchUserInput.value.trim();
        if (query.length < 2) {
            userSearchResults.innerHTML = "";
            return;
        }

        fetch(`/search_users?q=${encodeURIComponent(query)}`)
            .then(response => response.json())
            .then(users => {
                userSearchResults.innerHTML = "";
                users.forEach(user => {
                    const li = document.createElement("li");
                    li.textContent = user.name;
                    li.dataset.userid = user.id;
                    li.addEventListener("click", function () {
                        if (selectedUsers.has(user.id)) {
                            selectedUsers.delete(user.id);
                            li.classList.remove("selected");
                        } else {
                            selectedUsers.add(user.id);
                            li.classList.add("selected");
                        }
                    });
                    userSearchResults.appendChild(li);
                });
            })
            .catch(error => console.error("Error en la búsqueda de usuarios:", error));
    }

    function handleAddSelectedUsers() {
        if (selectedUsers.size === 0) {
            alert("No hay usuarios seleccionados.");
            return;
        }

        const groupId = document.body.dataset.groupid;
        if (!groupId) {
            console.error("No se encontró el ID del grupo.");
            return;
        }

        fetch("/add_members", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ groupId, userIds: Array.from(selectedUsers) })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    location.reload();
                } else {
                    alert("Error al agregar miembros");
                }
            })
            .catch(error => console.error("Error en la petición:", error));
    }

    function assignEvents() {
        btnNewMember.addEventListener("click", function () {
            modalSearchUsers.classList.remove("hidden");
        });
        searchUserInput.addEventListener("input", handleSearchUsers);
        btnAddSelectedUsers.addEventListener("click", handleAddSelectedUsers);
        assignUserButtonEvents();
    }

    assignEvents();
});

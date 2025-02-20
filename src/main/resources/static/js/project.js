document.addEventListener("DOMContentLoaded", function() {
    const taskModal = document.getElementById("modalTask");
    const btnNewTask = document.getElementById("btnNewTask");
    const closeTaskModal = taskModal.querySelector(".close");
    const formNewTask = document.getElementById("formNewTask");
    const projectID = document.getElementById("project-info").dataset.projectid;

    // Mostrar el modal cuando se hace clic en el botón
    btnNewTask.addEventListener("click", function() {
        taskModal.style.display = "flex";
    });

    // Cerrar el modal al hacer clic en la 'X'
    closeTaskModal.addEventListener("click", function() {
        taskModal.style.display = "none";
    });

    // Cerrar el modal si se hace clic fuera de él
    window.addEventListener("click", function(event) {
        if (event.target === taskModal) {
            taskModal.style.display = "none";
        }
    });

    // Enviar datos por AJAX y actualizar la lista de tareas
    formNewTask.addEventListener("submit", function(event) {
        event.preventDefault();

        const formData = new FormData(formNewTask);
        formData.append("projectID", projectID);

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
    });


});

document.addEventListener("DOMContentLoaded", function() {
    console.log("JavaScript cargado correctamente");

    const modal = document.getElementById("modalProject");
    const btn = document.getElementById("btnNewProject");
    const span = document.querySelector(".close");

    // Mostrar el modal cuando se hace clic en el botón
    btn.addEventListener("click", function() {
        modal.style.display = "flex";
    });

    // Cerrar el modal al hacer clic en la 'X'
    span.addEventListener("click", function() {
        modal.style.display = "none";
    });

    // Cerrar el modal si se hace clic fuera de él
    window.addEventListener("click", function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });
});

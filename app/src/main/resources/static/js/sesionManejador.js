async function obtenerTiempoSesion() {
    try {
        const resp = await fetch('/admin/config/tiempo-sesion');
        if (!resp.ok) throw new Error("Error al obtener tiempo");
        const segundos = await resp.json();
        return segundos;
    } catch (err) {
        console.error("Fallo al obtener tiempo de sesion:", err);
        return 60; // valor por defecto
    }
}

function hacerLogout() {
    const form = document.createElement("form");
    form.method = "POST";
    form.action = "/credenciales/logout";
    document.body.appendChild(form);
    form.submit();
}

async function iniciarControlSesion() {
    let tiempoRestante = sessionStorage.getItem("tiempoRestante");

    if (!tiempoRestante) {
        tiempoRestante = await obtenerTiempoSesion();
        sessionStorage.setItem("tiempoRestante", tiempoRestante);
    } else {
        tiempoRestante = parseInt(tiempoRestante, 10);
    }

    const intervalo = setInterval(async () => {
        tiempoRestante--;
        sessionStorage.setItem("tiempoRestante", tiempoRestante);

        // Cuando el tiempo llega a 0, preguntar qué hacer
        if (tiempoRestante <= 0) {
            clearInterval(intervalo);
            sessionStorage.removeItem("tiempoRestante");

            const deseaRenovar = confirm("Tu sesion ha expirado.\n¿Deseas renovarla?");
            if (deseaRenovar) {
                const nuevoTiempo = await obtenerTiempoSesion();
                sessionStorage.setItem("tiempoRestante", nuevoTiempo);
                alert("Sesion renovada exitosamente");
                iniciarControlSesion(); // reinicia el contador
            } else {
                alert("¡Sesion finalizada, volveras a la pagina principal y puedes volver a iniciar sesion!.");
                sessionStorage.removeItem("tiempoRestante");
                hacerLogout();
            }
        }
    }, 1000);
}


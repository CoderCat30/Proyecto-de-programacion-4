package com.tienda.app.service;

import com.tienda.app.model.Configuracion;
import com.tienda.app.repository.ConfiguracionRepository;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracionService {
    private final ConfiguracionRepository configuracionRepository;

    public ConfiguracionService(ConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    public Configuracion obtenerConfiguracion() {
        Configuracion con =  configuracionRepository.findByClave("tiempo_sesion");
        if (con == null) {
            con =  new Configuracion();
            con.setClave("tiempo_sesion");
            con.setValorSegundos(0);
        }
        return con;
    }

    public void actualizarTiempoSesion(int minutos, int segundos) {
        int totalSegundos = (minutos * 60) + segundos;
        Configuracion c = new Configuracion();
        c.setClave("tiempo_sesion");
        c.setValorSegundos(totalSegundos);
        configuracionRepository.save(c);
    }
}

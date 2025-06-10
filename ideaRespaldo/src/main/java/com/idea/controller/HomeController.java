package com.idea.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Muestra la página de inicio
     * @param model Modelo para pasar datos a la vista
     * @return La vista de inicio
     */
    @GetMapping("/")
    public String home(Model model) {
        // Aquí podrías agregar datos al modelo si necesitas mostrar información dinámica
        // Por ejemplo, estadísticas o resúmenes

        return "index";
    }
    @GetMapping("/explicacion")
    public String explicacion(Model model) {
        // Aquí podrías agregar datos al modelo si necesitas mostrar información dinámica
        // Por ejemplo, estadísticas o resúmenes

        return "explicacion";
    }



}
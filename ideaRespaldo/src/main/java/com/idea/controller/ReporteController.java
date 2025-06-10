package com.idea.controller;

import com.idea.entity.AsignacionHorario;
import com.idea.entity.AsignacionZona;
import com.idea.entity.HorarioExcelGenerator;
import com.idea.repository.AsignacionHorarioRepository;
import com.idea.repository.AsignacionZonaRepository;
import com.idea.entity.ExcelGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private AsignacionZonaRepository asignacionZonaRepository;
    @Autowired
    private AsignacionHorarioRepository asignacionHorarioRepository;

    // Vista donde el usuario puede generar el reporte
    @GetMapping("/vista")
    public String vista() {
        return "reporteVista";
    }

    // Generar y devolver el reporte Excel
    @GetMapping("/generarExcel")
    @ResponseBody
    public ResponseEntity<byte[]> generarExcel() throws IOException {
        // Obtener las asignaciones desde la base de datos
        List<AsignacionZona> asignaciones = asignacionZonaRepository.findAll();

        // Generar el archivo Excel
        byte[] archivoExcel = ExcelGenerator.generarExcel(asignaciones);

        // Nombre del archivo con fecha y hora
        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = "ReporteAsignaciones_" + fechaHora + ".xlsx";

        // Configurar el encabezado para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", nombreArchivo);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(archivoExcel);
    }

    // Generar y devolver el reporte Excel
    @GetMapping("/generarHorario")
    @ResponseBody
    public ResponseEntity<byte[]> generarExcelHorario() throws IOException {
        // Obtener las asignaciones desde la base de datos
        List<AsignacionHorario> asignaciones = asignacionHorarioRepository.findAll();

        // Generar el archivo Excel
        byte[] archivoExcel = HorarioExcelGenerator.generarExcel(asignaciones);

        // Nombre del archivo con fecha y hora
        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = "ReporteHorarios_" + fechaHora + ".xlsx";

        // Configurar el encabezado para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", nombreArchivo);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(archivoExcel);
    }

}


package com.idea.entity;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class HorarioExcelGenerator {

    private static final String[] DIAS_SEMANA = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Colores más modernos y agradables a la vista (RGB)
    private static final Color[] COLORES_BASE = {
            new Color(141, 211, 199), // Verde menta claro
            new Color(255, 255, 179), // Amarillo pálido
            new Color(190, 186, 218), // Lavanda
            new Color(251, 180, 174), // Salmón claro
            new Color(179, 205, 227), // Azul claro
            new Color(204, 235, 197), // Verde claro
            new Color(222, 203, 228), // Lila
            new Color(254, 217, 166)  // Melocotón
    };

    // Mapa para almacenar colores por código de horario
    private static final Map<String, Color> COLORES_HORARIO = new HashMap<>();

    // Mapa para almacenar el tipo de horario predominante por empleado (A o P)
    private static final Map<Empleado, String> TIPO_HORARIO_EMPLEADO = new HashMap<>();

    public static byte[] generarExcel(List<AsignacionHorario> asignaciones) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Asignaciones de Horarios");

        // Asignar colores a horarios únicos
        asignarColoresAHorarios(asignaciones);

        // Determinar el tipo de horario predominante para cada empleado
        determinarTipoHorarioPorEmpleado(asignaciones);

        // Crear estilos
        CellStyle estiloHeader = crearEstiloEncabezado(workbook);
        CellStyle estiloEmpleado = crearEstiloEmpleado(workbook);
        CellStyle estiloDato = crearEstiloDato(workbook);
        CellStyle estiloSinAsignacion = crearEstiloSinAsignacion(workbook);
        Map<String, CellStyle> estilosHorarios = crearEstilosHorarios(workbook);

        // Agrupar asignaciones por Empleado y Día
        Map<Empleado, Map<String, List<AsignacionHorario>>> asignacionesPorEmpleadoYDia = agruparAsignaciones(asignaciones);

        int rowNum = 0;
        int colNum = 0;

        // Crear título del reporte con estilo mejorado
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("REPORTE DE ASIGNACIONES DE HORARIOS");
        tituloCell.setCellStyle(crearEstiloTitulo(workbook));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, DIAS_SEMANA.length));

        // Agregar fecha de generación con mejor formato
        Row fechaRow = sheet.createRow(rowNum++);
        Cell fechaCell = fechaRow.createCell(0);
        fechaCell.setCellValue("Generado: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        CellStyle estiloFecha = workbook.createCellStyle();
        Font fuenteFecha = workbook.createFont();
        fuenteFecha.setItalic(true);
        fuenteFecha.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        estiloFecha.setFont(fuenteFecha);
        estiloFecha.setAlignment(HorizontalAlignment.RIGHT);
        fechaCell.setCellStyle(estiloFecha);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, DIAS_SEMANA.length));

        rowNum++; // Espacio después del título

        // Crear encabezados de días con mejor diseño
        Row headerRow = sheet.createRow(rowNum++);

        // Primera columna para "Empleado"
        Cell primeraCell = headerRow.createCell(colNum++);
        primeraCell.setCellValue("EMPLEADO");
        primeraCell.setCellStyle(estiloHeader);

        // Columnas para cada día de la semana
        for (String dia : DIAS_SEMANA) {
            Cell cell = headerRow.createCell(colNum++);
            cell.setCellValue(dia.toUpperCase());
            cell.setCellStyle(estiloHeader);
        }

        // Crear estilos para los grupos de tipo A y P
        CellStyle estiloGrupoA = workbook.createCellStyle();
        estiloGrupoA.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        estiloGrupoA.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloGrupoA.setAlignment(HorizontalAlignment.CENTER);
        Font fuenteGrupo = workbook.createFont();
        fuenteGrupo.setBold(true);
        estiloGrupoA.setFont(fuenteGrupo);
        setBorder(estiloGrupoA, BorderStyle.THIN);

        CellStyle estiloGrupoP = workbook.createCellStyle();
        estiloGrupoP.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        estiloGrupoP.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloGrupoP.setAlignment(HorizontalAlignment.CENTER);
        estiloGrupoP.setFont(fuenteGrupo);
        setBorder(estiloGrupoP, BorderStyle.THIN);

        // Variable para controlar si ya se mostró el encabezado del grupo A
        boolean grupoAMostrado = false;
        boolean grupoPMostrado = false;

        // Iterar por cada empleado (ya ordenados por tipo A primero, luego P)
        for (Map.Entry<Empleado, Map<String, List<AsignacionHorario>>> entryEmpleado : asignacionesPorEmpleadoYDia.entrySet()) {
            Empleado empleado = entryEmpleado.getKey();
            Map<String, List<AsignacionHorario>> asignacionesPorDia = entryEmpleado.getValue();

            // Obtener el tipo de horario del empleado
            String tipoHorario = TIPO_HORARIO_EMPLEADO.getOrDefault(empleado, "");

            // Mostrar encabezado de grupo si es necesario
            if (tipoHorario.equals("A") && !grupoAMostrado) {
                Row grupoRow = sheet.createRow(rowNum++);
                Cell grupoCell = grupoRow.createCell(0);
                grupoCell.setCellValue("GRUPO A - HORARIOS TIPO A");
                grupoCell.setCellStyle(estiloGrupoA);
                sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, DIAS_SEMANA.length));
                grupoAMostrado = true;
            } else if (tipoHorario.equals("P") && !grupoPMostrado) {
                Row grupoRow = sheet.createRow(rowNum++);
                Cell grupoCell = grupoRow.createCell(0);
                grupoCell.setCellValue("GRUPO P - HORARIOS TIPO P");
                grupoCell.setCellStyle(estiloGrupoP);
                sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, DIAS_SEMANA.length));
                grupoPMostrado = true;
            }

            Row empleadoRow = sheet.createRow(rowNum++);

            // Celda para el nombre del empleado
            Cell empleadoCell = empleadoRow.createCell(0);
            empleadoCell.setCellValue(empleado.getNombre());
            empleadoCell.setCellStyle(estiloEmpleado);

            // Iterar por cada día de la semana
            for (int i = 0; i < DIAS_SEMANA.length; i++) {
                String dia = DIAS_SEMANA[i];
                Cell diaCell = empleadoRow.createCell(i + 1);

                // Verificar si hay asignaciones para este día
                if (asignacionesPorDia.containsKey(dia) && !asignacionesPorDia.get(dia).isEmpty()) {
                    // Construir texto con las asignaciones
                    StringBuilder sb = new StringBuilder();
                    String codigoHorario = null;

                    for (AsignacionHorario asignacion : asignacionesPorDia.get(dia)) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }

                        Horario horario = asignacion.getHorario();
                        codigoHorario = horario.getCodigo();

                        sb.append(codigoHorario)
                                .append(" (")
                                .append(horario.getInicio().format(TIME_FORMATTER))
                                .append("-")
                                .append(horario.getFin().format(TIME_FORMATTER))
                                .append(") ")
                                .append(asignacion.getPeriodo());
                    }
                    diaCell.setCellValue(sb.toString());

                    // Aplicar estilo basado en el código de horario para consistencia
                    if (codigoHorario != null && estilosHorarios.containsKey(codigoHorario)) {
                        diaCell.setCellStyle(estilosHorarios.get(codigoHorario));
                    } else {
                        diaCell.setCellStyle(estiloDato);
                    }
                } else {
                    diaCell.setCellValue("Sin asignación");
                    diaCell.setCellStyle(estiloSinAsignacion);
                }
            }
        }

        // Agregar leyenda de horarios
        rowNum = agregarLeyendaHorarios(workbook, sheet, rowNum);

        // Ajustar altura de filas para contenido múltiple
        for (int i = 0; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                row.setHeight((short)-1); // Ajuste automático
            }
        }

        // Ajustar ancho de columnas
        for (int i = 0; i <= DIAS_SEMANA.length; i++) {
            sheet.autoSizeColumn(i);
            // Añadir un poco más de espacio para mejor legibilidad
            int ancho = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, ancho + 800); // Más espacio para mejor visualización
        }

        // Convertir a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    /**
     * Determina el tipo de horario predominante para cada empleado (A o P)
     */
    private static void determinarTipoHorarioPorEmpleado(List<AsignacionHorario> asignaciones) {
        // Agrupar asignaciones por empleado
        Map<Empleado, List<AsignacionHorario>> asignacionesPorEmpleado = new HashMap<>();

        for (AsignacionHorario asignacion : asignaciones) {
            Empleado empleado = asignacion.getEmpleado();
            if (!asignacionesPorEmpleado.containsKey(empleado)) {
                asignacionesPorEmpleado.put(empleado, new ArrayList<>());
            }
            asignacionesPorEmpleado.get(empleado).add(asignacion);
        }

        // Determinar el tipo predominante para cada empleado
        for (Map.Entry<Empleado, List<AsignacionHorario>> entry : asignacionesPorEmpleado.entrySet()) {
            Empleado empleado = entry.getKey();
            List<AsignacionHorario> asignacionesEmpleado = entry.getValue();

            // Contar horarios tipo A y P
            int countA = 0;
            int countP = 0;

            for (AsignacionHorario asignacion : asignacionesEmpleado) {
                String codigo = asignacion.getHorario().getCodigo();
                if (codigo.startsWith("A")) {
                    countA++;
                } else if (codigo.startsWith("P")) {
                    countP++;
                }
            }

            // Determinar el tipo predominante
            if (countA > 0 || countP > 0) {
                String tipoPredominante = (countA >= countP) ? "A" : "P";
                TIPO_HORARIO_EMPLEADO.put(empleado, tipoPredominante);
            }
        }
    }

    /**
     * Asigna colores a los códigos de horario únicos
     */
    private static void asignarColoresAHorarios(List<AsignacionHorario> asignaciones) {
        // Extraer códigos de horario únicos
        Set<String> codigosUnicos = asignaciones.stream()
                .map(a -> a.getHorario().getCodigo())
                .collect(Collectors.toSet());

        // Separar códigos por tipo A y P para asignar colores similares a cada grupo
        List<String> codigosA = new ArrayList<>();
        List<String> codigosP = new ArrayList<>();
        List<String> otrosCodigos = new ArrayList<>();

        for (String codigo : codigosUnicos) {
            if (codigo.startsWith("A")) {
                codigosA.add(codigo);
            } else if (codigo.startsWith("P")) {
                codigosP.add(codigo);
            } else {
                otrosCodigos.add(codigo);
            }
        }

        // Ordenar los códigos para asignación consistente
        Collections.sort(codigosA);
        Collections.sort(codigosP);
        Collections.sort(otrosCodigos);

        // Asignar colores a códigos tipo A (tonos verdes/azules)
        int colorIndex = 0;
        for (String codigo : codigosA) {
            // Usar los primeros colores de la paleta para tipo A
            COLORES_HORARIO.put(codigo, COLORES_BASE[colorIndex % 4]);
            colorIndex++;
        }

        // Asignar colores a códigos tipo P (tonos naranjas/amarillos)
        colorIndex = 4; // Comenzar desde la mitad de la paleta
        for (String codigo : codigosP) {
            COLORES_HORARIO.put(codigo, COLORES_BASE[colorIndex % COLORES_BASE.length]);
            colorIndex++;
        }

        // Asignar colores a otros códigos
        for (String codigo : otrosCodigos) {
            COLORES_HORARIO.put(codigo, COLORES_BASE[colorIndex % COLORES_BASE.length]);
            colorIndex++;
        }
    }

    /**
     * Agrega una leyenda de horarios al final del reporte
     */
    private static int agregarLeyendaHorarios(Workbook workbook, Sheet sheet, int rowNum) {
        // Espacio antes de la leyenda
        rowNum += 2;

        // Título de la leyenda
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("LEYENDA DE HORARIOS");

        CellStyle estiloTituloLeyenda = workbook.createCellStyle();
        Font fuenteTituloLeyenda = workbook.createFont();
        fuenteTituloLeyenda.setBold(true);
        estiloTituloLeyenda.setFont(fuenteTituloLeyenda);
        estiloTituloLeyenda.setAlignment(HorizontalAlignment.LEFT);
        estiloTituloLeyenda.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        estiloTituloLeyenda.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        tituloCell.setCellStyle(estiloTituloLeyenda);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 2));

        // Encabezados de la leyenda
        Row headerRow = sheet.createRow(rowNum++);
        Cell colorHeaderCell = headerRow.createCell(0);
        colorHeaderCell.setCellValue("COLOR");
        Cell codigoHeaderCell = headerRow.createCell(1);
        codigoHeaderCell.setCellValue("CÓDIGO");
        Cell descripcionHeaderCell = headerRow.createCell(2);
        descripcionHeaderCell.setCellValue("TIPO");

        CellStyle estiloHeaderLeyenda = workbook.createCellStyle();
        Font fuenteHeaderLeyenda = workbook.createFont();
        fuenteHeaderLeyenda.setBold(true);
        estiloHeaderLeyenda.setFont(fuenteHeaderLeyenda);
        colorHeaderCell.setCellStyle(estiloHeaderLeyenda);
        codigoHeaderCell.setCellStyle(estiloHeaderLeyenda);
        descripcionHeaderCell.setCellStyle(estiloHeaderLeyenda);

        // Separar códigos por tipo para mostrarlos agrupados en la leyenda
        List<String> codigosA = new ArrayList<>();
        List<String> codigosP = new ArrayList<>();
        List<String> otrosCodigos = new ArrayList<>();

        for (String codigo : COLORES_HORARIO.keySet()) {
            if (codigo.startsWith("A")) {
                codigosA.add(codigo);
            } else if (codigo.startsWith("P")) {
                codigosP.add(codigo);
            } else {
                otrosCodigos.add(codigo);
            }
        }

        // Ordenar los códigos
        Collections.sort(codigosA);
        Collections.sort(codigosP);
        Collections.sort(otrosCodigos);

        // Mostrar primero los códigos tipo A
        if (!codigosA.isEmpty()) {
            Row grupoRow = sheet.createRow(rowNum++);
            Cell grupoCell = grupoRow.createCell(0);
            grupoCell.setCellValue("HORARIOS TIPO A");
            grupoCell.setCellStyle(estiloHeaderLeyenda);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 2));

            for (String codigo : codigosA) {
                agregarFilaLeyenda(workbook, sheet, rowNum++, codigo, "A");
            }
        }

        // Luego mostrar los códigos tipo P
        if (!codigosP.isEmpty()) {
            Row grupoRow = sheet.createRow(rowNum++);
            Cell grupoCell = grupoRow.createCell(0);
            grupoCell.setCellValue("HORARIOS TIPO P");
            grupoCell.setCellStyle(estiloHeaderLeyenda);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 2));

            for (String codigo : codigosP) {
                agregarFilaLeyenda(workbook, sheet, rowNum++, codigo, "P");
            }
        }

        // Finalmente mostrar otros códigos si existen
        if (!otrosCodigos.isEmpty()) {
            Row grupoRow = sheet.createRow(rowNum++);
            Cell grupoCell = grupoRow.createCell(0);
            grupoCell.setCellValue("OTROS HORARIOS");
            grupoCell.setCellStyle(estiloHeaderLeyenda);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 2));

            for (String codigo : otrosCodigos) {
                agregarFilaLeyenda(workbook, sheet, rowNum++, codigo, "Otro");
            }
        }

        return rowNum;
    }

    /**
     * Agrega una fila a la leyenda de horarios
     */
    private static void agregarFilaLeyenda(Workbook workbook, Sheet sheet, int rowNum, String codigo, String tipo) {
        Row horarioRow = sheet.createRow(rowNum);
        Color color = COLORES_HORARIO.get(codigo);

        // Celda para el color
        Cell colorCell = horarioRow.createCell(0);
        colorCell.setCellValue("");

        XSSFCellStyle estiloColor = (XSSFCellStyle) workbook.createCellStyle();
        estiloColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Usar XSSFColor para colores RGB personalizados
        byte[] rgb = new byte[]{(byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue()};
        XSSFColor xssfColor = new XSSFColor(rgb, null);
        estiloColor.setFillForegroundColor(xssfColor);

        setBorder(estiloColor, BorderStyle.THIN);
        colorCell.setCellStyle(estiloColor);

        // Celda para el código
        Cell codigoCell = horarioRow.createCell(1);
        codigoCell.setCellValue(codigo);

        // Celda para el tipo
        Cell tipoCell = horarioRow.createCell(2);
        tipoCell.setCellValue(tipo);

        CellStyle estiloTexto = workbook.createCellStyle();
        estiloTexto.setAlignment(HorizontalAlignment.LEFT);
        codigoCell.setCellStyle(estiloTexto);
        tipoCell.setCellStyle(estiloTexto);
    }

    /**
     * Agrupa las asignaciones por Empleado y Día, ordenando los empleados por tipo de horario (A primero, luego P)
     */
    private static Map<Empleado, Map<String, List<AsignacionHorario>>> agruparAsignaciones(List<AsignacionHorario> asignaciones) {
        // Usar un comparador personalizado que ordene primero por tipo de horario (A, P, otros)
        // y luego por nombre de empleado
        Comparator<Empleado> comparadorPorTipoYNombre = (e1, e2) -> {
            String tipoE1 = TIPO_HORARIO_EMPLEADO.getOrDefault(e1, "");
            String tipoE2 = TIPO_HORARIO_EMPLEADO.getOrDefault(e2, "");

            // Si ambos son del mismo tipo, ordenar por nombre
            if (tipoE1.equals(tipoE2)) {
                return e1.getNombre().compareTo(e2.getNombre());
            }

            // Si e1 es tipo A y e2 no, e1 va primero
            if (tipoE1.equals("A") && !tipoE2.equals("A")) {
                return -1;
            }

            // Si e2 es tipo A y e1 no, e2 va primero
            if (tipoE2.equals("A") && !tipoE1.equals("A")) {
                return 1;
            }

            // Si e1 es tipo P y e2 no es ni A ni P, e1 va primero
            if (tipoE1.equals("P") && !tipoE2.equals("P") && !tipoE2.equals("A")) {
                return -1;
            }

            // Si e2 es tipo P y e1 no es ni A ni P, e2 va primero
            if (tipoE2.equals("P") && !tipoE1.equals("P") && !tipoE1.equals("A")) {
                return 1;
            }

            // En cualquier otro caso, ordenar por nombre
            return e1.getNombre().compareTo(e2.getNombre());
        };

        Map<Empleado, Map<String, List<AsignacionHorario>>> resultado = new TreeMap<>(comparadorPorTipoYNombre);

        for (AsignacionHorario asignacion : asignaciones) {
            Empleado empleado = asignacion.getEmpleado();
            String dia = asignacion.getDiaDeLaSemana();

            // Inicializar mapas si no existen
            if (!resultado.containsKey(empleado)) {
                resultado.put(empleado, new HashMap<>());
            }

            Map<String, List<AsignacionHorario>> diaMap = resultado.get(empleado);
            if (!diaMap.containsKey(dia)) {
                diaMap.put(dia, new ArrayList<>());
            }

            // Agregar asignación a la lista
            diaMap.get(dia).add(asignacion);
        }

        return resultado;
    }

    private static CellStyle crearEstiloTitulo(Workbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        Font fuente = workbook.createFont();
        fuente.setBold(true);
        fuente.setFontHeightInPoints((short) 16); // Título más grande
        estilo.setFont(fuente);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        // Agregar un fondo sutil al título
        estilo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // Agregar bordes al título
        setBorder(estilo, BorderStyle.MEDIUM);
        return estilo;
    }

    private static CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        Font fuente = workbook.createFont();
        fuente.setBold(true);
        fuente.setColor(IndexedColors.WHITE.getIndex());
        estilo.setFont(fuente);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setFillForegroundColor(IndexedColors.INDIGO.getIndex()); // Color más moderno
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(estilo, BorderStyle.THIN);
        return estilo;
    }

    private static CellStyle crearEstiloEmpleado(Workbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        Font fuente = workbook.createFont();
        fuente.setBold(true);
        estilo.setFont(fuente);
        estilo.setAlignment(HorizontalAlignment.LEFT);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(estilo, BorderStyle.THIN);
        return estilo;
    }

    private static CellStyle crearEstiloDato(Workbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.LEFT);
        estilo.setVerticalAlignment(VerticalAlignment.TOP);
        estilo.setWrapText(true); // Permitir múltiples líneas
        setBorder(estilo, BorderStyle.THIN);
        return estilo;
    }

    private static CellStyle crearEstiloSinAsignacion(Workbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        Font fuente = workbook.createFont();
        fuente.setItalic(true);
        fuente.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        estilo.setFont(fuente);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(estilo, BorderStyle.THIN);
        return estilo;
    }

    /**
     * Crea estilos para cada código de horario
     */
    private static Map<String, CellStyle> crearEstilosHorarios(Workbook workbook) {
        Map<String, CellStyle> estilos = new HashMap<>();

        for (Map.Entry<String, Color> entry : COLORES_HORARIO.entrySet()) {
            String codigo = entry.getKey();
            Color color = entry.getValue();

            XSSFCellStyle estilo = (XSSFCellStyle) workbook.createCellStyle();
            estilo.setAlignment(HorizontalAlignment.LEFT);  workbook.createCellStyle();
            estilo.setAlignment(HorizontalAlignment.LEFT);
            estilo.setVerticalAlignment(VerticalAlignment.TOP);
            estilo.setWrapText(true);
            estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Usar XSSFColor para colores RGB personalizados
            byte[] rgb = new byte[]{(byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue()};
            XSSFColor xssfColor = new XSSFColor(rgb, null);
            estilo.setFillForegroundColor(xssfColor);

            setBorder(estilo, BorderStyle.THIN);
            estilos.put(codigo, estilo);
        }

        return estilos;
    }

    private static void setBorder(CellStyle estilo, BorderStyle borderStyle) {
        estilo.setBorderTop(borderStyle);
        estilo.setBorderRight(borderStyle);
        estilo.setBorderBottom(borderStyle);
        estilo.setBorderLeft(borderStyle);
    }
}
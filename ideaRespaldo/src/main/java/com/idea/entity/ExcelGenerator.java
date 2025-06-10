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

public class ExcelGenerator {

    private static final String[] DIAS_SEMANA = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Colores modernos para grupos de zonas (RGB)
    private static final Color[] COLORES_GRUPO_ZONA = {
            new Color(26, 115, 232),    // Azul Google
            new Color(52, 168, 83),     // Verde Google
            new Color(251, 188, 5),     // Amarillo Google
            new Color(234, 67, 53),     // Rojo Google
            new Color(103, 58, 183),    // Púrpura
            new Color(0, 150, 136),     // Verde azulado
            new Color(255, 152, 0),     // Naranja
            new Color(96, 125, 139)     // Azul grisáceo
    };

    // Mapa para almacenar colores por grupo de zona
    private static final Map<GrupoZona, Color> COLORES_POR_GRUPO = new HashMap<>();

    // Mapa para almacenar colores por zona (derivados del color del grupo)
    private static final Map<Zona, Color> COLORES_POR_ZONA = new HashMap<>();

    public static byte[] generarExcel(List<AsignacionZona> asignaciones) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Asignaciones por Zona y Día");

        // Asignar colores a grupos de zonas y zonas
        asignarColores(asignaciones);

        // Crear estilos
        CellStyle estiloHeader = crearEstiloEncabezado(workbook);
        CellStyle estiloSubHeader = crearEstiloSubEncabezado(workbook);
        CellStyle estiloSinAsignacion = crearEstiloSinAsignacion(workbook);

        // Crear estilos dinámicos para grupos y zonas
        Map<GrupoZona, CellStyle> estilosGrupoZona = crearEstilosGrupoZona(workbook);
        Map<Zona, CellStyle> estilosZona = crearEstilosZona(workbook);

        // Agrupar asignaciones por GrupoZona y Zona
        Map<GrupoZona, Map<Zona, Map<String, List<AsignacionZona>>>> asignacionesPorGrupoZonaYDia = agruparAsignaciones(asignaciones);

        int rowNum = 0;
        int colNum = 0;

        // Crear título del reporte con mejor estilo
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("REPORTE DE ASIGNACIONES DE ZONAS");
        tituloCell.setCellStyle(crearEstiloTitulo(workbook));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, DIAS_SEMANA.length));

        // Agregar fecha de generación
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

        // Primera columna para "Grupo/Zona"
        Cell primeraCell = headerRow.createCell(colNum++);
        primeraCell.setCellValue("GRUPO / ZONA");
        primeraCell.setCellStyle(estiloHeader);

        // Columnas para cada día de la semana
        for (String dia : DIAS_SEMANA) {
            Cell cell = headerRow.createCell(colNum++);
            cell.setCellValue(dia.toUpperCase());
            cell.setCellStyle(estiloHeader);
        }

        // Iterar por cada grupo de zona
        for (Map.Entry<GrupoZona, Map<Zona, Map<String, List<AsignacionZona>>>> entryGrupo : asignacionesPorGrupoZonaYDia.entrySet()) {
            GrupoZona grupoZona = entryGrupo.getKey();
            Map<Zona, Map<String, List<AsignacionZona>>> zonasPorDia = entryGrupo.getValue();

            // Fila para el grupo de zona con estilo personalizado
            Row grupoRow = sheet.createRow(rowNum++);
            Cell grupoCell = grupoRow.createCell(0);
            grupoCell.setCellValue(grupoZona.getNombre().toUpperCase());

            // Usar el estilo específico para este grupo
            grupoCell.setCellStyle(estilosGrupoZona.get(grupoZona));
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, DIAS_SEMANA.length));

            // Iterar por cada zona del grupo
            for (Map.Entry<Zona, Map<String, List<AsignacionZona>>> entryZona : zonasPorDia.entrySet()) {
                Zona zona = entryZona.getKey();
                Map<String, List<AsignacionZona>> asignacionesPorDia = entryZona.getValue();

                Row zonaRow = sheet.createRow(rowNum++);

                // Celda para el nombre de la zona con estilo personalizado
                Cell zonaCell = zonaRow.createCell(0);
                zonaCell.setCellValue(zona.getNombre());

                // Usar el estilo específico para esta zona
                zonaCell.setCellStyle(estilosZona.get(zona));

                // Iterar por cada día de la semana
                for (int i = 0; i < DIAS_SEMANA.length; i++) {
                    String dia = DIAS_SEMANA[i];
                    Cell diaCell = zonaRow.createCell(i + 1);

                    // Verificar si hay asignaciones para este día
                    if (asignacionesPorDia.containsKey(dia) && !asignacionesPorDia.get(dia).isEmpty()) {
                        // Construir texto con las asignaciones
                        StringBuilder sb = new StringBuilder();
                        for (AsignacionZona asignacion : asignacionesPorDia.get(dia)) {
                            if (sb.length() > 0) {
                                sb.append("\n");
                            }
                            sb.append(asignacion.getEmpleado().getNombre())
                                    .append(" (")
                                    .append(asignacion.getInicio().format(TIME_FORMATTER))
                                    .append("-")
                                    .append(asignacion.getFin().format(TIME_FORMATTER))
                                    .append(")");
                        }
                        diaCell.setCellValue(sb.toString());

                        // Crear estilo para datos con borde y wrap text
                        CellStyle estiloDato = workbook.createCellStyle();
                        estiloDato.setAlignment(HorizontalAlignment.LEFT);
                        estiloDato.setVerticalAlignment(VerticalAlignment.TOP);
                        estiloDato.setWrapText(true);
                        setBorder(estiloDato, BorderStyle.THIN);

                        // Agregar un fondo sutil que coincida con el color de la zona
                        Color colorZona = COLORES_POR_ZONA.get(zona);
                        if (colorZona != null) {
                            // Crear una versión más clara del color para el fondo
                            Color colorClaro = aclararColor(colorZona, 0.85f);
                            XSSFColor xssfColor = new XSSFColor(
                                    new byte[]{(byte)colorClaro.getRed(), (byte)colorClaro.getGreen(), (byte)colorClaro.getBlue()},
                                    null
                            );
                            ((XSSFCellStyle)estiloDato).setFillForegroundColor(xssfColor);
                            estiloDato.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        }

                        diaCell.setCellStyle(estiloDato);
                    } else {
                        diaCell.setCellValue("Sin asignación");
                        diaCell.setCellStyle(estiloSinAsignacion);
                    }
                }
            }

            // Espacio entre grupos
            rowNum++;
        }

        // Agregar leyenda de grupos de zonas
        rowNum = agregarLeyendaGrupos(workbook, sheet, rowNum);

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
            sheet.setColumnWidth(i, ancho + 500);
        }

        // Convertir a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    /**
     * Asigna colores a los grupos de zonas y zonas
     */
    private static void asignarColores(List<AsignacionZona> asignaciones) {
        // Extraer grupos de zonas únicos
        Set<GrupoZona> gruposUnicos = asignaciones.stream()
                .map(a -> a.getZona().getGrupoZona())
                .collect(Collectors.toSet());

        // Asignar un color a cada grupo
        int colorIndex = 0;
        for (GrupoZona grupo : gruposUnicos) {
            Color colorGrupo = COLORES_GRUPO_ZONA[colorIndex % COLORES_GRUPO_ZONA.length];
            COLORES_POR_GRUPO.put(grupo, colorGrupo);
            colorIndex++;

            // Extraer zonas de este grupo
            Set<Zona> zonasDelGrupo = asignaciones.stream()
                    .map(AsignacionZona::getZona)
                    .filter(z -> z.getGrupoZona().equals(grupo))
                    .collect(Collectors.toSet());

            // Asignar colores derivados a cada zona
            int zonaIndex = 0;
            for (Zona zona : zonasDelGrupo) {
                // Crear variaciones del color del grupo para cada zona
                float factor = 0.8f + (zonaIndex * 0.05f); // Varía entre 0.8 y 1.0
                Color colorZona = ajustarColor(colorGrupo, factor);
                COLORES_POR_ZONA.put(zona, colorZona);
                zonaIndex++;
            }
        }
    }

    /**
     * Ajusta un color multiplicando sus componentes RGB por un factor
     */
    private static Color ajustarColor(Color color, float factor) {
        int r = Math.min(255, Math.max(0, Math.round(color.getRed() * factor)));
        int g = Math.min(255, Math.max(0, Math.round(color.getGreen() * factor)));
        int b = Math.min(255, Math.max(0, Math.round(color.getBlue() * factor)));
        return new Color(r, g, b);
    }

    /**
     * Aclara un color mezclándolo con blanco
     */
    private static Color aclararColor(Color color, float factor) {
        int r = Math.round(color.getRed() * factor + 255 * (1 - factor));
        int g = Math.round(color.getGreen() * factor + 255 * (1 - factor));
        int b = Math.round(color.getBlue() * factor + 255 * (1 - factor));
        return new Color(r, g, b);
    }

    /**
     * Agrega una leyenda de grupos de zonas al final del reporte
     */
    private static int agregarLeyendaGrupos(Workbook workbook, Sheet sheet, int rowNum) {
        // Espacio antes de la leyenda
        rowNum += 2;

        // Título de la leyenda
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("LEYENDA DE GRUPOS DE ZONAS");

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
        Cell grupoHeaderCell = headerRow.createCell(1);
        grupoHeaderCell.setCellValue("GRUPO");
        Cell descripcionHeaderCell = headerRow.createCell(2);
        descripcionHeaderCell.setCellValue("DESCRIPCIÓN");

        CellStyle estiloHeaderLeyenda = workbook.createCellStyle();
        Font fuenteHeaderLeyenda = workbook.createFont();
        fuenteHeaderLeyenda.setBold(true);
        estiloHeaderLeyenda.setFont(fuenteHeaderLeyenda);
        colorHeaderCell.setCellStyle(estiloHeaderLeyenda);
        grupoHeaderCell.setCellStyle(estiloHeaderLeyenda);
        descripcionHeaderCell.setCellStyle(estiloHeaderLeyenda);

        // Mostrar cada grupo con su color
        for (Map.Entry<GrupoZona, Color> entry : COLORES_POR_GRUPO.entrySet()) {
            GrupoZona grupo = entry.getKey();
            Color color = entry.getValue();

            Row grupoRow = sheet.createRow(rowNum++);

            // Celda para el color
            Cell colorCell = grupoRow.createCell(0);
            colorCell.setCellValue("");

            XSSFCellStyle estiloColor = (XSSFCellStyle) workbook.createCellStyle();
            estiloColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Usar XSSFColor para colores RGB personalizados
            byte[] rgb = new byte[]{(byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue()};
            XSSFColor xssfColor = new XSSFColor(rgb, null);
            estiloColor.setFillForegroundColor(xssfColor);

            setBorder(estiloColor, BorderStyle.THIN);
            colorCell.setCellStyle(estiloColor);

            // Celda para el nombre del grupo
            Cell grupoCell = grupoRow.createCell(1);
            grupoCell.setCellValue(grupo.getNombre());

            // Celda para la descripción (puedes agregar más información si la tienes)
            Cell descripcionCell = grupoRow.createCell(2);
            descripcionCell.setCellValue("Grupo de zonas " + grupo.getNombre());

            CellStyle estiloTexto = workbook.createCellStyle();
            estiloTexto.setAlignment(HorizontalAlignment.LEFT);
            grupoCell.setCellStyle(estiloTexto);
            descripcionCell.setCellStyle(estiloTexto);

            // Mostrar las zonas de este grupo con indentación
            Set<Zona> zonasDelGrupo = COLORES_POR_ZONA.keySet().stream()
                    .filter(z -> z.getGrupoZona().equals(grupo))
                    .collect(Collectors.toSet());

            for (Zona zona : zonasDelGrupo) {
                Color colorZona = COLORES_POR_ZONA.get(zona);

                Row zonaRow = sheet.createRow(rowNum++);

                // Celda para el color de la zona
                Cell colorZonaCell = zonaRow.createCell(0);
                colorZonaCell.setCellValue("");

                XSSFCellStyle estiloColorZona = (XSSFCellStyle) workbook.createCellStyle();
                estiloColorZona.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                byte[] rgbZona = new byte[]{(byte)colorZona.getRed(), (byte)colorZona.getGreen(), (byte)colorZona.getBlue()};
                XSSFColor xssfColorZona = new XSSFColor(rgbZona, null);
                estiloColorZona.setFillForegroundColor(xssfColorZona);

                setBorder(estiloColorZona, BorderStyle.THIN);
                colorZonaCell.setCellStyle(estiloColorZona);

                // Celda para el nombre de la zona con indentación
                Cell zonaCell = zonaRow.createCell(1);
                zonaCell.setCellValue("   ↳ " + zona.getNombre());

                // Celda para la descripción de la zona
                Cell descripcionZonaCell = zonaRow.createCell(2);
                descripcionZonaCell.setCellValue("Zona " + zona.getNombre());

                CellStyle estiloTextoZona = workbook.createCellStyle();
                estiloTextoZona.setAlignment(HorizontalAlignment.LEFT);
                zonaCell.setCellStyle(estiloTextoZona);
                descripcionZonaCell.setCellStyle(estiloTextoZona);
            }

            // Espacio entre grupos en la leyenda
            rowNum++;
        }

        return rowNum;
    }

    /**
     * Agrupa las asignaciones por GrupoZona, Zona y Día
     */
    private static Map<GrupoZona, Map<Zona, Map<String, List<AsignacionZona>>>> agruparAsignaciones(List<AsignacionZona> asignaciones) {
        Map<GrupoZona, Map<Zona, Map<String, List<AsignacionZona>>>> resultado = new TreeMap<>(
                Comparator.comparing(GrupoZona::getNombre)
        );

        for (AsignacionZona asignacion : asignaciones) {
            Zona zona = asignacion.getZona();
            GrupoZona grupoZona = zona.getGrupoZona();
            String dia = asignacion.getDiaDeLaSemana();

            // Inicializar mapas si no existen
            if (!resultado.containsKey(grupoZona)) {
                resultado.put(grupoZona, new TreeMap<>(Comparator.comparing(Zona::getNombre)));
            }

            Map<Zona, Map<String, List<AsignacionZona>>> zonaMap = resultado.get(grupoZona);
            if (!zonaMap.containsKey(zona)) {
                zonaMap.put(zona, new HashMap<>());
            }

            Map<String, List<AsignacionZona>> diaMap = zonaMap.get(zona);
            if (!diaMap.containsKey(dia)) {
                diaMap.put(dia, new ArrayList<>());
            }

            // Agregar asignación a la lista
            diaMap.get(dia).add(asignacion);

            // Ordenar asignaciones por hora de inicio
            diaMap.get(dia).sort(Comparator.comparing(AsignacionZona::getInicio));
        }

        return resultado;
    }

    /**
     * Crea estilos personalizados para cada grupo de zona
     */
    private static Map<GrupoZona, CellStyle> crearEstilosGrupoZona(Workbook workbook) {
        Map<GrupoZona, CellStyle> estilos = new HashMap<>();

        for (Map.Entry<GrupoZona, Color> entry : COLORES_POR_GRUPO.entrySet()) {
            GrupoZona grupo = entry.getKey();
            Color color = entry.getValue();

            XSSFCellStyle estilo = (XSSFCellStyle) workbook.createCellStyle();
            Font fuente = workbook.createFont();
            fuente.setBold(true);
            fuente.setColor(IndexedColors.WHITE.getIndex());
            estilo.setFont(fuente);
            estilo.setAlignment(HorizontalAlignment.LEFT);
            estilo.setVerticalAlignment(VerticalAlignment.CENTER);
            estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Usar XSSFColor para colores RGB personalizados
            byte[] rgb = new byte[]{(byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue()};
            XSSFColor xssfColor = new XSSFColor(rgb, null);
            estilo.setFillForegroundColor(xssfColor);

            setBorder(estilo, BorderStyle.THIN);
            estilos.put(grupo, estilo);
        }

        return estilos;
    }

    /**
     * Crea estilos personalizados para cada zona
     */
    private static Map<Zona, CellStyle> crearEstilosZona(Workbook workbook) {
        Map<Zona, CellStyle> estilos = new HashMap<>();

        for (Map.Entry<Zona, Color> entry : COLORES_POR_ZONA.entrySet()) {
            Zona zona = entry.getKey();
            Color color = entry.getValue();

            XSSFCellStyle estilo = (XSSFCellStyle) workbook.createCellStyle();
            Font fuente = workbook.createFont();
            fuente.setBold(true);
            estilo.setFont(fuente);
            estilo.setIndention((short) 2); // Indentación
            estilo.setAlignment(HorizontalAlignment.LEFT);
            estilo.setVerticalAlignment(VerticalAlignment.CENTER);
            estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Usar XSSFColor para colores RGB personalizados
            byte[] rgb = new byte[]{(byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue()};
            XSSFColor xssfColor = new XSSFColor(rgb, null);
            estilo.setFillForegroundColor(xssfColor);

            setBorder(estilo, BorderStyle.THIN);
            estilos.put(zona, estilo);
        }

        return estilos;
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

    private static CellStyle crearEstiloSubEncabezado(Workbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        Font fuente = workbook.createFont();
        fuente.setBold(true);
        estilo.setFont(fuente);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
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

    private static void setBorder(CellStyle estilo, BorderStyle borderStyle) {
        estilo.setBorderTop(borderStyle);
        estilo.setBorderRight(borderStyle);
        estilo.setBorderBottom(borderStyle);
        estilo.setBorderLeft(borderStyle);
    }
}

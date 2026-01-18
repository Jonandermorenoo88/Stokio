package com.proyecto.stockio.service;

import com.proyecto.stockio.model.Albaran;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelService {

    public ByteArrayInputStream albaranesToExcel(List<Albaran> albaranes) {
        String[] columns = { "ID", "Código", "Tipo", "Fecha", "Almacén", "Usuario" };

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Historial de Movimientos");

            // Header Style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Row for Header
            Row headerRow = sheet.createRow(0);

            // Header
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // Date Format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            int rowIdx = 1;
            for (Albaran albaran : albaranes) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(albaran.getId());
                row.createCell(1).setCellValue(albaran.getCodigo());
                row.createCell(2).setCellValue(albaran.getTipo().toString());
                row.createCell(3).setCellValue(albaran.getFecha().format(formatter));

                String almacenNombre = (albaran.getAlmacen() != null) ? albaran.getAlmacen().getNombre() : "N/A";
                row.createCell(4).setCellValue(almacenNombre);

                String usuarioEmail = (albaran.getUsuario() != null) ? albaran.getUsuario().getEmail() : "Desconocido";
                row.createCell(5).setCellValue(usuarioEmail);
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error al importar datos a Excel archivo: " + e.getMessage());
        }
    }
}

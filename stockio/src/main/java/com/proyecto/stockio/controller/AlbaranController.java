package com.proyecto.stockio.controller;

import com.proyecto.stockio.repository.AlbaranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/albaranes")
public class AlbaranController {

    @Autowired
    private AlbaranRepository albaranRepository;

    @Autowired
    private com.proyecto.stockio.repository.LineaAlbaranRepository lineaAlbaranRepository;

    @Autowired
    private com.proyecto.stockio.service.ExcelService excelService;

    @GetMapping
    public String listarAlbaranes(Model model) {
        model.addAttribute("albaranes", albaranRepository.findAll());
        return "albaranes/index";
    }

    @GetMapping("/exportar")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.InputStreamResource> exportarExcel() {
        java.util.List<com.proyecto.stockio.model.Albaran> albaranes = albaranRepository.findAll();

        java.io.ByteArrayInputStream in = excelService.albaranesToExcel(albaranes);
        // Validar que no sea null para evitar warnings de tipo
        java.io.InputStream inputStream = java.util.Objects.requireNonNull(in, "El stream de Excel no puede ser nulo");

        // Headers
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=movimientos.xlsx");

        return org.springframework.http.ResponseEntity
                .ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType
                        .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new org.springframework.core.io.InputStreamResource(inputStream));
    }

    @GetMapping("/{id}")
    public String verAlbaran(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        java.util.Objects.requireNonNull(id, "El ID de albarán no puede ser nulo");
        com.proyecto.stockio.model.Albaran albaran = albaranRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Albarán no encontrado: " + id));

        java.util.List<com.proyecto.stockio.model.LineaAlbaran> lineas = lineaAlbaranRepository.findByAlbaran(albaran);

        model.addAttribute("albaran", albaran);
        model.addAttribute("lineas", lineas);

        return "albaranes/detalle";
    }
}

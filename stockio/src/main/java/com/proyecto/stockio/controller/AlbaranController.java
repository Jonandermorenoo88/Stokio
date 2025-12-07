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

    @GetMapping
    public String listarAlbaranes(Model model) {
        model.addAttribute("albaranes", albaranRepository.findAll());
        return "albaranes/index";
    }
}

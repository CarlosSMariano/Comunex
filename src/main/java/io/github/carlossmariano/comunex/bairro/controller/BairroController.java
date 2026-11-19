package io.github.carlossmariano.comunex.bairro.controller;

import io.github.carlossmariano.comunex.bairro.dto.response.BairroResponse;
import io.github.carlossmariano.comunex.bairro.service.BairroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bairros")
public class BairroController {

    private final BairroService bairroService;

    public BairroController(BairroService bairroService) {
        this.bairroService = bairroService;
    }

    @GetMapping
    public ResponseEntity<List<BairroResponse>> listarBairros(){
        return ResponseEntity.ok(bairroService.listarBairros());
    }
}

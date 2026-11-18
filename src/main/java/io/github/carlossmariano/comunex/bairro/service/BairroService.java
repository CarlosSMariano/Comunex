package io.github.carlossmariano.comunex.bairro.service;

import io.github.carlossmariano.comunex.bairro.repository.BairroRepository;
import org.springframework.stereotype.Service;

@Service
public class BairroService {

    private final BairroRepository bairroRepository;

    public BairroService(BairroRepository bairroRepository) {
        this.bairroRepository = bairroRepository;
    }

    public boolean existePorId(Long idBairro){
        return bairroRepository.existsById(idBairro);
    }
}

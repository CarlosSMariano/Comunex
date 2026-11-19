package io.github.carlossmariano.comunex.bairro.service;

import io.github.carlossmariano.comunex.bairro.dto.response.BairroResponse;
import io.github.carlossmariano.comunex.bairro.entity.Bairro;
import io.github.carlossmariano.comunex.bairro.repository.BairroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BairroService {

    private final BairroRepository bairroRepository;

    public BairroService(BairroRepository bairroRepository) {
        this.bairroRepository = bairroRepository;
    }

    public boolean existePorId(Long idBairro){
        return bairroRepository.existsById(idBairro);
    }

    public Bairro criar(String nome, String municipio, String estado) {

        Bairro bairro = new Bairro();

        bairro.setNome(nome.trim());
        bairro.setMunicipio(municipio.trim());
        bairro.setEstado(estado.trim().toUpperCase());

        return bairroRepository.save(bairro);
    }

    public List<BairroResponse> listarBairros(){
        return  bairroRepository.findAll()
                .stream()
                .map(b -> new BairroResponse(
                        b.getIdBairro(),
                        b.getNome(),
                        b.getMunicipio(),
                        b.getEstado()
                ))
                .toList();
    }
}

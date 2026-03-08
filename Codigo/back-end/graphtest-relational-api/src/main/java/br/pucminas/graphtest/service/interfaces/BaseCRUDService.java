package br.pucminas.graphtest.service.interfaces;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface BaseCRUDService<I> {

    I criar(@NotNull I obj);

    I encontrarPorId(@NotNull UUID id);

    List<I> listarTodos();

    I atualizar(@NotNull I obj);

    void deletar(@NotNull UUID id);
}

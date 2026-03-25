package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.adapters.inbound.exception.JsonResponseBuilderException;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

@UtilityClass
public class JsonResponseBuilderUtil {

    /**
     * ConstrÃ³i o Json de resposta
     *
     * @param keys     chaves do Json
     * @param arguments argumentos do Json
     * @return Json a partir das chaves e argumentos
     */
    public static Map<String, Object> buildJsonResponse(List<String> keys, List<Object> arguments) {

        validateListSizes(keys, arguments);

        return range(0, keys.size())
                .boxed()
                .collect(toMap(keys::get, arguments::get));
    }

    /**
     * Valida se a quantidade de chaves Ã© igual ao nÃºmero de argumentos
     *
     * @param keys    chaves do Json
     * @param arguments argumentos do Json
     */
    private static void validateListSizes(@NotNull List<String> keys, @NotNull List<Object> arguments) {
        if (keys.size() != arguments.size()) {
            throw new JsonResponseBuilderException("O nÃºmero de chaves deve ser igual ao nÃºmero de argumentos.");
        }
    }
}

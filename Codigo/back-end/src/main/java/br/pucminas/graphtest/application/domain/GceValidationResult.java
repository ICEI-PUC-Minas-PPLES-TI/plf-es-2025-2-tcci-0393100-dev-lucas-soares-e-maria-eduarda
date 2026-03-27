package br.pucminas.graphtest.application.domain;

import br.pucminas.graphtest.application.domain.records.ValidationMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GceValidationResult {

    private final List<ValidationMessage> errors = new ArrayList<>();
    private final List<ValidationMessage> warnings = new ArrayList<>();

    public void addError(String code, String message) {
        errors.add(new ValidationMessage(code, message));
    }

    public void addWarning(String code, String message) {
        warnings.add(new ValidationMessage(code, message));
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<ValidationMessage> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public List<ValidationMessage> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
}

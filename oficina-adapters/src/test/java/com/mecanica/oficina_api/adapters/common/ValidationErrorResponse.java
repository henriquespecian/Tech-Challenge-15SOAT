package com.mecanica.oficina_api.adapters.common;

import java.util.List;

public record ValidationErrorResponse(String mensagem, List<FieldError> erros) {

}

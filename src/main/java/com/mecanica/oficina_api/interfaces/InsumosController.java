package com.mecanica.oficina_api.interfaces;

import com.mecanica.oficina_api.application.insumo.InsumosService;
import com.mecanica.oficina_api.interfaces.dto.request.AlterarInsumosRequest;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarInsumosRequest;
import com.mecanica.oficina_api.interfaces.dto.response.InsumosResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("insumos")
@Tag(name = "Insumos", description = "Gerenciamento de Insumos e Peças da oficina")
public class InsumosController {
  private final InsumosService insumosService;

  public InsumosController(InsumosService insumosService){this.insumosService = insumosService;}

  @GetMapping
  @Operation(summary = "Listar Insumos e Peças", description = "Retorna todos os Insumos e Peças ativas no sistema")
  public ResponseEntity<List<InsumosResponse>> listarInsumos(){
    return ResponseEntity.status(HttpStatus.OK).body(insumosService.listar());
  }

  @PostMapping
  @Operation(summary = "Cadastrar novo Insumo ou Peça", description = "Permite cadastrar Insumos e peças no estoque")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Insumo/Peça criado com sucesso"),
      @ApiResponse(responseCode = "409", description = "Insumo/Peça com mesmo nome já existe"),
      @ApiResponse(responseCode = "400", description = "Dados da solicitação inválidos",
          content = @Content(schema = @Schema()))
  })
  public ResponseEntity<Void> cadastrarInsumo(@RequestBody CadastrarInsumosRequest request){
    insumosService.cadastrar(request);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/{id}")
  @Operation(summary = "Alterar Insumo ou Peça", description = "Permite realizar alterações no cadastro de um Insumo ou Peça ativo")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Insumo/Peça alterado com sucesso"),
      @ApiResponse(responseCode = "404", description = "Insumo/Peça não encontrado"),
      @ApiResponse(responseCode = "400", description = "Dados da solicitação inválidos",
          content = @Content(schema = @Schema()))
  })
  public ResponseEntity<Void> alterarInsumo(@RequestParam String id, @RequestBody AlterarInsumosRequest request){
    insumosService.atualizar(id, request);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Desativar Insumo ou Peça", description = "Permite desativar um Insumo ou Peça do sistema")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Insumo/Peça desativado com sucesso")
  })
  public ResponseEntity<Void> desativarInsumo(@RequestParam String id){
    insumosService.deletar(id);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}

package com.mecanica.oficina_api.interfaces;

import com.mecanica.oficina_api.application.veiculo.VeiculoService;
import com.mecanica.oficina_api.interfaces.dto.request.CadastrarVeiculoRequest;
import com.mecanica.oficina_api.interfaces.dto.response.VeiculoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("veiculo")
@Tag(name = "Veículo", description = "Gerenciamento de veículos dos clientes da oficina")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo veículo", description = "Permite cadastrar um novo veículo para um cliente na oficina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Veículo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da solicitação inválidos",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> cadastrar(@RequestBody CadastrarVeiculoRequest request) {
        veiculoService.cadastrar(request);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar veículo por ID", description = "Retorna os detalhes de um veículo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veículo encontrado"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<VeiculoResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar veículos de um cliente", description = "Retorna todos os veículos associados a um cliente")
    public ResponseEntity<List<VeiculoResponse>> listarPorCliente(@PathVariable String clienteId) {
        return ResponseEntity.ok(veiculoService.listarPorCliente(clienteId));
    }
}

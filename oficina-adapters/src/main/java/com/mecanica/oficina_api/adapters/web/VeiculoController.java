package com.mecanica.oficina_api.adapters.web;

import com.mecanica.oficina_api.application.veiculo.usecase.AlterarVeiculoUseCase;
import com.mecanica.oficina_api.application.veiculo.usecase.CadastrarVeiculoUseCase;
import com.mecanica.oficina_api.application.veiculo.usecase.ConsultarVeiculoUseCase;
import com.mecanica.oficina_api.application.veiculo.usecase.InativarVeiculoUseCase;
import com.mecanica.oficina_api.application.veiculo.usecase.ListarVeiculosPorClienteUseCase;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mecanica.oficina_api.adapters.web.dto.request.AlterarVeiculoRequest;
import com.mecanica.oficina_api.adapters.web.dto.request.CadastrarVeiculoRequest;
import com.mecanica.oficina_api.adapters.web.dto.response.VeiculoResponse;
import com.mecanica.oficina_api.adapters.web.dto.response.ValidationErrorResponse;
import com.mecanica.oficina_api.adapters.web.presenter.VeiculoPresenter;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("veiculo")
@Tag(name = "Veículo", description = "Gerenciamento de veículos dos clientes da oficina")
@SecurityRequirement(name = "bearerAuth")
public class VeiculoController {

    private final CadastrarVeiculoUseCase cadastrarVeiculoUseCase;
    private final ConsultarVeiculoUseCase consultarVeiculoUseCase;
    private final ListarVeiculosPorClienteUseCase listarVeiculosPorClienteUseCase;
    private final AlterarVeiculoUseCase alterarVeiculoUseCase;
    private final InativarVeiculoUseCase inativarVeiculoUseCase;
    private final VeiculoPresenter presenter;

    public VeiculoController(CadastrarVeiculoUseCase cadastrarVeiculoUseCase,
                            ConsultarVeiculoUseCase consultarVeiculoUseCase,
                            ListarVeiculosPorClienteUseCase listarVeiculosPorClienteUseCase,
                            AlterarVeiculoUseCase alterarVeiculoUseCase,
                            InativarVeiculoUseCase inativarVeiculoUseCase,
                            VeiculoPresenter presenter) {
        this.cadastrarVeiculoUseCase = cadastrarVeiculoUseCase;
        this.consultarVeiculoUseCase = consultarVeiculoUseCase;
        this.listarVeiculosPorClienteUseCase = listarVeiculosPorClienteUseCase;
        this.alterarVeiculoUseCase = alterarVeiculoUseCase;
        this.inativarVeiculoUseCase = inativarVeiculoUseCase;
        this.presenter = presenter;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Cadastrar um novo veículo", description = "Permite cadastrar um novo veículo para um cliente na oficina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Veículo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    public ResponseEntity<VeiculoResponse> cadastrar(@Valid @RequestBody CadastrarVeiculoRequest request) {
        Veiculo veiculo = cadastrarVeiculoUseCase.executar(
                request.getClienteId(),
                request.getPlaca(),
                request.getMarca(),
                request.getModelo(),
                request.getAno(),
                request.getCor()
        );
        return ResponseEntity.status(201).body(presenter.apresentar(veiculo));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Consultar veículo por ID", description = "Retorna os detalhes de um veículo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veículo encontrado"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<VeiculoResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(presenter.apresentar(consultarVeiculoUseCase.executar(id)));
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Listar veículos de um cliente", description = "Retorna todos os veículos associados a um cliente")
    @ApiResponse(responseCode = "200", description = "Lista de veículos do cliente")
    public ResponseEntity<List<VeiculoResponse>> listarPorCliente(@PathVariable String clienteId) {
        return ResponseEntity.ok(presenter.apresentar(listarVeiculosPorClienteUseCase.executar(clienteId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE', 'MECANICO')")
    @Operation(summary = "Alterar um veículo", description = "Permite atualizar os dados de um veículo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos de entrada",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<VeiculoResponse> alterar(@PathVariable String id, @Valid @RequestBody AlterarVeiculoRequest request) {
        Veiculo veiculo = alterarVeiculoUseCase.executar(
                id,
                request.getPlaca(),
                request.getMarca(),
                request.getModelo(),
                request.getAno(),
                request.getCor()
        );
        return ResponseEntity.ok(presenter.apresentar(veiculo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATENDENTE')")
    @Operation(summary = "Deletar um veículo por ID", description = "Permite deletar um veículo específico usando seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Veículo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado",
                    content = @Content(schema = @Schema()))
    })
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        inativarVeiculoUseCase.executar(id);
        return ResponseEntity.status(204).build();
    }
}

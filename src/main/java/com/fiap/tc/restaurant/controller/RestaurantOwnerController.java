package com.fiap.tc.restaurant.controller;

import com.fiap.tc.restaurant.dto.request.RestaurantOwnerRequest;
import com.fiap.tc.restaurant.dto.request.UpdatePasswordRequest;
import com.fiap.tc.restaurant.dto.request.UpdateUserRequest;
import com.fiap.tc.restaurant.dto.response.UserResponse;
import com.fiap.tc.restaurant.service.RestaurantOwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant-owners")
@Tag(name = "Restaurant Owners", description = "Gerenciamento de donos de restaurante")
public class RestaurantOwnerController {

    private final RestaurantOwnerService service;

    public RestaurantOwnerController(RestaurantOwnerService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar dono de restaurante", description = "Cadastra um novo dono de restaurante. Endpoint público.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dono criado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Campos inválidos ou ausentes",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "E-mail ou login já em uso",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<UserResponse> create(@RequestBody @Valid RestaurantOwnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Buscar dono por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dono encontrado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Dono não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Buscar donos por nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de donos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<List<UserResponse>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Deletar dono de restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dono deletado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Dono não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Atualizar dados do dono", description = "Apenas o próprio dono pode atualizar seus dados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Campos inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Tentativa de modificar dados de outro usuário",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Dono não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "E-mail já em uso",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}/password")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Alterar senha do dono", description = "Apenas o próprio dono pode alterar sua senha.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Tentativa de modificar senha de outro usuário",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Dono não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody @Valid UpdatePasswordRequest request) {
        service.updatePassword(id, request);
        return ResponseEntity.ok().build();
    }
}

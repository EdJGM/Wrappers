package ec.com.intelectus.system.sgb.serviciosbasicos.controllers;

import ec.com.intelectus.system.sgb.serviciosbasicos.dtos.request.ServicioSolicitadoRqDTO;
import ec.com.intelectus.system.sgb.serviciosbasicos.dtos.request.SolicitudContratoRqDTO;
import ec.com.intelectus.system.sgb.serviciosbasicos.dtos.response.*;
import ec.com.intelectus.system.sgb.serviciosbasicos.services.interfaces.ISolicitudContratoService;
import ec.com.intelectus.system.sgb.utilitario.util.UsuarioAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitud-contrato")
@RequiredArgsConstructor
@Tag(name = "Solicitud Contrato", description = "Gestión de solicitudes de servicios básicos (Agua/Alcantarillado)")
public class SolicitudContratoController {

    private static final Logger log = LoggerFactory.getLogger(SolicitudContratoController.class);

    private final ISolicitudContratoService solicitudContratoService;

    @GetMapping("/predios/autocompletar")
    @Operation(summary = "Autocompletar búsqueda de predios (Legacy: Por Código o Nombre)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de predios encontrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<PredioGenericoRsDTO>> autocompletarBusquedaPredio(
            @RequestParam String search) {
        try {
            List<PredioGenericoRsDTO> predios = solicitudContratoService.autocompletarBusquedaPredio(search);
            return ResponseEntity.ok(predios);
        } catch (Exception e) {
            log.error("Error en autocompletarBusquedaPredio: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/cuentas/autocompletar")
    @Operation(summary = "Autocompletar búsqueda de cuentas de agua")
    public ResponseEntity<List<SerCuentaRsDTO>> autocompletarCuentaAgua(
            @RequestParam String search) {
        try {
            List<SerCuentaRsDTO> cuentas = solicitudContratoService.autocompletarCuentaAgua(search);
            return ResponseEntity.ok(cuentas);
        } catch (Exception e) {
            log.error("Error en autocompletarCuentaAgua: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/propietario/buscar")
    @Operation(summary = "Buscar propietario por identificación y asociar barrio del predio")
    public ResponseEntity<PropietarioBarrioRsDTO> buscarPropietario(
            @RequestParam String identificacion,
            @RequestParam(required = false) Long predioId) {
        try {
            PropietarioBarrioRsDTO resultado = solicitudContratoService.buscarPropietario(identificacion, predioId);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error buscando propietario: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/categorias")
    @Operation(summary = "Listar categorías activas")
    public ResponseEntity<List<SerCategoriaRsDTO>> listaCategorias() {
        try {
            List<SerCategoriaRsDTO> categorias = solicitudContratoService.listaCategorias();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            log.error("Error listando categorías: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/servicios-defecto/{tipoPedido}")
    @Operation(summary = "Generar servicios por defecto según tipo de pedido (1=Agua, 2=Alcantarillado, 3=Ambos)")
    public ResponseEntity<List<ServicioSolicitadoRsDTO>> generarServiciosPorDefecto(
            @PathVariable Short tipoPedido) {
        try {
            List<ServicioSolicitadoRsDTO> servicios = solicitudContratoService.generarServiciosPorDefecto(tipoPedido);
            return ResponseEntity.ok(servicios);
        } catch (Exception e) {
            log.error("Error generando servicios por defecto: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar solicitudes existentes (Filtradas por estado si se requiere)")
    public ResponseEntity<List<SolicitudContratoRsDTO>> listarSolicitudes(
            @RequestParam(required = false, defaultValue = "R,A") String estados) {
        try {
            List<SolicitudContratoRsDTO> lista = solicitudContratoService.listarSolicitudes(estados);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            log.error("Error al listar solicitudes: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{socId}")
    @Operation(summary = "Obtener solicitud por ID (Incluye carga para edición)")
    public ResponseEntity<SolicitudContratoRsDTO> obtenerPorId(@PathVariable Integer socId) {
        try {
            SolicitudContratoRsDTO solicitud = solicitudContratoService.obtenerPorId(socId);
            if (solicitud == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(solicitud);
        } catch (Exception e) {
            log.error("Error al obtener solicitud {}: {}", socId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/guardar")
    @Operation(summary = "Guardar o Actualizar Solicitud (Grabar/Modificar Action)")
    public ResponseEntity<SolicitudContratoRsDTO> guardarSolicitud(
            @RequestBody SolicitudCompositeRqDTO request) {
        try {
            UsuarioAdmin usuarioAutenticado = obtenerUsuarioAutenticado();
            if (usuarioAutenticado == null) {
                log.warn("Intento de guardar solicitud sin usuario autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            SolicitudContratoRsDTO result = solicitudContratoService.guardarSolicitud(
                    request.getSolicitud(),
                    request.getServicios(),
                    usuarioAutenticado
            );

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error guardando solicitud: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/servicios/{servicioId}/caracteristicas")
    @Operation(summary = "Cargar características de un servicio público específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de características del servicio"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<CatalogoRsDTO>> cargarCaracteristicasDelServicioPublico(
            @PathVariable Short servicioId) {
        try {
            List<CatalogoRsDTO> caracteristicas = solicitudContratoService.cargarCaracteristicasDelServicioPublico(servicioId);
            return ResponseEntity.ok(caracteristicas);
        } catch (Exception e) {
            log.error("Error cargando características del servicio {}: {}", servicioId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }


    private UsuarioAdmin obtenerUsuarioAutenticado() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UsuarioAdmin) {
                return (UsuarioAdmin) authentication.getPrincipal();
            }
            return null;
        } catch (Exception e) {
            log.error("Error obteniendo usuario de sesión", e);
            return null;
        }
    }

    @Data
    public static class SolicitudCompositeRqDTO {
        private SolicitudContratoRqDTO solicitud;
        private List<ServicioSolicitadoRqDTO> servicios;
    }
}
package ec.com.intelectus.system.sgb.serviciosbasicos.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudContratoRsDTO implements Serializable {
    private Integer socId;
    private Date socFechaSolicitud;
    private String socEstadoSolicitud; // Convertido a String para mostrar 'Registrado'/'Aprobada'
    private String socNumeroSolicitud;
    private String socPedido;
    private String socRepresentante;
    private Date socFechaFormulario;
    private Short socServPedido;

    // Relaciones DTOs completos o resumidos segun necesidad
    private GesPropietarioRsDTO propietario;
    private CatPredioRsDTO predio;
    private SerCuentaRsDTO cuenta;
    private SerCategoriaRsDTO categoria;
    private List<ServicioSolicitadoRsDTO> servicios;
}
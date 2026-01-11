package ec.com.intelectus.system.sgb.serviciosbasicos.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudContratoRqDTO implements Serializable {
    private Integer socId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Date socFechaSolicitud;
    private Character socEstadoSolicitud;
    private String socInformeTecnico;
    private String socResponsableFactibilidad;
    private Character socExistenciaRed;
    private String socNumeroContrato;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Date socFechaContrato;
    private Short socNumeroCuotas;
    private BigDecimal socValorContado;
    private BigDecimal socValorCredito;
    private BigDecimal socCuotaValor;
    private BigDecimal socValorDescuento;
    private BigDecimal socValorContrato;
    private Short socServPedido; // 1=Agua, 2=Alcantarillado, 3=Ambos
    private Short socFormaPago;
    private String socPedido;
    private String socRepresentante;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Date socFechaFormulario;
    private String socNumeroSolicitud;
    private String socRegistradoPor;

    // Foreign Keys
    private Integer cuentaId; // cue_id
    private Integer propietarioId; // pro_id
    private Long predioId; // pre_id
    private Short categoriaId; // ctg_id
}
package ec.com.intelectus.system.sgb.serviciosbasicos.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PredioGenericoRsDTO implements Serializable {
    private Long preId;
    private String codigoCatastral;
    private String codigoAnterior;
    private String direccionPrincipal;
    private BigDecimal areaTotalTerreno;
    private BigDecimal areaTotalConstruccion;
    private String cedulaPropietario;
    private Short tipoPropiedad; // 1=Urbano, 0=Rural
    private String nombrePredio;

    // Objeto completo si es necesario, aunque preferible usar DTOs anidados
    private CatPredioRsDTO catPredio;
}
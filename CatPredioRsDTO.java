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
public class CatPredioRsDTO implements Serializable {
    private Long preId;
    private String preCodigoCatastral;
    private String preCodigoAnterior;
    private String preDireccionPrincipal;
    private BigDecimal preAreaTotalTer;
    private BigDecimal preAreaTotalConst;
    private String nombrePredio; // Campo computado o auxiliar
}
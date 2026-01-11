package ec.com.intelectus.system.sgb.serviciosbasicos.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SerCuentaRsDTO implements Serializable {
    private Integer cueId;
    private String cueNumeroCta;
    private String cueClaveCatastral;
    private String medidorNumero; // Obtenido de SerCuentaMedidor
    private GesPropietarioRsDTO propietario;
}
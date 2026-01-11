package ec.com.intelectus.system.sgb.serviciosbasicos.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicioSolicitadoRqDTO implements Serializable {
    private Integer ssoId;
    private Short ssoEstado;
    private Integer servicioPublicoId; // ser_id
    private Integer solicitudContratoId; // soc_id
    private Integer opcDiametroId; // cat_id (GesCatalogo)
}
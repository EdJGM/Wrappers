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
public class ServicioSolicitadoRsDTO implements Serializable {
    private Integer ssoId;
    private Short ssoEstado; // 1=Activo, 0=Inactivo
    private String ssoEstadoDescripcion; // "Activo" / "Inactivo"

    private ServicioPublicoRsDTO servicioPublico;
    private CatalogoRsDTO opcDiametro;
}
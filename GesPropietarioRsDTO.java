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
public class GesPropietarioRsDTO implements Serializable {
    private Integer proId;
    private String proNumIdentificacion;
    private String proNombre;
    private String proApellido;
    private String proNombreCompleto; // proApellido + " " + proNombre
    private String proDireccionDomicilio;
    private String proTelefono1;
    private String proTelefono2;
    private String proCorreoElectronico;
}

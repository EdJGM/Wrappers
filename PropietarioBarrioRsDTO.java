package ec.com.intelectus.system.sgb.serviciosbasicos.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropietarioBarrioRsDTO implements Serializable {
    private GesPropietarioRsDTO propietario;
    private CatBarrioRsDTO barrio;
    private Boolean existe; // Para controlar el mensaje "Solicitante no existe"
}

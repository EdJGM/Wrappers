package ec.com.intelectus.system.sgb.serviciosbasicos.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CatBarrioRsDTO {
    private Integer barId;
    private String barNombre;
    private String barSiglas;
    private String barDescripcion;

    // Relación aplanada de CatParroquias
    private Integer parroquiaId;
    private String parroquiaNombre;
}
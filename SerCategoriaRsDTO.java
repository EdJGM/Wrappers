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
public class SerCategoriaRsDTO implements Serializable {
    private Short ctgId;
    private String ctgNombre;
}

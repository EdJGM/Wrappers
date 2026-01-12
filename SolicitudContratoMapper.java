package ec.com.intelectus.system.sgb.serviciosbasicos.mappers;

import ec.com.intelectus.system.sgb.serviciosbasicos.dtos.request.SolicitudContratoRqDTO;
import ec.com.intelectus.system.sgb.serviciosbasicos.dtos.response.*;
import ec.com.intelectus.system.sgd.colmena.catastro.CatBarrio;
import ec.com.intelectus.system.sgd.colmena.catastro.CatPredio;
import ec.com.intelectus.system.sgd.colmena.gestion.GesCatalogo;
import ec.com.intelectus.system.sgd.colmena.gestion.GesPropietario;
import ec.com.intelectus.system.sgd.colmena.serviciosbasicos.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SolicitudContratoMapper {

    public SolicitudContratoRsDTO toSolicitudContratoRsDTO(SerSolicitudContrato entity) {
        if (entity == null) return null;

        String estadoDescripcion = null;
        if (entity.getSocEstadoSolicitud() != null) {
            if (entity.getSocEstadoSolicitud() == 'R') estadoDescripcion = "Registrado";
            else if (entity.getSocEstadoSolicitud() == 'A') estadoDescripcion = "Aprobada";
            else estadoDescripcion = String.valueOf(entity.getSocEstadoSolicitud());
        }

        SolicitudContratoRsDTO dto = SolicitudContratoRsDTO.builder()
                .socId(entity.getSocId())
                .socFechaSolicitud(entity.getSocFechaSolicitud())
                .socEstadoSolicitud(estadoDescripcion)
                .socNumeroSolicitud(entity.getSocNumeroSolicitud())
                .socPedido(entity.getSocPedido())
                .socRepresentante(entity.getSocRepresentante())
                .socFechaFormulario(entity.getSocFechaFormulario())
                .socServPedido(entity.getSocServPedido())
                .propietario(toGesPropietarioRsDTO(entity.getPropietario()))
                .predio(toCatPredioRsDTO(entity.getPredio()))
                .cuenta(toSerCuentaRsDTOWithMedidor(entity.getCuenta()))
                .categoria(toSerCategoriaRsDTO(entity.getCategoria()))
                .build();
        if (entity.getSerServicioSolicitadoList() != null && !entity.getSerServicioSolicitadoList().isEmpty()) {
            List<ServicioSolicitadoRsDTO> serviciosDto = entity.getSerServicioSolicitadoList().stream()
                    .map(this::toServicioSolicitadoRsDTO)
                    .collect(Collectors.toList());
            dto.setServicios(serviciosDto);
        }
        return dto;
    }

    private SerCuentaRsDTO toSerCuentaRsDTOWithMedidor(SerCuenta entity) {
        if (entity == null) return null;

        String numeroMedidor = null;
        try {
            if (entity.getSerCuentaMedidorList() != null && !entity.getSerCuentaMedidorList().isEmpty()) {
                numeroMedidor = entity.getSerCuentaMedidorList().get(0).getMedidor().getMedNumeroMedidor();
            }
        } catch (Exception e) {
            numeroMedidor = entity.getMedidor();
        }

        return SerCuentaRsDTO.builder()
                .cueId(entity.getCueId())
                .cueNumeroCta(entity.getCueNumeroCta())
                .cueClaveCatastral(entity.getCueClaveCatastral())
                .medidorNumero(numeroMedidor)
                .propietario(toGesPropietarioRsDTO(entity.getPropietario()))
                .build();
    }

    public SerSolicitudContrato toSolicitudContratoEntity(SolicitudContratoRqDTO dto) {
        if (dto == null) return null;

        SerSolicitudContrato entity = new SerSolicitudContrato();
        entity.setSocId(dto.getSocId());
        entity.setSocFechaSolicitud(dto.getSocFechaSolicitud());
        entity.setSocEstadoSolicitud(dto.getSocEstadoSolicitud());
        entity.setSocInformeTecnico(dto.getSocInformeTecnico());
        entity.setSocResponsableFactibilidad(dto.getSocResponsableFactibilidad());
        entity.setSocExistenciaRed(dto.getSocExistenciaRed());
        entity.setSocNumeroContrato(dto.getSocNumeroContrato());
        entity.setSocFechaContrato(dto.getSocFechaContrato());
        entity.setSocNumeroCuotas(dto.getSocNumeroCuotas());
        entity.setSocValorContado(dto.getSocValorContado());
        entity.setSocValorCredito(dto.getSocValorCredito());
        entity.setSocCuotaValor(dto.getSocCuotaValor());
        entity.setSocValorDescuento(dto.getSocValorDescuento());
        entity.setSocValorContrato(dto.getSocValorContrato());
        entity.setSocServPedido(dto.getSocServPedido());
        entity.setSocFormaPago(dto.getSocFormaPago());
        entity.setSocPedido(dto.getSocPedido());
        entity.setSocRepresentante(dto.getSocRepresentante());
        entity.setSocFechaFormulario(dto.getSocFechaFormulario());
        entity.setSocNumeroSolicitud(dto.getSocNumeroSolicitud());
        entity.setSocRegistradoPor(dto.getSocRegistradoPor());

        if (dto.getPropietarioId() != null) {
            GesPropietario prop = new GesPropietario();
            prop.setProId(dto.getPropietarioId());
            entity.setPropietario(prop);
        }

        if (dto.getCategoriaId() != null) {
            SerCategorias cat = new SerCategorias();
            cat.setCtgId(dto.getCategoriaId().shortValue());
            entity.setCategoria(cat);
        }

        if (dto.getPredioId() != null) {
            CatPredio predio = new CatPredio();
            predio.setPreId(dto.getPredioId());
            entity.setPredio(predio);
        }

        if (dto.getCuentaId() != null) {
            SerCuenta cuenta = new SerCuenta();
            cuenta.setCueId(dto.getCuentaId());
            entity.setCuenta(cuenta);
        }

        return entity;
    }

    public List<SolicitudContratoRsDTO> toSolicitudContratoRsDTOList(List<SerSolicitudContrato> entities) {
        if (entities == null || entities.isEmpty()) return new ArrayList<>();
        return entities.stream().map(this::toSolicitudContratoRsDTO).collect(Collectors.toList());
    }

    public GesPropietarioRsDTO toGesPropietarioRsDTO(GesPropietario entity) {
        if (entity == null) return null;
        return GesPropietarioRsDTO.builder()
                .proId(entity.getProId())
                .proNumIdentificacion(entity.getProNumIdentificacion())
                .proNombre(entity.getProNombre())
                .proApellido(entity.getProApellido())
                .proNombreCompleto((entity.getProApellido() != null ? entity.getProApellido() : "") + " " +
                        (entity.getProNombre() != null ? entity.getProNombre() : ""))
                .proDireccionDomicilio(entity.getProDireccionDomicilio())
                .proTelefono1(entity.getProTelefono1())
                .proTelefono2(entity.getProTelefono2())
                .proCorreoElectronico(entity.getProCorreoElectronico())
                .build();
    }

    public CatPredioRsDTO toCatPredioRsDTO(CatPredio entity) {
        if (entity == null) return null;
        return CatPredioRsDTO.builder()
                .preId(entity.getPreId())
                .preCodigoCatastral(entity.getPreCodigoCatastral())
                .preCodigoAnterior(entity.getPreCodigoAnterior())
                .preDireccionPrincipal(entity.getPreDireccionPrincipal())
                .preAreaTotalTer(entity.getPreAreaTotalTer())
                .preAreaTotalConst(entity.getPreAreaTotalConst())
                .nombrePredio(entity.getPreNombrePredio())
                .build();
    }

    public SerCuentaRsDTO toSerCuentaRsDTO(SerCuenta entity, String medidorNumero) {
        if (entity == null) return null;

        return SerCuentaRsDTO.builder()
                .cueId(entity.getCueId())
                .cueNumeroCta(entity.getCueNumeroCta())
                .cueClaveCatastral(entity.getCueClaveCatastral())
                .medidorNumero(medidorNumero)
                .propietario(toGesPropietarioRsDTO(entity.getPropietario()))
                .build();
    }

    public SerCategoriaRsDTO toSerCategoriaRsDTO(SerCategorias entity) {
        if (entity == null) return null;
        return SerCategoriaRsDTO.builder()
                .ctgId(entity.getCtgId())
                .ctgNombre(entity.getCtgNombre())
                .build();
    }

    public ServicioPublicoRsDTO toServicioPublicoRsDTO(SerServicioPublico entity) {
        if (entity == null) return null;
        return ServicioPublicoRsDTO.builder()
                .serId(entity.getSerId())
                .serDescripcion(entity.getSerDescripcion())
                .serTag(entity.getSerTag())
                .build();
    }

    public CatalogoRsDTO toCatalogoRsDTO(GesCatalogo entity) {
        if (entity == null) return null;
        return CatalogoRsDTO.builder()
                .catId(entity.getCatId())
                .catNombre(entity.getCatNombre())
                .catTag(entity.getCatTag())
                .build();
    }

    public CatBarrioRsDTO toCatBarrioRsDTO(CatBarrio entity) {
        if (entity == null) return null;
        return CatBarrioRsDTO.builder()
                .barId(entity.getBarId())
                .barNombre(entity.getBarNombre())
                .build();
    }

    public ServicioSolicitadoRsDTO toServicioSolicitadoRsDTO(SerServicioSolicitado entity) {
        if (entity == null) return null;

        String estadoDesc = (entity.getSsoEstado() != null && entity.getSsoEstado() == 1) ? "Activo" : "Inactivo";

        ServicioSolicitadoRsDTO dto = ServicioSolicitadoRsDTO.builder()
                .ssoId(entity.getSsoId())
                .ssoEstado(entity.getSsoEstado())
                .ssoEstadoDescripcion(estadoDesc)
                .servicioPublico(toServicioPublicoRsDTO(entity.getServicioPublico()))
                .opcDiametro(toCatalogoRsDTO(entity.getOpcDiametro()))
                .build();
        return dto;
    }
}
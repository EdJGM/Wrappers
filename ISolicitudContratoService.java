package ec.com.intelectus.system.sgb.serviciosbasicos.services.interfaces;

import ec.com.intelectus.system.sgb.serviciosbasicos.dtos.request.ServicioSolicitadoRqDTO;
import ec.com.intelectus.system.sgb.serviciosbasicos.dtos.request.SolicitudContratoRqDTO;
import ec.com.intelectus.system.sgb.serviciosbasicos.dtos.response.*;
import ec.com.intelectus.system.sgb.utilitario.util.UsuarioAdmin;

import java.util.List;

public interface ISolicitudContratoService {

    List<PredioGenericoRsDTO> autocompletarBusquedaPredio(String search);

    List<SerCuentaRsDTO> autocompletarCuentaAgua(String search);

    SolicitudContratoRsDTO obtenerPorId(Integer socId) throws Exception;

    List<SolicitudContratoRsDTO> listarSolicitudes(String estados);

    SolicitudContratoRsDTO guardarSolicitud(SolicitudContratoRqDTO cabecera, List<ServicioSolicitadoRqDTO> detalles, UsuarioAdmin usuario);

    List<ServicioSolicitadoRsDTO> generarServiciosPorDefecto(Short tipoPedido);

    PropietarioBarrioRsDTO buscarPropietario(String identificacion, Long predioId);

    List<SerCategoriaRsDTO> listaCategorias();

    List<CatalogoRsDTO> cargarCaracteristicasDelServicioPublico(Short servicioPublicoId);

}

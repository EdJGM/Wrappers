package ec.com.intelectus.system.sgb.serviciosbasicos.services.impl;

import ec.com.intelectus.system.sgb.serviciosbasicos.dtos.request.ServicioSolicitadoRqDTO;
import ec.com.intelectus.system.sgb.serviciosbasicos.dtos.request.SolicitudContratoRqDTO;
import ec.com.intelectus.system.sgb.serviciosbasicos.dtos.response.*;
import ec.com.intelectus.system.sgb.serviciosbasicos.mappers.SolicitudContratoMapper;
import ec.com.intelectus.system.sgb.serviciosbasicos.services.interfaces.*;
import ec.com.intelectus.system.sgb.serviciosbasicos.utils.Cuenta;
import ec.com.intelectus.system.sgb.serviciosbasicos.utils.ServiciosBasicos;
import ec.com.intelectus.system.sgb.utilitario.util.AbstracFacade;
import ec.com.intelectus.system.sgb.utilitario.util.UsuarioAdmin;

import ec.com.intelectus.system.sgd.colmena.catastro.CatBarrio;
import ec.com.intelectus.system.sgd.colmena.catastro.CatPredio;
import ec.com.intelectus.system.sgd.colmena.gestion.GesCatalogo;
import ec.com.intelectus.system.sgd.colmena.gestion.GesPropietario;
import ec.com.intelectus.system.sgd.colmena.serviciosbasicos.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudContratoService extends AbstracFacade implements ISolicitudContratoService {

    private static final Logger log = LoggerFactory.getLogger(SolicitudContratoService.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final SolicitudContratoMapper mapper;
    private final Cuenta cuenta;
    private final ServiciosBasicos serviciosBasicos;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    private CatBarrio buscarBarrio(CatPredio predio) {
        try {
            List<CatBarrio> listaDatos = serviciosBasicos.listaBarriosPredio(predio);
            if (listaDatos != null && !listaDatos.isEmpty()) {
                if (listaDatos.size() > 0) {
                    return listaDatos.get(0);
                }
            }
        } catch (Exception e) {
            return new CatBarrio();
        }
        return null;
    }

    @Override
    public List<PredioGenericoRsDTO> autocompletarBusquedaPredio(String search) {
        List<PredioGenericoRsDTO> listaResultado = new ArrayList<>();

        if (search != null && search.length() > 3) {
            List<Object> listadosRural = autoPredioPropietario(search, "1, 0", "1, 0");

            for (Object obj : listadosRural) {
                if (obj instanceof CatPredio) {
                    CatPredio ipredio = (CatPredio) obj;

                    String nombrePredio = ipredio.getPreNombrePredio() != null ? ipredio.getPreNombrePredio() : "";
                    String[] cedula = nombrePredio.split(" ");
                    String cedulaPropietario = (cedula.length > 0) ? cedula[0] : "";

                    PredioGenericoRsDTO mpredio = PredioGenericoRsDTO.builder()
                            .preId(ipredio.getPreId())
                            .codigoCatastral(ipredio.getPreCodigoCatastral())
                            .codigoAnterior(ipredio.getPreCodigoAnterior())
                            .direccionPrincipal(ipredio.getPreDireccionPrincipal())
                            .areaTotalTerreno(ipredio.getPreAreaTotalTer())
                            .areaTotalConstruccion(ipredio.getPreAreaTotalConst())
                            .cedulaPropietario(cedulaPropietario)
                            .tipoPropiedad(ipredio.getPreTipo())
                            .nombrePredio(ipredio.getPreNombrePredio())
                            .build();

                    listaResultado.add(mpredio);
                }
            }
        }
        return listaResultado;
    }

    @Override
    public List<SerCuentaRsDTO> autocompletarCuentaAgua(String search) {
        List<SerCuentaRsDTO> listadoResultado = new ArrayList<>();

        try {
            if (search != null && search.trim().length() > 6) {

                // Por Datos de Cuenta / Propietario
                List<Object> listados = cuenta.autoCuentaAguaTodas(search);

                if (listados != null && !listados.isEmpty()) {
                    for (Object obj : listados) {
                        if (obj instanceof SerCuenta) {
                            SerCuenta cuentaObj = (SerCuenta) obj;
                            medidorAgua(cuentaObj);
                            String numeroMedidor = cuentaObj.getMedidor();
                            listadoResultado.add(mapper.toSerCuentaRsDTO(cuentaObj, numeroMedidor));
                        }
                    }
                }
                // Si no hay resultados, buscar por Medidor
                if (listadoResultado.isEmpty()) {
                    List<SerMedidorAgua> listaMedidores = cuenta.cargarMedidorByNroMedidorEjb(search.trim());

                    if (listaMedidores != null && listaMedidores.size() == 1) {
                        try {
                            SerMedidorAgua medidorAgua = listaMedidores.get(0);

                            List<SerCuenta> cuentasPorMedidor = cuenta.cargarCuentasAguaByMedidorEjb(medidorAgua);

                            if (cuentasPorMedidor != null) {
                                for (SerCuenta cta : cuentasPorMedidor) {
                                    String codigoMedidor = (medidorAgua != null) ? medidorAgua.getMedNumeroMedidor() : search;

                                    listadoResultado.add(mapper.toSerCuentaRsDTO(cta, codigoMedidor));
                                }
                            }
                        } catch (Exception e) {
                            log.warn("Error controlada al cargar cuentas por medidor: {}", e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error en autocompletarCuentaAgua", e);
        }

        return listadoResultado;
    }

    private void medidorAgua(SerCuenta item) {
        try {
            List<SerCuentaMedidor> cuentaMedidor = cuenta.listaCuentaMedidoresAguaAdmin(item);
            for (SerCuentaMedidor serCuentaMedidor : cuentaMedidor) {
                item.setMedidor(serCuentaMedidor.getMedidor().getMedNumeroMedidor());
            }

        } catch (Exception e) {
            log.error("activarMedidorAsignado={}" + e);
        }
    }

    @Override
    public PropietarioBarrioRsDTO buscarPropietario(String identificacion, Long predioId) {
        GesPropietario propietarioEncontrado = new GesPropietario();
        CatBarrio barrioEncontrado = null;
        boolean existe = false;

        try {
            CatPredio predio = new CatPredio();
            if (predioId != null) {
                Object p = buscar(CatPredio.class, predioId);
                if (p instanceof CatPredio) {
                    predio = (CatPredio) p;
                }
            }

            List<GesPropietario> listaPropietarios = new ArrayList<>();
            try {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("proNumIdentificacion", identificacion);
                listaPropietarios = listar(GesPropietario.class, parameters, "proNumIdentificacion", true);
            } catch (Exception e) {
                log.error("buscarPropietarioIdentificacionEjb id={}", e);
            }

            if (listaPropietarios != null && listaPropietarios.size() > 0) {
                propietarioEncontrado = listaPropietarios.get(0);
                barrioEncontrado = buscarBarrio(predio);
                existe = true;

            } else {
                propietarioEncontrado = new GesPropietario();
            }

        } catch (Exception e) {
            log.error("buscarPropietario id={}", e);
        }

        return PropietarioBarrioRsDTO.builder()
                .propietario(mapper.toGesPropietarioRsDTO(propietarioEncontrado))
                .barrio(mapper.toCatBarrioRsDTO(barrioEncontrado))
                .existe(existe)
                .build();
    }

    @Override
    public List<SerCategoriaRsDTO> listaCategorias() {
        List<SerCategoriaRsDTO> resultado = new ArrayList<>();
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ctgActivo", (short) 1);
            List<SerCategorias> lista = listar(SerCategorias.class, parameters, "ctgNombre", true);

            if (lista != null) {
                for (SerCategorias cat : lista) {
                    resultado.add(mapper.toSerCategoriaRsDTO(cat));
                }
            }
        } catch (Exception e) {
            log.error("Error al cargar categorías", e);
        }
        return resultado;
    }

    @Override
    @Transactional
    public List<ServicioSolicitadoRsDTO> generarServiciosPorDefecto(Short tipoPedido) {
        List<ServicioSolicitadoRsDTO> listaServicios = new ArrayList<>();
        try {
            if (tipoPedido == null) return listaServicios;
            Map<String, Object> params = new HashMap<>();
            params.put("serEstado", (short) 1); // 1 = Activo
            List<SerServicioPublico> todosServicios = listar(SerServicioPublico.class, params, "serId", true);

            for (SerServicioPublico sp : todosServicios) {
                boolean agregar = false;
                String tag = sp.getSerTag();
                if (tag == null) tag = "";

                if (tipoPedido == 2) {
                    // CASO 2: ALCANTARILLADO
                    if ("ALCANTARILLADO".equals(tag)) {
                        agregar = true;
                    }
                } else if (tipoPedido == 1) {
                    // CASO 1: AGUA
                    if (!"ALCANTARILLADO".equals(tag)) {
                        agregar = true;
                    }
                } else if (tipoPedido == 3) {
                    // CASO 3: AMBOS -> Agrega todo
                    agregar = true;
                }

                if (agregar) {
                    SerServicioSolicitado tempEntity = new SerServicioSolicitado();
                    tempEntity.setServicioPublico(sp);
                    tempEntity.setSsoEstado((short) 1);

                    listaServicios.add(mapper.toServicioSolicitadoRsDTO(tempEntity));
                }
            }
        } catch (Exception e) {
            log.error("Error generando servicios por defecto", e);
        }
        return listaServicios;
    }

    @Override
    @Transactional
    public SolicitudContratoRsDTO guardarSolicitud(SolicitudContratoRqDTO cabecera, List<ServicioSolicitadoRqDTO> detalles, UsuarioAdmin usuario) {
        try {
            SerSolicitudContrato entity;

            if (cabecera.getSocId() != null) {
                // ----------------------------------------------------------------
                // MODIFICAR (modificarAction)
                // ----------------------------------------------------------------
                entity = (SerSolicitudContrato)buscar(SerSolicitudContrato.class, cabecera.getSocId());
                SerSolicitudContrato objectoOldSolicitud = entity;

                if (entity != null) {
                    // Set Descripción Auditoría
                    usuario.setDescripcion("Se modificó la Soliciud " + entity.getSocId());

                    entity.setSocNumeroSolicitud(cabecera.getSocNumeroSolicitud());
                    entity.setSocRepresentante(cabecera.getSocRepresentante());
                    entity.setSocPedido(cabecera.getSocPedido());
                    entity.setSocFechaFormulario(cabecera.getSocFechaFormulario());
                    entity.setSocServPedido(cabecera.getSocServPedido());
                    entity.setSocInformeTecnico(cabecera.getSocInformeTecnico());
                    entity.setSocResponsableFactibilidad(cabecera.getSocResponsableFactibilidad());
                    entity.setSocExistenciaRed(cabecera.getSocExistenciaRed());

                    // Relaciones (Categoria, Predio, Propietario, Cuenta)
                    actualizarRelaciones(entity, cabecera);
                    modificar(entity, objectoOldSolicitud, usuario);

                } else {
                    throw new RuntimeException("Solicitud no encontrada para edición");
                }

            } else {
                // ----------------------------------------------------------------
                // CREAR (grabarAction)
                // ----------------------------------------------------------------
                entity = mapper.toSolicitudContratoEntity(cabecera);

                actualizarRelaciones(entity, cabecera);
                entity.setSocFechaSolicitud(new Date());
                entity.setSocEstadoSolicitud('R'); // R = Registrado
                String propId = (entity.getPropietario() != null) ? String.valueOf(entity.getPropietario().getProId()) : "";
                usuario.setDescripcion("Se creó la solicitud para propietario: " + propId);
                crear(entity, usuario);
                getEntityManager().flush(); // Para asegurar ID
            }

            // ----------------------------------------------------------------
            // GESTIÓN DE DETALLES (SERVICIOS)
            // ----------------------------------------------------------------
            boolean esCreacion = (cabecera.getSocId() == null);

            if ((detalles == null || detalles.isEmpty())) {

                Map<String, Object> params = new HashMap<>();
                params.put("serEstado", (short) 1);
                List<SerServicioPublico> todosServicios = listar(SerServicioPublico.class, params, "serId", true);

                for (SerServicioPublico sp : todosServicios) {
                    boolean agregar = false;
                    String tag = sp.getSerTag();
                    if (tag == null) tag = "";

                    Short tipoPedido = entity.getSocServPedido();

                    if (tipoPedido == 2) { // Solo Alcantarillado
                        if ("ALCANTARILLADO".equals(tag)) agregar = true;
                    } else if (tipoPedido == 1) { // Solo Agua
                        if (!"ALCANTARILLADO".equals(tag)) agregar = true;
                    } else if (tipoPedido == 3) { // Ambos
                        agregar = true;
                    }

                    if (agregar) {
                        SerServicioSolicitado detEntity = new SerServicioSolicitado();
                        detEntity.setSolicitudContrato(entity);
                        detEntity.setServicioPublico(sp);
                        detEntity.setSsoEstado((short) 1);

                        usuario.setDescripcion("Se creó servicio automático: " + sp.getSerDescripcion());
                        crear(detEntity, usuario);
                    }
                }
            }
            // Si enviaron detalles explícitos
            else {
                for (ServicioSolicitadoRqDTO detDto : detalles) {
                    SerServicioSolicitado detEntity;

                    if (detDto.getSsoId() == null) {
                        // --- NUEVO DETALLE EN LA LISTA ---
                        detEntity = new SerServicioSolicitado();
                        detEntity.setSolicitudContrato(entity);
                        detEntity.setSsoEstado(detDto.getSsoEstado());

                        // Asignar Servicio
                        if (detDto.getServicioPublicoId() != null) {
                            SerServicioPublico sp = (SerServicioPublico) buscar(SerServicioPublico.class, detDto.getServicioPublicoId().shortValue());
                            detEntity.setServicioPublico(sp);
                        }

                        // Asignar Diámetro
                        if (detDto.getOpcDiametroId() != null) {
                            GesCatalogo diametro = (GesCatalogo) buscar(GesCatalogo.class, detDto.getOpcDiametroId());
                            detEntity.setOpcDiametro(diametro);
                        }

                        usuario.setDescripcion("Se agregó servicio ID: " + detDto.getServicioPublicoId());
                        crear(detEntity, usuario);

                    } else {
                        // --- MODIFICAR DETALLE EXISTENTE ---
                        detEntity = (SerServicioSolicitado) buscar(SerServicioSolicitado.class, detDto.getSsoId());
                        SerServicioSolicitado detEntityOld = detEntity; // Para auditoría si fuera clone

                        if (detEntity != null) {
                            detEntity.setSsoEstado(detDto.getSsoEstado());

                            if (detDto.getOpcDiametroId() != null) {
                                GesCatalogo diametro = (GesCatalogo) buscar(GesCatalogo.class, detDto.getOpcDiametroId());
                                detEntity.setOpcDiametro(diametro);
                            } else {
                                detEntity.setOpcDiametro(null);
                            }

                            usuario.setDescripcion("Se modificó servicio ID: " + detDto.getSsoId());
                            modificar(detEntity, detEntityOld, usuario);
                        }
                    }
                }
            }

            return mapper.toSolicitudContratoRsDTO(entity);

        } catch (Exception e) {
            log.error("Error al guardar solicitud: {}", e.getMessage());
            throw new RuntimeException("Error al guardar la solicitud", e);
        }
    }

    // Helper para evitar duplicar código de seteo de relaciones
    private void actualizarRelaciones(SerSolicitudContrato entity, SolicitudContratoRqDTO cabecera) {
        // Categoria
        if (cabecera.getCategoriaId() != null) {
            SerCategorias cat = new SerCategorias();
            cat.setCtgId(cabecera.getCategoriaId().shortValue());
            entity.setCategoria(cat);
        } else {
            entity.setCategoria(null);
        }

        // Predio
        try {
            if (cabecera.getPredioId() != null && cabecera.getPredioId() > 0) {
                CatPredio p = new CatPredio();
                p.setPreId(cabecera.getPredioId());
                entity.setPredio(p);
            } else {
                entity.setPredio(null);
            }
        } catch (Exception e) {
            entity.setPredio(null);
        }

        // Propietario
        if (cabecera.getPropietarioId() != null) {
            GesPropietario prop = new GesPropietario();
            prop.setProId(cabecera.getPropietarioId());
            entity.setPropietario(prop);
        }

        // Cuenta
        if (cabecera.getCuentaId() != null) {
            SerCuenta cta = new SerCuenta();
            cta.setCueId(cabecera.getCuentaId());
            entity.setCuenta(cta);
        } else {
            entity.setCuenta(null);
        }
    }

    @Override
    public List<SolicitudContratoRsDTO> listarSolicitudes(String estados) {
        List<SerSolicitudContrato> lista = cuenta.listaSolicitudesEstadoEjb(estados);
        List<SerSolicitudContrato> listaCasteada = new ArrayList<>();
        if (lista != null) {
            for (Object o : lista) {
                if (o instanceof SerSolicitudContrato) {
                    listaCasteada.add((SerSolicitudContrato) o);
                }
            }
        }
        return mapper.toSolicitudContratoRsDTOList(listaCasteada);
    }

    @Override
    public SolicitudContratoRsDTO obtenerPorId(Integer socId) throws Exception {
        if (socId == null) return null;
        SerSolicitudContrato entity = (SerSolicitudContrato)buscar(SerSolicitudContrato.class, socId);
        if (entity == null) {
            throw new Exception("Solicitud no encontrada: " + socId);
        }
        return mapper.toSolicitudContratoRsDTO(entity);
    }

    @Override
    public List<CatalogoRsDTO> cargarCaracteristicasDelServicioPublico(Short servicioPublicoId) {
        List<CatalogoRsDTO> resultado = new ArrayList<>();
        try {
            if (servicioPublicoId == null) return resultado;

            SerServicioPublico servicioPublico = (SerServicioPublico) buscar(SerServicioPublico.class, servicioPublicoId);
            if (servicioPublico == null || servicioPublico.getSerTag() == null) {
                return resultado;
            }

            GesCatalogo cat = (GesCatalogo) buscar(GesCatalogo.class, "catTag", servicioPublico.getSerTag());

            if (cat != null) {
                // Buscar características del servicio
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("gesCatalogo", cat);
                parameters.put("catEstado", (short) 1);
                List<GesCatalogo> listaCaracteristicas = listar(GesCatalogo.class, parameters, "catOrden", true);

                if (listaCaracteristicas != null) {
                    for (GesCatalogo caracteristica : listaCaracteristicas) {
                        resultado.add(mapper.toCatalogoRsDTO(caracteristica));
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error al cargar características del servicio público: {}", e.getMessage());
        }
        return resultado;
    }
}
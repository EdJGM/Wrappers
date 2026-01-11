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
import ec.com.intelectus.system.sgd.colmena.serviciosbasicos.SerCategorias;
import ec.com.intelectus.system.sgd.colmena.serviciosbasicos.SerCuenta;
import ec.com.intelectus.system.sgd.colmena.serviciosbasicos.SerServicioPublico;
import ec.com.intelectus.system.sgd.colmena.serviciosbasicos.SerServicioSolicitado;
import ec.com.intelectus.system.sgd.colmena.serviciosbasicos.SerSolicitudContrato;

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
        List<SerCuentaRsDTO> resultados = new ArrayList<>();
        if (search != null && !search.isEmpty()) {
            List<Object> cuentas = cuenta.autoCuentaAguaTodas(search);
            if (cuentas != null) {
                for (Object obj : cuentas) {
                    if (obj instanceof SerCuenta) {
                        resultados.add(mapper.toSerCuentaRsDTO((SerCuenta) obj));
                    }
                }
            }
        }
        return resultados;
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

            if (tipoPedido == 1 || tipoPedido == 3) {
                // Buscar directo
                SerServicioPublico servicioAgua = (SerServicioPublico)buscar(SerServicioPublico.class, (short) 1);
                if (servicioAgua != null) {
                    listaServicios.add(ServicioSolicitadoRsDTO.builder()
                            .servicioPublico(mapper.toServicioPublicoRsDTO(servicioAgua))
                            .ssoEstado((short) 1)
                            .ssoEstadoDescripcion("Activo")
                            .build());
                }
            }

            if (tipoPedido == 2 || tipoPedido == 3) {
                // Buscar directo
                SerServicioPublico servicioAlcantarillado = (SerServicioPublico)buscar(SerServicioPublico.class, (short) 2);
                if (servicioAlcantarillado != null) {
                    listaServicios.add(ServicioSolicitadoRsDTO.builder()
                            .servicioPublico(mapper.toServicioPublicoRsDTO(servicioAlcantarillado))
                            .ssoEstado((short) 1)
                            .ssoEstadoDescripcion("Activo")
                            .build());
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
                    modificar(entity, objectoOldSolicitud, usuario);

                } else {
                    throw new RuntimeException("Solicitud no encontrada para edición");
                }

            } else {
                // ----------------------------------------------------------------
                // CREAR (grabarAction)
                // ----------------------------------------------------------------
                entity = mapper.toSolicitudContratoEntity(cabecera);

                try {
                    if (cabecera.getPredioId() != null && cabecera.getPredioId() > 0) {
                        CatPredio p = new CatPredio();
                        p.setPreId(cabecera.getPredioId());
                        entity.setPredio(p);
                    }
                } catch (Exception e) {
                    entity.setPredio(new CatPredio());
                }

                if (cabecera.getPropietarioId() != null) {
                    GesPropietario prop = new GesPropietario();
                    prop.setProId(cabecera.getPropietarioId());
                    entity.setPropietario(prop);
                }

                entity.setSocFechaSolicitud(new Date());
                entity.setSocEstadoSolicitud('R'); // R = Registrado

                String propId = (entity.getPropietario() != null) ? String.valueOf(entity.getPropietario().getProId()) : "";
                usuario.setDescripcion("Se creó la solicitud " + propId);
                crear(entity, usuario);
                getEntityManager().flush(); // Para asegurar ID
            }

            // ----------------------------------------------------------------
            // GESTIÓN DE DETALLES (SERVICIOS)
            // ----------------------------------------------------------------
            // Nota: El legacy itera "listaServicios" y filtra por tipo (1,2,3).
            // Aquí iteramos "detalles" que ya viene filtrado desde el Front.
            if (detalles != null && !detalles.isEmpty()) {
                for (ServicioSolicitadoRqDTO detDto : detalles) {
                    SerServicioSolicitado detEntity;

                    if (detDto.getSsoId() == null) {
                        detEntity = new SerServicioSolicitado();
                        detEntity.setSolicitudContrato(entity);
                        detEntity.setSsoEstado(detDto.getSsoEstado());

                        if (detDto.getServicioPublicoId() != null) {
                            SerServicioPublico sp = (SerServicioPublico)buscar(SerServicioPublico.class, detDto.getServicioPublicoId().shortValue());
                            detEntity.setServicioPublico(sp);
                        }

                        // Asignar Servicio
                        if (detDto.getServicioPublicoId() != null) {
                            SerServicioPublico sp = (SerServicioPublico)buscar(SerServicioPublico.class, detDto.getServicioPublicoId().shortValue());
                            detEntity.setServicioPublico(sp);
                        }

                        // Asignar Diámetro
                        if (detDto.getOpcDiametroId() != null) {
                            GesCatalogo diametro = (GesCatalogo) buscar(GesCatalogo.class, detDto.getOpcDiametroId());
                            detEntity.setOpcDiametro(diametro);
                        }

                        usuario.setDescripcion("Se creó el servicio de la solicitud  " + entity.getSocId());
                        crear(detEntity, usuario);

                    } else {
                        // --- MODIFICACIÓN DE SERVICIO EXISTENTE ---
                        detEntity = (SerServicioSolicitado)buscar(SerServicioSolicitado.class, detDto.getSsoId());
                        SerServicioSolicitado detEntityOld = detEntity;

                        if (detEntity != null) {
                            detEntity.setSsoEstado(detDto.getSsoEstado());

                            if (detDto.getOpcDiametroId() != null) {
                                GesCatalogo diametro = (GesCatalogo) buscar(GesCatalogo.class, detDto.getOpcDiametroId());
                                detEntity.setOpcDiametro(diametro);
                            } else {
                                detEntity.setOpcDiametro(null);
                            }

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
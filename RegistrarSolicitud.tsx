import { Column } from 'primereact/column';
import { Toast } from 'primereact/toast';
import { Toolbar } from 'primereact/toolbar';
import { useEffect, useRef, useState } from 'react';
import { FaFile, FaSave, FaTimes, FaPencilAlt } from 'react-icons/fa';

import AutoCompletePrimary from '~/components/shared/AutoCompletePrimary';
import ButtonCancel from '~/components/shared/ButtonCancel';
import ButtonPrimary from '~/components/shared/ButtonPrimary';
import CalendarPrimary from '~/components/shared/CalendarPrimary';
import DataTablePrimary from '~/components/shared/DataTablePrimary';
import DropdownPrimary from '~/components/shared/DropdownPrimary';
import InputTextPrimary from '~/components/shared/InputTextPrimary';
import LoadingSpinner from '~/components/shared/LoadingSpinner';
import PanelPrimary from '~/components/shared/PanelPrimary';
import { COLORS } from '~/constants/colors';
import { PredioGenericoRsDTO } from '~/types/general/PredioGenericoRsDTO';
import { SerCategoriaRsDTO } from '~/types/general/SerCategoriaRsDTO';
import { SerCuentaRsDTO } from '~/types/general/SerCuentaRsDTO';

import * as solicitudService from '../service/SolicitudService';
import { CatalogoRsDTO, ServicioSolicitadoRqDTO, SolicitudCompositeRqDTO, SolicitudContratoRqDTO, SolicitudContratoRsDTO } from '../types/SolicitudTypes';

export default function RegistrarSolicitud() {
    const toast = useRef<Toast>(null);

    const [viewMode, setViewMode] = useState<'LIST' | 'NEW' | 'EDIT'>('LIST');
    const [loading, setLoading] = useState(false);
    const [loadingAction, setLoadingAction] = useState(false);
    const [swNuevaSolicitud, setSwNuevaSolicitud] = useState(false);
    const [swSolicitaSoloAlcantarillado, setSwSolicitaSoloAlcantarillado] = useState(false);
    const [swDatosSolicitaCiudadano, setSwDatosSolicitaCiudadano] = useState(false);
    const [swServiciosSolicitados, setSwServiciosSolicitados] = useState(false);
    const [, setSwEdicionServiciosSolicitados] = useState(false);

    // =================== DATOS ===================
    const [listaSolicitudes, setListaSolicitudes] = useState<SolicitudContratoRsDTO[]>([]);
    const [categorias, setCategorias] = useState<SerCategoriaRsDTO[]>([]);
    const [listaCaracteristicasServicio, setListaCaracteristicasServicio] = useState<CatalogoRsDTO[]>([]);
    const initialSolicitud: SolicitudContratoRqDTO = {
        socFechaSolicitud: new Date(),
        socFechaFormulario: new Date(),
        socEstadoSolicitud: 'R',
        socServPedido: undefined,
        socNumeroSolicitud: '',
        socInformeTecnico: '',
        socRepresentante: '',
        socPedido: '',
    };
    const [solicitud, setSolicitud] = useState<SolicitudContratoRqDTO>(initialSolicitud);
    const [listaServiciosSolicitados, setListaServiciosSolicitados] = useState<ServicioSolicitadoRqDTO[]>([]);
    const [servicioSolicitado, setServicioSolicitado] = useState<ServicioSolicitadoRqDTO>({
        ssoEstado: 1,
        servicioPublicoId: 0
    });
    const [prediosSuggestions, setPrediosSuggestions] = useState<PredioGenericoRsDTO[]>([]);
    const [selectedPredio, setSelectedPredio] = useState<PredioGenericoRsDTO | null>(null);
    const [cuentasSuggestions, setCuentasSuggestions] = useState<SerCuentaRsDTO[]>([]);
    const [selectedCuenta, setSelectedCuenta] = useState<SerCuentaRsDTO | null>(null);
    const [displayPropietario, setDisplayPropietario] = useState({
        identificacion: '',
        apellidos: '',
        nombres: '',
        direccion: '',
        telefonos: '',
        email: ''
    });

    const tiposPedido = [
        { label: 'AGUA', value: 1 },
        { label: 'ALCANTARILLADO', value: 2 },
        { label: 'AGUA / ALCANTARILLADO', value: 3 }
    ];

    const opcionesEstadoServicio = [
        { label: 'Activo', value: 1 },
        { label: 'Inactivo', value: 0 }
    ];

    // =================== INICIALIZACIÓN ===================
    useEffect(() => {
        cargarDatosMaestros();
    }, []);

    const cargarDatosMaestros = async () => {
        setLoading(true);
        try {
            const [list, cats] = await Promise.all([
                solicitudService.listarSolicitudes('R,A'),
                solicitudService.listaCategorias()
            ]);
            setListaSolicitudes(list);
            setCategorias(cats);
        } catch (e) { console.error(e); }
        finally { setLoading(false); }
    };

    // =================== ACCIONES ===================
    const nuevaSolicitud = () => {
        limpiarFormulario();
        setSwNuevaSolicitud(true);
        setViewMode('NEW');
    };

    const cancelarAction = () => {
        limpiarFormulario();
        setViewMode('LIST');
    };

    const limpiarFormulario = () => {
        setSolicitud(initialSolicitud);
        setListaServiciosSolicitados([]);
        setSwSolicitaSoloAlcantarillado(false);
        setSwDatosSolicitaCiudadano(false);
        setSwServiciosSolicitados(false);
        setSelectedPredio(null);
        setSelectedCuenta(null);
        setDisplayPropietario({ identificacion: '', apellidos: '', nombres: '', direccion: '', telefonos: '', email: '' });
    };

    const seleccionServicioSolicitado = (val: number) => {
        setSolicitud(prev => ({ ...prev, socServPedido: val }));

        if (val === 2) { // ALCANTARILLADO
            setSwSolicitaSoloAlcantarillado(true);
            setSwDatosSolicitaCiudadano(true); 
        } else if (val === 1 || val === 3) { // AGUA o AMBOS
            setSwSolicitaSoloAlcantarillado(false);
            setSwDatosSolicitaCiudadano(true); 
        } else {
            setSwSolicitaSoloAlcantarillado(false);
            setSwDatosSolicitaCiudadano(false);
        }

        // Limpiar selecciones al cambiar tipo
        setSelectedPredio(null);
        setSelectedCuenta(null);
        setDisplayPropietario({ identificacion: '', apellidos: '', nombres: '', direccion: '', telefonos: '', email: '' });
    };

    const onPredioSelect = async (predio: PredioGenericoRsDTO) => {
        setSelectedPredio(predio);
        setSolicitud(prev => ({ ...prev, predioId: predio.preId }));


        if (predio.cedulaPropietario) {
            await ejecutarBuscarPropietario(predio.cedulaPropietario, predio.preId);
        }
    };

    const ejecutarBuscarPropietario = async (cedula: string, predioId?: number) => {
        setLoadingAction(true);
        try {
            const res = await solicitudService.buscarPropietario(cedula, predioId);
            if (res.existe && res.propietario) {
                setSolicitud(prev => ({ ...prev, propietarioId: res.propietario.proId }));
                setDisplayPropietario({
                    identificacion: res.propietario.proNumIdentificacion,
                    apellidos: res.propietario.proApellido,
                    nombres: res.propietario.proNombre,
                    direccion: res.propietario.proDireccionDomicilio || '',
                    telefonos: `${res.propietario.proTelefono1 || ''} ${res.propietario.proTelefono2 || ''}`,
                    email: res.propietario.proCorreoElectronico || ''
                });
            } else {
                toast.current?.show({ severity: 'warn', summary: 'Aviso', detail: 'Solicitante no existe', life: 3000 });
                setSolicitud(prev => ({ ...prev, propietarioId: undefined }));
            }
        } catch (e) {
            console.error(e);
        } finally {
            setLoadingAction(false);
        }
    };

    const onCuentaSelect = (cuenta: SerCuentaRsDTO) => {
        setSelectedCuenta(cuenta);
        if (cuenta.cueId) {
            setSolicitud(prev => ({ ...prev, cuentaId: cuenta.cueId }));

            if (cuenta.propietario) {
                setSolicitud(prev => ({ ...prev, propietarioId: cuenta.propietario?.proId }));
                setDisplayPropietario({
                    identificacion: cuenta.propietario.proNumIdentificacion,
                    apellidos: cuenta.propietario.proApellido,
                    nombres: cuenta.propietario.proNombre,
                    direccion: cuenta.propietario.proDireccionDomicilio || '',
                    telefonos: `${cuenta.propietario.proTelefono1 || ''}`,
                    email: cuenta.propietario.proCorreoElectronico || ''
                });
            }
        }
    };

    const grabarAction = async () => {
        if (!solicitud.socServPedido) {
            toast.current?.show({ severity: 'error', summary: 'Error', detail: 'Seleccione servicio', life: 3000 });
            return;
        }
        setLoadingAction(true);
        try {
            const payload: SolicitudCompositeRqDTO = {
                solicitud: solicitud,
                servicios: viewMode === 'EDIT' ? listaServiciosSolicitados : []
            };
            await solicitudService.guardarSolicitud(payload);
            toast.current?.show({ severity: 'success', summary: 'Éxito', detail: 'Guardado correctamente', life: 3000 });
            const list = await solicitudService.listarSolicitudes('R,A');
            setListaSolicitudes(list);
            cancelarAction();
        } catch (e) {
            toast.current?.show({ severity: 'error', summary: 'Error', detail: 'No se pudo guardar', life: 3000 });
        } finally {
            setLoadingAction(false);
        }
    };

    const editarRegistro = async (item: SolicitudContratoRsDTO) => {
        setLoadingAction(true);
        try {
            const fullData = await solicitudService.obtenerPorId(item.socId);
            setSolicitud({
                socId: fullData.socId,
                socFechaSolicitud: fullData.socFechaSolicitud ? new Date(fullData.socFechaSolicitud + 'T00:00:00') : undefined,
                socFechaFormulario: fullData.socFechaFormulario ? new Date(fullData.socFechaFormulario + 'T00:00:00') : new Date(),
                socEstadoSolicitud: fullData.socEstadoSolicitud === 'Registrado' ? 'R' : 'A',
                socServPedido: fullData.socServPedido,
                socNumeroSolicitud: fullData.socNumeroSolicitud,
                socRepresentante: fullData.socRepresentante,
                socPedido: fullData.socPedido,
                categoriaId: fullData.categoria?.ctgId,
                propietarioId: fullData.propietario?.proId,
                predioId: fullData.predio?.preId,
                cuentaId: fullData.cuenta?.cueId
            });

            if (fullData.propietario) {
                setDisplayPropietario({
                    identificacion: fullData.propietario.proNumIdentificacion,
                    apellidos: fullData.propietario.proApellido,
                    nombres: fullData.propietario.proNombre,
                    direccion: fullData.propietario.proDireccionDomicilio || '',
                    telefonos: fullData.propietario.proTelefono1 || '',
                    email: fullData.propietario.proCorreoElectronico || ''
                });
            }

            if (fullData.predio) {
                setSelectedPredio({
                    preId: fullData.predio.preId,
                    codigoCatastral: fullData.predio.preCodigoCatastral,
                    nombrePredio: fullData.predio.nombrePredio,
                    cedulaPropietario: fullData.propietario?.proNumIdentificacion
                } as PredioGenericoRsDTO);
            }
            if (fullData.cuenta) setSelectedCuenta(fullData.cuenta);

            if (fullData.servicios && fullData.servicios.length > 0) {
                const mapped = fullData.servicios.map(s => ({
                    ssoId: s.ssoId, 
                    ssoEstado: s.ssoEstado,
                    servicioPublicoId: s.servicioPublico.serId,
                    servicioPublicoDescripcion: s.servicioPublico.serDescripcion,
                    opcDiametroId: s.opcDiametro?.catId,
                    opcDiametroNombre: s.opcDiametro?.catNombre
                } as ServicioSolicitadoRqDTO));
                setListaServiciosSolicitados(mapped);
            }
            else if (fullData.socServPedido) {
                const servs = await solicitudService.generarServiciosPorDefecto(fullData.socServPedido);
                const mapped = servs.map(s => ({
                    ssoId: s.ssoId, 
                    ssoEstado: s.ssoEstado,
                    servicioPublicoId: s.servicioPublico.serId,
                    servicioPublicoDescripcion: s.servicioPublico.serDescripcion
                } as any));
                setListaServiciosSolicitados(mapped);
            }

            setSwNuevaSolicitud(false);
            setSwDatosSolicitaCiudadano(true);
            setSwSolicitaSoloAlcantarillado(fullData.socServPedido === 2);
            setViewMode('EDIT');

        } catch (e) { console.error(e); }
        finally { setLoadingAction(false); }
    };

    const editarRegistroServicioSolicitado = async (item: ServicioSolicitadoRqDTO) => {
        setServicioSolicitado(item);
        setSwServiciosSolicitados(true);
        setSwEdicionServiciosSolicitados(true);
        // Cargar características del servicio seleccionado
        if (item.servicioPublicoId) {
            try {
                const caracteristicas = await solicitudService.cargarCaracteristicasDelServicioPublico(item.servicioPublicoId);
                setListaCaracteristicasServicio(caracteristicas);
            } catch (e) {
                setListaCaracteristicasServicio([]);
            }
        }
    };

    const modificarCaracteristicaServicioSolicitado = () => {
        const newList = listaServiciosSolicitados.map(s => {
            const coincideId = s.ssoId && servicioSolicitado.ssoId && s.ssoId === servicioSolicitado.ssoId;
            const coincideNuevo = !s.ssoId && !servicioSolicitado.ssoId && s.servicioPublicoId === servicioSolicitado.servicioPublicoId;

            if (coincideId || coincideNuevo) {
                return {
                    ...servicioSolicitado,
                    opcDiametroNombre: listaCaracteristicasServicio.find(c => c.catId === servicioSolicitado.opcDiametroId)?.catNombre
                };
            }
            return s;
        });
        setListaServiciosSolicitados(newList);
        setSwServiciosSolicitados(false);
        setSwEdicionServiciosSolicitados(false);
    };

    return (
        <div className="card">
            <Toast ref={toast} />
            {(loading || loadingAction) ? <div className="flex items-center justify-center min-h-screen bg-gray-50">
                <div className="flex flex-col items-center gap-4">
                    <LoadingSpinner style={{ width: '80px', height: '80px' }} />
                    <span className="text-xl font-semibold text-gray-700">
                        Cargando datos, por favor espere...
                    </span>
                </div>
            </div> : null}

            {/* HEADER PRINCIPAL */}
            <div
                className="mb-4 p-6 rounded-lg shadow-lg"
                style={{
                    background: `linear-gradient(135deg, ${COLORS.PRIMARY} 0%, ${COLORS.PRIMARY_HOVER} 100%)`,
                    borderLeft: `6px solid ${COLORS.SECONDARY}`
                }}
            >
                <div className="flex items-center gap-4">
                    <FaFile className="text-white text-4xl" />
                    <div>
                        <h1 className="text-3xl font-bold text-white mb-2">
                            Registro de Solicitudes de Servicio de Agua
                        </h1>
                        <p className="text-white text-opacity-90 text-sm">
                            Gestión y administración de solicitudes de servicios básicos de agua y alcantarillado.
                        </p>
                    </div>
                </div>
            </div>

            {/* TOOLBAR */}
            <Toolbar start={
                <div className="flex gap-2">
                    {viewMode === 'LIST' ? <ButtonPrimary label="Nuevo" icon={<FaFile />} onClick={nuevaSolicitud} /> : null}
                    {viewMode !== 'LIST' ? <>
                        <ButtonPrimary label={viewMode === 'NEW' ? "Guardar" : "Modificar"} icon={<FaSave />} onClick={grabarAction} />
                        <ButtonCancel label="Cancelar" icon={<FaTimes />} onClick={cancelarAction} />
                    </> : null}
                </div>
            }
            style={{
                border: `1px solid ${COLORS.PRIMARY_LIGHT}`,
                margin: '0 0 1rem 0',
                borderRadius: '8px',
                padding: '0.5rem 1rem'
            }} 
            />

            {/* FORMULARIO */}
            {viewMode !== 'LIST' ? <PanelPrimary header="Datos de la Solicitud de Servicios">
                    {/* 1. SERVICIO SOLICITADO */}
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                        <div className="flex items-center gap-2">
                            <span className="font-bold">Servicio Solicitado:</span>
                            <DropdownPrimary
                                value={solicitud.socServPedido}
                                options={tiposPedido}
                                onChange={(e) => seleccionServicioSolicitado(e.value)}
                                placeholder="Seleccionar..."
                                disabled={!swNuevaSolicitud}
                            />
                        </div>
                    </div>

                    {/* 2. CUENTA (Solo Alcantarillado) */}
                    {swSolicitaSoloAlcantarillado ? <div className="grid grid-cols-1 gap-4 mb-4 border-t pt-4">
                            <div className="flex items-center gap-2">
                                <span className="font-bold w-32">Cuenta:</span>
                                <div className="flex-1 max-w-xl flex gap-2">
                                    {swNuevaSolicitud ? (
                                        <AutoCompletePrimary
                                            value={selectedCuenta}
                                            suggestions={cuentasSuggestions}
                                            completeMethod={(e) => solicitudService.autocompletarCuentaAgua(e.query).then(setCuentasSuggestions)}
                                            field="cueNumeroCta"
                                            placeholder="Buscar..."
                                            onChange={(e) => {
                                                if (e.value && typeof e.value === 'object') onCuentaSelect(e.value);
                                                else setSelectedCuenta(e.value);
                                            }}
                                            itemTemplate={(item: SerCuentaRsDTO, index) => {
                                                if (index === 0) {
                                                    return (
                                                        <>
                                                            <div className="grid grid-cols-6 font-bold text-xs bg-blue-100 border-b border-blue-300 px-2 py-1">
                                                                <span>Cuenta</span>
                                                                <span>Medidor</span>
                                                                <span>Apellido</span>
                                                                <span>Nombre</span>
                                                                <span>Cédula/RUC</span>
                                                                <span>Clave Catastral</span>
                                                            </div>
                                                            <div className="grid grid-cols-6 items-center px-2 py-1 border-b border-gray-100 hover:bg-blue-50 transition text-xs">
                                                                <span className="font-bold">{item.cueNumeroCta}</span>
                                                                <span className="font-bold">{item.medidorNumero}</span>
                                                                <span className="font-bold">{item.propietario?.proApellido}</span>
                                                                <span className="font-bold">{item.propietario?.proNombre}</span>
                                                                <span className="font-bold">{item.propietario?.proNumIdentificacion}</span>
                                                                <span className="font-bold">{item.cueClaveCatastral}</span>
                                                            </div>
                                                        </>
                                                    );
                                                }
                                                return (
                                                    <div className="grid grid-cols-6 items-center px-2 py-1 border-b border-gray-100 hover:bg-blue-50 transition text-xs">
                                                        <span className="font-bold">{item.cueNumeroCta}</span>
                                                        <span className="font-bold">{item.medidorNumero}</span>
                                                        <span className="font-bold">{item.propietario?.proApellido}</span>
                                                        <span className="font-bold">{item.propietario?.proNombre}</span>
                                                        <span className="font-bold">{item.propietario?.proNumIdentificacion}</span>
                                                        <span className="font-bold">{item.cueClaveCatastral}</span>
                                                    </div>
                                                );
                                            }}
                                        />
                                    ) : (
                                        // Solo muestra la cuenta como label en modo EDIT
                                        selectedCuenta ? (
                                            <span className="p-2 bg-gray-100 rounded font-bold">
                                                {selectedCuenta.cueNumeroCta} - {selectedCuenta.medidorNumero} - {selectedCuenta.propietario?.proNombre} {selectedCuenta.propietario?.proApellido}
                                            </span>
                                        ) : (
                                            <span className="text-gray-500">Sin cuenta asociada</span>
                                        )
                                    )}
                                    {swNuevaSolicitud ? <span className="text-xs text-gray-500 self-center">
                                            (Ingrese el Nro de Cuenta, Cedula o Apellidos del Propietario, Observación, Nro de MEDIDOR)
                                        </span> : null}                                
                                </div>
                            </div>
                        </div> : null}

                    {/* 3. DATOS CIUDADANO (Predio y Detalles) */}
                    {swDatosSolicitaCiudadano ? <div className="flex flex-col gap-4 border-t pt-4">

                            {/* Buscador Predio: Solo visible en Nuevo Registro (según rendered="#{...nuevoRegistro=='1'}") */}
                            {swNuevaSolicitud ? <div className="grid grid-cols-1 gap-4">
                                    <div className="flex items-center gap-2">
                                        <span className="font-bold w-32">Propietario/Ci:</span>
                                        <div className="flex-1 max-w-xl">
                                            <AutoCompletePrimary
                                                value={selectedPredio}
                                                suggestions={prediosSuggestions}
                                                completeMethod={(e) => solicitudService.autocompletarBusquedaPredio(e.query).then(setPrediosSuggestions)}
                                                field="codigoCatastral"
                                                placeholder="Buscar..."
                                                onChange={(e) => {
                                                    if (e.value && typeof e.value === 'object') onPredioSelect(e.value);
                                                    else setSelectedPredio(e.value);
                                                }}
                                                itemTemplate={(item: PredioGenericoRsDTO) => (
                                                    <div className="flex flex-col px-2 py-1 border-b border-gray-100 hover:bg-blue-50 transition">
                                                        <div className="flex items-center gap-2">
                                                            <span className="font-bold text-blue-700">{item.codigoCatastral}</span>
                                                            <span className="text-gray-500 ml-2">[{item.tipoPropiedad === 1 ? 'Urbano' : 'Rural'}]</span>
                                                            <span>{item.cedulaPropietario}</span>
                                                        </div>
                                                        <div className="flex gap-2text-gray-700">
                                                            <span className="truncate max-w-[900px]">{item.nombrePredio}</span>
                                                        </div>
                                                    </div>
                                                )}
                                            />
                                        </div>
                                    </div>
                                </div> : null}

                            {/* Campos OutputText (Identificación, Nombres, etc.) */}
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-2">
                                <div className="flex gap-2"><span className="font-bold w-40">Identificación:</span> <span>{displayPropietario.identificacion}</span></div>
                                <div className="flex gap-2"></div>
                                <div className="flex gap-2"><span className="font-bold w-40">Nombres:</span> <span>{displayPropietario.nombres}</span></div>
                                <div className="flex gap-2"><span className="font-bold w-40">Apellidos:</span> <span>{displayPropietario.apellidos}</span></div>
                                <div className="flex gap-2"><span className="font-bold w-40">Dirección:</span> <span>{displayPropietario.direccion}</span></div>
                                <div className="flex gap-2"><span className="font-bold w-40">Teléfonos:</span> <span>{displayPropietario.telefonos}</span></div>
                                <div className="flex gap-2"><span className="font-bold w-40">Correo:</span> <span>{displayPropietario.email}</span></div>
                                <div className="flex gap-2"><span className="font-bold w-40">Predio:</span> <span>{selectedPredio?.codigoCatastral}</span></div>
                            </div>

                            {/* Campos Administrativos */}
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                                <div className="flex flex-col">
                                    <span className="font-bold text-sm mb-1">Fecha Registro:</span>
                                    <CalendarPrimary value={solicitud.socFechaSolicitud} onChange={(e) => setSolicitud({ ...solicitud, socFechaSolicitud: e.value as Date })} showIcon />
                                </div>
                                <div className="flex flex-col">
                                    <span className="font-bold text-sm mb-1">Fecha Solicitud:</span>
                                    <CalendarPrimary value={solicitud.socFechaFormulario} onChange={(e) => setSolicitud({ ...solicitud, socFechaFormulario: e.value as Date })} showIcon />
                                </div>
                                <div className="flex flex-col">
                                    <span className="font-bold text-sm mb-1">Número Solicitud:</span>
                                    <InputTextPrimary value={solicitud.socNumeroSolicitud} onChange={(e) => setSolicitud({ ...solicitud, socNumeroSolicitud: e.target.value })} maxLength={16} required />
                                </div>
                                <div className="flex flex-col">
                                    <span className="font-bold text-sm mb-1">Representante:</span>
                                    <InputTextPrimary value={solicitud.socRepresentante} onChange={(e) => setSolicitud({ ...solicitud, socRepresentante: e.target.value })} maxLength={64} />
                                </div>
                                <div className="flex flex-col">
                                    <span className="font-bold text-sm mb-1">Detalle Solicitud:</span>
                                    <InputTextPrimary value={solicitud.socPedido} onChange={(e) => setSolicitud({ ...solicitud, socPedido: e.target.value })} maxLength={1024} required />
                                </div>
                                <div className="flex flex-col">
                                    <span className="font-bold text-sm mb-1">Categoría:</span>
                                    <DropdownPrimary
                                        value={solicitud.categoriaId}
                                        options={categorias}
                                        optionLabel="ctgNombre"
                                        optionValue="ctgId"
                                        onChange={(e) => setSolicitud({ ...solicitud, categoriaId: e.value })}
                                        placeholder="Seleccionar..."
                                    />
                                </div>
                            </div>
                        </div> : null}

                    {/* 4. TABLA SERVICIOS (Solo visible en MODIFICAR) */}
                    {viewMode === 'EDIT' ? <div className="mt-6 border-t pt-4">
                            <h3 className="font-bold text-lg mb-2">Servicios de la Solicitud</h3>
                            <DataTablePrimary value={listaServiciosSolicitados} rows={5}>
                                <Column header="Servicio" field="servicioPublicoDescripcion" />
                                <Column header="Característica Solicitada" 
                                    body={(r) => r.opcDiametroNombre || 'No especificado'}
                                />     
                                <Column header="Estado" body={(r) => r.ssoEstado === 1 ? 'Activo' : 'Inactivo'} />
                                <Column header="Acciones" body={(r) => (
                                    <button onClick={() => editarRegistroServicioSolicitado(r)} className="text-green-600"><FaPencilAlt /></button>
                                )} />
                            </DataTablePrimary>
                        </div> : null}

                    {/* 5. PANEL EDICION SERVICIO INDIVIDUAL (Solo si swServiciosSolicitados) */}
                    {swServiciosSolicitados ? <PanelPrimary header="Edición de Servicio" className="mt-4">
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
                                <div className="flex flex-col">
                                    <span className="font-bold mb-1">Servicio:</span>
                            <span className="p-2 bg-gray-100 rounded block">  {servicioSolicitado.servicioPublicoDescripcion}</span>
                                </div>
                                <div className="flex flex-col">
                                    <span className="font-bold mb-1">Característica del Servicio Público:</span>
                                    <DropdownPrimary
                                        value={servicioSolicitado.opcDiametroId}
                                        options={listaCaracteristicasServicio}
                                        optionLabel="catNombre"
                                        optionValue="catId"
                                        onChange={(e) => setServicioSolicitado({
                                            ...servicioSolicitado,
                                            opcDiametroId: e.value,
                                            opcDiametroNombre: listaCaracteristicasServicio.find(c => c.catId === e.value)?.catNombre
                                        })}
                                        placeholder="Seleccionar..."
                                    />
                                </div>                                
                                <div className="flex flex-col">
                                    <span className="font-bold mb-1">Estado:</span>
                                    <DropdownPrimary
                                        value={servicioSolicitado.ssoEstado}
                                        options={opcionesEstadoServicio}
                                        onChange={(e) => setServicioSolicitado({ ...servicioSolicitado, ssoEstado: e.value })}
                                    />
                                </div>
                                <div>
                                    <ButtonPrimary label="Grabar Cambios" icon={<FaSave />} onClick={modificarCaracteristicaServicioSolicitado} className="w-full" />
                                </div>
                            </div>
                        </PanelPrimary> : null}

                </PanelPrimary> : null}

            {/* VISTA LISTA */}
            {viewMode === 'LIST' ? <PanelPrimary header="Registro de Solicitudes de Servicio de Agua">
                    <DataTablePrimary value={listaSolicitudes} paginator rows={10} emptyMessage="No hay registros">
                        <Column header="Solicitante" field="propietario.proNombreCompleto" />
                        <Column header="Predio" field="predio.preCodigoCatastral" />
                        <Column header="Fecha" field="socFechaSolicitud" body={(r) => r.socFechaSolicitud} />
                        <Column header="Estado" field="socEstadoSolicitud" />
                        <Column header="Acciones" body={(r) => (
                            <button onClick={() => editarRegistro(r)} className="text-green-600"><FaPencilAlt /></button>
                        )} style={{ textAlign: 'center' }} />
                    </DataTablePrimary>
                </PanelPrimary> : null}
        </div>
    );
}
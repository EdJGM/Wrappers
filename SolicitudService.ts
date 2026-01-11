import { SGB_API_CONTEXT } from "~/constants/routes";
import { api } from "~/services/api";
import { ApiResponse } from "~/types/ApiResponse";
import { PredioGenericoRsDTO } from "~/types/general/PredioGenericoRsDTO";
import { SerCategoriaRsDTO } from "~/types/general/SerCategoriaRsDTO";
import { SerCuentaRsDTO } from "~/types/general/SerCuentaRsDTO";

import { CatalogoRsDTO, PropietarioBarrioRsDTO, ServicioSolicitadoRsDTO, SolicitudCompositeRqDTO, SolicitudContratoRsDTO } from "../types/SolicitudTypes";


const SERVICE_PATH = SGB_API_CONTEXT + "/solicitud-contrato";

// =================== AUTOCOMPLETAR PREDIOS ===================

export const autocompletarBusquedaPredio = async (search: string): Promise<PredioGenericoRsDTO[]> => {
    const res = await api.get<ApiResponse<PredioGenericoRsDTO[]>>(
        `${SERVICE_PATH}/predios/autocompletar`,
        { params: { search } }
    );
    return res.data as unknown as PredioGenericoRsDTO[];
};

// =================== AUTOCOMPLETAR CUENTAS AGUA ===================

export const autocompletarCuentaAgua = async (search: string): Promise<SerCuentaRsDTO[]> => {
    const res = await api.get<ApiResponse<SerCuentaRsDTO[]>>(
        `${SERVICE_PATH}/cuentas/autocompletar`,
        { params: { search } }
    );
    return res.data as unknown as SerCuentaRsDTO[];
};

// =================== BUSCAR PROPIETARIO Y BARRIO ===================

export const buscarPropietario = async (identificacion: string, predioId?: number): Promise<PropietarioBarrioRsDTO> => {
    const params: Record<string, string | number> = { identificacion };
    if (predioId) {
        params.predioId = predioId;
    }

    const res = await api.get<ApiResponse<PropietarioBarrioRsDTO>>(
        `${SERVICE_PATH}/propietario/buscar`,
        { params }
    );
    return res.data as unknown as PropietarioBarrioRsDTO;
};

// =================== LISTAR CATEGORÍAS ===================

export const listaCategorias = async (): Promise<SerCategoriaRsDTO[]> => {
    const res = await api.get<ApiResponse<SerCategoriaRsDTO[]>>(
        `${SERVICE_PATH}/categorias`
    );
    return res.data as unknown as SerCategoriaRsDTO[];
};

// =================== GENERAR SERVICIOS POR DEFECTO ===================

export const generarServiciosPorDefecto = async (tipoPedido: number): Promise<ServicioSolicitadoRsDTO[]> => {
    const res = await api.get<ApiResponse<ServicioSolicitadoRsDTO[]>>(
        `${SERVICE_PATH}/servicios-defecto/${tipoPedido}`
    );
    return res.data as unknown as ServicioSolicitadoRsDTO[];
};

// =================== LISTAR SOLICITUDES (TABLA PRINCIPAL) ===================

export const listarSolicitudes = async (estados?: string): Promise<SolicitudContratoRsDTO[]> => {
    const params: Record<string, string> = {};
    if (estados) {
        params.estados = estados;
    }

    const res = await api.get<ApiResponse<SolicitudContratoRsDTO[]>>(
        `${SERVICE_PATH}/listar`,
        { params }
    );
    return res.data as unknown as SolicitudContratoRsDTO[];
};

// =================== OBTENER SOLICITUD POR ID (EDICIÓN) ===================

export const obtenerPorId = async (socId: number): Promise<SolicitudContratoRsDTO> => {
    const res = await api.get<ApiResponse<SolicitudContratoRsDTO>>(
        `${SERVICE_PATH}/${socId}`
    );
    return res.data as unknown as SolicitudContratoRsDTO;
};

export const cargarCaracteristicasDelServicioPublico = async (servicioId: number): Promise<CatalogoRsDTO[]> => {
    const res = await api.get<ApiResponse<CatalogoRsDTO[]>>(
        `${SERVICE_PATH}/servicios/${servicioId}/caracteristicas`
    );
    return res.data as unknown as CatalogoRsDTO[];
};

// =================== GUARDAR SOLICITUD (CREAR/MODIFICAR) ===================

export const guardarSolicitud = async (payload: SolicitudCompositeRqDTO): Promise<SolicitudContratoRsDTO> => {
    const res = await api.post<ApiResponse<SolicitudContratoRsDTO>>(
        `${SERVICE_PATH}/guardar`,
        payload
    );
    return res.data as unknown as SolicitudContratoRsDTO;
};

// =================== UTIL ===================
export const toBackendDateString = (date: Date | string | undefined): string | undefined => {
    if (!date) return undefined;
    const d = typeof date === 'string' ? new Date(date) : date;
    return d.toISOString().slice(0, 19);
};
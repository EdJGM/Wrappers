import { CatBarrioRsDTO } from "~/types/general/CatBarrioRsDTO";
import { CatPredioRsDTO } from "~/types/general/CatPredioRsDTO";
import { GesPropietarioRsDTO } from "~/types/general/GesPropietarioRsDTO";
import { SerCategoriaRsDTO } from "~/types/general/SerCategoriaRsDTO";
import { SerCuentaRsDTO } from "~/types/general/SerCuentaRsDTO";
import { ServicioPublicoRsDTO } from "~/types/general/ServicioPublicoRsDTO";

export interface SolicitudContratoRsDTO {
    socId: number;
    socFechaSolicitud: string; // Date se recibe como string ISO
    socEstadoSolicitud: string; // 'Registrado', 'Aprobada'
    socNumeroSolicitud?: string;
    socPedido?: string;
    socRepresentante?: string;
    socFechaFormulario?: string;
    socServPedido?: number; // 1=Agua, 2=Alcantarillado, 3=Ambos

    // Campos editables cargados
    socInformeTecnico?: string;
    socResponsableFactibilidad?: string;
    socExistenciaRed?: string;

    // Relaciones completas
    propietario?: GesPropietarioRsDTO;
    predio?: CatPredioRsDTO;
    cuenta?: SerCuentaRsDTO;
    categoria?: SerCategoriaRsDTO;
}

export interface CatalogoRsDTO {
    catId: number;
    catNombre: string;
    catTag?: string;
}

export interface ServicioSolicitadoRsDTO {
    ssoId: number;
    ssoEstado: number; // 1=Activo, 0=Inactivo
    ssoEstadoDescripcion: string;
    servicioPublico: ServicioPublicoRsDTO;
    opcDiametro?: CatalogoRsDTO;
}

// DTO Compuesto para la búsqueda de propietario + barrio
export interface PropietarioBarrioRsDTO {
    propietario: GesPropietarioRsDTO;
    barrio: CatBarrioRsDTO;
    existe: boolean;
}

// --- REQUEST DTOs (Para guardar/editar) ---

export interface SolicitudContratoRqDTO {
    socId?: number; // Null si es nuevo
    socFechaSolicitud?: Date; // O string
    socEstadoSolicitud?: string; // char 'R'
    socInformeTecnico?: string;
    socResponsableFactibilidad?: string;
    socExistenciaRed?: string;
    socNumeroContrato?: string;
    socFechaContrato?: Date;
    socNumeroCuotas?: number;
    socValorContado?: number;
    socValorCredito?: number;
    socCuotaValor?: number;
    socValorDescuento?: number;
    socValorContrato?: number;
    socServPedido?: number;
    socFormaPago?: number;
    socPedido?: string;
    socRepresentante?: string;
    socFechaFormulario?: Date;
    socNumeroSolicitud?: string;
    socRegistradoPor?: string;

    // Foreign Keys (IDs)
    cuentaId?: number;
    propietarioId?: number;
    predioId?: number;
    categoriaId?: number;
}

export interface ServicioSolicitadoRqDTO {
    ssoId?: number; // Null si es nuevo
    ssoEstado: number;
    servicioPublicoId: number;
    solicitudContratoId?: number;
    opcDiametroId?: number;
    opcDiametroNombre?: string;
}

// --- COMPOSITE DTO (Payload final para el Controller) ---

export interface SolicitudCompositeRqDTO {
    solicitud: SolicitudContratoRqDTO;
    servicios: ServicioSolicitadoRqDTO[];
}
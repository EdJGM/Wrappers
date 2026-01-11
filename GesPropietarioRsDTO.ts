export interface GesPropietarioRsDTO {
    proId: number;
    proNumIdentificacion: string;
    proNombre: string;
    proApellido: string;
    proNombreCompleto: string; // Campo computado
    proDireccionDomicilio?: string;
    proTelefono1?: string;
    proTelefono2?: string;
    proCorreoElectronico?: string;
}
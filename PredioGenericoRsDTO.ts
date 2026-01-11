//DTO especial del legacy para el autocompletado
export interface PredioGenericoRsDTO {
    preId: number;
    codigoCatastral: string;
    codigoAnterior?: string;
    direccionPrincipal?: string;
    areaTotalTerreno?: number;
    areaTotalConstruccion?: number;
    cedulaPropietario?: string;
    tipoPropiedad?: number; // 1=Urbano, 0=Rural (según legacy)
    nombrePredio?: string;
}
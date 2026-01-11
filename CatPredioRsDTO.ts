export interface CatPredioRsDTO {
    preId: number;
    preCodigoCatastral: string;
    preCodigoAnterior?: string;
    preDireccionPrincipal?: string;
    preAreaTotalTer?: number;
    preAreaTotalConst?: number;
    nombrePredio?: string; // Campo computado 
}
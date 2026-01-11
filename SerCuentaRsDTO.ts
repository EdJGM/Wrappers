import { GesPropietarioRsDTO } from "./GesPropietarioRsDTO";

export interface SerCuentaRsDTO {
    cueId: number;
    cueNumeroCta: string;
    cueClaveCatastral: string;
    medidorNumero?: string;
    propietario?: GesPropietarioRsDTO;
}
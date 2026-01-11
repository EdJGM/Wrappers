package ec.com.intelectus.system.sgb.serviciosbasicos.utils;

import ec.com.intelectus.system.sgb.utilitario.util.AbstracFacade;
import ec.com.intelectus.system.sgd.colmena.serviciosbasicos.SerCuenta;
import ec.com.intelectus.system.sgd.colmena.serviciosbasicos.SerSolicitudContrato;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Cuenta extends AbstracFacade {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public List<Object> autoCuentaAguaTodas(String search) {
        List<String> listaCoincidencias = new ArrayList<>();
        String[] busqueda;
        busqueda = search.split(" ");
        List<Object> listado;

        try {
            if (search.trim().length() > 0) {
                //Eliminamos los caracters en blanco de la cadena de texto: search
                for (int i = 0; i < busqueda.length; i++) {
                    if (busqueda[i].trim().length() > 0) {
                        listaCoincidencias.add(busqueda[i]);
                    }
                }

                String query = "select * from  servicios.ser_cuenta a left join  gestion.ges_propietario b on  a.pro_id = b.pro_id where ";
                int i = 1;
                for (String obj : listaCoincidencias) {
                    //vemos si tiene un solo elemento
                    if (listaCoincidencias.size() == 1) {

                        query += " ( "
                                + "   ( translate(UPPER(a.cue_numero_cta_anterior),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                + "or ( translate(UPPER(a.cue_numero_cta),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                + "or ( translate(UPPER(b.pro_apellido),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                + "or ( translate(UPPER(b.pro_nombre),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                + "or ( translate(UPPER(b.pro_num_identificacion),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                + ") ";
                    } else //realizamos el AND mientas no sea el ultimo
                        if (i < listaCoincidencias.size()) {
                            query += " ( "
                                    + "   ( translate(UPPER(a.cue_numero_cta_anterior),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                    + "or ( translate(UPPER(a.cue_numero_cta),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                    + "or ( translate(UPPER(b.pro_apellido),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                    + "or ( translate(UPPER(b.pro_nombre),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                    + "or ( translate(UPPER(b.pro_num_identificacion),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                    + ")  AND  ";
                        } else {
                            query += "  ( "
                                    + "   ( translate(UPPER(a.cue_numero_cta_anterior),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                    + "or ( translate(UPPER(a,cue_numero_cta),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                    + "or ( translate(UPPER(b.pro_apellido),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                    + "or ( translate(UPPER(b.pro_nombre),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                    + "or ( translate(UPPER(b.pro_num_identificacion),'ÁÉÍÓÚ','AEIOU') ~ translate(UPPER('" + obj + "'),'ÁÉÍÓÚ','AEIOU') ) "
                                    + ")  ";
                        }
                    i++;
                }
                query += " order by a.cue_numero_cta ";

                listado = nativeQueryList(SerCuenta.class, query);

                return listado;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<SerSolicitudContrato> listaSolicitudesEstadoEjb(String estados) {
        List<SerSolicitudContrato> lista = new ArrayList<>();
        try {
            List<String> estadosList = Arrays.asList(estados.split(","));
            String jpql = "SELECT P FROM SerSolicitudContrato P " +
                    "LEFT JOIN FETCH P.cuenta " +
                    "LEFT JOIN FETCH P.categoria " +
                    "LEFT JOIN FETCH P.propietario " +
                    "LEFT JOIN FETCH P.predio " +
                    "WHERE P.socEstadoSolicitud IN :estados " +
                    "ORDER BY P.socId DESC";
            Query q = getEntityManager().createQuery(jpql);
            q.setParameter("estados", estadosList);
            lista = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

}

package ec.com.intelectus.system.sgb.serviciosbasicos.utils;

import ec.com.intelectus.system.sgb.utilitario.util.AbstracFacade;
import ec.com.intelectus.system.sgd.colmena.catastro.CatBarrio;
import ec.com.intelectus.system.sgd.colmena.catastro.CatPredio;
import ec.com.intelectus.system.sgd.colmena.serviciosbasicos.SerServicioSolicitado;
import ec.com.intelectus.system.sgd.colmena.serviciosbasicos.SerSolicitudContrato;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServiciosBasicos extends AbstracFacade {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public List<CatBarrio> listaBarriosPredio(CatPredio predio) {
        try {
//            String sql = "Select b from CatCallePredio P left join fetch P.catCalleBarrio a left join fetch a.catBarrio b where P.catPredio.preId=:preId";
            String sql = "  select * from catastro.cat_barrio where bar_id in (select  b.bar_id from catastro.cat_calle_predio a, catastro.cat_calle_barrio b where a.cab_id = b.cab_id and  a.pre_id = :preId )";
            //System.out.println("*** listaBarriosPredio *************** " + sql);
//            Query q = getEntityManager().createQuery(sql).setParameter("preId", predio.getPreId());
            Query q = getEntityManager().createNativeQuery(sql, CatBarrio.class);
            q.setParameter("preId", predio.getPreId());
            List<CatBarrio> objs = q.getResultList();
            return objs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}

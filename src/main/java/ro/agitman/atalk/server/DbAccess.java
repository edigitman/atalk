package ro.agitman.atalk.server;

import ro.agitman.atalk.model.TextMsg;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by edi on 3/23/2016.
 */
public class DbAccess {

    private EntityManagerFactory factory;
    private static DbAccess instance = new DbAccess();

    public DbAccess() {
        try {
            this.factory = Persistence.createEntityManagerFactory("jpaUnit");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save(TextMsg msg) {
        msg.setInsertDate(new Date());
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        em.persist(msg);

        em.getTransaction().commit();
        em.close();
    }

    public List<TextMsg> getToday() {
        List<TextMsg> result;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);

        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        TypedQuery<TextMsg> query = em.createNamedQuery("TextMsg.allOfToday", TextMsg.class);
        query.setParameter("yesterday", calendar.getTime(), TemporalType.DATE);
        result = query.getResultList();

        em.getTransaction().commit();
        em.close();

        return result;
    }

    public static DbAccess getInst() {
        return instance;
    }
}

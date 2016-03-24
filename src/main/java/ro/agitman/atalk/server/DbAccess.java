package ro.agitman.atalk.server;

import ro.agitman.atalk.model.TextMsg;

import javax.persistence.*;
import java.text.SimpleDateFormat;
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
        System.out.println("Called save: " + msg);
        msg.setInsertDate(new Date());
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        em.persist(msg);

        em.getTransaction().commit();
        em.close();

        System.out.println("Called save successfully: " + msg);
    }

    public List<TextMsg> getToday() {
        System.out.println("Called getToday");
        List<TextMsg> result;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);

        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        TypedQuery<TextMsg> query = em.createNamedQuery("TextMsg.allOfToday", TextMsg.class);
        query.setParameter("yesterday", calendar.getTime(), TemporalType.DATE);
        result = query.getResultList();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm:ss");
        System.out.println("yesterday: " + sdf.format(calendar.getTime()));

        em.getTransaction().commit();
        em.close();
        System.out.println("Called getToday successfully with " + result.size());
        return result;
    }

    public static DbAccess getInst() {
        return instance;
    }
}

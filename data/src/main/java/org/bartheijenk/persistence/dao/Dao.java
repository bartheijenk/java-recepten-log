package org.bartheijenk.persistence.dao;

import lombok.extern.log4j.Log4j2;
import org.bartheijenk.persistence.entity.Identifiable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

@Log4j2
public abstract class Dao<E extends Identifiable<K>, K> {

    @PersistenceContext(unitName = "MySQL-recipelog")
    protected EntityManager em;

    public Dao(EntityManager em) {
        this.em = em;
    }

    public Dao() {
    }

    public List<E> findAll() {
        return em.createNamedQuery(typeSimple() + ".findAll", E()).getResultList();
    }

    public Optional<E> find(K id) {
        return Optional.ofNullable(em.find(E(), id));
    }

    public void save(E e) {
        try {
            em.getTransaction().begin();
            em.persist(e);
            em.getTransaction().commit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public E update(E e) {
        em.getTransaction().begin();
        E mergedE = em.merge(e);
        em.getTransaction().commit();
        return mergedE;
    }

    public void remove(E e) {
        em.getTransaction().begin();
        em.remove(e.getId());
        em.getTransaction().commit();
    }

    public Boolean contains(E e) {
        return em.contains(e);
    }

    @SuppressWarnings("unchecked")
    private Class<E> E() {
        ParameterizedType thisDaoClass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<E>) thisDaoClass.getActualTypeArguments()[0];
    }

    private String typeSimple() {
        return E().getSimpleName();
    }
}

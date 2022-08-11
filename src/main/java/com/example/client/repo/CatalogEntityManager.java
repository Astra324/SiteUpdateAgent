package com.example.client.repo;


import com.example.client.model.CatalogItem;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;

@Repository
public class CatalogEntityManager {
    @PersistenceContext
    private EntityManager entityManager;

    public ArrayList<CatalogItem> selectOrdered(){
        return (ArrayList<CatalogItem>) entityManager.createQuery("SELECT p FROM CatalogItem p ORDER BY p.id desc", CatalogItem.class)
                .getResultList();
    }
    public Integer ifExists(String href){
        ArrayList<CatalogItem> result = new ArrayList<>();
        result = (ArrayList<CatalogItem>) entityManager
                  .createQuery("SELECT p  FROM CatalogItem p WHERE p.href = \'" + href + "\'", CatalogItem.class).getResultList();
        return result.size();
    }
}

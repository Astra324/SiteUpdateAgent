package com.example.client.repo;


import com.example.client.model.CatalogItem;
import org.hibernate.annotations.NamedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface CatalogRepository extends CrudRepository<CatalogItem, Long> {


//    @Query("SELECT p FROM CatalogItem p ORDER BY p.id desc")
//    ArrayList<CatalogItem> selectOrdered();

}

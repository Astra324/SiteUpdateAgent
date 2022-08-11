package com.example.client;

import com.example.client.services.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Controller
@EnableScheduling
public class StartSchedules {
    @Autowired
    CatalogService catalogService;

    @Scheduled(fixedRate = 3600000)
    public void dbUpdate(){
        catalogService.updateCatalog();
    }




}

package com.example.client.controllers;

import com.example.client.model.CatalogItem;
import com.example.client.services.CatalogService;
import com.example.client.services.SiteService;
import com.example.client.site_engine.SiteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MainController {
    @Autowired
    SiteService siteService;
    @Autowired
    CatalogService catalogService;

    @GetMapping("/update")
    //@ResponseBody
    public String update(Model model) {
        //
        catalogService.updateCatalog();
        List<CatalogItem> resultList = catalogService.getList(0, 30);
        model.addAttribute("data", resultList);
        return "catalog_view";
    }


}

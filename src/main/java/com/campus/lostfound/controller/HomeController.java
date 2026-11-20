package com.campus.lostfound.controller;

import com.campus.lostfound.service.CategoryService;
import com.campus.lostfound.service.ItemReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ItemReportService itemReportService;
    private final CategoryService categoryService;

    public HomeController(ItemReportService itemReportService, CategoryService categoryService) {
        this.itemReportService = itemReportService;
        this.categoryService = categoryService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("stats", itemReportService.getStatistics());
        model.addAttribute("recentItems", itemReportService.getRecentReports(6));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "index";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error";
    }
}

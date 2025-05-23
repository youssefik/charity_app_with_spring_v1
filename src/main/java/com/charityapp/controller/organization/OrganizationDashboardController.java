package com.charityapp.controller.organization;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/organization/dashboard")
public class OrganizationDashboardController {
    
    @GetMapping
    public String showDashboard() {
        return "dashboard/organization";
    }
}

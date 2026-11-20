package com.campus.lostfound.controller;

import com.campus.lostfound.entity.*;
import com.campus.lostfound.service.ClaimRequestService;
import com.campus.lostfound.service.ItemReportService;
import com.campus.lostfound.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final UserService userService;
    private final ItemReportService itemReportService;
    private final ClaimRequestService claimRequestService;

    public StudentController(UserService userService,
                             ItemReportService itemReportService,
                             ClaimRequestService claimRequestService) {
        this.userService = userService;
        this.itemReportService = itemReportService;
        this.claimRequestService = claimRequestService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User student = userService.findByEmail(principal.getName());
        List<ItemReport> myReports = itemReportService.getReportsByUser(student);
        List<ClaimRequest> myClaims = claimRequestService.getClaimsByUser(student);

        long openReportsCount = myReports.stream()
                .filter(r -> r.getStatus() == ItemStatus.OPEN)
                .count();

        long resolvedReportsCount = myReports.stream()
                .filter(r -> r.getStatus() == ItemStatus.CLAIMED || r.getStatus() == ItemStatus.CLOSED)
                .count();

        long pendingClaimsCount = myClaims.stream()
                .filter(c -> c.getStatus() == ClaimStatus.PENDING)
                .count();

        long approvedClaimsCount = myClaims.stream()
                .filter(c -> c.getStatus() == ClaimStatus.APPROVED)
                .count();

        model.addAttribute("student", student);
        model.addAttribute("totalReports", myReports.size());
        model.addAttribute("openReports", openReportsCount);
        model.addAttribute("resolvedReports", resolvedReportsCount);
        model.addAttribute("totalClaims", myClaims.size());
        model.addAttribute("pendingClaims", pendingClaimsCount);
        model.addAttribute("approvedClaims", approvedClaimsCount);

        // Limit to 4 recent
        model.addAttribute("recentReports", myReports.stream().limit(4).toList());
        model.addAttribute("recentClaims", myClaims.stream().limit(4).toList());

        return "student/dashboard";
    }

    @GetMapping("/my-reports")
    public String myReports(Model model, Principal principal) {
        User student = userService.findByEmail(principal.getName());
        List<ItemReport> reports = itemReportService.getReportsByUser(student);
        model.addAttribute("reports", reports);
        return "student/my-reports";
    }

    @GetMapping("/my-claims")
    public String myClaims(Model model, Principal principal) {
        User student = userService.findByEmail(principal.getName());
        List<ClaimRequest> claims = claimRequestService.getClaimsByUser(student);
        model.addAttribute("claims", claims);
        return "student/my-claims";
    }
}

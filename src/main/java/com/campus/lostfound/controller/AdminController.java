package com.campus.lostfound.controller;

import com.campus.lostfound.entity.*;
import com.campus.lostfound.service.CategoryService;
import com.campus.lostfound.service.ClaimRequestService;
import com.campus.lostfound.service.ItemReportService;
import com.campus.lostfound.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ItemReportService itemReportService;
    private final ClaimRequestService claimRequestService;
    private final CategoryService categoryService;

    public AdminController(UserService userService,
                           ItemReportService itemReportService,
                           ClaimRequestService claimRequestService,
                           CategoryService categoryService) {
        this.userService = userService;
        this.itemReportService = itemReportService;
        this.claimRequestService = claimRequestService;
        this.categoryService = categoryService;
    }

    // Admin Dashboard with summary stats
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User admin = userService.findByEmail(principal.getName());
        model.addAttribute("admin", admin);
        model.addAttribute("stats", itemReportService.getStatistics());
        model.addAttribute("totalUsers", userService.getTotalUsersCount());
        model.addAttribute("claimStats", claimRequestService.getClaimStatistics());
        model.addAttribute("recentReports", itemReportService.getRecentReports(5));
        model.addAttribute("recentClaims", claimRequestService.getRecentClaims(5));
        return "admin/dashboard";
    }

    // Reports Management
    @GetMapping("/reports")
    public String manageReports(@RequestParam(required = false) ReportType type,
                                @RequestParam(required = false) Long categoryId,
                                @RequestParam(required = false) ItemStatus status,
                                @RequestParam(required = false) String location,
                                @RequestParam(required = false) String keyword,
                                Model model) {
        List<ItemReport> reports = itemReportService.searchAndFilter(type, categoryId, status, location, keyword);
        model.addAttribute("reports", reports);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedLocation", location);
        model.addAttribute("keyword", keyword);
        return "admin/reports";
    }

    // Update Report Status
    @PostMapping("/reports/{id}/status")
    public String updateReportStatus(@PathVariable Long id,
                                     @RequestParam ItemStatus status,
                                     RedirectAttributes redirectAttributes) {
        try {
            itemReportService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Report status updated to " + status + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update status: " + ex.getMessage());
        }
        return "redirect:/admin/reports";
    }

    // Delete Report
    @PostMapping("/reports/{id}/delete")
    public String deleteReportByAdmin(@PathVariable Long id,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes) {
        try {
            User admin = userService.findByEmail(principal.getName());
            itemReportService.deleteReport(id, admin, true);
            redirectAttributes.addFlashAttribute("successMessage", "Report deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting report: " + ex.getMessage());
        }
        return "redirect:/admin/reports";
    }

    // Claims Management
    @GetMapping("/claims")
    public String manageClaims(@RequestParam(required = false) ClaimStatus status, Model model) {
        List<ClaimRequest> allClaims = claimRequestService.getAllClaims();
        if (status != null) {
            allClaims = allClaims.stream()
                    .filter(c -> c.getStatus() == status)
                    .toList();
        }
        model.addAttribute("claims", allClaims);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("claimStats", claimRequestService.getClaimStatistics());
        return "admin/claims";
    }

    // Review & Update Claim Status
    @PostMapping("/claims/{id}/update")
    public String updateClaimStatus(@PathVariable Long id,
                                    @RequestParam ClaimStatus status,
                                    @RequestParam(required = false) String adminRemarks,
                                    RedirectAttributes redirectAttributes) {
        try {
            claimRequestService.updateClaimStatus(id, status, adminRemarks);
            redirectAttributes.addFlashAttribute("successMessage", "Claim request updated to " + status + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update claim: " + ex.getMessage());
        }
        return "redirect:/admin/claims";
    }

    // Users Management
    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    // Toggle User Status (Enable/Disable)
    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "User account status updated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user: " + ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Categories Management
    @GetMapping("/categories")
    public String manageCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        if (!model.containsAttribute("category")) {
            model.addAttribute("category", new Category());
        }
        return "admin/categories";
    }

    // Add New Category
    @PostMapping("/categories/add")
    public String addCategory(@Valid @ModelAttribute("category") Category category,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Category name cannot be empty.");
            return "redirect:/admin/categories";
        }

        try {
            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("successMessage", "Category '" + category.getName() + "' created successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // Delete Category
    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete category: It might be referenced by existing item reports.");
        }
        return "redirect:/admin/categories";
    }
}

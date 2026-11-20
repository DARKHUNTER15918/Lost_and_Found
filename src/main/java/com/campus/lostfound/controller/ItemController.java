package com.campus.lostfound.controller;

import com.campus.lostfound.dto.ClaimRequestDto;
import com.campus.lostfound.dto.ItemReportDto;
import com.campus.lostfound.entity.*;
import com.campus.lostfound.service.CategoryService;
import com.campus.lostfound.service.ClaimRequestService;
import com.campus.lostfound.service.ItemReportService;
import com.campus.lostfound.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemReportService itemReportService;
    private final CategoryService categoryService;
    private final ClaimRequestService claimRequestService;
    private final UserService userService;

    public ItemController(ItemReportService itemReportService,
                          CategoryService categoryService,
                          ClaimRequestService claimRequestService,
                          UserService userService) {
        this.itemReportService = itemReportService;
        this.categoryService = categoryService;
        this.claimRequestService = claimRequestService;
        this.userService = userService;
    }

    // Browse and search all items
    @GetMapping
    public String browseItems(@RequestParam(required = false) ReportType type,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false) ItemStatus status,
                              @RequestParam(required = false) String location,
                              @RequestParam(required = false) String keyword,
                              Model model) {
        List<ItemReport> items = itemReportService.searchAndFilter(type, categoryId, status, location, keyword);
        model.addAttribute("items", items);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedLocation", location);
        model.addAttribute("keyword", keyword);
        return "items/browse";
    }

    // View Item Details
    @GetMapping("/{id}")
    public String viewItemDetails(@PathVariable Long id, Model model, Principal principal) {
        ItemReport item = itemReportService.getReportById(id);
        model.addAttribute("item", item);

        boolean isOwner = false;
        boolean hasClaimed = false;

        if (principal != null) {
            User currentUser = userService.findByEmail(principal.getName());
            if (currentUser != null) {
                isOwner = item.getPostedBy().getId().equals(currentUser.getId()) || currentUser.getRole() == Role.ROLE_ADMIN;
                hasClaimed = claimRequestService.hasUserClaimedItem(item, currentUser);

                // If owner or admin, pass claims list
                if (isOwner) {
                    model.addAttribute("claims", claimRequestService.getClaimsByItem(item));
                }
            }
        }

        model.addAttribute("isOwner", isOwner);
        model.addAttribute("hasClaimed", hasClaimed);
        model.addAttribute("claimDto", new ClaimRequestDto(item.getId()));

        return "items/details";
    }

    // Report Lost Item - GET
    @GetMapping("/report-lost")
    public String showReportLostForm(Model model) {
        ItemReportDto dto = new ItemReportDto();
        dto.setReportType(ReportType.LOST);
        dto.setItemDate(LocalDate.now());

        model.addAttribute("itemDto", dto);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formType", "LOST");
        return "items/report-lost";
    }

    // Report Lost Item - POST
    @PostMapping("/report-lost")
    public String submitReportLost(@Valid @ModelAttribute("itemDto") ItemReportDto itemDto,
                                   BindingResult result,
                                   Principal principal,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        itemDto.setReportType(ReportType.LOST);
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("formType", "LOST");
            return "items/report-lost";
        }

        User currentUser = userService.findByEmail(principal.getName());
        ItemReport saved = itemReportService.createReport(itemDto, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Lost item report posted successfully!");
        return "redirect:/items/" + saved.getId();
    }

    // Report Found Item - GET
    @GetMapping("/report-found")
    public String showReportFoundForm(Model model) {
        ItemReportDto dto = new ItemReportDto();
        dto.setReportType(ReportType.FOUND);
        dto.setItemDate(LocalDate.now());

        model.addAttribute("itemDto", dto);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formType", "FOUND");
        return "items/report-found";
    }

    // Report Found Item - POST
    @PostMapping("/report-found")
    public String submitReportFound(@Valid @ModelAttribute("itemDto") ItemReportDto itemDto,
                                    BindingResult result,
                                    Principal principal,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        itemDto.setReportType(ReportType.FOUND);
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("formType", "FOUND");
            return "items/report-found";
        }

        User currentUser = userService.findByEmail(principal.getName());
        ItemReport saved = itemReportService.createReport(itemDto, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Found item report posted successfully!");
        return "redirect:/items/" + saved.getId();
    }

    // Edit Item - GET
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal, Authentication authentication) {
        ItemReport item = itemReportService.getReportById(id);
        User currentUser = userService.findByEmail(principal.getName());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !item.getPostedBy().getId().equals(currentUser.getId())) {
            return "redirect:/access-denied";
        }

        ItemReportDto dto = new ItemReportDto();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setCategoryId(item.getCategory().getId());
        dto.setDescription(item.getDescription());
        dto.setLocation(item.getLocation());
        dto.setItemDate(item.getItemDate());
        dto.setReportType(item.getReportType());
        dto.setStatus(item.getStatus());
        dto.setExistingImageUrl(item.getImageUrl());

        model.addAttribute("itemDto", dto);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "items/edit";
    }

    // Edit Item - POST
    @PostMapping("/edit/{id}")
    public String updateItem(@PathVariable Long id,
                             @Valid @ModelAttribute("itemDto") ItemReportDto itemDto,
                             BindingResult result,
                             Principal principal,
                             Authentication authentication,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "items/edit";
        }

        User currentUser = userService.findByEmail(principal.getName());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        try {
            itemReportService.updateReport(id, itemDto, currentUser, isAdmin);
            redirectAttributes.addFlashAttribute("successMessage", "Report updated successfully!");
            return "redirect:/items/" + id;
        } catch (SecurityException ex) {
            return "redirect:/access-denied";
        }
    }

    // Delete Item
    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id,
                             Principal principal,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(principal.getName());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        try {
            itemReportService.deleteReport(id, currentUser, isAdmin);
            redirectAttributes.addFlashAttribute("successMessage", "Report deleted successfully.");
            return isAdmin ? "redirect:/admin/reports" : "redirect:/student/my-reports";
        } catch (SecurityException ex) {
            return "redirect:/access-denied";
        }
    }

    // Submit Claim Request
    @PostMapping("/claim/submit")
    public String submitClaim(@Valid @ModelAttribute("claimDto") ClaimRequestDto claimDto,
                              BindingResult result,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "All claim fields are required. Please provide detailed proof.");
            return "redirect:/items/" + claimDto.getItemReportId();
        }

        User currentUser = userService.findByEmail(principal.getName());
        try {
            claimRequestService.submitClaim(claimDto, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Your claim request has been submitted for review! You can track its status in 'My Claims'.");
            return "redirect:/student/my-claims";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/items/" + claimDto.getItemReportId();
        }
    }
}

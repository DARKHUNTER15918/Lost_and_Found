package com.campus.lostfound.service.impl;

import com.campus.lostfound.dto.ItemReportDto;
import com.campus.lostfound.entity.*;
import com.campus.lostfound.repository.CategoryRepository;
import com.campus.lostfound.repository.ItemReportRepository;
import com.campus.lostfound.service.FileStorageService;
import com.campus.lostfound.service.ItemReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemReportServiceImpl implements ItemReportService {

    private final ItemReportRepository itemReportRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public ItemReportServiceImpl(ItemReportRepository itemReportRepository,
                                 CategoryRepository categoryRepository,
                                 FileStorageService fileStorageService) {
        this.itemReportRepository = itemReportRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ItemReport createReport(ItemReportDto dto, User user) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        ItemReport report = new ItemReport();
        report.setTitle(dto.getTitle().trim());
        report.setCategory(category);
        report.setDescription(dto.getDescription().trim());
        report.setLocation(dto.getLocation().trim());
        report.setItemDate(dto.getItemDate());
        report.setReportType(dto.getReportType());
        report.setStatus(ItemStatus.OPEN);
        report.setPostedBy(user);

        // Handle image upload if provided
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            String imageUrl = fileStorageService.storeFile(dto.getImageFile());
            report.setImageUrl(imageUrl);
        }

        return itemReportRepository.save(report);
    }

    @Override
    public ItemReport updateReport(Long id, ItemReportDto dto, User user, boolean isAdmin) {
        ItemReport report = getReportById(id);

        // Security check: Only the owner or an admin can update
        if (!isAdmin && !report.getPostedBy().getId().equals(user.getId())) {
            throw new SecurityException("You do not have permission to edit this report.");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        report.setTitle(dto.getTitle().trim());
        report.setCategory(category);
        report.setDescription(dto.getDescription().trim());
        report.setLocation(dto.getLocation().trim());
        report.setItemDate(dto.getItemDate());
        
        if (dto.getStatus() != null) {
            report.setStatus(dto.getStatus());
        }

        // Handle image replacement if a new image was uploaded
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            // Delete old file if present
            if (report.getImageUrl() != null) {
                fileStorageService.deleteFile(report.getImageUrl());
            }
            String newImageUrl = fileStorageService.storeFile(dto.getImageFile());
            report.setImageUrl(newImageUrl);
        }

        return itemReportRepository.save(report);
    }

    @Override
    public void deleteReport(Long id, User user, boolean isAdmin) {
        ItemReport report = getReportById(id);

        // Security check: Only the owner or an admin can delete
        if (!isAdmin && !report.getPostedBy().getId().equals(user.getId())) {
            throw new SecurityException("You do not have permission to delete this report.");
        }

        // Delete associated image file
        if (report.getImageUrl() != null) {
            fileStorageService.deleteFile(report.getImageUrl());
        }

        itemReportRepository.delete(report);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemReport getReportById(Long id) {
        return itemReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item report not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemReport> getAllReports() {
        return itemReportRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemReport> getReportsByUser(User user) {
        return itemReportRepository.findByPostedByOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemReport> getRecentReports(int limit) {
        return itemReportRepository.findTop6ByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemReport> searchAndFilter(ReportType reportType, Long categoryId, ItemStatus status, String location, String keyword) {
        return itemReportRepository.searchAndFilterReports(reportType, categoryId, status, location, keyword);
    }

    @Override
    public void updateStatus(Long id, ItemStatus status) {
        ItemReport report = getReportById(id);
        report.setStatus(status);
        itemReportRepository.save(report);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", itemReportRepository.count());
        stats.put("lost", itemReportRepository.countByReportType(ReportType.LOST));
        stats.put("found", itemReportRepository.countByReportType(ReportType.FOUND));
        stats.put("open", itemReportRepository.countByStatus(ItemStatus.OPEN));
        stats.put("claimed", itemReportRepository.countByStatus(ItemStatus.CLAIMED));
        stats.put("closed", itemReportRepository.countByStatus(ItemStatus.CLOSED));
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalReportsCount() {
        return itemReportRepository.count();
    }
}

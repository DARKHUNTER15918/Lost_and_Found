package com.campus.lostfound.service;

import com.campus.lostfound.dto.ItemReportDto;
import com.campus.lostfound.entity.ItemReport;
import com.campus.lostfound.entity.ItemStatus;
import com.campus.lostfound.entity.ReportType;
import com.campus.lostfound.entity.User;

import java.util.List;
import java.util.Map;

public interface ItemReportService {
    ItemReport createReport(ItemReportDto dto, User user);
    ItemReport updateReport(Long id, ItemReportDto dto, User user, boolean isAdmin);
    void deleteReport(Long id, User user, boolean isAdmin);
    ItemReport getReportById(Long id);
    List<ItemReport> getAllReports();
    List<ItemReport> getReportsByUser(User user);
    List<ItemReport> getRecentReports(int limit);
    List<ItemReport> searchAndFilter(ReportType reportType, Long categoryId, ItemStatus status, String location, String keyword);
    void updateStatus(Long id, ItemStatus status);
    Map<String, Long> getStatistics();
    long getTotalReportsCount();
}

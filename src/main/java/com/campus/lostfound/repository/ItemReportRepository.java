package com.campus.lostfound.repository;

import com.campus.lostfound.entity.ItemReport;
import com.campus.lostfound.entity.ItemStatus;
import com.campus.lostfound.entity.ReportType;
import com.campus.lostfound.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemReportRepository extends JpaRepository<ItemReport, Long> {

    List<ItemReport> findByPostedByOrderByCreatedAtDesc(User user);

    List<ItemReport> findTop6ByOrderByCreatedAtDesc();

    List<ItemReport> findTop5ByOrderByCreatedAtDesc();

    long countByReportType(ReportType reportType);

    long countByStatus(ItemStatus status);

    long countByPostedBy(User user);

    long countByPostedByAndStatus(User user, ItemStatus status);

    @Query("SELECT r FROM ItemReport r WHERE " +
           "(:reportType IS NULL OR r.reportType = :reportType) AND " +
           "(:categoryId IS NULL OR r.category.id = :categoryId) AND " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:location IS NULL OR :location = '' OR LOWER(r.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY r.createdAt DESC")
    List<ItemReport> searchAndFilterReports(
            @Param("reportType") ReportType reportType,
            @Param("categoryId") Long categoryId,
            @Param("status") ItemStatus status,
            @Param("location") String location,
            @Param("keyword") String keyword
    );
}

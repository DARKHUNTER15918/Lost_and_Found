package com.campus.lostfound.repository;

import com.campus.lostfound.entity.ClaimRequest;
import com.campus.lostfound.entity.ClaimStatus;
import com.campus.lostfound.entity.ItemReport;
import com.campus.lostfound.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRequestRepository extends JpaRepository<ClaimRequest, Long> {

    List<ClaimRequest> findByClaimerOrderByCreatedAtDesc(User claimer);

    List<ClaimRequest> findByItemReportOrderByCreatedAtDesc(ItemReport itemReport);

    List<ClaimRequest> findAllByOrderByCreatedAtDesc();

    List<ClaimRequest> findTop5ByOrderByCreatedAtDesc();

    long countByStatus(ClaimStatus status);

    long countByClaimer(User claimer);

    long countByClaimerAndStatus(User claimer, ClaimStatus status);

    boolean existsByItemReportAndClaimer(ItemReport itemReport, User claimer);
}

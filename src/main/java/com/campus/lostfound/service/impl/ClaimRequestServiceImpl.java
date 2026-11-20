package com.campus.lostfound.service.impl;

import com.campus.lostfound.dto.ClaimRequestDto;
import com.campus.lostfound.entity.*;
import com.campus.lostfound.repository.ClaimRequestRepository;
import com.campus.lostfound.repository.ItemReportRepository;
import com.campus.lostfound.service.ClaimRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ClaimRequestServiceImpl implements ClaimRequestService {

    private final ClaimRequestRepository claimRequestRepository;
    private final ItemReportRepository itemReportRepository;

    public ClaimRequestServiceImpl(ClaimRequestRepository claimRequestRepository,
                                   ItemReportRepository itemReportRepository) {
        this.claimRequestRepository = claimRequestRepository;
        this.itemReportRepository = itemReportRepository;
    }

    @Override
    public ClaimRequest submitClaim(ClaimRequestDto dto, User claimer) {
        ItemReport itemReport = itemReportRepository.findById(dto.getItemReportId())
                .orElseThrow(() -> new IllegalArgumentException("Item report not found with id: " + dto.getItemReportId()));

        // Verification checks
        if (itemReport.getPostedBy().getId().equals(claimer.getId())) {
            throw new IllegalArgumentException("You cannot submit a claim for an item you posted.");
        }

        if (itemReport.getStatus() == ItemStatus.CLAIMED || itemReport.getStatus() == ItemStatus.CLOSED) {
            throw new IllegalStateException("This item is already " + itemReport.getStatus() + " and cannot be claimed.");
        }

        if (claimRequestRepository.existsByItemReportAndClaimer(itemReport, claimer)) {
            throw new IllegalStateException("You have already submitted a claim request for this item.");
        }

        ClaimRequest claim = new ClaimRequest();
        claim.setItemReport(itemReport);
        claim.setClaimer(claimer);
        claim.setClaimDescription(dto.getClaimDescription().trim());
        claim.setProofDetails(dto.getProofDetails().trim());
        claim.setContactInfo(dto.getContactInfo().trim());
        claim.setStatus(ClaimStatus.PENDING);

        return claimRequestRepository.save(claim);
    }

    @Override
    public ClaimRequest updateClaimStatus(Long claimId, ClaimStatus status, String adminRemarks) {
        ClaimRequest claim = getClaimById(claimId);
        claim.setStatus(status);
        claim.setAdminRemarks(adminRemarks != null ? adminRemarks.trim() : null);

        // If approved, update the item report status to CLAIMED
        if (status == ClaimStatus.APPROVED) {
            ItemReport item = claim.getItemReport();
            item.setStatus(ItemStatus.CLAIMED);
            itemReportRepository.save(item);
        }

        return claimRequestRepository.save(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimRequest getClaimById(Long claimId) {
        return claimRequestRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim request not found with id: " + claimId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimRequest> getAllClaims() {
        return claimRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimRequest> getClaimsByUser(User user) {
        return claimRequestRepository.findByClaimerOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimRequest> getClaimsByItem(ItemReport itemReport) {
        return claimRequestRepository.findByItemReportOrderByCreatedAtDesc(itemReport);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimRequest> getRecentClaims(int limit) {
        return claimRequestRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserClaimedItem(ItemReport itemReport, User user) {
        return claimRequestRepository.existsByItemReportAndClaimer(itemReport, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getClaimStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", claimRequestRepository.count());
        stats.put("pending", claimRequestRepository.countByStatus(ClaimStatus.PENDING));
        stats.put("approved", claimRequestRepository.countByStatus(ClaimStatus.APPROVED));
        stats.put("rejected", claimRequestRepository.countByStatus(ClaimStatus.REJECTED));
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalClaimsCount() {
        return claimRequestRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getPendingClaimsCount() {
        return claimRequestRepository.countByStatus(ClaimStatus.PENDING);
    }
}

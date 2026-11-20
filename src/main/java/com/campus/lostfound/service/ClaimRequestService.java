package com.campus.lostfound.service;

import com.campus.lostfound.dto.ClaimRequestDto;
import com.campus.lostfound.entity.ClaimRequest;
import com.campus.lostfound.entity.ClaimStatus;
import com.campus.lostfound.entity.ItemReport;
import com.campus.lostfound.entity.User;

import java.util.List;
import java.util.Map;

public interface ClaimRequestService {
    ClaimRequest submitClaim(ClaimRequestDto dto, User claimer);
    ClaimRequest updateClaimStatus(Long claimId, ClaimStatus status, String adminRemarks);
    ClaimRequest getClaimById(Long claimId);
    List<ClaimRequest> getAllClaims();
    List<ClaimRequest> getClaimsByUser(User user);
    List<ClaimRequest> getClaimsByItem(ItemReport itemReport);
    List<ClaimRequest> getRecentClaims(int limit);
    boolean hasUserClaimedItem(ItemReport itemReport, User user);
    Map<String, Long> getClaimStatistics();
    long getTotalClaimsCount();
    long getPendingClaimsCount();
}

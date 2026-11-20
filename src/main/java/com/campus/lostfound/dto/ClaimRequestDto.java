package com.campus.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClaimRequestDto {

    @NotNull(message = "Item report ID is required")
    private Long itemReportId;

    @NotBlank(message = "Please describe why you are claiming this item")
    private String claimDescription;

    @NotBlank(message = "Please provide verifiable proof (marks, color details, serial number, wallpaper, etc.)")
    private String proofDetails;

    @NotBlank(message = "Please provide contact phone or email for verification")
    private String contactInfo;

    public ClaimRequestDto() {}

    public ClaimRequestDto(Long itemReportId) {
        this.itemReportId = itemReportId;
    }

    public Long getItemReportId() {
        return itemReportId;
    }

    public void setItemReportId(Long itemReportId) {
        this.itemReportId = itemReportId;
    }

    public String getClaimDescription() {
        return claimDescription;
    }

    public void setClaimDescription(String claimDescription) {
        this.claimDescription = claimDescription;
    }

    public String getProofDetails() {
        return proofDetails;
    }

    public void setProofDetails(String proofDetails) {
        this.proofDetails = proofDetails;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}

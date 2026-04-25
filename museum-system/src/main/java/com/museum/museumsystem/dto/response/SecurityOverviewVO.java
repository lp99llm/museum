package com.museum.museumsystem.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class SecurityOverviewVO {
    private String backupInterfaceStatus;
    private String encryptionStatus;
    private String scanReportStatus;
    private Boolean autoBackupEnabled;
    private List<BackupItemVO> backupItems;
    private List<EncryptionFieldVO> encryptionFields;
    private SecurityReportVO securityReport;

    @Data
    public static class BackupItemVO {
        private String name;
        private String status;
        private String desc;
    }

    @Data
    public static class EncryptionFieldVO {
        private String field;
        private String desc;
        private String status;
        private String statusType;
    }

    @Data
    public static class SecurityReportVO {
        private String interfaceStatus;
        private String nextStep;
        private String conclusion;
    }
}

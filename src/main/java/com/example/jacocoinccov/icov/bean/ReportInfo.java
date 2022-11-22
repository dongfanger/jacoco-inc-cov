package com.example.jacocoinccov.icov.bean;

import lombok.Data;

@Data
public class ReportInfo {
    public String gitPath;
    public String gitUsername;
    public String gitPassword;
    public int type;
    public String branchName;
    public String newBranchName;
    public String oldBranchName;
    public String newVersion;
    public String oldVersion;
}

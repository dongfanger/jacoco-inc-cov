package com.example.jacocoinccov.jacoco.service;

public interface DevOpsService {
    public String getDiffByVersion(String branchName, String newVersion, String oldVersion);
    public String getDiffByBranch(String newBranchName, String oldBranchName);
    public boolean clone(String localPath, String gitPath, String username, String password, String branchName, String version);
    public boolean compile(String pomPath, String compileCmd, String compileParam);
}

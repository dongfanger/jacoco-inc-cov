package com.example.jacocoinccov.icov.service;

import java.io.IOException;

public interface JacocoService {
    public String getDiffByVersion(String branchName, String newVersion, String oldVersion);
    public String getDiffByBranch(String newBranchName, String oldBranchName);
    public void clone(String localPath, String gitPath, String username, String password, String branchName, String version);
    public void compile(String pomPath, String compileCmd, String compileParam);
    public void dump(String dumpPath, String ip, int port) throws IOException;
    public void report(String dumpPath, String classFilesPath, String srcPath, String reportPath) throws IOException;
}
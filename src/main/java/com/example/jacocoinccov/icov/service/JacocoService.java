package com.example.jacocoinccov.icov.service;

import com.example.jacocoinccov.icov.bean.ReportInfo;

import java.io.IOException;

public interface JacocoService {
    public void clone(String localPath, String gitPath, String username, String password, String branchName, String version);
    public void compile(String pomPath, String compileCmd, String compileParam);
    public void dump(String dumpPath, String ip, int port) throws IOException;
    public void report(String dumpPath, String classFilesPath, String srcPath, String reportPath, ReportInfo reportInfo) throws IOException;
}

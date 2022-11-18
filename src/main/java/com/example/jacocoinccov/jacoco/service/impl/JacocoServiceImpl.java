package com.example.jacocoinccov.jacoco.service.impl;

import cn.hutool.core.io.FileUtil;
import com.example.jacocoinccov.jacoco.service.JacocoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.html.HTMLFormatter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collections;

@Service
@Slf4j
public class JacocoServiceImpl implements JacocoService {
    @Override
    public String getDiffByVersion(String branchName, String newVersion, String oldVersion) {
        return null;
    }

    @Override
    public String getDiffByBranch(String newBranchName, String oldBranchName) {
        return null;
    }

    @Override
    public void clone(String localPath, String gitPath, String username, String password, String branchName, String version) {
        localPath = FileUtil.getAbsolutePath(localPath);
        if (FileUtil.exist(localPath)) {
            FileUtil.del(localPath);
        }
        log.info("开始下载代码，本地路径为{}", localPath);
        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider(username, password);
        try {
            CloneCommand cloneCommand = Git.cloneRepository();
            Git git = cloneCommand.setURI(gitPath)
                    .setBranch(branchName)
                    .setDirectory(new File(localPath))
                    .setCredentialsProvider(usernamePasswordCredentialsProvider)
                    .call();
            git.close();

            CheckoutCommand checkoutCommand = git.checkout().setName(version);
            checkoutCommand.call();
            log.info("下载代码完成，分支：{}，版本号：{}", branchName, version);
        } catch (Exception e) {
            log.error("下载代码出错，{}", e.getMessage());
        }
    }

    @Override
    public void compile(String pomPath, String mavenHome, String compileParam) {
        log.info("开始编译");
        pomPath = FileUtil.getAbsolutePath(pomPath);
        log.info("pomPath:{}", pomPath);
        log.info("mavenHome:{}", mavenHome);
        log.info("compileParam:{}", compileParam);
        InvocationRequest invocationRequest = new DefaultInvocationRequest();
        invocationRequest.setPomFile(new File(pomPath));
        invocationRequest.setGoals(Collections.singletonList(compileParam));
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(mavenHome));
        InvocationResult result;
        try {
            result = invoker.execute(invocationRequest);
        } catch (MavenInvocationException e) {
            log.info("编译失败:{}", e.getMessage());
            return;
        }
        if ( result.getExitCode() != 0 || result.getExecutionException() != null ) {
            log.info("编译失败:{}", result.getExecutionException().getMessage());
        }
    }

    @Override
    public void dump(String dumpPath, String ip, int port) throws IOException {
        dumpPath = FileUtil.getAbsolutePath(dumpPath);
        if (!FileUtil.exist(dumpPath)) {
            FileUtil.mkdir(dumpPath);
        }
        FileOutputStream localFile = new FileOutputStream(dumpPath + File.separator + "jacoco.exec");
        ExecutionDataWriter localWriter = new ExecutionDataWriter(localFile);
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        Socket socket = new Socket();
        try {
            socket.connect(socketAddress, 10000);
            RemoteControlWriter writer = new RemoteControlWriter(socket.getOutputStream());
            RemoteControlReader reader = new RemoteControlReader(socket.getInputStream());
            reader.setSessionInfoVisitor(localWriter);
            reader.setExecutionDataVisitor(localWriter);
            log.info("开始dump:{} {}", ip, port);
            writer.visitDumpCommand(true, false);
            if (!reader.read()) {
                log.error("Socket closed unexpectedly");
                throw new IOException("Socket closed unexpectedly.");
            }
            log.info("dump完成:{} {}", ip, port);
        } catch (Exception e) {
            log.info("dump失败:{}", e.getMessage());
        } finally {
            socket.close();
            localFile.close();

        }
    }

    @Override
    public void report(String dumpPath, String classFilesPath, String srcPath, String reportPath) throws IOException {
        dumpPath = FileUtil.getAbsolutePath(dumpPath);
        classFilesPath = FileUtil.getAbsolutePath(classFilesPath);
        srcPath = FileUtil.getAbsolutePath(srcPath);
        reportPath = FileUtil.getAbsolutePath(reportPath);

        File execFile = new File(dumpPath + File.separator + "jacoco.exec");
        ExecFileLoader execFileLoader = new ExecFileLoader();
        execFileLoader.load(execFile);

        CoverageBuilder coverageBuilder = new CoverageBuilder();
        Analyzer analyzer = new Analyzer(execFileLoader.getExecutionDataStore(), coverageBuilder);
        analyzer.analyzeAll(new File(classFilesPath));
        String reportTile = "增量覆盖率报告";
        IBundleCoverage bundleCoverage = coverageBuilder.getBundle(reportTile);
        HTMLFormatter htmlFormatter = new HTMLFormatter();
        IReportVisitor iReportVisitor = htmlFormatter.createVisitor(new FileMultiReportOutput(new File(reportPath)));
        iReportVisitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(), execFileLoader.getExecutionDataStore().getContents());
        DirectorySourceFileLocator directorySourceFileLocator = new DirectorySourceFileLocator(new File(srcPath),"utf-8",4);
        iReportVisitor.visitBundle(bundleCoverage, directorySourceFileLocator);
        iReportVisitor.visitEnd();
    }
}

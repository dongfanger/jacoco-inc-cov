package com.example.jacocoinccov.jacoco.service.impl;

import cn.hutool.core.io.FileUtil;
import com.example.jacocoinccov.jacoco.service.DevOpsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;

@Service
@Slf4j
public class DevOpsServiceImpl implements DevOpsService {
    @Override
    public String getDiffByVersion(String branchName, String newVersion, String oldVersion) {
        return null;
    }

    @Override
    public String getDiffByBranch(String newBranchName, String oldBranchName) {
        return null;
    }

    @Override
    public boolean clone(String localPath, String gitPath, String username, String password, String branchName, String version) {
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
            return true;
        } catch (Exception e) {
            log.error("下载代码出错，{}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean compile(String pomPath, String mavenHome, String compileParam) {
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
            return false;
        }
        if ( result.getExitCode() != 0 || result.getExecutionException() != null ) {
            log.info("编译失败:{}", result.getExecutionException().getMessage());
            return false;
        }
        return true;
    }
}

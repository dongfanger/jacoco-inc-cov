package com.example.jacocoinccov.icov.service;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import com.example.jacocoinccov.icov.bean.ReportInfo;
import com.example.jacocoinccov.icov.enums.ReportTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class JacocoServiceTest {
    private static final String clonePath = "/Users/wanggang424/IdeaProjects/jacoco-inc-cov/clone";
    String dumpPath = clonePath + File.separator + "dump";
    String classFilesPath = clonePath + File.separator + "target" + File.separator + "classes";
    String reportPath = clonePath + File.separator + "report";
    String srcPath = clonePath + File.separator + "src" + File.separator + "main" + File.separator + "java";

    Dict getLocal() throws IOException {
        org.springframework.core.io.Resource resource = new ClassPathResource("local.yaml");
        return YamlUtil.loadByPath(resource.getFile().getPath());
    }


    @Resource
    JacocoService jacocoService;

    @Test
    void getDiffByVersion() {
    }

    @Test
    void getDiffByBranch() {
    }

    @Test
    void testClone() throws IOException {

        jacocoService.clone(clonePath,
                Constant.GIT_PATH,
                getLocal().getStr("name"),
                getLocal().getStr("password"),
                Constant.TEST_BRANCH,
                Constant.TEST_VERSION_2);
    }

    @Test
    void testCompile() throws IOException {
        testClone();
        String pomPath = clonePath + File.separator + "pom.xml";
        jacocoService.compile(pomPath, Constant.MAVEN_HOME, Constant.COMPILE_PARAM);
    }

    @Test
    void testDump() throws IOException {
        String ip = "0.0.0.0";
        int port = 2014;
        jacocoService.dump(dumpPath, ip, port);
    }

    @Test
    void testReport() throws IOException {
        testDump();
        jacocoService.report(dumpPath, classFilesPath, srcPath, reportPath, null);
    }

    @Test
    void testReportBranch() throws IOException {
        testDump();
        ReportInfo reportInfo = new ReportInfo();
        reportInfo.setGitPath(clonePath);
        reportInfo.setGitUsername(getLocal().getStr("name"));
        reportInfo.setGitPassword(getLocal().getStr("password"));
        reportInfo.setNewBranchName("test");
        reportInfo.setOldBranchName("master");
        reportInfo.setType(ReportTypeEnum.BRANCH.getType());
        jacocoService.report(dumpPath, classFilesPath, srcPath, reportPath, reportInfo);
    }

    @Test
    void testReportVersion() throws IOException {
        testDump();
        ReportInfo reportInfo = new ReportInfo();
        reportInfo.setGitPath(clonePath);
        reportInfo.setGitUsername(getLocal().getStr("name"));
        reportInfo.setGitPassword(getLocal().getStr("password"));
        reportInfo.setBranchName("test");
        reportInfo.setNewVersion(Constant.TEST_VERSION_2);
        reportInfo.setOldVersion(Constant.TEST_VERSION_1);
        reportInfo.setType(ReportTypeEnum.VERSION.getType());
        jacocoService.report(dumpPath, classFilesPath, srcPath, reportPath, reportInfo);
    }
}
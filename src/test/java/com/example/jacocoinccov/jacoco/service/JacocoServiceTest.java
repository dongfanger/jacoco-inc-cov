package com.example.jacocoinccov.jacoco.service;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class JacocoServiceTest {
    private static final String clonePath = "/Users/wanggang424/IdeaProjects/jacoco-inc-cov/clone";

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
        org.springframework.core.io.Resource resource = new ClassPathResource("local.yaml");
        Dict local = YamlUtil.loadByPath(resource.getFile().getPath());
        jacocoService.clone(clonePath,
                Constant.GIT_PATH,
                local.getStr("name"),
                local.getStr("password"),
                Constant.MAIN_BRANCH,
                Constant.MAIN_VERSION_INIT);
    }

    @Test
    void testCompile() throws IOException {
        testClone();
        String pomPath = clonePath + File.separator + "pom.xml";
        jacocoService.compile(pomPath, Constant.MAVEN_HOME, Constant.COMPILE_PARAM);
    }

    @Test
    void testDump() throws IOException {
        String localPath = clonePath + File.separator + "dump";
        String ip = "0.0.0.0";
        int port = 0;
        jacocoService.dump(localPath, ip, port);
    }
}
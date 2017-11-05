package ru.veryevilzed.tools.tests;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.veryevilzed.tools.dto.KeyRequest;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

import java.io.IOException;
import java.nio.file.Paths;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Import(TestApplication.class)
@ActiveProfiles("test")
@Slf4j
public class GroupTest {


    @Autowired
    TestGroupFileService testFileService;

    @Value("${file.path}")
    String path;


    @Test
    public void versions()  {
        TextFileEntity device = testFileService.get(
                new KeyRequest("version", 123, SortedComparableTypes.LessThanEqual, 0),
                new KeyRequest("device", 1L, SortedComparableTypes.Equals, null),
                new KeyRequest("group", 1601L, SortedComparableTypes.Equals)

        );

        Assert.assertTrue(device.getPath().endsWith("@.yml"));


        device = testFileService.get(
                new KeyRequest("version", 1000, SortedComparableTypes.LessThanEqual, 0),
                new KeyRequest("device", 1L, SortedComparableTypes.Equals, null),
                new KeyRequest("group", 1601L, SortedComparableTypes.Equals)
        );
        log.info("Path:{}",device.getPath());
        Assert.assertTrue(device.getPath().endsWith("@1000.yml"));
    }

    @Test
    public void versions2_50()  {
        TextFileEntity device = testFileService.get(
                new KeyRequest("version", 10, SortedComparableTypes.LessThanEqual),
                new KeyRequest("device", 2L, SortedComparableTypes.Equals, null),
                new KeyRequest("group", 1601L, SortedComparableTypes.Equals)

        );
        log.info("Path:{}",device.getPath());
        Assert.assertTrue(device.getPath().endsWith("@.yml"));
    }
}

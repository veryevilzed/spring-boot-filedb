package ru.veryevilzed.tools.tests;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.veryevilzed.tools.dto.KeyRequest;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Import(TestApplication.class)
@ActiveProfiles("test")
public class GroupTest {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GroupTest.class);

    @Autowired
    private TestGroupFileService testFileService;

    @Value("${file.path}")
    private String path;

    @Test
    public void versions()  {
        TextFileEntity device = testFileService.get(
                new KeyRequest("version", 123, SortedComparableTypes.LessThanOrEqualTo, 0),
                new KeyRequest("device", 1L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("group", 1601L, SortedComparableTypes.EqualTo)
        );

        Assert.assertTrue(device.getPath().endsWith("@.yml"));

        device = testFileService.get(
                new KeyRequest("version", 1000, SortedComparableTypes.LessThanOrEqualTo, 0),
                new KeyRequest("device", 1L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("group", 1601L, SortedComparableTypes.EqualTo)
        );
        log.info("Path:{}",device.getPath());
        Assert.assertTrue(device.getPath().endsWith("@1000.yml"));
    }

    @Test
    public void versions2_50()  {
        TextFileEntity device = testFileService.get(
                new KeyRequest("version", 10, SortedComparableTypes.LessThanOrEqualTo),
                new KeyRequest("device", 2L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("group", 1601L, SortedComparableTypes.EqualTo)
        );
        log.info("Path:{}",device.getPath());
        Assert.assertTrue(device.getPath().endsWith("@.yml"));
    }
}

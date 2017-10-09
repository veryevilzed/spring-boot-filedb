package ru.veryevilzed.tools.tests;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.veryevilzed.tools.dto.FileEntity;
import ru.veryevilzed.tools.dto.KeyRequest;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Import(TestApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "debug=true")
@Slf4j
public class TestAll {


    @Autowired
    TestFileService testFileService;

    @Value("${file.path}")
    String path;


    @Test
    public void testDeviceNull() {
        Set<TextFileEntity> devices = testFileService.get("device").get(null, SortedComparableTypes.Equals);
        assertEquals(devices.size(), 2);
    }

    @Test
    public void testGetDevice() {

        Set<TextFileEntity> devices = testFileService.get("device").get(456L, SortedComparableTypes.Equals);
        assertEquals(devices.size(), 2);

        devices = testFileService.get("version").get(0, SortedComparableTypes.Equals);
        assertEquals(devices.size(), 2);

        devices = testFileService.get(
                new KeyRequest("device", 456L, SortedComparableTypes.Equals),
                new KeyRequest("version", 123, SortedComparableTypes.LessThanEqual)
        );

        assertEquals(devices.size(), 1);
        assertTrue(devices.iterator().next().getPath().endsWith("123@456.yml"));

        devices = testFileService.get(
                new KeyRequest("device", 456L, SortedComparableTypes.Equals),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanEqual)
        );

        assertEquals(devices.size(), 1);
        assertTrue(devices.iterator().next().getPath().endsWith("@456.yml"));


        devices = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.Equals),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanEqual)
        );

        assertEquals(devices.size(), 0);

        devices = testFileService.get(
                new KeyRequest("device", 456L, SortedComparableTypes.Equals, null),
                new KeyRequest("version", 124, SortedComparableTypes.LessThanEqual)
        );

        assertEquals(devices.size(), 1);
        assertTrue(devices.iterator().next().getPath().endsWith("123@456.yml"));

    }

    @Test
    public void testDefaultNullResponse() {
        Set<TextFileEntity> devices = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.Equals)
        );

        assertEquals(devices.size(), 0);

        devices = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.Equals, null)
        );

        assertEquals(devices.size(), 2);
    }

    @Test
    public void testUndefinedDevice() {
        Set<TextFileEntity> devices = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.Equals, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanEqual)
        );

        assertEquals(devices.size(), 1);
        assertTrue(devices.iterator().next().getPath().endsWith("@.yml"));


        devices = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.Equals, null),
                new KeyRequest("version", 125, SortedComparableTypes.LessThanEqual)
        );

        assertEquals(devices.size(), 1);
        assertTrue(devices.iterator().next().getPath().endsWith("123@.yml"));

    }


    @Test
    public void testUpdateDevice() {
        testFileService.update();
        Set<TextFileEntity> devices = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.Equals, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanEqual)
        );

        assertEquals(devices.size(), 1);
        assertTrue(devices.iterator().next().getPath().endsWith("@.yml"));


        devices = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.Equals, null),
                new KeyRequest("version", 125, SortedComparableTypes.LessThanEqual)
        );

        assertEquals(devices.size(), 1);
        assertTrue(devices.iterator().next().getPath().endsWith("123@.yml"));

    }

    @Test
    public void testFileCreationRemoving() throws IOException {
        Set<TextFileEntity> devices = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.Equals, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanEqual)
        );
        assertEquals(devices.size(), 1);
        assertTrue(devices.iterator().next().getPath().endsWith("@.yml"));

        File f = Paths.get(path, "122@999.yml").toFile();
        f.createNewFile();

        testFileService.update();

        devices = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.Equals, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanEqual)
        );
        assertEquals(devices.size(), 1);
        assertTrue(devices.iterator().next().getPath().endsWith("122@999.yml"));

        f.delete();
        testFileService.update();

        devices = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.Equals, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanEqual)
        );
        assertEquals(devices.size(), 1);
        assertTrue(devices.iterator().next().getPath().endsWith("@.yml"));
    }

}

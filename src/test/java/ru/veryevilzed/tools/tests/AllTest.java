package ru.veryevilzed.tools.tests;

import org.apache.commons.io.FileUtils;
import org.junit.After;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ru.veryevilzed.tools.dto.KeyRequest;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Import(TestApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "debug=true")
public class AllTest {

    @Autowired
    private TestFileService testFileService;

    @Value("${file.path}")
    private String path;

    @Before
    @After
    public void deleteOldStuff()  {
        try { FileUtils.forceDelete(Paths.get(path, "122@999.yml").toFile());  }catch (IOException ignored) {  }
        try { FileUtils.forceDelete(Paths.get(path, "122@500.yml").toFile());  }catch (IOException ignored) {  }
    }

    @Test
    public void testDeviceNull() {
        Set devices = testFileService.get("device").get(null, SortedComparableTypes.EqualTo);
        assertEquals(devices.size(), 2);
    }

    @Test
    public void testRequestDevice() {
        TextFileEntity device = testFileService.get(
                new KeyRequest("device", 456L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 123, SortedComparableTypes.LessThanOrEqualTo, 0)
        );

        assertNotNull(device);
        assertTrue(device.getPath().endsWith("123@456.yml"));

        device = testFileService.get(
                new KeyRequest("device", 456L, SortedComparableTypes.EqualTo),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo)
        );

        assertNotNull(device);
        assertTrue(device.getPath().endsWith("@456.yml"));

        device = testFileService.get(
                new KeyRequest("device", 456L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 124, SortedComparableTypes.LessThanOrEqualTo)
        );

        assertNotNull(device);
        assertTrue(device.getPath().endsWith("123@456.yml"));
    }

    @Test
    public void testAllFalseDevice() {
        TextFileEntity device = testFileService.get(
                new KeyRequest("device", 333L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 5, SortedComparableTypes.LessThanOrEqualTo)
        );

        assertNotNull(device);
        assertTrue(device.getPath().endsWith("@.yml"));
    }


    @Test
    public void testUndefinedDevice() {
        TextFileEntity device = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo)
        );

        assertNotNull(device);
        assertTrue(device.getPath().endsWith("@.yml"));

        device = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 125, SortedComparableTypes.LessThanOrEqualTo)
        );

        assertNotNull(device);
        assertTrue(device.getPath().endsWith("@.yml"));
    }

    @Test
    public void testUpdateDevice() {
        testFileService.update();
        TextFileEntity device = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo)
        );

        assertNotNull(device);
        assertTrue(device.getPath().endsWith("@.yml"));

        device = testFileService.get(
                new KeyRequest("version", 125, SortedComparableTypes.LessThanOrEqualTo),
                new KeyRequest("device", 999L, SortedComparableTypes.EqualTo, null)
        );

        assertNotNull(device);
        assertTrue(device.getPath().endsWith("123@.yml"));
    }

    @Test
    public void testDeleteFileAndSafeContent() throws IOException {
        FileUtils.writeStringToFile(Paths.get(path, "122@999.yml").toFile(), "world: down", "UTF-8");
        testFileService.update();

        TextFileEntity device = testFileService.get(
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo),
                new KeyRequest("device", 999L, SortedComparableTypes.EqualTo, null)
        );

        assertNotNull(device);
        assertTrue(device.getPath().endsWith("122@999.yml"));

        assertEquals(device.getText(), "world: down");

        FileUtils.forceDelete(Paths.get(path, "122@999.yml").toFile());

        device = testFileService.get(
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo),
                new KeyRequest("device", 999L, SortedComparableTypes.EqualTo, null)
        );

        assertNotNull(device);
        assertTrue(device.getPath().endsWith("122@999.yml"));

        assertEquals(device.getText(), "world: down");

        FileUtils.writeStringToFile(Paths.get(path, "122@500.yml").toFile(), "world: down", "UTF-8");
        testFileService.update();

        device = testFileService.get(
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo),
                new KeyRequest("device", 500L, SortedComparableTypes.EqualTo, null)
        );

        assertNotNull(device);
        assertTrue(device.getPath().endsWith("122@500.yml"));
    }

    @Test
    public void testDeleteFileWithoutSafeContent() throws IOException {

        TextFileEntity device;

        device = testFileService.get(
                new KeyRequest("device", 333L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo)
        );
        assertNotNull(device);
        assertTrue(device.getPath().endsWith("@.yml"));


        FileUtils.writeStringToFile(Paths.get(path, "122@500.yml").toFile(), "world: down", "UTF-8");

        testFileService.update();

        device = testFileService.get(
                new KeyRequest("device", 500L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo)
        );
        assertNotNull(device);
        assertTrue(device.getPath().endsWith("122@500.yml"));
        FileUtils.forceDelete(Paths.get(path, "122@500.yml").toFile());

        device = testFileService.get(
                new KeyRequest("device", 500L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo)
        );

        assertNotNull(device);;
        assertTrue(device.getPath().endsWith("@.yml"));
     }

    @Test
    public void testFileCreationRemoving() throws IOException {
        TextFileEntity device = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo)
        );
        assertNotNull(device);
        assertTrue(device.getPath().endsWith("@.yml"));

        File f = Paths.get(path, "122@999.yml").toFile();
        f.createNewFile();

        testFileService.update();

        device = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo)
        );
        assertNotNull(device);
        assertTrue(device.getPath().endsWith("122@999.yml"));

        f.delete();
        testFileService.update();

        device = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo)
        );
        assertNotNull(device);
        assertTrue(device.getPath().endsWith("@.yml"));
    }

    @Test
    public void testFileText() throws IOException {

        TextFileEntity device = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.EqualTo, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo)
        );
        assertNotNull(device);
        assertTrue(device.getPath().endsWith("@.yml"));


        assertEquals(device.getText(), "hello: world");



        FileUtils.writeStringToFile(Paths.get(path, "122@999.yml").toFile(), "world: down", "UTF-8");
        testFileService.update();

        device = testFileService.get(
                new KeyRequest("version", 122, SortedComparableTypes.LessThanOrEqualTo),
                new KeyRequest("device", 999L, SortedComparableTypes.EqualTo, null)
        );
        assertNotNull(device);
        assertTrue(device.getPath().endsWith("122@999.yml"));

        assertEquals(device.getText(), "world: down");

        FileUtils.forceDelete(Paths.get(path, "122@999.yml").toFile());
        assertFalse(Paths.get(path, "122@999.yml").toFile().exists());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignore) { /**/ }

        FileUtils.writeStringToFile(Paths.get(path, "122@999.yml").toFile(), "world: up", "UTF-8", false);

        testFileService.update();

        assertEquals(device.getText(), "world: up");

        FileUtils.forceDelete(Paths.get(path, "122@999.yml").toFile());
        testFileService.update();
    }
}

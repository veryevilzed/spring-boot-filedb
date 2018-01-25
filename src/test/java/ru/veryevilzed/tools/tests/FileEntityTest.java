package ru.veryevilzed.tools.tests;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileEntityTest {

    @Test
    public void fileEntityEqualityTest() {

        TextFileEntity fileEntity1 = new TextFileEntity(new File("file1"));
        TextFileEntity fileEntity2 = new TextFileEntity(new File("file2"));
        TextFileEntity fileEntity3 = new TextFileEntity(new File("file3"));

        assertTrue(fileEntity1.equals(fileEntity1));
        assertTrue(fileEntity2.equals(fileEntity2));
        assertTrue(fileEntity3.equals(fileEntity3));

        assertFalse(fileEntity1.equals(fileEntity2));
        assertFalse(fileEntity1.equals(fileEntity3));
        assertFalse(fileEntity2.equals(fileEntity3));

        assertFalse(fileEntity1.equals(null));
        assertFalse(fileEntity1.equals(""));
        assertFalse(fileEntity1.equals(new Object()));
    }
}

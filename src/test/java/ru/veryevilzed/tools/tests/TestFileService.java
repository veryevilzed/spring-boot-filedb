package ru.veryevilzed.tools.tests;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;

import javax.annotation.PostConstruct;

import ru.veryevilzed.tools.dto.IntegerKeyCollection;
import ru.veryevilzed.tools.dto.KeyCollection;
import ru.veryevilzed.tools.dto.LongKeyCollection;
import ru.veryevilzed.tools.repository.FileRepository;

@Service
public class TestFileService extends FileRepository<TextFileEntity> {

    @Override
    protected TextFileEntity createFileEntity(File file) {
        return new TextFileEntity(file);
    }

    @PostConstruct
    @Scheduled(fixedDelay = 60000L)
    public void update() {
        super.update();
    }

    public TestFileService(@Value("${file.path}") String path) {
        super(path, "(?<version>\\d+)?@(?<device>\\d+)?.yml", new KeyCollection[] {
                new IntegerKeyCollection("version", 0),
                new LongKeyCollection("device", null)
        });
    }
}

# FileDB

База данных основанная на файлах.

## Init

Структура каталога:

```
.
├── 123@.yml
├── 123@456.yml
├── @.yml
└── @456.yml

0 directories, 4 files

```

Entity:

```java

public class TextFileEntity extends FileEntity {
    String text = null;
    public TextFileEntity(File file) {
        super(file);
    }

    @Override
    public void update() {
        text = null;
    }

    @Override
    public boolean hasData() {
        return this.text != null;
    }

    public String getText() {
        if (text == null) {
            try {
                text = FileUtils.readFileToString(this.getFile(), "UTF-8");
            }catch (IOException ignored) {}
        }
        return text;
    }
}

```

Service:

```java

@Service
public class TestFileService extends FileRepository<TextFileEntity> {

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


```

Ключи:

`IntegerKeyCollection`,
`LongKeyCollection`,
`StringKeyCollection`,

Todo:

`FloatKeyCollection`,
`DateKeyCollection`


Создание ключа:

```java
// Без фолбэка
new KeyCollection(String name)

// С фолбэком на дефолтное значение
new KeyCollection(String name, T defaultKey)
```

## Построение запроса

```java

Set<TextFileEntity> devices = testFileService.get("device").get(456L, SortedComparableTypes.Equals);
// 2-ва файла 123@456.yml и @456.yml

Set<TextFileEntity> devices = testFileService.get("device").get(null, SortedComparableTypes.Equals);
// 2-ва файла 123@.yml и @.yml (так как указан фолбэк на Null)

TextFileEntity device = testFileService.get(
            new KeyRequest("device", 456L, SortedComparableTypes.Equals),
            new KeyRequest("version", 123, SortedComparableTypes.LessThanEqual)
); // 123@456.yml


TextFileEntity device = testFileService.get(
                new KeyRequest("device", 456L, SortedComparableTypes.Equals),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanEqual)
); // @456.yml


TextFileEntity devices = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.Equals, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanEqual)
); // @.yml

```
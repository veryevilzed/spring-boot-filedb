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


```java

@Service
public class TestFileService extends FileRepository {

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

Set<FileEntity> devices = testFileService.get("device").get(456L, SortedComparableTypes.Equals);
// 2-ва файла 123@456.yml и @456.yml

Set<FileEntity> devices = testFileService.get("device").get(null, SortedComparableTypes.Equals);
// 2-ва файла 123@.yml и @.yml (так как указан фолбэк на Null)

Set<FileEntity> devices = testFileService.get(
            new KeyRequest("device", 456L, SortedComparableTypes.Equals),
            new KeyRequest("version", 123, SortedComparableTypes.LessThanEqual)
); // 123@456.yml


Set<FileEntity> devices = testFileService.get(
                new KeyRequest("device", 456L, SortedComparableTypes.Equals),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanEqual)
); // @456.yml


Set<FileEntity> devices = testFileService.get(
                new KeyRequest("device", 999L, SortedComparableTypes.Equals, null),
                new KeyRequest("version", 122, SortedComparableTypes.LessThanEqual)
); // @.yml

```
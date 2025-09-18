# Liquibase Database Migration

Ez a projekt Liquibase-t használ az adatbázis migrációk kezelésére és a teszt adatok betöltésére.

## 📁 Könyvtárstruktúra

```
src/main/resources/db/
├── changelog/
│   ├── db.changelog-master.xml          # Fő changelog fájl
│   └── changesets/
│       ├── 001-create-tables.xml        # Táblák létrehozása
│       └── 002-insert-test-data.xml     # Teszt adatok betöltése
└── liquibase.properties                 # Liquibase konfiguráció
```

## 🚀 Használat

### 1. Docker Compose indítása
```bash
docker-compose up --build -d
```

**Automatikus Liquibase futtatás**: A konténer indításakor a Liquibase automatikusan ellenőrzi és futtatja a szükséges changeset-eket. A Docker Compose biztosítja, hogy az alkalmazás csak akkor induljon el, amikor az adatbázis elérhető.

### 2. Liquibase Maven parancsok

#### Adatbázis migráció futtatása
```bash
mvn liquibase:update
```

#### Adatbázis állapot ellenőrzése
```bash
mvn liquibase:status
```

#### Adatbázis rollback
```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

#### Adatbázis diff generálása
```bash
mvn liquibase:diff
```

#### Adatbázis dokumentáció generálása
```bash
mvn liquibase:dbDoc
```

## 📊 Teszt Adatok

A `002-insert-test-data.xml` fájl tartalmazza a következő teszt adatokat:

### CV Processing Requests
- **ID 1**: CV-1.md (Marketing Manager) - 1 év tapasztalat
- **ID 2**: CV-2.md (Software Developer) - 2 év tapasztalat

### Extracted Fields
- Mindkét CV-hez kinyert mezők (skills, languages, profile, stb.)

### Validation Results
- **CV-1**: INVALID (Java hiányzik, Hungarian hiányzik, GenAI érdeklődés hiányzik)
- **CV-2**: INVALID (Túl sok tapasztalat, de minden más rendben)

## 🔧 Konfiguráció

### application.yml
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # Liquibase kezeli a sémát
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml
    enabled: true
    drop-first: false
    contexts: default
    default-schema: public
    liquibase-schema: public
    database-change-log-table: DATABASECHANGELOG
    database-change-log-lock-table: DATABASECHANGELOGLOCK
```

### Automatikus Indítási Folyamat

1. **Adatbázis várakozás**: A Docker Compose `depends_on` és `healthcheck` biztosítja, hogy az alkalmazás csak akkor induljon el, amikor az adatbázis elérhető
2. **Liquibase ellenőrzés**: Spring Boot automatikusan futtatja a Liquibase migrációkat indításkor
3. **Changeset végrehajtás**: Csak az új vagy módosított changeset-ek futnak le
4. **Alkalmazás indítás**: Az alkalmazás csak a sikeres migráció után indul el

### Docker Volume
```yaml
volumes:
  postgres_data:  # Adatok megmaradnak container újraindítás után
```

## 📝 Új Changeset Hozzáadása

1. Hozz létre egy új XML fájlt a `changesets/` mappában
2. Add hozzá a `db.changelog-master.xml`-hez:
```xml
<include file="changesets/003-new-changeset.xml" relativeToChangelogFile="true"/>
```

## 🐛 Hibaelhárítás

### Adatbázis törlése és újraépítése
```bash
docker-compose down -v  # Volume-okat is törli
docker-compose up --build -d
```

### Liquibase checksum reset
```bash
mvn liquibase:clearCheckSums
```

## 📚 További Információ

- [Liquibase Dokumentáció](https://docs.liquibase.com/)
- [Spring Boot Liquibase Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-access.liquibase)

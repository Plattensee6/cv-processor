# Ollama Local Model Setup

Ez a dokumentum leírja, hogyan használhatod a lokális Ollama modelleket a CV Processor alkalmazásban.

## 🚀 Gyors indítás

### 1. Konténerek indítása
```bash
docker-compose up -d
```

### 2. Modell letöltése
```bash
# Ollama konténerbe belépés
docker-compose exec ollama ollama pull llama3.2:3b

# Vagy más modell használata
docker-compose exec ollama ollama pull mistral:7b
```

### 3. Modell tesztelése
```bash
# Modell listázása
docker-compose exec ollama ollama list

# Modell tesztelése
docker-compose exec ollama ollama run llama3.2:3b "Hello, how are you?"
```

## 📋 Elérhető modellek

### Kisebb modellek (ajánlott fejlesztéshez):
- `llama3.2:3b` - 3B paraméter, gyors
- `mistral:7b` - 7B paraméter, jó minőség
- `codellama:7b` - Kód generáláshoz optimalizált

### Nagyobb modellek (produkcióhoz):
- `llama3.2:8b` - 8B paraméter, jobb minőség
- `mistral:13b` - 13B paraméter, kiváló minőség

## ⚙️ Konfiguráció

### Környezeti változók:
```bash
# .env fájlban
OLLAMA_HOST=ollama
OLLAMA_PORT=11434
OLLAMA_MODEL=llama3.2:3b
OLLAMA_TIMEOUT=120
```

### Modell váltás:
```bash
# Új modell letöltése
docker-compose exec ollama ollama pull mistral:7b

# Konfiguráció frissítése
export OLLAMA_MODEL=mistral:7b
docker-compose restart cv-processor-app
```

## 🔧 Hibaelhárítás

### Modell nem elérhető:
```bash
# Ellenőrizd, hogy a modell letöltve van-e
docker-compose exec ollama ollama list

# Ha nincs, töltsd le
docker-compose exec ollama ollama pull llama3.2:3b
```

### Ollama nem válaszol:
```bash
# Konténer állapot ellenőrzése
docker-compose ps ollama

# Logok megtekintése
docker-compose logs ollama

# Újraindítás
docker-compose restart ollama
```

### Memória probléma:
```bash
# Kisebb modell használata
export OLLAMA_MODEL=llama3.2:3b
docker-compose restart cv-processor-app
```

## 📊 Teljesítmény

### Modell méret vs. teljesítmény:
- **3B modellek**: ~2-4GB RAM, gyors válaszidő
- **7B modellek**: ~4-8GB RAM, közepes válaszidő
- **13B+ modellek**: ~8GB+ RAM, lassabb válaszidő

### Optimalizálás:
```bash
# GPU használata (ha elérhető)
docker-compose exec ollama ollama run llama3.2:3b --gpu

# Batch méret csökkentése
export OLLAMA_TIMEOUT=60
```

## 🔄 Fallback mechanizmus

Az alkalmazás automatikusan fallback-et használ:
1. **Elsődleges**: Ollama lokális modell
2. **Fallback**: Hugging Face API (ha konfigurálva van)

## 📝 Példa használat

```bash
# Teljes stack indítása
docker-compose up -d

# Modell letöltése
docker-compose exec ollama ollama pull llama3.2:3b

# Alkalmazás tesztelése
curl -X POST http://localhost:8080/api/cv/upload \
  -F "file=@test-cv.pdf"
```

## 🎯 Előnyök

- ✅ **Ingyenes**: Nincs API kulcs szükséges
- ✅ **Lokális**: Adatok nem hagyják el a szervert
- ✅ **Gyors**: Nincs hálózati késés
- ✅ **Testreszabható**: Bármilyen modellt használhatsz
- ✅ **Offline**: Internet nélkül is működik

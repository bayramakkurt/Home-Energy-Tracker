# Home Energy Tracker

Ev enerji tüketimini gerçek zamanlı olarak izleyen, analiz eden ve yapay zeka destekli tasarruf önerileri sunan mikroservis tabanlı platform.

## İçindekiler

- [Proje Hakkında](#proje-hakkında)
- [Mimari](#mimari)
- [Teknolojiler](#teknolojiler)
- [Özellikler](#özellikler)
- [Kurulum](#kurulum)
- [API Dökümanı](#api-dökümanı)
- [Geliştirme](#geliştirme)

## Proje Hakkında

Home Energy Tracker, evlerdeki enerji tüketimini izlemek ve optimize etmek için geliştirilmiş özgün bir mikroservis platformudur. Proje, Spring Boot ile geliştirilmiş altı bağımsız servis, Apache Kafka ile olay tabanlı mesajlaşma, InfluxDB ile zaman serisi veri depolama ve Ollama AI entegrasyonu içerir.

### Ana Hedefler

- Gerçek zamanlı enerji tüketimi takibi
- Geçmiş verilerin analizi ve raporlanması
- Yapay zeka destekli enerji tasarrufu önerileri
- Eşik aşımlarında otomatik bildirimler
- Yüksek hacimli veri toplama ve işleme

## Mimari

### Sistem Akışı

```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│  İstemci    │───▶│  Ingestion   │───▶│    Kafka    │
│             │    │   Service    │    │   Mesaj     │
│             │    │    (8082)    │    │   Kuyruğu   │
└─────────────┘    └──────────────┘    └──────┬──────┘
                                              │
                   ┌──────────────────────────┴──────────────────┐
                   │                                              │
                   ▼                                              ▼
            ┌──────────────┐                             ┌──────────────┐
            │    Usage     │──────────────────────────▶ │  InfluxDB    │
            │   Service    │   (Zaman serisi yazma)     │   (TSDB)     │
            │   (8083)     │                             └──────────────┘
            └──────┬───────┘
                   │
                   │ (Eşik aşımı)
                   ▼
            ┌──────────────┐                             ┌──────────────┐
            │    Alert     │──────────────────────────▶ │   Mailpit    │
            │   Service    │   (E-posta bildirimi)      │    (SMTP)    │
            │   (8084)     │                             └──────────────┘
            └──────────────┘

     ┌─────────────┐    ┌──────────────┐    ┌──────────────┐
     │    User     │    │    Device    │    │   Insight    │───▶ Ollama AI
     │   Service   │    │   Service    │    │   Service    │
     │   (8080)    │    │    (8081)    │    │    (8085)    │
     └─────────────┘    └──────────────┘    └──────────────┘
            │                   │                     │
            └───────────────────┴─────────────────────┘
                              │
                        ┌─────▼─────┐
                        │   MySQL   │
                        │           │
                        └───────────┘
```

### Mikroservis Yapısı

Proje, bağımsız çalışabilen ve ölçeklenebilen altı mikroservisten oluşur:

**1. User Service (8080)**

- Kullanıcı yönetimi ve kimlik doğrulama
- MySQL veritabanı ile veri kalıcılığı
- Flyway ile veritabanı migrasyon yönetimi

**2. Device Service (8081)**

- Enerji tüketen cihazların (buzdolabı, klima, vb.) tanımlanması
- Kullanıcı-cihaz ilişkilendirmesi
- Cihaz kategorilendirme

**3. Ingestion Service (8082)**

- IoT cihazlarından veri toplama katmanı
- REST API ile veri girişi
- Kafka'ya olay yayınlama
- Simülasyon modu (test için dakikada 10.000 veri üretimi)

**4. Usage Service (8083)**

- Kafka'dan enerji verilerini dinleme
- InfluxDB'ye zaman serisi verilerini yazma
- 10 saniyede bir otomatik veri agregasyonu
- Eşik kontrolü ve alarm üretimi
- Cihaz ve kullanıcı bazlı sorgulama

**5. Alert Service (8084)**

- Kafka'dan alarm olaylarını dinleme
- E-posta bildirimi gönderme (Mailpit SMTP)
- Alarm geçmişi saklama

**6. Insight Service (8085)**

- Yapay zeka destekli analiz
- Ollama AI entegrasyonu (deepseek-coder modeli)
- Enerji tüketimi değerlendirmesi
- Kişiselleştirilmiş tasarruf önerileri

## Teknolojiler

### Backend Framework

- **Spring Boot 4.0.3** - Mikroservis geliştirme framework'ü
- **Java 17** - Programlama dili
- **Maven** - Bağımlılık yönetimi ve build aracı

### Mesajlaşma ve Akış İşleme

- **Apache Kafka 4.1.1** - Olay tabanlı mesajlaşma sistemi (KRaft modu)
- Event-driven mimari ile servisler arası asenkron iletişim

### Veritabanları

- **MySQL 8.3** - İlişkisel veritabanı (kullanıcı, cihaz, alarm verileri)
- **InfluxDB 2.7** - Zaman serisi veritabanı (enerji tüketim metrikleri)

### Yapay Zeka

- **Ollama** - Yerel AI model çalıştırma platformu
- **Spring AI 2.0.0-M2** - Spring Boot AI entegrasyon kütüphanesi
- **deepseek-coder** - Enerji analizi için kullanılan dil modeli

### Altyapı

- **Docker & Docker Compose** - Konteynerizasyon ve orkestrasyon
- **Mailpit** - Geliştirme ortamı için SMTP sunucusu
- **Kafka UI** - Kafka kuyruklarını görsel olarak izleme

### Diğer Kütüphaneler

- **Spring Data JPA** - Veritabanı erişim katmanı
- **Flyway** - Veritabanı migrasyon aracı
- **Lombok** - Kod tekrarını azaltma
- **Jackson** - JSON serileştirme/deserileştirme

## Özellikler

### ⚡ Gerçek Zamanlı İzleme

Kafka olay tabanlı mesajlaşma ile anlık enerji tüketimi takibi.

### 📊 Zaman Serisi Depolama

InfluxDB ile yüksek performanslı zaman serisi veri depolama ve sorgulama.

### 🤖 Yapay Zeka Destekli İçgörüler

Ollama AI ile kişiselleştirilmiş enerji tasarrufu önerileri ve tüketim analizleri.

### 🔔 Akıllı Bildirimler

Belirlenen eşik değerler aşıldığında otomatik e-posta bildirimleri.

### 📈 Yüksek Hacimli Veri İşleme

Simülasyon modunda dakikada 10.000+ veri noktası işleme kapasitesi.

### 🔌 Bağımsız Ölçeklenebilirlik

Her servis bağımsız olarak geliştirilebilir ve ölçeklendirilebilir.

## Kurulum

### Gereksinimler

Sisteminizde aşağıdakilerin kurulu olması gerekir:

- Java 17 veya üzeri
- Maven 3.8 veya üzeri
- Docker ve Docker Compose
- Ollama (yapay zeka özellikleri için)

### Adım 1: Projeyi İndirme

```bash
git clone <repository-url>
cd home-energy-tracker
```

### Adım 2: Yapılandırma Dosyalarını Hazırlama

Her servis için `application.properties` dosyasını oluşturun:

```bash
# User Service
cp user-service/src/main/resources/application.properties.example \
   user-service/src/main/resources/application.properties

# Device Service
cp device-service/src/main/resources/application.properties.example \
   device-service/src/main/resources/application.properties

# Ingestion Service
cp ingestion-service/src/main/resources/application.properties.example \
   ingestion-service/src/main/resources/application.properties

# Usage Service
cp usage-service/src/main/resources/application.properties.example \
   usage-service/src/main/resources/application.properties

# Alert Service
cp alert-service/src/main/resources/application.properties.example \
   alert-service/src/main/resources/application.properties

# Insight Service
cp insight-service/src/main/resources/application.properties.example \
   insight-service/src/main/resources/application.properties
```

> **Not:** Üretim ortamında `application.properties` dosyalarındaki şifre, token ve diğer hassas bilgileri mutlaka değiştirin.

**Yapılandırma Değişkenleri:**

`.example` dosyalarındaki değerler `${VARIABLE_NAME:default_value}` formatında tanımlanmıştır. Bu değerleri ortam değişkenleri ile veya doğrudan dosyada değiştirerek özelleştirebilirsiniz:

- `${DB_USERNAME:root}` - Veritabanı kullanıcı adı (varsayılan: root)
- `${DB_PASSWORD:password}` - Veritabanı şifresi (**üretimde değiştirin!**)
- `${INFLUX_TOKEN:my-token}` - InfluxDB API token (**üretimde değiştirin!**)
- `${INFLUX_ORG:hba}` - InfluxDB organizasyon adı
- `${PACKAGE_NAME:com.hba}` - Proje paket adı (Kafka type mapping için)
- `${SENDER_EMAIL:template@example.com}` - E-posta gönderen adresi
- `${OLLAMA_MODEL:deepseek-coder}` - Kullanılacak AI modeli
- `${OLLAMA_URL:http://localhost:11434}` - Ollama sunucu adresi
- `${USAGE_SERVICE_URL:http://localhost:8083/api/v1/usage}` - Usage Service API adresi

### Adım 3: Altyapı Servislerini Başlatma

Docker Compose ile MySQL, Kafka, InfluxDB ve Mailpit servislerini başlatın:

```bash
docker-compose up -d
```

Çalışan servisler:

- MySQL: `localhost:3306`
- Kafka: `localhost:9094` (dış bağlantılar için)
- Kafka UI: `http://localhost:8070`
- InfluxDB: `http://localhost:8072`
- Mailpit Web UI: `http://localhost:8025`
- Mailpit SMTP: `localhost:1025`

### Adım 4: Yapay Zeka Modelini İndirme

Ollama'yı başlatın ve gerekli modeli indirin:

```bash
# Ollama'yı başlat
ollama serve

# Model indir (776 MB)
ollama pull deepseek-coder
```

**Alternatif Modeller:**

- `llama3.2` (2GB) - Orta düzey performans
- `gemma2:2b` (1.6GB) - Hızlı yanıt, düşük kaynak kullanımı

### Adım 5: Mikroservisleri Başlatma

Önerilen başlatma sırası:

```bash
# 1. Temel servisler
cd user-service
mvn spring-boot:run
# Yeni terminal
cd device-service
mvn spring-boot:run

# 2. Veri işleme hattı
# Yeni terminal
cd ingestion-service
mvn spring-boot:run
# Yeni terminal
cd usage-service
mvn spring-boot:run

# 3. Bildirim ve analiz servisleri
# Yeni terminal
cd alert-service
mvn spring-boot:run
# Yeni terminal
cd insight-service
mvn spring-boot:run
```

### Sağlık Kontrolü

Servislerin çalıştığını doğrulayın:

```bash
curl http://localhost:8080/actuator/health  # User Service
curl http://localhost:8081/actuator/health  # Device Service
curl http://localhost:8082/actuator/health  # Ingestion Service
curl http://localhost:8083/actuator/health  # Usage Service
curl http://localhost:8084/actuator/health  # Alert Service
curl http://localhost:8085/actuator/health  # Insight Service
```

## API Dökümanı

### User Service (8080)

Kullanıcı yönetimi işlemleri.

```http
POST   /api/v1/user          # Yeni kullanıcı oluştur
GET    /api/v1/user          # Tüm kullanıcıları listele
GET    /api/v1/user/{id}     # Kullanıcı detaylarını getir
PUT    /api/v1/user/{id}     # Kullanıcı bilgilerini güncelle
DELETE /api/v1/user/{id}     # Kullanıcıyı sil
```

**Örnek İstek:**

```json
POST /api/v1/user
{
  "name": "Ahmet Yılmaz",
  "email": "ahmet@example.com",
  "energyThreshold": 100.0
}
```

### Device Service (8081)

Cihaz yönetimi işlemleri.

```http
POST   /api/v1/device                    # Yeni cihaz ekle
GET    /api/v1/device                    # Tüm cihazları listele
GET    /api/v1/device/{id}               # Cihaz detaylarını getir
GET    /api/v1/device/user/{userId}      # Kullanıcının cihazlarını listele
PUT    /api/v1/device/{id}               # Cihaz bilgilerini güncelle
DELETE /api/v1/device/{id}               # Cihazı sil
```

**Örnek İstek:**

```json
POST /api/v1/device
{
  "name": "Buzdolabı",
  "type": "APPLIANCE",
  "location": "Mutfak",
  "userId": 1
}
```

### Ingestion Service (8082)

Enerji tüketim verilerini alma.

```http
POST   /api/v1/ingestion                      # Enerji verisi gönder
GET    /api/v1/ingestion                      # Simülasyon durumunu görüntüle
POST   /api/v1/ingestion/simulation/start     # Simülasyonu başlat
POST   /api/v1/ingestion/simulation/stop      # Simülasyonu durdur
```

**Örnek İstek:**

```json
POST /api/v1/ingestion
{
  "deviceId": 1,
  "energyConsumed": 0.45,
  "timestamp": "2026-03-04T10:30:00Z"
}
```

### Usage Service (8083)

Enerji tüketim sorgulama.

```http
GET /api/v1/usage/{userId}?days=3    # Kullanıcının son X günlük tüketimini getir
```

**Örnek Yanıt:**

```json
{
  "userId": 1,
  "devices": [
    {
      "id": 1,
      "name": "Buzdolabı",
      "type": "APPLIANCE",
      "location": "Mutfak",
      "userId": 1,
      "energyConsumed": 24.67
    },
    {
      "id": 2,
      "name": "Klima",
      "type": "CLIMATE",
      "location": "Salon",
      "userId": 1,
      "energyConsumed": 56.42
    }
  ]
}
```

### Insight Service (8085)

Yapay zeka destekli analizler.

```http
GET /insights/{userId}/overview    # Son 3 günlük genel değerlendirme
GET /insights/{userId}/tips        # Tasarruf önerileri al
```

**Örnek Yanıt:**

```json
{
  "userId": 1,
  "tips": "Son 3 günde 81.09 kWh enerji tüketimi gerçekleşti. Ortalama bir ev için bu değer yüksek...",
  "energyUsage": 81.09
}
```

## Geliştirme

### Test Verisi Oluşturma

Simülasyon modunu kullanarak otomatik test verisi oluşturabilirsiniz:

```bash
# 1. Kullanıcı oluştur
curl -X POST http://localhost:8080/api/v1/user \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Kullanıcı",
    "email": "test@example.com",
    "energyThreshold": 100.0
  }'

# 2. Cihaz ekle
curl -X POST http://localhost:8081/api/v1/device \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Cihazı",
    "type": "APPLIANCE",
    "location": "Mutfak",
    "userId": 1
  }'

# 3. Simülasyonu başlat
curl -X POST http://localhost:8082/api/v1/ingestion/simulation/start
```

Simülasyon varsayılan olarak dakikada 10.000 veri noktası üretir. Ayarları `ingestion-service/application.properties` dosyasından değiştirebilirsiniz.

### Veritabanı Migration

User Service ve Device Service Flyway kullanır. Migration dosyaları:

- `user-service/src/main/resources/db/migration/`
- `device-service/src/main/resources/db/migration/`

Yeni migration eklemek için:

```bash
# SQL dosyası oluştur
touch user-service/src/main/resources/db/migration/V2__add_new_column.sql

# Servisi yeniden başlat (otomatik uygulanır)
```

### Kafka İzleme

Kafka mesajlarını görüntülemek için:

**Web UI ile:**

```bash
http://localhost:8070
```

**Komut satırı ile:**

```bash
# energy-usage topic'ini dinle
docker exec -it kafka kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic energy-usage \
  --from-beginning

# energy-alerts topic'ini dinle
docker exec -it kafka kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic energy-alerts \
  --from-beginning
```

### InfluxDB Sorguları

InfluxDB arayüzüne erişim: `http://localhost:8072`

**Örnek Flux sorguları:**

```flux
// Son 1 saatin tüketimi
from(bucket: "usage-bucket")
  |> range(start: -1h)
  |> filter(fn: (r) => r["_measurement"] == "energy_usage")
  |> aggregateWindow(every: 5m, fn: mean)

// Cihaz bazlı toplam tüketim
from(bucket: "usage-bucket")
  |> range(start: -24h)
  |> filter(fn: (r) => r["_measurement"] == "energy_usage")
  |> group(columns: ["deviceId"])
  |> sum()
```

### E-posta Önizleme

Mailpit web arayüzü ile gönderilen e-postaları görüntüleyin:

```bash
http://localhost:8025
```

### Yapılandırma Ayarları

Tüm konfigürasyonlar `application.properties` dosyalarında saklanır. Üretim ortamında mutlaka değiştirin:

- **MySQL şifresi:** `spring.datasource.password`
- **InfluxDB token:** `influx.token`
- **E-posta adresi:** `sender.mail`

---

**Not:** Bu proje eğitim ve araştırma amaçlıdır. Üretim ortamında kullanmadan önce güvenlik testleri yapılmalıdır.

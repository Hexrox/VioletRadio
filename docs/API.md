# 📡 Violet Radio - API Documentation

## Radio Browser API

**Base URL:** `https://de1.api.radio-browser.info/json/`

Darmowe, open-source API do stacji radiowych z całego świata.

### Endpoints

#### 1. Get Stations

```http
GET /stations?limit=50&order=votes&reverse=true
```

**Response:**
```json
[
  {
    "stationuuid": "960f4b87-0601-11e8-ae97-52543be04c81",
    "name": "Radio 357",
    "url": "http://stream.url...",
    "homepage": "https://radio357.pl",
    "favicon": "https://radio357.pl/logo.png",
    "country": "Poland",
    "language": "polish",
    "tags": "jazz,smooth",
    "codec": "MP3",
    "bitrate": 192,
    "votes": 145
  }
]
```

#### 2. Search Stations

```http
GET /stations/search?country=Poland&tag=jazz&limit=20
```

**Query Params:**
- `name` - nazwa stacji
- `country` - kraj
- `tag` - gatunek
- `codec` - MP3, AAC, OGG
- `bitrateMin` / `bitrateMax`
- `order` - sortowanie
- `limit` - max wyników

#### 3. Get Countries

```http
GET /countries
```

**Response:**
```json
[
  {
    "name": "Poland",
    "iso_3166_1": "PL",
    "stationcount": 234
  }
]
```

#### 4. Click Station (tracking)

```http
GET /url/{stationuuid}
```

Rejestruje kliknięcie (zwiększa licznik).

### Implementation

```kotlin
interface RadioBrowserApi {

    @GET("stations")
    suspend fun getStations(
        @Query("limit") limit: Int = 100,
        @Query("order") order: String = "votes"
    ): List<StationDto>

    @GET("stations/search")
    suspend fun searchStations(
        @Query("name") name: String? = null,
        @Query("country") country: String? = null,
        @Query("tag") tag: String? = null
    ): List<StationDto>

    @GET("countries")
    suspend fun getCountries(): List<CountryDto>

    @GET("url/{uuid}")
    suspend fun clickStation(@Path("uuid") uuid: String)
}
```

### Best Practices

1. **Cache locally** - zmniejsz API calls
2. **Handle errors** - HttpException, IOException
3. **Debounce search** - 300ms po zakończeniu pisania
4. **Load balancing** - rotuj serwery (de1, nl1, at1)

### Rate Limiting

- Brak oficjalnych limitów
- Używaj cache'u
- Debounce user input

---

*API docs dla Violet Radio 💜*

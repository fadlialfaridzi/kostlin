package com.example.kostlin.data.model

data class KosProperty(
    val id: Int,
    val name: String,
    val location: String,
    val price: String,
    val pricePerMonth: Int, // untuk sorting dan filtering
    val rating: String,
    val ratingValue: Float, // untuk sorting
    val type: KosType,
    val facilities: List<String>,
    val description: String,
    val imageUrl: String? = null,
    val isPopular: Boolean = false,
    val isRecommended: Boolean = false
)

enum class KosType {
    PUTRA, PUTRI, CAMPUR
}

data class KosReview(
    val id: Int,
    val userName: String,
    val userAvatar: String? = null,
    val rating: Float,
    val comment: String,
    val date: String,
    val isVerified: Boolean = false
)

data class KosFacility(
    val name: String,
    val icon: String,
    val isAvailable: Boolean = true
)

data class FavoriteKos(
    val id: Int,
    val kosProperty: KosProperty,
    val dateAdded: String,
    val status: FavoriteStatus = FavoriteStatus.ACTIVE
)

enum class FavoriteStatus {
    ACTIVE, REMOVED
}

object KosDummyData {
    val allKosProperties = listOf(
        // Popular Properties
        KosProperty(
            id = 1,
            name = "Kos Putri Mama Faiz",
            location = "Limau Manis, Padang",
            price = "Rp 800.000/Bulan",
            pricePerMonth = 800000,
            rating = "4.5",
            ratingValue = 4.5f,
            type = KosType.PUTRI,
            facilities = listOf("WiFi", "AC", "Kamar Mandi Dalam", "Dapur Bersama", "Parkir Motor"),
            description = "Kos putri yang nyaman dan aman dengan fasilitas lengkap di area Limau Manis",
            isPopular = true,
            isRecommended = true
        ),
        KosProperty(
            id = 2,
            name = "Kos Putra Buk Mina",
            location = "Cupak Tangah, Padang",
            price = "Rp 650.000/Bulan",
            pricePerMonth = 650000,
            rating = "3.8",
            ratingValue = 3.8f,
            type = KosType.PUTRA,
            facilities = listOf("WiFi", "Kamar Mandi Dalam", "Dapur Bersama", "Parkir Motor"),
            description = "Kos putra strategis dekat kampus dengan harga terjangkau",
            isPopular = true,
            isRecommended = true
        ),
        KosProperty(
            id = 3,
            name = "Kos Campur Pak Budi",
            location = "Ambacang, Padang",
            price = "Rp 700.000/Bulan",
            pricePerMonth = 700000,
            rating = "4.2",
            ratingValue = 4.2f,
            type = KosType.CAMPUR,
            facilities = listOf("WiFi", "AC", "Kamar Mandi Dalam", "Laundry", "Parkir Motor"),
            description = "Kos campur dengan fasilitas lengkap dan lokasi strategis",
            isPopular = true
        ),
        
        // Additional Properties
        KosProperty(
            id = 4,
            name = "Kos Putri Melati",
            location = "Pauh, Padang",
            price = "Rp 750.000/Bulan",
            pricePerMonth = 750000,
            rating = "4.4",
            ratingValue = 4.4f,
            type = KosType.PUTRI,
            facilities = listOf("WiFi", "AC", "Kamar Mandi Dalam", "Dapur Bersama", "Security 24 Jam"),
            description = "Kos putri eksklusif dengan keamanan 24 jam",
            isRecommended = true
        ),
        KosProperty(
            id = 5,
            name = "Kos Putra Mawar",
            location = "Padang Selatan",
            price = "Rp 680.000/Bulan",
            pricePerMonth = 680000,
            rating = "4.6",
            ratingValue = 4.6f,
            type = KosType.PUTRA,
            facilities = listOf("WiFi", "Kamar Mandi Dalam", "Dapur Bersama", "Parkir Motor", "Gym"),
            description = "Kos putra modern dengan fasilitas gym",
            isRecommended = true
        ),
        KosProperty(
            id = 6,
            name = "Kos Elite Padang Utara",
            location = "Padang Utara",
            price = "Rp 950.000/Bulan",
            pricePerMonth = 950000,
            rating = "4.8",
            ratingValue = 4.8f,
            type = KosType.CAMPUR,
            facilities = listOf("WiFi", "AC", "Kamar Mandi Dalam", "Dapur Pribadi", "Balkon", "Security 24 Jam"),
            description = "Kos premium dengan fasilitas mewah dan pemandangan kota",
            isRecommended = true
        ),
        KosProperty(
            id = 7,
            name = "Kos Sederhana Jati",
            location = "Jati, Padang",
            price = "Rp 450.000/Bulan",
            pricePerMonth = 450000,
            rating = "3.5",
            ratingValue = 3.5f,
            type = KosType.CAMPUR,
            facilities = listOf("WiFi", "Kamar Mandi Bersama", "Dapur Bersama"),
            description = "Kos ekonomis untuk mahasiswa dengan budget terbatas"
        ),
        KosProperty(
            id = 8,
            name = "Kos Putri Andalas",
            location = "Limau Manis, Padang",
            price = "Rp 850.000/Bulan",
            pricePerMonth = 850000,
            rating = "4.7",
            ratingValue = 4.7f,
            type = KosType.PUTRI,
            facilities = listOf("WiFi", "AC", "Kamar Mandi Dalam", "Dapur Bersama", "Ruang Belajar", "Security 24 Jam"),
            description = "Kos putri dekat Universitas Andalas dengan fasilitas belajar"
        ),
        KosProperty(
            id = 9,
            name = "Kos Putra Minang",
            location = "Koto Tangah, Padang",
            price = "Rp 600.000/Bulan",
            pricePerMonth = 600000,
            rating = "4.0",
            ratingValue = 4.0f,
            type = KosType.PUTRA,
            facilities = listOf("WiFi", "Kamar Mandi Dalam", "Dapur Bersama", "Parkir Motor"),
            description = "Kos putra dengan nuansa tradisional Minang"
        ),
        KosProperty(
            id = 10,
            name = "Kos Modern Tabing",
            location = "Tabing, Padang",
            price = "Rp 720.000/Bulan",
            pricePerMonth = 720000,
            rating = "4.3",
            ratingValue = 4.3f,
            type = KosType.CAMPUR,
            facilities = listOf("WiFi", "AC", "Kamar Mandi Dalam", "Dapur Bersama", "Rooftop", "Parkir Motor"),
            description = "Kos modern dengan rooftop untuk bersantai"
        )
    )
    
    // Filter functions
    fun getPopularProperties(): List<KosProperty> {
        return allKosProperties.filter { it.isPopular }
    }
    
    fun getRecommendedProperties(): List<KosProperty> {
        return allKosProperties.filter { it.isRecommended }
    }
    
    fun searchProperties(query: String): List<KosProperty> {
        if (query.isBlank()) return allKosProperties
        
        val lowercaseQuery = query.lowercase()
        return allKosProperties.filter { property ->
            property.name.lowercase().contains(lowercaseQuery) ||
            property.location.lowercase().contains(lowercaseQuery) ||
            property.type.name.lowercase().contains(lowercaseQuery) ||
            property.facilities.any { it.lowercase().contains(lowercaseQuery) }
        }
    }
    
    fun getPropertiesByType(type: KosType): List<KosProperty> {
        return allKosProperties.filter { it.type == type }
    }
    
    fun getPropertiesByPriceRange(minPrice: Int, maxPrice: Int): List<KosProperty> {
        return allKosProperties.filter { it.pricePerMonth in minPrice..maxPrice }
    }
    
    fun getRecentSearches(): List<String> {
        return listOf(
            "Kos Putri Mama Faiz",
            "Kos Putra Buk Mina", 
            "Kos Campur Pak Budi"
        )
    }
    
    // Reviews data
    private val allReviews = listOf(
        KosReview(1, "Sari Dewi", null, 5.0f, "Kos yang sangat nyaman dan bersih. Pemilik kos juga ramah dan fasilitas lengkap.", "2 hari lalu", true),
        KosReview(2, "Ahmad Rizki", null, 4.0f, "Lokasi strategis dekat kampus. WiFi cepat dan kamar luas.", "1 minggu lalu"),
        KosReview(3, "Maya Sari", null, 4.5f, "Fasilitas AC dan kamar mandi dalam sangat membantu. Recommended!", "2 minggu lalu", true),
        KosReview(4, "Budi Santoso", null, 3.5f, "Kos lumayan bagus tapi parkir agak sempit.", "3 minggu lalu"),
        KosReview(5, "Rina Putri", null, 5.0f, "Kos terbaik yang pernah saya tempati. Keamanan 24 jam dan lingkungan tenang.", "1 bulan lalu", true),
        KosReview(6, "Dedi Kurniawan", null, 4.0f, "Harga sesuai dengan fasilitas yang didapat. Dapur bersama cukup luas.", "1 bulan lalu"),
        KosReview(7, "Fitri Handayani", null, 4.5f, "Lokasi dekat dengan pusat perbelanjaan. Sangat nyaman untuk mahasiswa.", "2 bulan lalu"),
        KosReview(8, "Andi Pratama", null, 3.0f, "Kos standar dengan harga terjangkau. Cocok untuk budget mahasiswa.", "2 bulan lalu")
    )
    
    fun getReviewsByKosId(kosId: Int): List<KosReview> {
        // Simulate different reviews for different kos
        return when (kosId % 4) {
            0 -> allReviews.take(3)
            1 -> allReviews.drop(2).take(3)
            2 -> allReviews.drop(4).take(2)
            else -> allReviews.drop(6).take(2)
        }
    }
    
    fun getKosFacilities(kosId: Int): List<KosFacility> {
        val property = allKosProperties.find { it.id == kosId }
        return property?.facilities?.map { facility ->
            when (facility.lowercase()) {
                "wifi" -> KosFacility("WiFi", "wifi")
                "ac" -> KosFacility("AC", "ac")
                "kamar mandi dalam" -> KosFacility("Kamar Mandi Dalam", "bathroom")
                "dapur bersama" -> KosFacility("Dapur Bersama", "kitchen")
                "parkir motor" -> KosFacility("Parkir Motor", "parking")
                "security 24 jam" -> KosFacility("Security 24 Jam", "security")
                "laundry" -> KosFacility("Laundry", "laundry")
                "ruang belajar" -> KosFacility("Ruang Belajar", "study")
                "gym" -> KosFacility("Gym", "gym")
                "rooftop" -> KosFacility("Rooftop", "rooftop")
                "balkon" -> KosFacility("Balkon", "balcony")
                "dapur pribadi" -> KosFacility("Dapur Pribadi", "private_kitchen")
                else -> KosFacility(facility, "default")
            }
        } ?: emptyList()
    }
    
    fun getRecommendedKos(currentKosId: Int): List<KosProperty> {
        return allKosProperties.filter { it.id != currentKosId && it.isRecommended }.take(3)
    }
    
    // Favorite Kos Management
    private val _favoriteKosList = mutableListOf<FavoriteKos>()
    
    fun addToFavorite(kosProperty: KosProperty): Boolean {
        if (!isFavorite(kosProperty.id)) {
            _favoriteKosList.add(
                FavoriteKos(
                    id = _favoriteKosList.size + 1,
                    kosProperty = kosProperty,
                    dateAdded = "Hari ini"
                )
            )
            return true
        }
        return false
    }
    
    fun removeFromFavorite(kosId: Int): Boolean {
        val favoriteKos = _favoriteKosList.find { it.kosProperty.id == kosId }
        return if (favoriteKos != null) {
            _favoriteKosList.remove(favoriteKos)
            true
        } else {
            false
        }
    }
    
    fun isFavorite(kosId: Int): Boolean {
        return _favoriteKosList.any { it.kosProperty.id == kosId && it.status == FavoriteStatus.ACTIVE }
    }
    
    fun getFavoriteKosList(): List<FavoriteKos> {
        return _favoriteKosList.filter { it.status == FavoriteStatus.ACTIVE }
    }
    
    fun getFavoriteKosHistory(): List<FavoriteKos> {
        return _favoriteKosList.filter { it.status == FavoriteStatus.REMOVED }
    }
    
    // Initialize with some sample favorites
    init {
        // Add some sample favorites
        addToFavorite(allKosProperties[0]) // Kos Putri Mama Faiz
        addToFavorite(allKosProperties[4]) // Kos Putra Mawar
        addToFavorite(allKosProperties[5]) // Kos Elite Padang Utara
    }
}

package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class ServiceCategory(
    val id: String,
    val titleFr: String,
    val titleMg: String,
    val badgeColor: Color
) {
    ALL("all", "Tous", "Rehetra", EmeraldPrimaryLight),
    RESTAURANT("restaurant", "Restaurants", "Fisakafoanana", CategoryRestaurant),
    HOTEL("hotel", "Hôtels", "Hotely fandraisam-bahiny", CategoryHotel),
    PHARMACY("pharmacy", "Pharmacies", "Farmasia", CategoryPharmacy),
    SHOP("shop", "Commerces", "Mpivarotra", CategoryShop),
    OTHER("other", "Autres services", "Sampan-draharaha hafa", CategoryOther),
    TRANSPORT("transport", "Transport", "Fitaterana", CategoryTransport)
}

data class Review(
    val id: String,
    val authorName: String,
    val rating: Float,
    val date: String,
    val comment: String
)

data class MenuItem(
    val id: String,
    val name: String,
    val categorySection: String, // "Entrées", "Spécialités Malagasy", "Plats du Chef", "Desserts", "Boissons"
    val description: String,
    val priceAr: Int,
    val tag: String? = null // "Spécialité", "Populaire", "Chef", "Bio"
)

data class HotelRoomItem(
    val id: String,
    val name: String,
    val roomType: String, // "Chambre Standard", "Suite Panoramique", "Bungalow Familial", "Suite Présidentielle"
    val capacity: String,
    val pricePerNightAr: Int,
    val amenities: List<String>,
    val viewType: String,
    val isAvailable: Boolean = true
)

data class Establishment(
    val id: String,
    val name: String,
    val category: ServiceCategory,
    val rating: Float,
    val reviewCount: Int,
    val distanceKm: Double,
    val shortAddress: String,
    val fullAddress: String,
    val phone: String,
    val isOpen: Boolean,
    val openingHours: String,
    val priceLevel: String, // "Ar", "Ar Ar", "Ar Ar Ar"
    val description: String,
    val services: List<String>,
    val lat: Double,
    val lng: Double,
    val isFavorite: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val promoBadge: String? = null,
    val menuCatalog: List<MenuItem> = emptyList(),
    val roomCatalog: List<HotelRoomItem> = emptyList()
)

enum class VehicleType(
    val id: String,
    val labelFr: String,
    val labelMg: String,
    val capacity: String,
    val basePriceEstimate: Int
) {
    CAR("car", "Voiture", "Fiara", "4 passagers", 7000),
    MOTO("moto", "Moto / Tuk-Tuk", "Môtô / Tuk-Tuk", "1 passager", 4000),
    DELIVERY("delivery", "Livraison / Colis", "Fandefasana entana", "Jusqu'à 25 kg", 5000)
}

enum class RideStatus {
    IDLE,
    SEARCHING,
    OFFERS_RECEIVED,
    ACCEPTED,
    DRIVER_ON_WAY,
    DRIVER_ARRIVED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

data class DriverOffer(
    val id: String,
    val driverName: String,
    val rating: Float,
    val tripsCount: Int,
    val vehicleModel: String,
    val licensePlate: String,
    val phone: String,
    val distanceEtaMin: Int,
    val offeredPriceAr: Int
)

data class RideRequest(
    val pickupName: String,
    val destinationName: String,
    val vehicleType: VehicleType,
    val proposedPriceAr: Int,
    val passengerCount: Int = 1,
    val hasAC: Boolean = false,
    val hasLuggage: Boolean = false,
    val note: String = ""
)

data class RideHistoryItem(
    val id: String,
    val date: String,
    val pickup: String,
    val destination: String,
    val vehicleType: VehicleType,
    val driverName: String,
    val priceAr: Int,
    val status: String,
    val paymentMethod: String,
    val ratingGiven: Int = 5
)

data class VisitedServiceItem(
    val id: String,
    val establishmentId: String,
    val name: String,
    val category: ServiceCategory,
    val date: String,
    val actionTaken: String // "Appelé", "Itinéraire", "Visité"
)

enum class NotificationType {
    RIDE,
    ESTABLISHMENT,
    PROMO
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class UserRole(val labelFr: String, val labelMg: String) {
    CLIENT("Client / Passager", "Mpanjifa / Mpandeha"),
    DRIVER("Chauffeur Transport", "Mpamily Fitaterana"),
    MERCHANT("Prestataire / Commerce", "Mpandraharaha / Varotra")
}

enum class AppLanguage(val code: String, val label: String) {
    FR("fr", "Français"),
    MG("mg", "Malagasy")
}

enum class PaymentMethod(val id: String, val label: String, val providerColor: Color) {
    CASH("cash", "Espèces (Ariary)", EmeraldPrimaryLight),
    MVOLA("mvola", "MVola (Telma)", MVolaYellow),
    ORANGE_MONEY("orange", "Orange Money", OrangeMoney),
    AIRTEL_MONEY("airtel", "Airtel Money", AirtelRed),
    CARD("card", "Carte bancaire", CyanTertiaryLight)
}

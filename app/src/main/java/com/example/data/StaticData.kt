package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object StaticData {

    val INITIAL_ESTABLISHMENTS = listOf(
        Establishment(
            id = "est-1",
            name = "Café de la Gare Soarano",
            category = ServiceCategory.RESTAURANT,
            rating = 4.8f,
            reviewCount = 342,
            distanceKm = 0.4,
            shortAddress = "Analakely, Soarano",
            fullAddress = "Gare Soarano, Avenue de la Libération, Antananarivo 101",
            phone = "+261 20 22 214 00",
            isOpen = true,
            openingHours = "07:00 - 22:30",
            priceLevel = "Ar Ar Ar",
            description = "Restaurant et brasserie historique au cœur de l'ancienne gare de Soarano. Cuisine française raffinée et spécialités malagasy renommées.",
            services = listOf("Terrasse", "Wi-Fi Gratuit", "Cocktails", "Parking sécurisé", "Réservation"),
            lat = -18.9056,
            lng = 47.5255,
            isFavorite = true,
            promoBadge = "-10% le midi",
            reviews = listOf(
                Review("r-1", "Hery Razafy", 5.0f, "Hier", "Cadre historique exceptionnel, zébu braisé fondant et service rapide."),
                Review("r-2", "Sarah V.", 4.5f, "Il y a 3 jours", "Très bon café et pâtisseries fraîches le matin. Ambiance calme.")
            ),
            menuCatalog = listOf(
                MenuItem("m-1", "Sambos artisanaux au Zébu & Achards Mangue", "Entrées", "3 pièces croustillantes farcies au zébu parfumé aux herbes des hauts plateaux", 8000, "Populaire"),
                MenuItem("m-2", "Carpaccio de filet de Zébu au poivre vert", "Entrées", "Fines lamelles de zébu mariné au poivre vert frais et huile d'olive vierge", 12000, "Chef"),
                MenuItem("m-3", "Salade d'avocats et crevettes sauvages de Majunga", "Entrées", "Avocats onctueux locaux, crevettes sautées et vinaigrette aux fruits de la passion", 14000, null),
                MenuItem("m-4", "Romazava Royal au Zébu & Brèdes Mafana", "Spécialités Malagasy", "Le plat national emblématique : viande de zébu mijotée avec mélange de brèdes traditionnelles", 18000, "Spécialité"),
                MenuItem("m-5", "Ravitoto sy Henakisoa à l'ail et coco", "Spécialités Malagasy", "Feuilles de manioc pilées mijotées à la viande de porc et touche de lait de coco", 17000, "Spécialité"),
                MenuItem("m-6", "Hen'omby ritra aux oignons caramélisés", "Spécialités Malagasy", "Viande de zébu confite à l'étouffée servie avec riz blanc vary gasy et rougail tomate", 16500, null),
                MenuItem("m-7", "Filet de Zébu aux Morilles & Poivre Sauvage Voatsiperifery", "Plats du Chef", "Pièce maîtresse tendre saisie à la flamme, sauce crémeuse aux morilles et épices rares", 24000, "Chef"),
                MenuItem("m-8", "Pavé de Thon rouge snacké de Nosy Be", "Plats du Chef", "Thon mi-cuit en croûte de sésame noir, mousseline de patates douces vanillées", 22000, null),
                MenuItem("m-9", "Magret de canard laqué au miel d'eucalyptus", "Plats du Chef", "Canard fermier des hauts plateaux laqué au miel local et purée de manioc", 26000, null),
                MenuItem("m-10", "Tartelette ganache chocolat grand cru Ambanja", "Desserts", "Pâte sablée pur beurre, chocolat noir 72% de la vallée du Sambirano", 9000, "Populaire"),
                MenuItem("m-11", "Crème brûlée à la vanille bourbon de Sambava", "Desserts", "Crème onctueuse infusée aux gousses de vanille bourbon fraîches et caramel craquant", 8500, "Chef"),
                MenuItem("m-12", "Assiette gourmande de Mofo Gasy & Ramanonaka", "Desserts", "Beignets de riz traditionnels tièdes accompagnés de miel sauvage et confiture ananas", 6000, "Spécialité"),
                MenuItem("m-13", "Jus frais pressé Corossol & Fruit de la passion", "Boissons", "Cocktail de fruits 100% naturel sans sucre ajouté, préparé à la minute", 5000, "Bio"),
                MenuItem("m-14", "Cocktail Baobab & Rhum Dzama", "Boissons", "Pulpe de pain de singe (baobab), rhum ambré malagasy et zeste de combava", 11000, null),
                MenuItem("m-15", "Ranon'ampango traditionnel glacé", "Boissons", "Infusion d'eau de riz torréfié traditionnelle rafraîchissante", 2500, "Spécialité")
            )
        ),
        Establishment(
            id = "est-2",
            name = "Pharmacie Principale Analakely",
            category = ServiceCategory.PHARMACY,
            rating = 4.9f,
            reviewCount = 512,
            distanceKm = 0.2,
            shortAddress = "Avenue de l'Indépendance",
            fullAddress = "14 Avenue de l'Indépendance, Analakely, Antananarivo",
            phone = "+261 20 22 232 45",
            isOpen = true,
            openingHours = "Ouvert 24h/24 - 7j/7",
            priceLevel = "Ar",
            description = "Pharmacie de garde centrale d'Analakely. Large stock de médicaments, premiers secours, matériel médical et conseils personnalisés.",
            services = listOf("Garde 24/7", "Paiement MVola", "Tension artérielle", "Livraison urgente"),
            lat = -18.9082,
            lng = 47.5268,
            isFavorite = true,
            promoBadge = "Garde 24/7",
            reviews = listOf(
                Review("r-3", "Mialy Rakoto", 5.0f, "Cette semaine", "Pharmacie de confiance ouverte en pleine nuit, pharmaciens très disponibles.")
            )
        ),
        Establishment(
            id = "est-3",
            name = "Hôtel Carlton Madagascar",
            category = ServiceCategory.HOTEL,
            rating = 4.7f,
            reviewCount = 280,
            distanceKm = 1.6,
            shortAddress = "Anosy, Bord du Lac",
            fullAddress = "Rue Pierre Stibbe, Anosy, Antananarivo 101",
            phone = "+261 20 22 260 60",
            isOpen = true,
            openingHours = "Réception 24h/24",
            priceLevel = "Ar Ar Ar",
            description = "Hôtel 5 étoiles emblématique avec vue panoramique sur le lac Anosy et le Palais de la Reine. Piscine semi-olympique et centre de conférences.",
            services = listOf("Piscine", "Spa & Sauna", "Salle de sport", "Navette Aéroport Ivato", "Restaurant panoramique"),
            lat = -18.9189,
            lng = 47.5218,
            isFavorite = false,
            promoBadge = "Vue Lac Anosy",
            reviews = listOf(
                Review("r-4", "Patrick D.", 5.0f, "Il y a 5 jours", "Vue spectaculaire sur le lac et le Rova. Brunch du dimanche à recommander.")
            ),
            roomCatalog = listOf(
                HotelRoomItem(
                    id = "rm-1",
                    name = "Chambre Supérieure Vue Lac Anosy",
                    roomType = "Chambre Supérieure",
                    capacity = "2 adultes + 1 enfant",
                    pricePerNightAr = 180000,
                    amenities = listOf("Lit King Size", "Balcon vue Lac Anosy", "Climatisation", "Wi-Fi Très Haut Débit", "Petit-déjeuner buffet inclus", "Minibar"),
                    viewType = "Vue Lac & Monument aux Morts"
                ),
                HotelRoomItem(
                    id = "rm-2",
                    name = "Chambre Exécutive Affaires",
                    roomType = "Chambre Affaires",
                    capacity = "2 adultes",
                    pricePerNightAr = 220000,
                    amenities = listOf("Grand bureau de travail", "Accès Salon Club Exécutif", "Machine expresso en chambre", "Service repassage", "Coffre-fort"),
                    viewType = "Vue Haute Ville & Rova"
                ),
                HotelRoomItem(
                    id = "rm-3",
                    name = "Suite Junior Panoramique",
                    roomType = "Suite Junior",
                    capacity = "3 adultes",
                    pricePerNightAr = 340000,
                    amenities = listOf("Grand salon privatif", "Baignoire d'angle balnéo", "Terrasse panoramique à 180°", "Plateau de bienvenue fruits exotiques", "Peignoirs en soie"),
                    viewType = "Vue Panoramique Lac & Collines"
                ),
                HotelRoomItem(
                    id = "rm-4",
                    name = "Suite Présidentielle Haute Distinction",
                    roomType = "Suite Présidentielle",
                    capacity = "4 adultes",
                    pricePerNightAr = 580000,
                    amenities = listOf("2 chambres indépendantes", "Salle à manger privée 8 places", "Majordome dédié 24/7", "Transfert aéroport Ivato en berline de luxe", "Accès illimité Spa"),
                    viewType = "Vue Panoramique Complète"
                )
            )
        ),
        Establishment(
            id = "est-4",
            name = "Supermaki Ankorondrano",
            category = ServiceCategory.SHOP,
            rating = 4.5f,
            reviewCount = 195,
            distanceKm = 1.5,
            shortAddress = "Route des Hydrocarbures",
            fullAddress = "Zone Ankorondrano, Route des Hydrocarbures, Antananarivo",
            phone = "+261 20 22 491 10",
            isOpen = true,
            openingHours = "08:30 - 19:30",
            priceLevel = "Ar Ar",
            description = "Supermarché moderne proposant produits alimentaires frais de Madagascar, boucherie, boulangerie et produits importés.",
            services = listOf("Drive", "Paiement Mobile Money", "Boulangerie chaude", "Climatisation"),
            lat = -18.8890,
            lng = 47.5280,
            isFavorite = false,
            promoBadge = "Promos fruits locaux",
            reviews = listOf(
                Review("r-5", "Noro A.", 4.0f, "Il y a 1 semaine", "Très propre, rayons bien garnis et passage en caisse rapide.")
            )
        ),
        Establishment(
            id = "est-5",
            name = "La Varangue Restaurant & Musée",
            category = ServiceCategory.RESTAURANT,
            rating = 4.8f,
            reviewCount = 410,
            distanceKm = 0.8,
            shortAddress = "Isoraka, Printsy Ratsimamanga",
            fullAddress = "17 Rue Printsy Ratsimamanga, Isoraka, Antananarivo",
            phone = "+261 20 22 273 97",
            isOpen = true,
            openingHours = "11:30 - 14:30 · 18:30 - 22:30",
            priceLevel = "Ar Ar Ar",
            description = "Adresse gastronomique de référence entourée d'objets d'art malgaches et d'antiquités. Cuisine d'auteur raffinée aux saveurs de l'île.",
            services = listOf("Jardin fleuri", "Cave à vin", "Objets de collection", "Service voiturier"),
            lat = -18.9125,
            lng = 47.5230,
            isFavorite = true,
            promoBadge = "Coup de cœur",
            reviews = listOf(
                Review("r-6", "Jean-Marc L.", 5.0f, "Hier", "Une des meilleures tables de Tana. Filet de zébu aux morilles remarquable.")
            ),
            menuCatalog = listOf(
                MenuItem("lv-1", "Foie gras de canard poêlé à la vanille bourbon", "Entrées", "Foie gras fermier caramélisé à la gousse de Sambava et brioche toastée", 25000, "Chef"),
                MenuItem("lv-2", "Tartare d'espadon sauvage aux agrumes de l'Est", "Entrées", "Espadon frais pêché au large de Manakara assaisonné au combava et gingembre", 16000, "Populaire"),
                MenuItem("lv-3", "Tournedos Rossini au Zébu de Belo-sur-Tsiribihina", "Plats du Chef", "Coeur de filet de zébu garni d'escalope de foie gras et réduction au Porto", 29000, "Chef"),
                MenuItem("lv-4", "Camarons géants de Morondava grillés à l'ail vert", "Plats du Chef", "Grosses gambas d'eau douce rôties au beurre d'herbes et riz rouge safrané", 34000, "Spécialité"),
                MenuItem("lv-5", "Soufflé chaud au chocolat noir de Madagascar", "Desserts", "Chocolat grand cru coulant au coeur, glace artisanale vanille", 12000, "Populaire")
            )
        ),
        Establishment(
            id = "est-6",
            name = "Hôtel Colbert & Spa",
            category = ServiceCategory.HOTEL,
            rating = 4.6f,
            reviewCount = 320,
            distanceKm = 0.6,
            shortAddress = "Antaninarenina",
            fullAddress = "Rue Lalana Solombavambahoaka Frantsay, Antaninarenina, Antananarivo",
            phone = "+261 20 22 202 02",
            isOpen = true,
            openingHours = "Réception 24h/24",
            priceLevel = "Ar Ar Ar",
            description = "Au cœur du quartier d'affaires d'Antaninarenina. Spa Balnéoforme, pâtisserie réputée et restaurants haut de gamme.",
            services = listOf("Balnéothérapie", "Pâtisserie Colbert", "Casino", "Wi-Fi Haut débit"),
            lat = -18.9135,
            lng = 47.5250,
            isFavorite = false,
            promoBadge = null,
            reviews = listOf(
                Review("r-7", "Toky R.", 4.5f, "Il y a 4 jours", "Pâtisseries délicieuses et spa relaxant après une journée en ville.")
            ),
            roomCatalog = listOf(
                HotelRoomItem(
                    id = "col-1",
                    name = "Chambre Confort Antaninarenina",
                    roomType = "Chambre Confort",
                    capacity = "2 adultes",
                    pricePerNightAr = 145000,
                    amenities = listOf("Lit Queen Size", "Insonorisation complète", "TV Satellite", "Climatisation", "Wi-Fi Haut débit"),
                    viewType = "Vue Quartier Historique"
                ),
                HotelRoomItem(
                    id = "col-2",
                    name = "Chambre Deluxe Balnéoforme & Spa",
                    roomType = "Chambre Deluxe",
                    capacity = "2 adultes",
                    pricePerNightAr = 195000,
                    amenities = listOf("Accès libre au Spa Balnéoforme", "Piscine chauffée", "Hammam & Sauna", "Peignoirs & Chaussons", "Petit-déjeuner buffet inclus"),
                    viewType = "Vue Patio & Jardin"
                ),
                HotelRoomItem(
                    id = "col-3",
                    name = "Grande Suite Colbert Prestige",
                    roomType = "Suite Prestige",
                    capacity = "3 adultes",
                    pricePerNightAr = 320000,
                    amenities = listOf("Salon privé séparé", "Baignoire à remous hydro-massante", "Service en chambre VIP 24h/24", "Machine à café Nespresso"),
                    viewType = "Vue Panoramique Ville Haute"
                )
            )
        ),
        Establishment(
            id = "est-7",
            name = "Pharmacie Métropole Isoraka",
            category = ServiceCategory.PHARMACY,
            rating = 4.7f,
            reviewCount = 140,
            distanceKm = 0.9,
            shortAddress = "Isoraka centre",
            fullAddress = "Rue Pasteur, Isoraka, Antananarivo 101",
            phone = "+261 20 22 200 45",
            isOpen = false,
            openingHours = "08:00 - 18:30 (Ouvre à 08:00)",
            priceLevel = "Ar",
            description = "Pharmacie de quartier avec comptoir homéopathie, parapharmacie et conseils santé personnalisés.",
            services = listOf("Parapharmacie", "Produits bébé", "Paiement carte & mobile"),
            lat = -18.9110,
            lng = 47.5215,
            isFavorite = false,
            promoBadge = null,
            reviews = listOf()
        ),
        Establishment(
            id = "est-8",
            name = "BMOI Siège & Distributeur GAB",
            category = ServiceCategory.OTHER,
            rating = 4.4f,
            reviewCount = 88,
            distanceKm = 0.5,
            shortAddress = "Place de l'Indépendance",
            fullAddress = "Place de l'Indépendance, Antaninarenina, Antananarivo",
            phone = "+261 20 22 346 00",
            isOpen = true,
            openingHours = "08:00 - 16:00 · GAB 24/7",
            priceLevel = "Ar",
            description = "Agence bancaire et distributeur automatique de billets accessible 24h/24 (cartes Visa, Mastercard, opérations de change).",
            services = listOf("GAB 24h/24", "Change devises", "Opérations professionnelles", "Sécurisé"),
            lat = -18.9140,
            lng = 47.5270,
            isFavorite = false,
            promoBadge = "GAB Disponible",
            reviews = listOf()
        ),
        Establishment(
            id = "est-9",
            name = "Station TotalEnergies Behoririka",
            category = ServiceCategory.OTHER,
            rating = 4.3f,
            reviewCount = 110,
            distanceKm = 0.7,
            shortAddress = "Behoririka Lac",
            fullAddress = "Rond-point Behoririka, Antananarivo 101",
            phone = "+261 20 22 254 30",
            isOpen = true,
            openingHours = "24h/24 - 7j/7",
            priceLevel = "Ar",
            description = "Station service avec carburants, boutique Bonjour, vidange rapide, gonflage pneus et lavage auto.",
            services = listOf("Carburant 24/7", "Boutique Bonjour", "Lavage auto", "Pression pneus"),
            lat = -18.9020,
            lng = 47.5310,
            isFavorite = false,
            promoBadge = "Service 24/7",
            reviews = listOf()
        ),
        Establishment(
            id = "est-10",
            name = "Clinique des Sœurs Ankadifotsy",
            category = ServiceCategory.OTHER,
            rating = 4.9f,
            reviewCount = 260,
            distanceKm = 1.4,
            shortAddress = "Ankadifotsy",
            fullAddress = "Route d'Ankadifotsy, Antananarivo",
            phone = "+261 20 22 235 55",
            isOpen = true,
            openingHours = "Urgences 24h/24",
            priceLevel = "Ar Ar",
            description = "Centre médico-chirurgical moderne avec service d'urgences, maternité, radiologie et laboratoire d'analyses médicales.",
            services = listOf("Urgences 24/7", "Laboratoire", "Radiologie", "Chambres climatisées"),
            lat = -18.8980,
            lng = 47.5340,
            isFavorite = false,
            promoBadge = "Urgences médicales",
            reviews = listOf()
        ),
        Establishment(
            id = "est-11",
            name = "Jumbo Score Tanjombato",
            category = ServiceCategory.SHOP,
            rating = 4.6f,
            reviewCount = 480,
            distanceKm = 4.5,
            shortAddress = "Tanjombato",
            fullAddress = "Centre Commercial Smart Tanjombato, RN7",
            phone = "+261 20 22 462 00",
            isOpen = true,
            openingHours = "08:30 - 20:00",
            priceLevel = "Ar Ar",
            description = "Grand hypermarché et galerie marchande comprenant mode, high-tech, alimentation et restauration rapide.",
            services = listOf("Grand parking", "Galerie marchande", "Espace enfants", "Pharmacie intégrée"),
            lat = -18.9560,
            lng = 47.5210,
            isFavorite = false,
            promoBadge = "Galerie marchande",
            reviews = listOf()
        )
    )

    val INITIAL_DRIVERS = listOf(
        DriverOffer(
            id = "drv-1",
            driverName = "Jean-Luc R.",
            rating = 4.9f,
            tripsCount = 1240,
            vehicleModel = "Peugeot 206 (Blanche)",
            licensePlate = "4521 TAB",
            phone = "+261 34 11 223 34",
            distanceEtaMin = 3,
            offeredPriceAr = 7000
        ),
        DriverOffer(
            id = "drv-2",
            driverName = "Hery Ravelo",
            rating = 4.8f,
            tripsCount = 890,
            vehicleModel = "Toyota Yaris (Grise)",
            licensePlate = "8812 TBB",
            phone = "+261 32 44 556 67",
            distanceEtaMin = 5,
            offeredPriceAr = 8000
        ),
        DriverOffer(
            id = "drv-3",
            driverName = "Mamy Andry",
            rating = 4.9f,
            tripsCount = 2100,
            vehicleModel = "Moto Boxer 150 (Noire)",
            licensePlate = "1209 TAA",
            phone = "+261 33 77 889 90",
            distanceEtaMin = 2,
            offeredPriceAr = 4000
        ),
        DriverOffer(
            id = "drv-4",
            driverName = "Fanja N.",
            rating = 4.9f,
            tripsCount = 750,
            vehicleModel = "Renault Clio 4 (Rouge)",
            licensePlate = "6734 TBC",
            phone = "+261 34 99 001 12",
            distanceEtaMin = 6,
            offeredPriceAr = 7500
        )
    )

    val INITIAL_RIDE_HISTORY = listOf(
        RideHistoryItem(
            id = "rh-1",
            date = "Aujourd'hui, 09:15",
            pickup = "Analakely (Avenue de l'Indépendance)",
            destination = "Carlton Madagascar (Anosy)",
            vehicleType = VehicleType.CAR,
            driverName = "Jean-Luc R.",
            priceAr = 7000,
            status = "Terminée",
            paymentMethod = "MVola",
            ratingGiven = 5
        ),
        RideHistoryItem(
            id = "rh-2",
            date = "18 Oct 2026, 17:40",
            pickup = "Ankorondrano (Zone Galaxy)",
            destination = "Isoraka (La Varangue)",
            vehicleType = VehicleType.CAR,
            driverName = "Fanja N.",
            priceAr = 8500,
            status = "Terminée",
            paymentMethod = "Espèces",
            ratingGiven = 5
        ),
        RideHistoryItem(
            id = "rh-3",
            date = "15 Oct 2026, 12:10",
            pickup = "Gare Soarano",
            destination = "Clinique des Sœurs Ankadifotsy",
            vehicleType = VehicleType.MOTO,
            driverName = "Mamy Andry",
            priceAr = 4000,
            status = "Terminée",
            paymentMethod = "Orange Money",
            ratingGiven = 5
        )
    )

    val INITIAL_VISITED_SERVICES = listOf(
        VisitedServiceItem(
            id = "vs-1",
            establishmentId = "est-2",
            name = "Pharmacie Principale Analakely",
            category = ServiceCategory.PHARMACY,
            date = "Aujourd'hui, 10:30",
            actionTaken = "Appelé"
        ),
        VisitedServiceItem(
            id = "vs-2",
            establishmentId = "est-1",
            name = "Café de la Gare Soarano",
            category = ServiceCategory.RESTAURANT,
            date = "Hier, 19:45",
            actionTaken = "Itinéraire"
        ),
        VisitedServiceItem(
            id = "vs-3",
            establishmentId = "est-4",
            name = "Supermaki Ankorondrano",
            category = ServiceCategory.SHOP,
            date = "17 Oct 2026",
            actionTaken = "Visité"
        )
    )

    val INITIAL_NOTIFICATIONS = listOf(
        NotificationItem(
            id = "notif-1",
            title = "Course acceptée !",
            message = "Jean-Luc R. a accepté votre proposition de 7 000 Ar en Peugeot 206.",
            timeAgo = "Il y a 5 min",
            type = NotificationType.RIDE,
            isRead = false
        ),
        NotificationItem(
            id = "notif-2",
            title = "Nouveau partenaire à proximité",
            message = "Pharmacie Principale Analakely offre le service de garde 24/7.",
            timeAgo = "Il y a 2h",
            type = NotificationType.ESTABLISHMENT,
            isRead = false
        ),
        NotificationItem(
            id = "notif-3",
            title = "Offre spéciale Guide Ride",
            message = "Bénéficiez de zéro frais de service sur votre première course via MVola.",
            timeAgo = "Hier",
            type = NotificationType.PROMO,
            isRead = true
        )
    )
}

class AppRepository {
    private val _establishments = MutableStateFlow(StaticData.INITIAL_ESTABLISHMENTS)
    val establishments: StateFlow<List<Establishment>> = _establishments.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ServiceCategory.ALL)
    val selectedCategory: StateFlow<ServiceCategory> = _selectedCategory.asStateFlow()

    private val _searchRadiusKm = MutableStateFlow(2.0)
    val searchRadiusKm: StateFlow<Double> = _searchRadiusKm.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.CLIENT)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _appLanguage = MutableStateFlow(AppLanguage.FR)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _rideHistory = MutableStateFlow(StaticData.INITIAL_RIDE_HISTORY)
    val rideHistory: StateFlow<List<RideHistoryItem>> = _rideHistory.asStateFlow()

    private val _visitedServices = MutableStateFlow(StaticData.INITIAL_VISITED_SERVICES)
    val visitedServices: StateFlow<List<VisitedServiceItem>> = _visitedServices.asStateFlow()

    private val _notifications = MutableStateFlow(StaticData.INITIAL_NOTIFICATIONS)
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Transport live state
    private val _activeRide = MutableStateFlow<RideRequest?>(null)
    val activeRide: StateFlow<RideRequest?> = _activeRide.asStateFlow()

    private val _rideStatus = MutableStateFlow(RideStatus.IDLE)
    val rideStatus: StateFlow<RideStatus> = _rideStatus.asStateFlow()

    private val _receivedOffers = MutableStateFlow<List<DriverOffer>>(emptyList())
    val receivedOffers: StateFlow<List<DriverOffer>> = _receivedOffers.asStateFlow()

    private val _acceptedDriver = MutableStateFlow<DriverOffer?>(null)
    val acceptedDriver: StateFlow<DriverOffer?> = _acceptedDriver.asStateFlow()

    // Driver Mode State (Chauffeur view)
    private val _isDriverOnline = MutableStateFlow(true)
    val isDriverOnline: StateFlow<Boolean> = _isDriverOnline.asStateFlow()

    private val _driverTodayEarningsAr = MutableStateFlow(48000)
    val driverTodayEarningsAr: StateFlow<Int> = _driverTodayEarningsAr.asStateFlow()

    private val _driverCompletedTrips = MutableStateFlow(7)
    val driverCompletedTrips: StateFlow<Int> = _driverCompletedTrips.asStateFlow()

    fun setCategory(category: ServiceCategory) {
        _selectedCategory.value = category
    }

    fun setRadius(radius: Double) {
        _searchRadiusKm.value = radius
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(id: String) {
        _establishments.update { list ->
            list.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    fun setLanguage(language: AppLanguage) {
        _appLanguage.value = language
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun markAllNotificationsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    fun addVisitedAction(establishmentId: String, action: String) {
        val est = _establishments.value.find { it.id == establishmentId } ?: return
        val newItem = VisitedServiceItem(
            id = "vs-${System.currentTimeMillis()}",
            establishmentId = est.id,
            name = est.name,
            category = est.category,
            date = "À l'instant",
            actionTaken = action
        )
        _visitedServices.update { listOf(newItem) + it }
    }

    // Transport Actions (Guide Ride flow)
    fun createRideRequest(request: RideRequest) {
        _activeRide.value = request
        _rideStatus.value = RideStatus.SEARCHING
        _receivedOffers.value = emptyList()
        _acceptedDriver.value = null
    }

    fun populateDriverOffers() {
        _rideStatus.value = RideStatus.OFFERS_RECEIVED
        _receivedOffers.value = StaticData.INITIAL_DRIVERS
    }

    fun counterOfferPrice(offerId: String, newPriceAr: Int) {
        _receivedOffers.update { list ->
            list.map { if (it.id == offerId) it.copy(offeredPriceAr = newPriceAr) else it }
        }
    }

    fun acceptDriver(offer: DriverOffer) {
        _acceptedDriver.value = offer
        _rideStatus.value = RideStatus.DRIVER_ON_WAY
    }

    fun updateRideStatus(status: RideStatus) {
        _rideStatus.value = status
    }

    fun completeRide(paymentMethod: PaymentMethod, rating: Int) {
        val ride = _activeRide.value
        val driver = _acceptedDriver.value
        if (ride != null && driver != null) {
            val history = RideHistoryItem(
                id = "rh-${System.currentTimeMillis()}",
                date = "À l'instant",
                pickup = ride.pickupName,
                destination = ride.destinationName,
                vehicleType = ride.vehicleType,
                driverName = driver.driverName,
                priceAr = driver.offeredPriceAr,
                status = "Terminée",
                paymentMethod = paymentMethod.label,
                ratingGiven = rating
            )
            _rideHistory.update { listOf(history) + it }
        }
        _rideStatus.value = RideStatus.COMPLETED
    }

    fun resetRide() {
        _activeRide.value = null
        _rideStatus.value = RideStatus.IDLE
        _receivedOffers.value = emptyList()
        _acceptedDriver.value = null
    }

    fun toggleDriverOnline() {
        _isDriverOnline.value = !_isDriverOnline.value
    }
}

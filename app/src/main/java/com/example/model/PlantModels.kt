package com.example.model

enum class PlantType(
    val displayName: String,
    val shortName: String,
    val wateringIntervalHours: Int,
    val sunlightNeeds: String,
    val careTip: String,
    val ecoFactHighlight: String
) {
    SUCCULENT(
        displayName = "Suculenta Jade",
        shortName = "Suculenta",
        wateringIntervalHours = 240, // 10 días (preservado)
        sunlightNeeds = "Luz brillante indirecta",
        careTip = "Deja que la tierra se seque por completo entre riegos para proteger las raíces.",
        ecoFactHighlight = "¡Almacena agua en sus hojas gruesas, igual que los jardines urbanos inteligentes!"
    ),
    FERN(
        displayName = "Helecho de Boston",
        shortName = "Helecho",
        wateringIntervalHours = 72, // 3 días (preservado)
        sunlightNeeds = "Sombra filtrada y humedad",
        careTip = "Mantén la tierra ligeramente húmeda y rocía sus hojas en días cálidos.",
        ecoFactHighlight = "Purificadores naturales que refrescan interiores y mejoran el aire de la ciudad."
    ),
    CACTUS(
        displayName = "Cactus del Desierto",
        shortName = "Cactus",
        wateringIntervalHours = 336, // 14 días (preservado)
        sunlightNeeds = "Ventana muy soleada",
        careTip = "Necesita muy poca agua: espera a que la tierra esté completamente seca.",
        ecoFactHighlight = "Maestro del ahorro de agua que inspira parques urbanos sostenibles."
    ),
    FLOWERING(
        displayName = "Planta con Flor",
        shortName = "Con Flor",
        wateringIntervalHours = 96, // 4 días (preservado)
        sunlightNeeds = "Luz cálida de mañana",
        careTip = "Toca la superficie; riégala cuando los primeros centímetros de tierra estén secos.",
        ecoFactHighlight = "¡Las flores atraen polinizadores urbanos como abejas y mariposas (ODS 15)!"
    );

    companion object {
        fun fromName(name: String): PlantType =
            entries.firstOrNull { it.name == name } ?: SUCCULENT
    }
}

enum class PlantMood(
    val label: String,
    val statusHeadline: String,
    val avatarSpeech: String
) {
    HEALTHY(
        label = "Sana y Feliz",
        statusHeadline = "¡Creciendo feliz bajo el sol!",
        avatarSpeech = "¡Me siento fresca, verde y lista para limpiar el aire!"
    ),
    THIRSTY(
        label = "Con Sed",
        statusHeadline = "Necesita agua pronto",
        avatarSpeech = "Mi tierra se está secando… ¡un chorrito de agua me vendría genial!"
    ),
    CRITICAL(
        label = "Crítica y Seca",
        statusHeadline = "¡Cuidado urgente!",
        avatarSpeech = "¡Oh no, mis hojas se marchitan! ¡Por favor riégame ahora mismo!"
    ),
    GROWING(
        label = "Creciendo y Floreciendo",
        statusHeadline = "¡Brillando con agua fresca!",
        avatarSpeech = "¡Qué rico! ¡El agua fresca me ayuda a crecer fuerte y florecer!"
    )
}

enum class SdgTag(val code: String, val title: String) {
    SDG11("ODS 11", "Ciudades y Comunidades Sostenibles"),
    SDG15("ODS 15", "Vida de Ecosistemas Terrestres")
}

enum class NotificationCategory(val label: String) {
    WATERING("Recordatorio de Riego"),
    SDG11("Consejo ODS 11"),
    SDG15("Consejo ODS 15")
}

data class EcoFact(
    val id: Int,
    val title: String,
    val summary: String,
    val sdg: SdgTag,
    val actionTip: String
)

val ALL_ECO_FACTS: List<EcoFact> = listOf(
    EcoFact(
        id = 1,
        title = "Árboles que Refrescan la Ciudad",
        summary = "Los árboles urbanos y las plantas en balcones pueden refrescar los barrios entre 2 °C y 5 °C gracias a la sombra y la evaporación natural.",
        sdg = SdgTag.SDG11,
        actionTip = "Coloca tu planta cerca de una ventana luminosa y reutiliza agua fría sin sal para regar."
    ),
    EcoFact(
        id = 2,
        title = "Mini-Mundos en la Tierra",
        summary = "Una sola cucharadita de tierra sana contiene más microorganismos vivos que personas en el planeta, ¡impulsando la vida terrestre!",
        sdg = SdgTag.SDG15,
        actionTip = "Agrega un poco de composta casera a la tierra de tu maceta para nutrir sus microbios."
    ),
    EcoFact(
        id = 3,
        title = "Paradas para Polinizadores",
        summary = "Incluso una sola maceta con flores en una ventana o balcón crea un refugio vital para abejas y mariposas que cruzan la ciudad.",
        sdg = SdgTag.SDG15,
        actionTip = "Evita aerosoles químicos y deja que los insectos urbanos visiten flores seguras."
    ),
    EcoFact(
        id = 4,
        title = "Techos Verdes y Ciudades Esponja",
        summary = "Las plantas en azoteas absorben la lluvia intensa como una esponja, ayudando a las ciudades sostenibles a evitar inundaciones.",
        sdg = SdgTag.SDG11,
        actionTip = "Recoge agua de lluvia en una botella en días lluviosos para regar tu planta."
    ),
    EcoFact(
        id = 5,
        title = "Atrapadoras Naturales de Carbono",
        summary = "Al crecer, las plantas absorben dióxido de carbono del aire y lo guardan en sus hojas, tallos y raíces.",
        sdg = SdgTag.SDG15,
        actionTip = "Cuida plantas perennes y participa en jornadas de plantación de árboles en tu parque."
    ),
    EcoFact(
        id = 6,
        title = "Aire Más Limpio en tu Barrio",
        summary = "Las hojas atrapan partículas finas de polvo del aire y ayudan a filtrar contaminantes urbanos.",
        sdg = SdgTag.SDG11,
        actionTip = "Limpia suavemente el polvo de las hojas anchas con un paño húmedo para que respiren mejor."
    ),
    EcoFact(
        id = 7,
        title = "Inspiración para Parques Inteligentes",
        summary = "Las suculentas y cactus enseñan a los ingenieros a diseñar jardines urbanos que se mantienen verdes con muy poca agua.",
        sdg = SdgTag.SDG11,
        actionTip = "Riega directamente en la base por la mañana para que se evapore menos agua."
    ),
    EcoFact(
        id = 8,
        title = "Corredores de Biodiversidad",
        summary = "Los parques conectados y huertos comunitarios permiten que aves e insectos viajen seguros por toda la ciudad.",
        sdg = SdgTag.SDG15,
        actionTip = "Únete a un huerto escolar o vecinal para conectar más espacios verdes."
    ),
    EcoFact(
        id = 9,
        title = "Barreras Verdes Contra el Ruido",
        summary = "Los setos frondosos y la vegetación urbana absorben parte del ruido del tráfico, creando barrios más tranquilos.",
        sdg = SdgTag.SDG11,
        actionTip = "Observa en tu próximo paseo cómo los parques se sienten más silenciosos y frescos."
    ),
    EcoFact(
        id = 10,
        title = "Composta Sin Desperdicios",
        summary = "Convertir cáscaras de fruta y hojas secas en composta devuelve nutrientes al suelo en lugar de llenar vertederos.",
        sdg = SdgTag.SDG15,
        actionTip = "Separa restos vegetales de cocina para composta comunitaria o lombricomposta."
    ),
    EcoFact(
        id = 11,
        title = "Espacios Verdes y Bienestar",
        summary = "Dedicar 15 minutos a cuidar plantas o caminar por un parque reduce el estrés y mejora la concentración al estudiar.",
        sdg = SdgTag.SDG11,
        actionTip = "Tómate un respiro diario para revisar las hojas y la tierra de tu planta."
    ),
    EcoFact(
        id = 12,
        title = "Guardianes de Semillas",
        summary = "Proteger plantas nativas diversas conserva hábitats resistentes y alimentos saludables para futuras generaciones.",
        sdg = SdgTag.SDG15,
        actionTip = "Pregunta en casa o en tu escuela cómo plantar una semilla de flor silvestre local."
    )
)

val ECO_TIPS_POOL: List<Triple<String, String, NotificationCategory>> = listOf(
    Triple(
        "Refrescando la Ciudad (ODS 11)",
        "¡Los árboles urbanos y plantas de ventana funcionan como aire acondicionado natural! ¿Cómo está la tierra de tu planta hoy?",
        NotificationCategory.SDG11
    ),
    Triple(
        "Héroe de Polinizadores (ODS 15)",
        "Los balcones verdes y macetas crean paradas seguras para abejas y mariposas en barrios con mucho cemento.",
        NotificationCategory.SDG15
    ),
    Triple(
        "Truco Ciudad Esponja (ODS 11)",
        "Las plantas absorben el agua de lluvia y reducen charcos. ¡Prueba guardar el sobrante frío de tu botella para regar tu planta!",
        NotificationCategory.SDG11
    ),
    Triple(
        "Tierra Viva (ODS 15)",
        "Un suelo sano está lleno de microbios útiles que transforman materia orgánica en alimento para las plantas.",
        NotificationCategory.SDG15
    )
)

package com.namnam.recetapp

object RecetasData {
    fun obtenerRecetas(): List<Receta> {
        return listOf(
            Receta(
                id = 1,
                nombre = "Pasta Carbonara",
                tipo = "Comida",
                imagenResId = android.R.drawable.ic_menu_gallery,
                ingredientes = listOf(
                    "400g de pasta (espaguetis o fettuccine)",
                    "200g de panceta o tocino",
                    "4 huevos grandes",
                    "100g de queso parmesano rallado",
                    "Sal y pimienta negra al gusto",
                    "2 dientes de ajo (opcional)"
                ),
                pasos = listOf(
                    "Pon a hervir abundante agua con sal en una olla grande",
                    "Corta la panceta en cubitos pequeños",
                    "En una sartén grande, fríe la panceta a fuego medio hasta que esté dorada y crujiente",
                    "Mientras tanto, bate los huevos en un bowl con el queso parmesano rallado",
                    "Cocina la pasta según las instrucciones del paquete hasta que esté al dente",
                    "Reserva 1 taza del agua de cocción de la pasta antes de escurrirla",
                    "Escurre la pasta y agrégala inmediatamente a la sartén con la panceta",
                    "Retira la sartén del fuego y agrega la mezcla de huevo, revolviendo rápidamente",
                    "Si está muy espesa, agrega un poco del agua reservada",
                    "Sirve inmediatamente con más queso parmesano y pimienta negra molida"
                ),
                tiempoPreparacion = "30 min",
                dificultad = "Media"
            ),
            Receta(
                id = 2,
                nombre = "Brownies de Chocolate",
                tipo = "Postre",
                imagenResId = android.R.drawable.ic_menu_gallery,
                ingredientes = listOf(
                    "200g de chocolate semi-amargo",
                    "150g de mantequilla",
                    "3 huevos grandes",
                    "200g de azúcar",
                    "100g de harina",
                    "1 cucharadita de esencia de vainilla",
                    "Una pizca de sal"
                ),
                pasos = listOf(
                    "Precalienta el horno a 180°C",
                    "Engrasa y enharina un molde cuadrado de 20x20 cm",
                    "Derrite el chocolate con la mantequilla a baño maría o en microondas",
                    "En un bowl grande, bate los huevos con el azúcar hasta que estén espumosos",
                    "Agrega el chocolate derretido y la vainilla, mezcla bien",
                    "Tamiza la harina con la sal e incorpórala suavemente con movimientos envolventes",
                    "Vierte la mezcla en el molde preparado",
                    "Hornea durante 25-30 minutos (el centro debe quedar ligeramente húmedo)",
                    "Deja enfriar completamente antes de cortar en cuadrados",
                    "Opcional: espolvorea azúcar glas por encima antes de servir"
                ),
                tiempoPreparacion = "45 min",
                dificultad = "Fácil"
            ),
            Receta(
                id = 3,
                nombre = "Ensalada César",
                tipo = "Comida",
                imagenResId = android.R.drawable.ic_menu_gallery,
                ingredientes = listOf(
                    "1 lechuga romana grande",
                    "200g de pechuga de pollo",
                    "Pan para crutones",
                    "Queso parmesano",
                    "Salsa César",
                    "Aceite de oliva",
                    "Sal y pimienta"
                ),
                pasos = listOf(
                    "Lava y corta la lechuga en trozos",
                    "Sazona el pollo con sal y pimienta",
                    "Cocina el pollo a la plancha hasta que esté bien cocido",
                    "Corta el pan en cubos y tuéstalos con aceite de oliva",
                    "Deja enfriar el pollo y córtalo en tiras",
                    "En un bowl grande, mezcla la lechuga con la salsa César",
                    "Agrega el pollo y los crutones",
                    "Termina con queso parmesano rallado por encima"
                ),
                tiempoPreparacion = "25 min",
                dificultad = "Fácil"
            ),
            Receta(
                id = 4,
                nombre = "Tiramisú",
                tipo = "Postre",
                imagenResId = android.R.drawable.ic_menu_gallery,
                ingredientes = listOf(
                    "500g de queso mascarpone",
                    "6 huevos",
                    "100g de azúcar",
                    "300ml de café expreso fuerte",
                    "400g de bizcochos de soletilla",
                    "Cacao en polvo para espolvorear",
                    "2 cucharadas de licor de café (opcional)"
                ),
                pasos = listOf(
                    "Separa las yemas de las claras de huevo",
                    "Bate las yemas con el azúcar hasta que estén cremosas",
                    "Incorpora el queso mascarpone a las yemas batidas",
                    "Bate las claras a punto de nieve y mézclalas suavemente con la crema",
                    "Prepara el café y déjalo enfriar, agrega el licor si lo deseas",
                    "Moja rápidamente los bizcochos en el café",
                    "Coloca una capa de bizcochos en un molde rectangular",
                    "Cubre con una capa de crema de mascarpone",
                    "Repite las capas hasta terminar con crema",
                    "Refrigera por al menos 4 horas",
                    "Antes de servir, espolvorea generosamente con cacao en polvo"
                ),
                tiempoPreparacion = "30 min + 4h refrigeración",
                dificultad = "Media"
            ),
            Receta(
                id = 5,
                nombre = "Tacos al Pastor",
                tipo = "Comida",
                imagenResId = android.R.drawable.ic_menu_gallery,
                ingredientes = listOf(
                    "500g de carne de cerdo en láminas finas",
                    "Tortillas de maíz",
                    "Piña natural",
                    "Cebolla",
                    "Cilantro",
                    "Limones",
                    "Chiles",
                    "Achiote y especias para marinado"
                ),
                pasos = listOf(
                    "Marina la carne con achiote, especias y jugo de piña durante 2 horas",
                    "Corta la piña, cebolla y cilantro finamente",
                    "Calienta una sartén o plancha a fuego alto",
                    "Cocina la carne marinada hasta que esté dorada",
                    "Dora un poco la piña en la misma sartén",
                    "Calienta las tortillas",
                    "Sirve la carne en las tortillas",
                    "Agrega piña, cebolla, cilantro y limón al gusto"
                ),
                tiempoPreparacion = "40 min + 2h marinado",
                dificultad = "Media"
            ),
            Receta(
                id = 6,
                nombre = "Cheesecake de Frutos Rojos",
                tipo = "Postre",
                imagenResId = android.R.drawable.ic_menu_gallery,
                ingredientes = listOf(
                    "600g de queso crema",
                    "200ml de crema de leche",
                    "150g de azúcar",
                    "3 huevos",
                    "200g de galletas María",
                    "100g de mantequilla derretida",
                    "300g de frutos rojos mixtos",
                    "Esencia de vainilla"
                ),
                pasos = listOf(
                    "Tritura las galletas y mézclalas con la mantequilla derretida",
                    "Presiona la mezcla en el fondo de un molde desmontable",
                    "Refrigera la base mientras preparas el relleno",
                    "Bate el queso crema con el azúcar hasta que esté suave",
                    "Agrega los huevos uno a uno, batiendo después de cada adición",
                    "Incorpora la crema y la vainilla",
                    "Vierte la mezcla sobre la base de galleta",
                    "Hornea a 160°C por 50-60 minutos",
                    "Deja enfriar completamente y refrigera por 4 horas",
                    "Decora con frutos rojos frescos antes de servir"
                ),
                tiempoPreparacion = "90 min + 4h refrigeración",
                dificultad = "Media"
            )
        )
    }
}
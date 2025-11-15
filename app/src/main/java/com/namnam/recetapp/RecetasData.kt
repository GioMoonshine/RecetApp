package com.namnam.recetapp

object RecetasData {
    fun obtenerRecetasPorDefecto(usuarioId: Int): List<Receta> {
        return listOf(
            // === RECETA 1: PASTA CARBONARA ===
            Receta(
                nombre = "Pasta Carbonara Clásica",
                tipo = "Comida",
                dificultad = "Media",
                tiempoValor = 25,
                tiempoUnidad = "Minutos",
                ingredientes = listOf(
                    IngredienteItem("400g", "Spaghetti"),
                    IngredienteItem("200g", "Panceta o guanciale"),
                    IngredienteItem("4", "Huevos"),
                    IngredienteItem("100g", "Queso parmesano rallado"),
                    IngredienteItem("Al gusto", "Pimienta negra"),
                    IngredienteItem("Al gusto", "Sal")
                ),
                pasos = listOf(
                    "Cocina la pasta en agua hirviendo con sal según las instrucciones del paquete hasta que esté al dente.",
                    "Mientras tanto, corta la panceta en cubos pequeños y fríela en una sartén grande sin aceite hasta que esté dorada y crujiente.",
                    "En un bowl, bate los huevos con el queso parmesano rallado y abundante pimienta negra recién molida.",
                    "Escurre la pasta reservando una taza del agua de cocción. Añade la pasta caliente a la sartén con la panceta.",
                    "Retira del fuego y vierte la mezcla de huevo sobre la pasta, mezclando rápidamente. Añade agua de cocción si es necesario para crear una salsa cremosa.",
                    "Sirve inmediatamente con más queso parmesano y pimienta negra."
                ),
                usuarioId = usuarioId,
                imagenUri = "android.resource://com.namnam.recetapp/" + R.drawable.receta_carbonara,
                esPrivada = true
            ),

            // === RECETA 2: RISOTTO DE HONGOS ===
            Receta(
                nombre = "Risotto de Hongos y Trufa",
                tipo = "Comida",
                dificultad = "Difícil",
                tiempoValor = 40,
                tiempoUnidad = "Minutos",
                ingredientes = listOf(
                    IngredienteItem("300g", "Arroz Arborio"),
                    IngredienteItem("300g", "Hongos variados frescos"),
                    IngredienteItem("1L", "Caldo de verduras caliente"),
                    IngredienteItem("1", "Cebolla mediana picada"),
                    IngredienteItem("100ml", "Vino blanco seco"),
                    IngredienteItem("50g", "Mantequilla"),
                    IngredienteItem("80g", "Queso parmesano"),
                    IngredienteItem("2 cdas", "Aceite de oliva"),
                    IngredienteItem("Unas gotas", "Aceite de trufa (opcional)")
                ),
                pasos = listOf(
                    "Limpia y corta los hongos en láminas. Saltea la mitad en una sartén con aceite hasta dorar y reserva.",
                    "En una olla grande, sofríe la cebolla picada con aceite de oliva hasta que esté transparente.",
                    "Añade el arroz y tuesta por 2 minutos removiendo constantemente hasta que los granos estén ligeramente translúcidos.",
                    "Vierte el vino blanco y remueve hasta que se evapore completamente.",
                    "Añade los hongos crudos restantes y comienza a agregar el caldo caliente, un cucharón a la vez, removiendo constantemente.",
                    "Continúa añadiendo caldo y removiendo durante 18-20 minutos hasta que el arroz esté cremoso pero al dente.",
                    "Retira del fuego, añade la mantequilla, el parmesano, los hongos salteados reservados y el aceite de trufa. Mezcla bien.",
                    "Deja reposar 2 minutos con la tapa puesta antes de servir."
                ),
                usuarioId = usuarioId,
                imagenUri = "android.resource://com.namnam.recetapp/" + R.drawable.receta_rissotto,
                esPrivada = true
            ),

            // === RECETA 3: TARTA DE LIMÓN ===
            Receta(
                nombre = "Tarta de Limón Merengada",
                tipo = "Postre",
                dificultad = "Media",
                tiempoValor = 2,
                tiempoUnidad = "Horas",
                ingredientes = listOf(
                    IngredienteItem("200g", "Galletas María trituradas"),
                    IngredienteItem("100g", "Mantequilla derretida"),
                    IngredienteItem("4", "Limones (jugo y ralladura)"),
                    IngredienteItem("400ml", "Leche condensada"),
                    IngredienteItem("4", "Yemas de huevo"),
                    IngredienteItem("4", "Claras de huevo"),
                    IngredienteItem("150g", "Azúcar"),
                    IngredienteItem("1 pizca", "Sal")
                ),
                pasos = listOf(
                    "Mezcla las galletas trituradas con la mantequilla derretida. Presiona en el fondo de un molde desmontable de 24cm. Refrigera 30 minutos.",
                    "En un bowl, mezcla las yemas con la leche condensada, el jugo y la ralladura de limón hasta obtener una mezcla homogénea.",
                    "Vierte esta mezcla sobre la base de galletas y hornea a 180°C durante 15 minutos. Retira y deja enfriar.",
                    "Para el merengue, bate las claras con una pizca de sal hasta punto nieve. Añade el azúcar gradualmente mientras sigues batiendo hasta formar picos firmes y brillantes.",
                    "Cubre la tarta con el merengue, creando picos decorativos con una espátula o manga pastelera.",
                    "Hornea a 200°C durante 3-5 minutos hasta que el merengue esté dorado. Alternativamente, usa un soplete de cocina.",
                    "Refrigera al menos 2 horas antes de servir."
                ),
                usuarioId = usuarioId,
                imagenUri = "android.resource://com.namnam.recetapp/" + R.drawable.receta_tarta_limon,
                esPrivada = true
            ),

            // === RECETA 4: SALMÓN AL HORNO ===
            Receta(
                nombre = "Salmón al Horno con Hierbas",
                tipo = "Comida",
                dificultad = "Fácil",
                tiempoValor = 30,
                tiempoUnidad = "Minutos",
                ingredientes = listOf(
                    IngredienteItem("4 filetes", "Salmón fresco"),
                    IngredienteItem("2", "Limones"),
                    IngredienteItem("3 dientes", "Ajo picado"),
                    IngredienteItem("2 cdas", "Eneldo fresco picado"),
                    IngredienteItem("2 cdas", "Perejil fresco picado"),
                    IngredienteItem("4 cdas", "Aceite de oliva extra virgen"),
                    IngredienteItem("Al gusto", "Sal y pimienta"),
                    IngredienteItem("200g", "Espárragos verdes")
                ),
                pasos = listOf(
                    "Precalienta el horno a 200°C. Forra una bandeja para horno con papel aluminio.",
                    "Coloca los filetes de salmón en la bandeja con la piel hacia abajo. Salpimienta al gusto.",
                    "En un bowl pequeño, mezcla el aceite de oliva, ajo picado, eneldo, perejil y el jugo de un limón.",
                    "Vierte esta mezcla sobre los filetes de salmón, asegurándote de cubrirlos uniformemente.",
                    "Corta los espárragos, elimina las partes duras y colócalos alrededor del salmón. Rocía con aceite de oliva.",
                    "Corta el limón restante en rodajas y colócalas sobre el salmón.",
                    "Hornea durante 15-20 minutos o hasta que el salmón esté cocido pero jugoso en el centro.",
                    "Sirve caliente con las rodajas de limón caramelizadas y los espárragos."
                ),
                usuarioId = usuarioId,
                imagenUri = "android.resource://com.namnam.recetapp/" + R.drawable.receta_salmon,
                esPrivada = true
            ),

            // === RECETA 5: BROWNIE DE CHOCOLATE ===
            Receta(
                nombre = "Brownie de Chocolate Intenso",
                tipo = "Postre",
                dificultad = "Fácil",
                tiempoValor = 45,
                tiempoUnidad = "Minutos",
                ingredientes = listOf(
                    IngredienteItem("200g", "Chocolate negro 70% cacao"),
                    IngredienteItem("150g", "Mantequilla sin sal"),
                    IngredienteItem("200g", "Azúcar"),
                    IngredienteItem("3", "Huevos grandes"),
                    IngredienteItem("1 cdta", "Extracto de vainilla"),
                    IngredienteItem("100g", "Harina"),
                    IngredienteItem("30g", "Cacao en polvo"),
                    IngredienteItem("1 pizca", "Sal"),
                    IngredienteItem("100g", "Nueces picadas (opcional)")
                ),
                pasos = listOf(
                    "Precalienta el horno a 180°C. Engrasa y enharina un molde cuadrado de 20x20cm.",
                    "Derrite el chocolate con la mantequilla a baño maría o en microondas a intervalos de 30 segundos, removiendo entre cada intervalo. Deja enfriar ligeramente.",
                    "En un bowl grande, bate los huevos con el azúcar hasta que la mezcla esté espumosa y de color pálido, aproximadamente 5 minutos.",
                    "Añade el extracto de vainilla y la mezcla de chocolate derretido. Mezcla hasta integrar completamente.",
                    "Tamiza la harina, el cacao en polvo y la sal sobre la mezcla. Incorpora suavemente con movimientos envolventes usando una espátula.",
                    "Si usas nueces, añádelas y mezcla brevemente.",
                    "Vierte la masa en el molde preparado y alisa la superficie.",
                    "Hornea durante 25-30 minutos. El centro debe estar ligeramente húmedo. Un palillo insertado debe salir con pocas migas húmedas.",
                    "Deja enfriar completamente en el molde antes de cortar en cuadrados."
                ),
                usuarioId = usuarioId,
                imagenUri = "android.resource://com.namnam.recetapp/" + R.drawable.receta_brownie,
                esPrivada = true
            )
        )
    }
}
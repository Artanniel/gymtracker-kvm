package com.gymtracker.data.model

/** Uma opção de alimento: quantidade em g/ml + nome/descrição. */
data class FoodOption(
    val qty: Int?,
    val name: String
)

/** Uma categoria dentro de uma refeição, com até 3 opções (preferência + 2 substitutos). */
data class FoodItem(
    val category: String,
    val preference: FoodOption,
    val sub1: FoodOption? = null,
    val sub2: FoodOption? = null
)

data class Meal(
    val id: String,
    val number: Int,
    val items: List<FoodItem>
)

data class Supplement(
    val id: String,
    val name: String,
    val timing: String
)

object DietData {

    const val CALORIES = 2300
    const val CARBS_G = 303
    const val PROTEIN_G = 180

    val meals: List<Meal> = listOf(
        Meal("meal_1", 1, listOf(
            FoodItem(
                "Carnes e Proteínas",
                FoodOption(120, "Ovo, 1un=45g (mexido, cozido, omelete ou frito com pouco óleo)"),
                FoodOption(120, "Ovo, 1un=45g (mexido, cozido, omelete ou frito com pouco óleo)"),
                FoodOption(58, "Whey Protein")
            ),
            FoodItem(
                "Carboidrato (personalizado)",
                FoodOption(200, "Molico Triplo Zero Baunilha"),
                FoodOption(352, "Canto de Minas Coalhada Triplo Zero"),
                FoodOption(291, "Piracanjuba Leite Desnatado")
            ),
            FoodItem(
                "Frutas",
                FoodOption(200, "Mamão papaia"),
                FoodOption(166, "Abacaxi"),
                FoodOption(200, "Mamão papaia")
            ),
            FoodItem(
                "Fibras",
                FoodOption(50, "Aveia em flocos"),
                FoodOption(40, "Granola"),
                FoodOption(58, "Farelo de aveia")
            ),
            FoodItem(
                "Frutas",
                FoodOption(160, "Banana nanica"),
                FoodOption(264, "Maçã fuji"),
                FoodOption(264, "Maçã fuji")
            )
        )),
        Meal("meal_2", 2, listOf(
            FoodItem(
                "Carnes e Proteínas",
                FoodOption(160, "Peito de frango (grelhado, cozido ou desfiado)"),
                FoodOption(116, "Carne vermelha magra sem gordura (patinho, paleta, músculo, lagarto, filé mignon, coxão mole e coxão duro)"),
                FoodOption(154, "Peixe assado ou cozido sem óleo (atum, sardinha, bacalhau, tilápia e pescada)")
            ),
            FoodItem(
                "Cereais, Raízes, Tubérculos e Frutos",
                FoodOption(150, "Arroz branco cozido"),
                FoodOption(385, "Abóbora cabotian refogada"),
                FoodOption(373, "Batata inglesa cozida")
            ),
            FoodItem(
                "Vegetais (personalizado)",
                FoodOption(50, "Mistura de Vegetais (Milho, Feijão-verde, Ervilhas, Cenouras)"),
                FoodOption(50, "Mistura de Vegetais (Milho, Feijão-verde, Ervilhas, Cenouras)"),
                FoodOption(50, "Mistura de Vegetais (Milho, Feijão-verde, Ervilhas, Cenouras)")
            )
        )),
        Meal("meal_3", 3, listOf(
            FoodItem(
                "Frutas",
                FoodOption(200, "Banana nanica"),
                FoodOption(330, "Maçã fuji"),
                FoodOption(330, "Maçã fuji")
            ),
            FoodItem(
                "Leite e Derivados",
                FoodOption(200, "Iogurte natural"),
                FoodOption(264, "Leite de vaca desnatado, líquido"),
                FoodOption(75, "Creme de ricota")
            ),
            FoodItem(
                "Fibras",
                FoodOption(50, "Aveia em flocos"),
                FoodOption(58, "Farelo de aveia"),
                FoodOption(40, "Granola")
            ),
            FoodItem(
                "Carnes e Proteínas",
                FoodOption(30, "Whey Protein"),
                FoodOption(62, "Ovo, 1un=45g (mexido, cozido, omelete ou frito com pouco óleo)"),
                FoodOption(62, "Ovo, 1un=45g (mexido, cozido, omelete ou frito com pouco óleo)")
            )
        )),
        Meal("meal_4", 4, listOf(
            FoodItem(
                "Carnes e Proteínas",
                FoodOption(120, "Peito de frango (grelhado, cozido ou desfiado)"),
                FoodOption(87, "Carne vermelha magra sem gordura (patinho, paleta, músculo, lagarto, filé mignon, coxão mole e coxão duro)"),
                FoodOption(96, "Ovo, 1un=45g (mexido, cozido, omelete ou frito com pouco óleo)")
            ),
            FoodItem(
                "Vegetais (livres p/ consumo)",
                FoodOption(100, "Brócolis"),
                FoodOption(null, "Alface"),
                FoodOption(null, "Tomate")
            ),
            FoodItem(
                "Cereais, Raízes, Tubérculos e Frutos",
                FoodOption(200, "Abóbora moranga refogada"),
                FoodOption(194, "Cenoura"),
                FoodOption(112, "Batata inglesa cozida")
            ),
            FoodItem(
                "Frutas",
                FoodOption(200, "Abacaxi"),
                FoodOption(200, "Abacaxi"),
                FoodOption(241, "Mamão papaia")
            )
        ))
    )

    val supplements: List<Supplement> = listOf(
        Supplement("supp_cafeina", "200mg Cafeína", "1h30 (ou após a refeição) antes do treino"),
        Supplement("supp_creatina", "6g Creatina", "No café da manhã"),
        Supplement("supp_omega3", "1 cápsula Ômega 3", "Junto com o café da manhã"),
        Supplement("supp_vitc_zinco", "1g Vitamina C + Zinco", "1x ao dia, junto com o café da manhã"),
        Supplement("supp_leflora", "1 cápsula Leflora", "Na última refeição do dia")
    )

    val guidanceTips: List<String> = listOf(
        "Beber 25 a 50 ml/kg de água por dia.",
        "Pode usar pouco óleo vegetal para preparar os alimentos.",
        "Pode usar refrigerante zero, suco zero, gelatina zero e ketchup zero.",
        "Tempere a salada com sal, limão ou vinagre e um fio de azeite extra virgem.",
        "Pode usar condimentos e temperos naturais (curry, orégano, alho, cebola, salsa, cebolinha, coentro, tempero baiano, páprica, pimenta, etc).",
        "Preferência para legumes de cor verde escura.",
        "Pode usar sal normalmente para preparar os alimentos (sem exageros).",
        "Pese os alimentos já preparados/prontos.",
        "Termine o dia com todas as refeições feitas, mesmo que precise juntar algumas."
    )

    fun checklistIds(): List<String> = meals.map { it.id } + supplements.map { it.id }
}

package com.gymtracker.data.model

object WorkoutData {

    val workouts: List<Workout> = listOf(

        Workout(
            id = "treino_a",
            name = "Treino A — Quadríceps e Glúteo",
            shortName = "A",
            exercises = listOf(
                Exercise("a_agachamento_smith", "treino_a", "Agachamento Smith", listOf(
                    SetDefinition(1, SetType.WARMUP, "12 a 15"),
                    SetDefinition(2, SetType.WORKING, "12 a 15"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("a_leg45", "treino_a", "Leg 45", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("a_agachamento_hack", "treino_a", "Agachamento Hack", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("a_cadeira_extensora", "treino_a", "Cadeira Extensora", listOf(
                    SetDefinition(2, SetType.WORKING, "12 a 15"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("a_bulgaro", "treino_a", "Búlgaro", listOf(
                    SetDefinition(2, SetType.WORKING, "12 a 15"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                ))
            )
        ),

        Workout(
            id = "treino_b",
            name = "Treino B — Peito e Ombro",
            shortName = "B",
            exercises = listOf(
                Exercise("b_supino_inclinado", "treino_b", "Supino Inclinado", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("b_supino_halter_inclinado", "treino_b", "Supino Halter Inclinado", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("b_supino_maquina", "treino_b", "Supino Máquina", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 12"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("b_elevacao_lateral", "treino_b", "Elevação Lateral", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("b_elevacao_frontal_corda", "treino_b", "Elevação Frontal Corda Polia", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10")
                ))
            )
        ),

        Workout(
            id = "treino_c",
            name = "Treino C — Dorsal",
            shortName = "C",
            exercises = listOf(
                Exercise("c_remada_curvada", "treino_c", "Remada Curvada", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("c_remada_maquina", "treino_c", "Remada Máquina", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("c_cavalinho", "treino_c", "Cavalinho", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("c_puxada_pronada", "treino_c", "Puxada Pronada", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 12"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("c_puxada_supinada", "treino_c", "Puxada Supinada", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                ))
            )
        ),

        Workout(
            id = "treino_d",
            name = "Treino D — Bíceps e Tríceps",
            shortName = "D",
            exercises = listOf(
                Exercise("d_triceps_testa_barra", "treino_d", "Tríceps Testa Barra", listOf(
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("d_triceps_polia_corda", "treino_d", "Tríceps Polia Corda", listOf(
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("d_triceps_pegada_invertida", "treino_d", "Tríceps Pegada Invertida", listOf(
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("d_rosca_direta", "treino_d", "Rosca Direta", listOf(
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("d_martelo", "treino_d", "Martelo", listOf(
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("d_biceps_maquina", "treino_d", "Bíceps Máquina", listOf(
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                ))
            )
        ),

        Workout(
            id = "treino_a2",
            name = "Treino A2 — Posterior e Glúteo",
            shortName = "A2",
            exercises = listOf(
                Exercise("a2_mesa_flexora", "treino_a2", "Mesa Flexora", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 12"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("a2_deadlift_sumo", "treino_a2", "Deadlift Sumô", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("a2_stiff", "treino_a2", "Stiff", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("a2_cadeira_flexora", "treino_a2", "Cadeira Flexora", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 12"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("a2_panturrilha_solear", "treino_a2", "Panturrilha Solear", listOf(
                    SetDefinition(2, SetType.WORKING, "12 a 15"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10")
                )),
                Exercise("a2_panturrilha_smith", "treino_a2", "Panturrilha Smith", listOf(
                    SetDefinition(2, SetType.WORKING, "12 a 15"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10")
                ))
            )
        ),

        Workout(
            id = "treino_b2",
            name = "Treino B2 — Ombro e Peito",
            shortName = "B2",
            exercises = listOf(
                Exercise("b2_supino_reto_maquina", "treino_b2", "Supino Reto Máquina", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("b2_crucifixo_crossover", "treino_b2", "Crucifixo / Cross Over", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("b2_elevacao_frontal_corda", "treino_b2", "Elevação Frontal Corda Polia", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("b2_elevacao_lateral_halteres", "treino_b2", "Elevação Lateral Halteres", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                )),
                Exercise("b2_crucifixo_inverso", "treino_b2", "Crucifixo Inverso", listOf(
                    SetDefinition(2, SetType.WORKING, "10 a 12"),
                    SetDefinition(3, SetType.WORKING, "10 a 12"),
                    SetDefinition(4, SetType.WORKING, "8 a 10"),
                    SetDefinition(5, SetType.HARD, "4 a 6")
                ))
            )
        )
    )

    // Treinos extras: agendamento flexível, encaixar na semana conforme disponibilidade
    val extraWorkouts: List<Workout> = listOf(
        Workout(
            id = "treino_abdomen",
            name = "Treino de Abdômen",
            shortName = "ABD",
            isFlexible = true,
            minRestDays = 3,
            idealRestDays = 4,
            tip = "💨 Puxe o ar no alongamento e solte o ar na hora da contração!",
            exercises = listOf(
                Exercise("abd_polia_ajoelhado", "treino_abdomen", "Abdominal na Polia Ajoelhado", listOf(
                    SetDefinition(1, SetType.WORKING, "12 a 10"),
                    SetDefinition(2, SetType.WORKING, "8 a 10"),
                    SetDefinition(3, SetType.WORKING, "6 a 8")
                )),
                Exercise("abd_infra_paralela", "treino_abdomen", "Abdominal Infra Paralela", listOf(
                    SetDefinition(1, SetType.WORKING, "12 a 10"),
                    SetDefinition(2, SetType.WORKING, "8 a 10"),
                    SetDefinition(3, SetType.WORKING, "6 a 8")
                )),
                Exercise("abd_supra_peso", "treino_abdomen", "Abdominal Supra com Peso", listOf(
                    SetDefinition(1, SetType.WORKING, "12 a 10"),
                    SetDefinition(2, SetType.WORKING, "8 a 10"),
                    SetDefinition(3, SetType.WORKING, "6 a 8")
                )),
                Exercise("abd_remador", "treino_abdomen", "Abdominal Remador", listOf(
                    SetDefinition(1, SetType.WORKING, "12 a 10"),
                    SetDefinition(2, SetType.WORKING, "8 a 10"),
                    SetDefinition(3, SetType.WORKING, "6 a 8")
                ))
            )
        )
    )

    val allWorkouts: List<Workout> get() = workouts + extraWorkouts

    fun getWorkoutById(id: String): Workout? = allWorkouts.find { it.id == id }
}

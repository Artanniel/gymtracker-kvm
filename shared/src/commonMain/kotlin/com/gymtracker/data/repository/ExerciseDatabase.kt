package com.gymtracker.data.repository

import com.gymtracker.data.model.*

object ExerciseDatabase {

    val exercises: List<ExerciseDetail> = listOf(
        // ═══════════════════════════════════════════════════════════════════
        // PEITO (Chest)
        // ═══════════════════════════════════════════════════════════════════
        ExerciseDetail(
            id = "push_up",
            name = "Flexão de Braços",
            category = ExerciseCategory.BODYWEIGHT,
            muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Apoie as mãos no chão na largura dos ombros. Estenda os braços e desça o corpo até quase tocar o chão. Empurre para cima.",
            tips = "Mantenha o corpo reto como uma prancha. Não deixe o quadril cair."
        ),
        ExerciseDetail(
            id = "diamond_push_up",
            name = "Flexão Diamante",
            category = ExerciseCategory.BODYWEIGHT,
            muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Posicione as mãos formando um diamante com os dedos indicadores e polegares. Desça o peito até as mãos.",
            tips = "Mantenha os cotovelos próximos ao corpo para maior ativação dos tríceps."
        ),
        ExerciseDetail(
            id = "decline_push_up",
            name = "Flexão Declinada",
            category = ExerciseCategory.BODYWEIGHT,
            muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.BENCH),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Coloque os pés elevados em um banco. Realize a flexão normal.",
            tips = "Quanto mais alta a elevação, maior a carga nos ombros e peito superior."
        ),
        ExerciseDetail(
            id = "chest_press",
            name = "Supino Reto",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.BARBELL, Equipment.BENCH),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Deitado no banco, segure a barra na largura dos ombros. Desça até o peito e empurre para cima.",
            tips = "Mantenha os pés firmes no chão e as escápulas retraídas."
        ),
        ExerciseDetail(
            id = "dumbbell_fly",
            name = "Crucifixo com Halteres",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.CHEST),
            equipment = listOf(Equipment.DUMBBELLS, Equipment.BENCH),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Deitado no banco, abra os braços lateralmente com halteres. Volte à posição inicial controladamente.",
            tips = "Mantenha uma leve flexão nos cotovelos para proteger as articulações."
        ),

        // ═══════════════════════════════════════════════════════════════════
        // COSTAS (Back)
        // ═══════════════════════════════════════════════════════════════════
        ExerciseDetail(
            id = "pull_up",
            name = "Barra Fixa (Pull-up)",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.BACK, MuscleGroup.BICEPS),
            equipment = listOf(Equipment.PULL_UP_BAR),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Segure a barra com pegada pronada. Puxe o corpo para cima até o queixo passar a barra.",
            tips = "Inicie o movimento contraindo as escápulas. Evite balançar o corpo."
        ),
        ExerciseDetail(
            id = "australian_pull_up",
            name = "Barra Australiana",
            category = ExerciseCategory.BODYWEIGHT,
            muscleGroups = listOf(MuscleGroup.BACK, MuscleGroup.BICEPS),
            equipment = listOf(Equipment.BARBELL),
            difficulty = Difficulty.BEGINNER,
            instructions = "Deitado abaixo de uma barra baixa, puxe o peito até a barra.",
            tips = "Mantenha o corpo reto e contraia as costas no topo do movimento."
        ),
        ExerciseDetail(
            id = "bent_over_row",
            name = "Remada Curvada",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.BACK, MuscleGroup.BICEPS),
            equipment = listOf(Equipment.BARBELL),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Inclinado para frente, puxe a barra até o abdômen. Mantenha as costas retas.",
            tips = "Não arredonde as costas. Contraia as escápulas no topo."
        ),
        ExerciseDetail(
            id = "lat_pulldown",
            name = "Puxada Frontal",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.BACK, MuscleGroup.BICEPS),
            equipment = listOf(Equipment.CABLE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Sentado na máquina, puxe a barra até o peito. Controle a subida.",
            tips = "Incline levemente o tronco para trás. Não tracione com os braços."
        ),

        // ═══════════════════════════════════════════════════════════════════
        // OMBROS (Shoulders)
        // ═══════════════════════════════════════════════════════════════════
        ExerciseDetail(
            id = "overhead_press",
            name = "Desenvolvimento",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            equipment = listOf(Equipment.BARBELL),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Em pé, empurre a barra da altura dos ombros até acima da cabeça.",
            tips = "Mantenha o core contraído. Não arqueie as costas."
        ),
        ExerciseDetail(
            id = "lateral_raise",
            name = "Elevação Lateral",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = Difficulty.BEGINNER,
            instructions = "Com halteres nas mãos, eleve os braços lateralmente até a altura dos ombros.",
            tips = "Mantenha uma leve flexão nos cotovelos. Não suba acima dos ombros."
        ),
        ExerciseDetail(
            id = "pike_push_up",
            name = "Flexão Pike",
            category = ExerciseCategory.BODYWEIGHT,
            muscleGroups = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Posição de flexão com quadril elevado. Desça a cabeça em direção ao chão.",
            tips = "Mantenha os braços perpendicular ao chão para maior ativação dos ombros."
        ),

        // ═══════════════════════════════════════════════════════════════════
        // BRAÇOS (Arms)
        // ═══════════════════════════════════════════════════════════════════
        ExerciseDetail(
            id = "bicep_curl",
            name = "Rosca Direta",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.BICEPS),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = Difficulty.BEGINNER,
            instructions = "Com halteres, flexione os cotovelos levando os pesos aos ombros. Desça controladamente.",
            tips = "Mantenha os cotovelos fixos ao lado do corpo. Não balance o corpo."
        ),
        ExerciseDetail(
            id = "tricep_dips",
            name = "Mergulho em Banco",
            category = ExerciseCategory.BODYWEIGHT,
            muscleGroups = listOf(MuscleGroup.TRICEPS, MuscleGroup.CHEST),
            equipment = listOf(Equipment.BENCH),
            difficulty = Difficulty.BEGINNER,
            instructions = "Apoie as mãos em um banco atrás de você. Flexione os cotovelos para descer o corpo.",
            tips = "Mantenha as costas próximas ao banco. Não desça muito para proteger os ombros."
        ),
        ExerciseDetail(
            id = "hammer_curl",
            name = "Rosca Martelo",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.BICEPS, MuscleGroup.FOREARMS),
            equipment = listOf(Equipment.DUMBBELLS),
            difficulty = Difficulty.BEGINNER,
            instructions = "Com halteres em pegada neutra, flexione os cotovelos. Palmas viradas uma para a outra.",
            tips = "Mantenha os cotovelos estáveis. Controle a descida."
        ),

        // ═══════════════════════════════════════════════════════════════════
        // PERNAS (Legs)
        // ═══════════════════════════════════════════════════════════════════
        ExerciseDetail(
            id = "squat",
            name = "Agachamento",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Em pé, desça flexionando os quadris e joelhos como se fosse sentar. Volte à posição inicial.",
            tips = "Mantenha o peito aberto e os joelhos alinhados com os pés."
        ),
        ExerciseDetail(
            id = "lunges",
            name = "Afundo",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Dê um passo à frente e desça o joelho traseiro até quase tocar o chão. Alterne as pernas.",
            tips = "Mantenha o tronco ereto. O joelho da frente não deve ultrapassar os dedos dos pés."
        ),
        ExerciseDetail(
            id = "romanian_deadlift",
            name = "Stiff",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.LOWER_BACK),
            equipment = listOf(Equipment.BARBELL),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Com a barra, incline o tronco para frente flexionando os quadris. Sinta o estiramento na posterior.",
            tips = "Mantenha as costas retas. A barra deve ficar próxima ao corpo."
        ),
        ExerciseDetail(
            id = "leg_press",
            name = "Leg Press",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
            equipment = listOf(Equipment.MACHINE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Sentado na máquina, empurre a plataforma com os pés. Controle a descida.",
            tips = "Não travar os joelhos no topo. Manter os joelhos alinhados com os pés."
        ),
        ExerciseDetail(
            id = "calf_raise",
            name = "Elevação de Panturrilha",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.CALVES),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Em pé na borda de um degrau, eleve o corpo apoiando na ponta dos pés. Desça controladamente.",
            tips = "Segure em algo para equilíbrio. Estire bem na parte de baixo."
        ),
        ExerciseDetail(
            id = "bulgarian_split_squat",
            name = "Agachamento Búlgaro",
            category = ExerciseCategory.STRENGTH,
            muscleGroups = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
            equipment = listOf(Equipment.BENCH),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Com o pé traseiro apoiado em um banco, agache com a perna da frente.",
            tips = "Mantenha o tronco ereto. O joelho da frente deve ficar alinhado com o pé."
        ),

        // ═══════════════════════════════════════════════════════════════════
        // ABDÔMEN (Core)
        // ═══════════════════════════════════════════════════════════════════
        ExerciseDetail(
            id = "plank",
            name = "Prancha",
            category = ExerciseCategory.BODYWEIGHT,
            muscleGroups = listOf(MuscleGroup.ABS, MuscleGroup.LOWER_BACK),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Apoie os antebraços e as pontas dos pés no chão. Mantenha o corpo reto.",
            tips = "Não deixe o quadril cair ou subir demais. Contraia o abdômen."
        ),
        ExerciseDetail(
            id = "russian_twist",
            name = "Russo Twist",
            category = ExerciseCategory.BODYWEIGHT,
            muscleGroups = listOf(MuscleGroup.OBLIQUES, MuscleGroup.ABS),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Sentado com os pés elevados, rotacione o tronco de um lado para o outro.",
            tips = "Mantenha as costas retas. Olhe para a direção da rotação."
        ),
        ExerciseDetail(
            id = "mountain_climber",
            name = "Escalador",
            category = ExerciseCategory.HIIT,
            muscleGroups = listOf(MuscleGroup.ABS, MuscleGroup.QUADS, MuscleGroup.CHEST),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Em posição de prancha, traga um joelho ao peito alternadamente rapidamente.",
            tips = "Mantenha o quadril estável. Acelere o movimento para maior intensidade."
        ),
        ExerciseDetail(
            id = "leg_raise",
            name = "Elevação de Pernas",
            category = ExerciseCategory.BODYWEIGHT,
            muscleGroups = listOf(MuscleGroup.ABS, MuscleGroup.HIP_FLEXORS),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Deitado de costas, eleve as pernas até 90 graus. Desça controladamente.",
            tips = "Mantenha as costas baixas no chão. Não use impulso."
        ),
        ExerciseDetail(
            id = "bicycle_crunch",
            name = "Abdominal Bicicleta",
            category = ExerciseCategory.BODYWEIGHT,
            muscleGroups = listOf(MuscleGroup.ABS, MuscleGroup.OBLIQUES),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Deitado, alternadamente leve o cotovelo ao joelho oposto como se estivesse pedalando.",
            tips = "Estire completamente a perna. Não puxe o pescoço."
        ),

        // ═══════════════════════════════════════════════════════════════════
        // CARDIO
        // ═══════════════════════════════════════════════════════════════════
        ExerciseDetail(
            id = "jumping_jacks",
            name = "Polichinelo",
            category = ExerciseCategory.CARDIO,
            muscleGroups = listOf(MuscleGroup.QUADS, MuscleGroup.SHOULDERS),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Salte abrindo as pernas e levantando os braços. Volte à posição inicial.",
            tips = "Mantenha os joelhos levemente flexionados ao aterrissar."
        ),
        ExerciseDetail(
            id = "burpee",
            name = "Burpee",
            category = ExerciseCategory.HIIT,
            muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.QUADS, MuscleGroup.ABS),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Agache, apoie as mãos, salte para trás em prancha, flexione, salte para frente e salte para cima.",
            tips = "Mantenha o ritmo consistente. Modifique removendo o pulo se necessário."
        ),
        ExerciseDetail(
            id = "high_knees",
            name = "Joelhos Altos",
            category = ExerciseCategory.CARDIO,
            muscleGroups = listOf(MuscleGroup.QUADS, MuscleGroup.ABS),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Corra no lugar levando os joelhos à altura do peito.",
            tips = "Mantenha o ritmo rápido. Use os braços para impulsionar."
        ),
        ExerciseDetail(
            id = "squat_jump",
            name = "Agachamento com Salto",
            category = ExerciseCategory.PLYOMETRICS,
            muscleGroups = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.INTERMEDIATE,
            instructions = "Realize um agachamento e salte explosivamente. Aterrisse suavemente.",
            tips = "Aterrisse com os joelhos flexionados para absorver o impacto."
        ),

        // ═══════════════════════════════════════════════════════════════════
        // FLEXIBILIDADE
        // ═══════════════════════════════════════════════════════════════════
        ExerciseDetail(
            id = "hamstring_stretch",
            name = "Estiramento de Posterior",
            category = ExerciseCategory.FLEXIBILITY,
            muscleGroups = listOf(MuscleGroup.HAMSTRINGS),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Sentado, estenda uma perna e tente tocar os pés com as mãos.",
            tips = "Não force o estiramento. Mantenha por 20-30 segundos."
        ),
        ExerciseDetail(
            id = "quad_stretch",
            name = "Estiramento de Quadríceps",
            category = ExerciseCategory.FLEXIBILITY,
            muscleGroups = listOf(MuscleGroup.QUADS),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Em pé, segure o pé atrás e aproxime o calcanhar do glúteo.",
            tips = "Mantenha os joelhos juntos. Empure o quadril para frente."
        ),
        ExerciseDetail(
            id = "hip_flexor_stretch",
            name = "Estiramento de Flexores",
            category = ExerciseCategory.FLEXIBILITY,
            muscleGroups = listOf(MuscleGroup.HIP_FLEXORS, MuscleGroup.QUADS),
            equipment = listOf(Equipment.NONE),
            difficulty = Difficulty.BEGINNER,
            instructions = "Em posição de afundo, empure o quadril para frente e para baixo.",
            tips = "Mantenha o tronco ereto. Sinta o estiramento na frente da coxa."
        )
    )

    fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): List<ExerciseDetail> {
        return exercises.filter { it.muscleGroups.contains(muscleGroup) }
    }

    fun getExercisesByEquipment(equipment: Equipment): List<ExerciseDetail> {
        return exercises.filter { it.equipment.contains(equipment) || it.equipment.contains(Equipment.NONE) }
    }

    fun getExercisesByDifficulty(difficulty: Difficulty): List<ExerciseDetail> {
        return exercises.filter { it.difficulty == difficulty }
    }

    fun getExercisesByCategory(category: ExerciseCategory): List<ExerciseDetail> {
        return exercises.filter { it.category == category }
    }
}

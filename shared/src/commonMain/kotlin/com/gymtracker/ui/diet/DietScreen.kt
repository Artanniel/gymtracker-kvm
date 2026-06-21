package com.gymtracker.ui.diet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gymtracker.data.model.DietData
import com.gymtracker.data.model.FoodItem

@Composable
fun DietScreen() {
    val vm: DietViewModel = viewModel { DietViewModel() }
    val checklist by vm.checklist.collectAsState()
    val allIds = DietData.checklistIds()
    val dayComplete = allIds.isNotEmpty() && allIds.all { checklist[it] == true }

    LaunchedEffect(Unit) { vm.load() }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Protocolo 2300",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp))
        }

        // Banner de macros
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MacroItem("${DietData.CALORIES}", "kcal")
                    MacroItem("${DietData.CARBS_G}", "carbo (g)")
                    MacroItem("${DietData.PROTEIN_G}", "proteína (g)")
                }
            }
        }

        // Banner de dia completo
        if (dayComplete) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Text(
                        "🎉 Dia completo! Todas as refeições e suplementos registrados.",
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        color = Color.White, fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Refeições
        item {
            Text("Refeições", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 4.dp))
        }

        items(DietData.meals) { meal ->
            MealCard(
                meal = meal,
                checked = checklist[meal.id] == true,
                onToggle = { vm.toggle(meal.id, it) }
            )
        }

        // Suplementos
        item {
            Text("Suplementos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 4.dp))
        }

        item {
            Card(elevation = CardDefaults.cardElevation(3.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    DietData.supplements.forEachIndexed { i, supp ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = checklist[supp.id] == true,
                                onCheckedChange = { vm.toggle(supp.id, it) }
                            )
                            Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                                Text(supp.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(supp.timing, style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        if (i < DietData.supplements.lastIndex) HorizontalDivider()
                    }
                }
            }
        }

        // Orientações
        item {
            Text("Orientações Gerais", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 4.dp))
        }

        item {
            Card(elevation = CardDefaults.cardElevation(3.dp)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DietData.guidanceTips.forEach { tip ->
                        Text("•  $tip", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
fun MacroItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = Color.White)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White)
    }
}

@Composable
fun MealCard(meal: com.gymtracker.data.model.Meal, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Card(elevation = CardDefaults.cardElevation(3.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Refeição ${meal.number}", fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Feita", style = MaterialTheme.typography.bodySmall)
                    Checkbox(checked = checked, onCheckedChange = onToggle)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            meal.items.forEach { item -> FoodItemRow(item) }
        }
    }
}

@Composable
fun FoodItemRow(item: FoodItem) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(item.category, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall)
        val fmtQty: (Int?) -> String = { qty -> if (qty != null) "${qty}g/ml — " else "" }
        Text("🥇 ${fmtQty(item.preference.qty)}${item.preference.name}", style = MaterialTheme.typography.bodySmall)
        item.sub1?.let { Text("🥈 ${fmtQty(it.qty)}${it.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        item.sub2?.let { Text("🥉 ${fmtQty(it.qty)}${it.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

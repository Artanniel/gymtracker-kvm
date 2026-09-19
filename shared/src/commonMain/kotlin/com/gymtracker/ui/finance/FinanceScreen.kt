package com.gymtracker.ui.finance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gymtracker.db.Invoices
import com.gymtracker.db.Payment_plans
import com.gymtracker.util.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(onBack: () -> Unit) {
    val vm: FinanceViewModel = viewModel { FinanceViewModel() }
    val plans by vm.plans.collectAsState()
    val invoices by vm.invoices.collectAsState()
    val students by vm.students.collectAsState()
    val summary by vm.summary.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    var showAddPlan by remember { mutableStateOf(false) }
    var showAddInvoice by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestão Financeira") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FloatingActionButton(onClick = { showAddInvoice = true }, containerColor = MaterialTheme.colorScheme.secondary) {
                    Icon(Icons.Filled.Add, contentDescription = "Fatura")
                }
                FloatingActionButton(onClick = { showAddPlan = true }, containerColor = MaterialTheme.colorScheme.primary) {
                    Icon(Icons.Filled.Money, contentDescription = "Plano")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Text("Resumo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard("Recebido", summary?.totalPaid ?: 0.0, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    SummaryCard("Pendente", summary?.totalPending ?: 0.0, MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
                    SummaryCard("Atrasado", summary?.totalOverdue ?: 0.0, MaterialTheme.colorScheme.error, Modifier.weight(1f))
                }
            }

            item {
                Text("Planos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(plans) { plan ->
                PlanCard(plan = plan, onDelete = { vm.deletePlan(plan.id) })
            }

            item {
                Text("Faturas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(invoices) { invoice ->
                val student = students.find { it.id == invoice.studentId }
                InvoiceCard(
                    invoice = invoice,
                    studentName = student?.name ?: "Aluno #${invoice.studentId}",
                    onMarkPaid = { vm.markInvoicePaid(invoice.id, "pix") },
                    onMarkPending = { vm.markInvoicePending(invoice.id) },
                    onDelete = { vm.deleteInvoice(invoice.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showAddPlan) {
        AddPlanDialog(
            onDismiss = { showAddPlan = false },
            onConfirm = { name, desc, months, price, rec ->
                vm.createPlan(name, desc, months, price, rec)
                showAddPlan = false
            }
        )
    }

    if (showAddInvoice) {
        AddInvoiceDialog(
            students = students,
            plans = plans,
            onDismiss = { showAddInvoice = false },
            onConfirm = { studentId, planId, amount, dueDays, desc ->
                val due = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() + dueDays * 24L * 60 * 60 * 1000
                vm.createInvoice(studentId, planId, amount, due, desc)
                showAddInvoice = false
            }
        )
    }
}

@Composable
fun SummaryCard(title: String, value: Double, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 12.sp, color = color, fontWeight = FontWeight.Medium)
            Text(formatCurrency(value), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun PlanCard(plan: Payment_plans, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plan.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                if (!plan.description.isNullOrBlank()) {
                    Text(plan.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${plan.durationMonths} ${if (plan.durationMonths == 1L) "mês" else "meses"} · ${formatCurrency(plan.price)}",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
            }
            TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Text("Excluir")
            }
        }
    }
}

@Composable
fun InvoiceCard(
    invoice: Invoices,
    studentName: String,
    onMarkPaid: () -> Unit,
    onMarkPending: () -> Unit,
    onDelete: () -> Unit
) {
    val isPaid = invoice.status == "paid"
    val isOverdue = invoice.status == "pending" && invoice.dueDate < kotlinx.datetime.Clock.System.now().toEpochMilliseconds()

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(studentName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text(invoice.description ?: "Fatura", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Card(colors = CardDefaults.cardColors(
                    containerColor = when {
                        isPaid -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        isOverdue -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    }
                )) {
                    Text(
                        text = when {
                            isPaid -> "Pago"
                            isOverdue -> "Atrasado"
                            else -> "Pendente"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = when {
                            isPaid -> MaterialTheme.colorScheme.primary
                            isOverdue -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.tertiary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(formatCurrency(invoice.amount), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isPaid) {
                    OutlinedButton(onClick = onMarkPending) { Text("Reabrir") }
                } else {
                    Button(onClick = onMarkPaid) { Text("Marcar pago") }
                }
                TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text("Excluir")
                }
            }
        }
    }
}

@Composable
fun AddPlanDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String?, durationMonths: Int, price: Double, recurrence: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var months by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    var recurrence by remember { mutableStateOf("monthly") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Plano") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = months, onValueChange = { months = it }, label = { Text("Duração (meses) *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Preço (R$) *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = recurrence, onValueChange = { recurrence = it }, label = { Text("Recorrência") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val m = months.toIntOrNull() ?: 1
                val p = price.toDoubleOrNull() ?: 0.0
                if (name.isNotBlank() && p > 0) {
                    onConfirm(name, description.ifBlank { null }, m, p, recurrence)
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInvoiceDialog(
    students: List<com.gymtracker.db.Students>,
    plans: List<Payment_plans>,
    onDismiss: () -> Unit,
    onConfirm: (studentId: Long, planId: Long?, amount: Double, dueDays: Long, description: String?) -> Unit
) {
    var selectedStudent by remember { mutableStateOf<com.gymtracker.db.Students?>(null) }
    var selectedPlan by remember { mutableStateOf<Payment_plans?>(null) }
    var amount by remember { mutableStateOf("") }
    var dueDays by remember { mutableStateOf("7") }
    var description by remember { mutableStateOf("") }
    var expandedStudent by remember { mutableStateOf(false) }
    var expandedPlan by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Fatura") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(expanded = expandedStudent, onExpandedChange = { expandedStudent = it }) {
                    OutlinedTextField(
                        value = selectedStudent?.name ?: "Selecione o aluno",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Aluno *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStudent) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(expanded = expandedStudent, onDismissRequest = { expandedStudent = false }) {
                        students.forEach { student ->
                            DropdownMenuItem(text = { Text(student.name) }, onClick = { selectedStudent = student; expandedStudent = false })
                        }
                    }
                }

                ExposedDropdownMenuBox(expanded = expandedPlan, onExpandedChange = { expandedPlan = it }) {
                    OutlinedTextField(
                        value = selectedPlan?.name ?: "Selecione o plano (opcional)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Plano") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlan) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(expanded = expandedPlan, onDismissRequest = { expandedPlan = false }) {
                        plans.forEach { plan ->
                            DropdownMenuItem(text = { Text("${plan.name} - ${formatCurrency(plan.price)}") }, onClick = {
                                selectedPlan = plan
                                amount = plan.price.toString()
                                expandedPlan = false
                            })
                        }
                    }
                }

                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Valor (R$) *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = dueDays, onValueChange = { dueDays = it }, label = { Text("Vencimento (dias) *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val a = amount.toDoubleOrNull() ?: 0.0
                val d = dueDays.toLongOrNull() ?: 7L
                if (selectedStudent != null && a > 0) {
                    onConfirm(selectedStudent!!.id, selectedPlan?.id, a, d, description.ifBlank { null })
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

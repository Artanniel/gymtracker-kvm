package com.gymtracker.data.repository

import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.db.Invoices
import com.gymtracker.db.Payment_plans
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import kotlinx.datetime.Clock

class FinanceRepository {

    private val db get() = DatabaseProvider.get()
    private val q get() = db.financeQueries

    // ── Payment Plans ──

    fun getPlansFlow(): Flow<List<Payment_plans>> =
        q.getAllPlans().asFlow().mapToList(Dispatchers.Default)

    suspend fun getPlanById(id: Long): Payment_plans? =
        q.getPlanById(id).awaitAsOneOrNull()

    suspend fun createPlan(
        name: String,
        description: String?,
        durationMonths: Int,
        price: Double,
        recurrence: String = "monthly"
    ): Long {
        q.insertPlan(name, description, durationMonths.toLong(), price, "BRL", recurrence, 1L)
        return q.lastInsertPlanId().awaitAsOne()
    }

    suspend fun updatePlan(plan: Payment_plans) {
        q.updatePlan(plan.name, plan.description, plan.durationMonths, plan.price, plan.currency, plan.recurrence, plan.isActive, plan.id)
    }

    suspend fun deletePlan(id: Long) {
        q.deletePlan(id)
    }

    // ── Invoices ──

    fun getInvoicesFlow(): Flow<List<Invoices>> =
        q.getAllInvoices().asFlow().mapToList(Dispatchers.Default)

    suspend fun getInvoiceById(id: Long): Invoices? =
        q.getInvoiceById(id).awaitAsOneOrNull()

    suspend fun getInvoicesByStudent(studentId: Long): List<Invoices> =
        q.getInvoicesByStudent(studentId).awaitAsList()

    suspend fun createInvoice(
        studentId: Long,
        planId: Long?,
        amount: Double,
        dueDate: Long,
        description: String? = null
    ): Long {
        q.insertInvoice(studentId, planId, amount, "BRL", dueDate, "pending", description)
        return q.lastInsertInvoiceId().awaitAsOne()
    }

    suspend fun markPaid(id: Long, paymentMethod: String) {
        q.markInvoicePaid(Clock.System.now().toEpochMilliseconds(), paymentMethod, id)
    }

    suspend fun markPending(id: Long) {
        q.markInvoicePending(id)
    }

    suspend fun deleteInvoice(id: Long) {
        q.deleteInvoice(id)
    }

    // ── Summary ──

    suspend fun getSummary(): FinancialSummary {
        val row = q.getFinancialSummary().awaitAsOne()
        return FinancialSummary(
            totalPaid = row.totalPaid,
            totalPending = row.totalPending,
            totalOverdue = row.totalOverdue
        )
    }

    fun getSummaryFlow(): Flow<FinancialSummary> =
        q.getFinancialSummary().asFlow().mapToOne(Dispatchers.Default).map { row ->
            FinancialSummary(row.totalPaid, row.totalPending, row.totalOverdue)
        }
}

data class FinancialSummary(
    val totalPaid: Double,
    val totalPending: Double,
    val totalOverdue: Double
) {
    val totalReceivable: Double get() = totalPaid + totalPending
}

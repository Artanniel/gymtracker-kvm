package com.gymtracker.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.AppDependencies
import com.gymtracker.data.repository.FinancialSummary
import com.gymtracker.db.Invoices
import com.gymtracker.db.Payment_plans
import com.gymtracker.db.Students
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FinanceViewModel : ViewModel() {

    private val financeRepo = AppDependencies.financeRepo
    private val studentRepo = AppDependencies.studentRepo

    private val _plans = MutableStateFlow<List<Payment_plans>>(emptyList())
    val plans: StateFlow<List<Payment_plans>> = _plans.asStateFlow()

    private val _invoices = MutableStateFlow<List<Invoices>>(emptyList())
    val invoices: StateFlow<List<Invoices>> = _invoices.asStateFlow()

    private val _students = MutableStateFlow<List<Students>>(emptyList())
    val students: StateFlow<List<Students>> = _students.asStateFlow()

    private val _summary = MutableStateFlow<FinancialSummary?>(null)
    val summary: StateFlow<FinancialSummary?> = _summary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            financeRepo.getPlansFlow().collect { _plans.value = it }
        }
        viewModelScope.launch {
            financeRepo.getInvoicesFlow().collect { _invoices.value = it }
        }
        viewModelScope.launch {
            financeRepo.getSummaryFlow().collect { _summary.value = it }
        }
        viewModelScope.launch {
            studentRepo.getActiveStudentsFlow().collect { _students.value = it }
        }
        _isLoading.value = false
    }

    fun createPlan(name: String, description: String?, durationMonths: Int, price: Double, recurrence: String) {
        viewModelScope.launch {
            financeRepo.createPlan(name, description, durationMonths, price, recurrence)
        }
    }

    fun deletePlan(id: Long) {
        viewModelScope.launch { financeRepo.deletePlan(id) }
    }

    fun createInvoice(studentId: Long, planId: Long?, amount: Double, dueDate: Long, description: String?) {
        viewModelScope.launch {
            financeRepo.createInvoice(studentId, planId, amount, dueDate, description)
        }
    }

    fun markInvoicePaid(id: Long, method: String) {
        viewModelScope.launch { financeRepo.markPaid(id, method) }
    }

    fun markInvoicePending(id: Long) {
        viewModelScope.launch { financeRepo.markPending(id) }
    }

    fun deleteInvoice(id: Long) {
        viewModelScope.launch { financeRepo.deleteInvoice(id) }
    }
}

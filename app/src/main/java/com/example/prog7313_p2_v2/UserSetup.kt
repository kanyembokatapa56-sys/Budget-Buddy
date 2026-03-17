package com.example.prog7313_p2_v2

data class UserSetup(
    val language: String = "",
    val currency: String = "",
    val minBudget: String = "",
    val maxBudget: String = "",
    val budgetedAmount: String = "",
    val availableAmount: String = "",
    val savingsAmount: String = "",
    val debitGoal: String = ""
)
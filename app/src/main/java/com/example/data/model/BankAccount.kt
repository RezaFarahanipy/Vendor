package com.example.data.model

data class BankAccount(
    val bankName: String = "",
    val accountNumber: String = "",
    val iban: String = "", // شماره شبا
    val branch: String = "" // نام یا کد شعبه
)

object BankAccountUtils {
    fun serialize(accounts: List<BankAccount>): String {
        return accounts
            .filter { it.bankName.isNotBlank() || it.iban.isNotBlank() || it.accountNumber.isNotBlank() }
            .joinToString(";;") {
                val cleanBank = it.bankName.replace("|", "/").replace(";", "")
                val cleanAcc = it.accountNumber.replace("|", "/").replace(";", "")
                val cleanIban = it.iban.replace("|", "/").replace(";", "")
                val cleanBranch = it.branch.replace("|", "/").replace(";", "")
                "$cleanBank|$cleanAcc|$cleanIban|$cleanBranch"
            }
    }

    fun parse(raw: String): List<BankAccount> {
        if (raw.isBlank()) return emptyList()
        return raw.split(";;").mapNotNull { part ->
            val trimmed = part.trim()
            if (trimmed.isEmpty()) return@mapNotNull null
            val pieces = trimmed.split("|")
            if (pieces.isNotEmpty()) {
                BankAccount(
                    bankName = pieces.getOrElse(0) { "" }.trim(),
                    accountNumber = pieces.getOrElse(1) { "" }.trim(),
                    iban = pieces.getOrElse(2) { "" }.trim(),
                    branch = pieces.getOrElse(3) { "" }.trim()
                )
            } else null
        }
    }
}

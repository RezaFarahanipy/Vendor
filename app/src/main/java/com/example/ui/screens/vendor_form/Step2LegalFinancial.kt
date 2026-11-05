package com.example.ui.screens.vendor_form

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BankAccount
import com.example.ui.theme.GreenVerified
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.util.PersianUtils
import com.example.util.ShebaUtils

/**
 * Step 2: Legal, Registration & Bank Accounts with live Sheba validation
 */
@Composable
fun Step2LegalFinancial(
    economicCode: String,
    onEconomicCodeChange: (String) -> Unit,
    nationalId: String,
    onNationalIdChange: (String) -> Unit,
    registrationNumber: String,
    onRegistrationNumberChange: (String) -> Unit,
    bankAccountsList: SnapshotStateList<BankAccount>
) {
    Text(
        text = "اطلاعات حقوقی، ثبتی و حساب‌های بانکی",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    OutlinedTextField(
        value = economicCode,
        onValueChange = onEconomicCodeChange,
        label = { Text("کد اقتصادی (۱۲ رقمی)") },
        placeholder = { Text("411354897621") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = nationalId,
            onValueChange = onNationalIdChange,
            label = { Text("شناسه ملی (۱۱ رقمی)") },
            placeholder = { Text("10103487920") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
            value = registrationNumber,
            onValueChange = onRegistrationNumberChange,
            label = { Text("شماره ثبت شرکت") },
            placeholder = { Text("284910") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Multiple Bank Accounts Management
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "حساب‌های بانکی و شماره شبا (${PersianUtils.toPersianDigits(bankAccountsList.size)} حساب)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Styled Add Account Button
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                bankAccountsList.add(BankAccount("", "", "", ""))
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = TealPrimary.copy(alpha = 0.12f)
        ),
        border = BorderStroke(1.5.dp, TealPrimary.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = TealDark,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "افزودن حساب بانکی و شماره شبا جدید",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TealDark
            )
        }
    }

    bankAccountsList.forEachIndexed { index, account ->
        val shebaValidation = remember(account.iban) {
            if (account.iban.isNotBlank()) ShebaUtils.validateSheba(account.iban) else null
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "حساب شماره ${PersianUtils.toPersianDigits(index + 1)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (bankAccountsList.size > 1) {
                        IconButton(
                            onClick = { bankAccountsList.removeAt(index) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = RedFavorite, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = account.bankName,
                        onValueChange = {
                            bankAccountsList[index] = account.copy(bankName = it)
                        },
                        label = { Text("نام بانک") },
                        placeholder = { Text("ملت، صادرات، ملی...") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = account.branch,
                        onValueChange = {
                            bankAccountsList[index] = account.copy(branch = it)
                        },
                        label = { Text("نام یا کد شعبه") },
                        placeholder = { Text("شعبه مرکزی") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = account.accountNumber,
                    onValueChange = {
                        bankAccountsList[index] = account.copy(accountNumber = it)
                    },
                    label = { Text("شماره حساب") },
                    placeholder = { Text("123456789") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = account.iban,
                    onValueChange = { input ->
                        val raw = input.uppercase().replace(" ", "")
                        val formatted = if (!raw.startsWith("IR") && raw.any { it.isDigit() }) {
                            "IR" + raw.filter { it.isDigit() }
                        } else {
                            raw
                        }
                        val validation = ShebaUtils.validateSheba(formatted)
                        val autoBank = validation.detectedBankName
                        val newBankName = if (account.bankName.isBlank() && autoBank != null) {
                            autoBank
                        } else {
                            account.bankName
                        }
                        bankAccountsList[index] = account.copy(
                            iban = formatted,
                            bankName = newBankName
                        )
                    },
                    label = { Text("شماره شبا (IBAN)") },
                    placeholder = { Text("IR120120000000001234567890") },
                    singleLine = true,
                    isError = shebaValidation != null && !shebaValidation.isValid && account.iban.length > 5,
                    supportingText = {
                        if (shebaValidation != null) {
                            if (!shebaValidation.isValid && account.iban.length > 5) {
                                Text(
                                    text = shebaValidation.errorMessage ?: "شماره شبا نامعتبر است",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            } else if (shebaValidation.isValid) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = GreenVerified,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "شماره شبا معتبر است (${shebaValidation.detectedBankName ?: "بانک ایرانی"})",
                                        color = GreenVerified,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = if (shebaValidation?.isValid == true) GreenVerified else MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

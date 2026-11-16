package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BrandPrimary
import com.example.util.Lang
import com.example.util.PersianUtils
import com.example.util.t

const val GENDER_MALE = "آقا"
const val GENDER_FEMALE = "خانم"

/** "آقای علی احمدی" / "خانم سارا رضایی" (or Mr./Ms. in English); the plain name when the gender is unknown. */
fun personDisplayName(name: String, gender: String): String = when (gender) {
    GENDER_MALE -> (if (Lang.isFa) "آقای " else "Mr. ") + name
    GENDER_FEMALE -> (if (Lang.isFa) "خانم " else "Ms. ") + name
    else -> name
}

/** Two toggle chips (male / female). Tapping the selected chip again clears the choice. */
@Composable
fun GenderSelector(gender: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(GENDER_MALE, GENDER_FEMALE).forEach { option ->
            FilterChip(
                selected = gender == option,
                onClick = { onChange(if (gender == option) "" else option) },
                label = { Text(t(option)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandPrimary,
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

/**
 * Landline field with the 021-xxxxxxxx mask. The cursor is placed by counting digits, so editing in the
 * middle of the number (e.g. right after the dash) no longer throws the caret to the end.
 */
@Composable
fun MaskedLandlineField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
) {
    var field by remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    LaunchedEffect(value) {
        if (field.text != value) field = TextFieldValue(value, TextRange(value.length))
    }
    OutlinedTextField(
        value = field,
        onValueChange = { input ->
            val digitsBeforeCursor = input.text.take(input.selection.end).count { it.isDigit() }
            val formatted = PersianUtils.formatLandlinePhone(input.text)
            var pos = 0
            var seen = 0
            while (pos < formatted.length && seen < digitsBeforeCursor) {
                if (formatted[pos].isDigit()) seen++
                pos++
            }
            field = TextFieldValue(formatted, TextRange(pos))
            onValueChange(formatted)
        },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        isError = isError,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions
    )
}

/** Common job titles offered as one-tap helpers under the "job title" field. */
val COMMON_ROLES = listOf(
    "مدیرعامل", "مدیر فروش", "کارشناس فروش", "کارشناس مالی", "حسابدار", "ترانسپورت",
    "کارشناس خرید", "مدیر بازرگانی", "مدیر تولید", "مسئول انبار", "مسئول فنی", "بازاریاب", "منشی", "مدیر پروژه"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoleQuickChips(onPick: (String) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        COMMON_ROLES.forEach { role ->
            AssistChip(
                onClick = { onPick(role) },
                label = { Text(role, style = MaterialTheme.typography.labelSmall) },
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

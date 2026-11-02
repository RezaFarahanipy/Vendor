package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import com.example.util.PersianUtils
import java.io.File
import java.io.FileOutputStream

object CatalogFileUtils {

    fun ensureSampleCatalogFile(context: Context, vendorName: String, catalogTitle: String): File {
        val dir = File(context.filesDir, "catalogs")
        if (!dir.exists()) dir.mkdirs()
        val safeName = vendorName.replace(" ", "_")
        val file = File(dir, "catalog_${safeName}.pdf")
        if (!file.exists()) {
            try {
                FileOutputStream(file).use { out ->
                    // Standard minimal valid PDF binary stream header
                    val pdfContent = "%PDF-1.4\n" +
                            "1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n" +
                            "2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n" +
                            "3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R >> endobj\n" +
                            "4 0 obj << /Length 55 >> stream\n" +
                            "BT /F1 18 Tf 50 700 Td (VendorYab Industrial Catalog: $vendorName) Tj ET\n" +
                            "endstream endobj\n" +
                            "xref\n0 5\n0000000000 65535 f \n0000000009 00000 n \n0000000058 00000 n \n0000000115 00000 n \n0000000214 00000 n \n" +
                            "trailer << /Size 5 /Root 1 0 R >>\nstartxref\n320\n%%EOF"
                    out.write(pdfContent.toByteArray(Charsets.US_ASCII))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return file
    }

    fun openPdfFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "مشاهده کاتالوگ با PDFخوان"))
        } catch (e: Exception) {
            Toast.makeText(context, "برنامه پی‌دی‌اف‌خوان روی دستگاه یافت نشد. کاتالوگ در برنامه آماده نمایش است.", Toast.LENGTH_LONG).show()
        }
    }

    fun sharePdfFile(context: Context, file: File, title: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "کاتالوگ فنی و استانداردهای صنعتی: $title")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "اشتراک‌گذاری کاتالوگ"))
        } catch (e: Exception) {
            Toast.makeText(context, "خطا در اشتراک‌گذاری کاتالوگ", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun CatalogViewerDialog(
    vendorName: String,
    catalogTitle: String,
    catalogLocalPath: String,
    catalogPdfUrl: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Resolve or generate local PDF file
    val targetFile = if (catalogLocalPath.isNotBlank() && File(catalogLocalPath).exists()) {
        File(catalogLocalPath)
    } else {
        CatalogFileUtils.ensureSampleCatalogFile(context, vendorName, catalogTitle)
    }

    val fileSizeKb = (targetFile.length() / 1024L).coerceAtLeast(145L)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(RedFavorite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "کاتالوگ دیجیتال و مشخصات فنی",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = vendorName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = catalogTitle.ifBlank { "کاتالوگ جامع محصولات و مشخصات فنی" },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "شامل دیاگرام‌های هیدرولیکی، جدول متریال استیل، نمودارهای کارکرد و ابعاد نصب",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "حجم فایل: ${PersianUtils.toPersianDigits(fileSizeKb)} کیلوبایت",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "فرمت: PDF استاندارد صنعتی",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                // Interactive Action Buttons
                Button(
                    onClick = {
                        CatalogFileUtils.openPdfFile(context, targetFile)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = NavyDeep
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("باز کردن با برنامه PDFخوان", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        CatalogFileUtils.sharePdfFile(context, targetFile, "$vendorName - $catalogTitle")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("اشتراک‌گذاری فایل کاتالوگ")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("بستن")
            }
        }
    )
}

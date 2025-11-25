package com.example.kostlin.ui.screen.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun SelectDateScreen(
    onDateSelected: (LocalDate) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialDate: LocalDate? = null
) {
    var currentMonth by remember(initialDate) { mutableStateOf(initialDate?.let { YearMonth.from(it) } ?: YearMonth.now()) }
    var selectedDate by remember(initialDate) { mutableStateOf(initialDate) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onBackClick() }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.Center)
                .clickable(enabled = false) { },
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Pilih Tanggal",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2633)
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Month Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentMonth = currentMonth.minusMonths(1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = Color(0xFF1B2633)
                        )
                    }

                    Text(
                        text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B2633)
                        )
                    )

                    IconButton(
                        onClick = { currentMonth = currentMonth.plusMonths(1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = Color(0xFF1B2633)
                        )
                    }
                }

                // Day Headers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab").forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6B7280)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            fontSize = 12.sp
                        )
                    }
                }

                // Calendar Grid
                val firstDay = currentMonth.atDay(1)
                val lastDay = currentMonth.atEndOfMonth()
                val firstDayOfWeek = firstDay.dayOfWeek.value % 7
                val totalDays = lastDay.dayOfMonth

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var dayCounter = 1
                    repeat((firstDayOfWeek + totalDays + 6) / 7) { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(7) { dayOfWeek ->
                                if (week == 0 && dayOfWeek < firstDayOfWeek || dayCounter > totalDays) {
                                    Spacer(modifier = Modifier.weight(1f))
                                } else {
                                    val date = currentMonth.atDay(dayCounter)
                                    val isSelected = selectedDate == date
                                    val isToday = date == LocalDate.now()

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .clickable { selectedDate = date },
                                        shape = CircleShape,
                                        color = when {
                                            isSelected -> Color(0xFF5876FF)
                                            isToday -> Color(0xFF5876FF)
                                            else -> Color.Transparent
                                        }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = dayCounter.toString(),
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (isSelected || isToday) Color.White else Color(0xFF1B2633)
                                                ),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                    dayCounter++
                                }
                            }
                        }
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onBackClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Batal",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        )
                    }

                    Button(
                        onClick = {
                            if (selectedDate != null) {
                                onDateSelected(selectedDate!!)
                                onBackClick()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5876FF)),
                        shape = RoundedCornerShape(8.dp),
                        enabled = selectedDate != null
                    ) {
                        Text(
                            text = "Lanjut",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

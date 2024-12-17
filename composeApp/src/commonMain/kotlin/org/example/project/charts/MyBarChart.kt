package org.example.project.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.project.formatPercentage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBarChart(
    data: List<Pair<String, Double>>,
    maxHeight: Dp = 300.dp,
    barColor: Color = MaterialTheme.colorScheme.primary,
    barHeight: Dp = 20.dp,
    spaceBetweenBars: Dp = 6.dp,
    horizontalPadding: Dp = 16.dp,
    cardPadding: PaddingValues = PaddingValues(0.dp),
    title: String ,
) {
    // Estado para controlar la visibilidad del Dialog
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(cardPadding)
            .fillMaxWidth()
            .heightIn(max = maxHeight),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column {
            // Sección del título con el botón de expansión
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.OpenInFull,
                        contentDescription = "Expandir gráfica",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            // Contenido del gráfico
            BarChart(
                data = data,
                barColor = barColor,
                barHeight = barHeight,
                spaceBetweenBars = spaceBetweenBars,
                horizontalPadding = horizontalPadding,
            )
        }
    }

    // Dialog a pantalla completa
    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Botón para cerrar el Dialog
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(bottom = 16.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        actions = {
                            IconButton(onClick = { expanded = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                        )
                    )

                    BarChart(
                        data = data,
                        barColor = barColor,
                        barHeight = barHeight * 2, // Ajusta el tamaño según prefieras
                        spaceBetweenBars = spaceBetweenBars,
                        horizontalPadding = horizontalPadding,
                    )
                }
            }
        }
    }
}

@Composable
private fun BarChart(
    data: List<Pair<String, Double>>,
    barColor: Color,
    barHeight: Dp,
    spaceBetweenBars: Dp,
    horizontalPadding: Dp,
) {
    if (data.isEmpty()) {
        // Manejo de caso cuando no hay datos
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight * 2),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No hay datos para mostrar", fontSize = 14.sp, color = Color.Gray)
        }
        return
    }

    val maxValue = data.maxOfOrNull { it.second } ?: 0.0
    val minValue = 0.0

    // Animaciones para cada barra
    val progressList = remember {
        data.map { Animatable(0f) }
    }

    // Lanzamos las animaciones
    data.forEachIndexed { index, _ ->
        LaunchedEffect(key1 = index) {
            progressList[index].animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 500 + (index * 50))
            )
        }
    }

    // Column para las barras
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = horizontalPadding, vertical = spaceBetweenBars)
    ) {
        data.forEach { (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spaceBetweenBars / 2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Etiqueta a la izquierda
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(2f),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Barra
                Box(
                    modifier = Modifier
                        .weight(5f)
                        .height(barHeight)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val normalizedValue = if (maxValue == minValue) {
                            1f
                        } else {
                            val range = maxValue - minValue
                            ((value - minValue) / range).toFloat().coerceIn(0f, 1f)
                        }

                        val barWidth = size.width * normalizedValue * progressList[data.indexOf(Pair(label, value))].value

                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x = 0f, y = 0f),
                            size = Size(width = barWidth, height = size.height),
                            cornerRadius = CornerRadius(x = 12f, y = 12f)
                        )
                    }
                }

                // Valor a la derecha
                Text(
                    text = formatPercentage(value.toFloat()),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

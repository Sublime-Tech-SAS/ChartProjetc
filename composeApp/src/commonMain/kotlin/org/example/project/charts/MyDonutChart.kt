package org.example.project.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.RadioButtonChecked
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.project.formatPercentage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDonutChart(
    data: List<Triple<String, Double, Color>>,
    title: String,
    maxHeight: Dp = 300.dp,
    cardPadding: PaddingValues = PaddingValues(0.dp),
    chartSize: Dp = 200.dp,
    donutThickness: Dp = 30.dp, // Grosor del anillo del donut
    gapAngle: Float = 2f // Ángulo de separación entre porciones
) {
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            DonutChart(
                data = data,
                chartSize = chartSize,
                donutThickness = donutThickness,
                gapAngle = gapAngle,
                showLegend = true,
                extended = false
            )
        }
    }

    // Dialog expandido
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
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        },
                        actions = {
                            IconButton(onClick = { expanded = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                        )
                    )

                    DonutChart(
                        data = data,
                        chartSize = chartSize * 1.5f, // Gráfico más grande en modo expandido
                        donutThickness = donutThickness,
                        gapAngle = gapAngle,
                        showLegend = true,
                        extended = true
                    )
                }
            }
        }
    }
}

@Composable
private fun DonutChart(
    data: List<Triple<String, Double, Color>>,
    chartSize: Dp,
    donutThickness: Dp,
    gapAngle: Float,
    showLegend: Boolean = true,
    extended: Boolean = false
) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No hay datos para mostrar", fontSize = 14.sp, color = Color.Gray)
        }
        return
    }

    val total = data.sumOf { it.second }
    val angles = data.map { (it.second / total * 360f) }

    // Animación del finalAngle (para que el donut se vaya "llenando")
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(key1 = data) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
    }

    // Calculamos los ángulos ya animados
    val animatedAngles = angles.map { it * animatedProgress.value }

    // Calculamos si es necesario escalar las porciones para evitar superposición
    // Suma total requerida: sum(arcos) + N * gap
    val totalNeeded = animatedAngles.sum() + data.size * gapAngle
    val scaleFactor = if (totalNeeded > 360f) 360f / totalNeeded.toFloat() else 1f

    // Ángulo mínimo para asegurar que no desaparezca la porción
    val minAngle = 0.5f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .let { if (extended) it.height(chartSize) else it }
    ) {
        // Gráfico
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .size(chartSize)
                .weight(1f)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(chartSize)) {
                var startAngle = 0f
                animatedAngles.forEachIndexed { index, angle ->
                    // Escalamos el ángulo para evitar sobreposición
                    val scaledAngle = angle.toFloat() * scaleFactor
                    // Ajustamos el sweepAngle restando el gap
                    var sweepAngle = scaledAngle - gapAngle

                    // Aseguramos que nunca sea menor a minAngle para que no desaparezca
                    if (sweepAngle < minAngle) {
                        sweepAngle = minAngle
                    }

                    // Dibujamos el arco con un pequeño desplazamiento para dejar el gap visual
                    drawArc(
                        color = data[index].third,
                        startAngle = startAngle + (gapAngle*index),
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = donutThickness.toPx(), cap = StrokeCap.Round)
                    )

                    // Avanzamos el startAngle con el ángulo escalado original (sin el gap)
                    // de esta manera mantenemos la consistencia en la posición de las siguientes porciones
                    startAngle += scaledAngle
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showLegend) {
            // Leyenda
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                data.forEach { (label, value, color) ->
                    val percentage = if (total == 0.0) 0.0 else (value / total * 100)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.RadioButtonChecked,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Text(
                            text = "$label: ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = formatPercentage(percentage.toFloat()),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}


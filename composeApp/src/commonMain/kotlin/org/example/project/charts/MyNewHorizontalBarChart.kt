package org.example.project.charts

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Muestra un gráfico de barras horizontales con líneas verticales y un eje X de valores.
 * Ahora recibe varios datasets, cada uno asociado a un subtítulo (key).
 *
 * Agrega la posibilidad de animar más fluidamente la transición entre datasets
 * y la animación de las barras al cambiar de valor y color.
 *
 * @param title Título principal de la gráfica
 * @param dataSets Mapa con subtítulo como key y lista de barras como value
 * @param showAsPercentage Indica si la gráfica muestra porcentajes en lugar de valores absolutos
 * @param contentTransitionDuration Duración de la animación al cambiar de dataset (ms)
 * @param barAnimationDuration Duración de la animación de cada barra (ms)
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MyNewHorizontalBarChart(
    modifier: Modifier = Modifier,
    title: String,
    dataSets: Map<String, List<Triple<String, Double, Color>>>,
    showAsPercentage: Boolean = false,
    contentTransitionDuration: Int = 1200,
    barAnimationDuration: Int = 1200
) {
    // Subtítulos disponibles
    val subtitles = dataSets.keys.toList()

    // Subtítulo seleccionado (dataset actual)
    var selectedSubtitle by remember { mutableStateOf(subtitles.firstOrNull() ?: "") }
    val selectedData = dataSets[selectedSubtitle] ?: emptyList()

    ChartCard(
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            ChartTitle(title)
            Spacer(Modifier.height(8.dp))

            // Fila de botones para cambiar subtítulo/dataset
            SubtitlesRow(
                subtitles = subtitles,
                selectedSubtitle = selectedSubtitle,
                onSubtitleSelected = { subtitle ->
                    selectedSubtitle = subtitle
                }
            )

            Spacer(Modifier.height(8.dp))

            // Leyenda de la gráfica (subtítulos y valores)
            ChartSubTitlesAndValues(selectedData)

            Spacer(Modifier.height(16.dp))

            // Usamos AnimatedContent para animar el cambio de dataset
            AnimatedContent(
                targetState = selectedData,
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(contentTransitionDuration)
                    ) + expandVertically(
                        animationSpec = tween(contentTransitionDuration)
                    ) with fadeOut(
                                animationSpec = tween(contentTransitionDuration)
                            ) + shrinkVertically(
                        animationSpec = tween(contentTransitionDuration)
                    ).plus(
                        exit = slideOutVertically()
                    )
                },
                label = "BarsAnimatedContent"
            ) { currentData ->
                Bars(
                    data = currentData,
                    showAsPercentage = showAsPercentage,
                    lineCount = 12,
                    barAnimationDuration = barAnimationDuration
                )
            }
        }
    }
}

/**
 * Envoltorio con estilo de Card.
 */
@Composable
fun ChartCard(
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit),
) {
    Card(
        modifier = modifier.padding(top = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder(),
        content = content
    )
}

/**
 * Título principal de la gráfica.
 */
@Composable
fun ChartTitle(
    title: String,
    color: Color = Color.Black,
    fontSize: Float = 16f,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Text(
        text = title,
        color = color,
        fontSize = fontSize.sp,
        fontWeight = fontWeight
    )
}

/**
 * Lista horizontal de botones para seleccionar subtítulo/dataset.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubtitlesRow(
    subtitles: List<String>,
    selectedSubtitle: String,
    onSubtitleSelected: (String) -> Unit
) {
    if(subtitles.size > 1){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            subtitles.forEach { subtitle ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable{
                            onSubtitleSelected(subtitle)
                        }
                        .padding(8.dp)
                ){
                    Text(
                        text = subtitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if(subtitle == selectedSubtitle) Color.Black else Color.Gray
                    )
                    if(subtitle == selectedSubtitle){
                        HorizontalDivider(
                            modifier = Modifier
                                .clip(CircleShape)
                                .width(16.dp),
                            thickness = 2.dp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

/**
 * Muestra en un Row (con efecto marquee) la "leyenda" con (label, valor, color).
 */
@Composable
fun ChartSubTitlesAndValues(
    data: List<Triple<String, Double, Color>>
) {
    Row(
        modifier = Modifier
            .basicMarquee(
                iterations = Int.MAX_VALUE,
                spacing = MarqueeSpacing(0.dp)
            )
    ) {
        data.forEach { (label, quantity, color) ->
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30))
                            .background(color)
                            .size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = quantity.toInt().toString(),
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

/**
 * Dibuja las barras. Cada barra puede animar su tamaño y color.
 *
 * @param barAnimationDuration Controla la duración de la animación (en ms) al cambiar valor/color
 */
@Composable
fun Bars(
    data: List<Triple<String, Double, Color>>,
    showAsPercentage: Boolean,
    modifier: Modifier = Modifier,
    lineCount: Int = 12,
    barAnimationDuration: Int = 1200
) {
    val maxValue = data.maxOfOrNull { it.second } ?: 1.0

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .heightIn(max = 300.dp)
            .drawBehind {
                val spacing = size.width / (lineCount - 1)
                for (i in 0 until lineCount) {
                    val x = spacing * i
                    val isSolid = (i % 2 == 0)
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = if (isSolid) null else PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                    )
                }
            }
    ) {
        items(
            items = data,
            key = { it.first } // Usamos la etiqueta como key
        ) { (label, value, color) ->

            // 1) Calculamos la fracción objetivo
            val fractionTarget = if (maxValue == 0.0) 0f else (value / maxValue).toFloat()
            // Evitar barra de ancho cero
            val fractionSafe = fractionTarget.coerceAtLeast(0.05f)

            // 2) Animamos la fracción de anchura
            val animatedFraction by animateFloatAsState(
                targetValue = fractionSafe,
                animationSpec = tween(durationMillis = barAnimationDuration)
            )

            // 3) Animamos el color
            val animatedColor by animateColorAsState(
                targetValue = color,
                animationSpec = tween(durationMillis = barAnimationDuration)
            )

            // 4) Construimos el texto a mostrar (valor o porcentaje)
            val displayedValue = if (showAsPercentage) {
                "${(fractionTarget * 100).toInt()}%"
            } else {
                value.toInt().toString()
            }

            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "$label ($displayedValue)",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Barra
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .height(24.dp)
                        // El ancho de la barra se define por la fracción animada
                        .fillMaxWidth(animatedFraction)
                        .clip(RoundedCornerShape(30))
                        .background(animatedColor)
                )
            }
        }
    }
}

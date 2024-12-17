package org.example.project.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import org.example.project.DottedDivider
import kotlin.math.ceil
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLineChart(
    data: List<Pair<String, Double>>,
    xMinPointSpacing: Dp = 100.dp,
    yMinPointSpacing: Dp = 50.dp,
    modifier: Modifier = Modifier,
    axisColor: Color = Color.Black,
    axisStrokeWidth: Float = 2f,
    intervalLineColor: Color = Color.LightGray,
    intervalStrokeWidth: Float = 1f,
    fillGraph: Boolean = true, // parámetro para activar el llenado
    fillColor: Color = MaterialTheme.colorScheme.tertiary, // parámetro para el color de llenado
    lineColor: Color = MaterialTheme.colorScheme.primary, // parámetro para el color de la línea
    pointColor: Color = MaterialTheme.colorScheme.secondary, // parámetro para el color de los puntos
    smoothLines: Boolean = true, // Parámetro para líneas suavizadas
    title: String,
    maxHeight: Dp = 300.dp,
    cardPadding: PaddingValues = PaddingValues(0.dp),
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
        Column {
            // Encabezado con botón para expandir
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
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

            // Contenido de la gráfica de línea
            LineChart(
                data = data,
                xMinPointSpacing = xMinPointSpacing,
                yMinPointSpacing = yMinPointSpacing,
                modifier = modifier,
                axisColor = axisColor,
                axisStrokeWidth = axisStrokeWidth,
                intervalLineColor = intervalLineColor,
                intervalStrokeWidth = intervalStrokeWidth,
                fillGraph = fillGraph,
                fillColor = fillColor,
                lineColor = lineColor,
                pointColor = pointColor,
                smoothLines = smoothLines
            )
        }
    }

    // Dialog a pantalla completa para versión expandida
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
                    .fillMaxWidth()
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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

                    // Aquí en expandido, la gráfica debe ocupar todo el ancho disponible
                    LineChart(
                        data = data,
                        xMinPointSpacing = xMinPointSpacing,
                        yMinPointSpacing = yMinPointSpacing,
                        modifier = modifier,
                        axisColor = axisColor,
                        axisStrokeWidth = axisStrokeWidth,
                        intervalLineColor = intervalLineColor,
                        intervalStrokeWidth = intervalStrokeWidth,
                        fillGraph = fillGraph,
                        fillColor = fillColor,
                        lineColor = lineColor,
                        pointColor = pointColor,
                        smoothLines = smoothLines
                    )
                }
            }
        }
    }
}

@Composable
fun LineChart(
    data: List<Pair<String, Double>>,
    xMinPointSpacing: Dp = 100.dp,
    yMinPointSpacing: Dp = 50.dp,
    modifier: Modifier = Modifier,
    axisColor: Color = Color.Black,
    axisStrokeWidth: Float = 2f,
    intervalLineColor: Color = Color.LightGray,
    intervalStrokeWidth: Float = 1f,
    fillGraph: Boolean = true, // Nuevo parámetro para activar el llenado
    fillColor: Color = MaterialTheme.colorScheme.tertiary, // Nuevo parámetro para el color de llenado
    lineColor: Color = MaterialTheme.colorScheme.primary, // Nuevo parámetro para el color de la línea
    pointColor: Color = MaterialTheme.colorScheme.secondary, // Nuevo parámetro para el color de los puntos
    smoothLines: Boolean = true // Parámetro para líneas suavizadas
) {
    // Si no hay data, no dibujamos nada
    if (data.isEmpty()) return

    BoxWithConstraints(
        modifier = modifier
            .padding(end = 16.dp)
    ) {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()

        val density = LocalDensity.current
        val xMinSpacingPx = with(density) { xMinPointSpacing.toPx() }
        val yMinSpacingPx = with(density) { yMinPointSpacing.toPx() }

        val labelsX = data.map { it.first }
        val valuesY = data.map { it.second }
        val maxYValue = valuesY.maxOrNull() ?: 0.0

        // Determinar step en Y
        val yStep = if (maxYValue < 50) {
            5.0
        } else {
            val usableHeight = maxHeightPx * 0.8f
            val possibleIntervals = (usableHeight / yMinSpacingPx).toInt().coerceAtLeast(1)
            val rawStep = maxYValue / possibleIntervals
            val scaleFactor = 10.0
            (ceil(rawStep / scaleFactor) * scaleFactor)
        }

        val yIntervals = generateSequence(0.0) { it + yStep }
            .takeWhile { it <= maxYValue*2f }
            .toList()

        val numberOfPoints = labelsX.size

        val paddingLeft = 60f
        val paddingBottom = 60f
        val axisX = maxHeightPx - paddingBottom
        val axisY = paddingLeft

        val chartAvailableWidth = maxWidthPx - axisY - 20f
        val idealSpacing = if (numberOfPoints > 1) {
            chartAvailableWidth / (numberOfPoints - 1)
        } else {
            chartAvailableWidth
        }

        val finalXSpacing = if (numberOfPoints > 1) {
            max(idealSpacing, xMinSpacingPx)
        } else {
            chartAvailableWidth / 2f
        }

        val totalRequiredWidth = if (numberOfPoints > 1) {
            axisY + ((numberOfPoints - 1) * finalXSpacing) + 20f
        } else {
            axisY + chartAvailableWidth / 2f + 20f
        }

        val horizontalScrollState = rememberScrollState()

        // Una vez que la composición está lista, llevamos el scroll al final.
        // Esto hará que el scroll inicial esté en el máximo valor posible.
        LaunchedEffect(totalRequiredWidth) {
            // Un pequeño delay para asegurar que el layout calcule el maxValue del scroll
            delay(10)
            horizontalScrollState.scrollTo(horizontalScrollState.maxValue)
        }

        val textColor = MaterialTheme.colorScheme.onSurface
        val topPadding = 20.dp

        // Construimos la UI en un Row:
        // - Izquierda: eje Y fijo y etiquetas.
        // - Derecha: Canvas con scroll horizontal (eje X, datos, lineas horizontales).
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Eje Y y etiquetas
            Column(
                modifier = Modifier
                    //.width(with(density) { axisY.toDp() })
                    .fillMaxHeight()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                for (intervalValue in yIntervals.sortedByDescending { it }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
            //.fillMaxWidth()
                    ) {
                        Text(
                            text = intervalValue.toInt().toString(),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
                            color = textColor,
                            modifier = Modifier
                                .width(50.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }

            VerticalDivider(
                modifier = Modifier
                    .padding(bottom = 39.dp),
                color = Color.Black
            )

            // Parte scrollable (X)
            Box(

            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(horizontalScrollState)
                ) {
                    Canvas(
                        modifier = Modifier
                            .width(
                                if (totalRequiredWidth > maxWidthPx) {
                                    with(density) {
                                        ( axisY + ((numberOfPoints - 1) * finalXSpacing)).toDp()
                                    }
                                } else {
                                    with(density) { maxWidthPx.toDp() }
                                }
                            )
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        // Dibuja eje X
                        drawLine(
                            color = axisColor,
                            strokeWidth = axisStrokeWidth,
                            start = Offset(x = 0f, y = axisX),
                            end = Offset(x = axisY + (finalXSpacing * (numberOfPoints - 1)), y = axisX)
                        )

                        val chartHeight = axisX - with(density) { topPadding.toPx() }

                        // ------------------ NUEVA LÓGICA PARA DIBUJAR LA GRÁFICA ------------------

                        // Calcular posiciones de puntos
                        val points = data.mapIndexed { index, pair ->
                            val value = pair.second
                            val xPos = finalXSpacing * index
                            val yPos = axisX - (value / maxYValue * chartHeight)
                            Offset(xPos, yPos.toFloat())
                        }

                        // Función auxiliar para crear el Path de la línea
                        fun createLinePath(points: List<Offset>, smoothLines: Boolean): Path {
                            return Path().apply {
                                if (points.isNotEmpty()) {
                                    if (smoothLines) {
                                        moveTo(points[0].x, points[0].y)
                                        for (i in 1 until points.size) {
                                            val prev = points[i - 1]
                                            val current = points[i]
                                            val controlPointDiv = 2.0f
                                            val controlX1 = prev.x + (current.x - prev.x) / controlPointDiv
                                            val controlY1 = prev.y
                                            val controlX2 = current.x - (current.x - prev.x) / controlPointDiv
                                            val controlY2 = current.y

                                            cubicTo(
                                                controlX1,
                                                controlY1,
                                                controlX2,
                                                controlY2,
                                                current.x,
                                                current.y
                                            )
                                        }
                                    } else {
                                        moveTo(points[0].x, points[0].y)
                                        for (i in 1 until points.size) {
                                            lineTo(points[i].x, points[i].y)
                                        }
                                    }
                                }
                            }
                        }

                        // Crear el Path de la línea
                        val path = createLinePath(points, smoothLines)

                        // Dibujar el área de llenado si está activado
                        if (fillGraph && points.isNotEmpty()) {
                            val fillPath = Path().apply {
                                // Iniciar en el eje X en el primer punto
                                moveTo(points[0].x, axisX)
                                // Línea hacia el primer punto de la gráfica
                                lineTo(points[0].x, points[0].y)
                                // Añadir la curva de la gráfica
                                if (smoothLines) {
                                    // Crear el mismo Path suavizado que la línea de la gráfica
                                    for (i in 1 until points.size) {
                                        val prev = points[i - 1]
                                        val current = points[i]
                                        val controlPointDiv = 2.0f
                                        val controlX1 = prev.x + (current.x - prev.x) / controlPointDiv
                                        val controlY1 = prev.y
                                        val controlX2 = current.x - (current.x - prev.x) / controlPointDiv
                                        val controlY2 = current.y

                                        cubicTo(
                                            controlX1,
                                            controlY1,
                                            controlX2,
                                            controlY2,
                                            current.x,
                                            current.y
                                        )
                                    }
                                } else {
                                    // Conectar los puntos con líneas rectas
                                    for (i in 1 until points.size) {
                                        lineTo(points[i].x, points[i].y)
                                    }
                                }
                                // Línea desde el último punto hacia el eje X
                                lineTo(points.last().x, axisX)
                                // Cerrar el Path para formar una figura cerrada
                                close()
                            }

                            // Dibujar el área rellena
                            drawPath(
                                path = fillPath,
                                color = fillColor,
                                style = Fill
                            )
                        }

                        // Dibujar la línea que conecta los puntos
                        drawPath(
                            path = path,
                            color = lineColor, // Usar el color de línea proporcionado
                            style = Stroke(width = 2f)
                        )


                        // Dibujar líneas guía horizontales por cada intervalo del eje Y
//                        for (intervalValue in yIntervals) {
//                            val yPos = axisX - (intervalValue / maxYValue * chartHeight)
//                            drawLine(
//                                color = intervalLineColor,
//                                strokeWidth = intervalStrokeWidth,
//                                start = Offset(0f, yPos.toFloat()),
//                                end = Offset(axisY + ((numberOfPoints - 1) * finalXSpacing), yPos.toFloat()),
//                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
//                            )
//                        }

                        // Dibujar los puntos
                        for (point in points) {
                            drawCircle(
                                color = pointColor, // Usar el color de puntos proporcionado
                                radius = 5f,
                                center = point
                            )
                        }
                    }

                    // Etiquetas del eje X
                    Row(
                        modifier = Modifier
                            .width(
                                if (totalRequiredWidth > maxWidthPx) {
                                    with(density) {
                                        (((numberOfPoints - 1) * finalXSpacing) + 20f).toDp()
                                    }
                                } else {
                                    with(density) { maxWidthPx.toDp() }
                                }
                            )
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (numberOfPoints == 1) {
                            Text(
                                text = labelsX.firstOrNull().orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor,
                                modifier = Modifier
                                    .offset(x = (axisY + chartAvailableWidth / 2f).dp, y = 0.dp)
                            )
                        } else {
                            labelsX.forEachIndexed { index, label ->
                                Box(
                                    modifier = Modifier
                                        .height(24.dp),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }

                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 32.dp)
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    yIntervals.forEach { interval ->
                        DottedDivider(
                            color = Color.LightGray,
                            thickness = 1.dp,
                            dashWidth = 10f,
                            dashGap = 15f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}


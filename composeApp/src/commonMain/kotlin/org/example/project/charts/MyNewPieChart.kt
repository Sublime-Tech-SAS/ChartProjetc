package org.example.project.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PieChartCard(
    modifier: Modifier = Modifier,
    title: String,
    data: List<Triple<String, Double, Color>>
) {
    ChartCard(
        modifier = modifier.padding(horizontal = 16.dp),
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            ChartTitle(title)
            Spacer(Modifier.height(16.dp))
            Row(

            ){
                ChartSubtitlesAndVAlues(data)
                PieGraph(
                    data = data,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

}

@Composable
private fun ChartSubtitlesAndVAlues(
    data: List<Triple<String, Double, Color>>
){
    Column{
        data.forEach { (label, quantity, color) ->
            Column(
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30))
                            .background(color)
                            .height(16.dp)
                            .width(4.dp)
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
                    text = "${quantity.toInt()}%",
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }

}

@Composable
private fun PieGraph(
    data: List<Triple<String, Double, Color>>,
    modifier: Modifier = Modifier
){
    val total = data.sumOf { it.second }
    val angles = data.map { it.second / total * 360f }
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(key1 = data) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
    }
    Canvas(modifier = modifier.fillMaxSize().aspectRatio(1f)) {
        var startAngle = 0f
        angles.forEachIndexed { index, angle ->
            val sweepAngle = (angle * animatedProgress.value).toFloat()
            val sliceColor = data[index].third
            drawArc(
                color = sliceColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
            startAngle += angle.toFloat()
        }
    }
}


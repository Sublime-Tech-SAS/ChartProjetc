package org.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.example.project.charts.MyBarChart
import org.example.project.charts.MyPieChart
import org.example.project.charts.MyDonutChart
import org.example.project.charts.MyLineChart
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val domicilioData = listOf(
            Triple("Urbano", 60.0, MaterialTheme.colorScheme.primary),
            Triple("Rural", 40.0, MaterialTheme.colorScheme.tertiary)
        )

        val amenazaData = listOf(
            Pair("Amenaza", 3.0),
            Pair("Atentado", 4.0),
            Pair("Secuestro", 2.0),
            Pair("Homicidio", 1.0),
            Pair("Extorsión", 1.0),
            Pair("Reclutamiento ilegal", 1.0),
            Pair("Otra", 0.0),
        ).sortedByDescending { it.second }

        val sexoData = listOf(
            Triple("Hombre", 40.0, MaterialTheme.colorScheme.primary),
            Triple("Mujer", 56.0, MaterialTheme.colorScheme.secondary),
            Triple("Intersexual", 4.0, MaterialTheme.colorScheme.tertiary)
        )

        val  tendenciaTemporal = listOf(
            Pair("Ene", 30.0),
            Pair("Ene", 0.0),
            Pair("Ene", 30.0),
            Pair("Mar", 60.0),
            Pair("Mar", 90.0),
            Pair("Feb", 120.0),
            Pair("Feb", 143.0),
        )

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(1),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item{
                MyLineChart(
                    data = tendenciaTemporal,
                    title = "Tendencia temporal",
                    maxHeight = 500.dp
                )
            }
            item{
                MyBarChart(
                    title = "Situación de riesgo",
                    data = amenazaData,
                    maxHeight = 400.dp
                )
            }
            item{
                MyPieChart(
                    data = domicilioData,
                    title = "Zona de Domicilio",
                    chartSize = 250.dp, // Tamaño del gráfico
                    maxHeight = 400.dp, // Altura máxima del Card
                )
            }
            item{
                MyDonutChart(
                    data = sexoData,
                    title = "Sexo",
                    maxHeight = 500.dp,
                    chartSize = 250.dp,
                    gapAngle = 7f,
                    donutThickness = 20.dp
                )
            }
        }

    }
}
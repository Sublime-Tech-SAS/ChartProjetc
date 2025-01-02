package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.charts.MyBarChart
import org.example.project.charts.MyPieChart
import org.example.project.charts.MyDonutChart
import org.example.project.charts.MyLineChart
import org.example.project.charts.MyNewHorizontalBarChart
import org.example.project.charts.PieChartCard
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

        val amenazaDataWithColor = listOf(
            Triple("Amenaza", 2650.0, Color(0xFFFFCB04)), // Amarillo brillante
            Triple("Atentado", 2421.0, Color(0xFF28913B)), // Verde oscuro
            Triple("Secuestro", 2342.0, Color(0xFF6EA6FF)), // Azul claro
            Triple("Homicidio", 2000.0, Color(0xFFD93025)), // Rojo intenso
            Triple("Extorsión", 41.0, Color(0xFFFFA500)), // Naranja vibrante
            Triple("Reclutamiento ilegal", 190.0, Color(0xFF8B00FF)), // Morado oscuro
            Triple("Otra", 232.0, Color(0xFF00CED1)) // Turquesa
        ).sortedByDescending { it.second }

        val zonaDeResicencia = listOf(
            Triple("Urban", 2.0, Color(0xFF9DAAF2)),
            Triple("Rural", 98.0, Color(0xFF070047))
        )

        val dataSets = mapOf(
            "Género" to listOf(
                Triple("Masculino", 40.0, Color(0xFF4285F4)),
                Triple("Femenino", 50.0, Color(0xFFEA4335)),
                Triple("Otro", 10.0, Color(0xFFFBBC05))
            ),
            "Sexo" to listOf(
                Triple("Biológico M", 45.0, Color(0xFF34A853)),
                Triple("Biológico F", 55.0, Color(0xFFFBBC05))
            ),
            "Orientación" to listOf(
                Triple("Heterosexual", 70.0, Color(0xFFAB47BC)),
                Triple("Homosexual", 20.0, Color(0xFFFF7043)),
                Triple("Bisexual", 10.0, Color(0xFF26C6DA))
            )
        )
        val dataSetsV2 = mapOf(
            "Género" to listOf(
                Triple("Masculino", 40.0, Color(0xFF4285F4)),
                Triple("Femenino", 50.0, Color(0xFFEA4335)),
                Triple("Otro", 10.0, Color(0xFFFBBC05))
            )
        )


        Column(
            modifier = Modifier
                .background(Color(0XFFF4F4F4))
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ){
            MyNewHorizontalBarChart(
                title = "Situación de riesgo",
                dataSets = dataSets
            )
            MyNewHorizontalBarChart(
                title = "Situación de riesgo",
                dataSets = dataSetsV2
            )

            // Llamas a tu Card
            PieChartCard(
                title = "Residence Zone",
                data = zonaDeResicencia
            )
        }
//        LazyVerticalStaggeredGrid(
//            columns = StaggeredGridCells.Fixed(1),
//            verticalItemSpacing = 16.dp,
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            contentPadding = PaddingValues(16.dp),
//            modifier = Modifier
//                .background(Color.LightGray)
//        ) {
//            item{
//                MyLineChart(
//                    data = tendenciaTemporal,
//                    title = "Tendencia temporal",
//                    maxHeight = 500.dp
//                )
//            }
//            item{
//                MyBarChart(
//                    title = "Situación de riesgo",
//                    data = amenazaData,
//                    maxHeight = 400.dp
//                )
//            }
//            item{
//                MyPieChart(
//                    data = domicilioData,
//                    title = "Zona de Domicilio",
//                    chartSize = 250.dp, // Tamaño del gráfico
//                    maxHeight = 400.dp, // Altura máxima del Card
//                )
//            }
//            item{
//                MyDonutChart(
//                    data = sexoData,
//                    title = "Sexo",
//                    maxHeight = 500.dp,
//                    chartSize = 250.dp,
//                    gapAngle = 7f,
//                    donutThickness = 20.dp
//                )
//            }
//        }

    }
}
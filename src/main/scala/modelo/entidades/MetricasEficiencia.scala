package modelo.entidades

case class MetricasEficiencia(
                               ratioAhorro: Double,
                               eficienciaPresupuestaria: Double,
                               velocidadProgresoMetas: Double,
                               estabilidadFinanciera: Double,
                               promedioGastosMensual: Double,
                               desviacionEstandarGastos: Double,
                               presupuestosCumplidos: Int,
                               presupuestosExcedidos: Int,
                               metasCompletadas: Int,
                               metasEnProgreso: Int,
                               tiempoPromedioMeta: Double
                             )

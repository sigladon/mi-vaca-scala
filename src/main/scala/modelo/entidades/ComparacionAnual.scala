package modelo.entidades

case class ComparacionAnual(
                             anio: String,
                             ingresos: Double,
                             gastos: Double,
                             ahorro: Double,
                             crecimientoAnual: Double,
                             metasCompletadas: Int,
                             presupuestosCumplidos: Int
                           )

package modelo.entidades

case class ComparacionMensual(
                               mes: String,
                               ingresos: Double,
                               gastos: Double,
                               ahorro: Double,
                               crecimientoIngresos: Double,
                               crecimientoGastos: Double,
                               crecimientoAhorro: Double
                             )

package modelo.entidades

case class TendenciaMensual(
                             mes: String,
                             ingresos: Double,
                             gastos: Double,
                             ahorro: Double,
                             crecimiento: Double
                           )

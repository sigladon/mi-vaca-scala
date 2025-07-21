package modelo.entidades

case class ComparacionTrimestral(
                                  trimestre: String,
                                  ingresos: Double,
                                  gastos: Double,
                                  ahorro: Double,
                                  eficiencia: Double,
                                  estabilidad: Double
                                )

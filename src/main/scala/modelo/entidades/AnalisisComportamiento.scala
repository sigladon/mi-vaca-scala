package modelo.entidades

case class AnalisisComportamiento(
                                   patronesGasto: List[PatronGasto],
                                   habitosFinancieros: List[HabitoFinanciero],
                                   tendenciasMensuales: List[TendenciaMensual],
                                   categoriaMasGastada: String,
                                   categoriaMenosGastada: String,
                                   diaSemanaMasGastado: String,
                                   mesMasGastado: String,
                                   frecuenciaCompras: Double,
                                   variabilidadGastos: Double,
                                   consistenciaAhorro: Double
                                 )

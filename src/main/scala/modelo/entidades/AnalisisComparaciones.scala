package modelo.entidades

case class AnalisisComparaciones(
                                  comparacionesMensuales: List[ComparacionMensual],
                                  comparacionesCategorias: List[ComparacionCategoria],
                                  comparacionesTrimestrales: List[ComparacionTrimestral],
                                  comparacionesAnuales: List[ComparacionAnual],
                                  mejorMes: String,
                                  peorMes: String,
                                  categoriaMasMejorada: String,
                                  categoriaMasEmpeorada: String,
                                  tendenciaGeneral: String,
                                  periodosAnalizados: Int
                                )

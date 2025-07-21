package modelo.entidades

case class ComparacionCategoria(
                                 categoria: String,
                                 gastoActual: Double,
                                 gastoAnterior: Double,
                                 diferencia: Double,
                                 porcentajeCambio: Double,
                                 tendencia: String
                               )

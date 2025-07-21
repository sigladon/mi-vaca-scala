package modelo.entidades

case class PatronGasto(
                        categoria: String,
                        frecuencia: Int,
                        montoPromedio: Double,
                        tendencia: String,
                        diaSemanaFavorito: String,
                        mesMasActivo: String
                      )

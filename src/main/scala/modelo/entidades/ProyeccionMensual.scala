package modelo.entidades

case class ProyeccionMensual(
                              mes: String,
                              ingresosProyectados: Double,
                              gastosProyectados: Double,
                              ahorroProyectado: Double,
                              confianza: Double
                            )

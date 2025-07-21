package modelo.entidades

case class ProyeccionPresupuesto(
                                  presupuesto: String,
                                  limiteActual: Double,
                                  gastoProyectado: Double,
                                  excesoEstimado: Double,
                                  recomendacion: String
                                )

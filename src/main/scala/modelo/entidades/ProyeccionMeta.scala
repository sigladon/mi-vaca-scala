package modelo.entidades

import java.time.LocalDate

case class ProyeccionMeta(
                           meta: String,
                           fechaEstimada: LocalDate,
                           probabilidadExito: Double,
                           montoNecesario: Double,
                           recomendacion: String
                         )

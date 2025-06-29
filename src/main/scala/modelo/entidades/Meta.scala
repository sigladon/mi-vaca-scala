package modelo.entidades

import java.time.LocalDate
import java.util.Date

case class Meta(
               nombre: String,
               montoObjetivo: Double,
               fechaLimite: LocalDate,
               descripcion: String,
               fechaInicio: LocalDate = LocalDate.now(),
               montoActual: Double = 0,
               estaActivo: Boolean,
               id: String,
               movimientos: Set[String]
               ) {

}

package modelo.entidades

import java.time.LocalDate
import java.io.Serializable

case class Meta(
  nombre: String,
  montoObjetivo: Double,
  fechaLimite: LocalDate,
  descripcion: String,
  fechaInicio: LocalDate = LocalDate.now(),
  estaActivo: Boolean,
  id: Int,
  movimientos: Set[Int]
) extends Serializable

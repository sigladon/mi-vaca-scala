package modelo.entidades

import java.time.LocalDate
import java.io.Serializable

case class Movimiento(
  monto: Double,
  descripcion: String,
  fechaTransaccion: LocalDate,
  notas: String,
  categoria: Option[String],
  idRelacionado: Option[Int],
  id: Int,
  estado: String
) extends Serializable

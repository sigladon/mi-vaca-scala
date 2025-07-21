package modelo.entidades

import java.time.LocalDate
import java.io.Serializable

case class Presupuesto(
  nombre: String,
  limite: Double,
  inicioPresupuesto: LocalDate,
  finPresupuesto: LocalDate,
  notificarUsuario: Boolean,
  categorias: Map[String, Boolean],
  id: Int,
  estaActivo: Boolean,
  Notas: String,
  Movimientos: Set[Int]
) extends Serializable

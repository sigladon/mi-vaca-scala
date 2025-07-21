package modelo.entidades

import java.io.Serializable

case class Contadores(
  siguienteIdUsuario: Int = 1,
  siguienteIdPresupuesto: Int = 1,
  siguienteIdMeta: Int = 1,
  siguienteIdMovimiento: Int = 1,
  siguienteIdCategoria: Int = 1
) extends Serializable 
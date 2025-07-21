package modelo.entidades

import java.io.Serializable

case class Usuario(
  username: String,
  contrasenia: String,
  nombre: String,
  id: Int,
  correos: List[String],
  movimientos: List[Movimiento],
  presupuestos: List[Presupuesto],
  metas: List[Meta],
  categorias: List[Categoria]
) extends Serializable {
  def verificarContrasenia(contraseniaIngresada: String): Boolean = contrasenia == contraseniaIngresada
}

object Usuario {
  def hashearContrasenia(contrasenia: String): String = contrasenia.hashCode.toString
}

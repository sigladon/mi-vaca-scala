package modelo.entidades

import java.io.Serializable

case class Token(
  uuid: String,
  idUsuario: Int
) extends Serializable

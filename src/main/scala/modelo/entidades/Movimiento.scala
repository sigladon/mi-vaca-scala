package modelo.entidades

import java.time.LocalDate

case class Movimiento(
                     monto: Double,
                     tipoMovimiento: TipoMovimiento,
                     descripcion: String,
                     fechaTransaccion: LocalDate,
                     notas: String,
                     categoria: Option[String],
                     idRelacionado: Option[String],
                     id: String,
                     estado: String
                     )

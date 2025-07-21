package modelo.entidades

import java.time.LocalDate

case class Alerta(
                   tipo: String,
                   titulo: String,
                   descripcion: String,
                   severidad: String,
                   fecha: LocalDate,
                   accionRecomendada: String
                 )

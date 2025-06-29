package modelo.entidades

case class Usuario(
                  username: String,
                  contrasenia: String,
                  nombre: String,
                  id: String,
                  correos: List[String],
                  movimientos: List[Movimiento],
                  presupuestos: List[Presupuesto],
                  metas: List[Meta],
                  categorias: List[Categoria]
                  ) {

}

package seeder

import modelo.entidades._
import modelo.servicios.GestorDatos
import java.time.LocalDate

object Seeder {
  def main(args: Array[String]): Unit = {
    // Limpiar archivos de datos
    val datosPath = "src/main/scala/datos/"
    Seq("usuarios.dat", "movimientos.dat", "presupuestos.dat", "metas.dat", "categorias.dat").foreach { file =>
      val f = new java.io.File(datosPath + file)
      if (f.exists()) f.delete()
    }

    // Crear categorías
    val categorias = List(
      Categoria("Alimentación", 1),
      Categoria("Transporte", 2),
      Categoria("Entretenimiento", 3),
      Categoria("Servicios", 4),
      Categoria("Salud", 5),
      Categoria("Educación", 6),
      Categoria("Ropa", 7),
      Categoria("Otros", 8),
      Categoria("Sueldo", 9),
      Categoria("Freelance", 10),
      Categoria("Vivienda", 11)
    )

    // Crear movimientos (enero-julio)
    val movimientos = List(
      // Enero
      Movimiento(1200, "Sueldo mensual", LocalDate.of(2025,1,1), "Pago empresa", Some("Sueldo"), None, 1, "Activo"),
      Movimiento(-400, "Renta departamento", LocalDate.of(2025,1,2), "Pago mensual", Some("Vivienda"), None, 2, "Activo"),
      Movimiento(-100, "Supermercado", LocalDate.of(2025,1,3), "Compra quincenal", Some("Alimentación"), None, 3, "Activo"),
      Movimiento(-30, "Transporte público", LocalDate.of(2025,1,4), "Tarjeta recargada", Some("Transporte"), None, 4, "Activo"),
      Movimiento(-20, "Cine", LocalDate.of(2025,1,5), "Película estreno", Some("Entretenimiento"), None, 5, "Activo"),
      // Febrero
      Movimiento(1200, "Sueldo mensual", LocalDate.of(2025,2,1), "Pago empresa", Some("Sueldo"), None, 6, "Activo"),
      Movimiento(-400, "Renta departamento", LocalDate.of(2025,2,2), "Pago mensual", Some("Vivienda"), None, 7, "Activo"),
      Movimiento(-105, "Supermercado", LocalDate.of(2025,2,3), "Compra quincenal", Some("Alimentación"), None, 8, "Activo"),
      Movimiento(-32, "Transporte público", LocalDate.of(2025,2,4), "Tarjeta recargada", Some("Transporte"), None, 9, "Activo"),
      Movimiento(-22, "Cine", LocalDate.of(2025,2,5), "Película estreno", Some("Entretenimiento"), None, 10, "Activo"),
      // Marzo
      Movimiento(1200, "Sueldo mensual", LocalDate.of(2025,3,1), "Pago empresa", Some("Sueldo"), None, 11, "Activo"),
      Movimiento(-400, "Renta departamento", LocalDate.of(2025,3,2), "Pago mensual", Some("Vivienda"), None, 12, "Activo"),
      Movimiento(-110, "Supermercado", LocalDate.of(2025,3,3), "Compra quincenal", Some("Alimentación"), None, 13, "Activo"),
      Movimiento(-33, "Transporte público", LocalDate.of(2025,3,4), "Tarjeta recargada", Some("Transporte"), None, 14, "Activo"),
      Movimiento(-25, "Cine", LocalDate.of(2025,3,5), "Película estreno", Some("Entretenimiento"), None, 15, "Activo"),
      // Abril
      Movimiento(1200, "Sueldo mensual", LocalDate.of(2025,4,1), "Pago empresa", Some("Sueldo"), None, 16, "Activo"),
      Movimiento(-400, "Renta departamento", LocalDate.of(2025,4,2), "Pago mensual", Some("Vivienda"), None, 17, "Activo"),
      Movimiento(-112, "Supermercado", LocalDate.of(2025,4,3), "Compra quincenal", Some("Alimentación"), None, 18, "Activo"),
      Movimiento(-34, "Transporte público", LocalDate.of(2025,4,4), "Tarjeta recargada", Some("Transporte"), None, 19, "Activo"),
      Movimiento(-28, "Cine", LocalDate.of(2025,4,5), "Película estreno", Some("Entretenimiento"), None, 20, "Activo"),
      Movimiento(200, "Venta freelance", LocalDate.of(2025,4,10), "Proyecto web", Some("Freelance"), None, 21, "Activo"),
      // Mayo
      Movimiento(1200, "Sueldo mensual", LocalDate.of(2025,5,1), "Pago empresa", Some("Sueldo"), None, 22, "Activo"),
      Movimiento(-400, "Renta departamento", LocalDate.of(2025,5,2), "Pago mensual", Some("Vivienda"), None, 23, "Activo"),
      Movimiento(-110, "Supermercado", LocalDate.of(2025,5,3), "Compra quincenal", Some("Alimentación"), None, 24, "Activo"),
      Movimiento(-35, "Transporte público", LocalDate.of(2025,5,4), "Tarjeta recargada", Some("Transporte"), None, 25, "Activo"),
      Movimiento(-20, "Cine", LocalDate.of(2025,5,5), "Película estreno", Some("Entretenimiento"), None, 26, "Activo"),
      Movimiento(300, "Venta freelance", LocalDate.of(2025,5,10), "Diseño logo", Some("Freelance"), None, 27, "Activo"),
      // Junio
      Movimiento(1200, "Sueldo mensual", LocalDate.of(2025,6,1), "Pago empresa", Some("Sueldo"), None, 28, "Activo"),
      Movimiento(-400, "Renta departamento", LocalDate.of(2025,6,2), "Pago mensual", Some("Vivienda"), None, 29, "Activo"),
      Movimiento(-115, "Supermercado", LocalDate.of(2025,6,3), "Compra quincenal", Some("Alimentación"), None, 30, "Activo"),
      Movimiento(-38, "Transporte público", LocalDate.of(2025,6,4), "Tarjeta recargada", Some("Transporte"), None, 31, "Activo"),
      Movimiento(-22, "Cine", LocalDate.of(2025,6,5), "Película estreno", Some("Entretenimiento"), None, 32, "Activo"),
      Movimiento(180, "Venta de muebles", LocalDate.of(2025,6,7), "Silla y mesa", Some("Otros"), None, 33, "Activo"),
      // Julio
      Movimiento(1200, "Sueldo mensual", LocalDate.of(2025,7,1), "Pago empresa", Some("Sueldo"), None, 34, "Activo"),
      Movimiento(-400, "Renta departamento", LocalDate.of(2025,7,2), "Pago mensual", Some("Vivienda"), None, 35, "Activo"),
      Movimiento(-120, "Supermercado", LocalDate.of(2025,7,3), "Compra quincenal", Some("Alimentación"), None, 36, "Activo"),
      Movimiento(-40, "Transporte público", LocalDate.of(2025,7,4), "Tarjeta recargada", Some("Transporte"), None, 37, "Activo"),
      Movimiento(-20, "Cine", LocalDate.of(2025,7,5), "Película estreno", Some("Entretenimiento"), None, 38, "Activo"),
      Movimiento(350, "Venta freelance", LocalDate.of(2025,7,10), "Proyecto web", Some("Freelance"), None, 39, "Activo"),
      Movimiento(-50, "Gasolina", LocalDate.of(2025,7,12), "Llenado de tanque", Some("Transporte"), None, 40, "Activo"),
      Movimiento(-60, "Médico", LocalDate.of(2025,7,14), "Consulta general", Some("Salud"), None, 41, "Activo"),
      Movimiento(-45, "Ropa", LocalDate.of(2025,7,18), "Camisa nueva", Some("Ropa"), None, 42, "Activo")
    )

    // Crear presupuesto anual
    val presupuesto = Presupuesto(
      nombre = "Presupuesto Anual",
      limite = 12000.0,
      inicioPresupuesto = LocalDate.of(2025, 1, 1),
      finPresupuesto = LocalDate.of(2025, 12, 31),
      notificarUsuario = true,
      categorias = categorias.map(cat => cat.nombre -> true).toMap,
      id = 1,
      estaActivo = true,
      Notas = "Presupuesto anual por defecto",
      Movimientos = movimientos.map(_.id).toSet
    )

    // Crear 3 metas
    val metaAbril = Meta(
      nombre = "Pagar seguro auto",
      montoObjetivo = 500.0,
      fechaLimite = LocalDate.of(2025, 4, 20),
      descripcion = "Ahorrar para el seguro del auto",
      fechaInicio = LocalDate.of(2025, 1, 1),
      estaActivo = true,
      id = 1,
      movimientos = Set.empty[Int]
    )
    val metaMayo = Meta(
      nombre = "Comprar laptop",
      montoObjetivo = 900.0,
      fechaLimite = LocalDate.of(2025, 5, 25),
      descripcion = "Ahorrar para laptop nueva",
      fechaInicio = LocalDate.of(2025, 2, 1),
      estaActivo = true,
      id = 2,
      movimientos = Set.empty[Int]
    )
    val metaJulio = Meta(
      nombre = "Vacaciones verano",
      montoObjetivo = 1200.0,
      fechaLimite = LocalDate.of(2025, 7, 31),
      descripcion = "Ahorrar para vacaciones de verano",
      fechaInicio = LocalDate.of(2025, 3, 1),
      estaActivo = true,
      id = 3,
      movimientos = Set.empty[Int]
    )

    // Crear usuario admin
    val usuario = Usuario(
      username = "admin",
      contrasenia = "@admin123",
      nombre = "Administrador",
      id = 1,
      correos = List("admin@demo.com"),
      movimientos = movimientos,
      presupuestos = List(presupuesto),
      metas = List(metaAbril, metaMayo, metaJulio),
      categorias = categorias
    )

    // Guardar datos
    GestorDatos.guardarUsuario(usuario)
    GestorDatos.guardarMovimientos(movimientos)
    GestorDatos.guardarPresupuestos(List(presupuesto))
    GestorDatos.guardarMetas(List(metaAbril, metaMayo, metaJulio))
    GestorDatos.guardarCategorias(categorias)

    println("Seeder ejecutado: usuario admin creado con más datos de ejemplo.")
  }
} 
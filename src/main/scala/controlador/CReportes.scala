package controlador

import modelo.entidades.Usuario
import modelo.servicios.Utilidades
import vista.componentes.paneles.PanelReportes

import java.time.{LocalDate, YearMonth}

class CReportes(private val panelReportes: PanelReportes, private var usuario: Usuario) {

  def generarReporteMensual(mes: Int, anio: Int): Map[String, Any] = {
    val yearMonth = YearMonth.of(anio, mes)
    val inicioMes = yearMonth.atDay(1)
    val finMes = yearMonth.atEndOfMonth()

    val transaccionesMes = usuario.movimientos.filter(mov =>
      mov.fechaTransaccion.isAfter(inicioMes.minusDays(1)) &&
      mov.fechaTransaccion.isBefore(finMes.plusDays(1)) &&
      mov.estado == "Activo"
    )

    val (totalIngresos, totalGastos, balance) = Utilidades.calcularIngresosGastosAhorro(transaccionesMes)

    val gastosPorCategoria = transaccionesMes
      .filter(_.categoria.isDefined)
      .groupBy(_.categoria.get)
      .map { case (categoria, movs) => categoria -> movs.map(_.monto).sum }

    Map(
      "mes" -> yearMonth.getMonth.toString,
      "año" -> anio,
      "totalIngresos" -> totalIngresos,
      "totalGastos" -> totalGastos,
      "balance" -> balance,
      "gastosPorCategoria" -> gastosPorCategoria,
      "totalTransacciones" -> transaccionesMes.size
    )
  }

  def generarReportePresupuestos(): Map[String, Any] = {
    val presupuestosActivos = usuario.presupuestos.filter(_.estaActivo)
    
    val presupuestosConProgreso = presupuestosActivos.map { presupuesto =>
      val gastoActual = Utilidades.calcularGastoPresupuesto(usuario.movimientos, presupuesto)
      val porcentajeUsado = (gastoActual / presupuesto.limite) * 100
      
      Map(
        "nombre" -> presupuesto.nombre,
        "limite" -> presupuesto.limite,
        "gastoActual" -> gastoActual,
        "porcentajeUsado" -> porcentajeUsado,
        "restante" -> (presupuesto.limite - gastoActual),
        "categorias" -> presupuesto.categorias.keys.mkString(", ")
      )
    }

    val presupuestosExcedidos = presupuestosConProgreso.filter(_("porcentajeUsado").asInstanceOf[Double] > 100)
    val presupuestosEnRiesgo = presupuestosConProgreso.filter(p => 
      p("porcentajeUsado").asInstanceOf[Double] > 80 && 
      p("porcentajeUsado").asInstanceOf[Double] <= 100
    )

    Map(
      "totalPresupuestos" -> presupuestosActivos.size,
      "presupuestosConProgreso" -> presupuestosConProgreso,
      "presupuestosExcedidos" -> presupuestosExcedidos,
      "presupuestosEnRiesgo" -> presupuestosEnRiesgo,
      "gastoTotalPresupuestado" -> presupuestosActivos.map(_.limite).sum,
      "gastoTotalReal" -> presupuestosActivos.map(p => Utilidades.calcularGastoPresupuesto(usuario.movimientos, p)).sum
    )
  }

  def generarReporteMetas(): Map[String, Any] = {
    val metasActivas = usuario.metas.filter(_.estaActivo)
    val metasCompletadas = usuario.metas.filter(!_.estaActivo)
    
    val metasConProgreso = usuario.metas.map { meta =>
      val balance = Utilidades.balanceHastaFechaLimite(meta, usuario.movimientos)
      val progreso = if (meta.montoObjetivo > 0) balance / meta.montoObjetivo * 100 else 0
      
      Map(
        "nombre" -> meta.nombre,
        "montoObjetivo" -> meta.montoObjetivo,
        "balanceHastaFecha" -> balance,
        "progreso" -> progreso,
        "fechaLimite" -> meta.fechaLimite,
        "estaCompletada" -> (progreso >= 100),
        "diasRestantes" -> java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), meta.fechaLimite)
      )
    }

    val metasVencidas = metasConProgreso.filter(m => 
      m("diasRestantes").asInstanceOf[Long] < 0 && 
      !m("estaCompletada").asInstanceOf[Boolean]
    )

    Map(
      "totalMetas" -> usuario.metas.size,
      "metasActivas" -> metasActivas.size,
      "metasCompletadas" -> metasCompletadas.size,
      "metasVencidas" -> metasVencidas.size,
      "metasConProgreso" -> metasConProgreso,
      "montoTotalObjetivo" -> usuario.metas.map(_.montoObjetivo).sum,
      "montoTotalAhorrado" -> usuario.metas.map(meta => Utilidades.balanceHastaFechaLimite(meta, usuario.movimientos)).sum
    )
  }

  def obtenerUsuario: Usuario = usuario

  def actualizarUsuario(nuevoUsuario: Usuario): Unit = {
    usuario = nuevoUsuario

    panelReportes.mostrarReporte(usuario.movimientos, usuario.presupuestos, usuario.metas)
  }
} 
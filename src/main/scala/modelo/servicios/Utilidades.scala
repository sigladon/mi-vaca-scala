package modelo.servicios

import javax.swing.JOptionPane
import java.time.LocalDate
import java.time.YearMonth

object Utilidades {
  // Validaciones comunes
  def validarCampoNoVacio(valor: String, mensaje: String): Boolean = {
    if (valor.trim.isEmpty) {
      mostrarError(mensaje)
      false
    } else {
      true
    }
  }

  def validarMontoPositivo(monto: Double, mensaje: String): Boolean = {
    if (monto <= 0) {
      mostrarError(mensaje)
      false
    } else {
      true
    }
  }

  def validarFechaNoPasada(fecha: LocalDate, mensaje: String): Boolean = {
    if (fecha.isBefore(LocalDate.now())) {
      mostrarError(mensaje)
      false
    } else {
      true
    }
  }

  def mostrarError(mensaje: String): Unit = {
    JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE)
  }

  def mostrarExito(mensaje: String): Unit = {
    JOptionPane.showMessageDialog(null, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE)
  }

  // Formateo de montos y porcentajes
  def formatearMonto(monto: Double): String = f"$$$monto%.2f"
  def formatearPorcentaje(porcentaje: Double): String = f"$porcentaje%.1f%%"

  // Cálculo de días entre fechas
  def diasRestantes(hasta: java.time.LocalDate): Long = {
    java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), hasta)
  }

  // Creación de alertas
  def crearAlerta(
    tipo: String,
    titulo: String,
    mensaje: String,
    prioridad: String,
    fecha: java.time.LocalDate,
    sugerencia: String
  ): modelo.entidades.Alerta = {
    modelo.entidades.Alerta(tipo, titulo, mensaje, prioridad, fecha, sugerencia)
  }

  // Devuelve la fecha de hace X días desde hoy
  private def haceXDias(dias: Long): java.time.LocalDate = java.time.LocalDate.now().minusDays(dias)

  // Obtiene un Map de gastos por categoría en los últimos X días
  def gastosPorCategoria(movimientos: Seq[modelo.entidades.Movimiento], dias: Long): Map[String, Double] = {
    val desde = haceXDias(dias)
    movimientos
      .filter(m => m.fechaTransaccion.isAfter(desde) && m.monto < 0)
      .groupBy(_.categoria.getOrElse("Sin categoría"))
      .map { case (cat, movs) => cat -> movs.map(-_.monto).sum }
  }

  // Calcula el gasto total de un presupuesto a partir de una lista de movimientos y un presupuesto
  def calcularGastoPresupuesto(movimientos: Seq[modelo.entidades.Movimiento], presupuesto: modelo.entidades.Presupuesto): Double = {
    val categoriasActivas = presupuesto.categorias.filter(_._2).keys.toSet
    movimientos
      .filter(mov =>
        mov.categoria.isDefined &&
        categoriasActivas.contains(mov.categoria.get) &&
        mov.estado == "Activo" &&
        mov.fechaTransaccion.isAfter(presupuesto.inicioPresupuesto.minusDays(1)) &&
        mov.fechaTransaccion.isBefore(presupuesto.finPresupuesto.plusDays(1))
      )
      .map(_.monto)
      .sum
  }

  // Calcula el total gastado en la categoría de un presupuesto específico
  def calcularGastoCategoriaPresupuesto(movimientos: Seq[modelo.entidades.Movimiento], presupuesto: modelo.entidades.Presupuesto): Double = {
    movimientos.filter(m =>
      m.categoria.contains(presupuesto.nombre) && m.monto < 0
    ).map(_.monto.abs).sum
  }

  // Agrupa movimientos por mes y calcula ingresos, gastos y ahorro
  def resumenMensual(movimientos: Seq[modelo.entidades.Movimiento]): Map[YearMonth, (Double, Double, Double)] = {
    movimientos.groupBy(m => YearMonth.from(m.fechaTransaccion)).map {
      case (mes, movs) =>
        val ingresos = movs.filter(_.monto > 0).map(_.monto).sum
        val gastos = movs.filter(_.monto < 0).map(_.monto).sum.abs
        val ahorro = ingresos - gastos
        mes -> (ingresos, gastos, ahorro)
    }
  }

  // Calcula el crecimiento porcentual entre dos valores
  def crecimientoPorcentual(actual: Double, anterior: Double): Double = {
    if (anterior != 0) ((actual - anterior) / anterior) * 100 else 0.0
  }

  // Suma una colección de tuplas (ingresos, gastos, ahorro)
  def sumarTuplasFinancieras(tuplas: Iterable[(Double, Double, Double)]): (Double, Double, Double) = {
    tuplas.foldLeft((0.0, 0.0, 0.0)) {
      case ((ingAcc, gasAcc, ahoAcc), (ing, gas, aho)) => (ingAcc + ing, gasAcc + gas, ahoAcc + aho)
    }
  }

  // Calcula ingresos, gastos y ahorro de una lista de movimientos
  def calcularIngresosGastosAhorro(movimientos: Seq[modelo.entidades.Movimiento]): (Double, Double, Double) = {
    val ingresos = movimientos.filter(_.monto > 0).map(_.monto).sum
    val gastos = movimientos.filter(_.monto < 0).map(_.monto.abs).sum
    val ahorro = ingresos - gastos
    (ingresos, gastos, ahorro)
  }

  // Calcula promedio, varianza y desviación estándar de una lista de valores
  def estadisticasBasicas(valores: Seq[Double]): (Double, Double, Double) = {
    if (valores.isEmpty) (0.0, 0.0, 0.0)
    else {
      val promedio = valores.sum / valores.size
      val varianza = valores.map(v => math.pow(v - promedio, 2)).sum / valores.size
      val desviacionEstandar = math.sqrt(varianza)
      (promedio, varianza, desviacionEstandar)
    }
  }

  // Calcula la frecuencia de compras a partir de una lista de movimientos
  def calcularFrecuenciaCompras(movimientos: List[modelo.entidades.Movimiento]): Double = {
    val gastos = movimientos.filter(_.monto < 0)
    if (gastos.isEmpty) 0.0
    else {
      val diasUnicos = gastos.map(_.fechaTransaccion.toEpochDay).distinct.size
      val diasTotales = if (gastos.nonEmpty) {
        val fechaInicio = gastos.map(_.fechaTransaccion).min
        val fechaFin = gastos.map(_.fechaTransaccion).max
        fechaFin.toEpochDay - fechaInicio.toEpochDay + 1
      } else 0
      if (diasTotales > 0) diasUnicos.toDouble / diasTotales * 30 else 0.0
    }
  }

  // Calcula la consistencia del ahorro mensual a partir de una lista de movimientos
  def calcularConsistenciaAhorro(movimientos: List[modelo.entidades.Movimiento]): Double = {
    val movimientosPorMes = movimientos.groupBy(m => java.time.YearMonth.from(m.fechaTransaccion))
    val ahorrosMensuales = movimientosPorMes.map { case (_, movs) =>
      val (ingresos, gastos, _) = calcularIngresosGastosAhorro(movs)
      ingresos - gastos
    }.toList
    if (ahorrosMensuales.size < 2) 0.0
    else {
      val ahorrosPositivos = ahorrosMensuales.count(_ > 0)
      ahorrosPositivos.toDouble / ahorrosMensuales.size
    }
  }

  // Calcula la variabilidad de los gastos a partir de una lista de movimientos
  def calcularVariabilidadGastos(movimientos: List[modelo.entidades.Movimiento]): Double = {
    val gastos = movimientos.filter(_.monto < 0)
    if (gastos.size < 2) 0.0
    else {
      val montos = gastos.map(_.monto.abs)
      val (promedio, _, desviacionEstandar) = estadisticasBasicas(montos)
      if (promedio != 0) desviacionEstandar / promedio * 100 else 0.0
    }
  }

  def balanceHastaFechaLimite(meta: modelo.entidades.Meta, movimientos: List[modelo.entidades.Movimiento]): Double = {
    movimientos.filter(_.fechaTransaccion.isBefore(meta.fechaLimite.plusDays(1))).map(_.monto).sum
  }
} 
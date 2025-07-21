package controlador

import modelo.entidades._
import modelo.servicios.Utilidades

import java.time.{LocalDate, YearMonth}
import scala.math._

class CProyecciones {

  def generarProyecciones(usuario: Usuario): AnalisisProyecciones = {
    val movimientos = usuario.movimientos
    val presupuestos = usuario.presupuestos
    val metas = usuario.metas
    
    val proyeccionesMensuales = calcularProyeccionesMensuales(movimientos)
    val proyeccionesCategorias = calcularProyeccionesCategorias(movimientos)
    val proyeccionesMetas = calcularProyeccionesMetas(metas, movimientos)
    val proyeccionesPresupuestos = calcularProyeccionesPresupuestos(presupuestos, movimientos)
    
    val ahorroAnualProyectado = proyeccionesMensuales.map(_.ahorroProyectado).sum
    val probabilidadObjetivos = calcularProbabilidadObjetivos(metas, movimientos)
    val riesgoFinanciero = evaluarRiesgoFinanciero(movimientos)
    val oportunidadesAhorro = identificarOportunidadesAhorro(movimientos, proyeccionesCategorias)
    
    AnalisisProyecciones(
      proyeccionesMensuales,
      proyeccionesCategorias,
      proyeccionesMetas,
      proyeccionesPresupuestos,
      ahorroAnualProyectado,
      probabilidadObjetivos,
      riesgoFinanciero,
      oportunidadesAhorro
    )
  }
  
  private def calcularProyeccionesMensuales(movimientos: List[Movimiento]): List[ProyeccionMensual] = {
    val resumen = Utilidades.resumenMensual(movimientos)
    val meses = resumen.keys.toList.sorted
    if (meses.size < 3) {
      generarProyeccionesBasicas()
    } else {
      val ultimosMeses = meses.takeRight(6)
      val movimientosPorMes = movimientos.groupBy(m => YearMonth.from(m.fechaTransaccion))
      val tendencias = calcularTendencias(ultimosMeses, movimientosPorMes)
      (1 to 6).map { i =>
        val mesProyectado = meses.last.plusMonths(i)
        val ingresosProyectados = proyectarValor(tendencias.ingresos, i)
        val gastosProyectados = proyectarValor(tendencias.gastos, i)
        val ahorroProyectado = ingresosProyectados - gastosProyectados
        val confianza = calcularConfianza(meses.size, i)
        ProyeccionMensual(
          mesProyectado.toString,
          ingresosProyectados,
          gastosProyectados,
          ahorroProyectado,
          confianza
        )
      }.toList
    }
  }
  
  private def calcularProyeccionesCategorias(movimientos: List[Movimiento]): List[ProyeccionCategoria] = {
    val gastos = movimientos.filter(_.monto < 0)
    val gastosPorCategoria = gastos.groupBy(_.categoria.getOrElse("Sin categoría"))
    
    gastosPorCategoria.map { case (categoria, movs) =>
      val gastoPromedio = movs.map(_.monto.abs).sum / movs.size
      val factorCrecimiento = calcularFactorCrecimiento(movs)
      val gastoProyectado = gastoPromedio * factorCrecimiento
      val tendencia = interpretarTendencia(factorCrecimiento)
      val recomendacion = generarRecomendacionCategoria(categoria, factorCrecimiento)
      
      ProyeccionCategoria(categoria, gastoProyectado, tendencia, factorCrecimiento, recomendacion)
    }.toList.sortBy(-_.gastoProyectado)
  }
  
  private def calcularProyeccionesMetas(metas: List[Meta], movimientos: List[Movimiento]): List[ProyeccionMeta] = {
    metas.filter(_.estaActivo).map { meta =>
      val ahorroPromedio = calcularAhorroPromedio(movimientos)
      val montoRestante = meta.montoObjetivo - Utilidades.balanceHastaFechaLimite(meta, movimientos)
      val mesesNecesarios = if (ahorroPromedio > 0) montoRestante / ahorroPromedio else Double.MaxValue
      val fechaEstimada = LocalDate.now().plusMonths(mesesNecesarios.toInt)
      val probabilidadExito = calcularProbabilidadMeta(meta, ahorroPromedio, movimientos)
      val recomendacion = generarRecomendacionMeta(probabilidadExito)
      ProyeccionMeta(
        meta.nombre,
        fechaEstimada,
        probabilidadExito,
        montoRestante,
        recomendacion
      )
    }
  }
  
  private def calcularProyeccionesPresupuestos(presupuestos: List[Presupuesto], movimientos: List[Movimiento]): List[ProyeccionPresupuesto] = {
    presupuestos.filter(_.estaActivo).map { presupuesto =>
      val gastoPromedio = Utilidades.calcularGastoCategoriaPresupuesto(movimientos, presupuesto)
      val gastoProyectado = gastoPromedio * 1.1
      val excesoEstimado = gastoProyectado - presupuesto.limite
      val recomendacion = generarRecomendacionPresupuesto(presupuesto, excesoEstimado)
      
      ProyeccionPresupuesto(
        presupuesto.nombre,
        presupuesto.limite,
        gastoProyectado,
        excesoEstimado,
        recomendacion
      )
    }
  }
  
  private def calcularTendencias(meses: List[YearMonth], movimientosPorMes: Map[YearMonth, List[Movimiento]]): Tendencias = {
    val datos = meses.map { mes =>
      val resumen = Utilidades.resumenMensual(movimientosPorMes(mes))
      val (ingresos, gastos, _) = Utilidades.sumarTuplasFinancieras(resumen.values)
      (ingresos, gastos)
    }
    
    val ingresos = datos.map(_._1)
    val gastos = datos.map(_._2)
    
    val tendenciaIngresos = calcularTendenciaLineal(ingresos)
    val tendenciaGastos = calcularTendenciaLineal(gastos)
    
    Tendencias(tendenciaIngresos, tendenciaGastos)
  }
  
  private def calcularTendenciaLineal(valores: List[Double]): TendenciaLineal = {
    val n = valores.size
    val indices = (0 until n).map(_.toDouble).toList
    
    val sumX = indices.sum
    val sumY = valores.sum
    val sumXY = indices.zip(valores).map { case (x, y) => x * y }.sum
    val sumX2 = indices.map(x => x * x).sum
    
    val pendiente = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)
    val intercepto = (sumY - pendiente * sumX) / n
    
    TendenciaLineal(pendiente, intercepto)
  }
  
  private def proyectarValor(tendencia: TendenciaLineal, mesesFuturos: Int): Double = {
    val x = tendencia.intercepto + tendencia.pendiente * mesesFuturos
    max(0, x)
  }
  
  private def calcularConfianza(datosHistoricos: Int, mesesFuturos: Int): Double = {
    val baseConfianza = min(0.95, datosHistoricos / 12.0)
    val factorTiempo = max(0.5, 1.0 - (mesesFuturos * 0.1))
    baseConfianza * factorTiempo
  }
  
  private def calcularFactorCrecimiento(movimientos: List[Movimiento]): Double = {
    if (movimientos.size < 2) return 1.0
    
    val montos = movimientos.sortBy(_.fechaTransaccion).map(_.monto.abs)


    val crecimiento = if (montos.size >= 3) {
      val primeros = montos.take(montos.size / 2).sum / (montos.size / 2)
      val ultimos = montos.takeRight(montos.size / 2).sum / (montos.size / 2)
      if (primeros > 0) ultimos / primeros else 1.0
    } else 1.0
    

    max(0.5, min(2.0, crecimiento))
  }
  
  private def interpretarTendencia(factorCrecimiento: Double): String = {
    factorCrecimiento match {
      case f if f > 1.2 => "Creciente"
      case f if f < 0.8 => "Decreciente"
      case _ => "Estable"
    }
  }
  
  private def generarRecomendacionCategoria(categoria: String, factorCrecimiento: Double): String = {
    if (factorCrecimiento > 1.3) {
      s"Considera reducir gastos en $categoria. El crecimiento es muy alto."
    } else if (factorCrecimiento < 0.7) {
      s"Excelente control en $categoria. Mantén esta tendencia."
    } else {
      s"$categoria está bajo control. Continúa monitoreando."
    }
  }
  
  private def calcularAhorroPromedio(movimientos: List[Movimiento]): Double = {
    val movimientosPorMes = movimientos.groupBy(m => YearMonth.from(m.fechaTransaccion))
    val ahorrosMensuales = movimientosPorMes.map { case (_, movs) =>
      val (ingresos, gastos, _) = Utilidades.calcularIngresosGastosAhorro(movs)
      ingresos - gastos
    }.toList
    
    if (ahorrosMensuales.nonEmpty) ahorrosMensuales.sum / ahorrosMensuales.size else 0.0
  }
  
  private def calcularProbabilidadMeta(meta: Meta, ahorroPromedio: Double, movimientos: List[Movimiento]): Double = {
    val montoRestante = meta.montoObjetivo - Utilidades.balanceHastaFechaLimite(meta, movimientos)
    val mesesRestantes = java.time.temporal.ChronoUnit.MONTHS.between(LocalDate.now(), meta.fechaLimite)
    if (mesesRestantes <= 0) return 0.0
    if (ahorroPromedio <= 0) return 0.0
    val ahorroNecesario = montoRestante / mesesRestantes
    val ratio = ahorroPromedio / ahorroNecesario
    min(1.0, max(0.0, ratio))
  }
  
  private def generarRecomendacionMeta(probabilidad: Double): String = {
    if (probabilidad >= 0.8) {
      "Excelente progreso. Mantén el ritmo actual."
    } else if (probabilidad >= 0.5) {
      "Progreso moderado. Considera aumentar el ahorro mensual."
    } else {
      "Progreso lento. Revisa tu estrategia de ahorro."
    }
  }
  
  private def generarRecomendacionPresupuesto(presupuesto: Presupuesto, excesoEstimado: Double): String = {
    if (excesoEstimado > 0) {
      s"Riesgo de exceder presupuesto. Considera reducir gastos en ${presupuesto.nombre}."
    } else {
      s"Presupuesto bajo control. Mantén el gasto actual en ${presupuesto.nombre}."
    }
  }
  
  private def calcularProbabilidadObjetivos(metas: List[Meta], movimientos: List[Movimiento]): Double = {
    val metasActivas = metas.filter(_.estaActivo)
    if (metasActivas.isEmpty) return 0.0
    
    val probabilidades = metasActivas.map { meta =>
      val ahorroPromedio = calcularAhorroPromedio(movimientos)
      calcularProbabilidadMeta(meta, ahorroPromedio, movimientos)
    }
    
    probabilidades.sum / probabilidades.size
  }
  
  private def evaluarRiesgoFinanciero(movimientos: List[Movimiento]): String = {
    val ahorroPromedio = calcularAhorroPromedio(movimientos)
    val variabilidadGastos = Utilidades.calcularVariabilidadGastos(movimientos)
    
    if (ahorroPromedio < 0) "Alto - Gastos superan ingresos"
    else if (variabilidadGastos > 50) "Medio - Alta variabilidad en gastos"
    else if (ahorroPromedio < 100) "Bajo - Ahorro limitado"
    else "Muy Bajo - Situación financiera estable"
  }
  
  private def identificarOportunidadesAhorro(movimientos: List[Movimiento], proyeccionesCategorias: List[ProyeccionCategoria]): List[String] = {
    var oportunidades = List.empty[String]
    

    val categoriasCrecimiento = proyeccionesCategorias.filter(_.factorCrecimiento > 1.2)
    if (categoriasCrecimiento.nonEmpty) {
      val categoria = categoriasCrecimiento.head.categoria
      oportunidades = oportunidades :+ s"Reducir gastos en $categoria (crecimiento del ${((categoriasCrecimiento.head.factorCrecimiento - 1) * 100).toInt}%)"
    }
    

    val frecuenciaCompras = Utilidades.calcularFrecuenciaCompras(movimientos)
    if (frecuenciaCompras > 15) {
      oportunidades = oportunidades :+ "Consolidar compras pequeñas para reducir costos de transacción"
    }
    

    val consistenciaAhorro = Utilidades.calcularConsistenciaAhorro(movimientos)
    if (consistenciaAhorro < 0.5) {
      oportunidades = oportunidades :+ "Establecer ahorro automático mensual para mayor consistencia"
    }
    
    oportunidades
  }
  
  private def generarProyeccionesBasicas(): List[ProyeccionMensual] = {
    (1 to 6).map { i =>
      val mesProyectado = YearMonth.now().plusMonths(i)
      ProyeccionMensual(
        mesProyectado.toString,
        1000.0,
        800.0,
        200.0,
        0.5
      )
    }.toList
  }
  

  private case class Tendencias(ingresos: TendenciaLineal, gastos: TendenciaLineal)
  private case class TendenciaLineal(pendiente: Double, intercepto: Double)
} 
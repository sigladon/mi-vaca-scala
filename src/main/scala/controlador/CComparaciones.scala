package controlador

import modelo.entidades._
import modelo.servicios.Utilidades

import java.time.YearMonth
import scala.math._

class CComparaciones {

  def generarComparaciones(usuario: Usuario): AnalisisComparaciones = {
    val movimientos = usuario.movimientos
    val presupuestos = usuario.presupuestos
    val metas = usuario.metas
    
    val comparacionesMensuales = calcularComparacionesMensuales(movimientos)
    val comparacionesCategorias = calcularComparacionesCategorias(movimientos)
    val comparacionesTrimestrales = calcularComparacionesTrimestrales(movimientos)
    val comparacionesAnuales = calcularComparacionesAnuales(movimientos, metas, presupuestos)
    
    val mejorMes = encontrarMejorMes(comparacionesMensuales)
    val peorMes = encontrarPeorMes(comparacionesMensuales)
    val categoriaMasMejorada = encontrarCategoriaMasMejorada(comparacionesCategorias)
    val categoriaMasEmpeorada = encontrarCategoriaMasEmpeorada(comparacionesCategorias)
    val tendenciaGeneral = calcularTendenciaGeneral(comparacionesMensuales)
    val periodosAnalizados = comparacionesMensuales.size
    
    AnalisisComparaciones(
      comparacionesMensuales,
      comparacionesCategorias,
      comparacionesTrimestrales,
      comparacionesAnuales,
      mejorMes,
      peorMes,
      categoriaMasMejorada,
      categoriaMasEmpeorada,
      tendenciaGeneral,
      periodosAnalizados
    )
  }
  
  private def calcularComparacionesMensuales(movimientos: List[Movimiento]): List[ComparacionMensual] = {
    val resumen = Utilidades.resumenMensual(movimientos)
    val meses = resumen.keys.toList.sorted
    if (meses.size < 2) {
      List.empty
    } else {
      meses.zipWithIndex.map { case (mes, index) =>
        val (ingresos, gastos, ahorro) = resumen(mes)
        val (crecimientoIngresos, crecimientoGastos, crecimientoAhorro) = if (index > 0) {
          val (ingresosAnterior, gastosAnterior, ahorroAnterior) = resumen(meses(index - 1))
          (
            Utilidades.crecimientoPorcentual(ingresos, ingresosAnterior),
            Utilidades.crecimientoPorcentual(gastos, gastosAnterior),
            Utilidades.crecimientoPorcentual(ahorro, ahorroAnterior)
          )
        } else (0.0, 0.0, 0.0)
        ComparacionMensual(
          mes.toString,
          ingresos,
          gastos,
          ahorro,
          crecimientoIngresos,
          crecimientoGastos,
          crecimientoAhorro
        )
      }
    }
  }
  
  private def calcularComparacionesCategorias(movimientos: List[Movimiento]): List[ComparacionCategoria] = {
    val gastos = movimientos.filter(_.monto < 0)
    val gastosPorCategoria = gastos.groupBy(_.categoria.getOrElse("Sin categoría"))
    

    val fechaMediana = gastos.map(_.fechaTransaccion).sorted.apply(gastos.size / 2)
    val gastosActuales = gastos.filter(_.fechaTransaccion.isAfter(fechaMediana))
    val gastosAnteriores = gastos.filter(m => m.fechaTransaccion.isBefore(fechaMediana) || m.fechaTransaccion.isEqual(fechaMediana))
    
    val gastosActualesPorCategoria = gastosActuales.groupBy(_.categoria.getOrElse("Sin categoría"))
    val gastosAnterioresPorCategoria = gastosAnteriores.groupBy(_.categoria.getOrElse("Sin categoría"))
    
    gastosPorCategoria.keys.map { categoria =>
      val gastoActual = gastosActualesPorCategoria.getOrElse(categoria, List.empty).map(_.monto.abs).sum
      val gastoAnterior = gastosAnterioresPorCategoria.getOrElse(categoria, List.empty).map(_.monto.abs).sum
      val diferencia = gastoActual - gastoAnterior
      val porcentajeCambio = if (gastoAnterior > 0) (diferencia / gastoAnterior) * 100 else 0.0
      val tendencia = interpretarTendencia(porcentajeCambio)
      
      ComparacionCategoria(categoria, gastoActual, gastoAnterior, diferencia, porcentajeCambio, tendencia)
    }.toList.sortBy(-_.porcentajeCambio)
  }
  
  private def calcularComparacionesTrimestrales(movimientos: List[Movimiento]): List[ComparacionTrimestral] = {
    val resumen = Utilidades.resumenMensual(movimientos)
    val meses = resumen.keys.toList.sorted
    if (meses.size < 3) {
      List.empty
    } else {
      val trimestres = meses.grouped(3).toList
      trimestres.map { trimestreMeses =>
        val datosTrimestre = trimestreMeses.flatMap(resumen.get)
        val ingresos = datosTrimestre.map(_._1).sum
        val gastos = datosTrimestre.map(_._2).sum
        val ahorro = datosTrimestre.map(_._3).sum
        val eficiencia = if (ingresos > 0) (ahorro / ingresos) * 100 else 0.0
        val estabilidad = calcularEstabilidadTrimestre(trimestreMeses.flatMap(mes => resumen.get(mes).map(_ => movimientos.filter(mov => YearMonth.from(mov.fechaTransaccion) == mes))).flatten)
        val nombreTrimestre = s"T${trimestres.indexOf(trimestreMeses) + 1} ${trimestreMeses.head.getYear}"
        ComparacionTrimestral(nombreTrimestre, ingresos, gastos, ahorro, eficiencia, estabilidad)
      }
    }
  }
  
  private def calcularComparacionesAnuales(movimientos: List[Movimiento], metas: List[Meta], presupuestos: List[Presupuesto]): List[ComparacionAnual] = {
    val movimientosPorAnio = movimientos.groupBy(_.fechaTransaccion.getYear)
    val anios = movimientosPorAnio.keys.toList.sorted
    
    anios.map { anio =>
      val movsAnio = movimientosPorAnio(anio)
      val resumenAnio = Utilidades.resumenMensual(movsAnio)
      val (ingresos, gastos, ahorro) = Utilidades.sumarTuplasFinancieras(resumenAnio.values)
      

      val crecimientoAnual = if (anios.indexOf(anio) > 0) {
        val anioAnterior = anios(anios.indexOf(anio) - 1)
        val movsAnioAnterior = movimientosPorAnio(anioAnterior)
        val ahorroAnterior = movsAnioAnterior.filter(_.monto > 0).map(_.monto).sum -
                           movsAnioAnterior.filter(_.monto < 0).map(_.monto).sum.abs
        Utilidades.crecimientoPorcentual(ahorro, ahorroAnterior)
      } else 0.0
      

      val metasCompletadas = metas.count(m => Utilidades.balanceHastaFechaLimite(m, movimientos) >= m.montoObjetivo)
      

      val presupuestosCumplidos = presupuestos.count(p => {
        val totalGastado = Utilidades.calcularGastoCategoriaPresupuesto(movsAnio, p)
        totalGastado <= p.limite
      })
      
      ComparacionAnual(
        anio.toString,
        ingresos,
        gastos,
        ahorro,
        crecimientoAnual,
        metasCompletadas,
        presupuestosCumplidos
      )
    }
  }
  
  private def encontrarMejorMes(comparaciones: List[ComparacionMensual]): String = {
    if (comparaciones.isEmpty) "Sin datos"
    else {
      val mejorMes = comparaciones.maxBy(_.ahorro)
      mejorMes.mes
    }
  }
  
  private def encontrarPeorMes(comparaciones: List[ComparacionMensual]): String = {
    if (comparaciones.isEmpty) "Sin datos"
    else {
      val peorMes = comparaciones.minBy(_.ahorro)
      peorMes.mes
    }
  }
  
  private def encontrarCategoriaMasMejorada(comparaciones: List[ComparacionCategoria]): String = {
    if (comparaciones.isEmpty) "Sin datos"
    else {
      val categoriaMejorada = comparaciones.maxBy(_.porcentajeCambio)
      if (categoriaMejorada.porcentajeCambio > 0) categoriaMejorada.categoria else "Ninguna"
    }
  }
  
  private def encontrarCategoriaMasEmpeorada(comparaciones: List[ComparacionCategoria]): String = {
    if (comparaciones.isEmpty) "Sin datos"
    else {
      val categoriaEmpeorada = comparaciones.minBy(_.porcentajeCambio)
      if (categoriaEmpeorada.porcentajeCambio < 0) categoriaEmpeorada.categoria else "Ninguna"
    }
  }
  
  private def calcularTendenciaGeneral(comparaciones: List[ComparacionMensual]): String = {
    if (comparaciones.size < 3) return "Insuficientes datos"
    
    val ahorros = comparaciones.map(_.ahorro)
    val crecimientoPromedio = ahorros.zip(ahorros.tail).map { case (actual, siguiente) =>
      Utilidades.crecimientoPorcentual(siguiente, actual)
    }.sum / (ahorros.size - 1)
    
    crecimientoPromedio match {
      case c if c > 5 => "Creciente - Excelente progreso"
      case c if c > 0 => "Creciente - Progreso moderado"
      case c if c > -5 => "Estable - Mantenimiento"
      case _ => "Decreciente - Necesita atención"
    }
  }
  
  private def interpretarTendencia(porcentajeCambio: Double): String = {
    porcentajeCambio match {
      case p if p > 20 => "Creciente"
      case p if p > 5 => "Ligeramente creciente"
      case p if p > -5 => "Estable"
      case p if p > -20 => "Ligeramente decreciente"
      case _ => "Decreciente"
    }
  }
  
  private def calcularEstabilidadTrimestre(movimientos: List[Movimiento]): Double = {
    if (movimientos.size < 2) return 0.0
    
    val gastos = movimientos.filter(_.monto < 0).map(_.monto.abs)
    val (promedio, _, desviacionEstandar) = Utilidades.estadisticasBasicas(gastos)
    
    if (promedio > 0) max(0, 100 - (desviacionEstandar / promedio) * 100) else 0.0
  }
} 
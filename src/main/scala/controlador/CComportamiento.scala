package controlador

import modelo.entidades._
import modelo.servicios.Utilidades

import scala.math._

class CComportamiento {

  def analizarComportamiento(usuario: Usuario): AnalisisComportamiento = {
    val movimientos = usuario.movimientos
    val presupuestos = usuario.presupuestos
    val metas = usuario.metas
    
    val patronesGasto = analizarPatronesGasto(movimientos)
    val habitosFinancieros = identificarHabitosFinancieros(movimientos, presupuestos, metas)
    val tendenciasMensuales = calcularTendenciasMensuales(movimientos)
    
    val categoriaMasGastada = encontrarCategoriaMasGastada(movimientos)
    val categoriaMenosGastada = encontrarCategoriaMenosGastada(movimientos)
    val diaSemanaMasGastado = encontrarDiaSemanaMasGastado(movimientos)
    val mesMasGastado = encontrarMesMasGastado(movimientos)
    val frecuenciaCompras = Utilidades.calcularFrecuenciaCompras(movimientos)
    val variabilidadGastos = Utilidades.calcularVariabilidadGastos(movimientos)
    val consistenciaAhorro = Utilidades.calcularConsistenciaAhorro(movimientos)
    
    AnalisisComportamiento(
      patronesGasto,
      habitosFinancieros,
      tendenciasMensuales,
      categoriaMasGastada,
      categoriaMenosGastada,
      diaSemanaMasGastado,
      mesMasGastado,
      frecuenciaCompras,
      variabilidadGastos,
      consistenciaAhorro
    )
  }
  
  private def analizarPatronesGasto(movimientos: List[Movimiento]): List[PatronGasto] = {
    val gastos = movimientos.filter(_.monto < 0)
    
    if (gastos.isEmpty) {
      List.empty
    } else {
      val gastosPorCategoria = gastos.groupBy(_.categoria.getOrElse("Sin categoría"))
      
      gastosPorCategoria.map { case (categoria, movs) =>
        val frecuencia = movs.size
        val montoPromedio = movs.map(_.monto).sum / movs.size
        val tendencia = calcularTendenciaCategoria(movs)
        val diaSemanaFavorito = encontrarDiaSemanaFavorito(movs)
        val mesMasActivo = encontrarMesMasActivo(movs)
        
        PatronGasto(categoria, frecuencia, montoPromedio, tendencia, diaSemanaFavorito, mesMasActivo)
      }.toList.sortBy(-_.frecuencia)
    }
  }
  
  private def identificarHabitosFinancieros(
    movimientos: List[Movimiento], 
    presupuestos: List[Presupuesto], 
    metas: List[Meta]
  ): List[HabitoFinanciero] = {
    var habitos = List.empty[HabitoFinanciero]
    

    val frecuenciaCompras = Utilidades.calcularFrecuenciaCompras(movimientos)
    if (frecuenciaCompras > 15) {
      habitos = habitos :+ HabitoFinanciero(
        "Frecuencia de Compras",
        "Realizas muchas compras pequeñas frecuentemente",
        "Alta frecuencia",
        "Puede llevar a gastos innecesarios",
        "Considera consolidar compras para ahorrar tiempo y dinero"
      )
    } else if (frecuenciaCompras < 5) {
      habitos = habitos :+ HabitoFinanciero(
        "Frecuencia de Compras",
        "Realizas pocas compras pero de mayor valor",
        "Baja frecuencia",
        "Mejor control de gastos",
        "Mantén este hábito de planificación"
      )
    }
    

    val consistenciaAhorro = Utilidades.calcularConsistenciaAhorro(movimientos)
    if (consistenciaAhorro < 0.5) {
      habitos = habitos :+ HabitoFinanciero(
        "Consistencia de Ahorro",
        "Tu ahorro es irregular mes a mes",
        "Inconsistente",
        "Dificulta alcanzar metas financieras",
        "Establece un monto fijo mensual para ahorrar"
      )
    } else {
      habitos = habitos :+ HabitoFinanciero(
        "Consistencia de Ahorro",
        "Mantienes un ahorro consistente",
        "Consistente",
        "Facilita el logro de metas",
        "Excelente hábito, mantén la consistencia"
      )
    }
    

    val presupuestosCumplidos = presupuestos.count(p => {
      val gastosCategoria = movimientos.filter(m => m.categoria.contains(p.nombre) && m.monto < 0)
      val totalGastado = gastosCategoria.map(_.monto).sum.abs
      totalGastado <= p.limite
    })
    val porcentajeCumplimiento = if (presupuestos.nonEmpty) presupuestosCumplidos.toDouble / presupuestos.size else 0.0
    
    if (porcentajeCumplimiento < 0.6) {
      habitos = habitos :+ HabitoFinanciero(
        "Cumplimiento de Presupuestos",
        "Frecuentemente excedes tus presupuestos",
        "Bajo cumplimiento",
        "Puede llevar a problemas financieros",
        "Revisa y ajusta tus presupuestos regularmente"
      )
    }
    

    val metasCompletadas = metas.count(m => Utilidades.balanceHastaFechaLimite(m, movimientos) >= m.montoObjetivo)
    val porcentajeMetas = if (metas.nonEmpty) metasCompletadas.toDouble / metas.size else 0.0
    
    if (porcentajeMetas < 0.3) {
      habitos = habitos :+ HabitoFinanciero(
        "Progreso en Metas",
        "Tienes dificultades para completar metas financieras",
        "Bajo progreso",
        "Puede afectar motivación financiera",
        "Establece metas más pequeñas y alcanzables"
      )
    }
    
    habitos
  }
  
  private def calcularTendenciasMensuales(movimientos: List[Movimiento]): List[TendenciaMensual] = {
    val resumen = Utilidades.resumenMensual(movimientos)
    val meses = resumen.keys.toList.sorted
    if (meses.size < 2) {
      List.empty
    } else {
      meses.map { mes =>
        val (ingresos, gastos, ahorro) = resumen(mes)
        val mesAnterior = meses.find(_ == mes.minusMonths(1))
        val crecimiento = mesAnterior match {
          case Some(ma) =>
            val (_, _, ahorroAnterior) = resumen(ma)
            Utilidades.crecimientoPorcentual(ahorro, ahorroAnterior)
          case None => 0.0
        }
        TendenciaMensual(mes.toString, ingresos, gastos, ahorro, crecimiento)
      }
    }
  }
  
  private def calcularTendenciaCategoria(movimientos: List[Movimiento]): String = {
    if (movimientos.size < 2) return "Estable"
    
    val montos = movimientos.sortBy(_.fechaTransaccion).map(_.monto)
    val promedio = montos.sum / montos.size
    val variacion = montos.map(m => abs(m - promedio)).sum / montos.size
    
    if (variacion < promedio * 0.2) "Estable"
    else if (montos.last > promedio * 1.3) "Creciente"
    else if (montos.last < promedio * 0.7) "Decreciente"
    else "Variable"
  }
  
  private def encontrarDiaSemanaFavorito(movimientos: List[Movimiento]): String = {
    val diasSemana = List("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val movimientosPorDia = movimientos.groupBy(_.fechaTransaccion.getDayOfWeek.getValue)
    val diaMasFrecuente = movimientosPorDia.maxBy(_._2.size)._1
    diasSemana(diaMasFrecuente - 1)
  }
  
  private def encontrarMesMasActivo(movimientos: List[Movimiento]): String = {
    val meses = List("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                     "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
    val movimientosPorMes = movimientos.groupBy(_.fechaTransaccion.getMonthValue)
    val mesMasFrecuente = movimientosPorMes.maxBy(_._2.size)._1
    meses(mesMasFrecuente - 1)
  }
  
  private def encontrarCategoriaMasGastada(movimientos: List[Movimiento]): String = {
    val gastos = movimientos.filter(_.monto < 0)
    if (gastos.isEmpty) "Sin datos"
    else {
      val gastosPorCategoria = gastos.groupBy(_.categoria.getOrElse("Sin categoría"))
      gastosPorCategoria.maxBy(_._2.map(_.monto).sum)._1
    }
  }
  
  private def encontrarCategoriaMenosGastada(movimientos: List[Movimiento]): String = {
    val gastos = movimientos.filter(_.monto < 0)
    if (gastos.isEmpty) "Sin datos"
    else {
      val gastosPorCategoria = gastos.groupBy(_.categoria.getOrElse("Sin categoría"))
      gastosPorCategoria.minBy(_._2.map(_.monto).sum)._1
    }
  }
  
  private def encontrarDiaSemanaMasGastado(movimientos: List[Movimiento]): String = {
    val diasSemana = List("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val gastos = movimientos.filter(_.monto < 0)
    if (gastos.isEmpty) "Sin datos"
    else {
      val gastosPorDia = gastos.groupBy(_.fechaTransaccion.getDayOfWeek.getValue)
      val diaMasGastado = gastosPorDia.maxBy(_._2.map(_.monto).sum)._1
      diasSemana(diaMasGastado - 1)
    }
  }
  
  private def encontrarMesMasGastado(movimientos: List[Movimiento]): String = {
    val meses = List("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                     "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
    val gastos = movimientos.filter(_.monto < 0)
    if (gastos.isEmpty) "Sin datos"
    else {
      val gastosPorMes = gastos.groupBy(_.fechaTransaccion.getMonthValue)
      val mesMasGastado = gastosPorMes.maxBy(_._2.map(_.monto).sum)._1
      meses(mesMasGastado - 1)
    }
  }
} 
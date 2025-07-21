package controlador

import modelo.entidades._
import modelo.servicios.Utilidades

import java.time.{LocalDate, Period, YearMonth}
import scala.math._

class CInsights {

  def generarInsights(usuario: Usuario): AnalisisInsights = {
    val movimientos = usuario.movimientos
    val presupuestos = usuario.presupuestos
    val metas = usuario.metas
    
    val insightsUrgentes = generarInsightsUrgentes(movimientos, presupuestos, metas)
    val insightsImportantes = generarInsightsImportantes(movimientos)
    val insightsOportunidades = generarInsightsOportunidades(movimientos, presupuestos)
    val insightsMejoras = generarInsightsMejoras(movimientos, metas)
    
    val totalInsights = insightsUrgentes.size + insightsImportantes.size + 
                       insightsOportunidades.size + insightsMejoras.size
    val insightsImplementados = calcularInsightsImplementados(usuario)
    val scoreFinanciero = calcularScoreFinanciero(movimientos, presupuestos, metas)
    val nivelConfianza = calcularNivelConfianza(movimientos.size)
    
    AnalisisInsights(
      insightsUrgentes,
      insightsImportantes,
      insightsOportunidades,
      insightsMejoras,
      totalInsights,
      insightsImplementados,
      scoreFinanciero,
      nivelConfianza
    )
  }
  
  private def generarInsightsUrgentes(movimientos: List[Movimiento], presupuestos: List[Presupuesto], metas: List[Meta]): List[Insight] = {
    var insights = List.empty[Insight]
    

    presupuestos.foreach { presupuesto =>
      val gastosCategoria = Utilidades.calcularGastoCategoriaPresupuesto(movimientos, presupuesto)
      
      if (gastosCategoria > presupuesto.limite) {
        val exceso = gastosCategoria - presupuesto.limite
        val porcentajeExceso = (exceso / presupuesto.limite) * 100
        
        insights = Insight(
          s"Presupuesto excedido: ${presupuesto.nombre}",
          f"Has excedido tu presupuesto de ${presupuesto.nombre} en $$$exceso%.2f ($porcentajeExceso%.1f%%)",
          "Presupuesto",
          "Alta",
          "Crítico",
          "Revisa tus gastos en esta categoría y considera reducir gastos no esenciales",
          "Ahorro inmediato de $" + f"$exceso%.2f"
        ) :: insights
      }
    }
    

    metas.filter(meta => Utilidades.balanceHastaFechaLimite(meta, movimientos) < meta.montoObjetivo).foreach { meta =>
      val diasRestantes = Period.between(LocalDate.now(), meta.fechaLimite).getDays
      val montoFaltante = meta.montoObjetivo - Utilidades.balanceHastaFechaLimite(meta, movimientos)
      val montoDiarioNecesario = montoFaltante / max(diasRestantes, 1)
      
      if (diasRestantes < 30 && montoDiarioNecesario > 50) {
        insights = Insight(
          s"Meta en riesgo: ${meta.nombre}",
          f"Tu meta de ${meta.nombre} necesita $$$montoDiarioNecesario%.2f diarios para completarse",
          "Meta",
          "Alta",
          "Crítico",
          "Aumenta tus ahorros diarios o considera ajustar la fecha límite",
          "Completar meta de $" + f"${meta.montoObjetivo}%.2f"
        ) :: insights
      }
    }
    

    val gastosRecientes = movimientos.filter(m => 
      m.monto < 0 && m.fechaTransaccion.isAfter(LocalDate.now().minusDays(7))
    )
    
    if (gastosRecientes.map(_.monto.abs).sum > 1000) {
      insights = Insight(
        "Gastos elevados esta semana",
        f"Has gastado $$${gastosRecientes.map(_.monto.abs).sum}%.2f en los últimos 7 días",
        "Gastos",
        "Alta",
        "Alto",
        "Revisa tus gastos recientes y identifica áreas de reducción",
        "Potencial ahorro semanal significativo"
      ) :: insights
    }
    
    insights
  }
  
  private def generarInsightsImportantes(movimientos: List[Movimiento]): List[Insight] = {
    var insights = List.empty[Insight]
    

    val gastosPorCategoria = movimientos.filter(_.monto < 0)
      .groupBy(_.categoria.getOrElse("Sin categoría"))
      .map { case (categoria, movs) => (categoria, movs.map(_.monto.abs).sum) }
      .toList.sortBy(-_._2)
    
    if (gastosPorCategoria.nonEmpty) {
      val categoriaMasCara = gastosPorCategoria.head
      val porcentajeTotal = (categoriaMasCara._2 / movimientos.filter(_.monto < 0).map(_.monto.abs).sum) * 100
      
      if (porcentajeTotal > 40) {
        insights = Insight(
          s"Enfoque en: ${categoriaMasCara._1}",
          s"Esta categoría representa el ${"%.1f".format(porcentajeTotal)}% de tus gastos totales",
          "Análisis",
          "Media",
          "Alto",
          "Considera establecer un presupuesto específico para esta categoría",
          "Reducción potencial del 10-20% en gastos totales"
        ) :: insights
      }
    }
    

    val (ingresos, gastos, _) = Utilidades.calcularIngresosGastosAhorro(movimientos)
    val ratio = if (ingresos > 0) gastos / ingresos else 1.0
    
    if (ratio > 0.9) {
      insights = Insight(
        "Ratio gastos/ingresos alto",
        s"Tus gastos representan el ${"%.1f".format(ratio * 100)}% de tus ingresos",
        "Balance",
        "Media",
        "Alto",
        "Busca formas de aumentar ingresos o reducir gastos no esenciales",
        "Mejora en capacidad de ahorro del 10-15%"
      ) :: insights
    }
    

    val ahorrosMensuales = calcularAhorrosMensuales(movimientos)
    if (ahorrosMensuales.size >= 3) {
      val variabilidad = calcularVariabilidad(ahorrosMensuales)
      if (variabilidad > 0.5) {
        insights = Insight(
          "Inconsistencia en ahorros",
          "Tu capacidad de ahorro varía significativamente entre meses",
          "Ahorros",
          "Media",
          "Medio",
          "Establece un monto fijo de ahorro mensual automático",
          "Ahorro más predecible y consistente"
        ) :: insights
      }
    }
    
    insights
  }
  
  private def generarInsightsOportunidades(movimientos: List[Movimiento], presupuestos: List[Presupuesto]): List[Insight] = {
    var insights = List.empty[Insight]
    

    presupuestos.foreach { presupuesto =>
      val gastosCategoria = Utilidades.calcularGastoCategoriaPresupuesto(movimientos, presupuesto)
      
      if (gastosCategoria < presupuesto.limite * 0.7) {
        val ahorroPotencial = presupuesto.limite - gastosCategoria
        insights = Insight(
          s"Optimización: ${presupuesto.nombre}",
          f"Podrías reducir tu presupuesto en $$$ahorroPotencial%.2f y mantener el mismo nivel de gasto",
          "Optimización",
          "Baja",
          "Medio",
          "Considera reducir el límite del presupuesto y reasignar fondos",
          "Ahorro mensual de $" + f"$ahorroPotencial%.2f"
        ) :: insights
      }
    }
    

    val ahorroPromedio = calcularAhorroPromedio(movimientos)
    if (ahorroPromedio > 500) {
      insights = Insight(
        "Oportunidad de inversión",
        f"Tu ahorro promedio mensual de $$$ahorroPromedio%.2f podría generar rendimientos",
        "Inversión",
        "Baja",
        "Alto",
        "Considera opciones de inversión de bajo riesgo para tus ahorros",
        "Rendimiento potencial del 3-5% anual"
      ) :: insights
    }
    

    val gastosRecurrentes = identificarGastosRecurrentes(movimientos)
    if (gastosRecurrentes.size > 3) {
      insights = Insight(
        "Consolidación de gastos",
        s"Tienes ${gastosRecurrentes.size} gastos recurrentes que podrían optimizarse",
        "Optimización",
        "Baja",
        "Medio",
        "Revisa servicios y suscripciones para identificar duplicaciones",
        "Ahorro potencial del 5-15% en gastos mensuales"
      ) :: insights
    }
    
    insights
  }
  
  private def generarInsightsMejoras(movimientos: List[Movimiento], metas: List[Meta]): List[Insight] = {
    var insights = List.empty[Insight]
    

    val movimientosSinCategoria = movimientos.count(_.categoria.isEmpty)
    val porcentajeSinCategoria = if (movimientos.nonEmpty) (movimientosSinCategoria.toDouble / movimientos.size) * 100 else 0.0
    
    if (porcentajeSinCategoria > 20) {
      insights = Insight(
        "Mejorar categorización",
        s"El ${"%.1f".format(porcentajeSinCategoria)}% de tus movimientos no tienen categoría",
        "Organización",
        "Baja",
        "Medio",
        "Asigna categorías a todos tus movimientos para mejor análisis",
        "Análisis más preciso y reportes más útiles"
      ) :: insights
    }
    

    val metasCompletadas = metas.count(meta => Utilidades.balanceHastaFechaLimite(meta, movimientos) >= meta.montoObjetivo)
    val porcentajeCompletadas = if (metas.nonEmpty) (metasCompletadas.toDouble / metas.size) * 100 else 0.0
    
    if (porcentajeCompletadas < 50) {
      insights = Insight(
        "Revisar estrategia de metas",
        s"Solo el ${"%.1f".format(porcentajeCompletadas)}% de tus metas se han completado",
        "Metas",
        "Baja",
        "Medio",
        "Revisa la viabilidad de tus metas y ajusta fechas o montos",
        "Mayor tasa de éxito en el logro de objetivos"
      ) :: insights
    }
    

    val categoriasUtilizadas = movimientos.filter(_.monto < 0).map(_.categoria.getOrElse("Sin categoría")).distinct.size
    if (categoriasUtilizadas < 5) {
      insights = Insight(
        "Diversificar análisis",
        s"Solo utilizas $categoriasUtilizadas categorías para tus gastos",
        "Análisis",
        "Baja",
        "Bajo",
        "Considera crear categorías más específicas para mejor control",
        "Visión más detallada de tus patrones de gasto"
      ) :: insights
    }
    
    insights
  }
  
  private def calcularInsightsImplementados(usuario: Usuario): Int = {


    val movimientos = usuario.movimientos
    val presupuestos = usuario.presupuestos
    
    var implementados = 0
    

    if (presupuestos.nonEmpty) implementados += 1
    

    val movimientosConCategoria = movimientos.count(_.categoria.isDefined)
    if (movimientosConCategoria > movimientos.size * 0.8) implementados += 1
    

    val ahorrosMensuales = calcularAhorrosMensuales(movimientos)
    if (ahorrosMensuales.size >= 2 && ahorrosMensuales.forall(_ > 0)) implementados += 1
    
    implementados
  }
  
  private def calcularScoreFinanciero(movimientos: List[Movimiento], presupuestos: List[Presupuesto], metas: List[Meta]): Double = {
    var score = 0.0
    

    val (ingresos, _, ahorro) = Utilidades.calcularIngresosGastosAhorro(movimientos)
    val ratioAhorro = if (ingresos > 0) ahorro / ingresos else 0.0
    score += ratioAhorro * 40
    

    val presupuestosCumplidos = presupuestos.count { presupuesto =>
      val gastosCategoria = Utilidades.calcularGastoCategoriaPresupuesto(movimientos, presupuesto)
      gastosCategoria <= presupuesto.limite
    }
    val ratioPresupuestos = if (presupuestos.nonEmpty) presupuestosCumplidos.toDouble / presupuestos.size else 1.0
    score += ratioPresupuestos * 30
    

    val metasCompletadas = metas.count(meta => Utilidades.balanceHastaFechaLimite(meta, movimientos) >= meta.montoObjetivo)
    val ratioMetas = if (metas.nonEmpty) metasCompletadas.toDouble / metas.size else 1.0
    score += ratioMetas * 20
    

    val ahorrosMensuales = calcularAhorrosMensuales(movimientos)
    val consistencia = if (ahorrosMensuales.size >= 2) {
      1.0 - min(calcularVariabilidad(ahorrosMensuales), 1.0)
    } else 0.5
    score += consistencia * 10
    
    score
  }
  
  private def calcularNivelConfianza(cantidadMovimientos: Int): String = {
    cantidadMovimientos match {
      case n if n >= 50 => "Alto - Datos suficientes para análisis confiable"
      case n if n >= 20 => "Medio - Análisis basado en datos limitados"
      case n if n >= 10 => "Bajo - Se necesitan más datos para análisis preciso"
      case _ => "Muy bajo - Insuficientes datos para análisis"
    }
  }
  

  private def calcularAhorrosMensuales(movimientos: List[Movimiento]): List[Double] = {
    val movimientosPorMes = movimientos.groupBy(m => YearMonth.from(m.fechaTransaccion))
    movimientosPorMes.values.map { movsMes =>
      val (ingresos, gastos, _) = Utilidades.calcularIngresosGastosAhorro(movsMes)
      ingresos - gastos
    }.toList
  }
  
  private def calcularVariabilidad(valores: List[Double]): Double = {
    if (valores.size < 2) return 0.0
    
    val (promedio, _, desviacionEstandar) = Utilidades.estadisticasBasicas(valores)
    
    if (promedio != 0) desviacionEstandar / promedio.abs else 0.0
  }
  
  private def calcularAhorroPromedio(movimientos: List[Movimiento]): Double = {
    val ahorrosMensuales = calcularAhorrosMensuales(movimientos)
    if (ahorrosMensuales.nonEmpty) ahorrosMensuales.sum / ahorrosMensuales.size else 0.0
  }
  
  private def identificarGastosRecurrentes(movimientos: List[Movimiento]): List[String] = {
    val gastos = movimientos.filter(_.monto < 0)
    val gastosPorDescripcion = gastos.groupBy(_.descripcion)
    
    gastosPorDescripcion.filter { case (_, movs) => movs.size >= 2 }.keys.toList
  }
} 
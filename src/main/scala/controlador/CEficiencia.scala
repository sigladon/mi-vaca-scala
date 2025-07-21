package controlador

import modelo.entidades.{MetricasEficiencia, Usuario}
import modelo.servicios.Utilidades

import java.time.{LocalDate, YearMonth}

class CEficiencia(private var usuario: Usuario) {


  def generarReporteEficiencia(): MetricasEficiencia = {
    val ratioAhorro = calcularRatioAhorro()
    val eficienciaPresupuestaria = calcularEficienciaPresupuestaria()
    val velocidadProgresoMetas = calcularVelocidadProgresoMetas()
    val estabilidadFinanciera = calcularEstabilidadFinanciera()
    val (promedioGastos, desviacionGastos) = calcularEstadisticasGastos()
    val (presupuestosCumplidos, presupuestosExcedidos) = contarPresupuestos()
    val (metasCompletadas, metasEnProgreso) = contarMetas()
    val tiempoPromedioMeta = calcularTiempoPromedioMeta()

    MetricasEficiencia(
      ratioAhorro = ratioAhorro,
      eficienciaPresupuestaria = eficienciaPresupuestaria,
      velocidadProgresoMetas = velocidadProgresoMetas,
      estabilidadFinanciera = estabilidadFinanciera,
      promedioGastosMensual = promedioGastos,
      desviacionEstandarGastos = desviacionGastos,
      presupuestosCumplidos = presupuestosCumplidos,
      presupuestosExcedidos = presupuestosExcedidos,
      metasCompletadas = metasCompletadas,
      metasEnProgreso = metasEnProgreso,
      tiempoPromedioMeta = tiempoPromedioMeta
    )
  }

  private def calcularRatioAhorro(): Double = {
    val ultimos3Meses = LocalDate.now().minusMonths(3)
    val movimientosRecientes = usuario.movimientos.filter(_.fechaTransaccion.isAfter(ultimos3Meses))
    
    val totalIngresos = movimientosRecientes.filter(_.monto > 0).map(_.monto).sum
    val totalGastos = movimientosRecientes.filter(_.monto < 0).map(-_.monto).sum
    
    if (totalIngresos > 0) {
      ((totalIngresos - totalGastos) / totalIngresos) * 100
    } else {
      0.0
    }
  }

  private def calcularEficienciaPresupuestaria(): Double = {
    val presupuestosActivos = usuario.presupuestos.filter(_.estaActivo)
    
    if (presupuestosActivos.isEmpty) {
      100.0
    } else {
      val eficiencias = presupuestosActivos.map { presupuesto =>
        val gastoActual = Utilidades.calcularGastoPresupuesto(usuario.movimientos, presupuesto)
        val porcentajeUsado = (gastoActual / presupuesto.limite) * 100
        
        if (porcentajeUsado <= 100) {
          100.0
        } else {
          math.max(0, 100 - (porcentajeUsado - 100))
        }
      }
      
      eficiencias.sum / eficiencias.size
    }
  }

  private def calcularVelocidadProgresoMetas(): Double = {
    val metasActivas = usuario.metas.filter(_.estaActivo)
    
    if (metasActivas.isEmpty) {
      0.0
    } else {
      val velocidades = metasActivas.map { meta =>
        val progreso = if (meta.montoObjetivo > 0) Utilidades.balanceHastaFechaLimite(meta, usuario.movimientos) / meta.montoObjetivo * 100 else 0
        val diasTranscurridos = java.time.temporal.ChronoUnit.DAYS.between(meta.fechaInicio, LocalDate.now())
        val diasTotales = java.time.temporal.ChronoUnit.DAYS.between(meta.fechaInicio, meta.fechaLimite)
        
        if (diasTotales > 0 && diasTranscurridos > 0) {
          val progresoTiempo = (diasTranscurridos.toDouble / diasTotales) * 100
          if (progresoTiempo > 0) {
            progreso / progresoTiempo
          } else {
            0.0
          }
        } else {
          0.0
        }
      }
      
      velocidades.sum / velocidades.size
    }
  }

  private def calcularEstabilidadFinanciera(): Double = {
    val ultimos6Meses = LocalDate.now().minusMonths(6)
    val movimientosRecientes = usuario.movimientos.filter(_.fechaTransaccion.isAfter(ultimos6Meses))
    

    val gastosPorMes = movimientosRecientes
      .filter(_.monto < 0)
      .groupBy(m => YearMonth.from(m.fechaTransaccion))
      .map { case (yearMonth, movs) => yearMonth -> movs.map(-_.monto).sum }
      .toList
      .sortBy(_._1)
    
    if (gastosPorMes.size < 2) {
      100.0
    } else {
      val gastos = gastosPorMes.map(_._2)

      val (promedio, _, desviacionEstandar) = Utilidades.estadisticasBasicas(gastos)
      val coeficienteVariacion = if (promedio > 0) (desviacionEstandar / promedio) * 100 else 0
      

      math.max(0, 100 - coeficienteVariacion)
    }
  }

  private def calcularEstadisticasGastos(): (Double, Double) = {
    val ultimos6Meses = LocalDate.now().minusMonths(6)
    val movimientosRecientes = usuario.movimientos.filter(_.fechaTransaccion.isAfter(ultimos6Meses))
    
    val gastosPorMes = movimientosRecientes
      .filter(_.monto < 0)
      .groupBy(m => YearMonth.from(m.fechaTransaccion))
      .map { case (_, movs) => movs.map(-_.monto).sum }
      .toList
    
    if (gastosPorMes.isEmpty) {
      (0.0, 0.0)
    } else {
      val (promedio, _, desviacionEstandar) = Utilidades.estadisticasBasicas(gastosPorMes)

      (promedio, desviacionEstandar)
    }
  }

  private def contarPresupuestos(): (Int, Int) = {
    val presupuestosActivos = usuario.presupuestos.filter(_.estaActivo)
    
    val (cumplidos, excedidos) = presupuestosActivos.foldLeft((0, 0)) { case ((cumplidos, excedidos), presupuesto) =>
      val gastoActual = Utilidades.calcularGastoPresupuesto(usuario.movimientos, presupuesto)
      val porcentajeUsado = (gastoActual / presupuesto.limite) * 100
      
      if (porcentajeUsado <= 100) {
        (cumplidos + 1, excedidos)
      } else {
        (cumplidos, excedidos + 1)
      }
    }
    
    (cumplidos, excedidos)
  }

  private def contarMetas(): (Int, Int) = {
    val metasCompletadas = usuario.metas.count(!_.estaActivo)
    val metasEnProgreso = usuario.metas.count(_.estaActivo)
    
    (metasCompletadas, metasEnProgreso)
  }

  private def calcularTiempoPromedioMeta(): Double = {
    val metasCompletadas = usuario.metas.filter(!_.estaActivo)
    
    if (metasCompletadas.isEmpty) {
      0.0
    } else {
      val tiempos = metasCompletadas.map { meta =>
        java.time.temporal.ChronoUnit.DAYS.between(meta.fechaInicio, meta.fechaLimite).toDouble
      }
      
      tiempos.sum / tiempos.size
    }
  }

  def obtenerUsuario: Usuario = usuario

  def actualizarUsuario(nuevoUsuario: Usuario): Unit = {
    usuario = nuevoUsuario
  }
} 
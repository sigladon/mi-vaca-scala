package controlador

import modelo.entidades.{Alerta, Usuario}
import modelo.servicios.Utilidades

import java.time.LocalDate
import scala.collection.mutable.ListBuffer

class CAlertas(private var usuario: Usuario) {

  def generarAlertas(): List[Alerta] = {
    val alertas = new ListBuffer[Alerta]()
    

    alertas ++= generarAlertasPresupuestos()
    

    alertas ++= generarAlertasMetas()
    

    alertas ++= generarAlertasGastosInusuales()
    

    alertas ++= generarAlertasOportunidadesAhorro()
    
    alertas.toList.sortBy(_.fecha)
  }

  private def generarAlertasPresupuestos(): List[Alerta] = {
    val alertas = new ListBuffer[Alerta]()
    val hoy = LocalDate.now()
    
    usuario.presupuestos.filter(_.estaActivo).foreach { presupuesto =>
      val gastoActual = Utilidades.calcularGastoPresupuesto(usuario.movimientos, presupuesto)
      val porcentajeUsado = (gastoActual / presupuesto.limite) * 100
      val diasRestantes = Utilidades.diasRestantes(presupuesto.finPresupuesto)
      
      if (porcentajeUsado > 100) {
        alertas += Utilidades.crearAlerta(
          "presupuesto_excedido",
          s"Presupuesto '${presupuesto.nombre}' excedido",
          s"Has excedido el presupuesto en ${Utilidades.formatearPorcentaje(porcentajeUsado - 100)} (${Utilidades.formatearMonto(gastoActual - presupuesto.limite)})",
          "alta",
          hoy,
          "Revisa tus gastos en las categorías de este presupuesto"
        )
      } else if (porcentajeUsado > 80 && diasRestantes > 7) {
        alertas += Utilidades.crearAlerta(
          "presupuesto_riesgo",
          s"Presupuesto '${presupuesto.nombre}' en riesgo",
          s"Has usado ${Utilidades.formatearPorcentaje(porcentajeUsado)} del presupuesto con $diasRestantes días restantes",
          "media",
          hoy,
          "Reduce gastos en las próximas semanas"
        )
      } else if (diasRestantes <= 7 && diasRestantes > 0) {
        alertas += Utilidades.crearAlerta(
          "presupuesto_vencer",
          s"Presupuesto '${presupuesto.nombre}' por vencer",
          s"El presupuesto vence en $diasRestantes días. Has usado ${Utilidades.formatearPorcentaje(porcentajeUsado)}",
          "baja",
          hoy,
          "Planifica el próximo período presupuestario"
        )
      }
    }
    alertas.toList
  }

  private def generarAlertasMetas(): List[Alerta] = {
    val alertas = new ListBuffer[Alerta]()
    val hoy = LocalDate.now()
    
    usuario.metas.filter(_.estaActivo).foreach { meta =>
      val progreso = if (meta.montoObjetivo > 0) Utilidades.balanceHastaFechaLimite(meta, usuario.movimientos) / meta.montoObjetivo * 100 else 0
      val montoRestante = meta.montoObjetivo - Utilidades.balanceHastaFechaLimite(meta, usuario.movimientos)
      val diasRestantes = Utilidades.diasRestantes(meta.fechaLimite)
      
      if (diasRestantes < 0 && progreso < 100) {
        alertas += Utilidades.crearAlerta(
          "meta_vencida",
          s"Meta '${meta.nombre}' vencida",
          s"La meta venció hace ${-diasRestantes} días. Falta ${Utilidades.formatearMonto(montoRestante)} para completarla",
          "alta",
          hoy,
          "Considera extender la fecha límite o ajustar el objetivo"
        )
      } else if (diasRestantes <= 30 && diasRestantes > 0 && progreso < 70) {
        val montoNecesarioPorDia = if (diasRestantes != 0) montoRestante / diasRestantes else 0
        alertas += Utilidades.crearAlerta(
          "meta_peligro",
          s"Meta '${meta.nombre}' en peligro",
          s"Faltan $diasRestantes días y solo has completado ${Utilidades.formatearPorcentaje(progreso)}. Necesitas ahorrar ${Utilidades.formatearMonto(montoNecesarioPorDia)} por día",
          "media",
          hoy,
          "Aumenta tu ahorro diario para alcanzar la meta"
        )
      } else if (diasRestantes <= 7 && diasRestantes > 0) {
        alertas += Utilidades.crearAlerta(
          "meta_vencer",
          s"Meta '${meta.nombre}' por vencer",
          s"La meta vence en $diasRestantes días. Progreso: ${Utilidades.formatearPorcentaje(progreso)}",
          "baja",
          hoy,
          "Último esfuerzo para completar la meta"
        )
      }
    }
    alertas.toList
  }

  private def generarAlertasGastosInusuales(): List[Alerta] = {
    val alertas = new ListBuffer[Alerta]()
    val hoy = LocalDate.now()
    val gastosPorCategoria = Utilidades.gastosPorCategoria(usuario.movimientos, 30)
    val promedioHistorico = Utilidades.gastosPorCategoria(usuario.movimientos, 90).map { case (cat, total) => cat -> (total / 3) }
    gastosPorCategoria.foreach { case (categoria, gastoActual) =>
      promedioHistorico.get(categoria).foreach { promedio =>
        if (gastoActual > promedio * 1.5) {
          val incrementoStr = Utilidades.formatearPorcentaje((gastoActual / promedio - 1) * 100)
          alertas += Utilidades.crearAlerta(
            "gasto_inusual",
            s"Gasto inusual en '$categoria'",
            s"Has gastado ${Utilidades.formatearMonto(gastoActual)} este mes vs ${Utilidades.formatearMonto(promedio)} promedio. Incremento del $incrementoStr",
            "media",
            hoy,
            "Revisa si este incremento es necesario o puedes reducirlo"
          )
        }
      }
    }
    alertas.toList
  }

  private def generarAlertasOportunidadesAhorro(): List[Alerta] = {
    val alertas = new ListBuffer[Alerta]()
    val hoy = LocalDate.now()
    val gastosPorCategoria = Utilidades.gastosPorCategoria(usuario.movimientos, 30).toList.sortBy(-_._2)
    val totalGastos = gastosPorCategoria.map(_._2).sum
    gastosPorCategoria.take(3).foreach { case (categoria, monto) =>
      val porcentaje = if (totalGastos > 0) monto / totalGastos * 100 else 0.0
      alertas += Utilidades.crearAlerta(
        "oportunidad_ahorro",
        s"Oportunidad de ahorro en '$categoria'",
        s"Esta categoría representa ${Utilidades.formatearMonto(monto)} de tus gastos este mes (${Utilidades.formatearPorcentaje(porcentaje)})",
        "baja",
        hoy,
        "Considera reducir gastos en esta categoría para aumentar tus ahorros"
      )
    }
    alertas.toList
  }

  def obtenerUsuario: Usuario = usuario

  def actualizarUsuario(nuevoUsuario: Usuario): Unit = {
    usuario = nuevoUsuario
  }
} 
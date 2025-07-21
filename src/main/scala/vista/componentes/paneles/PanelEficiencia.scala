package vista.componentes.paneles

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import controlador._
import modelo.entidades.{Meta, MetricasEficiencia, Movimiento, Presupuesto}

import scala.collection.immutable.List
import javax.swing.ImageIcon

class PanelEficiencia extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)
  
  private val scrollPane = new JScrollPane()
  private val panelContenido = new JPanel()
  private var solicitarActualizarEficiencia: Option[() => Unit] = None
  
  initUI()
  
  private def initUI(): Unit = {
    setLayout(new BorderLayout())
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20))
    

    val panelSuperior = new JPanel(new BorderLayout())
    val lblTitulo = new JLabel("Eficiencia Financiera")
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 24))
    panelSuperior.add(lblTitulo, BorderLayout.WEST)
    

    panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS))
    scrollPane.setViewportView(panelContenido)
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
    

    add(panelSuperior, BorderLayout.NORTH)
    add(scrollPane, BorderLayout.CENTER)
    

    mostrarIndicadorCarga()
  }
  
  def setSolicitarActualizarEficiencia(callback: () => Unit): Unit = {
    solicitarActualizarEficiencia = Some(callback)

    cargarEficienciaAutomaticamente()
  }
  
  private def cargarEficienciaAutomaticamente(): Unit = {
    solicitarActualizarEficiencia.foreach(_())
  }
  
  private def mostrarIndicadorCarga(): Unit = {
    panelContenido.removeAll()
    val lblCarga = new JLabel("Calculando eficiencia financiera...")
    lblCarga.setFont(new Font("Arial", Font.PLAIN, 16))
    lblCarga.setAlignmentX(Component.CENTER_ALIGNMENT)
    panelContenido.add(lblCarga)
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  def mostrarMensajeVacio(): Unit = {
    panelContenido.removeAll()
    val lblMensaje = new JLabel("No hay datos suficientes para calcular la eficiencia. Haz clic en 'Actualizar Eficiencia' para analizar.")
    lblMensaje.setFont(new Font("Arial", Font.PLAIN, 16))
    lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT)
    panelContenido.add(lblMensaje)
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  def mostrarEficiencia(metricas: MetricasEficiencia): Unit = {
    panelContenido.removeAll()
    

    agregarSeccionKPIs(metricas)
    

    agregarSeccionMetricasDetalladas(metricas)
    

    agregarSeccionEstabilidad(metricas)
    

    agregarSeccionCumplimiento(metricas)
    
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  private def agregarSeccionKPIs(metricas: MetricasEficiencia): Unit = {
    val panelKPIs = new JPanel(new GridLayout(2, 2, 15, 15))
    panelKPIs.setBorder(BorderFactory.createTitledBorder("Indicadores Clave de Eficiencia"))
    

    val colorAhorro = obtenerColorPorPorcentaje(metricas.ratioAhorro)
    panelKPIs.add(crearTarjetaKPI(
      "Ratio de Ahorro",
      s"${"%.1f".format(metricas.ratioAhorro)}%",
      "Porcentaje de ingresos que ahorras",
      colorAhorro
    ))
    

    val colorPresupuesto = obtenerColorPorPorcentaje(metricas.eficienciaPresupuestaria)
    panelKPIs.add(crearTarjetaKPI(
      "Eficiencia Presupuestaria",
      s"${"%.1f".format(metricas.eficienciaPresupuestaria)}%",
      "Cumplimiento de presupuestos",
      colorPresupuesto
    ))
    

    val colorMetas = obtenerColorPorPorcentaje(metricas.velocidadProgresoMetas * 100)
    panelKPIs.add(crearTarjetaKPI(
      "Velocidad de Metas",
      s"${"%.1f".format(metricas.velocidadProgresoMetas * 100)}%",
      "Progreso vs tiempo esperado",
      colorMetas
    ))
    

    val colorEstabilidad = obtenerColorPorPorcentaje(metricas.estabilidadFinanciera)
    panelKPIs.add(crearTarjetaKPI(
      "Estabilidad Financiera",
      s"${"%.1f".format(metricas.estabilidadFinanciera)}%",
      "Consistencia en gastos",
      colorEstabilidad
    ))
    
    panelContenido.add(panelKPIs)
    panelContenido.add(Box.createVerticalStrut(20))
  }
  
  private def agregarSeccionMetricasDetalladas(metricas: MetricasEficiencia): Unit = {
    val panelMetricas = new JPanel(new BorderLayout())
    panelMetricas.setBorder(BorderFactory.createTitledBorder("Métricas Detalladas"))
    
    val panelGrid = new JPanel(new GridLayout(0, 2, 10, 5))
    

    panelGrid.add(crearMetricaDetallada(
      "Gastos Mensuales Promedio",
      s"$$${"%.2f".format(metricas.promedioGastosMensual)}"
    ))
    

    panelGrid.add(crearMetricaDetallada(
      "Variabilidad de Gastos",
      s"$$${"%.2f".format(metricas.desviacionEstandarGastos)}"
    ))
    

    panelGrid.add(crearMetricaDetallada(
      "Tiempo Promedio de Metas",
      s"${metricas.tiempoPromedioMeta.toInt} días"
    ))
    

    val coeficienteVariacion = if (metricas.promedioGastosMensual > 0) {
      (metricas.desviacionEstandarGastos / metricas.promedioGastosMensual) * 100
    } else 0.0
    panelGrid.add(crearMetricaDetallada(
      "Coeficiente de Variación",
      s"${"%.1f".format(coeficienteVariacion)}%"
    ))
    
    panelMetricas.add(panelGrid, BorderLayout.CENTER)
    panelContenido.add(panelMetricas)
    panelContenido.add(Box.createVerticalStrut(20))
  }
  
  private def agregarSeccionEstabilidad(metricas: MetricasEficiencia): Unit = {
    val panelEstabilidad = new JPanel(new BorderLayout())
    panelEstabilidad.setBorder(BorderFactory.createTitledBorder("Análisis de Estabilidad"))
    
    val panelContenidoEstabilidad = new JPanel(new BorderLayout())
    

    val interpretacion = interpretarEstabilidad(metricas.estabilidadFinanciera)
    val lblInterpretacion = new JLabel(interpretacion)
    lblInterpretacion.setFont(new Font("Arial", Font.PLAIN, 14))
    lblInterpretacion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))
    

    val barraEstabilidad = new JProgressBar(0, 100)
    barraEstabilidad.setValue(metricas.estabilidadFinanciera.toInt)
    barraEstabilidad.setStringPainted(true)
    barraEstabilidad.setString(s"${metricas.estabilidadFinanciera.toInt}%")
    barraEstabilidad.setForeground(obtenerColorPorPorcentaje(metricas.estabilidadFinanciera))
    barraEstabilidad.setPreferredSize(new Dimension(300, 25))
    
    panelContenidoEstabilidad.add(lblInterpretacion, BorderLayout.NORTH)
    panelContenidoEstabilidad.add(barraEstabilidad, BorderLayout.CENTER)
    
    panelEstabilidad.add(panelContenidoEstabilidad, BorderLayout.CENTER)
    panelContenido.add(panelEstabilidad)
    panelContenido.add(Box.createVerticalStrut(20))
  }
  
  private def agregarSeccionCumplimiento(metricas: MetricasEficiencia): Unit = {
    val panelCumplimiento = new JPanel(new GridLayout(1, 2, 15, 0))
    panelCumplimiento.setBorder(BorderFactory.createTitledBorder("Resumen de Cumplimiento"))
    

    val totalPresupuestos = metricas.presupuestosCumplidos + metricas.presupuestosExcedidos
    val porcentajePresupuestos = if (totalPresupuestos > 0) {
      (metricas.presupuestosCumplidos.toDouble / totalPresupuestos) * 100
    } else 0.0
    
    panelCumplimiento.add(crearTarjetaCumplimiento(
      "Presupuestos",
      s"${metricas.presupuestosCumplidos}/$totalPresupuestos",
      s"${"%.1f".format(porcentajePresupuestos)}% cumplidos",
      obtenerColorPorPorcentaje(porcentajePresupuestos)
    ))
    

    val totalMetas = metricas.metasCompletadas + metricas.metasEnProgreso
    val porcentajeMetas = if (totalMetas > 0) {
      (metricas.metasCompletadas.toDouble / totalMetas) * 100
    } else 0.0
    
    panelCumplimiento.add(crearTarjetaCumplimiento(
      "Metas",
      s"${metricas.metasCompletadas}/$totalMetas",
      s"${"%.1f".format(porcentajeMetas)}% completadas",
      obtenerColorPorPorcentaje(porcentajeMetas)
    ))
    
    panelContenido.add(panelCumplimiento)
  }
  
  private def crearTarjetaKPI(titulo: String, valor: String, descripcion: String, color: Color): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(color, 2),
      BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ))
    panel.setBackground(new Color(0xeff1f5))
    
    val lblTitulo = new JLabel(titulo)
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 14))
    lblTitulo.setForeground(color)
    
    val lblValor = new JLabel(valor)
    lblValor.setFont(new Font("Arial", Font.BOLD, 24))
    lblValor.setForeground(color)
    
    val lblDescripcion = new JLabel(descripcion)
    lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 11))
    lblDescripcion.setForeground(Color.GRAY)
    
    panel.add(lblTitulo, BorderLayout.NORTH)
    panel.add(lblValor, BorderLayout.CENTER)
    panel.add(lblDescripcion, BorderLayout.SOUTH)
    
    panel
  }
  
  private def crearMetricaDetallada(titulo: String, valor: String): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5))
    
    val lblTitulo = new JLabel(titulo)
    lblTitulo.setFont(new Font("Arial", Font.PLAIN, 12))
    
    val lblValor = new JLabel(valor)
    lblValor.setFont(new Font("Arial", Font.BOLD, 14))
    lblValor.setForeground(new Color(0x1e66f5))
    
    panel.add(lblTitulo, BorderLayout.NORTH)
    panel.add(lblValor, BorderLayout.SOUTH)
    
    panel
  }
  
  private def crearTarjetaCumplimiento(titulo: String, ratio: String, porcentaje: String, color: Color): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(color, 2),
      BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ))
    panel.setBackground(new Color(0xeff1f5))
    
    val lblTitulo = new JLabel(titulo)
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 14))
    lblTitulo.setForeground(color)
    
    val lblRatio = new JLabel(ratio)
    lblRatio.setFont(new Font("Arial", Font.BOLD, 18))
    lblRatio.setForeground(color)
    
    val lblPorcentaje = new JLabel(porcentaje)
    lblPorcentaje.setFont(new Font("Arial", Font.PLAIN, 12))
    lblPorcentaje.setForeground(Color.GRAY)
    
    panel.add(lblTitulo, BorderLayout.NORTH)
    panel.add(lblRatio, BorderLayout.CENTER)
    panel.add(lblPorcentaje, BorderLayout.SOUTH)
    
    panel
  }
  
  private def obtenerColorPorPorcentaje(porcentaje: Double): Color = {
    porcentaje match {
      case p if p >= 80 => new Color(0x40a02b)
      case p if p >= 60 => new Color(0xdf8e1d)
      case p if p >= 40 => new Color(0xfe640b)
      case _ => new Color(0xd20f39)
    }
  }
  
  private def interpretarEstabilidad(estabilidad: Double): String = {
    estabilidad match {
      case e if e >= 80 => "Excelente estabilidad financiera. Tus gastos son muy consistentes."
      case e if e >= 60 => "Buena estabilidad financiera. Tus gastos son relativamente consistentes."
      case e if e >= 40 => "Estabilidad moderada. Considera revisar patrones de gasto."
      case _ => "Baja estabilidad financiera. Tus gastos son muy variables."
    }
  }
} 
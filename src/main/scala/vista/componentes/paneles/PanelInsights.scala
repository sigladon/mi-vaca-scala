package vista.componentes.paneles

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import controlador._
import modelo.entidades.{AnalisisInsights, Insight, Meta, Movimiento, Presupuesto}

import scala.collection.immutable.List
import javax.swing.ImageIcon

class PanelInsights extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)
  
  private val scrollPane = new JScrollPane()
  private val panelContenido = new JPanel()
  private var solicitarActualizarInsights: Option[() => Unit] = None
  
  initUI()
  
  private def initUI(): Unit = {
    setLayout(new BorderLayout())
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20))
    

    val panelSuperior = new JPanel(new BorderLayout())
    val lblTitulo = new JLabel("Insights Personalizados")
    lblTitulo.setFont(fuenteTitulo)
    panelSuperior.add(lblTitulo, BorderLayout.WEST)
    

    panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS))
    scrollPane.setViewportView(panelContenido)
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
    

    add(panelSuperior, BorderLayout.NORTH)
    add(scrollPane, BorderLayout.CENTER)
    

    mostrarIndicadorCarga()
  }
  
  def setSolicitarActualizarInsights(callback: () => Unit): Unit = {
    solicitarActualizarInsights = Some(callback)

    cargarInsightsAutomaticamente()
  }
  
  private def cargarInsightsAutomaticamente(): Unit = {
    solicitarActualizarInsights.foreach(_())
  }
  
  private def mostrarIndicadorCarga(): Unit = {
    panelContenido.removeAll()
    val lblCarga = new JLabel("Generando insights personalizados...")
    lblCarga.setFont(fuenteSansSerif)
    lblCarga.setAlignmentX(Component.CENTER_ALIGNMENT)
    panelContenido.add(lblCarga)
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  def mostrarInsights(analisis: AnalisisInsights): Unit = {
    panelContenido.removeAll()
    

    agregarResumenEjecutivo(analisis)
    

    agregarSeccionInsights(analisis.insightsUrgentes, "Insights Urgentes", new Color(0xd20f39))
    

    agregarSeccionInsights(analisis.insightsImportantes, "Insights Importantes", new Color(0xfe640b))
    

    agregarSeccionInsights(analisis.insightsOportunidades, "Oportunidades", new Color(0x40a02b))
    

    agregarSeccionInsights(analisis.insightsMejoras, "Mejoras Sugeridas", new Color(0x1e66f5))
    
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  private def agregarResumenEjecutivo(analisis: AnalisisInsights): Unit = {
    val panelResumen = new JPanel(new BorderLayout())
    panelResumen.setBorder(BorderFactory.createTitledBorder("Resumen de Insights"))
    panelResumen.setBackground(colorFondo)
    
    val panelGrid = new JPanel(new GridLayout(2, 2, 15, 15))
    panelGrid.setOpaque(false)
    

    val scoreColor = obtenerColorScore(analisis.scoreFinanciero)
    panelGrid.add(crearTarjetaResumen(
      "📈 Score Financiero",
      "%.1f".format(analisis.scoreFinanciero) + "/100",
      obtenerDescripcionScore(analisis.scoreFinanciero),
      scoreColor
    ))
    

    panelGrid.add(crearTarjetaResumen(
      "💡 Total Insights",
      analisis.totalInsights.toString,
      "Recomendaciones generadas",
      new Color(0x1e66f5)
    ))
    

    panelGrid.add(crearTarjetaResumen(
      "✅ Implementados",
      analisis.insightsImplementados.toString,
      "Insights ya aplicados",
      new Color(0x40a02b)
    ))
    

    panelGrid.add(crearTarjetaResumen(
      "🎯 Confianza",
      analisis.nivelConfianza.split(" - ").head,
      "Nivel de confianza del análisis",
      new Color(0xdf8e1d)
    ))
    
    panelResumen.add(panelGrid, BorderLayout.CENTER)
    panelContenido.add(panelResumen)
    panelContenido.add(Box.createVerticalStrut(20))
    

    if (analisis.totalInsights > 0) {
      val panelProgreso = new JPanel(new BorderLayout())
      panelProgreso.setBorder(BorderFactory.createTitledBorder("Progreso de Implementación"))
      panelProgreso.setBackground(colorFondo)
      
      val porcentajeImplementado = (analisis.insightsImplementados.toDouble / analisis.totalInsights) * 100
      val progressBar = new JProgressBar(0, 100)
      progressBar.setValue(porcentajeImplementado.toInt)
      progressBar.setStringPainted(true)
      progressBar.setString("%.1f".format(porcentajeImplementado) + "% implementado")
      progressBar.setForeground(obtenerColorPorcentaje(porcentajeImplementado))
      
      val lblProgreso = new JLabel(s"${analisis.insightsImplementados} de ${analisis.totalInsights} insights implementados")
      lblProgreso.setFont(fuenteSansSerif)
      lblProgreso.setForeground(Color.GRAY)
      lblProgreso.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0))
      
      val panelProgresoInfo = new JPanel(new BorderLayout())
      panelProgresoInfo.setOpaque(false)
      panelProgresoInfo.add(progressBar, BorderLayout.CENTER)
      panelProgresoInfo.add(lblProgreso, BorderLayout.SOUTH)
      
      panelProgreso.add(panelProgresoInfo, BorderLayout.CENTER)
      panelContenido.add(panelProgreso)
      panelContenido.add(Box.createVerticalStrut(20))
    }
  }
  
  private def agregarSeccionInsights(insights: List[Insight], titulo: String, color: Color): Unit = {
    if (insights.nonEmpty) {
      val panelSeccion = new JPanel(new BorderLayout())
      panelSeccion.setBorder(BorderFactory.createTitledBorder(titulo))
      
      val panelLista = new JPanel()
      panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS))
      
      insights.foreach { insight =>
        val panelInsight = crearPanelInsight(insight, color)
        panelLista.add(panelInsight)
        panelLista.add(Box.createVerticalStrut(10))
      }
      
      panelSeccion.add(panelLista, BorderLayout.CENTER)
      panelContenido.add(panelSeccion)
      panelContenido.add(Box.createVerticalStrut(20))
    }
  }
  
  private def crearPanelInsight(insight: Insight, colorSeccion: Color): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(colorSeccion, 2),
      BorderFactory.createEmptyBorder(12, 12, 12, 12)
    ))
    panel.setBackground(Color.WHITE)
    

    val panelIzquierdo = new JPanel(new BorderLayout())
    panelIzquierdo.setOpaque(false)
    
    val lblTitulo = new JLabel(insight.titulo)
    lblTitulo.setFont(fuenteTitulo)
    lblTitulo.setForeground(colorSeccion)
    
    val lblDescripcion = new JLabel(insight.descripcion)
    lblDescripcion.setFont(fuenteSansSerif)
    lblDescripcion.setForeground(colorTexto)
    lblDescripcion.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0))
    
    val panelInfo = new JPanel(new GridLayout(2, 2, 10, 5))
    panelInfo.setOpaque(false)
    panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0))
    
    panelInfo.add(crearEtiquetaInfo("Categoría:", insight.categoria))
    panelInfo.add(crearEtiquetaInfo("Prioridad:", insight.prioridad))
    panelInfo.add(crearEtiquetaInfo("Impacto:", insight.impacto))
    panelInfo.add(crearEtiquetaInfo("Beneficio:", insight.beneficioEsperado))
    
    panelIzquierdo.add(lblTitulo, BorderLayout.NORTH)
    panelIzquierdo.add(lblDescripcion, BorderLayout.CENTER)
    panelIzquierdo.add(panelInfo, BorderLayout.SOUTH)
    

    val panelDerecho = new JPanel(new BorderLayout())
    panelDerecho.setOpaque(false)
    panelDerecho.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0))
    
    val lblAccion = new JLabel("Acción Recomendada:")
    lblAccion.setFont(fuenteTitulo)
    lblAccion.setForeground(new Color(0x1e66f5))
    
    val lblAccionDesc = new JLabel("<html><div style='width: 200px;'>" + insight.accionRecomendada + "</div></html>")
    lblAccionDesc.setFont(fuenteSansSerif)
    lblAccionDesc.setForeground(colorTexto)
    lblAccionDesc.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0))

    val panelAccion = new JPanel(new BorderLayout())
    panelAccion.setOpaque(false)
    panelAccion.add(lblAccion, BorderLayout.NORTH)
    panelAccion.add(lblAccionDesc, BorderLayout.CENTER)

    
    panelDerecho.add(panelAccion, BorderLayout.CENTER)
    
    panel.add(panelIzquierdo, BorderLayout.CENTER)
    panel.add(panelDerecho, BorderLayout.EAST)
    
    panel
  }
  
  private def crearEtiquetaInfo(etiqueta: String, valor: String): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setOpaque(false)
    
    val lblEtiqueta = new JLabel(etiqueta)
    lblEtiqueta.setFont(fuenteSansSerif)
    lblEtiqueta.setForeground(Color.GRAY)
    
    val lblValor = new JLabel(valor)
    lblValor.setFont(fuenteSansSerif)
    lblValor.setForeground(colorTexto)
    
    panel.add(lblEtiqueta, BorderLayout.NORTH)
    panel.add(lblValor, BorderLayout.CENTER)
    
    panel
  }
  
  private def crearTarjetaResumen(titulo: String, valor: String, descripcion: String, color: Color): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(color, 2),
      BorderFactory.createEmptyBorder(12, 12, 12, 12)
    ))
    panel.setBackground(Color.WHITE)
    
    val lblTitulo = new JLabel(titulo)
    lblTitulo.setFont(fuenteTitulo)
    lblTitulo.setForeground(color)
    
    val lblValor = new JLabel(valor)
    lblValor.setFont(fuenteTitulo)
    lblValor.setForeground(color)
    
    val lblDescripcion = new JLabel(descripcion)
    lblDescripcion.setFont(fuenteSansSerif)
    lblDescripcion.setForeground(Color.GRAY)
    
    panel.add(lblTitulo, BorderLayout.NORTH)
    panel.add(lblValor, BorderLayout.CENTER)
    panel.add(lblDescripcion, BorderLayout.SOUTH)
    
    panel
  }
  

  private def obtenerColorScore(score: Double): Color = {
    score match {
      case s if s >= 80 => new Color(0x40a02b)
      case s if s >= 60 => new Color(0xdf8e1d)
      case s if s >= 40 => new Color(0xfe640b)
      case _ => new Color(0xd20f39)
    }
  }
  
  private def obtenerDescripcionScore(score: Double): String = {
    score match {
      case s if s >= 80 => "Excelente salud financiera"
      case s if s >= 60 => "Buena gestión financiera"
      case s if s >= 40 => "Necesita mejoras"
      case _ => "Requiere atención inmediata"
    }
  }
  
  private def obtenerColorPorcentaje(porcentaje: Double): Color = {
    porcentaje match {
      case p if p >= 80 => new Color(0x40a02b)
      case p if p >= 60 => new Color(0xdf8e1d)
      case p if p >= 40 => new Color(0xfe640b)
      case _ => new Color(0xd20f39)
    }
  }
} 
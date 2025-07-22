package vista.componentes.paneles

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import controlador._
import modelo.entidades.{AnalisisProyecciones, Meta, Movimiento, Presupuesto, ProyeccionCategoria, ProyeccionMensual, ProyeccionMeta, ProyeccionPresupuesto}

import scala.collection.immutable.List
import java.time.format.DateTimeFormatter
import javax.swing.ImageIcon

class PanelProyecciones extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)
  
  private val scrollPane = new JScrollPane()
  private val panelContenido = new JPanel()
  private var solicitarActualizarProyecciones: Option[() => Unit] = None
  
  initUI()
  
  private def initUI(): Unit = {
    setLayout(new BorderLayout())
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20))
    

    val panelSuperior = new JPanel(new BorderLayout())
    val lblTitulo = new JLabel("Proyecciones Financieras")
    lblTitulo.setFont(fuenteTitulo)
    panelSuperior.add(lblTitulo, BorderLayout.WEST)
    

    panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS))
    scrollPane.setViewportView(panelContenido)
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
    

    add(panelSuperior, BorderLayout.NORTH)
    add(scrollPane, BorderLayout.CENTER)
    

    mostrarIndicadorCarga()
  }
  
  def setSolicitarActualizarProyecciones(callback: () => Unit): Unit = {
    solicitarActualizarProyecciones = Some(callback)

    cargarProyeccionesAutomaticamente()
  }
  
  private def cargarProyeccionesAutomaticamente(): Unit = {
    solicitarActualizarProyecciones.foreach(_())
  }
  
  private def mostrarIndicadorCarga(): Unit = {
    panelContenido.removeAll()
    val lblCarga = new JLabel("Calculando proyecciones financieras...")
    lblCarga.setFont(fuenteSansSerif)
    lblCarga.setForeground(colorTexto)
    lblCarga.setAlignmentX(Component.CENTER_ALIGNMENT)
    panelContenido.add(lblCarga)
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  def mostrarProyecciones(analisis: AnalisisProyecciones): Unit = {
    panelContenido.removeAll()
    

    agregarResumenEjecutivo(analisis)
    

    agregarSeccionProyeccionesMensuales(analisis.proyeccionesMensuales)
    

    agregarSeccionProyeccionesCategorias(analisis.proyeccionesCategorias)
    

    agregarSeccionProyeccionesMetas(analisis.proyeccionesMetas)
    

    agregarSeccionProyeccionesPresupuestos(analisis.proyeccionesPresupuestos)
    

    agregarSeccionOportunidadesAhorro(analisis.oportunidadesAhorro)
    
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  private def agregarResumenEjecutivo(analisis: AnalisisProyecciones): Unit = {
    val panelResumen = new JPanel(new BorderLayout())
    panelResumen.setBorder(BorderFactory.createTitledBorder("Resumen Ejecutivo de Proyecciones"))
    panelResumen.setBackground(colorFondo)
    
    val panelGrid = new JPanel(new GridLayout(2, 2, 15, 15))
    panelGrid.setOpaque(false)
    

    val ahorroAnualFormateado = "$" + "%.2f".format(analisis.ahorroAnualProyectado)
    panelGrid.add(crearTarjetaResumen(
      "💰 Ahorro Anual Proyectado",
      ahorroAnualFormateado,
      "Próximos 12 meses",
      obtenerColorAhorro(analisis.ahorroAnualProyectado)
    ))
    

    val probabilidadFormateada = if (analisis.probabilidadObjetivos < 0) "N/A" else "%.0f".format(analisis.probabilidadObjetivos * 100) + "%"
    panelGrid.add(crearTarjetaResumen(
      "🎯 Probabilidad de Objetivos",
      probabilidadFormateada,
      "Cumplimiento de metas",
      obtenerColorProbabilidad(analisis.probabilidadObjetivos)
    ))
    

    panelGrid.add(crearTarjetaResumen(
      "⚠️ Riesgo Financiero",
      analisis.riesgoFinanciero,
      "Evaluación de estabilidad",
      obtenerColorRiesgo(analisis.riesgoFinanciero)
    ))
    

    panelGrid.add(crearTarjetaResumen(
      "📅 Período de Proyección",
      "6 meses",
      "Horizonte temporal",
      new Color(0x1e66f5)
    ))
    
    panelResumen.add(panelGrid, BorderLayout.CENTER)
    panelContenido.add(panelResumen)
    panelContenido.add(Box.createVerticalStrut(20))
  }
  
  private def agregarSeccionProyeccionesMensuales(proyecciones: List[ProyeccionMensual]): Unit = {
    if (proyecciones.nonEmpty) {
      val panelProyecciones = new JPanel(new BorderLayout())
      panelProyecciones.setBorder(BorderFactory.createTitledBorder("Proyecciones Mensuales"))
      
      val panelTabla = new JPanel(new GridLayout(0, 5, 5, 5))
      

      panelTabla.add(crearCeldaTabla("Mes", true))
      panelTabla.add(crearCeldaTabla("Ingresos", true))
      panelTabla.add(crearCeldaTabla("Gastos", true))
      panelTabla.add(crearCeldaTabla("Ahorro", true))
      panelTabla.add(crearCeldaTabla("Confianza", true))
      

      proyecciones.take(6).foreach { proyeccion =>
        panelTabla.add(crearCeldaTabla(proyeccion.mes, false))
        panelTabla.add(crearCeldaTabla("$" + "%.2f".format(proyeccion.ingresosProyectados), false))
        panelTabla.add(crearCeldaTabla("$" + "%.2f".format(proyeccion.gastosProyectados), false))
        
        val colorAhorro = if (proyeccion.ahorroProyectado > 0) new Color(0x40a02b) else new Color(0xd20f39)
        panelTabla.add(crearCeldaTablaConColor("$" + "%.2f".format(proyeccion.ahorroProyectado), colorAhorro))
        
        val confianzaFormateada = "%.0f".format(proyeccion.confianza * 100) + "%"
        val colorConfianza = obtenerColorConfianza(proyeccion.confianza)
        panelTabla.add(crearCeldaTablaConColor(confianzaFormateada, colorConfianza))
      }
      
      panelProyecciones.add(panelTabla, BorderLayout.CENTER)
      panelContenido.add(panelProyecciones)
      panelContenido.add(Box.createVerticalStrut(20))
    }
  }
  
  private def agregarSeccionProyeccionesCategorias(proyecciones: List[ProyeccionCategoria]): Unit = {
    if (proyecciones.nonEmpty) {
      val panelCategorias = new JPanel(new BorderLayout())
      panelCategorias.setBorder(BorderFactory.createTitledBorder("Proyecciones por Categoría"))
      
      val panelLista = new JPanel()
      panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS))
      
      proyecciones.take(5).foreach { proyeccion =>
        val panelCategoria = crearPanelProyeccionCategoria(proyeccion)
        panelLista.add(panelCategoria)
        panelLista.add(Box.createVerticalStrut(10))
      }
      
      panelCategorias.add(panelLista, BorderLayout.CENTER)
      panelContenido.add(panelCategorias)
      panelContenido.add(Box.createVerticalStrut(20))
    }
  }
  
  private def agregarSeccionProyeccionesMetas(proyecciones: List[ProyeccionMeta]): Unit = {
    if (proyecciones.nonEmpty) {
      val panelMetas = new JPanel(new BorderLayout())
      panelMetas.setBorder(BorderFactory.createTitledBorder("Proyecciones de Metas"))
      
      val panelLista = new JPanel()
      panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS))
      
      proyecciones.foreach { proyeccion =>
        val panelMeta = crearPanelProyeccionMeta(proyeccion)
        panelLista.add(panelMeta)
        panelLista.add(Box.createVerticalStrut(15))
      }
      
      panelMetas.add(panelLista, BorderLayout.CENTER)
      panelContenido.add(panelMetas)
      panelContenido.add(Box.createVerticalStrut(20))
    }
  }
  
  private def agregarSeccionProyeccionesPresupuestos(proyecciones: List[ProyeccionPresupuesto]): Unit = {
    if (proyecciones.nonEmpty) {
      val panelPresupuestos = new JPanel(new BorderLayout())
      panelPresupuestos.setBorder(BorderFactory.createTitledBorder("Proyecciones de Presupuestos"))
      
      val panelLista = new JPanel()
      panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS))
      
      proyecciones.foreach { proyeccion =>
        val panelPresupuesto = crearPanelProyeccionPresupuesto(proyeccion)
        panelLista.add(panelPresupuesto)
        panelLista.add(Box.createVerticalStrut(10))
      }
      
      panelPresupuestos.add(panelLista, BorderLayout.CENTER)
      panelContenido.add(panelPresupuestos)
      panelContenido.add(Box.createVerticalStrut(20))
    }
  }
  
  private def agregarSeccionOportunidadesAhorro(oportunidades: List[String]): Unit = {
    if (oportunidades.nonEmpty) {
      val panelOportunidades = new JPanel(new BorderLayout())
      panelOportunidades.setBorder(BorderFactory.createTitledBorder("Oportunidades de Ahorro"))
      
      val panelLista = new JPanel()
      panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS))
      
      oportunidades.foreach { oportunidad =>
        val panelOportunidad = crearPanelOportunidad(oportunidad)
        panelLista.add(panelOportunidad)
        panelLista.add(Box.createVerticalStrut(8))
      }
      
      panelOportunidades.add(panelLista, BorderLayout.CENTER)
      panelContenido.add(panelOportunidades)
    }
  }
  
  private def crearTarjetaResumen(titulo: String, valor: String, descripcion: String, color: Color): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(color, 2),
      BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ))
    panel.setBackground(Color.WHITE)
    
    val lblTitulo = new JLabel(titulo)
    lblTitulo.setFont(fuenteTitulo)
    lblTitulo.setForeground(color)
    
    val lblValor = new JLabel(valor)
    lblValor.setFont(new Font("Arial", Font.BOLD, 18))
    lblValor.setForeground(color)
    
    val lblDescripcion = new JLabel(descripcion)
    lblDescripcion.setFont(fuenteSansSerif)
    lblDescripcion.setForeground(Color.GRAY)
    
    panel.add(lblTitulo, BorderLayout.NORTH)
    panel.add(lblValor, BorderLayout.CENTER)
    panel.add(lblDescripcion, BorderLayout.SOUTH)
    
    panel
  }
  
  private def crearCeldaTabla(texto: String, esEncabezado: Boolean): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5))
    
    val lbl = new JLabel(texto, SwingConstants.CENTER)
    if (esEncabezado) {
      lbl.setFont(fuenteTitulo)
      lbl.setForeground(new Color(0x1e66f5))
      panel.setBackground(colorFondo)
    } else {
      lbl.setFont(fuenteSansSerif)
    }
    
    panel.add(lbl, BorderLayout.CENTER)
    panel
  }
  
  private def crearCeldaTablaConColor(texto: String, color: Color): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5))
    
    val lbl = new JLabel(texto, SwingConstants.CENTER)
    lbl.setFont(fuenteSansSerif)
    lbl.setForeground(color)
    
    panel.add(lbl, BorderLayout.CENTER)
    panel
  }
  
  private def crearPanelProyeccionCategoria(proyeccion: ProyeccionCategoria): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(new Color(0xdf8e1d), 2),
      BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ))
    panel.setBackground(colorFondo)
    

    val panelIzquierdo = new JPanel(new BorderLayout())
    panelIzquierdo.setOpaque(false)
    
    val lblCategoria = new JLabel(proyeccion.categoria)
    lblCategoria.setFont(fuenteTitulo)
    lblCategoria.setForeground(new Color(0xdf8e1d))
    
    val lblTendencia = new JLabel("Tendencia: " + proyeccion.tendencia)
    lblTendencia.setFont(fuenteSansSerif)
    lblTendencia.setForeground(obtenerColorTendencia(proyeccion.tendencia))
    
    panelIzquierdo.add(lblCategoria, BorderLayout.NORTH)
    panelIzquierdo.add(lblTendencia, BorderLayout.SOUTH)
    

    val panelDerecho = new JPanel(new BorderLayout())
    panelDerecho.setOpaque(false)
    
    val gastoFormateado = "$" + "%.2f".format(proyeccion.gastoProyectado)
    val lblGasto = new JLabel("Proyectado: " + gastoFormateado)
    lblGasto.setFont(fuenteTitulo)
    
    val lblRecomendacion = new JLabel(proyeccion.recomendacion)
    lblRecomendacion.setFont(new Font("Arial", Font.ITALIC, 11))
    lblRecomendacion.setForeground(new Color(0x179299))
    
    panelDerecho.add(lblGasto, BorderLayout.NORTH)
    panelDerecho.add(lblRecomendacion, BorderLayout.SOUTH)
    
    panel.add(panelIzquierdo, BorderLayout.WEST)
    panel.add(panelDerecho, BorderLayout.CENTER)
    
    panel
  }
  
  private def crearPanelProyeccionMeta(proyeccion: ProyeccionMeta): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(new Color(0x179299), 2),
      BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ))
    panel.setBackground(Color.WHITE)
    

    val panelSuperior = new JPanel(new BorderLayout())
    panelSuperior.setOpaque(false)
    
    val lblMeta = new JLabel(proyeccion.meta)
    lblMeta.setFont(fuenteTitulo)
    lblMeta.setForeground(new Color(0x179299))
    
    val probabilidadFormateada = "%.0f".format(proyeccion.probabilidadExito * 100) + "%"
    val lblProbabilidad = new JLabel("Probabilidad: " + probabilidadFormateada)
    lblProbabilidad.setFont(fuenteSansSerif)
    lblProbabilidad.setForeground(obtenerColorProbabilidad(proyeccion.probabilidadExito))
    
    panelSuperior.add(lblMeta, BorderLayout.WEST)
    panelSuperior.add(lblProbabilidad, BorderLayout.EAST)
    

    val panelCentral = new JPanel(new BorderLayout())
    panelCentral.setOpaque(false)
    
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val fechaFormateada = proyeccion.fechaEstimada.format(formatter)
    val lblFecha = new JLabel("Fecha estimada: " + fechaFormateada)
    lblFecha.setFont(fuenteSansSerif)
    
    val montoFormateado = "$" + "%.2f".format(proyeccion.montoNecesario)
    val lblMonto = new JLabel("Monto necesario: " + montoFormateado)
    lblMonto.setFont(fuenteTitulo)
    
    panelCentral.add(lblFecha, BorderLayout.NORTH)
    panelCentral.add(lblMonto, BorderLayout.SOUTH)
    

    val lblRecomendacion = new JLabel("💡 " + proyeccion.recomendacion)
    lblRecomendacion.setFont(new Font("Arial", Font.ITALIC, 11))
    lblRecomendacion.setForeground(new Color(0x40a02b))
    
    panel.add(panelSuperior, BorderLayout.NORTH)
    panel.add(panelCentral, BorderLayout.CENTER)
    panel.add(lblRecomendacion, BorderLayout.SOUTH)
    
    panel
  }
  
  private def crearPanelProyeccionPresupuesto(proyeccion: ProyeccionPresupuesto): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(new Color(0x1e66f5), 2),
      BorderFactory.createEmptyBorder(8, 8, 8, 8)
    ))
    panel.setBackground(colorFondo)
    

    val panelIzquierdo = new JPanel(new BorderLayout())
    panelIzquierdo.setOpaque(false)
    
    val lblPresupuesto = new JLabel(proyeccion.presupuesto)
    lblPresupuesto.setFont(fuenteTitulo)
    lblPresupuesto.setForeground(new Color(0x1e66f5))
    
    val limiteFormateado = "$" + "%.2f".format(proyeccion.limiteActual)
    val lblLimite = new JLabel("Límite: " + limiteFormateado)
    lblLimite.setFont(fuenteSansSerif)
    
    panelIzquierdo.add(lblPresupuesto, BorderLayout.NORTH)
    panelIzquierdo.add(lblLimite, BorderLayout.SOUTH)
    

    val panelDerecho = new JPanel(new BorderLayout())
    panelDerecho.setOpaque(false)
    
    val gastoFormateado = "$" + "%.2f".format(proyeccion.gastoProyectado)
    val lblGasto = new JLabel("Proyectado: " + gastoFormateado)
    lblGasto.setFont(fuenteTitulo)
    
    val colorExceso = if (proyeccion.excesoEstimado > 0) new Color(0xd20f39) else new Color(0x40a02b)
    val excesoFormateado = "$" + "%.2f".format(proyeccion.excesoEstimado.abs)
    val lblExceso = new JLabel(if (proyeccion.excesoEstimado > 0) "Exceso: " + excesoFormateado else "Ahorro: " + excesoFormateado)
    lblExceso.setFont(fuenteSansSerif)
    lblExceso.setForeground(colorExceso)
    
    panelDerecho.add(lblGasto, BorderLayout.NORTH)
    panelDerecho.add(lblExceso, BorderLayout.SOUTH)
    
    panel.add(panelIzquierdo, BorderLayout.WEST)
    panel.add(panelDerecho, BorderLayout.CENTER)
    
    panel
  }
  
  private def crearPanelOportunidad(oportunidad: String): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(new Color(0x40a02b), 1),
      BorderFactory.createEmptyBorder(8, 8, 8, 8)
    ))
    panel.setBackground(colorFondo)
    
    val lblOportunidad = new JLabel("💡 " + oportunidad)
    lblOportunidad.setFont(fuenteSansSerif)
    lblOportunidad.setForeground(new Color(0x40a02b))
    
    panel.add(lblOportunidad, BorderLayout.CENTER)
    panel
  }
  

  private def obtenerColorAhorro(ahorro: Double): Color = {
    if (ahorro > 0) new Color(0x40a02b) else new Color(0xd20f39)
  }
  
  private def obtenerColorProbabilidad(probabilidad: Double): Color = {
    if (probabilidad < 0) return Color.GRAY
    probabilidad match {
      case p if p >= 0.8 => new Color(0x40a02b)
      case p if p >= 0.5 => new Color(0xdf8e1d)
      case _ => new Color(0xd20f39)
    }
  }
  
  private def obtenerColorRiesgo(riesgo: String): Color = {
    riesgo match {
      case r if r.contains("Alto") => new Color(0xd20f39)
      case r if r.contains("Medio") => new Color(0xfe640b)
      case r if r.contains("Bajo") => new Color(0xdf8e1d)
      case _ => new Color(0x40a02b)
    }
  }
  
  private def obtenerColorConfianza(confianza: Double): Color = {
    confianza match {
      case c if c >= 0.8 => new Color(0x40a02b)
      case c if c >= 0.6 => new Color(0xdf8e1d)
      case _ => new Color(0xd20f39)
    }
  }
  
  private def obtenerColorTendencia(tendencia: String): Color = {
    tendencia match {
      case "Creciente" => new Color(0xd20f39)
      case "Decreciente" => new Color(0x40a02b)
      case _ => new Color(0x1e66f5)
    }
  }
} 
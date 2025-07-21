package vista.componentes.paneles

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import controlador._
import modelo.entidades.{AnalisisComparaciones, ComparacionAnual, ComparacionCategoria, ComparacionMensual, ComparacionTrimestral, Meta, Movimiento, Presupuesto}

import scala.collection.immutable.List
import javax.swing.ImageIcon

class PanelComparaciones extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)
  
  private val scrollPane = new JScrollPane()
  private val panelContenido = new JPanel()
  private var solicitarActualizarComparaciones: Option[() => Unit] = None
  
  initUI()
  
  private def initUI(): Unit = {
    setLayout(new BorderLayout())
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20))
    

    val panelSuperior = new JPanel(new BorderLayout())
    val lblTitulo = new JLabel("Comparaciones Temporales")
    lblTitulo.setFont(fuenteTitulo)
    panelSuperior.add(lblTitulo, BorderLayout.WEST)
    

    panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS))
    scrollPane.setViewportView(panelContenido)
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
    

    add(panelSuperior, BorderLayout.NORTH)
    add(scrollPane, BorderLayout.CENTER)
    

    mostrarIndicadorCarga()
  }
  
  def setSolicitarActualizarComparaciones(callback: () => Unit): Unit = {
    solicitarActualizarComparaciones = Some(callback)

    cargarComparacionesAutomaticamente()
  }
  
  private def cargarComparacionesAutomaticamente(): Unit = {
    solicitarActualizarComparaciones.foreach(_())
  }
  
  private def mostrarIndicadorCarga(): Unit = {
    panelContenido.removeAll()
    val lblCarga = new JLabel("Analizando comparaciones temporales...")
    lblCarga.setFont(fuenteSansSerif)
    lblCarga.setAlignmentX(Component.CENTER_ALIGNMENT)
    panelContenido.add(lblCarga)
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  def mostrarComparaciones(analisis: AnalisisComparaciones): Unit = {
    panelContenido.removeAll()
    

    agregarResumenEjecutivo(analisis)
    

    agregarSeccionComparacionesMensuales(analisis.comparacionesMensuales)
    

    agregarSeccionComparacionesCategorias(analisis.comparacionesCategorias)
    

    agregarSeccionComparacionesTrimestrales(analisis.comparacionesTrimestrales)
    

    agregarSeccionComparacionesAnuales(analisis.comparacionesAnuales)
    
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  private def agregarResumenEjecutivo(analisis: AnalisisComparaciones): Unit = {
    val panelResumen = new JPanel(new BorderLayout())
    panelResumen.setBorder(BorderFactory.createTitledBorder("Resumen Ejecutivo de Comparaciones"))
    panelResumen.setBackground(colorFondo)
    
    val panelGrid = new JPanel(new GridLayout(2, 2, 15, 15))
    panelGrid.setOpaque(false)
    

    panelGrid.add(crearTarjetaResumen(
      "Mejor Mes",
      analisis.mejorMes,
      "Mayor ahorro",
      new Color(0x40a02b)
    ))
    

    panelGrid.add(crearTarjetaResumen(
      "Peor Mes",
      analisis.peorMes,
      "Menor ahorro",
      new Color(0xd20f39)
    ))
    

    panelGrid.add(crearTarjetaResumen(
      "Categoría Más Mejorada",
      analisis.categoriaMasMejorada,
      "Mayor reducción de gastos",
      new Color(0x40a02b)
    ))
    

    panelGrid.add(crearTarjetaResumen(
      "Categoría Más Empeorada",
      analisis.categoriaMasEmpeorada,
      "Mayor aumento de gastos",
      new Color(0xfe640b)
    ))
    
    panelResumen.add(panelGrid, BorderLayout.CENTER)
    panelContenido.add(panelResumen)
    panelContenido.add(Box.createVerticalStrut(20))
    

    val panelTendencia = new JPanel(new BorderLayout())
    panelTendencia.setBorder(BorderFactory.createTitledBorder("Tendencia General"))
    panelTendencia.setBackground(colorFondo)
    
    val lblTendencia = new JLabel(analisis.tendenciaGeneral)
    lblTendencia.setFont(fuenteTitulo)
    lblTendencia.setForeground(obtenerColorTendencia(analisis.tendenciaGeneral))
    lblTendencia.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))
    
    val lblPeriodos = new JLabel(s"Períodos analizados: ${analisis.periodosAnalizados} meses")
    lblPeriodos.setFont(fuenteSansSerif)
    lblPeriodos.setForeground(Color.GRAY)
    lblPeriodos.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10))
    
    val panelTendenciaInfo = new JPanel(new BorderLayout())
    panelTendenciaInfo.setOpaque(false)
    panelTendenciaInfo.add(lblTendencia, BorderLayout.CENTER)
    panelTendenciaInfo.add(lblPeriodos, BorderLayout.SOUTH)
    
    panelTendencia.add(panelTendenciaInfo, BorderLayout.CENTER)
    panelContenido.add(panelTendencia)
    panelContenido.add(Box.createVerticalStrut(20))
  }
  
  private def agregarSeccionComparacionesMensuales(comparaciones: List[ComparacionMensual]): Unit = {
    if (comparaciones.nonEmpty) {
      val panelComparaciones = new JPanel(new BorderLayout())
      panelComparaciones.setBorder(BorderFactory.createTitledBorder("Comparaciones Mensuales"))
      
      val panelTabla = new JPanel(new GridLayout(0, 7, 3, 3))
      

      panelTabla.add(crearCeldaTabla("Mes", true))
      panelTabla.add(crearCeldaTabla("Ingresos", true))
      panelTabla.add(crearCeldaTabla("Gastos", true))
      panelTabla.add(crearCeldaTabla("Ahorro", true))
      panelTabla.add(crearCeldaTabla("Crec. Ing.", true))
      panelTabla.add(crearCeldaTabla("Crec. Gas.", true))
      panelTabla.add(crearCeldaTabla("Crec. Aho.", true))
      

      comparaciones.take(12).foreach { comparacion =>
        panelTabla.add(crearCeldaTabla(comparacion.mes, false))
        panelTabla.add(crearCeldaTabla("$" + "%.2f".format(comparacion.ingresos), false))
        panelTabla.add(crearCeldaTabla("$" + "%.2f".format(comparacion.gastos), false))
        
        val colorAhorro = if (comparacion.ahorro > 0) new Color(0x40a02b) else new Color(0xd20f39)
        panelTabla.add(crearCeldaTablaConColor("$" + "%.2f".format(comparacion.ahorro), colorAhorro))
        
        val colorCrecimientoIng = obtenerColorCrecimiento(comparacion.crecimientoIngresos)
        panelTabla.add(crearCeldaTablaConColor("%.1f".format(comparacion.crecimientoIngresos) + "%", colorCrecimientoIng))
        
        val colorCrecimientoGas = obtenerColorCrecimiento(-comparacion.crecimientoGastos)
        panelTabla.add(crearCeldaTablaConColor("%.1f".format(comparacion.crecimientoGastos) + "%", colorCrecimientoGas))
        
        val colorCrecimientoAho = obtenerColorCrecimiento(comparacion.crecimientoAhorro)
        panelTabla.add(crearCeldaTablaConColor("%.1f".format(comparacion.crecimientoAhorro) + "%", colorCrecimientoAho))
      }
      
      panelComparaciones.add(panelTabla, BorderLayout.CENTER)
      panelContenido.add(panelComparaciones)
      panelContenido.add(Box.createVerticalStrut(20))
    }
  }
  
  private def agregarSeccionComparacionesCategorias(comparaciones: List[ComparacionCategoria]): Unit = {
    if (comparaciones.nonEmpty) {
      val panelCategorias = new JPanel(new BorderLayout())
      panelCategorias.setBorder(BorderFactory.createTitledBorder("Comparaciones por Categoría"))
      
      val panelLista = new JPanel()
      panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS))
      
      comparaciones.take(8).foreach { comparacion =>
        val panelCategoria = crearPanelComparacionCategoria(comparacion)
        panelLista.add(panelCategoria)
        panelLista.add(Box.createVerticalStrut(8))
      }
      
      panelCategorias.add(panelLista, BorderLayout.CENTER)
      panelContenido.add(panelCategorias)
      panelContenido.add(Box.createVerticalStrut(20))
    }
  }
  
  private def agregarSeccionComparacionesTrimestrales(comparaciones: List[ComparacionTrimestral]): Unit = {
    if (comparaciones.nonEmpty) {
      val panelTrimestres = new JPanel(new BorderLayout())
      panelTrimestres.setBorder(BorderFactory.createTitledBorder("Comparaciones Trimestrales"))
      
      val panelTabla = new JPanel(new GridLayout(0, 6, 5, 5))
      

      panelTabla.add(crearCeldaTabla("Trimestre", true))
      panelTabla.add(crearCeldaTabla("Ingresos", true))
      panelTabla.add(crearCeldaTabla("Gastos", true))
      panelTabla.add(crearCeldaTabla("Ahorro", true))
      panelTabla.add(crearCeldaTabla("Eficiencia", true))
      panelTabla.add(crearCeldaTabla("Estabilidad", true))
      

      comparaciones.foreach { comparacion =>
        panelTabla.add(crearCeldaTabla(comparacion.trimestre, false))
        panelTabla.add(crearCeldaTabla("$" + "%.2f".format(comparacion.ingresos), false))
        panelTabla.add(crearCeldaTabla("$" + "%.2f".format(comparacion.gastos), false))
        
        val colorAhorro = if (comparacion.ahorro > 0) new Color(0x40a02b) else new Color(0xd20f39)
        panelTabla.add(crearCeldaTablaConColor("$" + "%.2f".format(comparacion.ahorro), colorAhorro))
        
        val colorEficiencia = obtenerColorPorcentaje(comparacion.eficiencia)
        panelTabla.add(crearCeldaTablaConColor("%.1f".format(comparacion.eficiencia) + "%", colorEficiencia))
        
        val colorEstabilidad = obtenerColorPorcentaje(comparacion.estabilidad)
        panelTabla.add(crearCeldaTablaConColor("%.1f".format(comparacion.estabilidad) + "%", colorEstabilidad))
      }
      
      panelTrimestres.add(panelTabla, BorderLayout.CENTER)
      panelContenido.add(panelTrimestres)
      panelContenido.add(Box.createVerticalStrut(20))
    }
  }
  
  private def agregarSeccionComparacionesAnuales(comparaciones: List[ComparacionAnual]): Unit = {
    if (comparaciones.nonEmpty) {
      val panelAnuales = new JPanel(new BorderLayout())
      panelAnuales.setBorder(BorderFactory.createTitledBorder("Comparaciones Anuales"))
      
      val panelTabla = new JPanel(new GridLayout(0, 8, 3, 3))
      

      panelTabla.add(crearCeldaTabla("Año", true))
      panelTabla.add(crearCeldaTabla("Ingresos", true))
      panelTabla.add(crearCeldaTabla("Gastos", true))
      panelTabla.add(crearCeldaTabla("Ahorro", true))
      panelTabla.add(crearCeldaTabla("Crecimiento", true))
      panelTabla.add(crearCeldaTabla("Metas", true))
      panelTabla.add(crearCeldaTabla("Presupuestos", true))
      panelTabla.add(crearCeldaTabla("Estado", true))
      

      comparaciones.foreach { comparacion =>
        panelTabla.add(crearCeldaTabla(comparacion.anio, false))
        panelTabla.add(crearCeldaTabla("$" + "%.2f".format(comparacion.ingresos), false))
        panelTabla.add(crearCeldaTabla("$" + "%.2f".format(comparacion.gastos), false))
        
        val colorAhorro = if (comparacion.ahorro > 0) new Color(0x40a02b) else new Color(0xd20f39)
        panelTabla.add(crearCeldaTablaConColor("$" + "%.2f".format(comparacion.ahorro), colorAhorro))
        
        val colorCrecimiento = obtenerColorCrecimiento(comparacion.crecimientoAnual)
        panelTabla.add(crearCeldaTablaConColor("%.1f".format(comparacion.crecimientoAnual) + "%", colorCrecimiento))
        
        panelTabla.add(crearCeldaTabla(comparacion.metasCompletadas.toString, false))
        panelTabla.add(crearCeldaTabla(comparacion.presupuestosCumplidos.toString, false))
        
        val estado = evaluarEstadoAnual(comparacion)
        val colorEstado = obtenerColorEstado(estado)
        panelTabla.add(crearCeldaTablaConColor(estado, colorEstado))
      }
      
      panelAnuales.add(panelTabla, BorderLayout.CENTER)
      panelContenido.add(panelAnuales)
    }
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
  
  private def crearCeldaTabla(texto: String, esEncabezado: Boolean): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4))
    
    val lbl = new JLabel(texto, SwingConstants.CENTER)
    lbl.setFont(fuenteSansSerif)
    if (esEncabezado) {
      lbl.setForeground(new Color(0x1e66f5))
      panel.setBackground(colorFondo)
    } else {
      lbl.setForeground(colorTexto)
    }
    
    panel.add(lbl, BorderLayout.CENTER)
    panel
  }
  
  private def crearCeldaTablaConColor(texto: String, color: Color): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4))
    
    val lbl = new JLabel(texto, SwingConstants.CENTER)
    lbl.setFont(fuenteSansSerif)
    lbl.setForeground(color)
    
    panel.add(lbl, BorderLayout.CENTER)
    panel
  }
  
  private def crearPanelComparacionCategoria(comparacion: ComparacionCategoria): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(new Color(0xdf8e1d), 1),
      BorderFactory.createEmptyBorder(8, 8, 8, 8)
    ))
    panel.setBackground(colorFondo)
    

    val panelIzquierdo = new JPanel(new BorderLayout())
    panelIzquierdo.setOpaque(false)
    
    val lblCategoria = new JLabel(comparacion.categoria)
    lblCategoria.setFont(fuenteTitulo)
    lblCategoria.setForeground(new Color(0xdf8e1d))
    
    val lblTendencia = new JLabel("Tendencia: " + comparacion.tendencia)
    lblTendencia.setFont(fuenteSansSerif)
    lblTendencia.setForeground(obtenerColorTendencia(comparacion.tendencia))
    
    panelIzquierdo.add(lblCategoria, BorderLayout.NORTH)
    panelIzquierdo.add(lblTendencia, BorderLayout.SOUTH)
    

    val panelDerecho = new JPanel(new BorderLayout())
    panelDerecho.setOpaque(false)
    
    val gastoActualFormateado = "$" + "%.2f".format(comparacion.gastoActual)
    val lblGastoActual = new JLabel("Actual: " + gastoActualFormateado)
    lblGastoActual.setFont(fuenteTitulo)
    
    val porcentajeFormateado = "%.1f".format(comparacion.porcentajeCambio) + "%"
    val colorPorcentaje = obtenerColorCrecimiento(comparacion.porcentajeCambio)
    val lblPorcentaje = new JLabel("Cambio: " + porcentajeFormateado)
    lblPorcentaje.setFont(fuenteSansSerif)
    lblPorcentaje.setForeground(colorPorcentaje)
    
    panelDerecho.add(lblGastoActual, BorderLayout.NORTH)
    panelDerecho.add(lblPorcentaje, BorderLayout.SOUTH)
    
    panel.add(panelIzquierdo, BorderLayout.WEST)
    panel.add(panelDerecho, BorderLayout.CENTER)
    
    panel
  }
  

  private def obtenerColorTendencia(tendencia: String): Color = {
    tendencia match {
      case t if t.contains("Creciente") => new Color(0x40a02b)
      case t if t.contains("Decreciente") => new Color(0xd20f39)
      case t if t.contains("Estable") => new Color(0x1e66f5)
      case t if t.contains("Ligeramente creciente") => new Color(0x40a02b)
      case t if t.contains("Ligeramente decreciente") => new Color(0xfe640b)
      case _ => Color.GRAY
    }
  }
  
  private def obtenerColorCrecimiento(crecimiento: Double): Color = {
    crecimiento match {
      case c if c > 10 => new Color(0x40a02b)
      case c if c > 0 => new Color(0xdf8e1d)
      case c if c > -10 => new Color(0xfe640b)
      case _ => new Color(0xd20f39)
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
  
  private def evaluarEstadoAnual(comparacion: ComparacionAnual): String = {
    val ahorroPositivo = comparacion.ahorro > 0
    val crecimientoPositivo = comparacion.crecimientoAnual > 0
    val metasCompletadas = comparacion.metasCompletadas > 0
    
    if (ahorroPositivo && crecimientoPositivo && metasCompletadas) "Excelente"
    else if (ahorroPositivo && (crecimientoPositivo || metasCompletadas)) "Bueno"
    else if (ahorroPositivo) "Regular"
    else "Necesita mejora"
  }
  
  private def obtenerColorEstado(estado: String): Color = {
    estado match {
      case "Excelente" => new Color(0x40a02b)
      case "Bueno" => new Color(0xdf8e1d)
      case "Regular" => new Color(0xfe640b)
      case _ => new Color(0xd20f39)
    }
  }
} 
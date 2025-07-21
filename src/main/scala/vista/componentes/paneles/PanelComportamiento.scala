package vista.componentes.paneles

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import controlador._
import modelo.entidades.{AnalisisComportamiento, HabitoFinanciero, Meta, Movimiento, PatronGasto, Presupuesto, TendenciaMensual}

import scala.collection.immutable.List

class PanelComportamiento extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)
  
  private val scrollPane = new JScrollPane()
  private val panelContenido = new JPanel()
  private var solicitarActualizarComportamiento: Option[() => Unit] = None
  
  initUI()
  
  private def initUI(): Unit = {
    setLayout(new BorderLayout())
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20))
    

    val panelSuperior = new JPanel(new BorderLayout())
    val lblTitulo = new JLabel("Análisis de Comportamiento Financiero")
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 24))
    panelSuperior.add(lblTitulo, BorderLayout.WEST)
    

    panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS))
    scrollPane.setViewportView(panelContenido)
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
    

    add(panelSuperior, BorderLayout.NORTH)
    add(scrollPane, BorderLayout.CENTER)
    

    mostrarIndicadorCarga()
  }
  
  def setSolicitarActualizarComportamiento(callback: () => Unit): Unit = {
    solicitarActualizarComportamiento = Some(callback)

    cargarComportamientoAutomaticamente()
  }
  
  private def cargarComportamientoAutomaticamente(): Unit = {
    solicitarActualizarComportamiento.foreach(_())
  }
  
  private def mostrarIndicadorCarga(): Unit = {
    panelContenido.removeAll()
    val lblCarga = new JLabel("Analizando comportamiento financiero...")
    lblCarga.setFont(new Font("Arial", Font.PLAIN, 16))
    lblCarga.setAlignmentX(Component.CENTER_ALIGNMENT)
    panelContenido.add(lblCarga)
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  def mostrarComportamiento(analisis: AnalisisComportamiento): Unit = {
    panelContenido.removeAll()
    

    agregarResumenEjecutivo(analisis)
    

    agregarSeccionPatronesGasto(analisis.patronesGasto)
    

    agregarSeccionHabitosFinancieros(analisis.habitosFinancieros)
    

    agregarSeccionTendenciasMensuales(analisis.tendenciasMensuales)
    

    agregarSeccionEstadisticasClave(analisis)
    
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  private def agregarResumenEjecutivo(analisis: AnalisisComportamiento): Unit = {
    val panelResumen = new JPanel(new BorderLayout())
    panelResumen.setBorder(BorderFactory.createTitledBorder("Resumen Ejecutivo"))
    panelResumen.setBackground(new Color(0xeff1f5))
    
    val panelGrid = new JPanel(new GridLayout(2, 2, 10, 10))
    panelGrid.setOpaque(false)
    

    panelGrid.add(crearTarjetaResumen(
      "Categoría Más Gastada",
      analisis.categoriaMasGastada,
      "Mayor gasto acumulado"
    ))
    

    panelGrid.add(crearTarjetaResumen(
      "Día Más Gastado",
      analisis.diaSemanaMasGastado,
      "Día con mayor gasto"
    ))
    

    val frecuenciaText = s"${"%.1f".format(analisis.frecuenciaCompras)} compras/mes"
    panelGrid.add(crearTarjetaResumen(
      "Frecuencia de Compras",
      frecuenciaText,
      "Promedio mensual"
    ))
    

    val consistenciaText = s"${"%.0f".format(analisis.consistenciaAhorro * 100)}%"
    panelGrid.add(crearTarjetaResumen(
      "Consistencia de Ahorro",
      consistenciaText,
      "Meses con ahorro positivo"
    ))
    
    panelResumen.add(panelGrid, BorderLayout.CENTER)
    panelContenido.add(panelResumen)
    panelContenido.add(Box.createVerticalStrut(20))
  }
  
  private def agregarSeccionPatronesGasto(patrones: List[PatronGasto]): Unit = {
    if (patrones.nonEmpty) {
      val panelPatrones = new JPanel(new BorderLayout())
      panelPatrones.setBorder(BorderFactory.createTitledBorder("Patrones de Gasto por Categoría"))
      
      val panelLista = new JPanel()
      panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS))
      
      patrones.take(5).foreach { patron =>
        val panelPatron = crearPanelPatron(patron)
        panelLista.add(panelPatron)
        panelLista.add(Box.createVerticalStrut(10))
      }
      
      panelPatrones.add(panelLista, BorderLayout.CENTER)
      panelContenido.add(panelPatrones)
      panelContenido.add(Box.createVerticalStrut(20))
    }
  }
  
  private def agregarSeccionHabitosFinancieros(habitos: List[HabitoFinanciero]): Unit = {
    if (habitos.nonEmpty) {
      val panelHabitos = new JPanel(new BorderLayout())
      panelHabitos.setBorder(BorderFactory.createTitledBorder("Hábitos Financieros Identificados"))
      
      val panelLista = new JPanel()
      panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS))
      
      habitos.foreach { habito =>
        val panelHabito = crearPanelHabito(habito)
        panelLista.add(panelHabito)
        panelLista.add(Box.createVerticalStrut(15))
      }
      
      panelHabitos.add(panelLista, BorderLayout.CENTER)
      panelContenido.add(panelHabitos)
      panelContenido.add(Box.createVerticalStrut(20))
    }
  }
  
  private def agregarSeccionTendenciasMensuales(tendencias: List[TendenciaMensual]): Unit = {
    if (tendencias.nonEmpty) {
      val panelTendencias = new JPanel(new BorderLayout())
      panelTendencias.setBorder(BorderFactory.createTitledBorder("Tendencias Mensuales"))
      
      val panelTabla = new JPanel(new GridLayout(0, 5, 5, 5))
      

      panelTabla.add(crearCeldaTabla("Mes", true))
      panelTabla.add(crearCeldaTabla("Ingresos", true))
      panelTabla.add(crearCeldaTabla("Gastos", true))
      panelTabla.add(crearCeldaTabla("Ahorro", true))
      panelTabla.add(crearCeldaTabla("Crecimiento", true))
      

      tendencias.reverse.take(6).foreach { tendencia =>
        panelTabla.add(crearCeldaTabla(tendencia.mes, false))
        panelTabla.add(crearCeldaTabla("$" + "%.2f".format(tendencia.ingresos), false))
        panelTabla.add(crearCeldaTabla("$" + "%.2f".format(tendencia.gastos), false))
        panelTabla.add(crearCeldaTabla("$" + "%.2f".format(tendencia.ahorro), false))
        
        val colorCrecimiento = if (tendencia.crecimiento > 0) new Color(0x40a02b) else new Color(0xd20f39)
        panelTabla.add(crearCeldaTablaConColor("%.1f".format(tendencia.crecimiento) + "%", colorCrecimiento))
      }
      
      panelTendencias.add(panelTabla, BorderLayout.CENTER)
      panelContenido.add(panelTendencias)
      panelContenido.add(Box.createVerticalStrut(20))
    }
  }
  
  private def agregarSeccionEstadisticasClave(analisis: AnalisisComportamiento): Unit = {
    val panelEstadisticas = new JPanel(new BorderLayout())
    panelEstadisticas.setBorder(BorderFactory.createTitledBorder("Estadísticas Clave"))
    
    val panelGrid = new JPanel(new GridLayout(2, 2, 10, 10))
    panelGrid.setOpaque(false)
    

    val variabilidadColor = obtenerColorVariabilidad(analisis.variabilidadGastos)
    panelGrid.add(crearTarjetaEstadistica(
      "Variabilidad de Gastos",
      "%.1f".format(analisis.variabilidadGastos) + "%",
      "Coeficiente de variación",
      variabilidadColor
    ))
    

    panelGrid.add(crearTarjetaEstadistica(
      "Categoría Menos Gastada",
      analisis.categoriaMenosGastada,
      "Menor gasto acumulado",
      new Color(0x40a02b)
    ))
    

    panelGrid.add(crearTarjetaEstadistica(
      "Mes Más Gastado",
      analisis.mesMasGastado,
      "Mes con mayor gasto",
      new Color(0xfe640b)
    ))
    

    val interpretacion = interpretarVariabilidad(analisis.variabilidadGastos)
    panelGrid.add(crearTarjetaEstadistica(
      "Interpretación",
      interpretacion,
      "Análisis de estabilidad",
      new Color(0x1e66f5)
    ))
    
    panelEstadisticas.add(panelGrid, BorderLayout.CENTER)
    panelContenido.add(panelEstadisticas)
  }
  
  private def crearTarjetaResumen(titulo: String, valor: String, descripcion: String): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(new Color(0x1e66f5), 2),
      BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ))
    panel.setBackground(Color.WHITE)
    
    val lblTitulo = new JLabel(titulo)
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 12))
    lblTitulo.setForeground(new Color(0x1e66f5))
    
    val lblValor = new JLabel(valor)
    lblValor.setFont(new Font("Arial", Font.BOLD, 16))
    lblValor.setForeground(new Color(0x1e66f5))
    
    val lblDescripcion = new JLabel(descripcion)
    lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 10))
    lblDescripcion.setForeground(Color.GRAY)
    
    panel.add(lblTitulo, BorderLayout.NORTH)
    panel.add(lblValor, BorderLayout.CENTER)
    panel.add(lblDescripcion, BorderLayout.SOUTH)
    
    panel
  }
  
  private def crearPanelPatron(patron: PatronGasto): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(new Color(0xdf8e1d), 2),
      BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ))
    panel.setBackground(new Color(0xeff1f5))
    

    val panelIzquierdo = new JPanel(new BorderLayout())
    panelIzquierdo.setOpaque(false)
    
    val lblCategoria = new JLabel(patron.categoria)
    lblCategoria.setFont(new Font("Arial", Font.BOLD, 14))
    lblCategoria.setForeground(new Color(0xdf8e1d))
    
    val lblFrecuencia = new JLabel(s"Frecuencia: ${patron.frecuencia} veces")
    lblFrecuencia.setFont(new Font("Arial", Font.PLAIN, 12))
    
    panelIzquierdo.add(lblCategoria, BorderLayout.NORTH)
    panelIzquierdo.add(lblFrecuencia, BorderLayout.SOUTH)
    

    val panelDerecho = new JPanel(new BorderLayout())
    panelDerecho.setOpaque(false)
    
    val montoFormateado = "%.2f".format(patron.montoPromedio)
    val lblMonto = new JLabel("Promedio: $" + montoFormateado)
    lblMonto.setFont(new Font("Arial", Font.BOLD, 12))
    
    val lblTendencia = new JLabel(s"Tendencia: ${patron.tendencia}")
    lblTendencia.setFont(new Font("Arial", Font.PLAIN, 11))
    lblTendencia.setForeground(obtenerColorTendencia(patron.tendencia))
    
    panelDerecho.add(lblMonto, BorderLayout.NORTH)
    panelDerecho.add(lblTendencia, BorderLayout.SOUTH)
    
    panel.add(panelIzquierdo, BorderLayout.WEST)
    panel.add(panelDerecho, BorderLayout.CENTER)
    
    panel
  }
  
  private def crearPanelHabito(habito: HabitoFinanciero): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(new Color(0x179299), 2),
      BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ))
    panel.setBackground(Color.WHITE)
    

    val panelSuperior = new JPanel(new BorderLayout())
    panelSuperior.setOpaque(false)
    
    val lblTipo = new JLabel(habito.tipo)
    lblTipo.setFont(new Font("Arial", Font.BOLD, 14))
    lblTipo.setForeground(new Color(0x179299))
    
    val lblFrecuencia = new JLabel(habito.frecuencia)
    lblFrecuencia.setFont(new Font("Arial", Font.PLAIN, 12))
    lblFrecuencia.setForeground(Color.GRAY)
    
    panelSuperior.add(lblTipo, BorderLayout.WEST)
    panelSuperior.add(lblFrecuencia, BorderLayout.EAST)
    

    val lblDescripcion = new JLabel(habito.descripcion)
    lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 12))
    

    val panelInferior = new JPanel(new BorderLayout())
    panelInferior.setOpaque(false)
    
    val lblImpacto = new JLabel(s"Impacto: ${habito.impacto}")
    lblImpacto.setFont(new Font("Arial", Font.ITALIC, 11))
    lblImpacto.setForeground(new Color(0xfe640b))
    
    val lblRecomendacion = new JLabel(habito.recomendacion)
    lblRecomendacion.setFont(new Font("Arial", Font.ITALIC, 11))
    lblRecomendacion.setForeground(new Color(0x40a02b))
    
    panelInferior.add(lblImpacto, BorderLayout.NORTH)
    panelInferior.add(lblRecomendacion, BorderLayout.SOUTH)
    
    panel.add(panelSuperior, BorderLayout.NORTH)
    panel.add(lblDescripcion, BorderLayout.CENTER)
    panel.add(panelInferior, BorderLayout.SOUTH)
    
    panel
  }
  
  private def crearCeldaTabla(texto: String, esEncabezado: Boolean): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5))
    
    val lbl = new JLabel(texto, SwingConstants.CENTER)
    if (esEncabezado) {
      lbl.setFont(new Font("Arial", Font.BOLD, 12))
      lbl.setForeground(new Color(0x1e66f5))
      panel.setBackground(new Color(0xeff1f5))
    } else {
      lbl.setFont(new Font("Arial", Font.PLAIN, 11))
    }
    
    panel.add(lbl, BorderLayout.CENTER)
    panel
  }
  
  private def crearCeldaTablaConColor(texto: String, color: Color): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5))
    
    val lbl = new JLabel(texto, SwingConstants.CENTER)
    lbl.setFont(new Font("Arial", Font.BOLD, 11))
    lbl.setForeground(color)
    
    panel.add(lbl, BorderLayout.CENTER)
    panel
  }
  
  private def crearTarjetaEstadistica(titulo: String, valor: String, descripcion: String, color: Color): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(color, 2),
      BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ))
    panel.setBackground(new Color(0xeff1f5))
    
    val lblTitulo = new JLabel(titulo)
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 12))
    lblTitulo.setForeground(color)
    
    val lblValor = new JLabel(valor)
    lblValor.setFont(new Font("Arial", Font.BOLD, 14))
    lblValor.setForeground(color)
    
    val lblDescripcion = new JLabel(descripcion)
    lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 10))
    lblDescripcion.setForeground(Color.GRAY)
    
    panel.add(lblTitulo, BorderLayout.NORTH)
    panel.add(lblValor, BorderLayout.CENTER)
    panel.add(lblDescripcion, BorderLayout.SOUTH)
    
    panel
  }
  
  private def obtenerColorTendencia(tendencia: String): Color = {
    tendencia match {
      case "Creciente" => new Color(0xd20f39)
      case "Decreciente" => new Color(0x40a02b)
      case "Estable" => new Color(0x1e66f5)
      case _ => Color.GRAY
    }
  }
  
  private def obtenerColorVariabilidad(variabilidad: Double): Color = {
    variabilidad match {
      case v if v < 30 => new Color(0x40a02b)
      case v if v < 60 => new Color(0xdf8e1d)
      case v if v < 100 => new Color(0xfe640b)
      case _ => new Color(0xd20f39)
    }
  }
  
  private def interpretarVariabilidad(variabilidad: Double): String = {
    variabilidad match {
      case v if v < 30 => "Muy Estable"
      case v if v < 60 => "Estable"
      case v if v < 100 => "Variable"
      case _ => "Muy Variable"
    }
  }
} 
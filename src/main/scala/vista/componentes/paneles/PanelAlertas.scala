package vista.componentes.paneles

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import controlador._
import modelo.entidades.{Alerta, Meta, Movimiento, Presupuesto}

import scala.collection.immutable.List
import javax.swing.ImageIcon

class PanelAlertas extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)
  
  private val scrollPane = new JScrollPane()
  private val panelContenido = new JPanel()
  private var solicitarActualizarAlertas: Option[() => Unit] = None
  
  initUI()
  
  private def initUI(): Unit = {
    setLayout(new BorderLayout())
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20))
    

    val panelSuperior = new JPanel(new BorderLayout())
    val lblTitulo = new JLabel("Alertas Inteligentes")
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 24))
    panelSuperior.add(lblTitulo, BorderLayout.WEST)
    

    panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS))
    scrollPane.setViewportView(panelContenido)
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
    

    add(panelSuperior, BorderLayout.NORTH)
    add(scrollPane, BorderLayout.CENTER)
    

    mostrarIndicadorCarga()
  }
  
  def setSolicitarActualizarAlertas(callback: () => Unit): Unit = {
    solicitarActualizarAlertas = Some(callback)

    cargarAlertasAutomaticamente()
  }
  
  private def cargarAlertasAutomaticamente(): Unit = {
    solicitarActualizarAlertas.foreach(_())
  }
  
  private def mostrarIndicadorCarga(): Unit = {
    panelContenido.removeAll()
    val lblCarga = new JLabel("Cargando alertas...")
    lblCarga.setFont(new Font("Arial", Font.PLAIN, 16))
    lblCarga.setAlignmentX(Component.CENTER_ALIGNMENT)
    panelContenido.add(lblCarga)
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  def mostrarMensajeVacio(): Unit = {
    panelContenido.removeAll()
    val lblMensaje = new JLabel("No existen datos para generar alertas.")
    lblMensaje.setFont(new Font("Arial", Font.PLAIN, 16))
    lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT)
    panelContenido.add(lblMensaje)
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  def mostrarAlertas(alertas: List[Alerta]): Unit = {
    panelContenido.removeAll()
    
    if (alertas.isEmpty) {
      mostrarMensajeVacio()
    } else {

      val alertasCriticas = alertas.filter(_.severidad == "critica")
      val alertasAltas = alertas.filter(_.severidad == "alta")
      val alertasMedias = alertas.filter(_.severidad == "media")
      val alertasBajas = alertas.filter(_.severidad == "baja")
      

      if (alertasCriticas.nonEmpty) {
        agregarSeccionAlertas("Alertas Críticas", alertasCriticas, new Color(0xd20f39))
      }
      

      if (alertasAltas.nonEmpty) {
        agregarSeccionAlertas("Alertas Altas", alertasAltas, new Color(0xfe640b))
      }
      

      if (alertasMedias.nonEmpty) {
        agregarSeccionAlertas("Alertas Medias", alertasMedias, new Color(0xdf8e1d))
      }
      

      if (alertasBajas.nonEmpty) {
        agregarSeccionAlertas("Alertas Informativas", alertasBajas, new Color(0x40a02b))
      }
      

      agregarResumenAlertas(alertas)
    }
    
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  private def agregarSeccionAlertas(titulo: String, alertas: List[Alerta], color: Color): Unit = {
    val panelSeccion = new JPanel(new BorderLayout())
    panelSeccion.setBorder(BorderFactory.createTitledBorder(titulo))
    panelSeccion.setBackground(new Color(0xeff1f5))
    
    val panelLista = new JPanel()
    panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS))
    panelLista.setBackground(new Color(0xeff1f5))
    
    alertas.foreach { alerta =>
      val panelAlerta = crearPanelAlerta(alerta, color)
      panelLista.add(panelAlerta)
      panelLista.add(Box.createVerticalStrut(10))
    }
    
    panelSeccion.add(panelLista, BorderLayout.CENTER)
    panelContenido.add(panelSeccion)
    panelContenido.add(Box.createVerticalStrut(15))
  }
  
  private def crearPanelAlerta(alerta: Alerta, color: Color): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(color, 2),
      BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ))
    panel.setBackground(Color.WHITE)
    

    val panelIzquierdo = new JPanel(new BorderLayout())
    panelIzquierdo.setOpaque(false)
    
    val lblTitulo = new JLabel(alerta.titulo)
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 14))
    lblTitulo.setForeground(color)
    
    val lblFecha = new JLabel(s"Fecha: ${alerta.fecha}")
    lblFecha.setFont(new Font("Arial", Font.PLAIN, 10))
    lblFecha.setForeground(Color.GRAY)
    
    panelIzquierdo.add(lblTitulo, BorderLayout.NORTH)
    panelIzquierdo.add(lblFecha, BorderLayout.SOUTH)
    

    val panelDerecho = new JPanel(new BorderLayout())
    panelDerecho.setOpaque(false)
    
    val lblDescripcion = new JLabel(alerta.descripcion)
    lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 12))
    
    val lblAccion = new JLabel(alerta.accionRecomendada)
    lblAccion.setFont(new Font("Arial", Font.ITALIC, 11))
    lblAccion.setForeground(new Color(0x179299))
    
    panelDerecho.add(lblDescripcion, BorderLayout.NORTH)
    panelDerecho.add(lblAccion, BorderLayout.SOUTH)
    
    panel.add(panelIzquierdo, BorderLayout.WEST)
    panel.add(panelDerecho, BorderLayout.CENTER)
    
    panel
  }
  
  private def agregarResumenAlertas(alertas: List[Alerta]): Unit = {
    val panelResumen = new JPanel(new GridLayout(1, 4, 10, 0))
    panelResumen.setBorder(BorderFactory.createTitledBorder("Resumen de Alertas"))
    
    val total = alertas.size
    val criticas = alertas.count(_.severidad == "critica")
    val altas = alertas.count(_.severidad == "alta")
    val medias = alertas.count(_.severidad == "media")
    val bajas = alertas.count(_.severidad == "baja")
    
    panelResumen.add(crearTarjetaResumen("Total", total.toString, new Color(0x4c4f69)))
    panelResumen.add(crearTarjetaResumen("Críticas", criticas.toString, new Color(0xd20f39)))
    panelResumen.add(crearTarjetaResumen("Altas", altas.toString, new Color(0xfe640b)))
    panelResumen.add(crearTarjetaResumen("Medias/Bajas", (medias + bajas).toString, new Color(0x40a02b)))
    
    panelContenido.add(panelResumen)
  }
  
  private def crearTarjetaResumen(titulo: String, valor: String, color: Color): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createLineBorder(color, 2))
    panel.setBackground(new Color(0xeff1f5))
    
    val lblTitulo = new JLabel(titulo, SwingConstants.CENTER)
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 12))
    lblTitulo.setForeground(color)
    
    val lblValor = new JLabel(valor, SwingConstants.CENTER)
    lblValor.setFont(new Font("Arial", Font.BOLD, 18))
    lblValor.setForeground(color)
    
    panel.add(lblTitulo, BorderLayout.NORTH)
    panel.add(lblValor, BorderLayout.CENTER)
    
    panel
  }
} 
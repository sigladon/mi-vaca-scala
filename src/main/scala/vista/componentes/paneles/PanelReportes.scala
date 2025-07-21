package vista.componentes.paneles

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import modelo.entidades.{Movimiento, Presupuesto, Meta}
import scala.collection.immutable.List
import java.time.format.DateTimeFormatter
import modelo.servicios.Utilidades

class PanelReportes extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)
  
  private val scrollPane = new JScrollPane()
  private val panelContenido = new JPanel()
  private val btnGenerarReporte = new JButton("Generar Reporte")
  private var solicitarGenerarReporte: Option[() => Unit] = None
  private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  
  initUI()
  
  private def initUI(): Unit = {
    setLayout(new BorderLayout())
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20))
    

    val panelSuperior = new JPanel(new BorderLayout())
    val lblTitulo = new JLabel("Reportes")
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 24))
    panelSuperior.add(lblTitulo, BorderLayout.WEST)
    panelSuperior.add(btnGenerarReporte, BorderLayout.EAST)
    

    btnGenerarReporte.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        solicitarGenerarReporte.foreach(_())
      }
    })
    

    panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS))
    scrollPane.setViewportView(panelContenido)
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
    

    add(panelSuperior, BorderLayout.NORTH)
    add(scrollPane, BorderLayout.CENTER)
    

    mostrarMensajeVacio()
  }
  
  def setSolicitarGenerarReporte(callback: () => Unit): Unit = {
    solicitarGenerarReporte = Some(callback)
  }
  
  def mostrarMensajeVacio(): Unit = {
    panelContenido.removeAll()
    val lblMensaje = new JLabel("No hay datos suficientes para generar reportes. Haz clic en 'Generar Reporte' para actualizar.")
    lblMensaje.setFont(new Font("Arial", Font.PLAIN, 16))
    lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT)
    panelContenido.add(lblMensaje)
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  def mostrarReporte(transacciones: List[Movimiento], presupuestos: List[Presupuesto], metas: List[Meta]): Unit = {
    panelContenido.removeAll()
    
    if (transacciones.isEmpty && presupuestos.isEmpty && metas.isEmpty) {
      mostrarMensajeVacio()
    } else {

      if (transacciones.nonEmpty) {
        val panelLinea = new PanelGraficaReportes(
          transacciones,
          _.fechaTransaccion,
          m => if (m.monto > 0) m.monto else 0.0,
          m => if (m.monto < 0) -m.monto else 0.0
        )
        panelLinea.setBorder(BorderFactory.createTitledBorder("Tendencia de Ingresos y Gastos"))
        panelContenido.add(panelLinea)
        panelContenido.add(Box.createVerticalStrut(20))

        val panelPastel = new PanelGraficaGastosCategoria(
          transacciones,
          _.fechaTransaccion,
          m => m.categoria.getOrElse("Sin categoría"),
          _.monto
        )
        panelPastel.setBorder(BorderFactory.createTitledBorder("Porcentaje de Gastos por Categoría"))
        panelContenido.add(panelPastel)
        panelContenido.add(Box.createVerticalStrut(20))
      }


      agregarSeccionResumen(transacciones)

      if (presupuestos.nonEmpty) {
        agregarSeccionPresupuestos(presupuestos)
      }

      if (metas.nonEmpty) {
        agregarSeccionMetas(metas, transacciones)
      }

      if (transacciones.nonEmpty) {
        agregarSeccionTransacciones(transacciones)
      }
    }
    
    panelContenido.revalidate()
    panelContenido.repaint()
  }
  
  private def agregarSeccionResumen(transacciones: List[Movimiento]): Unit = {
    val panelResumen = new JPanel(new GridLayout(1, 3, 10, 0))
    panelResumen.setBorder(BorderFactory.createTitledBorder("Resumen Financiero"))
    
    val totalTransacciones = transacciones.map(_.monto).sum
    val transaccionesActivas = transacciones.filter(_.estado == "Activo").map(_.monto).sum
    val transaccionesInactivas = transacciones.filter(_.estado != "Activo").map(_.monto).sum
    
    panelResumen.add(crearTarjeta("Total", s"$$$totalTransacciones"))
    panelResumen.add(crearTarjeta("Activas", s"$$$transaccionesActivas"))
    panelResumen.add(crearTarjeta("Inactivas", s"$$$transaccionesInactivas"))
    
    panelContenido.add(panelResumen)
    panelContenido.add(Box.createVerticalStrut(20))
  }
  
  private def agregarSeccionPresupuestos(presupuestos: List[Presupuesto]): Unit = {
    val panelPresupuestos = new JPanel(new BorderLayout())
    panelPresupuestos.setBorder(BorderFactory.createTitledBorder("Estado de Presupuestos"))
    
    val panelLista = new JPanel(new GridLayout(0, 1, 5, 5))
    presupuestos.foreach { presupuesto =>
      val lbl = new JLabel(s"${presupuesto.nombre}: $${presupuesto.limite} (${presupuesto.categorias.keys.mkString(", ")})")
      panelLista.add(lbl)
    }
    
    panelPresupuestos.add(panelLista, BorderLayout.CENTER)
    panelContenido.add(panelPresupuestos)
    panelContenido.add(Box.createVerticalStrut(20))
  }
  
  private def agregarSeccionMetas(metas: List[Meta], movimientos: List[Movimiento]): Unit = {
    val panelMetas = new JPanel(new BorderLayout())
    panelMetas.setBorder(BorderFactory.createTitledBorder("Progreso de Metas"))
    val panelLista = new JPanel()
    panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS))
    metas.foreach { meta =>
      val balance = Utilidades.balanceHastaFechaLimite(meta, movimientos)
      val progreso = if (meta.montoObjetivo > 0) (balance / meta.montoObjetivo * 100).toInt else 0
      val lbl = new JLabel(s"${meta.nombre}: $progreso% ($$${balance}/$$${meta.montoObjetivo})")
      panelLista.add(lbl)
    }
    panelMetas.add(panelLista, BorderLayout.CENTER)
    panelContenido.add(panelMetas)
  }
  
  private def agregarSeccionTransacciones(transacciones: List[Movimiento]): Unit = {
    val panelTransacciones = new JPanel(new BorderLayout())
    panelTransacciones.setBorder(BorderFactory.createTitledBorder("Últimas Transacciones"))
    
    val transaccionesRecientes = transacciones.sortBy(_.fechaTransaccion)(Ordering[java.time.LocalDate].reverse).take(5)
    val panelLista = new JPanel(new GridLayout(0, 1, 2, 2))
    
    transaccionesRecientes.foreach { transaccion =>
      val lbl = new JLabel(s"${transaccion.fechaTransaccion.format(dateFormatter)} - ${transaccion.descripcion} - $${transaccion.monto}")
      panelLista.add(lbl)
    }
    
    panelTransacciones.add(panelLista, BorderLayout.CENTER)
    panelContenido.add(panelTransacciones)
  }
  
  private def crearTarjeta(titulo: String, valor: String): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1))

    
    val lblTitulo = new JLabel(titulo, SwingConstants.CENTER)
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 14))
    
    val lblValor = new JLabel(valor, SwingConstants.CENTER)
    lblValor.setFont(new Font("Arial", Font.BOLD, 18))

    
    panel.add(lblTitulo, BorderLayout.NORTH)
    panel.add(lblValor, BorderLayout.CENTER)
    
    panel
  }
} 
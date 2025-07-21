package vista.ventanas

import java.awt.{BorderLayout, Dimension, Font, Color, GraphicsEnvironment}
import javax.swing._
import javax.swing.WindowConstants._
import vista.componentes.paneles.PanelAcercaDe
import javax.swing.ImageIcon

class VentanaPrincipalUI extends JFrame {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorPrimario = new Color(0x1e66f5)
  private val colorSecundario = new Color(0x40a02b)
  private val colorResalte = new Color(0xfe640b)
  private val colorBorde = new Color(0xccd0da)

  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)

  private val barraMenu = new JMenuBar()
  private val menuOpciones = new JMenu("Opciones")
  private val itemDashboard = new JMenuItem("Dashboard", new ImageIcon("src/main/scala/assets/dashboard.png"))
  private val itemTransacciones = new JMenuItem("Transacciones", new ImageIcon("src/main/scala/assets/transacciones.png"))
  private val itemPresupuestos = new JMenuItem("Presupuestos", new ImageIcon("src/main/scala/assets/presupuestos.png"))
  private val itemMetas = new JMenuItem("Metas", new ImageIcon("src/main/scala/assets/metas.png"))
  private val menuReportes = new JMenu("Reportes")
  private val itemReporteTendencia = new JMenuItem("Tendencia de ingresos y gastos", new ImageIcon("src/main/scala/assets/reporte_tendencia.png"))
  private val itemReportePastel = new JMenuItem("Porcentaje de gastos por categoría", new ImageIcon("src/main/scala/assets/reporte_pastel.png"))
  private val itemAlertas = new JMenuItem("Alertas Inteligentes", new ImageIcon("src/main/scala/assets/alertas.png"))
  private val itemEficiencia = new JMenuItem("Eficiencia Financiera", new ImageIcon("src/main/scala/assets/eficiencia.png"))
  private val itemComportamiento = new JMenuItem("Análisis de Comportamiento", new ImageIcon("src/main/scala/assets/comportamiento.png"))
  private val itemProyecciones = new JMenuItem("Proyecciones Financieras", new ImageIcon("src/main/scala/assets/proyecciones.png"))
  private val itemComparaciones = new JMenuItem("Comparaciones Temporales", new ImageIcon("src/main/scala/assets/comparaciones.png"))
  private val itemInsights = new JMenuItem("Insights Personalizados", new ImageIcon("src/main/scala/assets/insights.png"))
  private val menuCuenta = new JMenu("Cuenta")
  private val itemPerfil = new JMenuItem("Perfil", new ImageIcon("src/main/scala/assets/perfil.png"))
  private val itemCerrarSesion = new JMenuItem("Cerrar Sesión", new ImageIcon("src/main/scala/assets/cerrar_sesion.png"))
  private val menuAyuda = new JMenu("Ayuda")
  private val itemAcercaDe = new JMenuItem("Acerca de", new ImageIcon("src/main/scala/assets/info.png"))

  private val toolBar = new JToolBar()
  private val btnToolbarDashboard = new JButton("Dashboard", new ImageIcon("src/main/scala/assets/dashboard.png"))
  private val btnToolbarAgregarTransaccion = new JButton("Registrar Transacción", new ImageIcon("src/main/scala/assets/agregar.png"))
  private val btnToolbarAgregarMeta = new JButton("Agregar Meta", new ImageIcon("src/main/scala/assets/metas.png"))

  private val centralPanel = new JPanel()

  private var solicitarMostrarDashboard: Option[() => Unit] = None
  private var solicitarMostrarTransacciones: Option[() => Unit] = None
  private var solicitarMostrarPresupuestos: Option[() => Unit] = None
  private var solicitarMostrarMetas: Option[() => Unit] = None
  private var solicitarMostrarReporteTendencia: Option[() => Unit] = None
  private var solicitarMostrarReportePastel: Option[() => Unit] = None
  private var solicitarMostrarComportamiento: Option[() => Unit] = None
  private var solicitarMostrarProyecciones: Option[() => Unit] = None
  private var solicitarMostrarComparaciones: Option[() => Unit] = None
  private var solicitarMostrarInsights: Option[() => Unit] = None
  private var solicitarCerrarSesion: Option[() => Unit] = None
  private var solicitarAgregarTransaccion: Option[() => Unit] = None
  private var solicitarAgregarMeta: Option[() => Unit] = None
  private var solicitarGenerarReporte: Option[() => Unit] = None

  private val tituloVentana = "Sistema de Gestión Financiera - Mi Vaca"

  initUI()

  private def initUI(): Unit = {
    setTitle(tituloVentana)
    setSize(1200, 800)
    setLocationRelativeTo(null)
    setDefaultCloseOperation(EXIT_ON_CLOSE)
    setLayout(new BorderLayout())
    getContentPane.setBackground(colorFondo)

    barraMenu.setBackground(colorFondo)
    barraMenu.setForeground(colorTexto)
    barraMenu.setFont(fuenteSansSerif)
    for (menu <- barraMenu.getSubElements) {
      menu.asInstanceOf[JMenu].setFont(fuenteSansSerif)
      menu.asInstanceOf[JMenu].setForeground(colorTexto)
      menu.asInstanceOf[JMenu].setBackground(colorFondo)
    }
    configurarMenuSuperior()
    setJMenuBar(barraMenu)

    configurarToolbar()
    toolBar.setBackground(colorFondo)
    toolBar.setForeground(colorTexto)
    toolBar.setFont(fuenteSansSerif)
    add(toolBar, BorderLayout.NORTH)

    centralPanel.setLayout(new BorderLayout())
    centralPanel.setBackground(colorFondo)
    add(centralPanel, BorderLayout.CENTER)
  }

  private def configurarMenuSuperior(): Unit = {

    val menus = Seq(menuOpciones, menuReportes, menuCuenta, menuAyuda)
    for (menu <- menus) {
      menu.setFont(fuenteSansSerif)
      menu.setForeground(colorTexto)
      menu.setBackground(colorFondo)
    }
    val items = Seq(
      itemDashboard, itemTransacciones, itemPresupuestos, itemMetas,
      itemReporteTendencia, itemReportePastel, itemAlertas, itemEficiencia,
      itemComportamiento, itemProyecciones, itemComparaciones, itemInsights,
      itemPerfil, itemCerrarSesion, itemAcercaDe
    )
    for (item <- items) {
      item.setFont(fuenteSansSerif)
      item.setForeground(colorTexto)
      item.setBackground(colorFondo)
      item.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12))
    }

    menuOpciones.add(itemDashboard)
    menuOpciones.add(itemTransacciones)
    menuOpciones.add(itemPresupuestos)
    menuOpciones.add(itemMetas)
    barraMenu.add(menuOpciones)

    menuReportes.add(itemReporteTendencia)
    menuReportes.add(itemReportePastel)
    menuReportes.addSeparator()
    menuReportes.add(itemAlertas)
    menuReportes.add(itemEficiencia)
    menuReportes.add(itemComportamiento)
    menuReportes.add(itemProyecciones)
    menuReportes.add(itemComparaciones)
    menuReportes.add(itemInsights)
    barraMenu.add(menuReportes)

    menuCuenta.add(itemPerfil)
    menuCuenta.addSeparator()
    menuCuenta.add(itemCerrarSesion)
    barraMenu.add(menuCuenta)

    menuAyuda.add(itemAcercaDe)
    barraMenu.add(menuAyuda)


    itemDashboard.addActionListener(_ => solicitarMostrarDashboard.foreach(_()))
    itemTransacciones.addActionListener(_ => solicitarMostrarTransacciones.foreach(_()))
    itemPresupuestos.addActionListener(_ => solicitarMostrarPresupuestos.foreach(_()))
    itemMetas.addActionListener(_ => solicitarMostrarMetas.foreach(_()))
    itemReporteTendencia.addActionListener(_ => solicitarMostrarReporteTendencia.foreach(_()))
    itemReportePastel.addActionListener(_ => solicitarMostrarReportePastel.foreach(_()))
    itemAlertas.addActionListener(_ => controlador.ControladorPrincipal.mostrarVistaAlertas())
    itemEficiencia.addActionListener(_ => controlador.ControladorPrincipal.mostrarVistaEficiencia())
    itemComportamiento.addActionListener(_ => controlador.ControladorPrincipal.mostrarVistaComportamiento())
    itemProyecciones.addActionListener(_ => controlador.ControladorPrincipal.mostrarVistaProyecciones())
    itemComparaciones.addActionListener(_ => controlador.ControladorPrincipal.mostrarVistaComparaciones())
    itemInsights.addActionListener(_ => controlador.ControladorPrincipal.mostrarVistaInsights())
    itemCerrarSesion.addActionListener(_ => solicitarCerrarSesion.foreach(_()))
    itemPerfil.addActionListener(_ => controlador.ControladorPrincipal.mostrarPerfilUsuario())
    itemAcercaDe.addActionListener(_ => mostrarAcercaDe())
  }

  private def configurarToolbar(): Unit = {
    toolBar.setFloatable(false)
    val botones = Seq(btnToolbarDashboard, btnToolbarAgregarTransaccion, btnToolbarAgregarMeta)
    for (btn <- botones) {
      btn.setFont(fuenteSansSerif)
      btn.setForeground(colorTexto)
      btn.setBackground(colorFondo)
      btn.setFocusPainted(false)
      btn.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(colorBorde),
        BorderFactory.createEmptyBorder(6, 16, 6, 16)
      ))
    }
    toolBar.add(btnToolbarDashboard)
    toolBar.add(btnToolbarAgregarTransaccion)
    toolBar.add(btnToolbarAgregarMeta)

    btnToolbarDashboard.addActionListener(_ => solicitarMostrarDashboard.foreach(_()))
    btnToolbarAgregarTransaccion.addActionListener(_ => solicitarAgregarTransaccion.foreach(_()))
    btnToolbarAgregarMeta.addActionListener(_ => solicitarAgregarMeta.foreach(_()))
  }


  def setSolicitarMostrarDashboard(callback: () => Unit): Unit = {
    solicitarMostrarDashboard = Some(callback)
  }
  def setSolicitarMostrarTransacciones(callback: () => Unit): Unit = {
    solicitarMostrarTransacciones = Some(callback)
  }
  def setSolicitarMostrarPresupuestos(callback: () => Unit): Unit = {
    solicitarMostrarPresupuestos = Some(callback)
  }
  def setSolicitarMostrarMetas(callback: () => Unit): Unit = {
    solicitarMostrarMetas = Some(callback)
  }
  def setSolicitarMostrarReporteTendencia(callback: () => Unit): Unit = {
    solicitarMostrarReporteTendencia = Some(callback)
  }
  def setSolicitarMostrarReportePastel(callback: () => Unit): Unit = {
    solicitarMostrarReportePastel = Some(callback)
  }
  def setSolicitarMostrarComportamiento(callback: () => Unit): Unit = {
    solicitarMostrarComportamiento = Some(callback)
  }
  def setSolicitarMostrarProyecciones(callback: () => Unit): Unit = {
    solicitarMostrarProyecciones = Some(callback)
  }

  def setSolicitarMostrarComparaciones(callback: () => Unit): Unit = {
    solicitarMostrarComparaciones = Some(callback)
  }

  def setSolicitarMostrarInsights(callback: () => Unit): Unit = {
    solicitarMostrarInsights = Some(callback)
  }
  def setSolicitarCerrarSesion(callback: () => Unit): Unit = {
    solicitarCerrarSesion = Some(callback)
  }

  def setSolicitarAgregarTransaccion(callback: () => Unit): Unit = {
    solicitarAgregarTransaccion = Some(callback)
  }
  def setSolicitarAgregarMeta(callback: () => Unit): Unit = {
    solicitarAgregarMeta = Some(callback)
  }
  def setSolicitarGenerarReporte(callback: () => Unit): Unit = {
    solicitarGenerarReporte = Some(callback)
  }

  def cambiarVistaCentral(nuevaVista: JPanel): Unit = {
    println(s"[DEBUG] cambiarVistaCentral: agregando panel de tipo ${nuevaVista.getClass.getSimpleName}")
    centralPanel.removeAll()
    centralPanel.add(nuevaVista, BorderLayout.CENTER)
    centralPanel.revalidate()
    centralPanel.repaint()
  }


  def setTituloVentana(titulo: String): Unit = {
    setTitle(titulo)
    getRootPane.setFont(fuenteTitulo)
  }


  private def mostrarAcercaDe(): Unit = {
    val acercaDe = new PanelAcercaDe()
    JOptionPane.showMessageDialog(this, acercaDe, "Acerca de", JOptionPane.PLAIN_MESSAGE)
  }


  def mostrarBarraMenuToolbar(): Unit = {
    setJMenuBar(barraMenu)
    toolBar.setVisible(true)
    revalidate()
    repaint()
  }

  def ocultarBarraMenuToolbar(): Unit = {
    setJMenuBar(null)
    toolBar.setVisible(false)
    revalidate()
    repaint()
  }
}

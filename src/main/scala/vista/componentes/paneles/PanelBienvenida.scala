package vista.componentes.paneles

import javax.swing._
import java.awt._
import modelo.entidades.{Usuario, Movimiento, Presupuesto}
import java.time.LocalDate
import scala.collection.immutable.List
import javax.swing.ImageIcon

class PanelBienvenida(usuario: Usuario) extends JPanel {
  println(s"[DEBUG] PanelBienvenida creado para usuario: ${usuario.nombre}, movimientos: ${usuario.movimientos.size}, presupuestos: ${usuario.presupuestos.size}")

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)

  private val verdeOk = new Color(0x34, 0xa8, 0x53)
  private val amarilloPrecaucion = new Color(0xfb, 0xbc, 0x04)
  private val rojoPeligro = new Color(0xea, 0x43, 0x35)

  private var movimientosUsuario: List[Movimiento] = List.empty
  private var presupuestosUsuario: List[Presupuesto] = List.empty
  
  initUI()
  actualizarDatos(usuario.movimientos, usuario.presupuestos)

  private def initUI(): Unit = {
    setBackground(colorFondo)
    setLayout(new BorderLayout())
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20))


    val panelSuperior = new JPanel(new BorderLayout())
    panelSuperior.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0))

    val lblTitulo = new JLabel(s"Bienvenido, ${usuario.nombre}")
    lblTitulo.setFont(fuenteTitulo)
    lblTitulo.setForeground(colorTexto)
    
    val lblSubtitulo = new JLabel("Dashboard Financiero")
    lblSubtitulo.setFont(fuenteSansSerif)
    lblSubtitulo.setForeground(colorTexto)
    
    val panelTitulo = new JPanel(new BorderLayout())
    panelTitulo.add(lblTitulo, BorderLayout.NORTH)
    panelTitulo.add(lblSubtitulo, BorderLayout.SOUTH)
    
    panelSuperior.add(panelTitulo, BorderLayout.WEST)
    add(panelSuperior, BorderLayout.NORTH)


    val panelCentral = new JPanel(new BorderLayout())
    

    val panelMetricas = crearPanelMetricas()
    panelCentral.add(panelMetricas, BorderLayout.NORTH)
    

    val panelPresupuesto = crearPanelPresupuesto()
    panelCentral.add(panelPresupuesto, BorderLayout.CENTER)
    
    add(panelCentral, BorderLayout.CENTER)
  }
  
  private def crearPanelMetricas(): JPanel = {
    val panel = new JPanel(new GridLayout(1, 3, 15, 0))
    panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0))
    

    val (ingresos, egresos, balance) = calcularMetricasMesActual()
    val (ingresosAnterior, egresosAnterior, balanceAnterior) = calcularMetricasMesAnterior()
    

    val panelIngresos = crearPanelMetrica(
      "Ingresos del Mes",
      f"$$${ingresos}%.2f",
      verdeOk,
      calcularVariacion(ingresos, ingresosAnterior),
      "ingresos"
    )
    

    val panelEgresos = crearPanelMetrica(
      "Egresos del Mes",
      f"$$${Math.abs(egresos)}%.2f",
      rojoPeligro,
      calcularVariacion(egresos, egresosAnterior),
      "egresos"
    )
    

    val colorBalance = if (balance >= 0) verdeOk else rojoPeligro
    val panelBalance = crearPanelMetrica(
      "Balance del Mes",
      f"$$${balance}%.2f",
      colorBalance,
      calcularVariacion(balance, balanceAnterior),
      "balance"
    )
    
    panel.add(panelIngresos)
    panel.add(panelEgresos)
    panel.add(panelBalance)
    
    panel
  }
  
  private def crearPanelMetrica(titulo: String, valor: String, color: Color, variacion: Double, tipo: String): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(colorBorde),
      BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ))
    panel.setBackground(Color.WHITE)
    
    val lblTitulo = new JLabel(titulo)
    lblTitulo.setFont(fuenteSansSerif)
    lblTitulo.setForeground(colorTexto)
    
    val lblValor = new JLabel(valor)
    lblValor.setFont(new Font("Arial", Font.BOLD, 24))
    lblValor.setForeground(color)
    
    val lblVariacion = new JLabel()
    lblVariacion.setFont(fuenteSansSerif)
    lblVariacion.setForeground(colorTexto)

    if (variacion != 0) {
      val signo = if (variacion > 0) "+" else ""
      val colorVariacion =
        if (tipo == "egresos" && variacion > 0) rojoPeligro
        else if (tipo == "egresos" && variacion < 0) verdeOk
        else if (tipo == "ingresos" && variacion > 0) verdeOk
        else if (tipo == "ingresos" && variacion < 0) rojoPeligro
        else if (tipo == "balance" && variacion > 0) verdeOk
        else if (tipo == "balance" && variacion < 0) rojoPeligro
        else colorTexto
      lblVariacion.setText(s"$signo${"%.1f".format(variacion)}% vs mes anterior")
      lblVariacion.setForeground(colorVariacion)
    } else {
      lblVariacion.setText("Sin cambios vs mes anterior")
      lblVariacion.setForeground(colorTexto)
    }
    
    panel.add(lblTitulo, BorderLayout.NORTH)
    panel.add(lblValor, BorderLayout.CENTER)
    panel.add(lblVariacion, BorderLayout.SOUTH)
    
    panel
  }
  
  private def crearPanelPresupuesto(): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder("Estado del Presupuesto Anual"),
      BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ))
    
    val añoActual = LocalDate.now().getYear
    val presupuestoAnual = presupuestosUsuario.find(p => 
      p.inicioPresupuesto.getYear == añoActual && p.estaActivo
    )
    
    if (presupuestoAnual.isDefined) {
      val presupuesto = presupuestoAnual.get
      val (_, _, balanceActual) = calcularMetricasMesActual()
      val gastosAcumulados = calcularGastosAcumulados()
      val porcentajeUsado = (gastosAcumulados / presupuesto.limite) * 100
      
      val lblLimite = new JLabel(f"Límite Anual: $$${presupuesto.limite}%.2f")
      lblLimite.setFont(fuenteSansSerif)
      
      val lblGastado = new JLabel(f"Gastado: $$${gastosAcumulados}%.2f")
      lblGastado.setFont(fuenteSansSerif)
      
      val lblDisponible = new JLabel(f"Disponible: $$${presupuesto.limite - gastosAcumulados}%.2f")
      lblDisponible.setFont(fuenteSansSerif)
      

      val panelInfo = new JPanel()
      panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS))
      panelInfo.setAlignmentX(Component.CENTER_ALIGNMENT)
      lblLimite.setAlignmentX(Component.CENTER_ALIGNMENT)
      lblGastado.setAlignmentX(Component.CENTER_ALIGNMENT)
      lblDisponible.setAlignmentX(Component.CENTER_ALIGNMENT)
      panelInfo.add(lblLimite)
      panelInfo.add(lblGastado)
      panelInfo.add(lblDisponible)
      panelInfo.add(Box.createVerticalStrut(8))


      val barraProgreso = new JProgressBar(0, 100)
      barraProgreso.setValue(porcentajeUsado.toInt)
      barraProgreso.setStringPainted(true)
      barraProgreso.setString(s"${porcentajeUsado.toInt}% usado")
      barraProgreso.setPreferredSize(new Dimension(260, 26))
      barraProgreso.setMaximumSize(new Dimension(300, 30))
      barraProgreso.setMinimumSize(new Dimension(180, 20))
      barraProgreso.setFont(new Font("Arial", Font.BOLD, 13))

      if (porcentajeUsado > 90) {
        barraProgreso.setForeground(rojoPeligro)
      } else if (porcentajeUsado > 75) {
        barraProgreso.setForeground(amarilloPrecaucion)
      } else {
        barraProgreso.setForeground(verdeOk)
      }
      barraProgreso.setBackground(Color.WHITE)

      val panelContenido = new JPanel()
      panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS))
      panelContenido.setAlignmentX(Component.CENTER_ALIGNMENT)
      panelContenido.add(panelInfo)
      panelContenido.add(barraProgreso)
      panel.add(panelContenido, BorderLayout.CENTER)

    } else {
      val lblMensaje = new JLabel("No hay presupuesto anual configurado para este año")
      lblMensaje.setFont(fuenteSansSerif)
      lblMensaje.setHorizontalAlignment(SwingConstants.CENTER)
      panel.add(lblMensaje, BorderLayout.CENTER)
    }
    
    panel
  }
  
  private def calcularMetricasMesActual(): (Double, Double, Double) = {
    val mesActual = LocalDate.now().getMonthValue
    val añoActual = LocalDate.now().getYear
    
    val movimientosMes = movimientosUsuario.filter { mov =>
      mov.fechaTransaccion.getMonthValue == mesActual && 
      mov.fechaTransaccion.getYear == añoActual
    }
    
    val ingresos = movimientosMes.filter(_.monto > 0).map(_.monto).sum
    val egresos = movimientosMes.filter(_.monto < 0).map(_.monto).sum
    val balance = ingresos + egresos
    
    (ingresos, egresos, balance)
  }
  
  private def calcularMetricasMesAnterior(): (Double, Double, Double) = {
    val mesAnterior = LocalDate.now().minusMonths(1)
    val mesAnteriorNum = mesAnterior.getMonthValue
    val añoAnterior = mesAnterior.getYear
    
    val movimientosMes = movimientosUsuario.filter { mov =>
      mov.fechaTransaccion.getMonthValue == mesAnteriorNum && 
      mov.fechaTransaccion.getYear == añoAnterior
    }
    
    val ingresos = movimientosMes.filter(_.monto > 0).map(_.monto).sum
    val egresos = movimientosMes.filter(_.monto < 0).map(_.monto).sum
    val balance = ingresos + egresos
    
    (ingresos, egresos, balance)
  }
  
  private def calcularVariacion(actual: Double, anterior: Double): Double = {
    if (anterior == 0) {
      if (actual == 0) 0 else 100
    } else {
      ((actual - anterior) / Math.abs(anterior)) * 100
    }
  }
  
  private def calcularGastosAcumulados(): Double = {
    val añoActual = LocalDate.now().getYear
    val movimientosAño = movimientosUsuario.filter { mov =>
      mov.fechaTransaccion.getYear == añoActual && mov.monto < 0
    }
    Math.abs(movimientosAño.map(_.monto).sum)
  }
  
  def actualizarDatos(movimientos: List[Movimiento], presupuestos: List[Presupuesto]): Unit = {
    movimientosUsuario = movimientos
    presupuestosUsuario = presupuestos

    removeAll()
    initUI()
    revalidate()
    repaint()
  }
}

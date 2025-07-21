package vista.componentes.paneles

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import modelo.entidades.{Meta, Movimiento}
import scala.collection.immutable.List
import java.time.LocalDate
import javax.swing.table.DefaultTableModel
import javax.swing.ImageIcon

class PanelMetas extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)
  
  private val scrollPane = new JScrollPane()
  private val panelContenido = new JPanel()
  private val btnAgregarMeta = new JButton("Agregar Meta", new ImageIcon("src/main/scala/assets/agregar.png"))
  private val btnEditarMeta = new JButton("Editar", new ImageIcon("src/main/scala/assets/editar.png"))
  private val btnEliminarMeta = new JButton("Eliminar", new ImageIcon("src/main/scala/assets/eliminar.png"))
  private var solicitarAgregarMeta: Option[() => Unit] = None
  private var solicitarEditarMeta: Option[Meta => Unit] = None
  private var solicitarEliminarMeta: Option[Int => Unit] = None
  
  private val columnasTabla = Array[Object]("Título", "Monto", "Fecha límite", "Notas", "¿Logrado?")
  private val modeloTabla = new DefaultTableModel(columnasTabla, 0) {
    override def isCellEditable(row: Int, column: Int): Boolean = false
  }
  private val tablaMetas = new JTable(modeloTabla)
  private var metasActuales: List[Meta] = List.empty
  
  initUI()
  
  private def initUI(): Unit = {
    setLayout(new BorderLayout())
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20))
    

    val panelSuperior = new JPanel(new BorderLayout())
    val lblTitulo = new JLabel("Metas Financieras")
    lblTitulo.setFont(fuenteTitulo)
    lblTitulo.setForeground(colorTexto)
    panelSuperior.add(lblTitulo, BorderLayout.WEST)
    
    val panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT))
    panelBotones.add(btnAgregarMeta)
    panelBotones.add(btnEditarMeta)
    panelBotones.add(btnEliminarMeta)
    panelSuperior.add(panelBotones, BorderLayout.EAST)
    

    btnAgregarMeta.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        solicitarAgregarMeta.foreach(_())
      }
    })
    btnEditarMeta.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        val idx = tablaMetas.getSelectedRow
        if (idx >= 0 && idx < metasActuales.length) {
          solicitarEditarMeta.foreach(_(metasActuales(idx)))
        } else {
          JOptionPane.showMessageDialog(PanelMetas.this, "Selecciona una meta para editar.", "Aviso", JOptionPane.WARNING_MESSAGE)
        }
      }
    })
    btnEliminarMeta.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        val idx = tablaMetas.getSelectedRow
        if (idx >= 0 && idx < metasActuales.length) {
          solicitarEliminarMeta.foreach(_(metasActuales(idx).id))
        } else {
          JOptionPane.showMessageDialog(PanelMetas.this, "Selecciona una meta para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE)
        }
      }
    })
    

    tablaMetas.setFillsViewportHeight(true)
    val scrollTabla = new JScrollPane(tablaMetas)
    

    add(panelSuperior, BorderLayout.NORTH)
    add(scrollTabla, BorderLayout.CENTER)
    

    mostrarMensajeVacio()
  }
  
  def setSolicitarAgregarMeta(callback: () => Unit): Unit = {
    solicitarAgregarMeta = Some(callback)
  }
  
  def setSolicitarEditarMeta(callback: Meta => Unit): Unit = {
    solicitarEditarMeta = Some(callback)
  }
  
  def setSolicitarEliminarMeta(callback: Int => Unit): Unit = {
    solicitarEliminarMeta = Some(callback)
  }
  
  def mostrarMensajeVacio(): Unit = {
    modeloTabla.setRowCount(0)
    modeloTabla.addRow(Array[Object]("No hay metas configuradas. Haz clic en 'Agregar Meta' para comenzar.", "", "", "", ""))
    metasActuales = List.empty
  }
  
  def mostrarMetas(metas: List[Meta], movimientos: List[Movimiento]): Unit = {
    modeloTabla.setRowCount(0)
    metasActuales = metas
    if (metas.isEmpty) {
      mostrarMensajeVacio()
    } else {

      val metasOrdenadas = metas.sortBy(m => (m.fechaLimite, -m.montoObjetivo))

      var saldoPorFecha = scala.collection.mutable.Map[LocalDate, Double]()
      val fechas = metasOrdenadas.map(_.fechaLimite).distinct.sorted
      fechas.foreach { fecha =>
        val saldo = movimientos.filter(_.fechaTransaccion.isBefore(fecha.plusDays(1))).map(_.monto).sum
        saldoPorFecha(fecha) = saldo
      }

      val logradoPorMeta = scala.collection.mutable.Map[Int, Boolean]()
      fechas.foreach { fecha =>
        val metasMismaFecha = metasOrdenadas.filter(_.fechaLimite == fecha).sortBy(-_.montoObjetivo)
        var saldo = saldoPorFecha(fecha)
        metasMismaFecha.foreach { meta =>
          val logrado = saldo >= meta.montoObjetivo
          logradoPorMeta(meta.id) = logrado
          if (logrado) saldo -= meta.montoObjetivo
        }
      }

      metasOrdenadas.foreach { meta =>
        modeloTabla.addRow(Array[Object](
          meta.nombre,
          f"$$${meta.montoObjetivo}",
          meta.fechaLimite.toString,
          meta.descripcion,
          if (logradoPorMeta.getOrElse(meta.id, false)) "Logrado" else "No logrado"
        ))
      }
      metasActuales = metasOrdenadas
    }
  }
} 
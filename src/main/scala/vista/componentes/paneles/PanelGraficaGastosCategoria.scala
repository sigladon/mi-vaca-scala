package vista.componentes.paneles

import javax.swing._
import java.awt._
import java.time.LocalDate
import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.data.general.DefaultPieDataset
import modelo.entidades.Movimiento
import scala.collection.immutable.List
import javax.swing.ImageIcon

class PanelGraficaGastosCategoria(
  movimientos: List[Movimiento],
  obtenerFecha: Movimiento => LocalDate,
  obtenerCategoria: Movimiento => String,
  obtenerMonto: Movimiento => Double
) extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)

  private val coloresCatppuccin = Array(
    new Color(0xdc8a78),
    new Color(0xdd7878),
    new Color(0xea76cb),
    new Color(0x8839ef),
    new Color(0xd20f39),
    new Color(0xe64553),
    new Color(0xfe640b),
    new Color(0xdf8e1d),
    new Color(0x40a02b),
    new Color(0x179299),
    new Color(0x04a5e5),
    new Color(0x209fb5),
    new Color(0x1e66f5),
    new Color(0x7287fd)
  )

  private val rangos = Seq(
    ("7 días", 7),
    ("1 mes", 30),
    ("3 meses", 90),
    ("6 meses", 180),
    ("1 año", 365)
  )
  private val tabsRango = new JTabbedPane()

  setLayout(new BorderLayout())
  add(tabsRango, BorderLayout.CENTER)


  rangos.foreach { case (nombre, dias) =>
    val panel = crearPanelGrafica(dias)
    tabsRango.addTab(nombre, panel)
  }

  tabsRango.addChangeListener(_ => {
    val idx = tabsRango.getSelectedIndex
    val dias = rangos(idx)._2
    tabsRango.setComponentAt(idx, crearPanelGrafica(dias))
  })

  private def crearPanelGrafica(dias: Int): JPanel = {
    val dataset = crearDataset(dias)
    val chart = ChartFactory.createPieChart(
      "Porcentaje de Gastos por Categoría",
      dataset,
      true,
      true,
      false
    )

    val plot = chart.getPlot.asInstanceOf[org.jfree.chart.plot.PiePlot[String]]
    plot.setBackgroundPaint(colorFondo)
    chart.setBackgroundPaint(colorFondo)
    chart.getTitle.setPaint(colorTexto)
    chart.getLegend.setItemPaint(colorTexto)
    plot.setLabelPaint(colorTexto)
    plot.setLabelBackgroundPaint(colorFondo)

    val keys = dataset.getKeys
    for (i <- 0 until keys.size()) {
      plot.setSectionPaint(keys.get(i), coloresCatppuccin(i % coloresCatppuccin.length))
    }
    val chartPanel = new ChartPanel(chart)
    chartPanel.setPreferredSize(new Dimension(400, 300))
    val panel = new JPanel(new BorderLayout())
    panel.add(chartPanel, BorderLayout.CENTER)
    panel
  }

  private def crearDataset(dias: Int): DefaultPieDataset[String] = {
    val dataset = new DefaultPieDataset[String]()
    val hoy = LocalDate.now()
    val desde = hoy.minusDays(dias - 1)
    val gastosPorCategoria = scala.collection.mutable.Map[String, Double]().withDefaultValue(0.0)
    movimientos.foreach { m =>
      val fecha = obtenerFecha(m)
      val monto = obtenerMonto(m)
      val categoria = obtenerCategoria(m)
      if (!fecha.isBefore(desde) && !fecha.isAfter(hoy) && monto < 0) {
        gastosPorCategoria(categoria) += -monto
      }
    }
    gastosPorCategoria.foreach { case (cat, total) =>
      if (total > 0) dataset.setValue(cat, total: java.lang.Double)
    }
    dataset
  }
} 
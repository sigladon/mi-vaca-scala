package vista.componentes.paneles

import modelo.entidades.Movimiento
import org.jfree.chart.axis.{DateAxis, DateTickUnit, DateTickUnitType}
import org.jfree.chart.plot.{PlotOrientation, XYPlot}
import org.jfree.chart.renderer.xy.{XYAreaRenderer, XYLineAndShapeRenderer}
import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}

import java.awt._
import java.text.SimpleDateFormat
import java.time.{LocalDate, ZoneId}
import java.time.temporal.{ChronoField, TemporalAdjusters, WeekFields}
import java.util.{Locale, TimeZone}
import java.util.Date
import javax.swing._
import scala.collection.immutable.List

class PanelGraficaReportes(
                            movimientos: List[Movimiento],
                            obtenerFecha: Movimiento => LocalDate,
                            obtenerIngreso: Movimiento => Double,
                            obtenerGasto: Movimiento => Double
                          ) extends JPanel {


  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)

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
    val panel = crearPanelGraficaXY(dias)
    tabsRango.addTab(nombre, panel)
  }

  tabsRango.addChangeListener(_ => {
    val idx = tabsRango.getSelectedIndex
    val dias = rangos(idx)._2
    tabsRango.setComponentAt(idx, crearPanelGraficaXY(dias))
  })

  private def crearPanelGraficaXY(dias: Int): JPanel = {
    val hoy = LocalDate.now()
    val weekFields = WeekFields.of(Locale.getDefault)


    val (divisiones, tipoDivision, desde) = dias match {
      case d if d <= 7 => (dias, "dia", hoy.minusDays(dias - 1))
      case d if d <= 30 => (5, "semana", hoy.withDayOfMonth(1).`with`(weekFields.dayOfWeek(), 1))
      case d if d <= 90 => (3, "mes", hoy.minusMonths(2).withDayOfMonth(1))
      case d if d <= 180 => (6, "mes", hoy.minusMonths(5).withDayOfMonth(1))
      case _ => (12, "mes", hoy.minusYears(1).plusMonths(1).withDayOfMonth(1))
    }


    val fechasDivision = tipoDivision match {
      case "dia" =>
        (0 until divisiones).map(i => desde.plusDays(i))
      case "semana" =>
        (0 until divisiones).map(i => desde.plusWeeks(i))
      case "mes" =>
        (0 until divisiones).map(i => desde.plusMonths(i))
    }


    val serieIngresos = new XYSeries("Ingresos")
    val serieGastos = new XYSeries("Gastos")

    fechasDivision.foreach { fecha =>
      val (ingresos, gastos) = tipoDivision match {
        case "dia" =>
          val ingresos = movimientos.filter(m => obtenerFecha(m) == fecha).map(obtenerIngreso).sum
          val gastos = movimientos.filter(m => obtenerFecha(m) == fecha).map(obtenerGasto).sum
          (ingresos, gastos)
        case "semana" =>
          val finSemana = fecha.plusWeeks(1).minusDays(1)
          val ingresos = movimientos.filter(m => {
            val fechaMov = obtenerFecha(m)
            !fechaMov.isBefore(fecha) && !fechaMov.isAfter(finSemana)
          }).map(obtenerIngreso).sum
          val gastos = movimientos.filter(m => {
            val fechaMov = obtenerFecha(m)
            !fechaMov.isBefore(fecha) && !fechaMov.isAfter(finSemana)
          }).map(obtenerGasto).sum
          (ingresos, gastos)
        case "mes" =>
          val finMes = fecha.plusMonths(1).minusDays(1)
          val ingresos = movimientos.filter(m => {
            val fechaMov = obtenerFecha(m)
            !fechaMov.isBefore(fecha) && !fechaMov.isAfter(finMes)
          }).map(obtenerIngreso).sum
          val gastos = movimientos.filter(m => {
            val fechaMov = obtenerFecha(m)
            !fechaMov.isBefore(fecha) && !fechaMov.isAfter(finMes)
          }).map(obtenerGasto).sum
          (ingresos, gastos)
      }

      val date = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant)
      serieIngresos.add(date.getTime.toDouble, ingresos)
      serieGastos.add(date.getTime.toDouble, gastos)
    }


    val dataset = new XYSeriesCollection()
    dataset.addSeries(serieIngresos)
    dataset.addSeries(serieGastos)


    val chart = ChartFactory.createXYLineChart(
      "Tendencia de Ingresos y Gastos",
      "Fecha",
      "Monto ($)",
      dataset,
      PlotOrientation.VERTICAL,
      true,
      true,
      false
    )


    val colorIngresos = new Color(0x40a02b)
    val colorGastos = new Color(0xd20f39)
    val colorIngresosFill = new Color(64, 160, 43, 30)
    val colorGastosFill = new Color(210, 15, 57, 30)

    val plot = chart.getPlot.asInstanceOf[XYPlot]
    plot.setBackgroundPaint(colorFondo)
    plot.setDomainGridlinePaint(colorBorde)
    plot.setRangeGridlinePaint(colorBorde)
    chart.setBackgroundPaint(colorFondo)
    chart.getTitle.setPaint(colorTexto)
    chart.getLegend.setItemPaint(colorTexto)
    chart.getTitle.setFont(fuenteTitulo)
    chart.getLegend.setItemFont(fuenteSansSerif)


    val axis = new DateAxis("Fecha")


    val dateFormat = dias match {
      case d if d <= 7 => "dd-MMM"
      case d if d <= 30 => "dd-MMM"
      case _ => "MMM"
    }
    axis.setDateFormatOverride(new SimpleDateFormat(dateFormat))


    val primeraFecha = fechasDivision.head
    val ultimaFecha = fechasDivision.last
    val desdeDate = Date.from(primeraFecha.atStartOfDay(ZoneId.systemDefault()).toInstant)
    val hastaDate = Date.from(ultimaFecha.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant)


    val tickUnit = tipoDivision match {
      case "dia" =>
        new DateTickUnit(DateTickUnitType.DAY, 1)

      case "semana" =>
        new DateTickUnit(DateTickUnitType.DAY, 1)

      case "mes" =>
        val unit = new DateTickUnit(DateTickUnitType.MONTH, 1)
        axis.setTickMarkPosition(org.jfree.chart.axis.DateTickMarkPosition.START)
        unit
    }

    axis.setTickUnit(tickUnit)


    if (tipoDivision == "semana") {

      axis.setAutoTickUnitSelection(false)


      val formatoPersonalizado = new SimpleDateFormat("dd/MM")
      axis.setDateFormatOverride(formatoPersonalizado)



      val margenTemporal = 12 * 60 * 60 * 1000L
      axis.setRange(
        desdeDate.getTime - margenTemporal,
        hastaDate.getTime + margenTemporal
      )
    }


    axis.setTickLabelPaint(colorTexto)
    axis.setTickLabelFont(fuenteSansSerif)
    axis.setLabelPaint(colorTexto)
    axis.setTickMarksVisible(true)
    axis.setTickLabelsVisible(true)

    plot.setDomainAxis(axis)
    plot.getRangeAxis.setLabelPaint(colorTexto)
    plot.getRangeAxis.setTickLabelPaint(colorTexto)


    val lineRenderer = new XYLineAndShapeRenderer(true, true)
    lineRenderer.setSeriesStroke(0, new java.awt.BasicStroke(3.0f))
    lineRenderer.setSeriesStroke(1, new java.awt.BasicStroke(3.0f))
    lineRenderer.setSeriesPaint(0, colorIngresos)
    lineRenderer.setSeriesPaint(1, colorGastos)


    lineRenderer.setSeriesShapesVisible(0, true)
    lineRenderer.setSeriesShapesVisible(1, true)
    lineRenderer.setSeriesShapesFilled(0, true)
    lineRenderer.setSeriesShapesFilled(1, true)


    lineRenderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-4, -4, 8, 8))
    lineRenderer.setSeriesShape(1, new java.awt.geom.Ellipse2D.Double(-4, -4, 8, 8))


    plot.setDataset(0, dataset)
    plot.setRenderer(0, lineRenderer)

    val chartPanel = new ChartPanel(chart)
    chartPanel.setPreferredSize(new Dimension(600, 300))
    val panel = new JPanel(new BorderLayout())
    panel.add(chartPanel, BorderLayout.CENTER)
    panel
  }
}
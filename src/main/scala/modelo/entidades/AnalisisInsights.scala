package modelo.entidades

case class AnalisisInsights(
                             insightsUrgentes: List[Insight],
                             insightsImportantes: List[Insight],
                             insightsOportunidades: List[Insight],
                             insightsMejoras: List[Insight],
                             totalInsights: Int,
                             insightsImplementados: Int,
                             scoreFinanciero: Double,
                             nivelConfianza: String
                           )

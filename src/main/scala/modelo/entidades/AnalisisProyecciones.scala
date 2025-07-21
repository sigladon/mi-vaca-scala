package modelo.entidades

case class AnalisisProyecciones(
                                 proyeccionesMensuales: List[ProyeccionMensual],
                                 proyeccionesCategorias: List[ProyeccionCategoria],
                                 proyeccionesMetas: List[ProyeccionMeta],
                                 proyeccionesPresupuestos: List[ProyeccionPresupuesto],
                                 ahorroAnualProyectado: Double,
                                 probabilidadObjetivos: Double,
                                 riesgoFinanciero: String,
                                 oportunidadesAhorro: List[String]
                               )

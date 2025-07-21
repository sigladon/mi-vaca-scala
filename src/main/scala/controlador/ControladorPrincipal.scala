package controlador

import modelo.entidades.{PerfilUsuarioData, Usuario}
import modelo.servicios.GestorDatos
import vista.componentes.overlays.{OverlayMeta, OverlayTransaccion}
import vista.componentes.paneles._
import vista.ventanas.{LoginUI, RegistrarseUI, VentanaPrincipalUI}

import javax.swing._

object ControladorPrincipal {

    private val nombrePrograma = "Mi Vaca: Sistema de Gestión Financiera Personal"
    private var usuario: Option[Usuario] = None
    private var cUsuario: Option[CUsuario] = None
    private var vista: Option[VentanaPrincipalUI] = None


    private var panelMetas: Option[PanelMetas] = None
    private var panelPresupuestos: Option[PanelPresupuestos] = None
    private var panelTransacciones: Option[PanelTransacciones] = None


  private var cMetas: Option[CMetas] = None
    private var cPresupuestos: Option[CPresupuestos] = None
    private var cTransacciones: Option[CTransacciones] = None
    private var cReportes: Option[CReportes] = None
    private var cAlertas: Option[CAlertas] = None
    private var cEficiencia: Option[CEficiencia] = None
    private var cComportamiento: Option[CComportamiento] = None
    private var cProyecciones: Option[CProyecciones] = None
    private var cComparaciones: Option[CComparaciones] = None
    private var cInsights: Option[CInsights] = None

    def iniciar(): Unit = {
      vista = Some(new VentanaPrincipalUI())
      configurarNavegacion()
      init()
    }

    private def configurarNavegacion(): Unit = {
      vista.foreach { v =>
        v.setSolicitarMostrarDashboard(() => mostrarDashboard())
        v.setSolicitarMostrarPresupuestos(() => mostrarVistaPresupuestos())
        v.setSolicitarMostrarTransacciones(() => mostrarVistaTransacciones())
        v.setSolicitarMostrarMetas(() => mostrarVistaMetas())
        v.setSolicitarCerrarSesion(() => cerrarSesion())

        v.setSolicitarAgregarTransaccion(() => mostrarOverlayAgregarTransaccion())
        v.setSolicitarAgregarMeta(() => mostrarOverlayAgregarMeta())
        v.setSolicitarGenerarReporte(() => generarReporteCompleto())

        v.setSolicitarMostrarReporteTendencia(() => mostrarSoloReporteTendencia())
        v.setSolicitarMostrarReportePastel(() => mostrarSoloReportePastel())
        v.setSolicitarMostrarComportamiento(() => mostrarVistaComportamiento())
        v.setSolicitarMostrarProyecciones(() => mostrarVistaProyecciones())
        v.setSolicitarMostrarComparaciones(() => mostrarVistaComparaciones())
        v.setSolicitarMostrarInsights(() => mostrarVistaInsights())
      }
    }

    private def init(): Unit = {
      vista.foreach(_.setVisible(true))
      try {
        val token = GestorDatos.cargarToken()
        if (token != null) {
          println("Mostrando bienvenida")
          mostrarVistaBienvenida()
        } else {
          println("Mostrando login")
          mostrarVistaLogin()
        }
      } catch {
        case _: Exception =>
          println("Mostrando login")
          mostrarVistaLogin()
      }
    }

    private def cambiarVistaCentral(nuevaVista: JPanel): Unit = {
      vista.foreach(_.cambiarVistaCentral(nuevaVista))
    }

    private def mostrarVistaLogin(): Unit = {
      vista.foreach(_.ocultarBarraMenuToolbar())
      val vistaLogin = new LoginUI()
      cUsuario = Some(new CUsuario(Left(vistaLogin)))
      vistaLogin.setSolicitarMostrarRegistro(() => mostrarVistaRegistrarse())
      vistaLogin.setSolicitarMostrarBienvenida(() => mostrarVistaBienvenida())
      cambiarVistaCentral(vistaLogin)
      vista.foreach(_.setTituloVentana(s"$nombrePrograma - Login"))
    }

    private def mostrarVistaRegistrarse(): Unit = {
      vista.foreach(_.ocultarBarraMenuToolbar())
      val vistaRegistrarse = new RegistrarseUI()
      cUsuario.foreach(_.cambiarVista(Right(vistaRegistrarse)))
      cambiarVistaCentral(vistaRegistrarse)
      vista.foreach(_.setTituloVentana(s"$nombrePrograma - Registrarse"))
      vistaRegistrarse.setSolicitarMostrarLogin(() => mostrarVistaLogin())
    }

    private def mostrarVistaBienvenida(): Unit = {
      vista.foreach(_.mostrarBarraMenuToolbar())
      try {
        val token = GestorDatos.cargarToken()
        if (token != null) {
          val idUsuario = token.idUsuario
          val usuarioData = GestorDatos.cargarUsuario()
          println(s"[DEBUG] mostrarVistaBienvenida: usuarioData = $usuarioData, idUsuario (token) = $idUsuario")
          if (usuarioData != null) println(s"[DEBUG] usuarioData.id = ${usuarioData.id}")
          if (usuarioData != null && usuarioData.id == idUsuario) {
            usuario = Some(usuarioData)
            val panelBienvenida = new PanelBienvenida(usuarioData)
            panelBienvenida.actualizarDatos(usuarioData.movimientos, usuarioData.presupuestos)
            cambiarVistaCentral(panelBienvenida)
            vista.foreach(_.setTituloVentana(s"$nombrePrograma - Bienvenida"))
          } else {
            println("[DEBUG] No coincide usuarioData.id con idUsuario o usuarioData es null, mostrando login")
            mostrarVistaLogin()
          }
        } else {
          println("[DEBUG] No hay token, mostrando login")
          mostrarVistaLogin()
        }
      } catch {
        case e: Exception =>
          println(s"[DEBUG] Excepción en mostrarVistaBienvenida: ${e.getMessage}")
          mostrarVistaLogin()
      }
    }

    private def mostrarDashboard(): Unit = {
      vista.foreach(_.mostrarBarraMenuToolbar())
      usuario.foreach { user =>
        val panelBienvenida = new PanelBienvenida(user)
        panelBienvenida.actualizarDatos(user.movimientos, user.presupuestos)
        cambiarVistaCentral(panelBienvenida)
        vista.foreach(_.setTituloVentana(s"$nombrePrograma - Dashboard"))
      }
    }

    private def mostrarVistaMetas(): Unit = {
      vista.foreach(_.mostrarBarraMenuToolbar())
      if (panelMetas.isEmpty) {
        panelMetas = Some(new PanelMetas())
      }
      
      usuario.foreach { user =>
        cMetas = inicializarOActualizar(cMetas, new CMetas(panelMetas.get, user), (c: CMetas) => c.actualizarUsuario(user))
        
        panelMetas.foreach { panel =>
          panel.setSolicitarAgregarMeta(() => mostrarOverlayAgregarMeta())
          panel.setSolicitarEditarMeta(meta => mostrarOverlayEditarMeta(meta))
          panel.setSolicitarEliminarMeta(metaId => {
            val exito = cMetas.get.eliminarMeta(metaId)
            if (exito) {

              actualizarControladores(cMetas.get.obtenerUsuario)
            }
          })
          panel.mostrarMetas(user.metas, user.movimientos)
          cambiarVistaCentral(panel)
          vista.foreach(_.setTituloVentana(s"$nombrePrograma - Metas"))
        }
      }
    }

    private def mostrarVistaPresupuestos(): Unit = {
      vista.foreach(_.mostrarBarraMenuToolbar())
      if (panelPresupuestos.isEmpty) {
        panelPresupuestos = Some(new PanelPresupuestos())
      }
      
      usuario.foreach { user =>
        cPresupuestos = inicializarOActualizar(cPresupuestos, new CPresupuestos(panelPresupuestos.get, user), (c: CPresupuestos) => c.actualizarUsuario(user))
        
        panelPresupuestos.foreach { panel =>
          panel.setSolicitarEditarMonto(nuevoMonto => {
            val exito = cPresupuestos.get.actualizarMontoPresupuesto(nuevoMonto)
            if (exito) {

              actualizarControladores(cPresupuestos.get.obtenerUsuario)
            }
          })
          panel.setSolicitarAgregarCategoria(nombreCategoria => {
            val exito = cPresupuestos.get.agregarCategoria(nombreCategoria)
            if (exito) {

              actualizarControladores(cPresupuestos.get.obtenerUsuario)
            }
          })
          panel.setSolicitarEliminarCategoria(nombreCategoria => {
            val exito = cPresupuestos.get.eliminarCategoria(nombreCategoria)
            if (exito) {

              actualizarControladores(cPresupuestos.get.obtenerUsuario)
            }
          })
          panel.setSolicitarEditarCategoria((nombreActual, nuevoNombre) => {
            val exito = cPresupuestos.get.editarCategoria(nombreActual, nuevoNombre)
            if (exito) {

              actualizarControladores(cPresupuestos.get.obtenerUsuario)
            }
          })
          panel.mostrarPresupuestos(user.presupuestos, user.movimientos)
          cambiarVistaCentral(panel)
          vista.foreach(_.setTituloVentana(s"$nombrePrograma - Presupuestos"))
        }
      }
    }

    private def mostrarVistaTransacciones(): Unit = {
      vista.foreach(_.mostrarBarraMenuToolbar())
      if (panelTransacciones.isEmpty) {
        panelTransacciones = Some(new PanelTransacciones())
      }
      
      usuario.foreach { user =>
        cTransacciones = inicializarOActualizar(cTransacciones, new CTransacciones(panelTransacciones.get, user), (c: CTransacciones) => c.actualizarUsuario(user))
        
        panelTransacciones.foreach { panel =>
          panel.setSolicitarAgregarTransaccion(() => mostrarOverlayAgregarTransaccion())
        panel.setSolicitarEditarTransaccion(transaccion => mostrarOverlayEditarTransaccion(transaccion))
        panel.setSolicitarEliminarTransaccion(transaccionId => {
          val exito = cTransacciones.get.eliminarTransaccion(transaccionId)
          if (exito) {

            actualizarControladores(cTransacciones.get.obtenerUsuario)
          }
        })
          panel.mostrarTransacciones(user.movimientos)
          cambiarVistaCentral(panel)
          vista.foreach(_.setTituloVentana(s"$nombrePrograma - Transacciones"))
        }
      }
    }


  private def mostrarOverlayAgregarMeta(): Unit = {
      if (panelMetas.isEmpty) {
        panelMetas = Some(new PanelMetas())
      }
      usuario.foreach { user =>
        cMetas = inicializarOActualizar(cMetas, new CMetas(panelMetas.get, user), (c: CMetas) => c.actualizarUsuario(user))
        (vista, cMetas) match {
          case (Some(_), Some(ctrl)) =>
            val overlay = new OverlayMeta()
            overlay.configurarParaAgregar((nombre, descripcion, monto, fecha) => {
              val exito = ctrl.agregarMeta(nombre, descripcion, monto, fecha)
              if (exito) {

                actualizarControladores(ctrl.obtenerUsuario)
              }
              exito
            })
            overlay.mostrar()
          case _ =>
            JOptionPane.showMessageDialog(
              vista.orNull,
              "No se puede agregar meta en este momento.",
              "Error",
              JOptionPane.ERROR_MESSAGE
            )
        }
      }
    }

    private def mostrarOverlayEditarMeta(meta: modelo.entidades.Meta): Unit = {
      usuario.foreach { user =>
        cMetas = inicializarOActualizar(cMetas, new CMetas(panelMetas.get, user), (c: CMetas) => c.actualizarUsuario(user))
        (vista, cMetas) match {
          case (Some(_), Some(ctrl)) =>
            val overlay = new OverlayMeta()
            overlay.configurarParaEditar(meta, (nombre, descripcion, monto, fecha) => {
              val exito = ctrl.editarMeta(meta.id, nombre, descripcion, monto, fecha)
              if (exito) {

                actualizarControladores(ctrl.obtenerUsuario)
              }
              exito
            })
            overlay.mostrar()
          case _ =>
            JOptionPane.showMessageDialog(
              vista.orNull,
              "No se puede editar meta en este momento.",
              "Error",
              JOptionPane.ERROR_MESSAGE
            )
        }
      }
    }



    private def mostrarOverlayEditarTransaccion(transaccion: modelo.entidades.Movimiento): Unit = {
      if (panelTransacciones.isEmpty) {
        panelTransacciones = Some(new PanelTransacciones())
      }
      usuario.foreach { user =>
        if (cTransacciones.isEmpty) {
          cTransacciones = Some(new CTransacciones(panelTransacciones.get, user))
        } else {
          cTransacciones.foreach(_.actualizarUsuario(user))
        }
        (vista, panelTransacciones, cTransacciones, usuario) match {
          case (Some(v), Some(_), Some(ctrl), Some(user)) =>
            val categoriasDisponibles = obtenerCategoriasDisponibles(user)
            val overlay = new OverlayTransaccion(
              v,
              categoriasDisponibles,
              (idOpt, monto, descripcion, categoria, notas, fecha) => {
                // idOpt.get debe ser igual a transaccion.id
                val exito = ctrl.editarTransaccion(transaccion.id, monto, descripcion, categoria, notas, fecha)
                if (exito) {
                  actualizarControladores(ctrl.obtenerUsuario)
                }
                exito
              },
              Some(transaccion)
            )
            overlay.setVisible(true)
          case _ =>
            JOptionPane.showMessageDialog(
              vista.orNull,
              "No se puede editar transacción en este momento.",
              "Error",
              JOptionPane.ERROR_MESSAGE
            )
        }
      }
    }

    private def mostrarOverlayAgregarTransaccion(): Unit = {
      if (panelTransacciones.isEmpty) {
        panelTransacciones = Some(new PanelTransacciones())
      }
      usuario.foreach { user =>
        cTransacciones = inicializarOActualizar(cTransacciones, new CTransacciones(panelTransacciones.get, user), (c: CTransacciones) => c.actualizarUsuario(user))
        (vista, panelTransacciones, cTransacciones, usuario) match {
          case (Some(v), Some(_), Some(ctrl), Some(user)) =>

            val categoriasDisponibles = obtenerCategoriasDisponibles(user)
            val overlay = new OverlayTransaccion(v, categoriasDisponibles, (idOpt: Option[Int], monto, descripcion, categoria, notas, fecha) => {
              val exito = ctrl.agregarTransaccion(monto, descripcion, categoria, notas, fecha)
              if (exito) {
                actualizarControladores(ctrl.obtenerUsuario)
              }
              exito
            }, None)
            overlay.setVisible(true)
          case _ =>
            JOptionPane.showMessageDialog(
              vista.orNull,
              "No se puede agregar transacción en este momento.",
              "Error",
              JOptionPane.ERROR_MESSAGE
            )
        }
      }
    }

    private def obtenerCategoriasDisponibles(user: Usuario): Seq[String] = {

      val categoriasMovimientos = user.movimientos.flatMap(_.categoria).distinct
      

      val categoriasPresupuestos = user.presupuestos.flatMap(_.categorias.keys).distinct
      

      (categoriasMovimientos ++ categoriasPresupuestos).distinct.sorted
    }

  private def generarReporteCompleto(): Unit = {
      cReportes.foreach { reportes =>
        val reporteMensual = reportes.generarReporteMensual(java.time.LocalDate.now().getMonthValue, java.time.LocalDate.now().getYear)
        val reportePresupuestos = reportes.generarReportePresupuestos()
        val reporteMetas = reportes.generarReporteMetas()
        
        JOptionPane.showMessageDialog(
          vista.orNull,
          s"Reporte generado exitosamente:\n" +
          s"- Transacciones del mes: ${reporteMensual.getOrElse("totalTransacciones", 0)}\n" +
          s"- Presupuestos activos: ${reportePresupuestos.getOrElse("totalPresupuestos", 0)}\n" +
          s"- Metas activas: ${reporteMetas.getOrElse("metasActivas", 0)}",
          "Reporte Generado",
          JOptionPane.INFORMATION_MESSAGE
        )
      }
    }


    private def mostrarSoloReporteTendencia(): Unit = {
      usuario.foreach { user =>
        val panel = new PanelGraficaReportes(
          user.movimientos,
          _.fechaTransaccion,
          m => if (m.monto > 0) m.monto else 0.0,
          m => if (m.monto < 0) -m.monto else 0.0
        )
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20))
        cambiarVistaCentral(panel)
        vista.foreach(_.setTituloVentana(s"$nombrePrograma - Tendencia de ingresos y gastos"))
      }
    }
    private def mostrarSoloReportePastel(): Unit = {
      usuario.foreach { user =>
        val panel = new PanelGraficaGastosCategoria(
          user.movimientos,
          _.fechaTransaccion,
          m => m.categoria.getOrElse("Sin categoría"),
          _.monto
        )
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20))
        cambiarVistaCentral(panel)
        vista.foreach(_.setTituloVentana(s"$nombrePrograma - Porcentaje de gastos por categoría"))
      }
    }

    private def cerrarSesion(): Unit = {
      val respuesta = JOptionPane.showConfirmDialog(
        vista.orNull,
        "¿Estás seguro de querer cerrar sesión?",
        "Confirmar cerrar sesión",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
      )

      if (respuesta == JOptionPane.YES_OPTION) {
        try {
          GestorDatos.eliminarToken()
        } catch {
          case _: Exception =>
        }
        

        cMetas = None
        cPresupuestos = None
        cTransacciones = None
        cReportes = None
        cAlertas = None
        cEficiencia = None
        cComportamiento = None
        cProyecciones = None
        
        mostrarVistaLogin()
      }
    }

    def mostrarPerfilUsuario(): Unit = {
      vista.foreach(_.mostrarBarraMenuToolbar())
      usuario match {
        case Some(user) =>
          val datos = PerfilUsuarioData(user.nombre, user.correos.headOption.getOrElse(""), user.username)
          val panel = new PanelPerfilUsuario(datos, (nuevo, cambioPass) => {

            val errores = List(
              if (nuevo.nombre.isEmpty) Some("El nombre no puede estar vacío.") else None,
              if (nuevo.correo.isEmpty) Some("El correo no puede estar vacío.") else None,
              if (nuevo.usuario.isEmpty) Some("El usuario no puede estar vacío.") else None
            ).flatten
            val erroresPass = cambioPass match {
              case Some((actual: String, nueva: String, confirmar: String)) if actual.nonEmpty || nueva.nonEmpty || confirmar.nonEmpty =>
                List(
                  if (!user.verificarContrasenia(actual)) Some("La contraseña actual es incorrecta.") else None,
                  if (nueva.length < 6) Some("La nueva contraseña debe tener al menos 6 caracteres.") else None,
                  if (nueva != confirmar) Some("La nueva contraseña y la confirmación no coinciden.") else None
                ).flatten
              case _ => Nil
            }
            val todosErrores = errores ++ erroresPass
            if (todosErrores.nonEmpty) {
              JOptionPane.showMessageDialog(vista.orNull, todosErrores.mkString("\n"), "Error", JOptionPane.ERROR_MESSAGE)
            } else {

              val nuevoUsuario = user.copy(
                nombre = nuevo.nombre,
                username = nuevo.usuario,
                correos = List(nuevo.correo),
                contrasenia = cambioPass.map(_._2).filter(_.nonEmpty).getOrElse(user.contrasenia)
              )
              modelo.servicios.GestorDatos.guardarUsuario(nuevoUsuario)
              usuario = Some(nuevoUsuario)
              JOptionPane.showMessageDialog(vista.orNull, "Datos actualizados correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE)
            }
          })
          cambiarVistaCentral(panel)
          vista.foreach(_.setTituloVentana(s"$nombrePrograma - Perfil de Usuario"))
        case None =>
          JOptionPane.showMessageDialog(vista.orNull, "No hay usuario cargado.", "Error", JOptionPane.ERROR_MESSAGE)
      }
    }

    def mostrarVistaAlertas(): Unit = {
      vista.foreach(_.mostrarBarraMenuToolbar())
      usuario.foreach { user =>
        cAlertas = inicializarOActualizar(cAlertas, new CAlertas(user), (c: CAlertas) => c.actualizarUsuario(user))
        
        val panelAlertas = new PanelAlertas()
        panelAlertas.setSolicitarActualizarAlertas(() => {
          val alertas = cAlertas.get.generarAlertas()
          panelAlertas.mostrarAlertas(alertas)
        })
        
        cambiarVistaCentral(panelAlertas)
        vista.foreach(_.setTituloVentana(s"$nombrePrograma - Alertas Inteligentes"))
      }
    }

    def mostrarVistaEficiencia(): Unit = {
      vista.foreach(_.mostrarBarraMenuToolbar())
      usuario.foreach { user =>
        cEficiencia = inicializarOActualizar(cEficiencia, new CEficiencia(user), (c: CEficiencia) => c.actualizarUsuario(user))
        
        val panelEficiencia = new PanelEficiencia()
        panelEficiencia.setSolicitarActualizarEficiencia(() => {
          val metricas = cEficiencia.get.generarReporteEficiencia()
          panelEficiencia.mostrarEficiencia(metricas)
        })
        
        cambiarVistaCentral(panelEficiencia)
        vista.foreach(_.setTituloVentana(s"$nombrePrograma - Eficiencia Financiera"))
      }
    }

    def mostrarVistaComportamiento(): Unit = {
      vista.foreach(_.mostrarBarraMenuToolbar())
      usuario.foreach { user =>
        if (cComportamiento.isEmpty) {
          cComportamiento = Some(new CComportamiento())
        }
        
        val panelComportamiento = new PanelComportamiento()
        panelComportamiento.setSolicitarActualizarComportamiento(() => {
          val analisis = cComportamiento.get.analizarComportamiento(user)
          panelComportamiento.mostrarComportamiento(analisis)
        })
        
        cambiarVistaCentral(panelComportamiento)
        vista.foreach(_.setTituloVentana(s"$nombrePrograma - Análisis de Comportamiento"))
      }
    }

    def mostrarVistaProyecciones(): Unit = {
      vista.foreach(_.mostrarBarraMenuToolbar())
      usuario.foreach { user =>
        if (cProyecciones.isEmpty) {
          cProyecciones = Some(new CProyecciones())
        }
        
        val analisis = cProyecciones.get.generarProyecciones(user)
        
        val panelProyecciones = new PanelProyecciones()
        panelProyecciones.setSolicitarActualizarProyecciones(() => {
          val nuevoAnalisis = cProyecciones.get.generarProyecciones(user)
          panelProyecciones.mostrarProyecciones(nuevoAnalisis)
        })
        panelProyecciones.mostrarProyecciones(analisis)
        
        cambiarVistaCentral(panelProyecciones)
        vista.foreach(_.setTituloVentana(s"$nombrePrograma - Proyecciones Financieras"))
      }
    }

    def mostrarVistaComparaciones(): Unit = {
      vista.foreach(_.mostrarBarraMenuToolbar())
      usuario.foreach { user =>
        if (cComparaciones.isEmpty) {
          cComparaciones = Some(new CComparaciones())
        }
        
        val analisis = cComparaciones.get.generarComparaciones(user)
        
        val panelComparaciones = new PanelComparaciones()
        panelComparaciones.setSolicitarActualizarComparaciones(() => {
          val nuevoAnalisis = cComparaciones.get.generarComparaciones(user)
          panelComparaciones.mostrarComparaciones(nuevoAnalisis)
        })
        panelComparaciones.mostrarComparaciones(analisis)
        
        cambiarVistaCentral(panelComparaciones)
        vista.foreach(_.setTituloVentana(s"$nombrePrograma - Comparaciones Temporales"))
      }
    }

    def mostrarVistaInsights(): Unit = {
      vista.foreach(_.mostrarBarraMenuToolbar())
      usuario.foreach { user =>
        if (cInsights.isEmpty) {
          cInsights = Some(new CInsights())
        }
        
        val analisis = cInsights.get.generarInsights(user)
        
        val panelInsights = new PanelInsights()
        panelInsights.setSolicitarActualizarInsights(() => {
          val nuevoAnalisis = cInsights.get.generarInsights(user)
          panelInsights.mostrarInsights(nuevoAnalisis)
        })
        panelInsights.mostrarInsights(analisis)
        
        cambiarVistaCentral(panelInsights)
        vista.foreach(_.setTituloVentana(s"$nombrePrograma - Insights Personalizados"))
      }
    }

  private def actualizarControladores(usuarioNuevo: Usuario): Unit = {
    usuario = Some(usuarioNuevo)
    cMetas.foreach(_.actualizarUsuario(usuarioNuevo))
    cPresupuestos.foreach(_.actualizarUsuario(usuarioNuevo))
    cTransacciones.foreach(_.actualizarUsuario(usuarioNuevo))
    cReportes.foreach(_.actualizarUsuario(usuarioNuevo))
  }

  private def inicializarOActualizar[T](opcion: Option[T], crear: => T, actualizar: T => Unit): Option[T] = {
    if (opcion.isEmpty) {
      Some(crear)
    } else {
      opcion.foreach(actualizar)
      opcion
    }
  }
}

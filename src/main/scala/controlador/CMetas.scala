package controlador

import modelo.entidades.{Meta, Usuario}
import modelo.servicios.{GestorDatos, Utilidades}
import vista.componentes.paneles.PanelMetas

import java.time.LocalDate
import javax.swing.JOptionPane

class CMetas(private val panelMetas: PanelMetas, private var usuario: Usuario) {

  private def finalizarOperacionMeta(mensaje: String): Unit = {
    GestorDatos.guardarMetas(usuario.metas)
    GestorDatos.guardarUsuario(usuario)
    panelMetas.mostrarMetas(usuario.metas, usuario.movimientos)
    Utilidades.mostrarExito(mensaje)
  }

  def agregarMeta(nombre: String, descripcion: String, montoObjetivo: Double, fechaLimite: LocalDate): Boolean = {
    try {
      if (!Utilidades.validarCampoNoVacio(nombre, "El nombre de la meta no puede estar vacío")) return false
      if (!Utilidades.validarMontoPositivo(montoObjetivo, "El monto objetivo debe ser mayor a 0")) return false
      if (!Utilidades.validarFechaNoPasada(fechaLimite, "La fecha límite no puede ser anterior a hoy")) return false


      val nuevaMeta = Meta(
        nombre = nombre.trim,
        montoObjetivo = montoObjetivo,
        fechaLimite = fechaLimite,
        descripcion = descripcion.trim,
        fechaInicio = LocalDate.now(),
        estaActivo = true,
        id = GestorDatos.obtenerSiguienteIdMeta(),
        movimientos = Set.empty[Int]
      )


      val metasActualizadas = nuevaMeta :: usuario.metas
      usuario = usuario.copy(metas = metasActualizadas)


      finalizarOperacionMeta("Meta creada exitosamente")
      true
    } catch {
      case e: Exception =>
        Utilidades.mostrarError(s"Error al crear la meta: ${e.getMessage}")
        false
    }
  }

  def editarMeta(metaId: Int, nombre: String, descripcion: String, montoObjetivo: Double, fechaLimite: LocalDate): Boolean = {
    try {

      if (nombre.trim.isEmpty) {
        JOptionPane.showMessageDialog(null, "El nombre de la meta no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE)
        return false
      }
      
      if (montoObjetivo <= 0) {
        JOptionPane.showMessageDialog(null, "El monto objetivo debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE)
        return false
      }
      
      if (fechaLimite.isBefore(LocalDate.now())) {
        JOptionPane.showMessageDialog(null, "La fecha límite no puede ser anterior a hoy", "Error", JOptionPane.ERROR_MESSAGE)
        return false
      }


      val metaIndex = usuario.metas.indexWhere(_.id == metaId)
      if (metaIndex == -1) {
        JOptionPane.showMessageDialog(null, "Meta no encontrada", "Error", JOptionPane.ERROR_MESSAGE)
        return false
      }

      val metaActual = usuario.metas(metaIndex)
      

      val metaActualizada = metaActual.copy(
        nombre = nombre.trim,
        descripcion = descripcion.trim,
        montoObjetivo = montoObjetivo,
        fechaLimite = fechaLimite
      )


      val metasActualizadas = usuario.metas.updated(metaIndex, metaActualizada)
      usuario = usuario.copy(metas = metasActualizadas)


      finalizarOperacionMeta("Meta actualizada exitosamente")
      true
    } catch {
      case e: Exception =>
        JOptionPane.showMessageDialog(null, s"Error al actualizar la meta: ${e.getMessage}", "Error", JOptionPane.ERROR_MESSAGE)
        false
    }
  }

  def eliminarMeta(metaId: Int): Boolean = {
    try {
      val respuesta = JOptionPane.showConfirmDialog(
        null,
        "¿Estás seguro de que quieres eliminar esta meta?",
        "Confirmar eliminación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
      )

      if (respuesta == JOptionPane.YES_OPTION) {

        val metasActualizadas = usuario.metas.filter(_.id != metaId)
        usuario = usuario.copy(metas = metasActualizadas)


        finalizarOperacionMeta("Meta eliminada exitosamente")
        true
      } else {
        false
      }
    } catch {
      case e: Exception =>
        JOptionPane.showMessageDialog(null, s"Error al eliminar la meta: ${e.getMessage}", "Error", JOptionPane.ERROR_MESSAGE)
        false
    }
  }

  def obtenerUsuario: Usuario = usuario

  def actualizarUsuario(nuevoUsuario: Usuario): Unit = {
    usuario = nuevoUsuario
    panelMetas.mostrarMetas(usuario.metas, usuario.movimientos)
  }
} 
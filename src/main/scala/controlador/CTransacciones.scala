package controlador

import modelo.entidades.{Movimiento, Usuario}
import modelo.servicios.{GestorDatos, Utilidades}
import vista.componentes.paneles.PanelTransacciones

import java.time.LocalDate
import javax.swing.JOptionPane

class CTransacciones(private val panelTransacciones: PanelTransacciones, private var usuario: Usuario) {

  def agregarTransaccion(monto: Double, descripcion: String, categoria: Option[String], notas: String, fecha: LocalDate): Boolean = {
    try {

      if (monto == 0) {
        Utilidades.mostrarError("El monto no puede ser 0")
        return false
      }
      
      if (!Utilidades.validarCampoNoVacio(descripcion, "La descripción no puede estar vacía")) return false
      
      if (categoria.isEmpty || !Utilidades.validarCampoNoVacio(categoria.get, "Debe seleccionar una categoría")) return false


      val nuevoMovimiento = Movimiento(
        monto = monto,
        descripcion = descripcion.trim,
        fechaTransaccion = fecha,
        notas = notas.trim,
        categoria = categoria,
        idRelacionado = None,
        id = GestorDatos.obtenerSiguienteIdMovimiento(),
        estado = "Activo"
      )


      val movimientosActualizados = nuevoMovimiento :: usuario.movimientos
      usuario = usuario.copy(movimientos = movimientosActualizados)


      GestorDatos.guardarMovimientos(usuario.movimientos)
      actualizarUsuarioEnListaYGuardar()


      categoria.foreach(cat => verificarPresupuestos(cat, Math.abs(monto)))


      panelTransacciones.mostrarTransacciones(usuario.movimientos)

      val tipoTransaccion = if (monto > 0) "ingreso" else "gasto"
      Utilidades.mostrarExito(s"$tipoTransaccion registrado exitosamente")
      true
    } catch {
      case e: Exception =>
        Utilidades.mostrarError(s"Error al registrar la transacción: ${e.getMessage}")
        false
    }
  }

  private def verificarPresupuestos(categoria: String, monto: Double): Unit = {
    val presupuestosActivos = usuario.presupuestos.filter(_.estaActivo)
    
    presupuestosActivos.foreach { presupuesto =>
      if (presupuesto.categorias.getOrElse(categoria, false)) {
        val gastoActual = Utilidades.calcularGastoPresupuesto(usuario.movimientos, presupuesto)
        val nuevoGasto = gastoActual + monto
        val porcentajeUsado = (nuevoGasto / presupuesto.limite) * 100

        if (porcentajeUsado >= 90 && presupuesto.notificarUsuario) {
          JOptionPane.showMessageDialog(
            null,
            s"¡Atención! Con esta transacción alcanzarás el ${porcentajeUsado.toInt}% de tu presupuesto '${presupuesto.nombre}'",
            "Límite de Presupuesto",
            JOptionPane.WARNING_MESSAGE
          )
        }
      }
    }
  }

  private def actualizarUsuarioEnListaYGuardar(): Unit = {
    val usuarios = GestorDatos.cargarUsuarios()
    val usuariosActualizados = usuarios.map(u => if (u.id == usuario.id) usuario else u)
    GestorDatos.guardarUsuarios(usuariosActualizados)
  }

  private def finalizarOperacionTransaccion(mensaje: String): Unit = {
    GestorDatos.guardarMovimientos(usuario.movimientos)
    actualizarUsuarioEnListaYGuardar()
    panelTransacciones.mostrarTransacciones(usuario.movimientos)
    JOptionPane.showMessageDialog(null, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE)
  }

  def eliminarTransaccion(movimientoId: String): Boolean = {
    try {
      val respuesta = JOptionPane.showConfirmDialog(
        null,
        "¿Estás seguro de que quieres eliminar esta transacción?",
        "Confirmar eliminación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
      )

      if (respuesta == JOptionPane.YES_OPTION) {

        val movimientosActualizados = usuario.movimientos.filter(_.id != movimientoId.toInt)
        usuario = usuario.copy(movimientos = movimientosActualizados)


        finalizarOperacionTransaccion("Transacción eliminada exitosamente")
        true
      } else {
        false
      }
    } catch {
      case e: Exception =>
        JOptionPane.showMessageDialog(null, s"Error al eliminar la transacción: ${e.getMessage}", "Error", JOptionPane.ERROR_MESSAGE)
        false
    }
  }

  def editarTransaccion(id: Int, monto: Double, descripcion: String, categoria: Option[String], notas: String, fecha: LocalDate): Boolean = {
    try {

      if (monto == 0) {
        JOptionPane.showMessageDialog(null, "El monto no puede ser 0", "Error", JOptionPane.ERROR_MESSAGE)
        return false
      }
      
      if (descripcion.trim.isEmpty) {
        JOptionPane.showMessageDialog(null, "La descripción no puede estar vacía", "Error", JOptionPane.ERROR_MESSAGE)
        return false
      }
      
      if (categoria.isEmpty || categoria.get.trim.isEmpty) {
        JOptionPane.showMessageDialog(null, "Debe seleccionar una categoría", "Error", JOptionPane.ERROR_MESSAGE)
        return false
      }


      val transaccionIndex = usuario.movimientos.indexWhere(_.id == id)
      if (transaccionIndex == -1) {
        JOptionPane.showMessageDialog(null, "No se encontró la transacción a editar", "Error", JOptionPane.ERROR_MESSAGE)
        return false
      }


      val transaccionOriginal = usuario.movimientos(transaccionIndex)
      val transaccionActualizada = transaccionOriginal.copy(
        monto = monto,
        descripcion = descripcion.trim,
        fechaTransaccion = fecha,
        notas = notas.trim,
        categoria = categoria
      )


      val movimientosActualizados = usuario.movimientos.updated(transaccionIndex, transaccionActualizada)
      usuario = usuario.copy(movimientos = movimientosActualizados)


      finalizarOperacionTransaccion("Transacción editada exitosamente")
      true
    } catch {
      case e: Exception =>
        JOptionPane.showMessageDialog(null, s"Error al editar la transacción: ${e.getMessage}", "Error", JOptionPane.ERROR_MESSAGE)
        false
    }
  }

  def obtenerUsuario: Usuario = usuario

  def actualizarUsuario(nuevoUsuario: Usuario): Unit = {
    usuario = nuevoUsuario
    panelTransacciones.mostrarTransacciones(usuario.movimientos)
  }
} 
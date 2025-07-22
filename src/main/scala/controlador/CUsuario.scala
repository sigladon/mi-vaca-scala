package controlador

import modelo.entidades.Categoria
import modelo.servicios.GestorDatos
import vista.ventanas.{LoginUI, RegistrarseUI}

import java.awt.Color
import java.awt.event.ActionEvent
import javax.swing.JTextField
import javax.swing.event.{DocumentEvent, DocumentListener}
import scala.util.matching.Regex

class CUsuario(private var vista: Either[LoginUI, RegistrarseUI]) {

  private var usernameValido = false
  private var nombreValido = false
  private var correoValido = false
  private var contraseniaValida = false
  private var contraseniaRepetidaValida = false


  private val colorError = new Color(255, 238, 238)
  private val colorNormal = Color.WHITE


  cambiarVista(vista)

  def cambiarVista(nuevaVista: Either[LoginUI, RegistrarseUI]): Unit = {
    this.vista = nuevaVista

    vista match {
      case Left(loginVista) =>
        configurarLogin(loginVista)
      case Right(registroVista) =>
        configurarRegistro(registroVista)
    }
  }

  private def configurarLogin(loginVista: LoginUI): Unit = {
    loginVista.getBtnIniciarSesion.addActionListener((_: ActionEvent) => iniciarSesion())

    loginVista.getBtnRegistrarseUI.addActionListener((_: ActionEvent) => loginVista.emitirSolicitarMostrarRegistro())
  }

  private def configurarRegistro(registroVista: RegistrarseUI): Unit = {

    registroVista.getTxtUsername.getDocument.addDocumentListener(createDocumentListener(() => verificarUsernameRegistro()))
    registroVista.getTxtNombre.getDocument.addDocumentListener(createDocumentListener(() => verificarNombreRegistro()))
    registroVista.getTxtCorreo.getDocument.addDocumentListener(createDocumentListener(() => verificarCorreoRegistro()))
    registroVista.getTxtContrasenia.getDocument.addDocumentListener(createDocumentListener(() => verificarContraseniaRegistro()))
    registroVista.getTxtRepetirContrasenia.getDocument.addDocumentListener(createDocumentListener(() => verificarContraseniaRepetida()))


    registroVista.getBtnRegistrarseUI.addActionListener((_: ActionEvent) => registrarse())

    registroVista.getBtnVolver.addActionListener((_: ActionEvent) => volverAlLogin())
  }

  private def createDocumentListener(callback: () => Unit): DocumentListener = {
    new DocumentListener {
      override def insertUpdate(e: DocumentEvent): Unit = callback()
      override def removeUpdate(e: DocumentEvent): Unit = callback()
      override def changedUpdate(e: DocumentEvent): Unit = callback()
    }
  }

  private def verificarUsernameRegistro(): Unit = {
    vista match {
      case Right(registroVista) =>
        val txtUsername = registroVista.getTxtUsername
        val username = txtUsername.getText

        if (username.isEmpty) {
          resetearEstiloCampo(txtUsername)
          usernameValido = false
        } else if (!validarUsername(username)) {
          println("El nombre de usuario ingresado no es válido")
          marcarCampoError(txtUsername)
          usernameValido = false
        } else {
          resetearEstiloCampo(txtUsername)
          usernameValido = true
        }
      case _ =>
    }
  }

  private def verificarNombreRegistro(): Unit = {
    vista match {
      case Right(registroVista) =>
        val txtNombre = registroVista.getTxtNombre
        val nombre = txtNombre.getText

        if (nombre.isEmpty) {
          println("El nombre no puede estar vacío")
          resetearEstiloCampo(txtNombre)
          nombreValido = false
        } else if (!validarNombre(nombre)) {
          println("El nombre ingresado no es válido")
          marcarCampoError(txtNombre)
          nombreValido = false
        } else {
          resetearEstiloCampo(txtNombre)
          nombreValido = true
        }
      case _ =>
    }
  }

  private def verificarCorreoRegistro(): Unit = {
    vista match {
      case Right(registroVista) =>
        val txtCorreo = registroVista.getTxtCorreo
        val correo = txtCorreo.getText

        if (!validarCorreo(correo)) {
          println("El correo proporcionado no es válido")
          marcarCampoError(txtCorreo)
          correoValido = false
        } else {
          resetearEstiloCampo(txtCorreo)
          correoValido = true
        }
      case _ =>
    }
  }

  private def verificarContraseniaRegistro(): Unit = {
    vista match {
      case Right(registroVista) =>
        val txtContrasenia = registroVista.getTxtContrasenia
        val contrasenia = new String(txtContrasenia.getPassword)

        if (!validarContrasenia(contrasenia)) {
          println("La contraseña no cumple con las condiciones")
          marcarCampoError(txtContrasenia)
          contraseniaValida = false
        } else {
          resetearEstiloCampo(txtContrasenia)
          contraseniaValida = true
        }


        if (registroVista.getTxtRepetirContrasenia.getPassword.nonEmpty) {
          verificarContraseniaRepetida()
        }
      case _ =>
    }
  }

  private def verificarContraseniaRepetida(): Unit = {
    vista match {
      case Right(registroVista) =>
        val txtContrasenia = registroVista.getTxtContrasenia
        val txtContraseniaRepetida = registroVista.getTxtRepetirContrasenia
        val contrasenia = new String(txtContrasenia.getPassword)
        val contraseniaRepetida = new String(txtContraseniaRepetida.getPassword)

        if (contrasenia != contraseniaRepetida) {
          println("Las contraseñas no son las mismas")
          marcarCampoError(txtContraseniaRepetida)
          contraseniaRepetidaValida = false
        } else {
          resetearEstiloCampo(txtContraseniaRepetida)
          contraseniaRepetidaValida = true
        }
      case _ =>
    }
  }

  private def iniciarSesion(): Unit = {
    vista match {
      case Left(loginVista) =>
        val txtIdentificacion = loginVista.getTxtCorreo
        val identificacion = txtIdentificacion.getText
        val txtContrasenia = loginVista.getTxtContrasenia
        val contrasenia = new String(txtContrasenia.getPassword)

        println(s"Intentando iniciar sesión con: $identificacion")

        if (!(validarCorreo(identificacion) || validarUsername(identificacion))) {
          println("Nombre de usuario/Correo electrónico no válido")
          marcarCampoError(txtIdentificacion)
          return
        }

        try {
          GestorDatos.buscarUsuarioPorIdentificacion(identificacion) match {
            case Some(usuarioData) =>
              val idUsuario = usuarioData.id
              println(s"ID de usuario encontrado: $idUsuario")
              if (!usuarioData.verificarContrasenia(contrasenia)) {
                println("La contraseña no es válida, intenta de nuevo")
                marcarCampoError(txtContrasenia)
                return
              }
              resetearEstiloCampo(txtIdentificacion)
              resetearEstiloCampo(txtContrasenia)
              println(s"Ingresó el usuario ${usuarioData.nombre}")
              val token = modelo.entidades.Token(idUsuario.toString, idUsuario)
              GestorDatos.guardarToken(token)
              loginVista.emitirSolicitarMostrarBienvenida()
            case None =>
              println(s"No se encontró ningún usuario vinculado a: $identificacion")
              marcarCampoError(txtIdentificacion)
          }
        } catch {
          case e: Exception =>
            println(s"Error al cargar usuario: ${e.getMessage}")
            marcarCampoError(txtIdentificacion)
        }
      case _ =>
    }
  }

  private def registrarse(): Unit = {
    vista match {
      case Right(registroVista) =>
        if (!(usernameValido && nombreValido && correoValido && contraseniaValida && contraseniaRepetidaValida)) {
          println("Hay campos inválidos en el formulario")
          return
        }

        val nombre = registroVista.getTxtNombre.getText
        val username = registroVista.getTxtUsername.getText
        val correo = registroVista.getTxtCorreo.getText
        val contrasenia = new String(registroVista.getTxtContrasenia.getPassword)
        val idUsuario = GestorDatos.obtenerSiguienteIdUsuario()

        println(s"Registrando usuario: $username con ID: $idUsuario")


        val categoriasPorDefecto = List(
          Categoria("Alimentación", GestorDatos.obtenerSiguienteIdCategoria()),
          Categoria("Transporte", GestorDatos.obtenerSiguienteIdCategoria()),
          Categoria("Entretenimiento", GestorDatos.obtenerSiguienteIdCategoria()),
          Categoria("Servicios", GestorDatos.obtenerSiguienteIdCategoria()),
          Categoria("Salud", GestorDatos.obtenerSiguienteIdCategoria()),
          Categoria("Educación", GestorDatos.obtenerSiguienteIdCategoria()),
          Categoria("Ropa", GestorDatos.obtenerSiguienteIdCategoria()),
          Categoria("Otros", GestorDatos.obtenerSiguienteIdCategoria())
        )
        

        val anioActual = java.time.LocalDate.now().getYear
        val presupuestoPorDefecto = modelo.entidades.Presupuesto(
          nombre = "Presupuesto Anual",
          limite = 12000.0,
          inicioPresupuesto = java.time.LocalDate.of(anioActual, 1, 1),
          finPresupuesto = java.time.LocalDate.of(anioActual, 12, 31),
          notificarUsuario = true,
          categorias = categoriasPorDefecto.map(cat => cat.nombre -> true).toMap,
          id = GestorDatos.obtenerSiguienteIdPresupuesto(),
          estaActivo = true,
          Notas = "Presupuesto anual por defecto",
          Movimientos = Set.empty[Int]
        )
        
        val nuevoUsuario = modelo.entidades.Usuario(
          username = username,
          contrasenia = contrasenia,
          nombre = nombre,
          id = idUsuario,
          correos = List(correo),
          movimientos = List(),
          presupuestos = List(presupuestoPorDefecto),
          metas = List(),
          categorias = categoriasPorDefecto
        )



        GestorDatos.agregarUsuario(nuevoUsuario)
        GestorDatos.guardarPresupuestos(nuevoUsuario.presupuestos)
        GestorDatos.guardarCategorias(nuevoUsuario.categorias)

        println("Se registró el usuario correctamente")
        
        volverAlLogin()
      case _ =>
    }
  }

  private def volverAlLogin(): Unit = {
    vista match {
      case Right(registroVista) =>
        registroVista.emitirSolicitarMostrarLogin()
      case _ =>
    }
  }

  private def marcarCampoError(campo: JTextField): Unit = {
    campo.setBackground(colorError)
  }

  private def resetearEstiloCampo(campo: JTextField): Unit = {
    campo.setBackground(colorNormal)
  }


  private def validarCorreo(correo: String): Boolean = {
    val regexCorreo: Regex = """^[\w.%+-]+@[\w.-]+(\.[a-zA-Z]{2,})+$""".r
    regexCorreo.findFirstIn(correo).isDefined
  }

  private def validarUsername(username: String): Boolean = {
    val regexUsername: Regex = """^[A-Za-z0-9_]{3,20}$""".r
    regexUsername.findFirstIn(username).isDefined
  }

  private def validarContrasenia(contrasenia: String): Boolean = {
    val regexContrasenia: Regex = """^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z\d])[A-Za-z\d\S]{8,}$""".r
    regexContrasenia.findFirstIn(contrasenia).isDefined
  }

  private def validarNombre(nombre: String): Boolean = {
    val regexNombre: Regex = """^[A-Za-zÁÉÍÓÚáéíóúÑñ' ]{2,50}$""".r
    regexNombre.findFirstIn(nombre).isDefined
  }
}

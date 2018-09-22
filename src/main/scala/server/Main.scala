package server

import java.io.{DataInputStream, DataOutputStream, ObjectInputStream, ObjectOutputStream}
import java.net.ServerSocket

import client.Request
import client.actions.Actions
import server.multimedia.states.States
import server.multimedia.{Light, Radio, TV}

object Main extends App {
  val light = Light()
  val tv = TV()
  val radio = Radio()
  val server = new ServerSocket(9999)
  while (true) {
    val socket = server.accept()
    val oos = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()))
    val ois = new ObjectInputStream(new DataInputStream(socket.getInputStream()))
    val request = ois.readObject().asInstanceOf[Request]
    println(request)
    request.action match {
      case Actions.getLight => {
        oos.writeObject(light)
      }
      case Actions.getRadio => {
        oos.writeObject(radio)
      }
      case Actions.getTV => {
        oos.writeObject(tv)
      }
      case Actions.TurnOn => {
        request.obj.get match {
          case _: Light =>
            light.state = States.On
            oos.writeObject(light)
          case _: Radio =>
            radio.state = States.On
            oos.writeObject(radio)
          case _: TV =>
            tv.state = States.On
            oos.writeObject(tv)
          case _ =>
            light.state = States.On
            oos.writeObject(light)
        }
      }
      case Actions.TurnOff => {
        request.obj.get match {
          case _: Light =>
            light.state = States.Off
            oos.writeObject(light)
          case _: Radio =>
            radio.state = States.Off
            oos.writeObject(radio)
          case _: TV =>
            tv.state = States.Off
            oos.writeObject(tv)
          case _ =>
            light.state = States.Off
            oos.writeObject(light)
        }
      }
      case Actions.ChangeVolume => {
        request.obj.get match {
          case _: Radio =>
            radio.volume = request.vol.get
            oos.writeObject(radio)
          case _: TV =>
            tv.volume = request.vol.get
            oos.writeObject(tv)
          case _ =>
            oos.writeObject(light)
        }
      }
      case _ => {
        oos.writeObject(light)
      }
    }
    oos.flush()
    socket.close()
  }
}

package client

import java.io.{DataInputStream, DataOutputStream, ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import scalafx.Includes._
import client.actions.Actions
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ComboBox, Slider, TextField}
import scalafx.scene.image.{Image, ImageView}
import server.multimedia.states.States
import server.multimedia.{Equipment, Light, Radio, TV}

object Main extends JFXApp {
  var current: Int = 0
  val itemList = Array(request(Actions.getLight), request(Actions.getRadio), request(Actions.getTV))
  stage = new PrimaryStage {
    title = "Multimedia Center"
    scene = new Scene(500, 250) {
      val comboBox = new ComboBox(itemList.map(_.name))
      comboBox.value = itemList.head.name
      comboBox.layoutX = 200
      comboBox.prefWidth = 100
      val imageView = new ImageView(new Image(itemList(current).imgPath))
      imageView.layoutX = 50
      imageView.layoutY = 65
      val volumeSlider = new Slider(0,100,0)
      volumeSlider.layoutX = 320
      volumeSlider.layoutY = 130
      volumeSlider.disable = true
      volumeSlider.visible = false
      val textField1 = new TextField()
      textField1.layoutX = 250
      textField1.layoutY = 130
      textField1.text = volumeSlider.value.toInt.toString()
      textField1.editable = false
      textField1.visible = false
      textField1.prefWidth = 50
      val onOffButton = new Button("Turn on")
      onOffButton.layoutX = 345
      onOffButton.layoutY = 80
      val textField2 = new TextField()
      textField2.layoutX = 250
      textField2.layoutY = 80
      textField2.prefWidth = 50
      textField2.text = "Off"
      textField2.editable = false

      onOffButton.onAction = () => {
        if(onOffButton.text() == "Turn on"){
          onOffButton.text = "Turn off"
          itemList(current) = request(Actions.TurnOn,Option(itemList(current)))
        } else {
          onOffButton.text = "Turn on"
          itemList(current) = request(Actions.TurnOff,Option(itemList(current)))
        }
        updateCurrentState(imageView, volumeSlider, textField1, onOffButton, textField2)
      }

      volumeSlider.value.onChange {
        val parsedToIntVal = volumeSlider.value().toInt
        volumeSlider.value = parsedToIntVal
        itemList(current) = request(Actions.ChangeVolume,Option(itemList(current)),Option(parsedToIntVal))
        updateCurrentState(imageView, volumeSlider, textField1, onOffButton, textField2)
      }

      comboBox.onAction = () => {
        current = itemList.indexWhere(_.name == comboBox.value())
        updateCurrentState(imageView, volumeSlider, textField1, onOffButton, textField2)
      }

      content = List(comboBox, imageView, volumeSlider, onOffButton, textField1, textField2)
    }
  }

  def request(action: Actions.Value, obj: Option[Equipment] = None, vol: Option[Int] = None): Equipment = {
    val socket = new Socket(InetAddress.getByName("localhost"), 9999)
    val request = Request(action,obj,vol)
    val ois = new ObjectInputStream(new DataInputStream(socket.getInputStream()))
    val oos = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()))
    oos.writeObject(request)
    oos.flush()
    val changedElement = ois.readObject().asInstanceOf[Equipment]
    socket.close()
    changedElement
  }

  def updateCurrentState(imgView: ImageView, volumeSlider: Slider, textField1: TextField, onOffButton: Button, textField2: TextField): Unit = {
    imgView.image = new Image(itemList(current).imgPath)
    volumeSlider.value = itemList(current) match {
      case _: Radio =>
        volumeSlider.disable = false
        volumeSlider.visible = true
        textField1.visible = true
        itemList(current).volume
      case _: TV =>
        volumeSlider.disable = false
        volumeSlider.visible = true
        textField1.visible = true
        itemList(current).volume
      case _ =>
        textField1.visible = false
        volumeSlider.disable = true
        volumeSlider.visible = false
        0
    }
    textField1.text = volumeSlider.value().toInt.toString()
    onOffButton.text = itemList(current).state match {
      case States.On => "Turn off"
      case States.Off => "Turn on"
    }
    textField2.text = itemList(current).state match {
      case States.On => "On"
      case States.Off => "Off"
    }
  }
}

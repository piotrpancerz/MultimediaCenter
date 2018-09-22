package server.multimedia

import server.multimedia.states.States

case class Radio() extends Equipment {
  val name = "Radio"
  val imgPath: String = "file:lib/radio.png"
  var state: States.Value = States.Off
  var volume: Int = 0
}

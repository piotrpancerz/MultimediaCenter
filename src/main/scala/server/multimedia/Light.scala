package server.multimedia

import server.multimedia.states.States

case class Light() extends Equipment {
  val name = "Light"
  val imgPath: String = "file:lib/light.png"
  var state: States.Value = States.Off
  var volume: Int = 0
}

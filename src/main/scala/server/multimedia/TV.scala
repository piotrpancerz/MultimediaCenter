package server.multimedia

import server.multimedia.states.States

case class TV() extends Equipment {
  val name = "TV"
  val imgPath: String = "file:lib/tv.png"
  var state: States.Value = States.Off
  var volume: Int = 0
}

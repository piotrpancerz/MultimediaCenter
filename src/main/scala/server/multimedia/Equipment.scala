package server.multimedia

import server.multimedia.states.States

abstract class Equipment {
  val name: String
  val imgPath: String
  var state: States.Value
  var volume: Int
}

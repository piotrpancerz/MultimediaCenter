package client

import client.actions.Actions
import server.multimedia.Equipment

case class Request(_action: Actions.Value, _obj: Option[Equipment] = None, _vol: Option[Int] = None){
  def action: Actions.Value = _action
  def obj: Option[Equipment] = _obj
  def vol: Option[Int] = _vol
}

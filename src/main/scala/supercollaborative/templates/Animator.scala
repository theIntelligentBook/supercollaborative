package supercollaborative.templates

import com.wbillingsley.veautiful.html._
import org.scalajs.dom

class Animator[T <: VHtmlNode](node:T)(f: (Double, Double, Double) => Boolean) {

  def playing = startTime.nonEmpty
  var startTime:Option[Double] = None
  var lastTime:Option[Double] = None

  val callback:(Double => Unit) = (ts) => {
    for t0 <- startTime do
      val elapsed = ts - t0
      val dt = ts - lastTime.getOrElse(t0)

      if f(ts, elapsed, dt) then lastTime = Some(ts)

      if (node.isAttached) 
        dom.window.requestAnimationFrame(callback)
      else
        startTime = None
  }

  def start() = {
    startTime = Some(dom.window.performance.now())
    dom.window.requestAnimationFrame(callback)
  }

  def stop() = 
    startTime = None

}
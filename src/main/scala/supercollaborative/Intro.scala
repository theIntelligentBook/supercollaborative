package supercollaborative

import com.wbillingsley.veautiful.html.{<, ^}

val frontPage = <.div(^.cls := "front-page",
  <.div(
    <.h1("Supercollaborative Build: How teams of teams build software together")
  ),
  <.div(^.cls := "lead",
    Common.marked(
      """
        |This is the beginnings of an intelligent (interactive) book on collaborative software engineering.
        |""".stripMargin
    ),
  ),
  Seq(
  )
)

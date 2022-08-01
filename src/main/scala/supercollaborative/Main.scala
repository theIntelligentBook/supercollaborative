package supercollaborative

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.doctacular._
import Medium._
import org.scalajs.dom

import Common._
import templates.{markdownDeck, markdownPage, markdownChapterPage, Example, OutsideExample, GitTutorial}
import templates.given

val site = Site()


object Main {

  val scaleChallengesToWindow:Boolean = {
    !dom.window.location.search.contains("scale=off")
  }

  def main(args:Array[String]): Unit = {
    val n = dom.document.getElementById("render-here")
    n.innerHTML = ""

    Styles.installStyles()

    import site.given 
        
    site.toc = site.Toc(
      "Home" -> site.HomeRoute,
      
      "1. Version Control" -> versioncontrol.toc,

      "2. Containers and Virtual Machines" -> containers.toc,

      "3. Build Systems and Testing" -> buildsystems.toc,

      "4. Continuous Development" -> continuousdevelopment.toc,

      "5. System Architecture" -> softwarearchitecture.toc,

      "6. Models and Diagrams" -> site.Toc(

      ),

      "7. Logging and Debugging" -> site.Toc(

      ),

      "8. Deployment and Ops" -> site.Toc(

      ),

      "9. Social Implications" -> site.Toc(

      ),

      "10. User Centred Design" -> site.Toc(

      ),
    )
    
    site.home = () => site.renderPage(frontPage)
    site.attachTo(n)

  }

}

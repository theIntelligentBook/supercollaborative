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

      "3. Build Systems" -> site.Toc(

      ),

      "4. Testing" -> site.Toc(

      ),

      "5. Continuous Development" -> site.Toc(

      ),

      "6. System Architecture" -> site.Toc(

      ),

      "7. Models and Diagrams" -> site.Toc(

      ),

      "8. Logging and Debugging" -> site.Toc(

      ),

      "9. Deployment and Ops" -> site.Toc(

      ),

      "10. Social Implications" -> site.Toc(

      ),

      "11. User Centred Design" -> site.Toc(

      ),
    )
    
    site.home = () => site.renderPage(frontPage)
    site.attachTo(n)

  }

}

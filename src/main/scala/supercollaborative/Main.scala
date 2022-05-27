package willscala

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
      
      "1. JS, HTML, and the DOM" -> site.Toc(
        "Intro" -> site.addPage("browserAsAP", 
          markdownChapterPage(1, "JS, HTML, and the DOM", "", "markdown/browserAsPlatform.md")
        ),
        "HTML and the DOM" -> site.add("html", 
          Alternative("Slide deck", Deck(() => markdownDeck("HTML and the DOM -- a fast recap", "markdown/dom.md")))
        ),
        "ES5" -> site.Toc(
          "ECMAScript 5" -> site.add("es5", 
            Alternative("Slide deck", Deck(() => markdownDeck("ECMAScript 5 -- a fast recap", "markdown/es5.md")))
          ),
          "Prototype inheritance" -> site.add("es5oo", 
            Alternative("Slide deck", Deck(() => markdownDeck("Prototype inheritance -- how OO worked in ES5", "markdown/es5oo.md")))
          ),
        ),
        "Modern ES" -> site.Toc(
          
        ),
        "Tutorial: Game of Life in the DOM" -> site.addPage("tutorial1", GitTutorial(
          "https://gitlab.une.edu.au/cosc360in2018/tutorial-conway-life-t1", 
          "Tutorial: Conway's Game of Life using the DOM API"
        )),
        "Resources" -> site.addPage("resources1", markdownPage("markdown/jsAndDom/resources.md")) 
      ),

      "2. Graphics" -> site.Toc(
        "Intro" -> site.addPage("graphics",
          markdownChapterPage(2, "2D Graphics in the Browser", "", "markdown/graphics.md")
        ),
        "Canvas" -> site.Toc(
          "Slides & video" -> site.add("canvas", 
            Alternative("Slide deck", Deck(() => markdownDeck("Canvas", "markdown/canvas.md")))
          ),
          "Example: mouse trails" -> site.addPage("mousetrails", 
            Example("examples/canvas/", "mousetrails.html", Seq("mousetrails.html", "canvasexample.js"))
          ),
          "Example: compositing" -> site.addPage("compositing", 
            Example("examples/canvas/", "composite.html", Seq("composite.html", "compositeexample.js"))
          ),
        ),
        "SVG" -> site.Toc(
          "Slides & video" -> site.add("svg", 
            Alternative("Slide deck", Deck(() => markdownDeck("Scalable Vector Graphics (SVG)", "markdown/svg.md")))
          ),
          "Example: 7 segment display" -> site.addPage("7seg", 
            Example("examples/7seg/", "index.html", Seq("index.html", "segments.js"))
          ),
        ),
        "D3" -> site.Toc(
          "Slides & video" -> site.add("d3", 
            Alternative("Slide deck", Deck(() => markdownDeck("D3: Data Driven Documents", "markdown/d3.md")))
          ),
          "Example: rolling sine wave" -> site.addPage("sinewave", 
            Example("examples/sinewave/", "index.html", Seq("index.html", "d3demo.js"))
          ),
        ),
        "CSS and Pre-processors" -> site.add("css", 
          Alternative("Slide deck", Deck(() => markdownDeck("CSS, SASS, Less, and pre-processors", "markdown/css.md")))
        ),
        "Tutorial: Adding SVG and SASS" -> site.addPage("tutorial2", GitTutorial(
          "https://gitlab.une.edu.au/cosc360in2018/tutorial-conway-life-t2", 
          "Tutorial 2: Adding SVG and SASS to game of Life"
        )),
        "Resources" -> site.addPage("resources2", markdownPage("markdown/graphics/resources.md")) 

      ),

      "3. Compile to JS" -> site.Toc(
        "Intro" -> site.addPage("compileToJS",
          markdownChapterPage(2, "Compile-to-JavaScript", "", "markdown/compileToJS.md")
        ),
        "Motivation" -> site.add("compileToJSDeck", 
          Alternative("Slide deck", Deck(() => markdownDeck("Compile to JavaScript", "markdown/compileToJSDeck.md")))
        ),     
        "TypeScript" -> site.Toc(
          "Slides & video" -> site.add("tsdeck", 
            Alternative("Slide deck", Deck(() => markdownDeck("TypeScript", "markdown/typescript.md")))
          ),
          "Example: asteroids" -> site.addPage("asteroids", 
            OutsideExample("https://www.wbillingsley.com/demo-typescript-canvas-d3/", "https://github.com/wbillingsley/demo-typescript-canvas-d3")
          ),
        ),
        "Webpack" -> site.Toc(
          "Slides & video" -> site.add("webpack", 
            Alternative("Slide deck", Deck(() => markdownDeck("Webpack", "markdown/webpack.md")))
          ),
        ),
        "Tutorial: Adding TypeScript and D3" -> site.addPage("tutorial3", GitTutorial(
          "https://gitlab.une.edu.au/cosc360in2018/tutorial-conway-life-t3", 
          "Tutorial 3: Adding TypeScript and D3"
        )),
        "Tutorial: Setting up Webpack" -> site.addPage("tutorial4", GitTutorial(
          "https://gitlab.une.edu.au/cosc360in2018/tutorial-conway-life-t4", 
          "Tutorial 4: Setting up Webpack and using TypeScript modules"
        )),
      ),

      "4. Client-side" -> site.Toc(
        "Vue.js" -> site.Toc(
          "Slides & video" -> site.add("vuejs", 
            Alternative("Slide deck", Deck(() => markdownDeck("Vue.js", "markdown/vuejs.md")))
          ),
          "Example: game of life" -> site.addPage("vuejs-life", 
            Example("examples/vuejs/", "index.html", Seq("index.html", "example.js", "helloVue.html", "life.js"))
          ),
        ),
        "React.js" -> site.Toc(
          "Slides & video" -> site.add("reactjs", 
            Alternative("Slide deck", Deck(() => markdownDeck("React.js", "markdown/reactjs.md")))
          )
        ),
        "Angular" -> site.Toc(
          "Slides & video" -> site.add("angluar", 
            Alternative("Slide deck", Deck(() => markdownDeck("Angular", "markdown/angular2.md")))
          ),
        ),
        "Tutorial: Using Vue or React" -> site.addPage("tutorial5", GitTutorial(
          "https://gitlab.une.edu.au/cosc360in2018/tutorial-conway-life-t5", 
          "Tutorial 5: Rendering using Vue or React"
        )),
      ),

      "5. Server-side" -> site.Toc(
        "DNS" -> site.add("dns", 
          Alternative("Slide deck", Deck(() => markdownDeck("DNS", "markdown/dns.md")))
        ),
        "Tutorial: DNS" -> site.addPage("tutorial6", markdownPage("markdown/dns/tutorial.md")) 


      )

    )
    
    site.home = () => site.renderPage(frontPage)
    site.attachTo(n)

  }

}

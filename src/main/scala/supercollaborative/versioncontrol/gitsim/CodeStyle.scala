package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.html.*

import supercollaborative.given

object CodeStyle {
  val styling = Styling(
    """
      |font-family: 'Fira Mono', monospace;
      |""".stripMargin
  ).modifiedBy(
  ).register()

  val codeCard = Styling(
    """
      |background: #f6f6f6;
      |padding: 5px;
      |border-radius: 5px;
      |margin: 10px 0;
      |""".stripMargin
  ).register()

  val history = Styling(
    """
    |
    |""".stripMargin
  ).modifiedBy(
    //" .history" -> "display: flex; flex-direction: column",
    " .history .history-row" -> "border: none; text-align: left; background:none; width: 100%; display: grid; grid-template-columns: 180px 300px 1fr 250px; align-items: center;",
    " .history .history-row.selected" -> "background: #cdf;",
    " .history .history-row:hover" -> "background: #def;",
    " .history .history-row .hash" -> "color: #555; font-family: monospace; margin-right: 10px;",
    " .history .history-row .author" -> "width: 300px; display: inline-flex; margin-right: 10px;",
    " .history .history-row .comment" -> "display: inline-flex; margin-right: 10px;",
    " .history .history-row .date" -> "font-size: 24px;",
  ).register()

  val selectorAndViewer = Styling(
    """
      |display: flex;
      |align-items: stretch;
      |border: 1px solid #f0f0f0;
      |""".stripMargin
    ).modifiedBy(
      " .fileSelector.expander.expanded::before" -> "content: '▾'; padding-right: 5px;",
      " .fileSelector.expander.collapsed::before" -> "content: '▸'; padding-right: 5px;",
      " .fileSelector.textfile::before" -> "content: '≣'; padding-right: 5px;",
      " .fileList" -> "width: 350px; display: flex; flex-direction: column; text-align: left; border-right: 1px solid #f0f0f0; background: white; overflow-y: auto; overflow-x: hidden;",
      " .fileList .fileSelector " -> "background: none; border: none; text-align: left; padding: 0 5px; width: 100%;",
      " .fileList .fileSelector:hover " -> "background: #def;",
      " .fileList .fileSelector.selected " -> "background: #cdf;",
      " .fileViewer" -> "padding: 5px; margin: 0; flex: auto; display: flex; flex-direction: column;",
      " .fileViewer .viewer-breadcrumbs" -> "flex: 0; border-bottom: 1px solid #f0f0f0; font-size: 18px; padding: 3px; background: #fafafa;",
      " .fileViewer .viewer-breadcrumbs .viewer-breadcrumb" -> "color: #444;",
      " .fileViewer .viewer-breadcrumbs .divider" -> "color: #aaa;",
      " .fileViewer.readOnly" -> "background: #f6f6f6; padding-bottom: 0;",
  ).register()

  val lcsViewer = Styling(
    """margin: 20px;
      |
      |""".stripMargin
    ).modifiedBy(
      ".horizontal" -> "display: grid; grid-template-columns: 1fr 1fr",
      ".horizontal .boxA" -> "border: 5px solid #f0f0f0; border-top-left-radius: 15px; border-bottom-left-radius: 15px; padding: 15px;",
      ".horizontal .boxB" -> "border: 5px solid #f0f0f0; border-left: none; border-top-right-radius: 15px; border-bottom-right-radius: 15px; padding: 15px;",
      ".vertical .boxA" -> "border: 5px solid #f0f0f0; border-top-left-radius: 15px; border-top-right-radius: 15px; padding: 15px;",
      ".vertical .boxB" -> "border: 5px solid #f0f0f0; border-top: none; border-bottom-left-radius: 15px; border-bottom-right-radius: 15px; padding: 15px;",
      " .both" -> "color: #444;",
      " .left" ->  "color: #a44;",
      " .right" ->  "color: #4a4;",
  ).register()

}
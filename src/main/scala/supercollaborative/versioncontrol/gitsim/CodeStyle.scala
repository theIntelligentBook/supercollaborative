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
    " .history" -> "display: flex; flex-direction: column",
    " .history .history-row" -> "border: none; text-align: left; background:none;",
    " .history .history-row.selected" -> "background: #cdf;",
    " .history .history-row:hover" -> "background: #def;",
    " .history .history-row .hash" -> "color: #555; font-family: monospace; margin-right: 10px;",
    " .history .history-row .author" -> "width: 300px; display: inline-flex; margin-right: 10px;",
    " .history .history-row .comment" -> "display: inline-flex; margin-right: 10px;",
    " .history .history-row .date" -> "width: 200px; float: right; margin-left: -200px;",
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
      " .fileList" -> "width: 250px; display: flex; flex-direction: column; text-align: left; border-right: 1px solid #f0f0f0; background: white;",
      " .fileList .fileSelector " -> "background: none; border: none; text-align: left; padding: 0 5px; width: 100%;",
      " .fileList .fileSelector:hover " -> "background: #def;",
      " .fileList .fileSelector.selected " -> "background: #cdf;",
      " .fileViewer" -> "padding: 5px; margin: 0; flex: auto;",
      " .fileViewer.readOnly" -> "background: #f6f6f6;",
  ).register()

}
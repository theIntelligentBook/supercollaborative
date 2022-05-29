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

  val selectorAndViewer = Styling(
    """
      |display: flex;
      |align-items: stretch;
      |border: 1px solid #f0f0f0;
      |""".stripMargin
    ).modifiedBy(
      " .fileList" -> "flex: 0; display: flex; flex-direction: column; text-align: left; border-right: 1px solid #f0f0f0; background: white;",
      " .fileList .fileSelector " -> "background: none; border: none; text-align: left; padding: 0 5px;",
      " .fileList .fileSelector:hover " -> "background: #def;",
      " .fileList .fileSelector.selected " -> "background: #cdf;",
      " .fileViewer" -> "padding: 5px; margin: 0; flex: auto;",
      " .fileViewer.readOnly" -> "background: #f6f6f6;",
  ).register()

}
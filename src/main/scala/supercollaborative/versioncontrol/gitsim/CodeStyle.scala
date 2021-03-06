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
    " .history .history-row" -> "border: none; text-align: left; background:none; width: 100%; display: grid; grid-template-columns: 1fr 2fr 3fr 1fr; align-items: center;",
    " .history .history-row.selected" -> "background: #cdf;",
    " .history .history-row:hover" -> "background: #def;",
    " .history .history-row .hash" -> "color: #555; font-size: 80%; font-family: monospace; margin-right: 10px;",
    " .history .history-row .author" -> "max-width: 300px; display: inline-flex; margin-right: 10px;",
    " .history .history-row .comment" -> "display: inline-flex; margin-right: 10px;",
    " .history .history-row .date" -> "font-size: 80%; text-align: right;",
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
      " .fileList .fileSelector.untracked::after " -> "content: 'U'; float:right; color: green;",
      " .fileList .fileSelector.untracked " -> "color: green;",
      " .fileList .fileSelector.modified::after " -> "content: 'M'; float:right; color: orange;",
      " .fileList .fileSelector.modified " -> "color: orange;",
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
      " .path" -> "stroke: #aaf; stroke-width: 3;",
      " .path.left" -> "stroke: #a44; stroke-width: 5;",
      " .path.right" -> "stroke: #4a4; stroke-width: 5;",
      " .path.both" -> "stroke: #444; stroke-width: 5;",
  ).register()

  val horizontalCommitDAG = Styling(
    """
      |
      |""".stripMargin
    ).modifiedBy(
      " .commit-label" -> "dominant-baseline: middle; text-anchor: middle;",
      " .tag-label" -> "font-size: 20px; fill: #0aa; dominant-baseline: text-before-edge;",
      " .branch-label" -> "font-size: 20px; fill: #a0a; dominant-baseline: text-before-edge;",
      " .named-detached-label" -> "font-size: 20px; fill: #aa0; dominant-baseline: text-before-edge;",
      " .ref-line" -> "stroke: #888; stroke-width: 3; stroke-dasharray: 4;",
      " .commit-label.hash-only" -> "font-size: 20px; fill: #44a;",
      " .parent-arrow" -> "stroke: #aaa; stroke-width: 4; fill: none; stroke-linejoin: round; stroke-linecap: round;",
      " .compact-commit-label-box" -> "text-align: center; font-size: 20px; position: absolute; bottom: 0; width: 100%; overflow-y: auto;",
      " .compact-commit-label-box .hash" -> "color: #44a",
      " .compact-commit-label-box .author" -> "color: #4a4",
      " .compact-commit-label-box .time" -> "color: #44a",
      " .commit.selected" -> "filter: drop-shadow(0 2px 4px #44a); stroke: #44a; stroke-width: 2px",
      " .parent-arrow.selected" -> "filter: drop-shadow(0 2px 4px #44a); stroke: #44a;",
      " .commit.fade" -> "opacity: 0.3;",
      " .parent-arrow.fade" -> "opacity: 0.3;",
      " .commit.highlight" -> "filter: drop-shadow(0 2px 6px #4af); stroke: #4af; stroke-width: 4px;",
      " .parent-arrow.highlight" -> "filter: drop-shadow(0 2px 6px #44a); stroke: #44a;",
  ).register()


  val blockLabel = Styling("""
  |position: relative; top: 0;
  |border: 1px solid #f0f0f0;
  |border-radius: 5px;
  |""".stripMargin
  ).modifiedBy(
    " .block-label" -> "position: absolute; top: 0; left: 0; padding: 2px; background: #f0f0f0; border-radius: 5px; font-size: 20px;"
  ).register()

  val threeWayDiff = Styling("""
  |""".stripMargin
  ).modifiedBy(
    " .chunk" -> "display: grid; grid-template-columns: 1fr 1fr 1fr; border-bottom: 1px dashed #aaa;",
  ).register()

  val commandBlock = Styling("""
  |display: flex;
  |flex-wrap: wrap;
  |align-items: stretch;
  |margin: 1em;
  |""".stripMargin
  ).modifiedBy(
    " .sh" -> "font-family: 'Fira Mono'; color: white; background-color: #444; line-height: 1.5; padding: 10px; ",
    " .execute" -> "flex: 0; border-radius: 0 5px 5px 0;",
    " :first-child" -> "border-radius: 5px 0 0 5px;",
    " input.sh" -> "border: none;",
  ).register()

  val gitToolbar = Styling("""
  |background: #f6f6f6;
  |padding: 10px;
  |""".stripMargin).modifiedBy(
    " button.active" -> "background: #444; color: white;",
    " .branch-indicator" -> "margin-left: 2em;"

  ).register()

  val editorDiff = Styling(
    """
      |
      |""".stripMargin
    ).modifiedBy(
      " .deleted" -> "background-color: #c88;  display: inline-block; width: 100%; height: 0; padding: 1px;",
      " .added" ->  "background-color: #8c8; display: inline-block; width: 100%;",
  ).register()

  val markdownSh = Styling("").modifiedBy(
    " pre.sh" -> "border-left: 10px solid #aaa; border-radius: 5px 5px 0 0; padding: 15px; display: block; background: #444; color: white; margin: 1rem 0 0 0;",
    " pre.output" -> "border: 1px solid #aaa; border-left: 10px solid #aaa; border-radius: 0 0 5px 5px; padding: 15px; display: block; background: #f0f0f0;"
  ).register()


  /** Style used by the git challenge to let the left side scroll and the right side display a fixed UI */
  val gitChallengeSplit = Styling("""
  |display: grid;
  |grid-template-columns: 1fr 750px;
  |""".stripMargin).modifiedBy(
    " .instructions" -> "height: 900px; overflow-y: scroll;"
  ).register()

}
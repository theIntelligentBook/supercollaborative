package supercollaborative.versioncontrol.gitsim

import scala.util.Failure

// A simple LCS finder based on memoisation of matches in the sequences
// Not the most efficient, but we're only dealing with short sequences.
def longestCommonSubsequence[T](left:Seq[T], right:Seq[T]):Seq[T] = {
  if left.isEmpty || right.isEmpty then 
    Seq.empty 
  else if left.head == right.head then
    left.head +: longestCommonSubsequence(left.tail, right.tail)
  else 
    type Loc = (Int, Int)
    type Path = List[(Boolean, Loc)] // A location and whether it was reached via a diagonal line

    val lRange = left.indices
    val rRange = right.indices

    val matches:Map[Loc, Boolean] = (for 
      i <- lRange
      j <- rRange
    yield (i, j) -> (left(i) == right(j))).toMap

    def nextLocs(path:Path):List[Path] = {
      path match {
        case (_, (x, y)) :: _ if lRange.contains(x) && rRange.contains(y) && matches((x, y)) => 
          for (b, (xx, yy)) <- List((false, (x + 1, y)), (true, (x + 1, y + 1)), (false, (x, y + 1))) yield (b, (xx, yy)) :: path
        case (_, (x, y)) :: _ => 
          for (xx, yy) <- List((x + 1, y), (x, y + 1)) if xx <= left.length && yy <= right.length yield (false, (xx, yy)) :: path
        case _ => 
          List.empty
      }
    } 

    def pathFind(target:Loc, from:List[Path]):Path = from match {
      case ((_, h) :: _) :: _ if h == target => from.head // We've arrived
      case head :: tail => 
        pathFind(target, tail ++ nextLocs(head))
      case _ => 
        throw new RuntimeException("Ran out of locations to pathfind from")
    }

    val path = pathFind((left.length, right.length), List(List((false, (0, 0)))))
    (for (b, (l, _)) <- path if b yield left(l - 1)).reverse
}


enum GitException extends Throwable:
  case AlreadyExists

sealed trait Obj:
  def hash = this.hashCode.toHexString

sealed trait File extends Obj:
  def toMutable: MutableFile

object File:
  case class TextFile(text:String) extends File:
    def toMutable = MutableFile.TextFile(text)

  case class BinaryFile(arr:Array[Int]) extends File:
    def toMutable = MutableFile.BinaryFile(arr.clone)  
  
  case class Tree(files:Map[String, File]) extends File:
    def toMutable:MutableFile.Tree = 
      import scala.collection.mutable
      MutableFile.Tree(mutable.Map((for (n, f) <- files.toSeq yield (n, f.toMutable))*))


sealed trait MutableFile:
  def toImmutable: File

object MutableFile {
  import scala.collection.mutable
  
  class TextFile(var text:String) extends MutableFile:
    def toImmutable = File.TextFile(text)
  
  class Tree(val files:mutable.Map[String, MutableFile]) extends MutableFile:
    def toImmutable = File.Tree((for (n, f) <- files yield (n, f.toImmutable)).toMap)

  class BinaryFile(val arr:Array[Int]) extends MutableFile:
    def toImmutable = File.BinaryFile(arr.clone)
}

case class Commit(parents: Seq[Commit], tree:File.Tree)(time: Int) extends Obj {
  // All the ancestors of this commit in a single Seq
  def ancestors:Seq[Commit] = parents.flatMap(_.ancestors) 

  def canFastForwardTo(other:Commit) = other.ancestors.contains(this)

  def behind(other:Commit) = this != other && (other.ancestors.toSet - ancestors.toSet).nonEmpty
}

def temporalPositionSort(start:Seq[Commit]) = {

}

enum Ref:
  case Branch(name:String, commit:Commit)
  case Tag(name:String, commit:Commit)
  case Detached(commit:Commit)
  
  def commit:Commit

case class Remote(name:String, url:String, refs:Set[Ref])

case class Git(objects:Set[Obj], refs:Set[Ref], head:Ref, remotes:Set[Remote], index:File.Tree) {

  def checkout(ref:Ref) = this.copy(head = ref)

  def fetch(remoteName:String, remote:Git) = this.copy(
    objects = objects ++ remote.objects, 
    remotes = remotes.map { r => 
      if r.name == remoteName then 
        Remote(r.name, r.url, remote.refs)
      else r
    }
  )

  def branch(name:String) = {
    if refs.exists { 
      case Ref.Branch(n, _) if n == name => true
      case _ => false
    } then Failure(GitException.AlreadyExists) else this.copy(refs = refs + Ref.Branch(name, head.commit))
  }


}

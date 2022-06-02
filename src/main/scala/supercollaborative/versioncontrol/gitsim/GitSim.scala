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

// Takes a list of commits and sorts them to show in a git graph
def temporalTopological(toAdd:Seq[Commit], sorted:Seq[Commit] = Seq.empty):Seq[Commit] = 
  toAdd.sortBy(_.time) match { // TODO: is this sorted the right way?
    case Nil => sorted
    case h :: t => temporalTopological(t ++ h.parents.filter(!sorted.contains(_)), sorted :+ h)
  }


// If the commit graph is vertical, tries to work out the horizontal positioning of commits
def layout(commits:Seq[Commit], laidOut:List[(Commit, Int)] = Nil, active:Seq[Commit] = Seq.empty):Seq[(Commit, Int)] = {
  commits match {
    case Nil => laidOut.reverse
    case h :: t if active.contains(h) =>
      val x = active.indexOf(h) 
      val lo = (h, x) :: laidOut
      val parents = h.parents.filterNot(active.contains(_))
      val newActive = active.take(x) ++ parents ++ active.drop(x + 1)
      layout(t, lo, newActive)
    case h :: t =>
      val x = active.length
      val lo = (h, x) :: laidOut
      val parents = h.parents.filterNot(active.contains(_))
      val newActive = active ++ parents
      layout(t, lo, newActive)
  }

}

def layoutRefs(refs:Seq[Ref]):Seq[(Commit, Int)] = {
  val commits = temporalTopological(refs.map(_.commit))
  layout(commits)
}


enum GitException extends Throwable:
  case AlreadyExists
  case FileException(msg:String)
  case CommitException(msg:String)

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

    def find(path:List[String]):File = {
      path match {
        case dir :: rest if rest.nonEmpty && files.contains(dir) => 
          files(dir) match {
            case t:Tree => t.find(rest)
            case _ => throw GitException.FileException("Not a directory: " + dir)
          }
        case f :: _ if files.contains(f) => files(f)
        case _ => throw GitException.FileException("File not found")
      }
    }

    def findPath(path:String):File = find(path.split("/").toList)

    def add(path:List[String], f:File):Tree = {
      path match {
        case dir :: rest if rest.nonEmpty && files.contains(dir) => 
          files(dir) match {
            case t:Tree => t.add(rest, f)
            case _ => throw GitException.FileException("Not a directory: " + dir)
          }
        case dir :: rest if rest.nonEmpty => Tree(files + (dir -> Tree(Map.empty).add(rest, f))) 
        case name :: _ => Tree(files + (name -> f)) 
        case _ => throw GitException.FileException("Tried to add without a path")
      }
    }

    def addPath(path:String, tree:File.Tree):Tree = {
      val p = path.split("/").toList
      add(p, tree.find(p))
    }


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

case class Commit(parents: Seq[Commit], tree:File.Tree, author:String, comment:String, time:Int)extends Obj {
  // All the ancestors of this commit in a single Seq
  def ancestors:Seq[Commit] = parents.flatMap(_.ancestors) 

  def canFastForwardTo(other:Commit) = other.ancestors.contains(this)

  def behind(other:Commit) = this != other && (other.ancestors.toSet - ancestors.toSet).nonEmpty
}

object Commit {

  val Empty = Commit(Seq.empty, File.Tree(Map.empty), "", "", 0)

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

  def branches:Map[String, Ref.Branch] = (refs.collect { 
    case b:Ref.Branch => b.name -> b
  }).toMap

  def addAll(t:File.Tree) = this.copy(index = t)

  def commit(author:String, message:String, time:Int) = {
    head match {
      case Ref.Branch(name, c) => 
        val newC = Commit(Seq(c), index, author, message, time)
        this.copy(refs = refs - Ref.Branch(name, c) + Ref.Branch(name, newC), head = Ref.Branch(name, newC), index = File.Tree(Map.empty))
      case _ => 
        throw GitException.CommitException("Can't commit in this checkout state")
    }
  }


}

object Git {

  def init = Git(Set.empty, Set(Ref.Branch("main", Commit.Empty)), Ref.Branch("main", Commit.Empty), Set.empty, File.Tree(Map.empty))


}
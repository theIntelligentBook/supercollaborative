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

    val memo = scala.collection.mutable.Map((0,0) -> 0)

    def neighboursUp(p:Loc):Seq[Loc] = {
      val (x, y) = p
      if lRange.contains(x) && rRange.contains(y) && left(x) == right(y) then
        Seq((x, y+1), (x+1, y+1), (x+1, y))
      else
        Seq((x, y+1), (x+1, y))
    }

    // memoise the distance table
    for 
      x <- (0 to left.length)
      y <- (0 to right.length)
      p = (x, y)

      // Note that we do have to include (left.length, right.length - the grid is one bigger than the num letters)
      (xx, yy) <- neighboursUp(p) //if xx <= left.length && yy <= right.length
    do 
      val d = memo(p)
      val pp = (xx, yy)
      if memo.getOrElse(pp, d + 2) > d + 1 then memo(pp) = d + 1

    //println(memo.toSeq.sortBy(_._1))

    def neighbourDown(d:Int, p:Loc):Option[(Boolean, Loc)] =
      val (x, y) = p
      // when traversing back down the table, we do still need to check for valid paths
      if (x > 0 && y > 0 && left(x-1) == right(y-1)) then
        Seq((x-1, y-1), (x-1, y), (x, y-1))
          .find { pp => memo.contains(pp) && memo(pp) < d }
          .map { (xx, yy) => (true, (xx, yy))}
      else 
        Seq((x-1, y), (x, y-1))
          .find { pp => memo.contains(pp) && memo(pp) < d }
          .map { (xx, yy) => (false, (xx, yy))}

    var _lcs:List[T] = Nil
    var pos = (left.length, right.length)
    for 
      _ <- 0 until memo(pos) 
      (diag, (x, y)) <- neighbourDown(memo(pos), pos)
    do
      //println(s"Following ${(x, y)}")
      if diag then 
        //println(s" ${(x, y)} $left $right")
        _lcs = left(x) :: _lcs
      pos = (x, y)

    _lcs
}

/** Describes the result of comparing two sequences */
enum CompareResult[T]:
  case Left(item:T)
  case Right(item:T)
  case Both(item:T)

/** Takes two sequences, diffs them, and returns what's in the left, what's in the right, and what's in both */
def compare[T](left:Seq[T], right:Seq[T]):Seq[CompareResult[T]] = {
  val lcs = longestCommonSubsequence(left, right)

  @scala.annotation.tailrec
  def recurse(b:List[T], l:List[T], r:List[T], result:Seq[CompareResult[T]]):Seq[CompareResult[T]] = (b, l, r) match {
    case (Nil, Nil, Nil) => result
    case (Nil, ll, rr) => result ++ ll.map(x => CompareResult.Left(x)) ++ rr.map(x => CompareResult.Right(x))
    case (_, ll, Nil) => result ++ ll.map(x => CompareResult.Left(x))
    case (_, Nil, rr) => result ++ rr.map(x => CompareResult.Right(x))
    case (bb :: _, ll :: lt, _) if bb != ll => recurse(b, lt, r, result :+ CompareResult.Left(ll))
    case (bb :: _, _, rr :: rt) if bb != rr => recurse(b, l, rt, result :+ CompareResult.Right(rr))
    case (bb :: bt, _ :: lt, _ :: rt) => recurse(bt, lt, rt, result :+ CompareResult.Both(bb))
  }
  
  recurse(lcs.toList, left.toList, right.toList, Seq.empty)
}

// Takes a list of commits and sorts them to show in a git graph
def temporalTopological(toAdd:Seq[Commit], sorted:List[Commit] = Nil):Seq[Commit] = 
  toAdd.filterNot(sorted.contains(_)).sortBy(- _.time) match { 
    case Nil => sorted
    case h :: t => 
      val newSorted = h :: sorted
      val newToAdd = (t ++ h.parents)
      temporalTopological(newToAdd, newSorted)
  }


// If the commit graph is vertical, tries to work out the horizontal positioning of commits
def layout(commits:Seq[Commit], laidOut:List[(Commit, Int)] = Nil, active:Seq[Commit] = Seq.empty):Map[Commit, (Int, Int)] = {

  // To do the layout, we "book" columns before we reach them. 
  // This contains the expected next occupant of a column
  var active:Seq[Commit] = Seq.empty

  // Add the commits to the map we can
  var children = scala.collection.mutable.Map.empty[Commit, Seq[Commit]]
  var map = scala.collection.mutable.Map.empty[Commit, (Int, Int)]

  for 
    c <- commits.iterator 
    p <- c.parents
  do children(p) = children.getOrElse(p, Seq.empty) :+ c

  // Run through the list from newest to oldest (i.e. in reverse)
  for (c, i) <- commits.iterator.zipWithIndex if !map.contains(c) do
    val pos = active.indexOf(c)
    if pos >= 0 then 
      map(c) = (i, pos)
      active = if children.contains(c) then 
        // Children try to occupy the same column as the parent
        // If there are multiple children, they will push other commits to the right
        active.take(pos) ++ children(c).filterNot(active.contains(_)) ++ active.drop(pos + 1)
      else 
        // If a commit has no children, we hold its column with an empty commit
        // this endeavours to prevent a line from passing through it 
        // (e.g. if a younger sibling was put in the same column, a line from its parent to that
        // sibling would pass through this commit)
        active.take(pos) ++ Seq(Commit.Empty) ++ active.drop(pos + 1)
    else 
      map(c) = (i, active.length)
      active = active ++ children.getOrElse(c, Seq.empty).filterNot(active.contains(_))

    println(s"done ${c.comment} active ${active.map(_.comment)}")

  map.toMap
}

def layoutRefs(refs:Seq[Ref]):Map[Commit, (Int, Int)] = {
  val commits = temporalTopological(refs.map(_.commit))
  layout(commits)
}


enum GitException extends Throwable:
  case AlreadyExists
  case FileException(msg:String)
  case CommitException(msg:String)

/** An object that can be stored in our GitSim */ 
sealed trait Obj:
  def hash:String = 
    val h = this.hashCode.toHexString
    if h.length < 8 then Seq.fill(8 - h.length)("0").mkString + h else h

/** Represents an immutable file */
sealed trait File extends Obj:
  def toMutable: MutableFile

/** The kinds of immutable file our git sim supports */
object File:
  case class TextFile(text:String) extends File:
    def toMutable = MutableFile.TextFile(text)

  case class BinaryFile(arr:Array[Int]) extends File:
    def toMutable = MutableFile.BinaryFile(arr.clone)  
  
  case class Tree(files:Map[String, File]) extends File:
    def toMutable:MutableFile.Tree = 
      import scala.collection.mutable
      MutableFile.Tree(mutable.Map((for (n, f) <- files.toSeq yield (n, f.toMutable))*))

    /** The selectable paths within the tree */
    def paths:List[List[String]] = 
      for 
        (n, f) <- files.toList.sortBy(_._1)
        p <- f match {
          case t:Tree => t.paths
          case _ => List(Nil)
        }
      yield n :: p

    def find(path:List[String]):Option[File] = {
      path match {
        case dir :: rest if rest.nonEmpty && files.contains(dir) => 
          files(dir) match {
            case t:Tree => t.find(rest)
            case _ => None
          }
        case f :: _ if files.contains(f) => Some(files(f))
        case _ => None
      }
    }

    def findPath(path:String):Option[File] = find(path.split("/").toList)

    def add(path:List[String], f:File):Tree = {
      path match {
        case dir :: rest if rest.nonEmpty && files.contains(dir) => 
          files(dir) match {
            case t:Tree => t.add(rest, f)
            case _ => throw GitException.FileException("Not a directory: " + dir)
          }
        case dir :: rest if rest.nonEmpty => Tree(files + (dir -> Tree(Map.empty).add(rest, f))) 
        case name :: _ => Tree(files.updated(name, f)) 
        case _ => throw GitException.FileException("Tried to add without a path")
      }
    }

    def addPath(path:String, tree:File.Tree):Tree = {
      val p = path.split("/").toList
      add(p, tree)
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

/** Represents a git commit */
case class Commit(parents: Seq[Commit], tree:File.Tree, author:String, comment:String, time:Double)extends Obj {
  // All the ancestors of this commit in a single Seq
  def ancestors:Set[Commit] = 
    val pset = parents.toSet
    pset ++ pset.flatMap(_.ancestors) 

  def canFastForwardTo(other:Commit) = other.ancestors.contains(this)

  def behind(other:Commit) = this != other && (other.ancestors.toSet - ancestors.toSet).nonEmpty

  @scala.annotation.tailrec
  final def ^(i:Int):Commit = if i <= 0 || parents.isEmpty then this else parents.head.^(i - 1)
}

object Commit {

  /** Git sim repositories start out pointing to the empty commit */
  val Empty = Commit(Seq.empty, File.Tree(Map.empty), "", "", 0)

}

enum Ref:
  case Branch(name:String, commit:Commit)
  case Tag(name:String, commit:Commit)
  case Detached(commit:Commit)

  case NamedDetached(name:String, commit:Commit)

  def commit:Commit

  // useful if we want to show a git graph but strip out the branch label
  def detach:Detached = Detached(commit)

  // useful for showing HEAD as if it was a reference in diagrams
  def namedDetach(name:String) = NamedDetached(name, commit)

case class Remote(name:String, url:String, refs:Set[Ref])

case class Git(objects:Set[Obj], refs:Set[Ref], head:Ref, remotes:Set[Remote], index:File.Tree) {

  def checkout(ref:Ref):Git = this.copy(head = ref)

  def switch(name:String):Git = checkout(branches(name))

  def checkout_^(i:Int):Git = checkout(Ref.Detached(this.head.commit.^(i)))

  def fetch(remoteName:String, remote:Git) = this.copy(
    objects = objects ++ remote.objects, 
    remotes = remotes.map { r => 
      if r.name == remoteName then 
        Remote(r.name, r.url, remote.refs)
      else r
    }
  )

  def branch(name:String):Git = {
    if refs.exists { 
      case Ref.Branch(n, _) if n == name => true
      case Ref.Tag(n, _) if n == name => true
      case _ => false
    } then throw GitException.AlreadyExists else this.copy(refs = refs + Ref.Branch(name, head.commit))
  }

  def tag(name:String):Git = {
    if refs.exists { 
      case Ref.Branch(n, _) if n == name => true
      case Ref.Tag(n, _) if n == name => true
      case _ => false
    } then throw GitException.AlreadyExists else this.copy(refs = refs + Ref.Tag(name, head.commit))
  }

  def branches:Map[String, Ref.Branch] = (refs.collect { 
    case b:Ref.Branch => b.name -> b
  }).toMap

  def tags:Map[String, Ref.Tag] = (refs.collect { 
    case b:Ref.Tag => b.name -> b
  }).toMap

  def addAll(t:File.Tree) = this.copy(index = t)

  def commit(author:String, message:String, time:Double) = {
    head match {
      case Ref.Branch(name, c) => 
        val newC = Commit(if c == Commit.Empty then Seq.empty else Seq(c), index, author, message, time)
        this.copy(refs = refs - Ref.Branch(name, c) + Ref.Branch(name, newC), head = Ref.Branch(name, newC), index = File.Tree(Map.empty))
      case _ => 
        throw GitException.CommitException("Can't commit in this checkout state")
    }
  }

  def headAsDetached = head match {
    case Ref.Branch(n, c) => Ref.NamedDetached(s"HEAD ($n)", c)
    case Ref.Tag(n, c) => Ref.NamedDetached(s"HEAD ($n)", c)
    case Ref.Detached(c) => Ref.NamedDetached(s"HEAD (detached)", c)
    case Ref.NamedDetached(n, c) => Ref.NamedDetached(s"HEAD ($n)", c)
  }

}

object Git {

  def init = Git(Set.empty, Set(Ref.Branch("main", Commit.Empty)), Ref.Branch("main", Commit.Empty), Set.empty, File.Tree(Map.empty))


}
package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  property("min1") = forAll { a: Int =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  property("empty") = forAll {a: Int =>
    val h = insert(a, empty)
    val nH = deleteMin(h)
    isEmpty(nH)
  }

  property("minOfTwo") = forAll { (i1: Int, i2: Int) =>
    val h = insert(i1, insert(i2, empty))

    val min = if (i1 <= i2) i1 else i2

    val minH = findMin(h)

    min == minH
  }

  lazy val genHeap: Gen[H] = for {
    i <- choose(0, 100)
    isEmpty <- arbitrary[Boolean]
    //emp <- empty
    //hp <- if (isEmpty) emp else insert(i, emp)
    gH <- oneOf(const(empty), genHeap)
  } yield insert(i, gH)

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h))==m
  }

  def printHeap(h: H) {
    if (!isEmpty(h)) {
      println(findMin(h))
      printHeap(deleteMin(h))
    }
  }

//  property("genPrint") = forAll { (h: H) =>
//    println("printing heap")
//    printHeap(h)
//    true
//  }
//
//  property("someProp") = forAll { h: H =>
//    true == false
//  }

  property("findMin1") = forAll { (h1: H, h2: H) =>
    val min1 = findMin(h1)
    val min2 = findMin(h2)

    val merged = meld(h1, h2)
    val mergeMin = findMin(merged)

    min1 == mergeMin || min2 == mergeMin
  }

  property("allRetrieved") = forAll { list: List[Int] =>
    val heap = insertIntoHeap(empty, list)

    val retList = getElements(heap, List.empty).reverse

    val sortedList = list.sortBy(i=>i)
    retList.equals(sortedList)
  }

  property("priorityqueue") = forAll { h: H =>

    val retList = getElements(h, List.empty)

    val reverse = retList.reverse
    val sorted = retList sortBy (i => i)

    sorted.equals(reverse)
  }

  def insertIntoHeap(h: H, ints: List[Int]) :H = {
    ints match {
      case Nil => h
      case x :: xs => insertIntoHeap(insert(x, h), xs)
    }
  }

  def getElements(h: H, list: List[Int]): List[Int] = {
    isEmpty(h) match {
      case true => list
      case false =>
        val newList = findMin(h) :: list
        getElements(deleteMin(h), newList)
    }
  }

}

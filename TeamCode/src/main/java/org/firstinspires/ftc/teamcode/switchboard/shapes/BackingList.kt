package org.firstinspires.ftc.teamcode.switchboard.shapes

import java.util.*
import kotlin.NoSuchElementException

class BackingList<E>(val list: MutableList<E> = mutableListOf()) : MutableList<E> by list {
    inner class SharedQueue : Queue<E> {
        var i = 0

        /**
         * Inserts the specified element into this queue if it is possible to do so
         * immediately without violating capacity restrictions, returning
         * `true` upon success and throwing an `IllegalStateException`
         * if no space is currently available.
         *
         * @param e the element to add
         * @return `true` (as specified by [Collection.add])
         * @throws IllegalStateException if the element cannot be added at this
         * time due to capacity restrictions
         * @throws ClassCastException if the class of the specified element
         * prevents it from being added to this queue
         * @throws NullPointerException if the specified element is null and
         * this queue does not permit null elements
         * @throws IllegalArgumentException if some property of this element
         * prevents it from being added to this queue
         */
        override fun add(element: E): Boolean {
            list.add(element)
            return true
        }

        /**
         * Returns the size of the collection.
         */
        override val size: Int
            get() = list.size - i

        /**
         * Adds all of the elements of the specified collection to this collection
         *
         * @return `true` if any of the specified elements was added to the collection, `false` if the collection was not modified.
         */
        override fun addAll(elements: Collection<E>): Boolean {
            list.addAll(elements)
            return true
        }

        /**
         * Removes all elements from this collection.
         */
        override fun clear() {
            i = list.size
        }

        override fun iterator(): MutableIterator<E>
                = list.listIterator(i)

        /**
         * Retrieves and removes the head of this queue.  This method differs
         * from [poll][.poll] only in that it throws an exception if this
         * queue is empty.
         *
         * @return the head of this queue
         * @throws NoSuchElementException if this queue is empty
         */
        override fun remove(): E
                = if (isEmpty()) element() else throw NoSuchElementException()

        /**
         * Checks if the specified element is contained in this collection.
         */
        override fun contains(element: E): Boolean {
            for (j in i..list.size)
                if (element === list[j])
                    return true
            return false
        }

        /**
         * Checks if all elements in the specified collection are contained in this collection.
         */
        override fun containsAll(elements: Collection<E>): Boolean {
            for (c in elements)
                if (!contains(c))
                    return false
            return true
        }

        /**
         * Returns `true` if the collection is empty (contains no elements), `false` otherwise.
         */
        override fun isEmpty(): Boolean
                = i >= list.size

        /**
         * Removes a single instance of the specified element from this
         * collection, if it is present.
         *
         * @return `true` if the element has been successfully removed; `false` if it was not present in the collection.
         */
        override fun remove(element: E): Boolean {
            TODO("Not yet implemented")
        }

        /**
         * Removes all of this collection's elements that are also contained in the specified collection.
         *
         * @return `true` if any of the specified elements was removed from the collection, `false` if the collection was not modified.
         */
        override fun removeAll(elements: Collection<E>): Boolean {
            TODO("Not yet implemented")
        }

        /**
         * Retains only the elements in this collection that are contained in the specified collection.
         *
         * @return `true` if any element was removed from the collection, `false` if the collection was not modified.
         */
        override fun retainAll(elements: Collection<E>): Boolean {
            TODO("Not yet implemented")
        }

        /**
         * Inserts the specified element into this queue if it is possible to do
         * so immediately without violating capacity restrictions.
         * When using a capacity-restricted queue, this method is generally
         * preferable to [.add], which can fail to insert an element only
         * by throwing an exception.
         *
         * @param e the element to add
         * @return `true` if the element was added to this queue, else
         * `false`
         * @throws ClassCastException if the class of the specified element
         * prevents it from being added to this queue
         * @throws NullPointerException if the specified element is null and
         * this queue does not permit null elements
         * @throws IllegalArgumentException if some property of this element
         * prevents it from being added to this queue
         */
        override fun offer(e: E): Boolean {
            add(e)
            return true
        }

        /**
         * Retrieves and removes the head of this queue,
         * or returns `null` if this queue is empty.
         *
         * @return the head of this queue, or `null` if this queue is empty
         */
        override fun poll(): E?
                = if (isEmpty()) element() else null

        /**
         * Retrieves, but does not remove, the head of this queue.  This method
         * differs from [peek][.peek] only in that it throws an exception
         * if this queue is empty.
         *
         * @return the head of this queue
         * @throws NoSuchElementException if this queue is empty
         */
        override fun element(): E
                = list[i].also { i++ }


        /**
         * Retrieves, but does not remove, the head of this queue,
         * or returns `null` if this queue is empty.
         *
         * @return the head of this queue, or `null` if this queue is empty
         */
        override fun peek(): E?
                = if (isEmpty()) list[i] else null
    }

    private val queues: MutableList<SharedQueue> = mutableListOf()
    fun queue(): SharedQueue = SharedQueue().also { queues += it }
    fun refresh() {
        val index = queues.map { it.i }.minOrNull() ?: return
        repeat(index) {
            list.removeFirst()
        }
        queues.forEach { it.i -= index }
    }
}
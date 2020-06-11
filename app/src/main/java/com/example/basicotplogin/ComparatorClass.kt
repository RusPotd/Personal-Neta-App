package com.example.basicotplogin

import com.example.basicotplogin.ModelClasses.Posts

class ComparatorClass : Comparator<Posts>{
    override fun compare(o1: Posts?, o2: Posts?): Int {
        return o1!!.getTime()!!.compareTo(o2!!.getTime())
    }
}
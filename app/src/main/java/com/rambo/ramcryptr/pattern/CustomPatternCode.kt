package com.rambo.ramcryptr.pattern

object CustomPatternCode {

    fun generatePattern(
        data:String
    ):Array<IntArray>{

        val rows=12
        val cols=16

        val grid=
            Array(rows){
                IntArray(cols)
            }

        grid[0][0]=1
        grid[0][15]=1
        grid[11][0]=1
        grid[11][15]=1

        var index=0

        for(r in 1 until 11){
            for(c in 1 until 15){

                if(index<data.length){

                    grid[r][c]=
                        data[index].code % 2

                    index++
                }
            }
        }

        return grid
    }

}

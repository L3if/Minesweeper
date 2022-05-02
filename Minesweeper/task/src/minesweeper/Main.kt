package minesweeper

import kotlin.random.Random

class Minefield(val minesNum: Int, val sizeX: Int = 9, val sizeY: Int = 9) {
    var _minesNum = minesNum
    val field  = MutableList(sizeY) { MutableList(sizeX){0}}
    val exploredCells  = MutableList(sizeY) { MutableList(sizeX){false}}
    val marks = mutableListOf<Pair<Int, Int>>()
    var firstTouch: Boolean = true
    init {
        var counter = _minesNum
        val rand = Random(43)
        do{
            val randX = rand.nextInt(0,sizeX)
            val randY = rand.nextInt(0,sizeY)
            if (field[randY][randX] == 0 ){
                field[randY][randX] = 9
                counter--
            }
        } while(counter > 0 )
        for (y in field.indices){
            for (x in field[y].indices)
                field[y][x] = CheckArea(x, y)
        }
    }
    fun print(){
        for (y in field.indices){
            for (x in field[y].indices)
                field[y][x] = CheckArea(x, y)
        }
        print(" │")
        (1..this.sizeX).forEach{print(it)}
        println("|")
        println("—│${"—".repeat(this.sizeX)}│")
        for (y in 0 until this.sizeY){
            print("${y+1}|")
            for (x in 0 until this.sizeX)
                when{
                    (Pair(x,y)  in marks)  && !(exploredCells[y][x]) -> print('*')
                    exploredCells[y][x] && field[y][x] == 0 -> print('/')
                    (exploredCells[y][x] && field[y][x] in 0 until 9) -> print(field[y][x])
                    exploredCells[y][x] && field[y][x] == 9 -> print('X')
                    else -> print('.')
                }
            println("|")
        }
        println("—│${"—".repeat(this.sizeX)}│")
    }
    operator fun get(x: Int, y: Int): Int{
        if (x<0 || y<0 || x >= this.sizeX || y>= this.sizeY )
            return 0
        else
            return this.field[y][x]
    }
    fun CheckArea(x: Int ,y: Int): Int{
        if (this[x, y] == 9)
            return 9
        var counter = 0
        if (this[x-1,y-1] == 9) counter++
        if (this[x,y-1] == 9) counter++
        if (this[x+1,y-1] == 9) counter++
        if (this[x+1,y] == 9) counter++
        if (this[x+1,y+1] == 9) counter++
        if (this[x,y+1] == 9) counter++
        if (this[x-1,y+1] == 9) counter++
        if (this[x-1,y] == 9) counter++
        return counter
    }

    fun ExploreCell(x: Int,y: Int){
        if (x<0 || y<0 || x >= this.sizeX || y>= this.sizeY || exploredCells[y][x])
            return
        if(firstTouch && this[x,y] == 9 ){
            field[y][x] = 0
            val rand = Random(45)
            var counter2 = 1
            do{
                val randX = rand.nextInt(0,sizeX)
                val randY = rand.nextInt(0,sizeY)
                if (field[randY][randX] != 0 ){
                    field[randY][randX] = 9
                    counter2--
                }
            } while(counter2 > 0 )
            field[y][x] = 0
            firstTouch = false
        }
        if (this[x,y] == 9) {
            exploredCells[y][x] = true
            println("You stepped on a mine and failed!")
            for (y in exploredCells.indices){
                for (x in exploredCells[y].indices)
                    exploredCells[y][x] = true
            }
            return
        }
        firstTouch = false
        exploredCells[y][x] = true
        if (this[x-1,y-1] in 0 until 9) ExploreCell(x-1,y-1)
        if (this[x,y-1] in 0 until 9) ExploreCell(x,y-1)
        if (this[x+1,y-1] in 0 until 9) ExploreCell(x+1,y-1)
        if (this[x+1,y] in 0 until 9) ExploreCell(x+1,y)
        if (this[x+1,y+1] in 0 until 9) ExploreCell(x+1,y+1)
        if (this[x,y+1] in 0 until 9) ExploreCell(x,y+1)
        if (this[x-1,y+1] in 0 until 9) ExploreCell(x-1,y+1)
        if (this[x-1,y] in 0 until 9) ExploreCell(x-1,y)
    }

    fun SetMark(inX: Int, inY: Int){
        when{
            Pair(inX, inY ) in marks -> {
                marks.remove(Pair(inX, inY))
            }
            (this[inX, inY] in  1 until 9) && Pair(inX, inY ) in marks -> {
                println("There is a number here!")
            }
            (this[inX, inY] == 9) -> {
                marks.add(Pair(inX, inY))
                _minesNum --
            }
            else -> {
                marks.add(Pair(inX, inY))
            }
        }

    }
    fun play(){
        this.print()
        do {
            println("Set/unset mine marks or claim a cell as free: ")
            val input = readln()
            val inX: Int = input.split(" ")[0].toInt() - 1
            val inY: Int = input.split(" ")[1].toInt() - 1
            val command: String = input.split(" ")[2].toString()
            when(command){
                "free" -> {ExploreCell(inX,inY)}
                "mine" -> {SetMark(inX, inY)}
            }
            this.print()
        }while (_minesNum > 0)
            println("Congratulations! You found all the mines!")
    }
}


fun main() {
    print("How many mines do you want on the field? ")
    val minesNum = readln().toInt()
    val field = Minefield(minesNum)
    field.play()
}

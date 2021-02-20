import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.*
import org.w3c.dom.*
import org.w3c.dom.events.Event
import kotlin.math.round
import kotlin.random.Random

fun main() {
    //Page Navigation
    // to do -set top margins of the main elements dynamically
    val top=document.getElementById("top")
    val mid=document.getElementById("mid")
    val bot=document.getElementById("bot")
    var topHeight:Double=top!!.scrollHeight.toDouble()
    var midHeight:Double=mid!!.scrollHeight.toDouble()
    var botHeight:Double=bot!!.scrollHeight.toDouble()
    val hangmanBox=document.getElementById("hangman") as HTMLElement
    var hangmanBoxHeight=hangmanBox!!.scrollHeight.toDouble()
    val tipCalcBox=document.getElementById("tipCalc") as HTMLElement
    var tipCalcBoxHeight=hangmanBox!!.scrollHeight.toDouble()
    val botGameBox=document.getElementById("botGame") as HTMLElement
    var botGameBoxHeight=botGameBox!!.scrollHeight.toDouble()
    hangmanBox.style.marginTop="${(topHeight-hangmanBoxHeight)/2}px"
    tipCalcBox.style.marginTop="${(midHeight-tipCalcBoxHeight)/2}px"
    botGameBox.style.marginTop="${(botHeight-botGameBoxHeight)/2}px"
    window.addEventListener("resize", {
        topHeight=top!!.scrollHeight.toDouble()
        midHeight=mid!!.scrollHeight.toDouble()
        botHeight=bot!!.scrollHeight.toDouble()
        hangmanBox.style.marginTop="${(topHeight-hangmanBoxHeight)/2}px"
        tipCalcBox.style.marginTop="${(midHeight-tipCalcBoxHeight)/2}px"
        botGameBox.style.marginTop="${(botHeight-botGameBoxHeight)/2}px"
    })

    val topBtn=document.getElementById("goToTop")
    topBtn!!.addEventListener("click", {
        window.scrollTo(ScrollToOptions(
            0.0,
            0.0,
            ScrollBehavior.Companion.SMOOTH
        ))
    })
    val midBtn=document.getElementById("goToMid")
    midBtn!!.addEventListener("click", {
        window.scrollTo(ScrollToOptions(
            0.0,
            topHeight,
            ScrollBehavior.Companion.SMOOTH
        ))
    })
    val botBtn=document.getElementById("goToBot")
    botBtn!!.addEventListener("click", {
        window.scrollTo(ScrollToOptions(
            0.0,
            topHeight+midHeight,
            ScrollBehavior.Companion.SMOOTH
        ))
    })
    //show or hide the nav bars based on scroll position
    val topNavBar = document.getElementById("topNavBar") as HTMLElement
    val botNavBar = document.getElementById("botNavBar") as HTMLElement
    window.addEventListener("scroll", {
        if(window.pageYOffset>=topHeight) topNavBar.style.display = "none"
        else topNavBar.style.display = "block"
        if(window.pageYOffset<=topHeight) botNavBar.style.display = "none"
        else botNavBar.style.display = "block"
    })
    //Hang Man
    val startHangmanBtn=document.getElementById("startHangman") as HTMLElement
    val blanks=document.getElementById("blanks") as HTMLElement
    val enterLetter=document.getElementById("enterLetter") as HTMLInputElement
    val guessBtn=document.getElementById("guessBtn") as HTMLElement
    val guessIndicator=document.getElementById("guessIndicator") as HTMLElement
    val guessedLetters=document.getElementById("guessedLetters") as HTMLElement
    val chooseDifficulty=document.getElementById("chooseDifficulty") as HTMLSelectElement
    val enterWord=document.getElementById("enterWord") as HTMLInputElement
    val restartBtn=document.getElementById("restart") as HTMLElement

    var word=""
    var guessedLets=ArrayList<Char>()
    var incorrectGuesses:Int=0
    var indicatorMessage=""
    var currMainLineString=""
    var currGuessedLetsString=""
    var difficulty:Int=4
    var gameOver=true

    startHangmanBtn!!.addEventListener("click", {
        difficulty=chooseDifficulty.value.toInt()
        startHangmanBtn.style.display="none"
        chooseDifficulty.style.display="none"
        blanks.style.display="block"
        enterLetter.style.display="block"
        enterWord.style.display="block"
        guessBtn.style.display="block"
        guessIndicator.style.display="block"
        guessedLetters.style.display="block"
        restartBtn.style.display="block"
        gameOver=false

        word = getRandWord(difficulty)
        for(k in word.indices) {
            currMainLineString+="_"
        }
        blanks!!.innerHTML="$currMainLineString"
        guessedLetters.innerHTML=""
        guessIndicator.innerHTML=""
    })
    guessBtn!!.addEventListener("click", {
        var letter:Char=enterLetter.value.get(0).toLowerCase()
        enterLetter.value=""
        if(!guessedLets.contains(letter)){
            guessedLets.add(letter)
            indicatorMessage="You guessed ${letter.toString()}"
        } else {
            indicatorMessage="You already guessed ${letter.toString()}"
            incorrectGuesses-=1
        }
        var guessedWord=enterWord.value.toLowerCase()
        enterWord.value=""
        if(!guessedWord.equals("")){
            if(guessedWord.equals(word)) {
                indicatorMessage="You Win"
                gameOver=true
            }
            else{
                indicatorMessage="The word is not $guessedWord"
                incorrectGuesses+=1
            }
        }
        guessIndicator!!.innerHTML="$indicatorMessage"
        //displays the guessed letters by iterating through the ArrayList
        currGuessedLetsString=""
        for(ch in guessedLets){
            currGuessedLetsString+="${ch.toString()}, "
        }
        guessedLetters!!.innerHTML="Guessed Letters: $currGuessedLetsString"

        //Add the letter to the correct pos in main line if it is in the word and check if its a correct guess
        var correctGuess=false
        for(i in word.indices){
            if(word.get(i) == letter){
                currMainLineString=currMainLineString.replaceRange(i..i,letter.toString())
                correctGuess=true
            }
        }
        //Adds to the incorrect guess counter if the guess was wrong
        if(!correctGuess){
            incorrectGuesses+=1
        }
        //builds the String to be displayed on the main line
        blanks!!.innerHTML="$currMainLineString"
        if(currMainLineString.equals(word)){
            guessIndicator!!.innerHTML="You Win"
            gameOver=true
        }
        if(incorrectGuesses==5) {
            guessIndicator!!.innerHTML="You Lose - The word was $word"
            gameOver=true
        }
        if(gameOver){
            blanks.style.display="none"
            enterLetter.style.display="none"
            enterWord.style.display="none"
            guessBtn.style.display="none"
            guessedLetters.style.display="none"
            word=""
            guessedLets.clear()
            incorrectGuesses=0
            indicatorMessage=""
            currMainLineString=""
            currGuessedLetsString=""
            difficulty=4
        }
    })
    restartBtn.addEventListener("click", {
        blanks.style.display="none"
        enterLetter.style.display="none"
        enterWord.style.display="none"
        guessBtn.style.display="none"
        guessedLetters.style.display="none"
        restartBtn.style.display="none"
        guessIndicator.style.display="none"
        startHangmanBtn.style.display="block"
        chooseDifficulty.style.display="block"
        word=""
        guessedLets.clear()
        incorrectGuesses=0
        indicatorMessage=""
        currMainLineString=""
        currGuessedLetsString=""
        difficulty=4
    })

    //Tip Calculator
    val billAmt=document.getElementById("billPrice") as HTMLInputElement
    val tipPerc=document.getElementById("tipPercent") as HTMLInputElement
    val numOfPpl=document.getElementById("numOfPeople") as HTMLInputElement
    val submitBtn=document.getElementById("submit")
    val amtPerPerson=document.getElementById("amtPerPerson")
    submitBtn!!.addEventListener("click", {
        var totalBeforeTip:Double=billAmt.value.toDouble()
        println(totalBeforeTip)
        var tipProp:Double=tipPerc.value.toDouble()/100
        println(tipProp)
        var numPpl:Double=numOfPpl.value.toDouble()
        println(numPpl)
        amtPerPerson!!.innerHTML="\$${round((totalBeforeTip*(1+tipProp)/numPpl)*100)/100} per Person"
    })
    //Ball Game
//    var margTop=225
//    var margLeft=225
//    val ball=document.getElementById("ball") as HTMLElement
//    ball.style.marginLeft = "${margLeft}px"
//    ball.style.marginTop = "${margTop}px"
//    document.onkeydown=fun(event){
//        when(event.keyCode){
//            37->{ //left
//                if(margLeft>=10) margLeft-=10
//                event.preventDefault()
//            }
//            39->{ //right
//                if(margLeft<=440) margLeft+=10
//                event.preventDefault()
//            }
//            38->{ //up
//                if(margTop>=10) margTop-=10
//                event.preventDefault()
//            }
//            40->{ //down
//                if(margTop<=440) margTop+=10
//                event.preventDefault()
//            }
//        }
//        ball.style.marginLeft = "${margLeft}px"
//        ball.style.marginTop = "${margTop}px"
//    }
}
fun getRandWord(l:Int):String{
    val fourLetters=arrayOf("book","bank","game","dark","dose","fear","foot","mine","race","task")
    val fiveLetters=arrayOf("smoke","smack","boron","adult","azure","cream","quick","blaze","comma","audit")
    val sixLetters=arrayOf("cobalt","squint","baboon","insane","driver","eating","quartz","autumn","caress","iphone")
    val sevenLetters=arrayOf("diamond","academy","chronic","deliver","deficit","highway","library","machine","natural","pioneer")
    when(l){
        4->return fourLetters.get(Random.nextInt(0,fourLetters.size-1))
        5->return fiveLetters.get(Random.nextInt(0,fiveLetters.size-1))
        6->return sixLetters.get(Random.nextInt(0,sixLetters.size-1))
        7-> return sevenLetters.get(Random.nextInt(0,sevenLetters.size-1))
    }
    return ""
}

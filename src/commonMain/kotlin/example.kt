import arrow.core.computations.EitherEffect
import arrow.core.computations.either
import arrow.core.right
import arrow.typeclasses.Semigroup

typealias EitherEff<E> = EitherEffect<E, Any?>

// TODO try to compile with multiple receivers branch
// context(WriterEffect<String>, WriterEffect<Int>, EitherEffect<Error>)
suspend fun <Ctx> Ctx.program(): Int
// Emulate multiple receivers
  where Ctx : WriterEffect<String>,
        Ctx : EitherEff<Error>,
        Ctx : KleisliEffect<Int> {
  tell("Hello")
  val res = ", World!".right().bind()
  tell(res)
  return ask()
}

suspend fun compositionExample() {
  val (writer, res) = writer(Semigroup.string()) {
    kleisli(500) {
      either<Error, Int> {
        // This will not be needed with the Multiple Receivers, you can simply call `program()`.
        IntersectionTypeHack(this as EitherEff<Error>, this@writer, this@kleisli)
          .program()
      }
    }
  }

  println("Program finished")
  println("Output writer: $writer")
  println("Output program: $res")
}

// Emulate what the compiler will automatically do with multiple receivers
class IntersectionTypeHack<A, B>(
  val either: EitherEff<Error>,
  val writer: WriterEffect<A>,
  val kleisli: KleisliEffect<B>
) : EitherEff<Error> by either,
  WriterEffect<A> by writer,
  KleisliEffect<B> by kleisli

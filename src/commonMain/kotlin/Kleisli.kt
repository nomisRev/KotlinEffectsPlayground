/**
 * This is not a very powerful effect.
 * You use it to wrap dependencies, or things that you don't own.
 *
 * In case you own the code, you should consider designing your own effects.
 * For example:
 *
 * ```kotlin
 * interface DataSaver<A> {
 *   suspend fun A.save(): Unit
 *   suspend fun List<A>.save(): Unit = map { it.save() }
 * }
 *
 * data class User(val id: UUID)
 * class UserSaver(..): DataSaver<User> {
 *   override suspend fun User.save(): Unit = ...
 * }
 *
 * context(DataSaver<User>)
 * suspend fun program(): Unit {
 *    (0..10)
 *      .map { User(UUID.randomUUID()) }
 *      .save()
 * }
 * ```
 */
interface KleisliEffect<A> {
  /** Ask value from Kleisli */
  suspend fun ask(): A
}

public suspend fun <A, B> kleisli(
  value: A,
  f: suspend KleisliEffect<A>.() -> B
): B = f(KleisliEf(value))

private class KleisliEf<A>(
  private val value: A
) : KleisliEffect<A> {
  override suspend fun ask(): A = value
}

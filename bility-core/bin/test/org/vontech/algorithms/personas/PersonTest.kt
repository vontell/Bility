package org.vontech.algorithms.personas

import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec
import org.vontech.algorithms.personas.utils.PersonaContext
import org.vontech.core.interfaces.*
import java.util.*

/**
 * Tests the basic operations of the core Person type, which is
 * an inactive user.
 */
class PersonTest: FeatureSpec({

    // Let's define the interface as a whole
    val screen = OutputChannel(ChannelType.VISUAL, "Device Screen")
    val touch = InputChannel(ChannelType.PHYSICAL, "Device Touch Display")
    val metadata = LiteralInterfaceMetadata(UUID.randomUUID().toString())

    // Then load example screens
    val firstScreenButtonPerceptBuilder = PerceptBuilder()
    firstScreenButtonPerceptBuilder.createTextPercept("Click me!")
    val firstScreenButton = Perceptifer(firstScreenButtonPerceptBuilder.buildPercepts(), null)
    val screenOne = LiteralInterace(
            setOf(firstScreenButton),
            setOf(screen),
            setOf(touch),
            metadata
    )

    val secondScreenButtonPerceptBuilder = PerceptBuilder()
    secondScreenButtonPerceptBuilder.createTextPercept("You found me!")
    val secondScreenText = Perceptifer(secondScreenButtonPerceptBuilder.buildPercepts(), null)
    val screenTwo = LiteralInterace(
            setOf(secondScreenText),
            setOf(screen),
            setOf(touch),
            metadata
    )


    feature("creating the Person user") {

        val person = Person("John Doe")

        scenario("should have default information") {

            person.nickname shouldBe "John Doe"
            person.baseType shouldBe "Person"

        }

        scenario("should be given a context") {

            // Create and provide the context
            val goalState = Percept(PerceptType.TEXT, "You found me!")
            val context = PersonaContext(goalState)
            person.context = context

            // Make sure that the goal state is correct
            person.context.desiredState.shouldBeTypeOf<Percept>()
            (person.context.desiredState as Percept).information shouldBe "You found me!"

        }

    }

})
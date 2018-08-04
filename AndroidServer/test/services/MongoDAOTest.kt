package services

import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec
import org.vontech.androidserver.services.MongoDAO

class MongoDAOTest: FeatureSpec({

    feature("the database object") {
        scenario("should be valid") {

            MongoDAO.database.name shouldBe "bility"
            MongoDAO.database.name shouldBe "bility"

        }
    }

})
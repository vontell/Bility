package org.vontech.bility.server.services

import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec

class MongoDAOTest: FeatureSpec({

    feature("the database object") {
        scenario("should be valid") {

            MongoDAO.database.name shouldBe "bility"
            MongoDAO.database.name shouldBe "bility"

        }
    }

})
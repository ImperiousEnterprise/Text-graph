package Models.DAO


import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * Created by adeyemi on 1/24/17.
  */
trait DAOSlick extends DBTableDefinitions with HasDatabaseConfigProvider[JdbcProfile]

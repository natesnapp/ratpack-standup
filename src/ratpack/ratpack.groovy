import groovy.json.JsonSlurper
import org.h2.jdbcx.JdbcDataSource
import standup.DatabaseMigrationService
import standup.DefaultStatusService
import standup.StatusBroadcaster
import standup.StatusService
import standup.handler.CreateStatusHandler
import standup.handler.GetAllStatusHandler

import javax.sql.DataSource

import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        bindInstance(DataSource, new JdbcDataSource(
                URL: "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                user: "sa",
                password: ""
        ))
        bindInstance(new DatabaseMigrationService())
        bindInstance(JsonSlurper, new JsonSlurper())
        bind(StatusService, DefaultStatusService)
        bindInstance(new StatusBroadcaster())
        bind(CreateStatusHandler)
        bind(GetAllStatusHandler)
    }
    handlers {
        prefix("api") {
            prefix("status") {
                post(CreateStatusHandler)
                get("all", GetAllStatusHandler)
            }
        }
        get("ws/status") { StatusBroadcaster broadcaster ->
            broadcaster.register context
        }
        files {
            dir("static").indexFiles("index.html")
        }
    }
}
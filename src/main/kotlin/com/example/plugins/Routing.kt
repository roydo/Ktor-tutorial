package com.example.plugins

import com.example.dao.DAOFacade
import com.example.dao.DAOFacadeCacheImpl
import com.example.dao.DAOFacadeImpl
//import com.example.dao.dao
import com.example.models.Article
import com.example.models.articles
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.runBlocking
import java.io.File


fun Application.configureRouting() {
    val dao: DAOFacade = DAOFacadeCacheImpl(
        DAOFacadeImpl(),
        File(environment.config.property("storage.ehcacheFilePath").getString())
    ).apply {
        runBlocking {
            if(allArticles().isEmpty()) {
                addNewArticle("The drive to develop!", "...it's what keeps me going.")
            }
        }
    }
    routing {
        /*
        get("/") {
            call.respondRedirect("articles")
        }
        */

        route("articles") {
            get {
                // show a list of articles
                call.respond(FreeMarkerContent(
                    "index.ftl",
                    mapOf("articles" to dao.allArticles())
                )
                )
            }
            get("new") {
                // show a page with fields for creatomg a new article
                call.respond(FreeMarkerContent(
                    "new.ftl",
                    model = null
                ))
            }
            post {
                // save an article
                val formParameters = call.receiveParameters()
                val title = formParameters.getOrFail("title")
                val body = formParameters.getOrFail("body")
                //val newEntry = Article.newEntry(title,body)
                val article = dao.addNewArticle(title,body)
                //articles.add(newEntry)
                //call.respondRedirect("/articles/${newEntry.id}")
                call.respondRedirect("/articles/${article?.id}")
            }
            get("{id}") {
                // show an article with a specific id
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent(
                    "show.ftl",
                    mapOf("article" to dao.article(id))
                ))
            }
            get("{id}/edit") {
                // show a page with fields for edting an article
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent(
                    "edit.ftl",
                    mapOf("article" to dao.article(id))
                ))
            }
            post("{id}") {
                // update or delete an article
                val id = call.parameters.getOrFail<Int>("id").toInt()
                val formParameters = call.receiveParameters()
                when (formParameters.getOrFail<String>("_action")) {
                    "update" -> {
                        //val index = articles.indexOf(articles.find { it.id == id })
                        val title = formParameters.getOrFail("title")
                        val body = formParameters.getOrFail("body")
                        dao.editArticle(id,title,body)
                        //articles[index].title = title//
                        //articles[index].body = body
                        call.respondRedirect("/articles/$id")
                    }
                    "delete" -> {
                        //articles.removeIf { it.id == id }
                        dao.deleteArticle(id)
                        call.respondRedirect("/articles")
                    }
                }
            }
        }

        /*
        staticResources(
            "/static",
            "files"
        )
        */
    }
}

/*fun Application.configureRouting() {
    routing {
        staticResources("/static", "files")
    }
}
*/
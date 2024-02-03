package com.example.plugins

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


fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondRedirect("articles")
        }
        route("articles") {
            get {
                // show a list of articles
                call.respond(FreeMarkerContent(
                    "index.ftl",
                    mapOf("articles" to articles)
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
                val newEntry = Article.newEntry(title,body)
                articles.add(newEntry)
                call.respondRedirect("/articles/${newEntry.id}")
            }
            get("{id}") {
                // show an article with a specific id
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent(
                    "show.ftl",
                    mapOf("article" to articles.find { it.id == id })
                ))
            }
            get("{id}/edit") {
                // show a page with fields for edting an article
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent(
                    "edit.ftl",
                    mapOf("article" to articles.find { it.id == id })
                ))
            }
            post("{id}") {
                // update or delete an article
                val id = call.parameters.getOrFail<Int>("id").toInt()
                val formParameters = call.receiveParameters()
                when (formParameters.getOrFail<String>("_action")) {
                    "update" -> {
                        val index = articles.indexOf(articles.find { it.id == id })
                        val title = formParameters.getOrFail("title")
                        val body = formParameters.getOrFail("body")
                        articles[index].title = title
                        articles[index].body = body
                        call.respondRedirect("/articles/$id")
                    }
                    "delete" -> {
                        articles.removeIf { it.id == id }
                        call.respondRedirect("/articles")
                    }
                }
            }
        }
        
        staticResources(
            "/static",
            "files"
        )
    }
}

/*fun Application.configureRouting() {
    routing {
        staticResources("/static", "files")
    }
}
*/
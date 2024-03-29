package com.example.plugins

import com.example.dao.DAOFacade
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.articleRoute(dao: DAOFacade) {
    route("articles") {
        get {
            // show a list of articles
            call.respond(
                FreeMarkerContent(
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
}

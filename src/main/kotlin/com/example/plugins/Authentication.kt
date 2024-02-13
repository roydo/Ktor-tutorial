package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.dao.DAOFacade
import com.example.dao.DAOFacadeCacheImpl
import com.example.dao.DAOFacadeImpl
import io.ktor.http.*
import io.ktor.server.sessions.*
//import com.example.dao.dao
import io.ktor.server.util.*
import kotlinx.coroutines.runBlocking
import java.io.File

data class UserSession(
    val name: String
) : Principal

data class Status(
    val isfailed: Boolean
)

fun Application.setUpAuthenticaion() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60
        }
    }
    install(Authentication) {
        form("authName") {
            userParamName = "username"
            passwordParamName = "password"
            
            challenge {
                //call.respond(HttpStatusCode.Unauthorized)
                //call.respondRedirect("/login")
            }
            validate { credentials ->
                if(credentials.name == "hiro" && credentials.password == "test") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }

            session<UserSession>("authSession") {
                validate { session ->
                    if(session.name.startsWith("hi")) {
                        session
                    } else {
                        null
                    }
                }
                challenge {
                    println("NG!!!")
                    call.respondRedirect("/login")
                }
            }
        }
    }
    
    routing {
        
        get("/") {
            //call.respondRedirect("articles")
            call.respondRedirect("login")
        }
        //route("login") {
        get("login") {
            call.respond(
                FreeMarkerContent(
                    "login.ftl",
                    model = mapOf("status" to Status(false))
                )
            )
        }
        
        get("/logout") {
            call.sessions.clear<UserSession>()
            call.respondRedirect("/login")
        }
            
        //}
        authenticate("authName") {
            post("login") {
                println("sucsess!!!! ${call.principal<UserIdPrincipal>()?.name ?: "null"}")
                //val postParameters = call.receiveParameters()
                //val username = postParameters["username"] ?: return@post call.respond(HttpStatusCode.Unauthorized)
                //val password = call.principal<UserIdPrincipal>()?.name ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                call.sessions.set(UserSession(name = userName))



                if (userName == "hiro") {
                    call.respondRedirect("/articles")
                } else {
                    println("hihihihihi")
                    call.respond(HttpStatusCode.Unauthorized, FreeMarkerContent(
                        "login.ftl",
                        model = mapOf("status" to Status(true))
                    ))
                }
            }
        }
        
        authenticate("authSession") {
            val dao: DAOFacade = DAOFacadeCacheImpl(
                DAOFacadeImpl(),
                File(environment?.config?.property("storage.ehcacheFilePath")?.getString() ?: "test")
            ).apply {
                runBlocking {
                    if(allArticles().isEmpty()) {
                        addNewArticle("The drive to develop!", "...it's what keeps me going.")
                    }
                }
            }
            articleRoute(dao = dao)
        }
        
        staticResources(
            "/static",
            "files"
        )
            
        }
    
    }
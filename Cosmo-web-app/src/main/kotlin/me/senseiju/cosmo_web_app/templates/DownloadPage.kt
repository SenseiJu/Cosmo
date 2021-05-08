package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse
import me.senseiju.cosmo_web_app.templates.common_components.HTMLHeadComponent
import me.senseiju.cosmo_web_app.templates.common_components.HeaderComponent

class DownloadPage(private val user: DiscordUserResponse): Template<HTML> {
    override fun HTML.apply() {
        insert(HTMLHeadComponent(), TemplatePlaceholder())

        body {
            insert(HeaderComponent(user), TemplatePlaceholder())

            main {
                id = "plugin-page"

                howToInstall()
                havingTrouble()
            }
        }
    }
}

private fun FlowContent.howToInstall() {
    h1 {
        + "How to install"
    }

    ol {
        li {
            + "Download the "
            a(href = "https://www.spigotmc.org/resources/cosmo.92103/") { + "Cosmo" }
            + " plugin as well as "
            a(href = "https://www.spigotmc.org/resources/protocollib.1997/") { + "ProtocolLib" }
        }
        li {
            + "Place the plugins in your servers "
            code { + "/plugins" }
            + " folder"
        }
        li {
            + "Start your server and wait for the configuration files to be generated. Once generated, edit the "
            code { + "config.yml" }
            + " found in the "
            code { + "/plugins/cosmo" }
            + " folder and replace the "
            code { + "pack-id" }
            + " with your pack-id from the "
            a(href = "/packs") { + "Your packs" }
            + " page on the website. You may also want to change the "
            code { + "internal-http-port" }
            + "if it is already used by another application"
        }
        li {
            + "Port forward the "
            code { + "internal-http-port" }
            + " that is set inside the "
        }
        li {
            + "Restart your server and connecting should now ask the player to accept the server resource pack"
        }
    }
}

private fun FlowContent.havingTrouble() {
    h1 {
        + "Having trouble?"
    }

    ol {
        li {
            + "Check your server console for any errors (This will normally occur on start up if something is wrong)"
        }
        li {
            + "Try doing a fresh install of the plugin. Delete the "
            code { + "/plugins/cosmo" }
            + " folder and the "
            code { + "Cosmo.jar" }
            + " and download the latest plugin above"
        }
        li {
            + "If all else fails, you can join the "
            a(href = "https://discord.com/invite/Et8x2YBsag") { + "Discord" }
            + " for further support"
        }
    }
}
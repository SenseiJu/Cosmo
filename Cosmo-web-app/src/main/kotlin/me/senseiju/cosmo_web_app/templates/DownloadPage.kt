package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.data_storage.wrappers.PackWrapper
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse
import me.senseiju.cosmo_web_app.templates.common_components.HTMLHeadComponent
import me.senseiju.cosmo_web_app.templates.common_components.HeaderComponent

class PluginPage(private val user: DiscordUserResponse): Template<HTML> {
    override fun HTML.apply() {
        insert(HTMLHeadComponent(), TemplatePlaceholder())

        body {
            insert(HeaderComponent(user), TemplatePlaceholder())

            main {
                id = "plugin-page"

                howToUse()
                havingTroubleInstalling()
            }
        }
    }
}

private fun FlowContent.howToUse() {
    h1 {
        + "How to install"
    }

    ol {
        li {
            + "Download the plugin "
            code {
                + ".jar"
            }
            + " file above as well as "
            a(href = "https://www.spigotmc.org/resources/protocollib.1997/") {
                + "ProtocolLib"
            }
        }
        li {
            + "Place the plugins in your servers "
            code {
                + "/plugins"
            }
            + " folder"
        }
        li {
            + "Start your server and wait for the configuration files to be generated. Once generated, edit the "
            code {
                + "config.yml"
            }
            + " found in the "
            code {
                + "/plugins/cosmo"
            }
            + " folder and replace the "
            code {
                + "pack-id"
            }
            + " with your pack-id from the "
            a(href = "/packs") {
                + "Your Packs"
            }
            + " section on the website "
        }
        li {
            + "Restart your server and connecting should now ask the player to install the server resource pack"
        }
    }
}

private fun FlowContent.havingTroubleInstalling() {
    h1 {
        + "Having trouble installing?"
    }

    ol {
        li {
            + "Check your server console for any errors (This will normally occur on start up if something is wrong)"
        }
        li {
            + "Try doing a fresh install of the plugin. Delete the "
            code {
                + "/plugins/cosmo"
            }
            + " folder and the "
            code {
                + ".jar"
            }
            + " and download the latest plugin above"
        }
        li {
            + "If all else fails, you can join the Discord for further support"
        }
    }
}
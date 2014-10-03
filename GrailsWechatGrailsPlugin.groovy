import org.grails.plugin.wechat.WechatResponseService
import org.grails.plugin.wechat.util.HandlersRegistry

class GrailsWechatGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp",
        "grails-app/domain/org/grails/plugin/wechat/test/**/*.groovy",
        "grails-app/controllers/org/grails/plugin/wechat/test/**/*.groovy",
        "grails-app/services/org/grails/plugin/wechat/test/**/*.groovy",
        "grails-app/src/groovy/org/grails/plugin/wechat/test/**/*.groovy",
        "grails-app/src/java/org/grails/plugin/wechat/test/**/*.java",
    ]

    def title = "Grails Wechat Plugin" // Headline display name of the plugin
    def author = "Haihua Xiao"
    def authorEmail = "hhxiao@gmail.com"
    def description = '''\
Grails plugin provides wechat integration features.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/grails-wechat"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
    def developers = [ [ name: "Haihua Xiao", email: "hhxiao@gmail.com" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/hhxiao/grails-wechat.git/" ]

    def observe = ['*'] // We observe everything so we can re-apply dynamic methods, conventions etc

    def doWithWebDescriptor = { xml ->
    }

    def doWithSpring = {
        wechatHandlersRegistry(HandlersRegistry) { bean ->
            grailsApplication = ref('grailsApplication')
        }
    }

    def doWithDynamicMethods = { ctx ->
        ctx.wechatHandlersRegistry.reloadHandlers()
    }

    def doWithApplicationContext = { applicationContext ->
    }

    def onChange = { event ->
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
        def ctx = event.application.mainContext

        if (event.source instanceof Class && application.isServiceClass(event.source)) {
            ctx.wechatHandlersRegistry.reloadHandler(event.source)
        }
    }

    def onConfigChange = { event ->
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
    }
}

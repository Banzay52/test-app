@()(implicit req: Http.Request)

@import views.html.tags._asset


@*
// This file specifies global javascript properties for libraries used in the cms-application backend (admin-area of cms) as well as frontend (web-application specfic)
// CMS-Default config is included by default (by rendering ch.insign.cms.views.js.js._config) and can be overwritten below the import
*@
@ch.insign.cms.views.js.js._config()


@*
// Overwrite CMS-Default parameters or add new ones according to your needs
*@

console.log("overwrite tiny-config");
jsconfig.tinyMce.externalPlugins.plantuml = "@{_asset("javascripts/plantuml/editor_plugin.js")}";
jsconfig.tinyMce.defaultLayout.plugins.concat("plantuml");
jsconfig.tinyMce.defaultLayout.toolbar = jsconfig.tinyMce.defaultLayout.toolbar + " | plantuml";
// jsconfig.tinyMce.simpleLayout.plugins.push("plantuml");
// jsconfig.tinyMce.simpleLayout.toolbar = jsconfig.tinyMce.simpleLayout.toolbar + " | plantuml";
console.log("finish tiny-config");

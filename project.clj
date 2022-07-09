(defproject testapp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.11.57"]
                 [compojure "1.6.3"]
                 [clj-unifier "0.0.15"]
                 [selmer "1.12.52"]

                 [ring/ring-defaults "0.3.3"]
                 [ring/ring-jetty-adapter "1.9.5"]
                 [ring "1.9.5"]
                 [metosin/ring-http-response "0.9.3"]
                 [ring-cors "0.1.13"]
                 [metosin/muuntaja "0.6.8"]
                 [ring/ring-json "0.5.1" :exclusions [cheshire]]
                 [cheshire "5.11.0"]

                 [com.datomic/datomic-free "0.9.5697" :exclusions [org.slf4j/log4j-over-slf4j org.slf4j/slf4j-nop com.google.guava/guava]]
                 [mount "0.1.16"]
                 [com.datomic/datomic "0.8.3335"]
                 [io.rkn/conformity "0.5.4"]
                 [cprop "0.1.19"]

                 [re-frame "1.2.0"]
                 [day8.re-frame/tracing "0.6.2"]
                 [day8.re-frame/http-fx "0.2.4"]
                 [binaryage/devtools "1.0.6"]
                 [day8.re-frame/re-frame-10x "1.2.7"]

                 [cambium/cambium.core "1.1.1"]
                 [cambium/cambium.codec-simple "1.0.0"]
                 [cambium/cambium.logback.core "0.4.5"]]

  :main testapp.server.core

  :plugins [[lein-ring "0.12.6"]
            [lein-cljsbuild "1.1.8"]]

  :hooks [leiningen.cljsbuild]

  :resource-paths ["resources" "target/resources"]

  :target-path "target/%s"

  :cljsbuild      {:builds {:min {:source-paths ["src/testapp/server" "src/testapp/ui"]
                                  :compiler     {:output-dir       "resources/public/js/compiled/js"
                                                 :output-to        "resources/public/js/compiled/js/app.js"
                                                 :source-map       "resources/public/js/compiled/js/app.js.map"
                                                 :optimizations    :simple
                                                 :pretty-print     false
                                                 :closure-warnings {:externs-validation :off
                                                                    :non-standard-jsdoc :off}}}}}

  :profiles {:dev     {:dependencies [[javax.servlet/servlet-api "2.5"]
                                      [ring/ring-mock "0.4.0"]
                                      [hashp "0.2.1"]]
                       :injections   [(require 'hashp.core)]}

             :uberjar {:aot            [testapp.server.core]
                       :jvm-opts       ["-Dclojure.compiler.direct-linking=true"]
                       :uberjar-name   "testapp.jar"
                       :resource-paths ["env/config"]}}

  )

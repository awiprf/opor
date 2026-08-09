/**
 * Precompiled [java-service-convention.gradle.kts][Java_service_convention_gradle] script plugin.
 *
 * @see Java_service_convention_gradle
 */
public
class JavaServiceConventionPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Java_service_convention_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}

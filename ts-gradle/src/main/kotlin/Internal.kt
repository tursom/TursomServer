import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtraPropertiesExtension

internal val Project.ext: ExtraPropertiesExtension
  get() = (this as ExtensionAware).extensions
    .getByName("ext") as ExtraPropertiesExtension
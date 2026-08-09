plugins {
    id("java-service-convention")
}

dependencies {
    implementation(project(":lib:cfg:dbu:db-aut"))
    implementation(project(":lib:cfg:rds:rd-aut"))

    implementation("com.bucket4j:bucket4j-core:8.10.1")
}

rootProject.name = "opor-jsb-grd"

include(
    // Shared libraries — Account domain
    "lib:cfg:dbu:db-aut",
    "lib:cfg:rds:rd-aut",

    // Services — Auth Domain > Signup > Username
    "svc:aut:sgu:usn:vec"
)

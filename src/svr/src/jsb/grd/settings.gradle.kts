rootProject.name = "opor-jsb-grd"

include(
    "lib:cfg:dbu",
    // Shared libraries — Account domain
    "lib:cfg:dbu:db-aut",
    "lib:cfg:rds:rd-aut",

    // Services — Auth Domain > Signup > Username
    "svc:aut:sgu:usn:vec"
)

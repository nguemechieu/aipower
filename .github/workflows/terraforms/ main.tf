resource "google_compute_instance" "backend_server" {
  name         = "aipower-backend"
  machine_type = var.instance_type
  zone         = var.zone

  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-11"
    }
  }

  network_interface {
    network = "default"

    access_config {
      # Ephemeral IP
    }
  }

  metadata_startup_script = <<-EOT
    #!/bin/bash
    apt-get update
    apt-get install -y openjdk-11-jdk
    # Commands to start AiPower application
  EOT

  tags = ["aipower-backend"]
}

resource "google_sql_database_instance" "mysql_instance" {
  name             = "aipower-db-instance"
  database_version = "MYSQL_8_0"
  region           = var.region

  settings {
    tier = "db-f1-micro"
  }
}

resource "google_sql_database" "mysql_db" {
  name     = var.db_name
  instance = google_sql_database_instance.mysql_instance.name
}

resource "google_sql_user" "mysql_user" {
  name     = var.db_username
  instance = google_sql_database_instance.mysql_instance.name
  password = var.db_password
}

resource "google_storage_bucket" "app_bucket" {
  name     = var.bucket_name
  location = var.region

  lifecycle_rule {
    action {
      type = "Delete"
    }
    condition {
      age = 30
    }
  }

  versioning {
    enabled = true
  }
}

output "backend_instance_ip" {
  value = google_compute_instance.backend_server.network_interface[0].access_config[0].nat_ip
}

output "db_instance_connection_name" {
  value = google_sql_database_instance.mysql_instance.connection_name
}

output "bucket_name" {
  value = google_storage_bucket.app_bucket.name
}

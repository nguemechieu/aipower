variable "project_id" {
  description = "Google Cloud project ID"
}

variable "region" {
  default     = "us-central1"
  description = "Region for resources"
}

variable "zone" {
  default     = "us-central1-a"
  description = "Zone for Compute Engine"
}

variable "instance_type" {
  default     = "e2-micro"
  description = "Instance type for Compute Engine"
}

variable "db_name" {
  default     = "aipower_db"
  description = "Cloud SQL database name"
}

variable "db_username" {
  default     = "admin"
  description = "Database username"
}

variable "db_password" {
  default     = "password"
  description = "Database password"
}

variable "bucket_name" {
  default     = "aipower-bucket"
  description = "Cloud Storage bucket name"
}
